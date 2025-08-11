package util;

import api.Spielverwaltung;
import controllers.GameBoardController;
import dto.CardDTO;
import dto.GameStateDTO;
import dto.PlayerDTO;
import dto.StichDTO;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.util.Duration;
import models.PlayerModel;
import models.StichModel;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Service class responsible for managing game state synchronization and polling.
 */
public class GameService {

    private final Spielverwaltung game;
    private final String localPlayer;
    private final String roomName;
    private final Map<String, PlayerModel> playerModels = new HashMap<>();
    private final StichModel stich;
    private GameBoardController gameBoardController;
    private boolean gameEnded = false;
    private Integer gameOverCounter;

    private final ScheduledExecutorService poller = Executors.newSingleThreadScheduledExecutor();

    /**
     * Initializes the service with game API, player information, and starts
     * automatic polling for game state updates.
     * @param game the remote game management interface.
     * @param localPlayer the name of the local player.
     * @param roomName the name of the game room.
     */
    public GameService(Spielverwaltung game, String localPlayer, String roomName) {
        this.game = game;
        this.localPlayer = localPlayer;
        this.roomName = roomName;
        this.stich = new StichModel();
        this.gameOverCounter = 0;

        startPolling();
    }
    /**
     * Gets the PlayerModel for the local player.
     *
     * @return the PlayerModel representing the current user
     */
    public PlayerModel getLocalPlayer() {
        return playerModels.get(localPlayer);
    }
    /**
     * Retrieves a list of all opponent players (non-local players).
     * @return list of PlayerModel objects representing opponents.
     */
    List<PlayerModel> getOpponentPlayers() {


        List<PlayerModel> opponents = new ArrayList<>();


        for (PlayerModel model : getPlayerModels().values()) {

            if (!model.getName().equals(localPlayer)) {
                opponents.add(model);
            }
        }
        return opponents;
    }

    public PlayerModel getLeftOpponent() {

        List<PlayerModel> opponents = getOpponentPlayers();
        if (opponents.isEmpty()) {
            return null;
        }

        return opponents.get(0);
    }

    public PlayerModel getRightOpponent() {

        List<PlayerModel> opponents = getOpponentPlayers();
        if (opponents.size() < 2) {
            return null;
        }

        PlayerModel rightOpponent = opponents.get(1);
        return rightOpponent;
    }
/**
 * Gets the PlayerModel for a specific player by name.
 * @param playerName the name of the player
 * @return the PlayerModel for the specified player, or null if not found
     */
    public PlayerModel getPlayerModel(String playerName) {
        return playerModels.get(playerName);
    }

    /**
     * Updates the local trick model based on data from the server.
     * @param dto the StichDTO containing current trick state from server.
     */
    public void updateStichFromDTO(StichDTO dto) {
        if (dto == null || dto.getCardsInTrick() == null || dto.getCardsInTrick().isEmpty()) {
            if (!stich.getCardsInTrick().isEmpty()) {
                this.stich.clear();
            }
            return;
        }
        gameBoardController.checkFor3CardsInStichWindow();
        LinkedHashMap<String, String> newCards = dto.getCardsInTrick();

        for (Map.Entry<String, String> entry : newCards.entrySet()) {
            String key = entry.getKey();
            String newValue = entry.getValue();
            String currentValue = stich.getCardsInTrick().get(key);
            if (currentValue == null && newValue != null) {
                stich.putCard(key, newValue);
            }
        }
    }
    /**
     * Clears all cards from the current trick.

     */
    public void clearStich() {
        stich.clear();
    }
    /**
     * Updates all player models based on the current game state from the server.
     * @param gameStateDTO the current game state data from the server
     */
    public void updatePlayersFromGameState(GameStateDTO gameStateDTO) {
        if (gameStateDTO == null) {
            return;
        }

        List<PlayerDTO> players = gameStateDTO.getPlayers();
        for (int i = 0; i < players.size(); i++) {
            PlayerDTO dto = players.get(i);

            String name = dto.getName();

            PlayerModel model = playerModels.get(name);
            if (!Objects.equals(model.getScore(), dto.getPoints())){
                model.setScore(dto.getPoints());
            }
            ObservableList<String> hand = FXCollections.observableArrayList(
                    dto.getHand().stream()
                            .map(CardDTO::getId)
                            .toList()
            );
            List<String> validCard =dto.getValidMoves().stream()
                    .map(CardDTO::getId)
                    .toList();

            if (!Objects.equals(model.getHand(), hand)){
                model.handProperty().set(hand);
            }
            if (!Objects.equals(model.getWinPoints(), dto.getWinPoints())) {
                model.setWinPoints(dto.getWinPoints());
                System.out.println("winpoints were set to " + dto.getWinPoints() + " for player " + name);
            }
            if (!Objects.equals(model.getValidCard(), validCard)){
                model.setValidCard(validCard);
            }
            boolean isMyTurn = i == gameStateDTO.getCurrentPlayerIndex();
            //flip flop because otherwise change doesnt get picked up
            model.isMyTurnProperty().set(!isMyTurn);
            model.isMyTurnProperty().set(isMyTurn);

        }
    }
    /**
     * Initializes player models from the initial game state.
     * @param dto the initial game state containing player information
     */
    public void initPlayerModels(GameStateDTO dto){
        List<PlayerDTO> players = dto.getPlayers();
        for (int i = 0; i < 3; i++) {
            PlayerDTO playerDTO = players.get(i);
            String name = playerDTO.getName();
            PlayerModel model = new PlayerModel(name);
            playerModels.put(name, model);
        }
    }

    /**
     * Ends the game for other players (non-host).
     * @throws IOException if there's an error during scene switching.
     */
    public void endGameOthers() throws IOException {
        this.shutdown();
        gameBoardController.switchWinScreen();
    }

    /**
     * Ends the game for the host player.
     * @throws Exception if there's an error during game cleanup or scene switching.
     */
    public void endGame() throws Exception {
        this.shutdown();
        gameBoardController.switchWinScreen();
        game.spielBeenden(roomName);
    }
    /**
     * Polls the game state from the server and updates local models.

     */
    void pollGameState() {
        try {
            GameStateDTO dto = game.getGameState(roomName);
            if (dto == null || !dto.isActive()) {
                gameOverCounter++;
                if (gameOverCounter > 5) {
                    System.err.println("Game is over or state is null. Stopping polling.");
                    gameEnded = true;
                    return;
                }
            }

            StichDTO dtoStich = game.getStichState(roomName);

            Platform.runLater(() -> {
                updatePlayersFromGameState(dto);

                PauseTransition pause = new PauseTransition(Duration.millis(500));
                pause.setOnFinished(event -> {
                    updateStichFromDTO(dtoStich);
                });
                pause.play();
            });

        } catch (Exception e) {
            System.err.println("Error while polling game state: " + e.getMessage());
            e.printStackTrace();
            gameEnded = true;
        }
    }

    /**
     *  Starts the automatic polling mechanism for game state updates.
     *  Automatically handles game end conditions and cleanup.
     */
    void startPolling() {
        poller.scheduleAtFixedRate(() -> {
            if (!gameEnded) {
                pollGameState();
            } else {
                try {
                    endGameOthers();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }, 0, 500, TimeUnit.MILLISECONDS);
    }
/**
 * Shuts down the polling service and stops all background tasks.
 */
    public void shutdown() {
        poller.shutdownNow();
    }

    /**
     * Gets the map of all player models.
     * @return map containing all PlayerModel instances indexed by player name.
     */
    public Map<String, PlayerModel> getPlayerModels() {
        return playerModels;
    }
    /**
     * Gets the remote game management interface.
     *
     * @return the Spielverwaltung instance used for server communication
     */
    public Spielverwaltung getGame() {
        return game;
    }
    /**
     * Gets the current trick model.
     *
     * @return the StichModel representing the current trick state
     */
    public StichModel getStich() {
        return stich;
    }
    /**
     * Sets the game board controller reference.
     * @param gameBoardController the GameBoardController instance to use for UI operations
     */
    public void setGameBoardController(GameBoardController gameBoardController) {
        this.gameBoardController = gameBoardController;
    }
}
