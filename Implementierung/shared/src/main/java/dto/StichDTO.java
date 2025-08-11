package dto;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * This class captures which player played which card in a single round of trick-taking.
 */
public class StichDTO implements Serializable {

    // Use player names (String) and card codes (String) instead of full objects
    private LinkedHashMap<String, String> cardsInTrick;
    /**
     * Constructs a new  StichDTO with the given mapping of players to their played cards.
     * @param cardsInTrick a LinkedHashMap where keys are player names and values are card IDs
     */
    public StichDTO(LinkedHashMap<String, String> cardsInTrick) {
        this.cardsInTrick = cardsInTrick;
    }

    public LinkedHashMap<String, String> getCardsInTrick() {
        return cardsInTrick;
    }
/**
        * Updates the mapping of played cards for this trick.
     *
             * @param cardsInTrick a new {@link LinkedHashMap} of player names to card IDs
     */
    public void setCardsInTrick(LinkedHashMap<String, String> cardsInTrick) {
        this.cardsInTrick = cardsInTrick;
    }
}