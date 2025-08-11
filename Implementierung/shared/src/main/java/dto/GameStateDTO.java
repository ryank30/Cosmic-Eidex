package dto;

import java.io.Serializable;
import java.util.List;

/**
 *This class is used to encapsulate and transmit information about the current game state,
 * including player data, which player's turn it is, and whether the game is currently active.

 */
public class GameStateDTO implements Serializable {
    private final List<PlayerDTO> players;
    private final int currentPlayerIndex;
    private final boolean isactive;

    /**
     * Constructs a new GameState with the given player list, current player index, and activity flag.
     * @param players the list of players in the game.
     * @param currentPlayerIndex the index of the current player in the list.
     * @param isactive whether the game is currently active
     */
    public GameStateDTO(List<PlayerDTO> players, int currentPlayerIndex,  boolean isactive) {
        this.players = players;
        this.currentPlayerIndex = currentPlayerIndex;
        this.isactive = isactive;
    }
    /**
     * The list of players participating in the game.
     */
    public List<PlayerDTO> getPlayers() {
        return players;
    }

    /**
     * it returns the index of the currernt player.
     * @return the current player index.
     */
    public int getCurrentPlayerIndex() {
        return currentPlayerIndex;
    }

    /**
     * Indicates whether the game is currently active.
     * @return if the game is active ; and false otherwise.
     */
    public boolean isActive() {
        return isactive;
    }
}