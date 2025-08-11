package controllers;

import java.io.IOException;
import java.rmi.registry.Registry;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import app.RemoteServiceLocator;
import dto.GameroomDTO;
import api.Zugriffsverwaltung;
import chat.ChatNachricht;
import api.Chatservice;
import dto.LeaderboardEntryDTO;
import javafx.scene.input.MouseButton;
import models.GameroomFX;
import api.Spielverwaltung;
import api.Raumverwaltung;
import util.HeartbeatManager;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

/**
 * Controller class for the lobby view. Handles room list updates,
 * global chat, and navigation to other scenes like join/create/edit/delete.
 */
public class LobbyController {
    Zugriffsverwaltung account;
    Raumverwaltung lobby;
    Chatservice chat;
    Spielverwaltung spiel;

    String username = AccountManager.getAccount().get_username();
    private String roomName = "global";

    @FXML
    Button join_button;
    @FXML private Button create_button;
    @FXML private Button logout_button;
    @FXML private Button delete_button;
    @FXML private Button edit_button;
    @FXML
    TextArea write_message;
    @FXML
    TextField message_input;

    @FXML
    TableView<GameroomFX> rooms;
    @FXML public TableColumn<GameroomFX, String> room_name;
    @FXML public TableColumn<GameroomFX, String> player_1;
    @FXML public TableColumn<GameroomFX, String> player_2;
    @FXML public TableColumn<GameroomFX, String> player_3;

    private final SceneSwitching sceneSwitcher = new SceneSwitching();

    private static ObservableList<GameroomFX> gamerooms;
    @FXML public ListView<?> bestenliste;

    @FXML public TableView<LeaderboardEntryDTO> bestliste;
    @FXML public TableColumn<LeaderboardEntryDTO, String> usernameColumn;
    @FXML public TableColumn<LeaderboardEntryDTO, Integer> winsColumn;
    private final ObservableList<LeaderboardEntryDTO> leaderboardData = FXCollections.observableArrayList();
    ScheduledExecutorService scheduler;

    /**
     * Constructs the LobbyController and sets up RMI references.
     *
     * @throws Exception if registry lookup fails
     */
    public LobbyController() throws Exception {
        this.account = RemoteServiceLocator.getZugriff();
        this.lobby = RemoteServiceLocator.getRaum();
        this.spiel = RemoteServiceLocator.getSpiel();
        chat = RemoteServiceLocator.getChat();
    }

    /**
     * Initializes the lobby view, sets up chat and room list updates.
     */
    @FXML
    public void initialize() {
        gamerooms = FXCollections.observableArrayList();
        rooms.setItems(gamerooms);

        room_name.setCellValueFactory(new PropertyValueFactory<>("room_name"));
        player_1.setCellValueFactory(new PropertyValueFactory<>("player_1"));
        player_2.setCellValueFactory(new PropertyValueFactory<>("player_2"));
        player_3.setCellValueFactory(new PropertyValueFactory<>("player_3"));


        //double click now makes you join room
        rooms.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                GameroomFX selectedRoom = rooms.getSelectionModel().getSelectedItem();
                if (selectedRoom != null) {
                    try {
                        joinRoom(selectedRoom.getRoom_name(), ""); //should we only allow direct joining with no password?
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        startChatUpdater();
        startRoomUpdater();
        HeartbeatManager.start(lobby, username);
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        winsColumn.setCellValueFactory(new PropertyValueFactory<>("wins"));
        bestliste.setItems(leaderboardData);
        startAutoUpdate();
    }

    /**
     * Periodically updates the global chat messages.
     */
    private void startChatUpdater() {
        Timer timer = new Timer(true);
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> {
                    try {
                        List<ChatNachricht> messages = chat.get_messages("global");
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
     * Periodically updates the game room list.
     */
    private void startRoomUpdater() {
        Timer timer = new Timer(true);
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> {
                    try {
                        List<GameroomDTO> serverRooms = lobby.getRooms();
                        List<GameroomFX> convertedList = new ArrayList<>();

                        for (GameroomDTO room : serverRooms) {
                            convertedList.add(new GameroomFX(
                                    room.getRoom_name(),
                                    room.getPlayer_1(),
                                    room.getPlayer_2(),
                                    room.getPlayer_3()
                            ));
                        }

                        gamerooms.setAll(convertedList);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }
        }, 0, 2000);
    }

    /**
     * Handles the create room button event.
     *
     * @param event button click event
     * @throws IOException if scene switch fails
     */
    @FXML
    void create_handle(ActionEvent event) throws IOException {
        sceneSwitcher.min_Scene(event, "/create.fxml");
        stopAutoUpdate();
    }

    /**
     * Handles the join room button event.
     *
     * @param event button click event
     * @throws IOException if scene switch fails
     */
    @FXML
    void join_handle(ActionEvent event) throws IOException {
        sceneSwitcher.min_Scene(event, "/join.fxml");
        stopAutoUpdate();
    }

    /**
     * Handles the delete account button event.
     *
     * @param event button click event
     * @throws IOException if scene switch fails
     */
    @FXML
    void delete_handle(ActionEvent event) throws IOException {
        sceneSwitcher.min_Scene(event, "/delete.fxml");
        stopAutoUpdate();
    }

    /**
     * Logs out the user and returns to the login screen.
     *
     * @param event button click event
     * @throws IOException if logout or scene switch fails
     */
    @FXML
    void logout_handle(ActionEvent event) throws IOException {
        account.logout(username);
        sceneSwitcher.min_Scene(event, "/login.fxml");
        HeartbeatManager.stop();
        stopAutoUpdate();
    }

    /**
     * Handles the edit account button event.
     *
     * @param event button click event
     * @throws IOException if scene switch fails
     */
    @FXML
    void edit_handle(ActionEvent event) throws IOException {
        sceneSwitcher.min_Scene(event, "/edit.fxml");
        stopAutoUpdate();
    }

    /**
     * Sends a chat message to the global chat room.
     */
    @FXML
    void send_message() {
        try {
            String message = message_input.getText().trim();
            if (!message.isEmpty()) {
                chat.post_message("global", message, username);
                write_message.appendText(username + ": " + message + "\n");
                message_input.clear();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void startAutoUpdate() {
        scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(() -> {
            try {
                List<LeaderboardEntryDTO> updated = lobby.getLeaderboard();  // Already sorted & limited
                Platform.runLater(() -> {
                    leaderboardData.setAll(updated);  // UI update on FX thread
                });
            } catch (Exception e) {
                System.err.println("Leaderboard update failed: " + e.getMessage());
                e.printStackTrace();
            }
        }, 0, 3, TimeUnit.SECONDS);
    }

    public void stopAutoUpdate() {
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdownNow();
        }
    }

    private void joinRoom(String roomName, String password) throws IOException {
        try {
            lobby.spielraumBeitreten(username, roomName, password);

            sceneSwitcher.max_Scene(join_button.getScene(), "/gameroom.fxml");

            stopAutoUpdate();
        } catch (Exception e) {
            System.err.println("Failed to join room: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
