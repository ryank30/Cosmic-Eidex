package api;

import dto.GameStateDTO;
import dto.StichDTO;

import java.rmi.RemoteException;


import java.rmi.Remote;
import java.util.ArrayList;

/**
 * Remote interface for managing the game lifecycle, player actions, game state,
 * and in-game chat within a game room.
 */
public interface Spielverwaltung extends Remote {



    //das senden des gamestates
    StichDTO getStichState(String spielraumName) throws RemoteException;

    /**
     * returns a game state DTO which holds the necessary information to draw the gameboard for the client.
     * @param spielraumName
     * @return
     * @throws RemoteException
     */
    GameStateDTO getGameState(String spielraumName) throws RemoteException;

    /**
     * returns the gamemode of the game, being Obenabe, Undenufe, Trumpf.
     * @return String
     * @throws RemoteException
     */
    String getGameMode(String spielraumName) throws RemoteException;

    String getTrumpSuit(String spielraumName) throws RemoteException;

    /**
     * makes the server process a change in game state due to a card played.
     * @param playerName
     * @param cardId
     * @throws RemoteException
     */
    void clientPlayCard(String spielraumName, String playerName, String cardId) throws RemoteException;
    // Lebenszyklus des Spiels


    /**
     * Starts the game in the specified room.
     *
     * @param spielraumName the room name
     * @throws Exception if the game cannot be started
     */
    void spielStarten(String spielraumName) throws Exception;

    /**
     * Ends the game in the specified room.
     *
     * @param spielraumName the room name
     * @throws Exception if the game cannot be ended
     */
    void spielBeenden(String spielraumName) throws Exception;

    /**
     * Removes a player from the game.
     *
     * @param spielraumName the room name
     * @param spielerName the name of the player leaving
     * @throws Exception if the player cannot be removed
     */
    void spielVerlassen(String spielraumName, String spielerName) throws Exception;

    /**
     * Checks whether it is the player's turn.
     *
     * @param spielraumName the room name
     * @param spielerName the player name
     * @return true if it is the player's turn
     * @throws Exception if check fails
     */
    boolean istAmZug(String spielraumName, String spielerName) throws Exception;

    // Abfrage des Spielstatus

    /**
     * Returns the player's current hand cards.
     *
     * @param spielraumName the room name
     * @param spielerName the player name
     * @return list of card codes
     * @throws Exception if retrieval fails
     */
    ArrayList<String> getHandkarten(String spielraumName, String spielerName) throws Exception;

    /**
     * Returns the cards currently in the trick.
     *
     * @param spielraumName the room name
     * @return list of card codes
     * @throws Exception if retrieval fails
     */
    //Stich getStich(String spielraumName) throws Exception;

    /**
     * Returns the current point total for the player.
     *
     * @param spielraumName the room name
     * @param spielerName the player name
     * @return the player's score
     * @throws Exception if retrieval fails
     */
    int getPunkte(String spielraumName, String spielerName) throws Exception;

    /**
     * Returns the full score board for the game.
     *
     * @param spielraumName the room name
     * @return formatted string of all players' scores
     * @throws Exception if retrieval fails
     */
    String getPunktestand(String spielraumName) throws Exception;

    /**
     * Checks whether the game has started.
     *
     * @param spielraumName the room name
     * @return true if the game is active
     * @throws Exception if check fails
     */
    boolean spielGestartet(String spielraumName) throws Exception;

    /**
     * Checks whether the game is over.
     *
     * @param spielraumName the room name
     * @return true if the game is inactive
     * @throws Exception if check fails
     */
    boolean spielVorbei(String spielraumName) throws Exception;

    // chat

    /**
     * Posts a new chat message in the game.
     *
     * @param nachricht the message content
     * @param sender the sender's name
     * @param empfaenger the receiver or room
     * @throws Exception if posting fails
     */
    void neueSpielNachricht(String nachricht, String sender, String empfaenger) throws Exception;

    /**
     * Returns the full game chat history.
     *
     * @param spielraumName the room name
     * @return chat history as string
     * @throws Exception if retrieval fails
     */
    String getSpielChatverlauf(String spielraumName) throws Exception;

    // Spieler info

    /**
     * Returns the list of players in the game.
     *
     * @param spielraumName the room name
     * @return list of Spieler objects
     * @throws Exception if retrieval fails
     */
    //ArrayList<Spieler> getSpielSpieler(String spielraumName) throws Exception;

    // Admin

    /**
     * Deletes the game and removes its data.
     *
     * @param spielraumName the room name
     * @throws Exception if deletion fails
     */
    void spielLoeschen(String spielraumName) throws Exception;

    /**
     * Marks the game as over without removing it.
     *
     * @param spielraumName the room name
     * @throws Exception if update fails
     */
    void setSpielVorbei(String spielraumName) throws Exception;
}
