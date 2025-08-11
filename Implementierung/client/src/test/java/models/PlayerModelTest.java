package models;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PlayerModelTest {
    @Test
    void testSetIsMyTurn() {
        PlayerModel player = new PlayerModel("TestPlayer");


        assertFalse(player.isMyTurnProperty().get(), "Initial isMyTurn should be false");


        player.setIsMyTurn(true);
        assertTrue(player.isMyTurnProperty().get(), "isMyTurn should be true after setting");

        // Set to false again
        player.setIsMyTurn(false);
        assertFalse(player.isMyTurnProperty().get(), "isMyTurn should be false after setting to false");
    }

    @Test
    void testScoreProperty() {
        PlayerModel player = new PlayerModel("TestPlayer");


        assertEquals(0, player.scoreProperty().get(), "Initial score should be 0");


        player.scoreProperty().set(42);
        assertEquals(42, player.getScore(), "Score should be 42 after setting via property");


        player.setScore(99);
        assertEquals(99, player.scoreProperty().get(), "Score should be 99 after using setScore");
    }
}

