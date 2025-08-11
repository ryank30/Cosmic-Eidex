package spiel;

import java.util.Comparator;
import java.util.List;
import java.util.Random;

/**
 * Handles bot behavior based on difficulty.
 */
public class Bot {

    private static final Random random = new Random();

    /**
     * Executes a turn for the current bot player in the game.
     * @param spiel the current game instance containing game state and rules.
     */
    public static void playTurn(Spiel spiel) {
        Spieler bot = spiel.getAktuellerSpieler();
        if (!bot.isBot()) return;

        if (isHardBot(bot)) {
            playHard(bot, spiel);
        } else {
            playEasy(bot, spiel);
        }
    }
    /**
     * Determines if a bot player is a hard difficulty bot.
     * @param bot the bot player to check
     * @return true if this is a hard bot, false if it's an easy bot
     */
    private static boolean isHardBot(Spieler bot) {
        return bot.getName().toLowerCase().contains("hardbot");
    }

    static void playEasy(Spieler bot, Spiel spiel) {
        List<Karte> hand = bot.getHandkarten();
        Karte leadCard = spiel.getStich().getFirstCard();

        List<Karte> validCards = hand.stream()
                .filter(k -> spiel.isValidMove(bot, k, leadCard))
                .toList();

        if (validCards.isEmpty() && !hand.isEmpty()) {
            Karte fallback = hand.get(0);
            try {
                spiel.playCard(bot, fallback);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Karte chosen = validCards.get(random.nextInt(validCards.size()));
            try {
                spiel.playCard(bot, chosen);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    /**
     * Implements easy bot strategy with random card selection.
     * @param bot the bot player making the move
     * @param spiel the current game instance
     */
    static void playHard(Spieler bot, Spiel spiel) {
        List<Karte> hand = bot.getHandkarten();
        Karte leadCard = spiel.getStich().getFirstCard();
        Farbe trump = spiel.getTrumpSuit();

        List<Karte> validCards = hand.stream()
                .filter(k -> spiel.isValidMove(bot, k, leadCard))
                .toList();

        if (validCards.isEmpty()) {
            return;
        }

        Karte chosen;
        if (leadCard == null) {
            chosen = chooseLeadCard(validCards, trump, spiel);
        } else {
            chosen = chooseFollowCard(validCards, leadCard, trump, spiel);
        }

        if (chosen == null) {
            chosen = validCards.get(0);
        }

        try {
            spiel.playCard(bot, chosen);
        } catch (Exception e) {

            e.printStackTrace();
        }
    }
    /**
     * Selects the optimal card for leading a trick in hard bot strategy.
     * @param cards list of valid cards the bot can play
     * @param trump the current trump suit, may be null
     * @param spiel the current game instance for accessing game mode rules
     * @return the selected card to lead with, or the first card as fallback
     */
    static Karte chooseLeadCard(List<Karte> cards, Farbe trump, Spiel spiel) {
        if (cards.isEmpty()) return null;

        return cards.stream()
                .sorted((a, b) -> Integer.compare(
                        b.getWert(trump, spiel.isObenabe(), spiel.isUndenufe()),
                        a.getWert(trump, spiel.isObenabe(), spiel.isUndenufe())))
                .skip(1)
                .findFirst()
                .orElse(cards.get(0));
    }
    /**
     * Selects the optimal card for following a lead in hard bot strategy.
     * @param cards list of valid cards the bot can play
     * @param leadCard the card that was played to lead the trick
     * @param trump the current trump suit, may be null
     * @param spiel the current game instance for accessing game mode rules
     * @return the selected card to follow with, or null if no cards available
     */
    static Karte chooseFollowCard(List<Karte> cards, Karte leadCard, Farbe trump, Spiel spiel) {
        boolean isTrumpGame = (trump != null);
        Farbe leadSuit = leadCard.getFarbe();

        return cards.stream()
                .filter(k -> k.getFarbe() == leadSuit || (isTrumpGame && k.getFarbe() == trump))
                .filter(k -> {
                    int kValue = k.getWert(trump, spiel.isObenabe(), spiel.isUndenufe());
                    int leadValue = leadCard.getWert(trump, spiel.isObenabe(), spiel.isUndenufe());
                    return kValue > leadValue;
                })
                .sorted(Comparator.comparingInt(k -> k.getWert(trump, spiel.isObenabe(), spiel.isUndenufe())))
                .findFirst()
                .orElseGet(() ->
                        cards.stream()
                                .sorted(Comparator.comparingInt(k -> k.getWert(trump, spiel.isObenabe(), spiel.isUndenufe())))
                                .findFirst()
                                .orElse(null)
                );
    }
}
