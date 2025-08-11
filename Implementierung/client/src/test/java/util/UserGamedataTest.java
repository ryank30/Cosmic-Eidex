package util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class UserGamedataTest {

    private UserGamedata userGamedata;

    @BeforeEach
    void setUp() {
        userGamedata = new UserGamedata();
    }

    @Test
    void testUsernameGetSet() {
        userGamedata.setUsername("ilias");

        assertEquals("ilias", userGamedata.getUsername(), "username should be correct");
    }

    @Test
    void testWinsGetSet() {
        userGamedata.setWins(5);

        assertEquals(5, userGamedata.getWins(), "wins should match");
    }

    @Test
    void testGamesGetSet() {
        userGamedata.setGames(10);

        assertEquals(10, userGamedata.getGames(), "games should match");
    }
}