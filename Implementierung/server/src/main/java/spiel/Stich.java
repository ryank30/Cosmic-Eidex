package spiel;

import java.io.Serializable;
import java.util.*;

/**
 * Represents a single trick (Stich) in the card game.
 * Tracks the cards played by each player in order.
 */
public class Stich implements Serializable {

    private final LinkedHashMap<Spieler, Karte> cardsInTrick;

    /**
     * Constructs an empty trick.
     */
    public Stich() {
        this.cardsInTrick = new LinkedHashMap<>();
    }


    public List<Karte> getCards() {
        return new ArrayList<>(cardsInTrick.values());
    }

    /**
     * Adds a card played by a player to the trick.
     *
     * @param player the player who played the card
     * @param card the card played
     */
    public void addCard(Spieler player, Karte card) {
        cardsInTrick.put(player, card);
    }

    /**
     * Returns all cards played in this trick, along with the corresponding players.
     *
     * @return a set of entries (Spieler, Karte)
     */
    public Set<Map.Entry<Spieler, Karte>> getAllCards() {
        return cardsInTrick.entrySet();
    }

    /**
     * Returns the number of cards currently in the trick.
     *
     * @return the number of cards
     */
    public int getCardCount() {
        return cardsInTrick.size();
    }

    /**
     * Returns the first card played in the trick.
     *
     * @return the first card, or null if none
     */
    public Karte getFirstCard() {
        if (cardsInTrick.isEmpty()) return null;
        return cardsInTrick.values().iterator().next();
    }

    /**
     * Returns the card played by the specified player.
     *
     * @param player the player
     * @return the card played by that player, or null if not found
     */
    public Karte getCardByPlayer(Spieler player) {
        return cardsInTrick.get(player);
    }

    /**
     * Clears the trick, removing all cards.
     */
    public void clear() {
        cardsInTrick.clear();
    }


    /**
     * Determines the winner of this trick based on trump, lead suit, and game mode.
     *
     * @param trumpSuit the trump suit of the game
     * @param undenufe true if Undenufe mode (low beats high)
     * @param obenabe true if Obenabe mode (high beats low, no trump)
     * @return the winning player of this trick
     */
    /**
     * Determines the winner of this trick based on trump, lead suit, and game mode.
     *
     * @param trumpSuit the trump suit of the game
     * @param undenufe true if Undenufe mode (low beats high, no trump)
     * @param obenabe true if Obenabe mode (high beats low, no trump)
     * @return the winning player of this trick
     */
    public Spieler getWinner(Farbe trumpSuit, boolean undenufe, boolean obenabe) {
        Spieler winner = null;
        Karte winningCard = null;

        Farbe leadSuit = getFirstCard() != null ? getFirstCard().getFarbe() : null;

        for (Map.Entry<Spieler, Karte> entry : cardsInTrick.entrySet()) {
            Spieler player = entry.getKey();
            Karte card = entry.getValue();

            if (winningCard == null) {
                winner = player;
                winningCard = card;
                continue;
            }

            boolean cardIsTrump = !(obenabe || undenufe) && card.getFarbe() == trumpSuit;
            boolean winningIsTrump = !(obenabe || undenufe) && winningCard.getFarbe() == trumpSuit;

            if (cardIsTrump && !winningIsTrump) {
                winner = player;
                winningCard = card;
            } else if (cardIsTrump == winningIsTrump) {
                boolean sameSuit = card.getFarbe() == winningCard.getFarbe();
                boolean followsLead = card.getFarbe() == leadSuit;
                boolean winningFollowsLead = winningCard.getFarbe() == leadSuit;

                if (sameSuit || (followsLead && winningFollowsLead)) {
                    int cardStrength = getCardStrength(card, cardIsTrump, undenufe);
                    int winningStrength = getCardStrength(winningCard, winningIsTrump, undenufe);

                    if (cardStrength > winningStrength) {
                        winner = player;
                        winningCard = card;
                    }
                }
            }
        }

        return winner;
    }



    int getCardStrength(Karte card, boolean isTrump, boolean undenufe) {
        Rang r = card.getRang();

        if (undenufe) {
            return 8 - r.ordinal();
        }

        else if (isTrump) {
            switch (r) {
                case JACK: return 20;
                case NINE: return 19;
                case ACE: return 18;
                case KING: return 17;
                case QUEEN: return 16;
                case TEN: return 15;
                case EIGHT: return 14;
                case SEVEN: return 13;
                case SIX: return 12;
            }
        } else {
            switch (r) {
                case ACE: return 18;
                case KING: return 17;
                case QUEEN: return 16;
                case JACK: return 15;
                case TEN: return 14;
                case NINE: return 13;
                case EIGHT: return 12;
                case SEVEN: return 11;
                case SIX: return 10;
            }
        }

        return -1;
    }

    /**
     * Calculates the total point value of all cards in the trick based on game mode.
     *
     * @param gameMode the current game mode ("OBENABE", "UNDENUFE", or trump suit as string)
     * @param trumpSuit the trump suit (can be null if OBENABE or UNDENUFE)
     * @return total points in the trick
     */
    public int getTotalPoints(String gameMode, Farbe trumpSuit) {
        boolean isObenabe = gameMode.equals("OBENABE");
        boolean isUndenufe = gameMode.equals("UNDENUFE");

        int total = 0;
        for (Karte karte : cardsInTrick.values()) {
            total += karte.getWert(trumpSuit, isObenabe, isUndenufe);
        }
        return total;
    }


}
