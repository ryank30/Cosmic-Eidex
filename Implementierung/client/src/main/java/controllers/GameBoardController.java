package controllers;

import api.Chatservice;
import api.Raumverwaltung;
import api.Spielverwaltung;
import app.RemoteServiceLocator;
import chat.ChatNachricht;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.ListChangeListener;
import javafx.collections.MapChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import models.PlayerModel;
import models.StichModel;
import util.GameService;
import java.io.InputStream;
import java.rmi.RemoteException;
import java.util.*;

/**
 * Fully functional controller for the GameBoard.
 * Handles card play, turn control, score display, and basic chat logic.
 */
public class GameBoardController {

    // Bottom section
    @FXML
    Label score;
    @FXML
    Button leaveGame;
    @FXML
    Button cosmicPower;

    // Chat section (right pane)
    @FXML
    ScrollPane chatScrollPane;
    @FXML
    TextField chatInput;
    @FXML
    VBox chatMessagesBox;
    private Chatservice chat;
    String roomName;
    Timer chatUpdateTimer;

    // Player's hand (bottom FlowPane)
    @FXML
    FlowPane playerHand;

    // Center area (maybe current round info)
    @FXML
    Pane trumpCard;
    @FXML
    HBox playedCardsHbox;

    // Left and right side VBox (possibly opponent areas)
    @FXML
    VBox opponentLeft;
    @FXML
    Label winpointsLeft;
    @FXML
    StackPane leftHandPane;
    @FXML
    VBox opponentRight;
    @FXML
    Label winpointsRight;
    @FXML
    StackPane rightHandPane;

    GameService gameService;
    PlayerModel localPlayer;
    PlayerModel leftOpponent;
    PlayerModel rightOpponent;
    StichModel stich;

    private Spielverwaltung game;
    Raumverwaltung raum;

    /**
     *Constructor with dependency injection for testing or manual instantiation.
     * @throws Exception
     */
    public GameBoardController() throws Exception {
        chat = RemoteServiceLocator.getChat();
        this.raum = RemoteServiceLocator.getRaum();
        this.game = RemoteServiceLocator.getSpiel();
    }
    public GameBoardController(Raumverwaltung raum, Spielverwaltung game, Chatservice chat) {
        this.raum = raum;
        this.game = game;
        this.chat = chat;

    }
    @FXML
    public void initialize() {
        startChatUpdater();
        chatMessagesBox.setPadding(new Insets(5, 5, 5, 5));
    }

    private void initializeAfterInjectionOfService() {
        try {

            if (gameService != null) {
                localPlayer = gameService.getLocalPlayer();
                this.leftOpponent = gameService.getLeftOpponent();
                this.rightOpponent = gameService.getRightOpponent();
                this.stich = gameService.getStich();
                setupBindings();
                this.roomName = this.raum.getRaumName(localPlayer.getName());
                makeGameModeLabel();
                gameService.setGameBoardController(this);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * Establishes data bindings between model properties and UI components.
     */
    private void setupBindings() {
        score.textProperty().bind(
                Bindings.createStringBinding(
                        () -> String.format("Punkte %d, Gewinnpunkte %d",
                                localPlayer.getScore(),
                                localPlayer.getWinPoints()),
                        localPlayer.scoreProperty(),
                        localPlayer.winPointsProperty()
                )
        );
        winpointsLeft.textProperty().bind(
                Bindings.concat("Gewinnpunkte: ")
                        .concat(Bindings.format("%d", leftOpponent.winPointsProperty()))
        );
        winpointsRight.textProperty().bind(
                Bindings.concat("Gewinnpunkte: ")
                        .concat(Bindings.format("%d", rightOpponent.winPointsProperty()))
        );
        localPlayer.handProperty().addListener((ListChangeListener<String>) change -> {
            while (change.next()) {
                if (change.wasAdded() || change.wasRemoved() || change.wasReplaced()) {
                    Platform.runLater(this::updateHandUI);
                }
            }
        });
        leftOpponent.handProperty().addListener((ListChangeListener<String>) change -> {
            while (change.next()) {
                if (change.wasAdded() || change.wasRemoved() || change.wasReplaced()) {
                    Platform.runLater(this::updateLeftOpponentUI);
                }
            }
        });
        rightOpponent.handProperty().addListener((ListChangeListener<String>) change -> {
            while (change.next()) {
                if (change.wasAdded() || change.wasRemoved() || change.wasReplaced()) {
                    Platform.runLater(this::updateRightOpponentUI);
                }
            }
        });
        stich.getCardsInTrick().addListener((MapChangeListener<String, String>) change -> {
            Platform.runLater(() -> {
                if (change.wasRemoved() || change.wasAdded()) {
                    updateStichUI();
                }
            });
        });
        localPlayer.isMyTurnProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                Platform.runLater(this::updateHandGlow);
            } else {
                Platform.runLater(this::removeHandGlow);
            }
        });
        leftOpponent.isMyTurnProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                applyGlow(opponentLeft);
            } else {
                removeGlow(opponentLeft);
            }
        });
        rightOpponent.isMyTurnProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                applyGlow(opponentRight);
            } else {
                removeGlow(opponentRight);
            }
        });


    }

    /**
     * * Updates the local player's hand display with current cards.
     */
    void updateHandUI() {
        playerHand.getChildren().clear();
        for (String cardId : localPlayer.getHand()) {
            ImageView imageView = loadCardImage(cardId);
            if (imageView != null) {
                playerHand.getChildren().add(imageView);
            }
        }
    }

    /**
     * /**
     * Loads and creates an ImageView for a specific card.
     * @param cardId the unique identifier of the card to load.
     * @return an ImageView of the card, or null if the image  cannot be loaded.
     */
    private ImageView loadCardImage(String cardId) {
        String imagePath = "/cards/"+cardId+".png";

        try (InputStream stream = getClass().getResourceAsStream(imagePath)) {
            if (stream == null) {
                return null;
            }

            Image image = new Image(stream);

            return createCardImageView(image, cardId);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static final int CARD_WIDTH = 111;
    private static final int CARD_HEIGHT = 171;

    /**
     * Creates a configured ImageView for a card with click handling.
     * @param image the loaded card image.
     * @param cardId the unique identifier of the card.
     * @return a configured ImageView ready for display.
     */
    private ImageView createCardImageView(Image image, String cardId) {
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(CARD_WIDTH);
        imageView.setFitHeight(CARD_HEIGHT);
        imageView.setPreserveRatio(true);
        imageView.setId(cardId);
        imageView.setOnMouseClicked(event -> {
            try {
                game.clientPlayCard(roomName, localPlayer.getName(), cardId);
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        });
        return imageView;
    }

    private static final String CARD_BACK_IMAGE_LEFT = "/cards/backOfCardLeft.png";
    private static final String CARD_BACK_IMAGE_RIGHT = "/cards/backOfCardRight.png";
    private static final double CARD_BACK_WIDTH = 171;
    private static final double CARD_BACK_HEIGHT = 111;
    private static final double CARD_OVERLAP = 90; // pixels to overlap cards
    /**
     * Creates an ImageView for opponent card backs.
     * @param leftOrRight path to the appropriate card back image
     * @return an ImageView configured for opponent card display
     */
    private ImageView createOpponentCardBack(String leftOrRight) {

        Image cardBack = new Image(getClass().getResourceAsStream(leftOrRight));
        ImageView imageView = new ImageView(cardBack);
        imageView.setFitWidth(CARD_BACK_WIDTH);
        imageView.setFitHeight(CARD_BACK_HEIGHT);
        imageView.setPreserveRatio(true);

        return imageView;
    }
    /**
     * Updates the left opponent's card display with current hand size.
     */
    void updateLeftOpponentUI() {

        leftHandPane.getChildren().clear();

        PlayerModel leftPlayer = gameService.getLeftOpponent();

        if (leftPlayer == null) {
            return;
        }
        int cardCount = leftPlayer.handProperty().size();

        for (int i = 0; i < cardCount; i++) {
            ImageView cardBack = createOpponentCardBack(CARD_BACK_IMAGE_LEFT);
            double translateY = i * (CARD_WIDTH - CARD_OVERLAP);
            cardBack.setTranslateY(translateY);
            leftHandPane.getChildren().add(cardBack);
        }

    }

    /**
     * /**
     * Updates the left opponent's card display with current hand size.
     */
    void updateRightOpponentUI() {

        rightHandPane.getChildren().clear();

        PlayerModel rightPlayer = gameService.getRightOpponent();

        if (rightPlayer == null) {

            return;
        }

        int cardCount = rightPlayer.handProperty().size();

        for (int i = 0; i < cardCount; i++) {
            ImageView cardBack = createOpponentCardBack(CARD_BACK_IMAGE_RIGHT);
            double translateY = i * (CARD_WIDTH - CARD_OVERLAP);
            cardBack.setTranslateY(translateY);
            rightHandPane.getChildren().add(cardBack);
        }

    }


    /**
     * Creates and displays the game mode label in the trump card area.
     * @throws RemoteException if the communication with the game fails.
     */
    void makeGameModeLabel() throws RemoteException {
        game = gameService.getGame();
        String mode = game.getGameMode(roomName);
        trumpCard.getChildren().clear();

        String text = switch (mode) {
            case "OBENABE" -> "Spielmodus: Obenabe";
            case "UNDENUFE" -> "Spielmodus: Undenufe";
            default -> "Trumpffarbe ist: " + switch(game.getTrumpSuit(roomName)) {
                case "STARS" -> "Sterne";
                case "RAVENS" -> "Raben";
                case "LIZARDS" -> "Eidechsen";
                case "HEARTS" -> "Herzen";
                default -> game.getTrumpSuit(roomName);
            };
        };
        Label label = new Label(text);
        label.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        label.setAlignment(Pos.CENTER);
        label.setMaxWidth(Double.MAX_VALUE);
        label.setMaxHeight(Double.MAX_VALUE);

        trumpCard.getChildren().add(label);
    }
    /**
     * Updates the display of cards currently played in the trick.
     */
    public void updateStichUI() {
        playedCardsHbox.getChildren().clear();

        for (Map.Entry<String, String> entry : stich.getCardsInTrick().entrySet()) {
            String cardId = entry.getValue();
            Image image = new Image("/cards/" + cardId + ".png");
            ImageView imageView = new ImageView(image);
            imageView.setFitWidth(111);
            imageView.setFitHeight(171);
            imageView.setPreserveRatio(true);
            playedCardsHbox.getChildren().add(imageView);
        }
    }

    @FXML
    void handleCosmicAbility() {
        showAlert("Kosmische Fähigkeit aktiviert (Platzhalter)." );
    }
    /**
     * Handles the leave room button click event.
     */
    @FXML
    private void handleLeaveRoom() {
        try {
            // TODO: Raum verlassen und Szene wechseln
            showAlert("Raum verlassen (Szenewechsel nicht implementiert).");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * Displays an informational alert dialog with the specified message.
     *
     * @param msg the message to display in the alert
     */
    void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, msg);
        alert.showAndWait();
    }
    /**
     * Sets the GameService and triggers complete initialization.
     * @param service the GameService instance to use
     */
    public void setGameService(GameService service) {
        this.gameService = service;
        initializeAfterInjectionOfService();
    }
    /**
     * Switches to the win screen displaying final game results.

     */
    public void switchWinScreen (){
        Platform.runLater(() -> {
            Map<String, PlayerModel> playerModels = gameService.getPlayerModels();
            List<PlayerModel> sortedPlayers = new ArrayList<>(playerModels.values());
            sortedPlayers.sort((p1, p2) -> Integer.compare(p2.getWinPoints(), p1.getWinPoints()));
            PlayerModel winner = sortedPlayers.get(0);
            PlayerModel second = sortedPlayers.get(1);
            PlayerModel third = sortedPlayers.get(2);

            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/win.fxml"));
                Parent root = loader.load();
                WinController controller = loader.getController();
                controller.setPlayers(
                        winner.getName(),
                        second.getName(),
                        third.getName()
                );
                controller.setPoints(
                        winner.getWinPoints(),
                        second.getWinPoints(),
                        third.getWinPoints()
                );
                controller.initializeAfterInjection();
                //silent point of failure if playedcardshbox is null IMPORTANT
                Stage stage = (Stage) (playedCardsHbox).getScene().getWindow();
                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.setMaximized(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

    }

    public void checkFor3CardsInStichWindow () {
         if (playedCardsHbox.getChildren().size() == 3) {
             gameService.clearStich();
        }
    }
    /**
     * Handles the leave game button click event.
     * @throws Exception if the game ending process fails
     */
    @FXML
    public void onClickLeaveGame () throws Exception {
        gameService.endGame();
    }
    /**
     * Applies a green glow effect to indicate turn or valid actions.
     * @param node the UI node to apply the glow effect to
     */
    void applyGlow(Node node) {
        DropShadow greenGlow = new DropShadow();
        greenGlow.setColor(Color.LIMEGREEN);
        greenGlow.setRadius(20);
        greenGlow.setSpread(0.4);
        greenGlow.setOffsetX(0);
        greenGlow.setOffsetY(0);

        node.setEffect(greenGlow);
    }
    /**
     * Updates glow effects on player's hand based on card validity.

     */
    private void updateHandGlow() {
        for (Node node : playerHand.getChildren()) {
            if (node instanceof ImageView imageView) {
                String cardId = imageView.getId();
                if (localPlayer.getValidCard().contains(cardId)) {
                    applyGlow(imageView);
                } else {
                    removeGlow(imageView);
                }
            }
        }
    }
    /**
     * Removes all glow effects from the player's hand.
     */
    private void removeHandGlow(){
        for (Node node : playerHand.getChildren()) {
            if (node instanceof ImageView imageView) {
                removeGlow(imageView);
            }
        }
    }
/**
        * Removes any applied glow effect from the specified UI node.
            *
            * @param node the UI node to remove effects from
     */
    void removeGlow(Node node) {
        node.setEffect(null);
    }

    /**
     * Handles sending a chat message when the user presses enter or clicks send.
     */
    @FXML
    void sendChatMessage() {
        String message = chatInput.getText().trim();
        if (!message.isEmpty()) {
            try {
                chat.post_message(roomName, message, localPlayer.getName());
                chatInput.clear();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Starts the periodic chat message update timer.
     */
    private void startChatUpdater() {
        chatUpdateTimer = new Timer(true);
        chatUpdateTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> {
                    try {
                        List<ChatNachricht> messages = chat.get_messages(roomName);
                        chatMessagesBox.getChildren().clear();
                        for (ChatNachricht msg : messages) {
                            Label label = new Label(msg.getSender() + ": " + msg.getContent());
                            label.setWrapText(true);
                            label.setStyle("-fx-background-color: #f0f0f0; -fx-padding: 4 8; -fx-background-radius: 6;");
                            chatMessagesBox.getChildren().add(label);
                        }
                        chatScrollPane.setVvalue(1.0);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }
        }, 0, 1000);
    }

    /**
     * Sets the local player model for this controller.
     * @param player the PlayerModel to use as a template for the local player
     */
    public void setLocalPlayer(PlayerModel player) {
        this.localPlayer = new PlayerModel(player.getName());
    }
}