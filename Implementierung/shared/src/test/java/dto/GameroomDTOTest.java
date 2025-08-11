package dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class GameroomDTOTest {

    private GameroomDTO gameroom;

    @BeforeEach
    void setUp() {
        gameroom = new GameroomDTO("room1", "ilias", "yongjun", "lucas");
    }

    @Test
    void testGetRoomName() {
        assertEquals("room1", gameroom.getRoom_name(), "The room name should match");
    }

    @Test
    void testGetPlayer1() {
        assertEquals("ilias", gameroom.getPlayer_1(), "first players name should be correct");
    }

    @Test
    void testGetPlayer2() {
        assertEquals("yongjun", gameroom.getPlayer_2(), "second players name should be correct");
    }

    @Test
    void testGetPlayer3() {
        assertEquals("lucas", gameroom.getPlayer_3(), "third players name should be correct");
    }
}