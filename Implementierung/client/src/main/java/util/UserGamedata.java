package util;

/**
 * Class representing user game statistics such as username, games played, and wins.
 */
public class UserGamedata {

    private String username;
    private int games;
    private int wins;

    /**
     * Gets the username of the player.
     *
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the username of the player.
     *
     * @param username the username to set
     */
    public void setUsername(String username){
        this.username = username;
    }

    /**
     * Gets the number of wins.
     *
     * @return the number of wins
     */
    public int getWins(){
        return this.wins;
    }

    /**
     * Sets the number of wins.
     *
     * @param wins the number of wins to set
     */
    public void setWins(int wins){
        this.wins = wins;
    }

    /**
     * Gets the number of games played.
     *
     * @return the number of games played
     */
    public int getGames(){
        return this.games;
    }

    /**
     * Sets the number of games played.
     *
     * @param games the number of games played to set
     */
    public void setGames(int games){
        this.games = games;
    }
}
