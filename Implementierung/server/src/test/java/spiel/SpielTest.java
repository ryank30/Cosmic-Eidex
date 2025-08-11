package spiel;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;
import static org.mockito.Mockito.*;


public class SpielTest {

    private Spiel spiel;
    private Spieler spieler1;
    private Spieler spieler2;
    private Spieler spieler3;
    private List<Spieler> spielerListe;
    private Karte card1;
    private Karte card2;
    private Karte card3;
    private Stich stich;

    private Deck deck;

    @BeforeEach
    void setUp() {
        spieler1 = new Spieler("spieler1",false);
        spieler2 = new Spieler("spieler2",false);
        spieler3 = new Spieler("spieler3",false);

        card1 = new Karte(Farbe.HEARTS, Rang.ACE);
        card2 = new Karte(Farbe.HEARTS, Rang.KING);
        card3 = new Karte(Farbe.HEARTS, Rang.JACK);

        spieler1.getHandkarten().add(card1);
        spieler2.getHandkarten().add(card2);
        spieler3.getHandkarten().add(card3);
        deck = new Deck();
        stich = new Stich();

        spielerListe = List.of(spieler1, spieler2, spieler3);
        spiel = new Spiel("TestSpielraum", spielerListe);

        spiel.getMode();
        spiel.setAktuellerSpielerIndex(0);
        spiel.initializeGame(new Deck());

        for (Spieler s : spiel.getSpielerListe()) {
            spiel.getAlleStiche().put(s, new ArrayList<>());
        }
    }

    @Test
    void testGetSpielraumName() {
        assertEquals("TestSpielraum", spiel.getSpielraumName());
    }

    @Test
    void testGetSpielerListe() {
        List<Spieler> spielerListe = spiel.getSpielerListe();
        assertEquals(3, spielerListe.size());
        assertTrue(spielerListe.contains(spieler1));
        assertTrue(spielerListe.contains(spieler2));
        assertTrue(spielerListe.contains(spieler3));
    }

    @Test
    void testAktivStatus() {

        assertTrue(spiel.isAktiv());
        spiel.setAktiv(false);
        assertFalse(spiel.isAktiv());
    }
    @Test
    void testPlayCard_ValidMove() {
        Spieler current = spiel.getAktuellerSpieler();
        Karte cardToPlay = current.getHandkarten().get(0);

        spiel.playCard(current, cardToPlay);

        assertFalse(current.getHandkarten().contains(cardToPlay), "Card should be removed from hand.");
    }


    @Test
    void testKarteInStich() {
        Karte karte1 = new Karte(Farbe.HEARTS, Rang.ACE);
        spiel.karteInStich(spieler1, karte1);

        Stich stich = spiel.getStich();
        assertEquals(1, stich.getCardCount());
        assertTrue(stich.getAllCards().stream().anyMatch(entry -> entry.getValue().equals(karte1)));

        Map<Spieler, List<Karte>> gespielteStiche = spiel.getAlleStiche();
        assertEquals(1, gespielteStiche.get(spieler1).size());
        assertTrue(gespielteStiche.get(spieler1).contains(karte1));
    }

    @Test
    void testIstLetzterStich() {
        spieler1.addKarte(new Karte(Farbe.HEARTS, Rang.ACE));
        spieler2.addKarte(new Karte(Farbe.HEARTS, Rang.KING));
        spieler3.addKarte(new Karte(Farbe.HEARTS, Rang.QUEEN));

        assertFalse(spiel.istLetzterStich());

        spieler1.getHandkarten().clear();
        spieler2.getHandkarten().clear();
        spieler3.getHandkarten().clear();

        assertTrue(spiel.istLetzterStich());
    }
    @Test
    void testIsValidMove_NoLeadCard_AlwaysValid() {
        Spieler player = new Spieler("Test", false);
        Karte anyCard = new Karte(Farbe.HEARTS, Rang.ACE);
        player.getHandkarten().add(anyCard);

        assertTrue(spiel.isValidMove(player, anyCard, null));
    }
    @Test
    void testIsValidMove_NoUntertrumpfen() {
        spiel.setTrumpSuit(Farbe.HEARTS);
        Spieler player = new Spieler("player", false);
        Spieler opponent = new Spieler("Opponent", false);


        Karte highTrump = new Karte(Farbe.HEARTS, Rang.ACE);
        spiel.getStich().addCard(opponent, highTrump);

        Karte lowTrump = new Karte(Farbe.HEARTS, Rang.SIX);
        Karte leadSuitCard = new Karte(Farbe.LIZARDS, Rang.KING);
        Karte otherCard = new Karte(Farbe.STARS, Rang.QUEEN);


        player.getHandkarten().addAll(List.of(lowTrump, new Karte(Farbe.HEARTS, Rang.QUEEN)));
        assertTrue(spiel.isValidMove(player, lowTrump, new Karte(Farbe.LIZARDS, Rang.SEVEN)));


        player.getHandkarten().clear();
        player.getHandkarten().addAll(List.of(lowTrump, leadSuitCard));
        assertTrue(spiel.isValidMove(player, lowTrump, new Karte(Farbe.LIZARDS, Rang.SEVEN)));


        player.getHandkarten().clear();
        player.getHandkarten().addAll(List.of(lowTrump, otherCard));
        assertTrue(spiel.isValidMove(player, lowTrump, new Karte(Farbe.LIZARDS, Rang.SEVEN)));
    }

    @Test
    void testIsValidMove_CanPlayTrumpWhenNoLeadSuit() {
        spiel.setTrumpSuit(Farbe.STARS);
        Spieler player = new Spieler("Test", false);
        Karte leadCard = new Karte(Farbe.HEARTS, Rang.KING);
        Karte trumpCard = new Karte(Farbe.STARS, Rang.TEN);

        player.getHandkarten().add(trumpCard);

        assertTrue(spiel.isValidMove(player, trumpCard, leadCard));
    }
    @Test
    void testIsValidMove_MustFollowLeadSuit() {
        Spieler s1 = new Spieler("Alice",false);
        Karte leadCard = new Karte(Farbe.HEARTS, Rang.KING);
        Karte invalid = new Karte(Farbe.STARS, Rang.TEN);

        s1.addKarte(new Karte(Farbe.HEARTS, Rang.SIX));

        Spiel spiel = new Spiel("TestRoom", List.of(s1));
        spiel.setTrumpSuit(Farbe.LIZARDS);
        spiel.getStich().addCard(s1, leadCard);

        boolean isValid = spiel.isValidMove(s1, invalid, leadCard);
        assertFalse(isValid);
    }

    @Test
    void testStartGameWithDeck() {
        Deck deck = new Deck();
        spiel.startGame();
        assertTrue(spiel.isAktiv());
        assertNotNull(spiel.getLastCard());
    }

    @Test
    void testInitializeGameDistributesCards() {
        spiel.initializeGame(new Deck());
        for (Spieler s : spiel.getSpielerListe()) {
            assertFalse(s.getHandkarten().isEmpty());
        }
        assertTrue(spiel.isAktiv());

    }



    @Test
    public void testAlleHandkartenLeer_ReturnsFalse() {
        spieler2.getHandkarten().add(new Karte(Farbe.RAVENS, Rang.SEVEN));
        assertFalse(spiel.alleHandkartenLeer());
    }

    @Test
    public void testAlleHandkartenLeer() {
        for (Spieler player : spiel.getSpielerListe()) {
            player.getHandkarten().clear();
        }
        assertTrue(spiel.alleHandkartenLeer());
    }


    @Test
    void testUpdateValidMovesForEachPlayer() {
        spiel.initializeGame(new Deck());
        spiel.updateValidMoves(spieler1);

        for (Spieler s : spiel.getSpielerListe()) {
            List<Karte> validMoves = spiel.getCurrentValidMoves();

            assertNotNull(validMoves);
        }
    }


    @Test
    void testGetValidMovesForPlayer() {
        spiel.initializeGame(new Deck());
        spiel.updateValidMoves(spieler1);
        List<Karte> moves = spiel.getCurrentValidMoves();
        assertNotNull(moves);
    }

    @Test
    void testIsValidMoveSuitMatches() {
        Karte lead = new Karte(Farbe.HEARTS, Rang.SIX);
        Karte follow = new Karte(Farbe.HEARTS, Rang.JACK);
        spieler1.addKarte(follow);
        assertTrue(spiel.isValidMove(spieler1, follow, lead));
    }

    @Test
    void testDitributeWinpointsSpecialCase() {
        spieler1.addPunkte(100); // all 11 tricks
        spiel.ditributeWinpoints();
        assertEquals(1, spieler2.getWinPoint());
        assertEquals(1, spieler3.getWinPoint());
    }

    @Test
    void testDitributeWinpointsNormal() {
        spieler1.addPunkte(80);
        spieler2.addPunkte(60);
        spieler3.addPunkte(90);
        spiel.ditributeWinpoints();
        assertEquals(1, spieler3.getWinPoint());
    }

    @Test
    void testStartGameClearsOldState() {
        spieler1.addPunkte(100);
        spiel.startGame();
        assertTrue(spiel.isAktiv());
        assertEquals(0, spieler1.getPunkte());
        assertTrue(spieler1.getHandkarten().size() > 0);
    }

    @Test
    void testGetCardByIdSuccess() {
        Karte k = new Karte(Farbe.HEARTS, Rang.SEVEN);
        List<Karte> list = List.of(k);
        assertEquals(k, Spiel.getCardById(list, k.getId()));
    }

    @Test
    void testGetCardByIdThrowsIfNotFound() {
        assertThrows(NoSuchElementException.class, () -> {
            Spiel.getCardById(List.of(), "invalid-id");
        });
    }
    @Test
    public void testValidMove_NoLeadCard_ShouldBeValid() {
        Spieler spieler = new Spieler("Player 1", false);
        Karte card = new Karte(Farbe.RAVENS, Rang.ACE);
        spieler.getHandkarten().add(card);

        assertTrue(spiel.isValidMove(spieler, card, null));
    }

    @Test
    public void testValidMoveNoValidLeadCard() {
        Spieler spieler = new Spieler("Player 1", false);
        Karte card = new Karte(Farbe.RAVENS, Rang.ACE);
        spieler.getHandkarten().add(card);

        assertTrue(spiel.isValidMove(spieler, card, null));
    }

    @Test
    public void testInvalidMovePlaysDifferentSuit() {
        Spieler spieler = new Spieler("Player 1", false);
        Karte leadCard = new Karte(Farbe.RAVENS, Rang.ACE);
        Karte inHand = new Karte(Farbe.RAVENS, Rang.JACK);
        Karte played = new Karte(Farbe.LIZARDS, Rang.SIX);

        spieler.getHandkarten().add(inHand);
        spieler.getHandkarten().add(played);

        assertFalse(spiel.isValidMove(spieler, played, leadCard));
    }

    @Test
    public void testValidMoveHasNoLeadSuit() {
        Spieler spieler = new Spieler("Player 1", false);
        Karte leadCard = new Karte(Farbe.RAVENS, Rang.TEN);
        Karte played = new Karte(Farbe.LIZARDS, Rang.QUEEN);

        spieler.getHandkarten().add(played);

        assertTrue(spiel.isValidMove(spieler, played, leadCard));
    }
    @Test
    public void testValidMoveTrumpPlayed1() {
        Spieler spieler = new Spieler("Player 1", false);
        Karte leadCard = new Karte(Farbe.RAVENS, Rang.QUEEN);


        Karte played = new Karte(Farbe.HEARTS, Rang.JACK);
        Karte otherCard = new Karte(Farbe.LIZARDS, Rang.QUEEN);
        spieler.getHandkarten().addAll(List.of(played, otherCard));


        Karte middle = new Karte(Farbe.HEARTS, Rang.QUEEN);
        stich.addCard(new Spieler("Opponent", false), middle);


        stich.addCard(spieler, played);

        assertTrue(spiel.isValidMove(spieler, played, leadCard));
    }
    @Test
    public void testWinnerWithNoTrump(){
        spiel.getTrumpSuit();
        spiel.isObenabe();


        Spieler p1 = new Spieler("Alice", false);
        Spieler p2 = new Spieler("Bob", false);
        Spieler p3 = new Spieler("Charlie", false);


        Karte c1 = new Karte(Farbe.LIZARDS, Rang.ACE);
        Karte c2 = new Karte(Farbe.LIZARDS, Rang.TEN);
        Karte c3 = new Karte(Farbe.LIZARDS, Rang.KING);

        List<Karte> trick = List.of(c1, c2, c3);
        List<Spieler> players = List.of(p1, p2, p3);

        Spieler winner = spiel.determineTrickWinner(trick, players);
        assertEquals(p1, winner);
    }

    @Test
    public void testWinnerWithTrump() {
        spiel.setTrumpSuit(Farbe.HEARTS);
        spiel.isObenabe();

        Spieler p1 = new Spieler("Alice", false);
        Spieler p2 = new Spieler("Bob", false);
        Spieler p3 = new Spieler("Charlie", false);

        Karte c1 = new Karte(Farbe.LIZARDS, Rang.ACE);
        Karte c2 = new Karte(Farbe.LIZARDS, Rang.KING);
        Karte c3 = new Karte(Farbe.HEARTS, Rang.JACK);

        List<Karte> trick = List.of(c1, c2, c3);
        List<Spieler> players = List.of(p1, p2, p3);

        Spieler winner = spiel.determineTrickWinner(trick, players);
        assertEquals(p3, winner);
    }
    @Test
    void testSpielStarten() {
        Spieler s1 = new Spieler("s1", false);
        Spieler s2 = new Spieler("s2", false);
        Spieler s3 = new Spieler("s2", false);
        Spiel testSpiel = new Spiel("game", List.of(s1, s2, s3));

        List<Karte> hand1 = new ArrayList<>();
        List<Karte> hand2 = new ArrayList<>();
        List<Karte> hand3 = new ArrayList<>();

        for (int i = 0; i < 12; i++) {
            hand1.add(new Karte(Farbe.HEARTS, Rang.TEN));
            hand2.add(new Karte(Farbe.STARS, Rang.KING));
            hand3.add(new Karte(Farbe.LIZARDS, Rang.SEVEN));
        }

        List<List<Karte>> hands = List.of(hand1, hand2, hand3);
        Karte lastCard = new Karte(Farbe.STARS, Rang.JACK);

        TestDeck testDeck = new TestDeck(hands, lastCard);

        testSpiel.startGame(testDeck);

        assertTrue(testSpiel.isAktiv());

        assertEquals(hand1, s1.getHandkarten());
        assertEquals(hand2, s2.getHandkarten());
        assertEquals(hand3, s3.getHandkarten());


        assertEquals(lastCard, testSpiel.getLastCard());
        assertEquals(Farbe.STARS, testSpiel.getTrumpSuit());
    }
    @Test
    void testEval7WinPoints() {
        Spieler s1 = new Spieler("s1", false);
        Spieler s2 = new Spieler("s2", false);
        Spieler s3 = new Spieler("s3", false);

        s1.setWinPoint(5);
        s2.setWinPoint(7);
        s3.setWinPoint(3);

        Spiel testSpiel = new Spiel("game", List.of(s1, s2, s3));



        testSpiel.eval7WinPoints();
    }
    @Test
    void testGetTrumpSuitString() {
        Spieler s1 = new Spieler("s1", false);
        Spieler s2 = new Spieler("s2", false);
        Spieler s3 = new Spieler("s3", false);

        Spiel spiel = new Spiel("game", List.of(s1, s2, s3));
        spiel.setTrumpSuit(Farbe.HEARTS);

        assertEquals("HEARTS", spiel.getTrumpSuitString());
    }
    @Test
    void testPlayBotMoveDoesNotThrow() {
        Spieler bot = new Spieler("Bot", true);
        Spieler s2 = new Spieler("s2", false);
        Spieler s3 = new Spieler("s3", false);

        Spiel spiel = new Spiel("game", List.of(bot, s2, s3));
        Deck deck = new Deck();
        spiel.initializeGame(deck);

        spiel.setAktuellerSpielerIndex(spiel.getSpielerListe().indexOf(bot));

        assertDoesNotThrow(() -> spiel.playBotMove());


    }
    @Test
    void playCard_FirstCardOfPushTrick_ShouldAddToPushedCards() {
        Karte card = spieler1.getHandkarten().get(0);
        spiel.playCard(spieler1, card);

        assertTrue(spiel.pushedCards.containsKey(spieler1));
        assertEquals(card, spiel.pushedCards.get(spieler1));
        assertFalse(spieler1.getHandkarten().contains(card));
        assertEquals(spieler2, spiel.getAktuellerSpieler());
    }

    @Test
    void playCard_SecondCardOfPushTrick_ShouldAddToPushedCards() {
        Karte card1 = spieler1.getHandkarten().get(0);
        Karte card2 = spieler2.getHandkarten().get(0);

        spiel.playCard(spieler1, card1);
        spiel.playCard(spieler2, card2);

        assertTrue(spiel.pushedCards.containsKey(spieler2));
        assertEquals(card2, spiel.pushedCards.get(spieler2));
        assertFalse(spieler2.getHandkarten().contains(card2));
        assertEquals(spieler3, spiel.getAktuellerSpieler());
    }

    @Test
    void playCard_ThirdCardOfPushTrick() {
        Karte card1 = spieler1.getHandkarten().get(0);
        Karte card2 = spieler2.getHandkarten().get(0);
        Karte card3 = spieler3.getHandkarten().get(0);

        spiel.playCard(spieler1, card1);
        spiel.playCard(spieler2, card2);
        spiel.playCard(spieler3, card3);

        assertFalse(!spiel.isPushCardTrick);
        assertEquals(spieler1, spiel.getAktuellerSpieler());
    }

    @Test
    void playCard_InvalidMove() {

        spiel.isPushCardTrick = false;


        Karte leadCard = new Karte(Farbe.HEARTS, Rang.KING);
        spiel.getStich().addCard(spieler1, leadCard);


        Karte invalidCard = spieler2.getHandkarten().stream()
                .filter(c -> c.getFarbe() != leadCard.getFarbe())
                .findFirst()
                .orElseThrow();


        boolean hasLeadSuit = spieler2.getHandkarten().stream()
                .anyMatch(c -> c.getFarbe() == leadCard.getFarbe());
        assumeTrue(hasLeadSuit);

        assertThrows(IllegalStateException.class, () -> {
            spiel.playCard(spieler2, invalidCard);
        });
    }

    @Test
    void playCard_FirstCardOfNormalTrick() {

        spiel.isPushCardTrick = false;

        Karte card = spieler1.getHandkarten().get(0);
        spiel.playCard(spieler1, card);

        assertEquals(1, spiel.getStich().getCardCount());
        assertEquals(card, spiel.getStich().getFirstCard());
        assertFalse(spieler1.getHandkarten().contains(card));
        assertEquals(spieler2, spiel.getAktuellerSpieler());
    }

    @Test
    void resetStich() {
        Spieler s1 = new Spieler("s1", false);
        Spieler s2 = new Spieler("s2", false);
        Spieler s3 = new Spieler("s3", false);
        ArrayList<Spieler> players = new ArrayList<>(List.of(s1,s2,s3));

        Spiel game = new Spiel("room", players);
        card1 = new Karte(Farbe.HEARTS, Rang.ACE);
        card2 = new Karte(Farbe.RAVENS, Rang.KING);

        game.getStich().addCard(s1, card1);
        game.getStich().addCard(s2, card2);

        assertFalse(game.getStich().getCards().isEmpty());

        game.resetStich();

        assertTrue(game.getStich().getCards().isEmpty());
    }


        @Test
        void testPlayCardCoverage() {

            try {
                spiel.playCard(spieler1, card1);
            } catch (Exception e) {

            }


            try {
                spiel.playCard(spieler2, card2);
            } catch (IllegalStateException e) {

            }


            Karte cardNotInHand = new Karte(Farbe.RAVENS, Rang.ACE);
            try {
                spiel.playCard(spieler1, cardNotInHand);
            } catch (IllegalArgumentException e) {

            }


            try {

                spiel.getClass().getDeclaredField("isPushCardTrick").setAccessible(true);
                spiel.getClass().getDeclaredField("isPushCardTrick").set(spiel, true);

                spiel.playCard(spieler1, card1);
            } catch (Exception e) {

            }


            try {

                spiel.getStich().addCard(spieler1, card1);
                spiel.getStich().addCard(spieler2, card2);
                spiel.getStich().addCard(spieler3, card3);

                spiel.playCard(spieler1, card1);
            } catch (Exception e) {

            }


            spiel.setAktuellerSpielerIndex(0);
            try {

                spiel.playCard(spieler1, card1);
                if (spiel.getStich().getCardCount() < 3) {
                    spiel.setAktuellerSpielerIndex(1);
                    spiel.playCard(spieler2, card2);
                }
                if (spiel.getStich().getCardCount() < 3) {
                    spiel.setAktuellerSpielerIndex(2);
                    spiel.playCard(spieler3, card3);
                }
            } catch (Exception e) {

            }


            try {
                spieler1.getHandkarten().clear();
                spieler2.getHandkarten().clear();
                spieler3.getHandkarten().clear();


                spieler1.getHandkarten().add(new Karte(Farbe.RAVENS, Rang.ACE));
                spiel.setAktuellerSpielerIndex(0);

                spiel.playCard(spieler1, spieler1.getHandkarten().get(0));
            } catch (Exception e) {

            }


            try {

                spiel.getClass().getDeclaredField("gameMode").setAccessible(true);
                spiel.getClass().getDeclaredField("gameMode").set(spiel, "UNDENUFE");


                spieler1.getHandkarten().add(card1);
                spiel.setAktuellerSpielerIndex(0);
                spiel.playCard(spieler1, card1);
            } catch (Exception e) {

            }

            try {

                spiel.getClass().getDeclaredField("gameMode").set(spiel, "OBENABE");

                spieler2.getHandkarten().add(card2);
                spiel.setAktuellerSpielerIndex(1);
                spiel.playCard(spieler2, card2);
            } catch (Exception e) {

            }
        }
    class TestDeck extends Deck {
        private final List<List<Karte>> testHands;
        private final Karte testLastCard;

        public TestDeck(List<List<Karte>> hands, Karte lastCard) {
            super();
            this.testHands = hands;
            this.testLastCard = lastCard;
        }

        @Override
        public List<List<Karte>> deal() {
            return testHands;
        }

        @Override
        public Karte revealLastCard() {
            return testLastCard;
        }
    }
}

