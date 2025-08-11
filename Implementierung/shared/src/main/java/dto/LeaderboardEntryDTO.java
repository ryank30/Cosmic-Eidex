package dto;

import java.io.Serializable;

/**
 * This class is used to transfer leaderboard data (username and number of wins)
 */
public class LeaderboardEntryDTO implements Serializable {
    private String username;
    private int wins;

    /**
     * Constructs a new Leaderboarder with the specified username and number of wins.
     * @param username the name of the player
     * @param wins the number of games the player has won.
     */
    public LeaderboardEntryDTO(String username, int wins) {
        this.username = username;
        this.wins = wins;
    }

    /**
     * it returns the username of the player.
     * @return the player`s username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Returns the number of games the player has won.
     * @return the player´s win count.
     */
    public int getWins() {
        return wins;
    }
}