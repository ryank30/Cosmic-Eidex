package spielraum;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class SpielraumTest {

    private Spielraum spielraum;

    @BeforeEach
    void setUp() {
        spielraum = new Spielraum("testroom", "password123", "host");
    }

    @Test
    void testConstructor() {
        assertEquals("testroom", spielraum.getName());
        assertEquals("password123", spielraum.getPasswort());
        assertEquals("host", spielraum.getHostSpieler());


        assertTrue(spielraum.getTeilnehmer().contains("host"));


        assertTrue(spielraum.getBots().isEmpty());
        assertTrue(spielraum.getChatverlauf().isEmpty());
    }

    @Test
    void testAddAndRemoveParticipant() {
        // Adding a participant should work
        spielraum.addTeilnehmer("Player1");
        assertTrue(spielraum.isTeilnehmer("Player1"));

        // Removing a participant should also work
        spielraum.removeTeilnehmer("Player1");
        assertFalse(spielraum.isTeilnehmer("Player1"));
    }

    @Test
    void testIsParticipant() {

        assertTrue(spielraum.isTeilnehmer("host"));

        assertFalse(spielraum.isTeilnehmer("fakeplayer"));
    }

    @Test
    void testRoomFull() {

        assertFalse(spielraum.isVoll());

        spielraum.addTeilnehmer("Player1");
        spielraum.addTeilnehmer("Player2");

        assertTrue(spielraum.isVoll());
    }

    @Test
    void testAddAndRemoveBot() {

        spielraum.addBot("Bot1");
        assertTrue(spielraum.getBots().contains("Bot1"));
        assertTrue(spielraum.getTeilnehmer().contains("Bot1"));


        spielraum.removeBot("Bot1");
        assertFalse(spielraum.getBots().contains("Bot1"));
        assertFalse(spielraum.getTeilnehmer().contains("Bot1"));
    }

    @Test
    void testChatMessages() {

        assertTrue(spielraum.getChatverlauf().isEmpty());
        assertEquals("", spielraum.getChatverlaufAsString());


        spielraum.addChatNachricht("host", "hi");
        spielraum.addChatNachricht("Player1", "you're in the matrix neo");


        String expectedChat = "host: hi\nPlayer1: you're in the matrix neo";
        assertEquals(expectedChat, spielraum.getChatverlaufAsString());
    }

}