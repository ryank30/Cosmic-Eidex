package spiel;

import java.util.*;

/**
 * Represents a deck of cards for the Eidex game.
 * Handles creation, shuffling, dealing, and trump determination.
 */
public class Deck {
    private final List<Karte> karten;

    public Deck() {
        this.karten = generateFullDeck();
        shuffle();
    }

    /**
     * Generates all 36 cards for Eidex (6 to Ace for each of 4 suits).
     *
     * @return list of all 36 cards
     */
    private List<Karte> generateFullDeck() {
        List<Karte> deck = new ArrayList<>();
        for (Farbe farbe : Farbe.values()) {
            for (Rang rang : Rang.values()) {
                deck.add(new Karte(farbe, rang));
            }
        }
        return deck;
    }

    /**
     * Shuffles the deck randomly.
     */
    public void shuffle() {
        Collections.shuffle(karten);
    }

    /**
     * Deals 12 cards to each of 3 players.
     *
     * @return list of 3 hands, each with 12 cards
     */
    public List<List<Karte>> deal() {
        List<List<Karte>> hands = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            hands.add(new ArrayList<>());
        }

        for (int i = 0; i < 36; i++) {
            hands.get(i % 3).add(karten.get(i));
        }

        return hands;
    }

    /**
     * Reveals the last card in the deck (used to determine game mode).
     *
     * @return the last card
     */
    public Karte revealLastCard() {
        return karten.get(35);
    }

    /**
     * Determines the game mode based on the last card.
     *
     * @param lastCard the last dealt card
     * @return "OBENABE", "UNDENUFE", or the trump Farbe (e.g. "HEARTS")
     */
    public static String getGameMode(Karte lastCard) {
        if (lastCard.getRang() == Rang.ACE) return "OBENABE";
        if (lastCard.getRang() == Rang.SIX) return "UNDENUFE";
        return lastCard.getFarbe().name();
    }
}
