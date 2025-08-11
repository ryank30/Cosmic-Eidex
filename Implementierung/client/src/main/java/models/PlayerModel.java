package models;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.List;

/**
 * Model class representing a player in the game.
 */
public class PlayerModel {
    private final String name;
    private final IntegerProperty score = new SimpleIntegerProperty();
    private final ListProperty<String> hand = new SimpleListProperty<>(FXCollections.observableArrayList());
    private final BooleanProperty isMyTurn = new SimpleBooleanProperty();
    private final IntegerProperty winPoints = new SimpleIntegerProperty();
    private final List<String> validCard = new ArrayList<>();

    /**
     * creates a new playerModel with the specified name.
     *
     * @param name the player´s name.
     */
    public PlayerModel(String name) {
        this.name = name;
    }

    /**
     * Gets the observable list of cards in the player´s hand.
     * @return the observable list of hand cards
     */
    public ObservableList<String> getHand() {
        return hand.get();
    }

    /**
     * Sets the player´s hand to new list of cards.
     * @param newHand the new list of cards for the player´s hand.
     */
    public void setHand(List<String> newHand) {
        hand.set(FXCollections.observableArrayList(newHand));
    }

    /**
     * Gets the hand property for data binding purposes.
     * @return the ListProperty representing the player´s hand.
     */
    public ListProperty<String> handProperty() {
        return hand;
    }
    /**
     * Gets the player's current score.
     *
     * @return the current score value
     */
    public int getScore() {
        return score.get();
    }
    /**
     * Sets the player's score to a new value.
     *
     * @param newScore the new score value
     */
    public void setScore(int newScore) {
        score.set(newScore);
    }
    /**
     * Gets the score property for data binding purposes.
     *
     * @return the IntegerProperty representing the player's score
     */
    public IntegerProperty scoreProperty() {
        return score;
    }
    /**
     * Gets the turn status property for data binding purposes.
     *
     * @return the BooleanProperty indicating if it's this player's turn
     */
    public BooleanProperty isMyTurnProperty() {
        return isMyTurn;
    }
    public void setIsMyTurn(Boolean newIsMyTurn) {
        isMyTurn.set(newIsMyTurn);
    }
    /**
     * Gets the player's current win points.
     *
     * @return the current win points value
     */
    public int getWinPoints(){
        return winPoints.get();
    }
    /**
     * Gets the win points property for data binding purposes.
     *
     * @return the IntegerProperty representing the player's win points
     */
    public IntegerProperty winPointsProperty() {
        return winPoints;
    }
    /**
     * Sets the player's win points to a new value.
     *
     * @param newWinPoints the new win points value
     */
    public void setWinPoints(int newWinPoints){
        winPoints.set(newWinPoints);
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
     * Gets the list of cards that are currently valid for this player to play.
     *
     * @return the list of valid cards
     */
    public List<String> getValidCard() {
        return validCard;
    }
    public void setValidCard(List<String> newValidCard) {
        validCard.clear();
        validCard.addAll(newValidCard);
    }
}