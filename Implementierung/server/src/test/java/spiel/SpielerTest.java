package spiel;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class SpielerTest {

    private Spieler spieler;

    @Mock
    private Karte karte1;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        spieler = new Spieler("ilias",false);
    }

    @Test
    void testGetName() {
        assertEquals("ilias", spieler.getName(), "name should be ilias, since its a very cool name");
    }

    @Test
    void testAddKarte() {
        spieler.addKarte(karte1);
        assertEquals(1, spieler.getHandkarten().size(), "after adding a card, the hand should contain one card");
        assertTrue(spieler.getHandkarten().contains(karte1), "the added card should be in the hand");
    }

    @Test
    void testRemoveKarte() {
        spieler.addKarte(karte1);
        spieler.removeKarte(karte1);

        assertEquals(0, spieler.getHandkarten().size(), "after removing the card, the hand should be empty");
        assertFalse(spieler.getHandkarten().contains(karte1), "the removed card should no longer be in the hand");
    }

    @Test
    void testAddPunkte() {
        spieler.addPunkte(10);
        assertEquals(10, spieler.getPunkte(), "points should increase by the value added");

        spieler.addPunkte(5);
        assertEquals(15, spieler.getPunkte(), "points should be added correctly");
    }

    @Test
    void testSetAmZug() {
        assertFalse(spieler.isAmZug(), "starting, the player should not be on turn.");

        spieler.setAmZug(true);
        assertTrue(spieler.isAmZug(), "after setting AmZug to true, it should be the players turn");

        spieler.setAmZug(false);
        assertFalse(spieler.isAmZug(), "after setting amZug to false, it should not be the players turn");
    }
}