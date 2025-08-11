package api;

import account.Account;
import dto.GameroomDTO;
import dto.LeaderboardEntryDTO;


import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

/**
 * Remote interface for managing game rooms, including creation, joining,
 * leaving, chat, bots, and room metadata.
 */
public interface Raumverwaltung extends Remote {
    Account getAccount(String playername) throws RemoteException;

    List<Account> getAccounts() throws RemoteException;

    List<LeaderboardEntryDTO> getLeaderboard() throws RemoteException;
    /**
     * Creates a new game room with the given host player and optional password.
     *
     * @param hostSpielerName the name of the host player
     * @param spielraumName the desired room name
     * @param passwort optional password (may be null or empty)
     * @throws RemoteException if RMI communication fails
     */
    void spielraumErstellen(String hostSpielerName, String spielraumName, String passwort) throws RemoteException;

    /**
     * Allows a player to join an existing room if the password matches.
     *
     * @param spielerName the name of the player
     * @param spielraumName the room to join
     * @param passwort the password for the room
     * @throws RemoteException if RMI communication fails
     */
    void spielraumBeitreten(String spielerName, String spielraumName, String passwort) throws RemoteException;

    /**
     * Returns the name of the room that a user is currently in.
     *
     * @param username the username to look up
     * @return the name of the room or null if not found
     * @throws RemoteException if RMI communication fails
     */
    String getRaumName(String username) throws RemoteException;

    /**
     * Removes a player from the specified room.
     *
     * @param spielerName the player's name
     * @param spielraumName the room name
     * @throws RemoteException if RMI communication fails
     */
    void spielraumVerlassen(String spielerName, String spielraumName) throws RemoteException;

    /**
     * Forcefully removes a player from the room (e.g., by the host).
     *
     * @param spielerName the player's name
     * @param spielraumName the room name
     * @throws RemoteException if RMI communication fails
     */
    void spielerEntfernen(String spielerName, String spielraumName) throws RemoteException;

    /**
     * Adds a bot to the specified room.
     *
     * @param spielraumName the room name
     * @throws RemoteException if RMI communication fails
     */

    void easyBotHinzufuegen(String spielraumName) throws RemoteException;

    void hardBotHinzufuegen(String spielraumName) throws RemoteException;


    /**
     * Removes the bot from the specified room.
     *
     * @param spielraumName the room name
     * @throws RemoteException if RMI communication fails
     */
    void botEntfernen(String spielraumName) throws RemoteException;

    /**
     * Returns the names of all players currently in the room.
     *
     * @param spielraumName the room name
     * @return list of player names
     * @throws RemoteException if RMI communication fails
     */
    ArrayList<String> getRaumSpieler(String spielraumName) throws RemoteException;

    /**
     * Posts a new message to the room chat.
     *
     * @param nachricht the message content
     * @param sender the sender's name
     * @param empfaenger the target room name
     * @throws RemoteException if RMI communication fails
     */
    void neueSpielraumNachricht(String nachricht, String sender, String empfaenger) throws RemoteException;

    /**
     * Retrieves the chat history for the specified room.
     *
     * @param empfaenger the room name
     * @return chat messages as a string
     * @throws RemoteException if RMI communication fails
     */
    String getSpielraumChatverlauf(String empfaenger) throws RemoteException;

    /**
     * Checks whether the specified player is the host of the room.
     *
     * @param spielerName the player's name
     * @param spielraumName the room name
     * @return true if player is host
     * @throws RemoteException if RMI communication fails
     */
    boolean isHost(String spielerName, String spielraumName) throws RemoteException;

    /**
     * Checks if a player is currently in the specified room.
     *
     * @param spielraumName the room name
     * @param spielerName the player name
     * @return true if the player is in the room
     * @throws RemoteException if RMI communication fails
     */
    boolean istInRaum(String spielraumName, String spielerName) throws RemoteException;

    /**
     * Checks whether the room is full.
     *
     * @param spielraumName the room name
     * @return true if the room has reached max capacity
     * @throws RemoteException if RMI communication fails
     */
    boolean raumVoll(String spielraumName) throws RemoteException;

    /**
     * Receives a heartbeat signal from a user to mark them as active.
     *
     * @param username the user's name
     * @throws RemoteException if RMI communication fails
     */
    void receiveHeartbeat(String username) throws RemoteException;

    /**
     * Returns a list of all available game rooms with their player data.
     *
     * @return list of GameroomDTO objects
     * @throws RemoteException if RMI communication fails
     */
    List<GameroomDTO> getRooms() throws RemoteException;


}
