package dto;

import java.io.Serializable;

/**
 * Represents a game room on the server side with up to three players.
 * This class is serializable for RMI transmission.
 */
public class GameroomDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String room_name;
    private String player_1;
    private String player_2;
    private String player_3;

    /**
     * Constructs a new GameroomDTO with the given name and players.
     *
     * @param room_name the name of the room
     * @param player_1 the first player (host)
     * @param player_2 the second player
     * @param player_3 the third player
     */
    public GameroomDTO(String room_name, String player_1, String player_2, String player_3) {
        this.room_name = room_name;
        this.player_1 = player_1;
        this.player_2 = player_2;
        this.player_3 = player_3;
    }

    /**
     * @return the name of the room
     */
    public String getRoom_name() { return room_name; }

    /**
     * @return the first player's name
     */
    public String getPlayer_1() { return player_1; }

    /**
     * @return the second player's name
     */
    public String getPlayer_2() { return player_2; }

    /**
     * @return the third player's name
     */
    public String getPlayer_3() { return player_3; }
}
