package mapper;

import dto.StichDTO;
import org.junit.jupiter.api.Test;
import spiel.*;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class DTOConverterTest {
    @Test
    void testToDTO_normalCase() {
        Stich stich = new Stich();
        Spieler p1 = new Spieler("Alice",false);
        Spieler p2 = new Spieler("Bob", false);
        Karte k1 = new Karte(Farbe.HEARTS, Rang.TEN);
        Karte k2 = new Karte(Farbe.RAVENS,Rang.SIX);

        stich.addCard(p1, k1);
        stich.addCard(p2, k2);

        StichDTO dto = DTOConverter.toDTO(stich);
        Map<String, String> map = dto.getCardsInTrick();

        assertEquals(2, map.size());
        assertEquals("card-hearts-ten", map.get("Alice"));
        assertEquals("card-ravens-six", map.get("Bob"));
    }
    @Test
    void testToDTO_skipsNullPlayer() {
        Stich stich = new Stich();
        Karte k1 = new Karte(Farbe.HEARTS, Rang.TEN);
        stich.addCard(null, k1);

        StichDTO dto = DTOConverter.toDTO(stich);
        assertFalse(dto.getCardsInTrick().isEmpty());
    }

    @Test
    void testToDTO_skipsNullCard() {
        Stich stich = new Stich();
        Spieler alice = new Spieler("Alice",false);
        stich.addCard(alice, null);

        StichDTO dto = DTOConverter.toDTO(stich);
        assertFalse(dto.getCardsInTrick().isEmpty());
    }

    @Test
    void testToDTO_emptyStich() {
        Stich stich = new Stich();
        StichDTO dto = DTOConverter.toDTO(stich);
        assertTrue(dto.getCardsInTrick().isEmpty());
    }
}
