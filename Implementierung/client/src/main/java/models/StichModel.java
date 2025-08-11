package models;

import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;

/**
 * Model class representing a trick (stich) in a card game.
 */
public class StichModel {

    //player name | CardId
    private final ObservableMap<String, String> cardsInTrick = FXCollections.observableHashMap();

    /**
     * Creates a new empty trick model.
      */
    public StichModel() {
    }

    /**
     * Gets the observable map of cards currently in this trick.
     * @return the observable map containing player names mapped to their played cards.
     */
    public ObservableMap<String, String> getCardsInTrick() {
        return cardsInTrick;
    }

    /**
     * Adds a card to the trick for the specified player.
     * @param playerName name of the player playing.
     * @param cardCode code of the card being played.
     */
    public void putCard(String playerName, String cardCode) {
        cardsInTrick.put(playerName, cardCode);
    }

    /**
     * Clears all cards from the trick.
     */
    public void clear() {
        cardsInTrick.clear();
    }
}