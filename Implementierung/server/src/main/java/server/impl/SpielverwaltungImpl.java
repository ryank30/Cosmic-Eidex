package server.impl;

import dto.StichDTO;
import server.ServerContext;
import api.Spielverwaltung;
import spiel.Spiel;
import spiel.Karte;
import spiel.Spieler;
import mapper.DTOConverter;
import dto.GameStateDTO;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

public class SpielverwaltungImpl extends UnicastRemoteObject implements Spielverwaltung {

    private final ServerContext context;

    public SpielverwaltungImpl(ServerContext context) throws RemoteException {
        this.context = context;
    }
    /**
     * handles the creation of the gamestate and how the client accesses it.
     */

    @Override
    public GameStateDTO getGameState(String spielraumName) throws RemoteException {
        Spiel game = this.context.spiele.get(spielraumName);
        if (game == null) return null;
        return DTOConverter.toDTO(game);
    }


    @Override
    public StichDTO getStichState(String spielraumName) throws RemoteException {
        Spiel game = this.context.spiele.get(spielraumName);
        return DTOConverter.toDTO(game.getStich());
    }

    /**
     * returns the gamemode of the game, being Obenabe, Undenufe, Trumpf.
     * @return String
     * @throws RemoteException
     */
    @Override
    public String getGameMode(String spielraumName) throws RemoteException {
        Spiel game = this.context.spiele.get(spielraumName);
        return game.getMode();
    }

    /**
     * returns the trumpsuit of the game as a String.
     * @param spielraumName
     * @return
     * @throws RemoteException
     */
    @Override
    public String getTrumpSuit(String spielraumName) throws RemoteException {
        Spiel game = this.context.spiele.get(spielraumName);
        return game.getTrumpSuitString();
    }

    /**
     * serves as a method for a client to request a change to the game state by playing a card
     */
    @Override
    public void clientPlayCard(String spielraumName, String spielerName, String cardId) throws RemoteException {

        Spiel game = this.context.spiele.get(spielraumName);
        Spieler player = game.getAktuellerSpieler();
        Karte card = Spiel.getCardById(player.getHandkarten(), cardId);

        game.playCard(player, card);
        while (game.getAktuellerSpieler().isBot() && game.isAktiv()) {
            try {
                if (game.isAktiv()) {
                    game.playBotMove();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void spielStarten(String spielraumName) {
        List<Spieler> spieler = this.context.spielraeume.get(spielraumName);
        if (spieler == null || spieler.size() < 2)
            throw new RuntimeException("Nicht genug Spieler.");
        Spiel neuesSpiel = new Spiel(spielraumName, spieler);
        neuesSpiel.startGame();
        neuesSpiel.setAktiv(true);
        this.context.spiele.put(spielraumName, neuesSpiel);

    }

    /**
     * Ends the game in the specified room.
     *
     * @param spielraumName the room name
     * @throws Exception if the game is not found
     */
    @Override
    public void spielBeenden(String spielraumName) throws Exception {
        Spiel spiel = getSpiel(spielraumName);
        spiel.setAktiv(false);
    }

    /**
     * Removes a player from the game in the specified room.
     *
     * @param spielraumName the room name
     * @param spielerName the player name
     * @throws Exception if the game is not found
     */
    @Override
    public void spielVerlassen(String spielraumName, String spielerName) throws Exception {
        Spiel spiel = getSpiel(spielraumName);
        spiel.getSpielerListe().removeIf(s -> s.getName().equals(spielerName));
    }


    /**
     * Checks if it is currently the player's turn in the specified game.
     *
     * @param spielraumName the room name
     * @param spielerName the player name
     * @return true if it's the player's turn
     * @throws Exception if game or player is not found
     */
    @Override
    public boolean istAmZug(String spielraumName, String spielerName) throws Exception {
        Spiel spiel = getSpiel(spielraumName);
        return spiel.getAktuellerSpieler().getName().equals(spielerName);
    }

    /**
     * Retrieves the hand cards of a player in the specified game.
     *
     * @param spielraumName the room name
     * @param spielerName the player name
     * @return list of card codes (e.g., "Rot:7")
     * @throws Exception if game or player is not found
     */
    @Override
    public ArrayList<String> getHandkarten(String spielraumName, String spielerName) throws Exception {
        Spieler spieler = getSpieler(getSpiel(spielraumName), spielerName);
        ArrayList<String> codes = new ArrayList<>();
        for (Karte k : spieler.getHandkarten()) {
            codes.add(k.getFarbe() + ":" + k.getRang());
        }
        return codes;
    }

    /**
     * Returns the current trick (played cards) in the specified game.
     *
     * @param spielraumName the room name
     * @return list of card codes in the current trick
     * @throws Exception if the game is not found
     */
    /*
    @Override
    public Stich getStich(String spielraumName) throws Exception {
        spiel spiel = getSpiel(spielraumName);
        return spiel.getStich();
    }
    */

    /**
     * Returns the points of a player in the specified game.
     *
     * @param spielraumName the room name
     * @param spielerName the player name
     * @return the number of points
     * @throws Exception if game or player is not found
     */
    @Override
    public int getPunkte(String spielraumName, String spielerName) throws Exception {
        Spieler spieler = getSpieler(getSpiel(spielraumName), spielerName);
        return spieler.getPunkte();
    }

    /**
     * Returns the total score overview of all players in the game.
     *
     * @param spielraumName the room name
     * @return formatted string of scores per player
     * @throws Exception if the game is not found
     */
    @Override
    public String getPunktestand(String spielraumName) throws Exception {
        Spiel spiel = getSpiel(spielraumName);
        StringBuilder sb = new StringBuilder();
        for (Spieler s : spiel.getSpielerListe()) {
            sb.append(s.getName()).append(": ").append(s.getPunkte()).append(" Punkte\n");
        }
        return sb.toString();
    }

    /**
     * Checks if the game in the specified room has started.
     *
     * @param spielraumName the room name
     * @return true if game is active
     * @throws Exception if the game is not found
     */
    @Override
    public boolean spielGestartet(String spielraumName) throws Exception {
        return getSpiel(spielraumName).isAktiv();
    }

    /**
     * Checks if the game in the specified room is over.
     *
     * @param spielraumName the room name
     * @return true if the game is inactive
     * @throws Exception if the game is not found
     */
    @Override
    public boolean spielVorbei(String spielraumName) throws Exception {
        return !getSpiel(spielraumName).isAktiv();
    }

    /**
     * Removes the game and its chat log from the server.
     *
     * @param spielraumName the room name
     * @throws Exception if any internal error occurs
     */
    @Override
    public void spielLoeschen(String spielraumName) throws Exception {
        this.context.spiele.remove(spielraumName);
        this.context.chatVerlauf.remove(spielraumName);
    }

    /**
     * Sets the game state to ended (inactive).
     *
     * @param spielraumName the room name
     * @throws Exception if the game is not found
     */
    @Override
    public void setSpielVorbei(String spielraumName) throws Exception {
        getSpiel(spielraumName).setAktiv(false);
    }


    /**
     * Returns the list of players currently in the game.
     *
     * @param spielraumName the room name
     * @return list of Spieler objects
     * @throws Exception if the game is not found
     */
    /*
    @Override
    public ArrayList<Spieler> getSpielSpieler(String spielraumName) throws Exception {
        return new ArrayList<>(getSpiel(spielraumName).getSpielerListe());
    }
    */

    /**
     * Retrieves a spiel instance by room name.
     *
     * @param spielraumName the room name
     * @return the spiel object
     * @throws Exception if no game is found
     */
    private Spiel getSpiel(String spielraumName) throws Exception {
        Spiel spiel = this.context.spiele.get(spielraumName);
        if (spiel == null)
            throw new Exception("spiel nicht gefunden");
        return spiel;
    }

    /**
     * Retrieves a Spieler from a spiel instance by name.
     *
     * @param spiel the game object
     * @param name the name of the player
     * @return the Spieler object
     * @throws Exception if player is not found
     */
    private Spieler getSpieler(Spiel spiel, String name) throws Exception {
        return spiel.getSpielerListe().stream()
                .filter(s -> s.getName().equals(name))
                .findFirst()
                .orElseThrow(() -> new Exception("Spieler nicht gefunden"));
    }

    /**
     * Finds a card in a player's hand by card code.
     *
     * @param hand list of Karten
     * @param code card code in the format "Farbe:Rang"
     * @return the Karte object or null if not found
     */
    public Karte findKarte(List<Karte> hand, String code) {
        for (Karte k : hand) {
            String kCode = k.getFarbe() + ":" + k.getRang();
            if (kCode.equals(code))
                return k;
        }
        return null;
    }
    /**
     * Returns the room name associated with the recipient.
     *
     * @param empfaenger the recipient identifier
     * @return the same string (placeholder)
     */
    private String spielraumNameFromEmpfaenger(String empfaenger) {
        return empfaenger;
    }

    //chat part, to be refactored
    /**
     * Posts a message from sender to recipient in the game chat.
     *
     * @param nachricht the message content
     * @param sender the name of the sender
     * @param empfaenger the target recipient (room name)
     * @throws Exception if the room is not found
     */
    @Override
    public void neueSpielNachricht(String nachricht, String sender, String empfaenger) throws Exception {
        String eintrag = sender + " -> " + empfaenger + ": " + nachricht;
        context.chatVerlauf.putIfAbsent(spielraumNameFromEmpfaenger(empfaenger), new ArrayList<>());
        context.chatVerlauf.get(spielraumNameFromEmpfaenger(empfaenger)).add(eintrag);
    }

    /**
     * Returns the full game chat log of a room.
     *
     * @param spielraumName the room name
     * @return chat log as formatted string
     * @throws Exception if the room is not found
     */
    @Override
    public String getSpielChatverlauf(String spielraumName) throws Exception {
        List<String> verlauf = context.chatVerlauf.getOrDefault(spielraumName, new ArrayList<>());
        return String.join("\n", verlauf);
    }

}