package spielraum;

import spiel.Spieler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class BestenlisteTest {

    private Bestenliste liste;
    private Spieler alice;
    private Spieler bob;

    @BeforeEach
    void setUp() {
        liste = new Bestenliste();
        alice = new Spieler("Alice",false);
        bob = new Spieler("Bob",false);
    }

    @Test
    void testSetAndGetEintrag() {
        liste.setEintrag(alice, 30);
        BestenlisteEintrag entry = liste.getEintrag(alice);

        assertNotNull(entry);
        assertEquals("Alice", entry.getName());
        assertEquals(30, entry.getPunkte());
    }

    @Test
    void testOverwriteEintrag() {
        liste.setEintrag(bob, 40);
        liste.setEintrag(bob, 70); // overwrite with new value

        BestenlisteEintrag entry = liste.getEintrag(bob);
        assertEquals(70, entry.getPunkte());
    }

    @Test
    void testGetEintragNotPresent() {
        assertNull(liste.getEintrag(new Spieler("Nonexistent",false)));
    }
}