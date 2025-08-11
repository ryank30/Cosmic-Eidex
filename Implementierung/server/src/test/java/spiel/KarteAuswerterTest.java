package spiel;

import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class KarteAuswerterTest {

    @Test
    public void testObenabePointValues() {
        assertEquals(11, KarteAuswerter.evalCardPointValue(new Karte(Farbe.HEARTS, Rang.ACE), "OBENABE", Farbe.LIZARDS));
        assertEquals(10, KarteAuswerter.evalCardPointValue(new Karte(Farbe.HEARTS, Rang.TEN), "OBENABE", Farbe.LIZARDS));
        assertEquals(4, KarteAuswerter.evalCardPointValue(new Karte(Farbe.HEARTS, Rang.KING), "OBENABE", Farbe.LIZARDS));
        assertEquals(3, KarteAuswerter.evalCardPointValue(new Karte(Farbe.HEARTS, Rang.QUEEN), "OBENABE", Farbe.LIZARDS));
        assertEquals(2, KarteAuswerter.evalCardPointValue(new Karte(Farbe.HEARTS, Rang.JACK), "OBENABE", Farbe.LIZARDS));
        assertEquals(0, KarteAuswerter.evalCardPointValue(new Karte(Farbe.HEARTS, Rang.NINE), "OBENABE", Farbe.LIZARDS));
    }

    @Test
    public void testUndenufePointValues() {
        assertEquals(11, KarteAuswerter.evalCardPointValue(new Karte(Farbe.RAVENS, Rang.SIX), "UNDENUFE", Farbe.STARS));
        assertEquals(0, KarteAuswerter.evalCardPointValue(new Karte(Farbe.RAVENS, Rang.ACE), "UNDENUFE", Farbe.STARS));
        assertEquals(8, KarteAuswerter.evalCardPointValue(new Karte(Farbe.RAVENS, Rang.EIGHT), "UNDENUFE", Farbe.STARS));
        assertEquals(10, KarteAuswerter.evalCardPointValue(new Karte(Farbe.RAVENS, Rang.TEN), "UNDENUFE", Farbe.STARS));
    }

    @Test
    public void testTrumpPointValues() {
        // Card is trump suit
        assertEquals(20, KarteAuswerter.evalCardPointValue(new Karte(Farbe.LIZARDS, Rang.JACK), "TRUMP", Farbe.LIZARDS));
        assertEquals(14, KarteAuswerter.evalCardPointValue(new Karte(Farbe.LIZARDS, Rang.NINE), "TRUMP", Farbe.LIZARDS));
        assertEquals(0, KarteAuswerter.evalCardPointValue(new Karte(Farbe.LIZARDS, Rang.EIGHT), "TRUMP", Farbe.LIZARDS));
    }

    @Test
    public void testNormalPointValuesNonTrump() {
        // Card is NOT trump suit
        assertEquals(10, KarteAuswerter.evalCardPointValue(new Karte(Farbe.HEARTS, Rang.TEN), "TRUMP", Farbe.LIZARDS));
        assertEquals(3, KarteAuswerter.evalCardPointValue(new Karte(Farbe.HEARTS, Rang.QUEEN), "TRUMP", Farbe.LIZARDS));
        assertEquals(0, KarteAuswerter.evalCardPointValue(new Karte(Farbe.HEARTS, Rang.EIGHT), "TRUMP", Farbe.LIZARDS));
    }

    @Test
    public void testDefaultCaseWithUnknownMode() {
        // Simulates a wrong mode string; default case should trigger
        assertEquals(11, KarteAuswerter.evalCardPointValue(new Karte(Farbe.LIZARDS, Rang.ACE), "UNKNOWNMODE", Farbe.STARS));
    }

    @Test
    public void testPairEqualityAndHashing() {
        KarteAuswerter.Pair<Rang, Farbe> pair1 = new KarteAuswerter.Pair<>(Rang.ACE, Farbe.STARS);
        KarteAuswerter.Pair<Rang, Farbe> pair2 = new KarteAuswerter.Pair<>(Rang.ACE, Farbe.STARS);
        KarteAuswerter.Pair<Rang, Farbe> pair3 = new KarteAuswerter.Pair<>(Rang.KING, Farbe.HEARTS);

        assertEquals(pair1, pair2);
        assertNotEquals(pair1, pair3);

        Set<KarteAuswerter.Pair<Rang, Farbe>> set = new HashSet<>();
        set.add(pair1);
        assertTrue(set.contains(pair2));
        assertFalse(set.contains(pair3));
    }

}