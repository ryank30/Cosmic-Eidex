package models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class GameroomFXTest {

    private GameroomFX gameroomFX;

    @BeforeEach
    void setUp() {
        gameroomFX = new GameroomFX("Room1", "Player1", "Player2", "Player3");
    }

    @Test
    void testRoomNameProperty() {
        assertEquals("Room1",gameroomFX.room_nameProperty().get(), "should return correct property");
    }

    @Test
    void testPlayer1Property() {
        assertEquals("Player1", gameroomFX.player_1Property().get(), "should return correct property");
    }

    @Test
    void testPlayer2Property() {
        assertEquals("Player2", gameroomFX.player_2Property().get(), "should return correct property");
    }

    @Test
    void testPlayer3Property() {
        assertEquals("Player3", gameroomFX.player_3Property().get(), "should return correct property");
    }

    @Test
    void testGetRoomName() {
        assertEquals("Room1", gameroomFX.getRoom_name(), "should return correct value");
    }

    @Test
    void testGetPlayer1() {
        assertEquals("Player1", gameroomFX.getPlayer_1(), "should return correct value");
    }

    @Test
    void testGetPlayer2() {
        assertEquals("Player2", gameroomFX.getPlayer_2(), "should return correct value");
    }

    @Test
    void testGetPlayer3() {
        assertEquals("Player3", gameroomFX.getPlayer_3(), "should return correct value");
    }
}