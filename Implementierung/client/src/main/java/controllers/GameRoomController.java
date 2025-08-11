package controllers;

import api.Zugriffsverwaltung;
import app.RemoteServiceLocator;
import chat.ChatNachricht;
import api.Chatservice;
import api.Spielverwaltung;
import api.Raumverwaltung;
import dto.GameStateDTO;
import dto.LeaderboardEntryDTO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import util.HeartbeatManager;
import util.GameService;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Controller class responsible for managing game room operations.
 */
public class GameRoomController {

    private Raumverwaltung raum;
    private Spielverwaltung game;
    private Zugriffsverwaltung zugriff;


    private String username = AccountManager.getAccount().get_username();

    @FXML
    TextField Spieler1Field;
    @FXML
    TextField Spieler2Field;
    @FXML
    TextField Spieler3Field;


    @FXML
    Button spieler2RemoveBtn;
    @FXML
    Button spieler3RemoveBtn;


    @FXML private Button removeBot1Button;
    @FXML private Button removeBot2Button;
    @FXML private Button addEasyBotButton;
    @FXML private Button addHardBotButton;


    @FXML private CheckBox readyCheckBox;
    @FXML private Button leaveButton;
    @FXML
    Button startGameButton;


    @FXML private TextField chatTextBox;
    private Chatservice chat;

    @FXML public TableView<LeaderboardEntryDTO> LeaderBoardTable;
    @FXML public TableColumn<LeaderboardEntryDTO, String> playerNameColumn;
    @FXML public TableColumn<LeaderboardEntryDTO, Integer> winsColumn;
    private final ObservableList<LeaderboardEntryDTO> leaderboardData = FXCollections.observableArrayList();
    private ScheduledExecutorService scheduler;


    @FXML public TextArea write_message;
    @FXML public TextField message_input;
    String roomName;
    private Timer playerUpdateTimer;
    private Timer gameStartTimer;

    SceneSwitching sceneSwitcher = new SceneSwitching();

    public GameRoomController() throws Exception {
        chat = RemoteServiceLocator.getChat();
        this.raum = RemoteServiceLocator.getRaum();
        this.game = RemoteServiceLocator.getSpiel();
        this.roomName = this.raum.getRaumName(username);

    }
    public GameRoomController(Raumverwaltung raum, Spielverwaltung game, Chatservice chat) {
        this.raum = raum;
        this.game = game;
        this.chat = chat;
    }

    /**
     * Checks if the current user is the host of the game room.
     *
     * @return true if host, false otherwise
     */
    boolean isCurrentUserHost() {
        return username.equals(Spieler1Field.getText());
    }


    /**
     * Removes Spieler 2 from the game room.
     *
     * @throws RemoteException if RMI call fails
     */
    @FXML
    void onRemoveSpieler2() throws RemoteException {
        raum.spielraumVerlassen(Spieler2Field.getText(), raum.getRaumName(username));
        Spieler2Field.clear();
        Spieler2Field.setVisible(false);
        Spieler2Field.setDisable(true);
    }

    /**
     * Removes Spieler 3 from the game room.
     *
     * @throws RemoteException if RMI call fails
     */
    @FXML
    void onRemoveSpieler3() throws RemoteException {
        raum.spielraumVerlassen(Spieler3Field.getText(), raum.getRaumName(username));
        Spieler3Field.clear();
        Spieler3Field.setVisible(false);
        Spieler3Field.setDisable(true);
    }


    @FXML
    void handleAddEasyBot() throws RemoteException {
        raum.easyBotHinzufuegen(raum.getRaumName(username));
        System.out.println("Easy Bot added");
    }

    @FXML
    void handleAddHardBot() throws RemoteException {
        raum.hardBotHinzufuegen(raum.getRaumName(username));
        System.out.println("Hard Bot added");
    }

    @FXML
    void HandleLeave(ActionEvent event) throws IOException {
        if (playerUpdateTimer != null) {
            playerUpdateTimer.cancel();
        }
        HeartbeatManager.stop();
        sceneSwitcher.max_Scene(event, "/Lobby.fxml");

        raum.spielraumVerlassen(username, roomName);

        List<String> spieler = raum.getRaumSpieler(roomName);
        boolean onlyBotsLeft = spieler.stream().allMatch(name -> name.toLowerCase().contains("bot"));
        stopLeaderBoardUpdater();
        if (onlyBotsLeft) {
            for (String botName : spieler) {
                raum.spielraumVerlassen(botName, roomName);
            }
        }
    }

    /**
     * Opens the edit account screen.
     *
     * @param event the button action
     * @throws IOException if scene load fails
     */
    @FXML
    void edit_handle(ActionEvent event) throws IOException {
        sceneSwitcher.min_Scene(event, "/edit.fxml");
    }

    /**
     * Sends a chat message to the room.
     */
    @FXML
    void onSendChatMessage() {
        String message = message_input.getText().trim();
        if (!message.isEmpty()) {
            try {
                chat.post_message(roomName, message, username);
                message_input.clear();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Initializes the scene when loaded.
     */
    @FXML
    public void initialize() {
        try {
            roomName = raum.getRaumName(username);
            startChatUpdater();
            startPlayerUpdater();
            HeartbeatManager.start(raum, username);
            startGameStartChecker();
            playerNameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
            winsColumn.setCellValueFactory(new PropertyValueFactory<>("wins"));
            LeaderBoardTable.setItems(leaderboardData);
            startLeaderBoardUpdater();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void onStartGameClick(ActionEvent event) {
        try {
            String raumName = raum.getRaumName(username);

            if (!raum.raumVoll(raumName)) {
                Alert alert = new Alert(Alert.AlertType.WARNING, "3 Players needed to start the game.");
                alert.showAndWait();
                return;
            }

            game.spielStarten(raumName);
            GameStateDTO initialState = game.getGameState(raumName);
            GameService service = new GameService(game, username, raumName);
            service.initPlayerModels(initialState);

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gameboard.fxml"));
            Parent root = loader.load();
            GameBoardController controller = loader.getController();
            controller.setGameService(service);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
            stage.setMaximized(true);

            stopLeaderBoardUpdater();

        } catch (Exception e) {
            e.printStackTrace();
            Alert error = new Alert(Alert.AlertType.ERROR, "Error: " + e.getMessage());
            error.showAndWait();
        }
    }
    private void startLeaderBoardUpdater() {
        scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(() -> {
            try {
                List<LeaderboardEntryDTO> updated = raum.getLeaderboard();
                Platform.runLater(() -> {
                    leaderboardData.setAll(updated);
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, 0, 3, TimeUnit.SECONDS);
    }

    public void stopLeaderBoardUpdater() {
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdownNow();
        }
    }
    /**
     * Periodically updates the chat messages in the room.
     */
    private void startChatUpdater() {
        Timer timer = new Timer(true);
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> {
                    try {
                        List<ChatNachricht> messages = chat.get_messages(roomName);
                        write_message.clear();
                        for (ChatNachricht msg : messages) {
                            write_message.appendText(msg.toString() + "\n");
                        }
                        write_message.setScrollTop(Double.MAX_VALUE);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }
        }, 0, 1000);
    }

    /**
     * Periodically updates the player list in the room.
     * If the user has been removed from the room, returns to the lobby.
     */
    private void startPlayerUpdater() {
        playerUpdateTimer = new Timer(true);
        playerUpdateTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> {
                    try {
                        String raumName = raum.getRaumName(username);
                        if (raumName == null) {
                            playerUpdateTimer.cancel();
                            try {
                                sceneSwitcher.max_Scene(write_message.getScene(), "/Lobby.fxml");
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            return;
                        }
                        List<String> players = raum.getRaumSpieler(raumName);
                        PlayerUpdater(players);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                });
            }
        }, 0, 1000);
    }

    /**
     * Updates the UI with current player names.
     *
     * @param players list of player names in the room
     */
    void PlayerUpdater(List<String> players) {
        Spieler1Field.clear();
        Spieler2Field.clear();
        Spieler3Field.clear();

        Spieler1Field.setVisible(false);
        Spieler2Field.setVisible(false);
        Spieler3Field.setVisible(false);

        for (int i = 0; i < players.size(); i++) {
            switch (i) {
                case 0 -> {
                    Spieler1Field.setText(players.get(i));
                    Spieler1Field.setVisible(true);
                }
                case 1 -> {
                    Spieler2Field.setText(players.get(i));
                    Spieler2Field.setVisible(true);
                }
                case 2 -> {
                    Spieler3Field.setText(players.get(i));
                    Spieler3Field.setVisible(true);
                }
            }
        }
        updateHostControls();
    }

    private void startGameStartChecker() {
        gameStartTimer = new Timer(true);
        gameStartTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> {
                    try {
                        String raumName = raum.getRaumName(username);
                        if (game.spielGestartet(raumName)) {
                            gameStartTimer.cancel();

                            GameStateDTO initialState = game.getGameState(raumName);
                            GameService service = new GameService(game, username, raumName);
                            service.initPlayerModels(initialState);

                            //custom loading instead of SceneSwitching because I need to pass the GameService object
                            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gameboard.fxml"));
                            Parent root = loader.load();
                            GameBoardController controller = loader.getController();
                            controller.setGameService(service);

                            Stage stage = (Stage) startGameButton.getScene().getWindow();
                            Scene scene = new Scene(root);
                            stage.setScene(scene);
                            stage.setMaximized(true);

                        }
                    } catch (Exception ex) {
                    }
                });
            }
        }, 0, 1000);
    }

    /**
     * Enables/disables remove buttons based on host status.
     */
    void updateHostControls() {
        boolean isHost = username.equals(Spieler1Field.getText());
        spieler2RemoveBtn.setDisable(!isHost);
        spieler3RemoveBtn.setDisable(!isHost);
        startGameButton.setDisable(!isHost);
    }


}

