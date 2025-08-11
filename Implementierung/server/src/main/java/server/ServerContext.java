package server;

import account.Account;
import chat.ChatNachricht;
import dto.GameStateDTO;
import dto.PlayerDTO;
import spiel.Spiel;
import spiel.Spieler;
import api.Raumverwaltung;

import java.rmi.registry.Registry;
import java.util.*;

/**
 * Central server context managing game state, user sessions, and room data.
 */
public class ServerContext {
    private static ServerContext instance;
    public List<Account> accounts;
    public List<Account> logged_in = new ArrayList<>();
    public String lobby_chat = "";
    public Map<String, Spiel> spiele = new HashMap<>();
    public final Map<String, List<String>> chatVerlauf = new HashMap<>();
    public final Map<String, List<Spieler>> spielraeume = new HashMap<>();
    public final Map<String, String> passwoerter = new HashMap<>(); // for private rooms
    public final Map<String, String> hosts = new HashMap<>();
    public final List<ChatNachricht> chatHistory = new ArrayList<>();
    public final Map<String, List<ChatNachricht>> roomMessages = new HashMap<>();
    public final Map<String, Long> lastHeartbeats = new HashMap<>();

    public Registry registry;
    private Raumverwaltung raumService;

    public synchronized void setRegistry(Registry registry) {
        this.registry = registry;
    }

    public synchronized Registry getRegistry() {
        return registry;
    }
    public ServerContext() {
        instance = this;
    }

    public void setAccounts(List<Account> accounts) {
        this.accounts = accounts;
    }
    /**
     * Starts a background timer to remove inactive users from rooms.
     * Called once during server construction.
     */
    public void startHeartbeatCleanup() {
        Timer timer = new Timer(true);
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                long now = System.currentTimeMillis();
                List<String> toRemove = new ArrayList<>();

                for (Map.Entry<String, Long> entry : lastHeartbeats.entrySet()) {
                    if (now - entry.getValue() > 4000) {
                        toRemove.add(entry.getKey());
                    }
                }

                for (String user : toRemove) {
                    try {
                        System.out.println("Removing inactive user: " + user);
                        String raumName = raumService.getRaumName(user);
                        if (raumName != null) {
                            raumService.spielraumVerlassen(user, raumName);
                        }
                        lastHeartbeats.remove(user);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }, 0, 2000);
    }
    /** @param raumService the room management service */
    public void setRaumService(Raumverwaltung raumService) {
        this.raumService = raumService;
    }
    /**
     * Starts background cleanup of bot-only rooms.
     * Removes rooms containing only bot players.
     */
    public void startBotRoomCleanup() {
        Timer timer = new Timer(true);
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                List<String> roomsToRemove = new ArrayList<>();

                for (Map.Entry<String, List<Spieler>> entry : spielraeume.entrySet()) {
                    String raumName = entry.getKey();
                    List<Spieler> players = entry.getValue();

                    boolean allBots = players.stream().allMatch(Spieler::isBot);

                    if (allBots) {
                        roomsToRemove.add(raumName);
                    }
                }

                for (String raumName : roomsToRemove) {
                    System.out.println("[CLEANUP] Removing bot-only room: " + raumName);

                    spiele.remove(raumName);
                    spielraeume.remove(raumName);
                    chatVerlauf.remove(raumName);
                    roomMessages.remove(raumName);
                    passwoerter.remove(raumName);
                    hosts.remove(raumName);
                }
            }
        }, 0, 2000);
    }

    /**
     * Finds account by username.
     * @param playerName the username to search for
     * @return the account or null if not found
     */
    public Account getAccount(String playerName){

        for (Account account : accounts) {
            if (account.get_username().equals(playerName)) {
                return account;
            }
        }
        return null;
    }
        public static ServerContext getServerContext() {
            return instance;
        }

}

