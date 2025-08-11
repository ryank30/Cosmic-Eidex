package server.implTest;

import account.Account;
import dto.GameroomDTO;
import dto.LeaderboardEntryDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.ServerContext;
import server.impl.RaumverwaltungImpl;
import server.impl.SpielverwaltungImpl;
import server.impl.ZugriffsverwaltungImpl;
import spiel.Spiel;
import spiel.Spieler;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class RaumverwaltungImplTest {

    RaumverwaltungImpl testRoom;
    ZugriffsverwaltungImpl testAccess;
    ServerContext context;
    @BeforeEach
    void setup() throws RemoteException{
        context = new ServerContext();
        testRoom = new RaumverwaltungImpl(context);
        testAccess = new ZugriffsverwaltungImpl(context);
    }
    @Test
    void testSpielraumErstellen() {
        testRoom.spielraumErstellen("host", "room", "pass");
        assertEquals(1, testRoom.getRaumSpieler("room").size());
        assertThrows(RuntimeException.class, () ->
                testRoom.spielraumErstellen("host2", "room", "pass2"));
    }

    @Test
    void testSpielraumBeitreten() {
        testRoom.spielraumErstellen("host", "room", "pass");

        assertThrows(RuntimeException.class, () ->
                testRoom.spielraumBeitreten("player", "noroom", "pass"));

        assertThrows(RuntimeException.class, () ->
                testRoom.spielraumBeitreten("player", "room", "wrong"));

        testRoom.spielraumBeitreten("player", "room", "pass");
        assertEquals(2, testRoom.getRaumSpieler("room").size());

        assertThrows(RuntimeException.class, () ->
                testRoom.spielraumBeitreten("player", "room", "pass"));
    }

    @Test
    void testGetRaumName() throws RemoteException {
        testRoom.spielraumErstellen("host", "room", "pass");

        String result = testRoom.getRaumName("host");
        assertEquals("room", result);

        String notFound = testRoom.getRaumName("nobody");
        assertNull(notFound);
    }

    @Test
    void testSpielraumVerlassen() {
        testRoom.spielraumErstellen("host", "room", "pass");
        testRoom.spielraumBeitreten("player", "room", "pass");

        assertThrows(RuntimeException.class, () ->
                testRoom.spielraumVerlassen("player", "noroom"));

        testRoom.spielraumVerlassen("player", "room");
        assertEquals(1, testRoom.getRaumSpieler("room").size());

        testRoom.spielraumVerlassen("host", "room");
        assertNull(testRoom.getRaumSpieler("room"));
    }
    @Test
    void testSpielerEntfernen() {
        testRoom.spielraumErstellen("host", "room", "pass");
        testRoom.spielraumBeitreten("player", "room", "pass");

        assertThrows(RuntimeException.class, () ->
                testRoom.spielerEntfernen("player", "noroom"));

        testRoom.spielerEntfernen("player", "room");
        assertEquals(1, testRoom.getRaumSpieler("room").size());
    }

    @Test
    void testEasyBotHinzufuegen() throws RemoteException {
        testRoom.spielraumErstellen("host", "room", "pass");
        testRoom.easyBotHinzufuegen( "room");
        ArrayList<String> players = testRoom.getRaumSpieler("room");
        assertTrue(players.stream().anyMatch(name -> name.startsWith("EasyBot")));
    }
    @Test
    void testHardBotHinzufuegen() throws RemoteException {
        testRoom.spielraumErstellen("host", "room", "pass");
        testRoom.hardBotHinzufuegen( "room");
        ArrayList<String> players = testRoom.getRaumSpieler("room");

        assertTrue(players.stream().anyMatch(name -> name.startsWith("HardBot")));

    }

    @Test
    void testBotEntfernen() throws RemoteException {
        testRoom.spielraumErstellen("host", "room", "pass");
        testRoom.easyBotHinzufuegen("room");
        testRoom.botEntfernen("room");
        ArrayList<String> players = testRoom.getRaumSpieler("room");

    }
    @Test
    void testGetRaumSpieler() {
        testRoom.spielraumErstellen("host", "room", "pass");
        ArrayList<String> result = testRoom.getRaumSpieler("room");
        assertEquals(1, result.size());
        assertEquals("host", result.get(0));
    }

    @Test
    void testNeueSpielraumNachricht() {
        testRoom.neueSpielraumNachricht("msg", "sender", "room");
        String result = testRoom.getSpielraumChatverlauf("room");
        assertTrue(result.contains("sender: msg"));
    }

    @Test
    void testGetSpielraumChatverlauf() {
        testRoom.neueSpielraumNachricht("msg", "sender", "room");
        String result = testRoom.getSpielraumChatverlauf("room");
        assertEquals("sender: msg", result);

        String empty = testRoom.getSpielraumChatverlauf("empty");
        assertEquals("", empty);
    }

    @Test
    void testIsHost() {
        testRoom.spielraumErstellen("host", "room", "pass");
        testRoom.spielraumBeitreten("player", "room", "pass");

        assertTrue(testRoom.isHost("host", "room"));
        assertFalse(testRoom.isHost("player", "room"));
    }
    @Test
    void testIstInRaum() {
        testRoom.spielraumErstellen("host", "room", "pass");

        assertTrue(testRoom.istInRaum("room", "host"));
        assertFalse(testRoom.istInRaum("room", "nobody"));
        assertFalse(testRoom.istInRaum("noroom", "host"));
    }

    @Test
    void testRaumVoll() {
        testRoom.spielraumErstellen("host", "room", "pass");
        assertFalse(testRoom.raumVoll("room"));

        testRoom.spielraumBeitreten("p1", "room", "pass");
        testRoom.spielraumBeitreten("p2", "room", "pass");
        assertTrue(testRoom.raumVoll("room"));
    }
    @Test
    void testReceiveHeartbeat() {
        testRoom.receiveHeartbeat("user");

    }

    @Test
    void testGetRooms() throws RemoteException {
        testRoom.spielraumErstellen("host", "room", "pass");

        List<GameroomDTO> result = testRoom.getRooms();
        assertEquals(1, result.size());

        testRoom.spielraumVerlassen("host", "room");
        result = testRoom.getRooms();
        assertEquals(0, result.size());
    }
    @Test
    void testGetAccountExistingUser() {
        Account account = new Account("player1", "password");
        context.accounts = new ArrayList<>();
        context.accounts.add(account);

        Account result = testRoom.getAccount("player1");

        assertNotNull(result);
        assertEquals("player1", result.get_username());
        assertEquals(account, result);
    }
    @Test
    void testGetAccountNonExistingUser() {
        context.accounts = new ArrayList<>();
        context.accounts.add(new Account("player1", "password"));

        Account result = testRoom.getAccount("UnknownUser");

        assertNull(result);
    }
    @Test
    void testGetAccounts() {
        Account account1 = new Account("player1", "pass1");
        Account account2 = new Account("player2", "pass2");
        List<Account> expectedAccounts = Arrays.asList(account1, account2);

        context.accounts = new ArrayList<>(expectedAccounts);

        List<Account> result = testRoom.getAccounts();

        assertEquals(expectedAccounts, result);
    }
    @Test
    void testGetLeaderboard() throws RemoteException {
        List<Account> mockAccounts = new ArrayList<>();
        for (int i = 0; i < 15; i++) {
            Account account = new Account("player" + i, "pass" + i);
            account.set_wins(i);
            mockAccounts.add(account);
        }

        context.accounts = mockAccounts;

        List<LeaderboardEntryDTO> leaderboard = testRoom.getLeaderboard();

        assertEquals(10, leaderboard.size());

        for (int i = 0; i < leaderboard.size() - 1; i++) {
            int winsCurrent = leaderboard.get(i).getWins();
            int winsNext = leaderboard.get(i + 1).getWins();
            assertTrue(winsCurrent >= winsNext, "Leaderboard is not sorted correctly.");
        }

        assertEquals("player14", leaderboard.get(0).getUsername());
        assertEquals(14, leaderboard.get(0).getWins());
    }
}





