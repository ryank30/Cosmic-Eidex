package server.implTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.ServerContext;
import server.impl.SpielverwaltungImpl;
import spiel.*;

import java.rmi.RemoteException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class SpielverwaltungImplTest {

    SpielverwaltungImpl testSpiel;
    ServerContext context;
    Spieler player1;
    Spieler player2;
    Spieler player3;

    @BeforeEach
    void setup() throws RemoteException{
        context = new ServerContext();
        testSpiel = new SpielverwaltungImpl(context);
        player1 = new Spieler("player1", false);
        player2 = new Spieler("player2", false);
        player3 = new Spieler("player3", false);
    }
    @Test
    void testGetGameState() throws Exception {
        ArrayList<Spieler> players = new ArrayList<>();
        players.add(player1);
        players.add(player2);
        players.add(player3);
        context.spielraeume.put("game", players);
        testSpiel.spielStarten("game");
        assertNotNull(testSpiel.getGameState("game"));
        assertNull(testSpiel.getGameState("nonexistent"));
    }
    @Test
    void testGetStichState() throws Exception {
        ArrayList<Spieler> players = new ArrayList<>();
        players.add(player1);
        players.add(player2);
        players.add(player3);
        context.spielraeume.put("game", players);
        testSpiel.spielStarten("game");
        assertNotNull(testSpiel.getStichState("game"));
    }
    @Test
    void testGetGameMode() throws Exception {
        ArrayList<Spieler> players = new ArrayList<>();
        players.add(player1);
        players.add(player2);
        players.add(player3);
        context.spielraeume.put("game", players);
        testSpiel.spielStarten("game");

        assertDoesNotThrow(() -> testSpiel.getGameMode("game"));
    }

    @Test
    void testClientPlayCard() throws Exception {
        ArrayList<Spieler> players = new ArrayList<>();
        players.add(player1);
        players.add(player2);
        players.add(player3);
        context.spielraeume.put("game", players);
        testSpiel.spielStarten("game");
        Spiel ongoing = context.spiele.get("game");
        ongoing.setAktuellerSpielerIndex(players.indexOf(player1));
        Karte firstCard = player1.getHandkarten().get(0);
        String cardId = firstCard.getId();
        assertDoesNotThrow(() -> testSpiel.clientPlayCard("game", player1.getName(), cardId));


        assertFalse(player1.getHandkarten().contains(firstCard));
    }
    @Test
    void testClientPlayCard_botPlaysAfterPlayer() throws Exception {
        List<Spieler> players = new ArrayList<>();
        players.add(player1);
        Spieler bot = new Spieler("bot1", true);
        bot.getHandkarten();
        players.add(bot);

        context.spielraeume.put("game", players);
        testSpiel.spielStarten("game");
        Spiel game = context.spiele.get("game");
        game.setAktuellerSpielerIndex(0);

        Karte cardToPlay = player1.getHandkarten().get(0);
        String cardId = cardToPlay.getId();


        testSpiel.clientPlayCard("game", player1.getName(), cardId);
        assertTrue(bot.getHandkarten().isEmpty() || !bot.getHandkarten().contains(cardToPlay));
    }
    @Test
    void testSpielStarten() throws Exception {
        ArrayList<Spieler> players = new ArrayList<>();
        players.add(player1);
        players.add(player2);
        players.add(player3);
        context.spielraeume.put("game", players);
        testSpiel.spielStarten("game");
        assertTrue(testSpiel.spielGestartet("game"));
    }
    @Test
    void testSpielStartenWithFewPlayers() {
        ArrayList<Spieler> players = new ArrayList<>();
        players.add(player1);
        context.spielraeume.put("game", players);
        assertThrows(RuntimeException.class, () -> testSpiel.spielStarten("game"));
    }

    @Test
    void testSpielBeenden() throws Exception {
        ArrayList<Spieler> players = new ArrayList<>();
        players.add(player1);
        players.add(player2);
        players.add(player3);
        context.spielraeume.put("game", players);
        testSpiel.spielStarten("game");

        testSpiel.spielBeenden("game");
    }

    @Test
    void testSpielVerlassen() throws Exception {
        ArrayList<Spieler> players = new ArrayList<>();
        players.add(player1);
        players.add(player2);
        players.add(player3);
        context.spielraeume.put("game", players);
        testSpiel.spielStarten("game");

        testSpiel.spielVerlassen("game", "p1");
    }
    @Test
    void testSpielGestartet() throws Exception {
        ArrayList<Spieler> players = new ArrayList<>();
        players.add(player1);
        players.add(player2);
        players.add(player3);
        context.spielraeume.put("game", players);
        testSpiel.spielStarten("game");

        boolean result = testSpiel.spielGestartet("game");
        assertTrue(result);
    }


    @Test
    void testSpielVorbei() throws Exception {
        ArrayList<Spieler> players = new ArrayList<>();
        players.add(player1);
        players.add(player2);
        players.add(player3);
        context.spielraeume.put("game", players);
        testSpiel.spielStarten("game");
        testSpiel.spielBeenden("game");
        boolean result = testSpiel.spielVorbei("game");
        assertTrue(result);
    }
    @Test
    void testSpielLoeschen() throws Exception {
        ArrayList<Spieler> players = new ArrayList<>();
        players.add(player1);
        players.add(player2);
        players.add(player3);
        context.spielraeume.put("game", players);
        testSpiel.spielStarten("game");

        testSpiel.spielLoeschen("game");
    }

    @Test
    void testSetSpielVorbei() throws Exception {
        ArrayList<Spieler> players = new ArrayList<>();
        players.add(player1);
        players.add(player2);
        players.add(player3);
        context.spielraeume.put("game", players);
        testSpiel.spielStarten("game");

        testSpiel.setSpielVorbei("game");
        assertTrue(testSpiel.spielVorbei("game"));
    }
    @Test
    void testNeueSpielNachricht() throws Exception {
        testSpiel.neueSpielNachricht("msg", "sender", "receiver");
    }

    @Test
    void testGetSpielChatverlauf() throws Exception {
        String result = testSpiel.getSpielChatverlauf("game");
        assertNotNull(result);
    }
    @Test
    void testIstAmZug() throws Exception {
        ArrayList<Spieler> players = new ArrayList<>();
        players.add(player1);
        players.add(player2);
        players.add(player3);
        context.spielraeume.put("game", players);
        testSpiel.spielStarten("game");
        assertDoesNotThrow(() -> testSpiel.istAmZug("game", "player1"));
    }
    @Test
    void testGetHandkarten() throws Exception {
        ArrayList<Spieler> players = new ArrayList<>();
        players.add(player1);
        players.add(player2);
        players.add(player3);
        context.spielraeume.put("game", players);
        testSpiel.spielStarten("game");
        assertNotNull(testSpiel.getHandkarten("game", "player1"));
    }
    @Test
    void testGetPunkte() throws Exception {
        ArrayList<Spieler> players = new ArrayList<>();
        players.add(player1);
        players.add(player2);
        players.add(player3);
        context.spielraeume.put("game", players);
        testSpiel.spielStarten("game");
        assertEquals(0, testSpiel.getPunkte("game", "player1"));
    }
    @Test
    void testGetPunktestand() throws Exception {
        ArrayList<Spieler> players = new ArrayList<>();
        players.add(player1);
        players.add(player2);
        players.add(player3);
        context.spielraeume.put("game", players);
        testSpiel.spielStarten("game");
        assertTrue(testSpiel.getPunktestand("game").contains("player1"));
    }
    @Test
    void testGetSpielThrows() {
        assertThrows(Exception.class, () -> testSpiel.getHandkarten("Does not exist", "player1"));
    }
}