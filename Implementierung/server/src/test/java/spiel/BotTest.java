package spiel;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import spielraum.Spielraum;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class BotTest {

    Spiel spiel;
    Spieler player;
    Spieler easyBot;
    Spieler hardBot;
    Stich stich;
    Karte card1;
    Karte card2;
    List<Spieler> players;


    @BeforeEach
    void setUp() {
        card1 = new Karte( Farbe.HEARTS, Rang.KING);
        card2 = new Karte( Farbe.HEARTS, Rang.ACE);

        easyBot = new Spieler("EasyBot",true);
        easyBot.setAmZug(true);
        easyBot.getHandkarten().add(card1);
        easyBot.getHandkarten().add(card2);

        hardBot = new Spieler("HardBot",true);
        hardBot.setAmZug(true);
        hardBot.getHandkarten().add(card1);
        hardBot.getHandkarten().add(card2);

        player = new Spieler("player1", false);
        player.getHandkarten().add(card1);

        players = List.of(easyBot, hardBot, player);
        spiel = new Spiel("TestGame", new ArrayList<>(players));
        stich = new Stich();
    }


    @Test
    void testPlayTurnNoBots() {
        Spieler player1 = new Spieler("player1", false);
        Spieler player2 = new Spieler("player2", false);
        player1.setAmZug(true);

        List<Spieler> players = List.of(player1, player2);
        Spiel testSpiel = new Spiel("NoBotGame", new ArrayList<>(players));

        testSpiel.setAktuellerSpielerIndex(0);

        Bot.playTurn(testSpiel);
        assertFalse(testSpiel.getAktuellerSpieler().isBot());
    }

    @Test
    void testPlayTurnBotAlreadyPlayed() {
        spiel.getStich().addCard(easyBot, card1);
        spiel.getStich().addCard(hardBot, card2);
        Bot.playTurn(spiel);
        assertTrue(spiel.getAktuellerSpieler().isBot());
        assertTrue(spiel.getAktuellerSpieler().isBot());
    }

    @Test
    void testPlayTurnEasyBotValidCards() throws Exception {
        Bot.playTurn(spiel);
        assertTrue(spiel.getAktuellerSpieler().isBot());
    }

    @Test
    void testPlayTurn_easyBotInvalidCards() throws Exception {
        Karte lead = new Karte( Farbe.RAVENS, Rang.TEN);
        spiel.getStich().addCard(player, lead);

        Bot.playTurn(spiel);
        assertTrue(spiel.getAktuellerSpieler().isBot());
    }
    @Test
    void testPlayEasy_EmptyValidCards() {
        easyBot.getHandkarten();
        spiel.setAktuellerSpielerIndex(players.indexOf(easyBot));
        Bot.playEasy(easyBot, spiel);

    }

    @Test
    void testPlayTurnHardBotValidCards() throws Exception {
        Bot.playTurn(spiel);
        assertTrue(spiel.getAktuellerSpieler().isBot());

    }

    @Test
    void testPlayTurnHardBotInvalidCards() throws Exception {
        Karte lead = new Karte( Farbe.RAVENS, Rang.TEN);
        spiel.getStich().addCard(player, lead);

        Bot.playTurn(spiel);
        assertTrue(spiel.getAktuellerSpieler().isBot());
    }

    @Test
    void testPlayHard() {

        Karte leadCard = new Karte(Farbe.HEARTS, Rang.EIGHT);
        Karte validFollowCard = new Karte(Farbe.HEARTS, Rang.KING);

        hardBot.getHandkarten().clear();

        hardBot.getHandkarten().add(validFollowCard);
        spiel.getStich().addCard(player, leadCard);

        spiel.setTrumpSuit(Farbe.RAVENS);
        spiel.setAktuellerSpielerIndex(spiel.getSpielerListe().indexOf(hardBot));


        Bot.playHard(hardBot, spiel);


        assertEquals(0, hardBot.getHandkarten().size());

    }

    @Test
    void testChooseLeadCard() {

        Karte card1 = new Karte(Farbe.HEARTS, Rang.SEVEN);
        Karte card2 = new Karte(Farbe.HEARTS, Rang.EIGHT);
        Karte card3 = new Karte(Farbe.HEARTS, Rang.ACE);

        List<Karte> cards = new ArrayList<>();
        cards.add(card1);
        cards.add(card2);
        cards.add(card3);

        Spiel testSpiel = new Spiel("test", new ArrayList<>());
        testSpiel.isObenabe();

        Karte choice = Bot.chooseLeadCard(cards, Farbe.RAVENS, testSpiel );


        assertEquals(card1, choice);
    }
    @Test
    void testChooseFollowCardWeakCards() {
        Karte lead = new Karte(Farbe.HEARTS, Rang.ACE);
        Karte weak1 = new Karte(Farbe.HEARTS, Rang.SEVEN);
        Karte weak2 = new Karte(Farbe.HEARTS, Rang.NINE);
        List<Karte> cards = List.of(weak1, weak2);

        Spiel testSpiel = new Spiel("test", new ArrayList<>());

        Karte result = Bot.chooseFollowCard(cards, lead, Farbe.RAVENS, testSpiel);
        assertTrue(result == weak1 || result == weak2);
    }
    @Test
    void testChooseLeadCardSingleCard() {
        Karte single = new Karte(Farbe.HEARTS, Rang.TEN);
        List<Karte> hand = List.of(single);
        Spiel testSpiel = new Spiel("test", new ArrayList<>());

        Karte chosen = Bot.chooseLeadCard(hand, Farbe.RAVENS, testSpiel);

        assertEquals(single, chosen);
    }
    @Test
    void testChooseFollowCard() {

        Karte leadCard = new Karte(Farbe.HEARTS, Rang.EIGHT);
        Karte strongerCard = new Karte(Farbe.HEARTS, Rang.ACE);
        Karte weakerCard = new Karte(Farbe.HEARTS, Rang.SEVEN);

        List<Karte> hand = List.of(weakerCard, strongerCard);

        Spiel testSpiel = new Spiel("test", new ArrayList<>());
        testSpiel.isObenabe();

        Karte result = Bot.chooseFollowCard(hand, leadCard, null, testSpiel);
        assertEquals(strongerCard, result);
    }

}



