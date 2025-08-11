package spiel;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class DeckTest {

    private Deck deck;

    @BeforeEach
    void setUp() {
        deck = new Deck();
    }

    @Test
    void testGenerateFullDeck() {
        List<List<Karte>> hands = deck.deal();
        int totalCards = hands.stream().mapToInt(List::size).sum();
        assertEquals(36, totalCards, "Deck should have 36 cards after generateFullDeck");
    }

    @Test
    void testShuffle() {
        Deck d1 = new Deck();
        Deck d2 = new Deck();

        List<Karte> h1 = d1.deal().get(0);
        List<Karte> h2 = d2.deal().get(0);

        assertNotEquals(h1.toString(), h2.toString(), "Shuffled decks should be in different order");
    }

    @Test
    void testDeal() {
        List<List<Karte>> hands = deck.deal();
        assertEquals(3, hands.size(), "There should be 3 hands");
        for (List<Karte> hand : hands) {
            assertEquals(12, hand.size(), "Each hand should have 12 cards");
        }
    }

    @Test
    void testRevealLastCard() {
        Karte lastCard = deck.revealLastCard();
        assertNotNull(lastCard, "Last card should not be null");
    }

    @Test
    void testGetGameMode() {
        Karte ace = new Karte(Farbe.STARS, Rang.ACE);
        Karte six = new Karte(Farbe.RAVENS, Rang.SIX);
        Karte king = new Karte(Farbe.LIZARDS, Rang.KING);

        assertEquals("OBENABE", Deck.getGameMode(ace), "Should return OBENABE for ACE");
        assertEquals("UNDENUFE", Deck.getGameMode(six), "Should return UNDENUFE for SIX");
        assertEquals("LIZARDS", Deck.getGameMode(king), "Should return trump suit name for other cards");
    }
}
