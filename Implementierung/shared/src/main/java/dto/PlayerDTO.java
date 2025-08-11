package dto;

import java.io.Serializable;
import java.util.List;

/**
 * A Data Transfer object (DTO) representing a player in the game.
 */
public class PlayerDTO implements Serializable {
    private final String name;
    private final List<CardDTO> hand;
    private final int points;
    private final int winPoints;
    private final List<CardDTO> validMoves;

    /**
     * Constructs a new PlayerDTO instance with the given data.
     * @param name the name of the player
     * @param hand the cards in the player`s hand
     * @param validMoves the list of cards the player can validly play.
     * @param points the current score of the player in this round.
     * @param winPoints the sum of win points across rounds
     */
    public PlayerDTO(String name, List<CardDTO> hand,List<CardDTO> validMoves ,int points, int winPoints) {
        this.name = name;
        this.hand = hand;
        this.validMoves = validMoves;
        this.points = points;
        this.winPoints = winPoints;
    }
    /**
     * Gets the player's name.
     *
     * @return the player's name
     */
    public String getName() {
        return name;
    }
    /**
     * Gets the list of cards in the player's hand.
     *
     * @return the player's hand as a list of  CardDTO
     */
    public List<CardDTO> getHand() {
        return hand;
    }
    /**
     * Gets the number of points the player has earned in the current round.
     *
     * @return the player's current round points
     */
    public int getPoints() {
        return points;
    }
    public int getWinPoints() {
        return winPoints;
    }
    /**
     * Gets the list of cards the player is allowed to play.
     *
     * @return the valid moves as a list of {@link CardDTO}
     */
    public List<CardDTO> getValidMoves() {
        return validMoves;
    }
}