package spielraum;

import spiel.Spieler;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;


class BestenlisteEintragTest {

    @Test
    void testEintragCreation() {
        Spieler s = new Spieler("Alice", false);
        BestenlisteEintrag eintrag = new BestenlisteEintrag(s, 50);

        assertEquals(s, eintrag.getSpieler());
        assertEquals("Alice", eintrag.getName());
        assertEquals(50, eintrag.getPunkte());
    }

    @Test
    void testSetPunkte() {
        Spieler s = new Spieler("Bob", false);
        BestenlisteEintrag eintrag = new BestenlisteEintrag(s, 10);

        eintrag.setPunkte(80);
        assertEquals(80, eintrag.getPunkte());
    }

    @Test
    void testToStringFormat() {
        Spieler s = new Spieler("Charlie", false);
        BestenlisteEintrag eintrag = new BestenlisteEintrag(s, 100);

        assertEquals("Charlie: 100 Punkte", eintrag.toString());
    }
}