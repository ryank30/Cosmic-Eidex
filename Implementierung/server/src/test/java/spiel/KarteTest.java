package spiel;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the Karte class.
 */
public class KarteTest {

    private Karte karte;

    @BeforeEach
    void setUp() {
        karte = new Karte(Farbe.HEARTS, Rang.KING);
    }

    @Test
    void testGetFarbe() {
        assertEquals(Farbe.HEARTS, karte.getFarbe(), "The suit should be HEARTS.");
    }

    @Test
    void testGetRang() {
        assertEquals(Rang.KING, karte.getRang(), "The rank should be KING.");
    }

    @Test
    void testGetWert_AllGameModes() {
        Karte trumpJack = new Karte(Farbe.HEARTS, Rang.JACK);
        Karte nonTrumpAce = new Karte(Farbe.STARS, Rang.ACE);
        Karte bottomSix = new Karte(Farbe.LIZARDS, Rang.SIX);


        assertEquals(20, trumpJack.getWert(Farbe.HEARTS, false, false), "Trump Jack should be 20");


        assertEquals(11, nonTrumpAce.getWert(Farbe.HEARTS, false, false), "Non-trump Ace should be 11");


        assertEquals(11, nonTrumpAce.getWert(null, true, false), "Obenabe Ace should be 11");


        assertEquals(11, bottomSix.getWert(null, false, true), "Undenufe Six should be 11");
    }
    @Test void testUndenufeScoring() {
        assertEquals(0, new Karte(Farbe.HEARTS, Rang.ACE).getWert(null, false, true));
        assertEquals(4, new Karte(Farbe.HEARTS, Rang.KING).getWert(null, false, true));
        assertEquals(3, new Karte(Farbe.HEARTS, Rang.QUEEN).getWert(null, false, true));
        assertEquals(2, new Karte(Farbe.HEARTS, Rang.JACK).getWert(null, false, true));
        assertEquals(10, new Karte(Farbe.HEARTS, Rang.TEN).getWert(null, false, true));
        assertEquals(0, new Karte(Farbe.HEARTS, Rang.NINE).getWert(null, false, true));
        assertEquals(8, new Karte(Farbe.HEARTS, Rang.EIGHT).getWert(null, false, true));
        assertEquals(0, new Karte(Farbe.HEARTS, Rang.SEVEN).getWert(null, false, true));
        assertEquals(11, new Karte(Farbe.HEARTS, Rang.SIX).getWert(null, false, true));
    }

    @Test void testTrumpScoring() {
        Farbe trump = Farbe.RAVENS;
        assertEquals(20, new Karte(trump, Rang.JACK).getWert(trump, false, false));
        assertEquals(14, new Karte(trump, Rang.NINE).getWert(trump, false, false));
        assertEquals(11, new Karte(trump, Rang.ACE).getWert(trump, false, false));
        assertEquals(4, new Karte(trump, Rang.KING).getWert(trump, false, false));
        assertEquals(3, new Karte(trump, Rang.QUEEN).getWert(trump, false, false));
        assertEquals(10, new Karte(trump, Rang.TEN).getWert(trump, false, false));
        assertEquals(0, new Karte(trump, Rang.EIGHT).getWert(trump, false, false));
        assertEquals(0, new Karte(trump, Rang.SEVEN).getWert(trump, false, false));
        assertEquals(0, new Karte(trump, Rang.SIX).getWert(trump, false, false));
    }

    @Test void testObenabeScoring() {
        assertEquals(11, new Karte(Farbe.LIZARDS, Rang.ACE).getWert(null, true, false));
        assertEquals(4, new Karte(Farbe.LIZARDS, Rang.KING).getWert(null, true, false));
        assertEquals(3, new Karte(Farbe.LIZARDS, Rang.QUEEN).getWert(null, true, false));
        assertEquals(2, new Karte(Farbe.LIZARDS, Rang.JACK).getWert(null, true, false));
        assertEquals(10, new Karte(Farbe.LIZARDS, Rang.TEN).getWert(null, true, false));
        assertEquals(0, new Karte(Farbe.LIZARDS, Rang.NINE).getWert(null, true, false));
        assertEquals(8, new Karte(Farbe.LIZARDS, Rang.EIGHT).getWert(null, true, false));
        assertEquals(0, new Karte(Farbe.LIZARDS, Rang.SEVEN).getWert(null, true, false));
        assertEquals(0, new Karte(Farbe.LIZARDS, Rang.SIX).getWert(null, true, false));
    }

    @Test void testNormalScoring() {
        assertEquals(11, new Karte(Farbe.STARS, Rang.ACE).getWert(null, false, false));
        assertEquals(4, new Karte(Farbe.STARS, Rang.KING).getWert(null, false, false));
        assertEquals(3, new Karte(Farbe.STARS, Rang.QUEEN).getWert(null, false, false));
        assertEquals(2, new Karte(Farbe.STARS, Rang.JACK).getWert(null, false, false));
        assertEquals(10, new Karte(Farbe.STARS, Rang.TEN).getWert(null, false, false));
        assertEquals(0, new Karte(Farbe.STARS, Rang.NINE).getWert(null, false, false));
        assertEquals(0, new Karte(Farbe.STARS, Rang.EIGHT).getWert(null, false, false));
        assertEquals(0, new Karte(Farbe.STARS, Rang.SEVEN).getWert(null, false, false));
        assertEquals(0, new Karte(Farbe.STARS, Rang.SIX).getWert(null, false, false));
    }
}
