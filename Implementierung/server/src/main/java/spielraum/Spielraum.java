package spielraum;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a game room with players, optional password, chat history, and bots.
 * Manages participant tracking and room-level metadata.
 */
public class Spielraum {
    private final String name;
    private final String passwort;
    private final String hostSpieler;
    private final List<String> teilnehmer = new ArrayList<>();
    private final List<String> bots;
    private final List<String> chatverlauf;

    /**
     * Constructs a new spielraum (game room) instance.
     *
     * @param name the name of the room
     * @param passwort the password for joining the room
     * @param hostSpieler the name of the host player
     */
    public Spielraum(String name, String passwort, String hostSpieler) {
        this.name = name;
        this.passwort = passwort;
        this.hostSpieler = hostSpieler;
        this.bots = new ArrayList<>();
        this.chatverlauf = new ArrayList<>();
        this.teilnehmer.add(hostSpieler);
    }

    /**
     * Returns the name of the room.
     *
     * @return room name
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the password required to join the room.
     *
     * @return room password
     */
    public String getPasswort() {
        return passwort;
    }

    /**
     * Returns the host player's name.
     *
     * @return host name
     */
    public String getHostSpieler() {
        return hostSpieler;
    }

    /**
     * Returns the list of all participants (including bots).
     *
     * @return list of participant names
     */
    public List<String> getTeilnehmer() {
        return teilnehmer;
    }

    /**
     * Returns the list of bots in the room.
     *
     * @return list of bot names
     */
    public List<String> getBots() {
        return bots;
    }

    /**
     * Returns the chat history of the room.
     *
     * @return list of chat messages
     */
    public List<String> getChatverlauf() {
        return chatverlauf;
    }

    /**
     * Adds a player to the room.
     *
     * @param spieler the player name
     */
    public void addTeilnehmer(String spieler) {
        teilnehmer.add(spieler);
    }

    /**
     * Removes a player from the room.
     *
     * @param spieler the player name
     */
    public void removeTeilnehmer(String spieler) {
        teilnehmer.remove(spieler);
    }

    /**
     * Checks if a player is in the room.
     *
     * @param spieler the player name
     * @return true if the player is present
     */
    public boolean isTeilnehmer(String spieler) {
        return teilnehmer.contains(spieler);
    }

    /**
     * Checks if the room has reached its maximum capacity.
     *
     * @return true if full (4 or more players)
     */
    public boolean isVoll() {
        return teilnehmer.size() >= 3;
    }

    /**
     * Adds a bot to the room (as participant).
     *
     * @param botName the name of the bot
     */
    public void addBot(String botName) {
        bots.add(botName);
        teilnehmer.add(botName);
    }

    /**
     * Removes a bot from the room (from both bot and participant lists).
     *
     * @param botName the name of the bot
     */
    public void removeBot(String botName) {
        bots.remove(botName);
        teilnehmer.remove(botName);
    }

    /**
     * Adds a chat message to the room's chat history.
     *
     * @param sender the name of the sender
     * @param nachricht the message content
     */
    public void addChatNachricht(String sender, String nachricht) {
        chatverlauf.add(sender + ": " + nachricht);
    }

    /**
     * Returns the entire chat history as a single formatted string.
     *
     * @return chat history with line breaks
     */
    public String getChatverlaufAsString() {
        return String.join("\n", chatverlauf);
    }
}
