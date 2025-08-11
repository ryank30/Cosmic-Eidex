package server.impl;

import account.Account;
import dto.GameroomDTO;
import dto.LeaderboardEntryDTO;
import spiel.Spiel;
import server.ServerContext;
import api.Raumverwaltung;
import spiel.Spieler;
import mapper.DTOConverter;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;
import java.util.stream.Collectors;

import static mapper.DTOConverter.toDTO;

public class RaumverwaltungImpl extends UnicastRemoteObject implements Raumverwaltung {

    private final ServerContext context;

    public RaumverwaltungImpl(ServerContext context) throws RemoteException {
        this.context = context;
    }

    /**
     * Creates a new game room with the specified name and host.
     *
     * @param hostSpielerName the username of the host player
     * @param spielraumName the desired room name
     * @param passwort optional password for the room
     */
    @Override
    public void spielraumErstellen(String hostSpielerName, String spielraumName, String passwort) {
        if (this.context.spielraeume.containsKey(spielraumName))
            throw new RuntimeException("spielraum existiert bereits.");
        List<Spieler> spielerListe = new ArrayList<>();
        spielerListe.add(new Spieler(hostSpielerName,false));
        this.context.spielraeume.put(spielraumName, spielerListe);
        this.context.passwoerter.put(spielraumName, passwort);
        this.context.hosts.put(spielraumName, hostSpielerName);
        this.context.chatVerlauf.put(spielraumName, new ArrayList<>());
    }

    /**
     * Adds a player to an existing game room if the password is correct.
     *
     * @param spielerName the name of the player joining
     * @param spielraumName the room to join
     * @param passwort the room password
     */
    @Override
    public void spielraumBeitreten(String spielerName, String spielraumName, String passwort) {
        if (!this.context.spielraeume.containsKey(spielraumName)) {
            throw new RuntimeException("Spielraum existiert nicht.");
        }

        if (!Objects.equals(this.context.passwoerter.get(spielraumName), passwort)) {
            throw new RuntimeException("Falsches Passwort.");
        }

        List<Spieler> spielerListe = this.context.spielraeume.get(spielraumName);

        if (spielerListe.size() >= 3) {
            throw new RuntimeException("Der Raum ist voll."); // Room is full
        }

        for (Spieler s : spielerListe) {
            if (s.getName().equals(spielerName)) {
                throw new RuntimeException("Spieler ist bereits im Raum.");
            }
        }

        spielerListe.add(new Spieler(spielerName, false));
        System.out.println("[DEBUG] Spieler hinzugefügt: " + spielerName + " -> " + spielraumName);
    }


    /**
     * Finds the name of the room the given user is in.
     *
     * @param username the username to search
     * @return the name of the room or null if not found
     * @throws RemoteException if RMI communication fails
     */
    @Override
    public String getRaumName(String username) throws RemoteException {
        for (Map.Entry<String, List<Spieler>> entry : this.context.spielraeume.entrySet()) {
            for (Spieler s : entry.getValue()) {
                if (s.getName().equals(username)) {
                    return entry.getKey();
                }
            }
        }
        return null;
    }

    /**
     * Removes a player from the specified game room. Deletes the room if empty.
     *
     * @param spielerName the name of the player leaving
     * @param spielraumName the room to leave
     */
    @Override
    public void spielraumVerlassen(String spielerName, String spielraumName) {
        List<Spieler> spielerListe = this.context.spielraeume.get(spielraumName);
        if (spielerListe == null)
            throw new RuntimeException("Raum existiert nicht.");

        spielerListe.removeIf(s -> s.getName().equals(spielerName));


        if (spielerName.equals(this.context.hosts.get(spielraumName))) {
            this.context.hosts.remove(spielraumName);
        }

        if (spielerListe.size() == 1) {
            Spieler remaining = spielerListe.get(0);
            spielerListe.clear();
            spielerListe.add(remaining);
        }

        if (spielerListe.isEmpty()) {
            this.context.spielraeume.remove(spielraumName);
            this.context.passwoerter.remove(spielraumName);
            this.context.chatVerlauf.remove(spielraumName);
            this.context.hosts.remove(spielraumName);
        }
    }



    /**
     * Removes a player from the specified room.
     *
     * @param spielerName the name of the player to remove
     * @param spielraumName the name of the room
     */
    @Override
    public void spielerEntfernen(String spielerName, String spielraumName) {
        List<Spieler> spielerListe = this.context.spielraeume.get(spielraumName);
        if (spielerListe == null)
            throw new RuntimeException("Raum existiert nicht.");
        spielerListe.removeIf(s -> s.getName().equals(spielerName));
    }

    /**
     * Adds an easy bot to the specified game room.
     *
     * @param spielraumName the room to add the bot into
     * @throws RemoteException if RMI communication fails
     */
    @Override
    public void easyBotHinzufuegen(String spielraumName) throws RemoteException {
        List<Spieler> spielerListe = this.context.spielraeume.get(spielraumName);
        if (spielerListe == null) {
            throw new RuntimeException("Raum existiert nicht.");
        }

        if (raumVoll(spielraumName)) {
            throw new RuntimeException("Raum ist bereits voll.");
        }

        int botCount = 1;
        while (true) {
            String botName = "EasyBot" + botCount;
            boolean exists = spielerListe.stream().anyMatch(s -> s.getName().equals(botName));
            if (!exists) {
                spielerListe.add(new Spieler(botName, true));
                break;
            }
            botCount++;
        }
    }

    /**
     * Adds a hard bot to the specified game room.
     *
     * @param spielraumName the room to add the bot into
     * @throws RemoteException if RMI communication fails
     */
    @Override
    public void hardBotHinzufuegen(String spielraumName) throws RemoteException {
        List<Spieler> spielerListe = this.context.spielraeume.get(spielraumName);
        if (spielerListe == null) {
            throw new RuntimeException("Raum existiert nicht.");
        }

        if (raumVoll(spielraumName)) {
            throw new RuntimeException("Raum ist bereits voll.");
        }

        int botCount = 1;
        while (true) {
            String botName = "HardBot" + botCount;
            boolean exists = spielerListe.stream().anyMatch(s -> s.getName().equals(botName));
            if (!exists) {
                spielerListe.add(new Spieler(botName, true)); // true = isBot
                break;
            }
            botCount++;
        }
    }

    /**
     * Removes a bot from the specified game room.
     *
     * @param spielraumName the room name
     * @throws RemoteException if RMI communication fails
     */
    @Override
    public void botEntfernen(String spielraumName) throws RemoteException {
        List<Spieler> spielerListe = this.context.spielraeume.get(spielraumName);
        if (spielerListe == null) {
            throw new RuntimeException("Raum existiert nicht.");
        }

        Optional<Spieler> botToRemove = spielerListe.stream()
                .filter(Spieler::isBot)
                .findFirst();

        if (botToRemove.isPresent()) {
            spielerListe.remove(botToRemove.get());
        } else {
            throw new RuntimeException("Kein Bot im Raum vorhanden.");
        }
    }

    /**
     * Returns the list of player names in the specified room.
     *
     * @param spielraumName the room name
     * @return a list of player names
     */
    @Override
    public ArrayList<String> getRaumSpieler(String spielraumName) {
        ArrayList<String> namen = new ArrayList<>();
        List<Spieler> spielerListe = this.context.spielraeume.get(spielraumName);
        if (spielerListe == null) {
            return null;
        }
        for (Spieler s : spielerListe) {
            namen.add(s.getName());
        }
        return namen;
    }



    /**
     * Adds a new message to the specified room chat.
     *
     * @param nachricht the message content
     * @param sender the sender's name
     * @param empfaenger the target room
     */
    @Override
    public void neueSpielraumNachricht(String nachricht, String sender, String empfaenger) {
        synchronized (this){
        this.context.chatVerlauf.putIfAbsent(empfaenger, new ArrayList<>());
        this.context.chatVerlauf.get(empfaenger).add(sender + ": " + nachricht);
        notifyAll();
        }
    }

    /**
     * Retrieves the chat history of a specified room.
     *
     * @param empfaenger the room name
     * @return chat history as a string
     */
    @Override
    public String getSpielraumChatverlauf(String empfaenger) {
        List<String> verlauf = context.chatVerlauf.getOrDefault(empfaenger, new ArrayList<>());
        return String.join("\n", verlauf);
    }

    /**
     * Checks if a user is the host of the specified room.
     *
     * @param spielerName the name of the player
     * @param spielraumName the room name
     * @return true if the user is the host, false otherwise
     */
    @Override
    public boolean isHost(String spielerName, String spielraumName) {
        return Objects.equals(this.context.hosts.get(spielraumName), spielerName);
    }

    /**
     * Checks if a player is present in the specified room.
     *
     * @param spielraumName the room name
     * @param spielerName the player name
     * @return true if the player is in the room
     */
    @Override
    public boolean istInRaum(String spielraumName, String spielerName) {
        return this.context.spielraeume.getOrDefault(spielraumName, new ArrayList<>())
                .stream().anyMatch(s -> s.getName().equals(spielerName));
    }

    /**
     * Checks if the room has reached its player capacity.
     *
     * @param spielraumName the room name
     * @return true if room is full (3 players), false otherwise
     */
    @Override
    public boolean raumVoll(String spielraumName) {
        return this.context.spielraeume.get(spielraumName).size() >= 3;
    }

    /**
     * Receives a heartbeat signal from a client to mark it as active.
     *
     * @param username the username sending the heartbeat
     */
    @Override
    public void receiveHeartbeat(String username) {
        context.lastHeartbeats.put(username, System.currentTimeMillis());
    }

    /**
     * Returns a list of all active game rooms and their players.
     *
     * @return list of GameroomDTO objects
     * @throws RemoteException if RMI communication fails
     */
    @Override
    public List<GameroomDTO> getRooms() throws RemoteException {
        List<GameroomDTO> roomList = new ArrayList<>();
        for (Map.Entry<String, List<Spieler>> entry : this.context.spielraeume.entrySet()) {
            List<Spieler> players = entry.getValue();
            String player1 = players.size() > 0 ? players.get(0).getName() : "";
            String player2 = players.size() > 1 ? players.get(1).getName() : "";
            String player3 = players.size() > 2 ? players.get(2).getName() : "";
            roomList.add(new GameroomDTO(entry.getKey(), player1, player2, player3));
        }
        return roomList;
    }

    @Override
    public Account getAccount(String playerName){
        for (Account account : context.accounts) {
            if (account.get_username().equals(playerName)) {
                return account;
            }
        }
        return null;
    }

    @Override
    public List<Account> getAccounts(){
        return this.context.accounts;
    }

    @Override
    public List<LeaderboardEntryDTO> getLeaderboard() throws RemoteException {
        return context.accounts.stream()
                .sorted(Comparator.comparingInt(Account::get_wins).reversed())
                .limit(10)
                .map(DTOConverter::toDTo)
                .collect(Collectors.toList());
    }
}