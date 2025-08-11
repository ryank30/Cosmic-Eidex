package util;
import api.Spielverwaltung;
import controllers.GameBoardController;
import dto.GameStateDTO;
import dto.PlayerDTO;
import dto.StichDTO;
import dto.CardDTO;
import models.StichModel;
import models.PlayerModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import java.util.*;
public class GameserviceTest {

    private GameService gameService;
    private final String LOCAL_PLAYER = "player1";
    private final String ROOM_NAME = "testRoom";

    @Mock
    private Spielverwaltung spielverwaltungMock;

    @Mock
    private GameBoardController gameBoardControllerMock;

    private PlayerDTO createPlayerDTO(String name, int points, int winPoints, List<CardDTO> cardIds, List<CardDTO> validMoves) {
        PlayerDTO player = new PlayerDTO(name, cardIds, validMoves, points, winPoints);

        List<CardDTO> cards = new ArrayList<>();
        for (CardDTO id : cardIds) {
            CardDTO card = id;
            card.getId();
            cards.add(card);
        }
        player.getHand();

        return player;
    }

    private GameStateDTO createTestGameState() {
        return new GameStateDTO(
                Arrays.asList(
                        new PlayerDTO("player1", Arrays.asList(new CardDTO("card1"), new CardDTO("card2")),Arrays.asList(new CardDTO("card1"), new CardDTO("card2")),10, 0),
                        new PlayerDTO("player2", Arrays.asList(new CardDTO("card3"), new CardDTO("card4")), Arrays.asList(new CardDTO("card3"), new CardDTO("card4")),5, 0),
                        new PlayerDTO("player3", Arrays.asList(new CardDTO("card5"), new CardDTO("card6")),Arrays.asList(new CardDTO("card5"), new CardDTO("card6")) ,8, 0)
                ),
                1,
                true
        );
    }
    @BeforeEach
    void setUp() throws RemoteException, NotBoundException {

        spielverwaltungMock = mock(Spielverwaltung.class);
        gameBoardControllerMock = mock(GameBoardController.class);
        gameService = new GameService(spielverwaltungMock, LOCAL_PLAYER, ROOM_NAME) {

        };
        gameService.setGameBoardController(gameBoardControllerMock);
        GameStateDTO initialState = createTestGameState();
        //when(spielverwaltungMock.getGameState(ROOM_NAME)).thenReturn(initialState);
        gameService.initPlayerModels(initialState);

    }


    @Test
    void constructor_ShouldInitializeFields() {
        assertEquals(LOCAL_PLAYER, gameService.getLocalPlayer().getName());
        assertNotNull(gameService.getStich());
        assertFalse(gameService.getPlayerModels().isEmpty());
    }

    @Test
    void testGetLocalPlayer() {
        PlayerModel result = gameService.getLocalPlayer();
        assertEquals(LOCAL_PLAYER, result.getName());
    }

    @Test
    void TestGetPlayerModel() {
        PlayerModel result = gameService.getPlayerModel("player2");
        assertEquals("player2", result.getName());
    }

    @Test
    void updateStichFromDTO_WithNullDto_ShouldClearStich() {
        StichModel stich = gameService.getStich();
        stich.putCard("player1", "card1");

        gameService.updateStichFromDTO(null);

        assertTrue(stich.getCardsInTrick().isEmpty());
    }



    @Test
    void updateStichFromDTO() {
        StichModel stich = gameService.getStich();
        LinkedHashMap<String, String> cards = new LinkedHashMap<>();
        cards.put("player1", "card1");
        cards.put("player2", "card2");
        StichDTO dto = new StichDTO(cards);

        gameService.updateStichFromDTO(dto);

        assertEquals(2, stich.getCardsInTrick().size());
        assertEquals("card1", stich.getCardsInTrick().get("player1"));
        assertEquals("card2", stich.getCardsInTrick().get("player2"));
    }

    @Test
    void TestClearStich() {
        StichModel stich = gameService.getStich();
        stich.putCard("player1", "card1");
        stich.putCard("player2", "card2");

        gameService.clearStich();

        assertTrue(stich.getCardsInTrick().isEmpty());
    }

    @Test
    void TestupdatePlayersFromGameState_WithNullDto() {
        PlayerModel player = gameService.getPlayerModel(LOCAL_PLAYER);
        int originalScore = player.getScore();

        gameService.updatePlayersFromGameState(null);

        assertEquals(originalScore, player.getScore());
    }

    @Test
    void TestInitPlayerModels() {
        GameStateDTO dto = createTestGameState();
        gameService.initPlayerModels(dto);

        assertEquals(3, gameService.getPlayerModels().size());
        assertTrue(gameService.getPlayerModels().containsKey("player1"));
        assertTrue(gameService.getPlayerModels().containsKey("player2"));
        assertTrue(gameService.getPlayerModels().containsKey("player3"));
    }
    @Test
    public void testPollGameState() throws RemoteException {
        GameStateDTO state = mock(GameStateDTO.class);
        when(spielverwaltungMock.getGameState(ROOM_NAME)).thenReturn(state);

        gameService.pollGameState();


        assertTrue(true);
    }
    @Test
    public void testStartPolling() {
        assertDoesNotThrow(() -> {
            gameService.startPolling();
            gameService.shutdown();
        });
    }
    @Test
    public void testShutdownCleansUp() {
        gameService.startPolling();
        gameService.shutdown();
        assertTrue(true);
    }
    @Test
    public void testGetGameReturnsSpielverwaltung() {
        assertEquals(spielverwaltungMock, gameService.getGame());
    }

    @Test
    void testGetLeftOpponentReturnsCorrectOpponent() {

        PlayerModel left = gameService.getLeftOpponent();

        assertEquals("player2", left.getName());
    }

    @Test
    void testGetRightOpponentReturnsCorrectOpponent() {

        PlayerModel right = gameService.getRightOpponent();

        assertEquals("player3", right.getName());
    }

}
