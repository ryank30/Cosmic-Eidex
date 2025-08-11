package spiel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Represents a player in the game, including their hand, score, and turn status.
 */
public class Spieler implements Serializable {
    private final String name;
    private final List<Karte> handkarten;
    private int punkte;
    private int winPoint;
    private boolean amZug;
    private final boolean isBot;
    private final List<Karte> validMoves;

    /**
     * Constructs a new player with the given name.
     *
     * @param name the player's name
     */
    public Spieler(String name, boolean isBot) {
        this.name = name;
        this.isBot = isBot;
        this.handkarten = new ArrayList<>();
        this.punkte = 0;
        this.amZug = false;
        this.winPoint = 0;
        this.validMoves = new ArrayList<>();
    }

    /**
     * Returns the player's name.
     *
     * @return the name of the player
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the list of cards currently in the player's hand.
     *
     * @return list of Karten
     */
    public List<Karte> getHandkarten() {
        return handkarten;
    }

    /**
     * Adds a card to the player's hand.
     *
     * @param karte the card to add
     */
    public void addKarte(Karte karte) {
        handkarten.add(karte);
    }

    /**
     * Removes a card from the player's hand.
     *
     * @param karte the card to remove
     */
    public void removeKarte(Karte karte) {
        handkarten.remove(karte);
    }

    /**
     * Returns the current number of points the player has.
     *
     * @return player's score
     */
    public int getPunkte() {
        return punkte;
    }

    /**
     * Adds the given number of points to the player's total score.
     *
     * @param punkte the number of points to add
     */
    public void addPunkte(int punkte) {
        this.punkte += punkte;
    }

    public int getWinPoint() {
        return winPoint;
    }

    public void setWinPoint(int winPoint) {
        this.winPoint = winPoint;
    }

    /**
     * Checks whether it's currently this player's turn.
     *
     * @return true if it's the player's turn
     */
    public boolean isAmZug() {
        return amZug;
    }

    /**
     * Sets whether it's currently this player's turn.
     *
     * @param amZug true if the player should be marked as "on turn"
     */
    public void setAmZug(boolean amZug) {
        this.amZug = amZug;
    }

    /**
     * Indicates whether this player is a bot.
     *
     * @return true if the player is a bot, false if the player is a human
     */
    public boolean isBot() {
        return isBot;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Spieler)) return false;
        Spieler spieler = (Spieler) o;
        return Objects.equals(name, spieler.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    public List<Karte> getValidMoves() {
        return validMoves;
    }
    public void setValidMoves(List<Karte> validcards) {
        validMoves.clear();
        validMoves.addAll(validcards);
    }
}
