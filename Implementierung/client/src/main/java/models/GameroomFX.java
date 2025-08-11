package models;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Represents a JavaFX-compatible model of a game room
 * with bindable player and room name properties.
 */
public class GameroomFX {

    private final SimpleStringProperty room_name;
    private final SimpleStringProperty player_1;
    private final SimpleStringProperty player_2;
    private final SimpleStringProperty player_3;

    /**
     * Constructs a GameroomFX with the given player names and room name.
     *
     * @param roomName the name of the game room
     * @param p1       the name of the first player
     * @param p2       the name of the second player
     * @param p3       the name of the third player
     */
    public GameroomFX(String roomName, String p1, String p2, String p3) {
        this.room_name = new SimpleStringProperty(roomName);
        this.player_1 = new SimpleStringProperty(p1);
        this.player_2 = new SimpleStringProperty(p2);
        this.player_3 = new SimpleStringProperty(p3);
    }

    /**
     * @return the property for the room name
     */
    public StringProperty room_nameProperty() { return room_name; }

    /**
     * @return the property for player 1
     */
    public StringProperty player_1Property() { return player_1; }

    /**
     * @return the property for player 2
     */
    public StringProperty player_2Property() { return player_2; }

    /**
     * @return the property for player 3
     */
    public StringProperty player_3Property() { return player_3; }

    /**
     * @return the room name as a String
     */
    public String getRoom_name() { return room_name.get(); }

    /**
     * @return the name of player 1
     */
    public String getPlayer_1() { return player_1.get(); }

    /**
     * @return the name of player 2
     */
    public String getPlayer_2() { return player_2.get(); }

    /**
     * @return the name of player 3
     */
    public String getPlayer_3() { return player_3.get(); }
}
