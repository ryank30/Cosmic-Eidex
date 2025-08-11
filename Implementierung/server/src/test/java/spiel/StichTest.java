package spiel;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class StichTest {

    private Stich stich;

    @Mock
    private Spieler spieler1;

    @Mock
    private Spieler spieler2;

    @Mock
    private Karte karte1;

    @Mock
    private Karte karte2;

    @BeforeEach
    void setUp() {
        stich = new Stich();
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAddCard() {
        stich.addCard(spieler1, karte1);

        assertEquals(1, stich.getCardCount(), "The number of cards should be 1 after adding one card");
        assertEquals(karte1, stich.getCardByPlayer(spieler1), "The card retrieved from the player should match the added card");
    }

    @Test
    void testGetAllCards() {
        stich.addCard(spieler1, karte1);
        stich.addCard(spieler2, karte2);

        var allCards = stich.getAllCards();
        assertEquals(2, allCards.size(), "There should be 2 entries in the trick");

        boolean containsSpieler1 = false, containsSpieler2 = false;
        for (var entry : allCards) {
            if (entry.getKey().equals(spieler1) && entry.getValue().equals(karte1)) {
                containsSpieler1 = true;
            }
            if (entry.getKey().equals(spieler2) && entry.getValue().equals(karte2)) {
                containsSpieler2 = true;
            }
        }
        assertTrue(containsSpieler1, "The trick should contain an entry for spieler1 with karte1");
        assertTrue(containsSpieler2, "The trick should contain an entry for spieler2 with karte2");
    }

    @Test
    void testGetCardCount() {
        assertEquals(0, stich.getCardCount(), "The trick should initially be empty");

        stich.addCard(spieler1, karte1);
        assertEquals(1, stich.getCardCount(), "The trick should have 1 card after adding one");

        stich.addCard(spieler2, karte2);
        assertEquals(2, stich.getCardCount(), "The trick should have 2 cards after adding two");
    }

    @Test
    void testGetFirstCard() {
        assertNull(stich.getFirstCard(), "The first card should be null when the trick is empty");

        stich.addCard(spieler1, karte1);
        assertEquals(karte1, stich.getFirstCard(), "The first card should match the first card added");
    }

    @Test
    void testGetCardByPlayer() {
        stich.addCard(spieler1, karte1);
        stich.addCard(spieler2, karte2);

        assertEquals(karte1, stich.getCardByPlayer(spieler1), "The card for spieler1 should match karte1");
        assertEquals(karte2, stich.getCardByPlayer(spieler2), "The card for spieler2 should match karte2");
    }

    @Test
    void testClear() {
        stich.addCard(spieler1, karte1);
        stich.addCard(spieler2, karte2);

        stich.clear();
        assertEquals(0, stich.getCardCount(), "The number of cards should be 0 after clearing the trick");
        assertNull(stich.getFirstCard(), "The first card should be null after clearing the trick");
    }


    @Test
    void testGetWinner_TrumpMode() {
        Karte jackHearts = new Karte(Farbe.HEARTS, Rang.JACK);  // strong trump
        Karte aceStars = new Karte(Farbe.STARS, Rang.ACE);      // strong non-trump

        stich.addCard(spieler1, aceStars);
        stich.addCard(spieler2, jackHearts);

        Spieler winner = stich.getWinner(Farbe.HEARTS, false, false);
        assertEquals(spieler2, winner, "In trump game, trump card should win even if played later");
    }

    @Test
    void testGetWinner_ObenabeMode() {
        Karte aceStars = new Karte(Farbe.STARS, Rang.ACE);  // high card
        Karte kingStars = new Karte(Farbe.STARS, Rang.KING);  // lower

        stich.addCard(spieler1, kingStars);
        stich.addCard(spieler2, aceStars);

        Spieler winner = stich.getWinner(null, false, true); // Obenabe
        assertEquals(spieler2, winner, "In Obenabe, higher card should win");
    }

    @Test
    void testGetWinner_UndenufeMode() {
        Karte sixStars = new Karte(Farbe.STARS, Rang.SIX);  // best card in Undenufe
        Karte kingStars = new Karte(Farbe.STARS, Rang.KING);  // weaker

        stich.addCard(spieler1, kingStars);
        stich.addCard(spieler2, sixStars);

        Spieler winner = stich.getWinner(null, true, false); // Undenufe
        assertEquals(spieler2, winner, "In Undenufe, lower card should win");
    }

    @Test
    void testGetTotalPoints_Trump() {
        stich.addCard(spieler1, new Karte(Farbe.HEARTS, Rang.JACK)); // 20
        stich.addCard(spieler2, new Karte(Farbe.HEARTS, Rang.NINE)); // 14

        int total = stich.getTotalPoints("HEARTS", Farbe.HEARTS);
        assertEquals(34, total, "Trump game: total points should be 34");
    }

    @Test
    void testGetTotalPoints_Obenabe() {
        stich.addCard(spieler1, new Karte(Farbe.STARS, Rang.ACE));  // 11
        stich.addCard(spieler2, new Karte(Farbe.RAVENS, Rang.EIGHT)); // 8

        int total = stich.getTotalPoints("OBENABE", null);
        assertEquals(19, total, "Obenabe game: total points should be 19");
    }

    @Test
    void testGetTotalPoints_Undenufe() {
        stich.addCard(spieler1, new Karte(Farbe.LIZARDS, Rang.SIX));  // 11
        stich.addCard(spieler2, new Karte(Farbe.LIZARDS, Rang.EIGHT)); // 8

        int total = stich.getTotalPoints("UNDENUFE", null);
        assertEquals(19, total, "Undenufe game: total points should be 19");
    }
    @Test
    void testGetCardStrengthUndenufe() {
        Stich stich = new Stich();

        assertEquals(8, stich.getCardStrength(new Karte(Farbe.HEARTS, Rang.SIX), false, true));
        assertEquals(7, stich.getCardStrength(new Karte(Farbe.HEARTS, Rang.SEVEN), false, true));
        assertEquals(6, stich.getCardStrength(new Karte(Farbe.HEARTS, Rang.EIGHT), false, true));
        assertEquals(5, stich.getCardStrength(new Karte(Farbe.HEARTS, Rang.NINE), false, true));
        assertEquals(4, stich.getCardStrength(new Karte(Farbe.HEARTS, Rang.TEN), false, true));
        assertEquals(3, stich.getCardStrength(new Karte(Farbe.HEARTS, Rang.JACK), false, true));
        assertEquals(2, stich.getCardStrength(new Karte(Farbe.HEARTS, Rang.QUEEN), false, true));
        assertEquals(1, stich.getCardStrength(new Karte(Farbe.HEARTS, Rang.KING), false, true));
        assertEquals(0, stich.getCardStrength(new Karte(Farbe.HEARTS, Rang.ACE), false, true));
    }

        @Test
        void testGetCardStrengthTrump() {
            Stich stich = new Stich();

            assertEquals(20, stich.getCardStrength(new Karte(Farbe.RAVENS, Rang.JACK), true, false));
            assertEquals(19, stich.getCardStrength(new Karte(Farbe.RAVENS, Rang.NINE), true, false));
            assertEquals(18, stich.getCardStrength(new Karte(Farbe.RAVENS, Rang.ACE), true, false));
            assertEquals(17, stich.getCardStrength(new Karte(Farbe.RAVENS, Rang.KING), true, false));
            assertEquals(16, stich.getCardStrength(new Karte(Farbe.RAVENS, Rang.QUEEN), true, false));
            assertEquals(15, stich.getCardStrength(new Karte(Farbe.RAVENS, Rang.TEN), true, false));
            assertEquals(14, stich.getCardStrength(new Karte(Farbe.RAVENS, Rang.EIGHT), true, false));
            assertEquals(13, stich.getCardStrength(new Karte(Farbe.RAVENS, Rang.SEVEN), true, false));
            assertEquals(12, stich.getCardStrength(new Karte(Farbe.RAVENS, Rang.SIX), true, false));
        }

    @Test
    void testGetCardStrengthObenabe() {
        Stich stich = new Stich();

        assertEquals(18, stich.getCardStrength(new Karte(Farbe.LIZARDS, Rang.ACE), false, false));
        assertEquals(17, stich.getCardStrength(new Karte(Farbe.LIZARDS, Rang.KING), false, false));
        assertEquals(16, stich.getCardStrength(new Karte(Farbe.LIZARDS, Rang.QUEEN), false, false));
        assertEquals(15, stich.getCardStrength(new Karte(Farbe.LIZARDS, Rang.JACK), false, false));
        assertEquals(14, stich.getCardStrength(new Karte(Farbe.LIZARDS, Rang.TEN), false, false));
        assertEquals(13, stich.getCardStrength(new Karte(Farbe.LIZARDS, Rang.NINE), false, false));
        assertEquals(12, stich.getCardStrength(new Karte(Farbe.LIZARDS, Rang.EIGHT), false, false));
        assertEquals(11, stich.getCardStrength(new Karte(Farbe.LIZARDS, Rang.SEVEN), false, false));
        assertEquals(10, stich.getCardStrength(new Karte(Farbe.LIZARDS, Rang.SIX), false, false));
    }

}
