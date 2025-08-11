package controllers;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import api.Chatservice;
import api.Raumverwaltung;
import api.Spielverwaltung;
import chat.ChatNachricht;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import models.PlayerModel;
import models.StichModel;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import util.GameService;

import javafx.application.Platform;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

import controllers.GameBoardController;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import org.junit.jupiter.api.Assertions;


public class GameboardControllerTest {
    private Raumverwaltung mockRaum;
    private Spielverwaltung mockSpiel;
    private Chatservice mockChat;
    private GameService mockGameService;

    private GameBoardController controller;
    private VBox chatMessagesBox;
    private ScrollPane chatScrollPane;
    private Chatservice chatMock;

    @BeforeAll
    static void initJfx() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        Platform.startup(latch::countDown);
        latch.await();
    }

    @BeforeEach
    void setUp() throws Exception {

        mockRaum = mock(Raumverwaltung.class);
        mockSpiel = mock(Spielverwaltung.class);
        mockChat = mock(Chatservice.class);
        mockGameService = mock(GameService.class);


        controller = new GameBoardController(mockRaum, mockSpiel, mockChat);
        controller.setGameService(mockGameService);
        controller.score = new Label();
        controller.chatScrollPane = new ScrollPane();
        controller.leftOpponent = mock(PlayerModel.class);
        controller.rightOpponent = mock(PlayerModel.class);

        PlayerModel mockLocalPlayer = mock(PlayerModel.class);
        when(mockLocalPlayer.getName()).thenReturn("TestPlayer");
        controller.setLocalPlayer(mockLocalPlayer);
        when(controller.leftOpponent.handProperty()).thenReturn(new SimpleListProperty<>(FXCollections.observableArrayList()));
        when(controller.rightOpponent.handProperty()).thenReturn(new SimpleListProperty<>(FXCollections.observableArrayList()));

        controller.playerHand = new FlowPane();
        controller.leftHandPane = new StackPane();
        controller.rightHandPane = new StackPane();
        controller.playedCardsHbox = new HBox();
        controller.trumpCard = new Pane();
        controller.chatMessagesBox = new VBox();
        controller.chatInput = new TextField();
        controller.score = new Label();

    }

    @Test
    void testCheckFor3CardsInStichWindowCallsClearStich() {
        HBox hbox = new HBox();
        hbox.getChildren().addAll(new ImageView(), new ImageView(), new ImageView());
        controller.playedCardsHbox = hbox;

        controller.checkFor3CardsInStichWindow();

        verify(mockGameService, times(1)).clearStich();
    }

    @Test
    void testSendChatMessagePostsMessage() throws Exception {
        controller.chatInput = new TextField("Hello World");
        controller.localPlayer = new PlayerModel("Alice");
        controller.roomName = "TestRoom";

        controller.sendChatMessage();

        verify(mockChat).post_message("TestRoom", "Hello World", "Alice");
    }

    @Test
    void testSendChatMessageWithEmptyInputDoesNothing() throws Exception {
        controller.chatInput = new TextField("   ");
        controller.localPlayer = new PlayerModel("Alice");
        controller.roomName = "TestRoom";

        controller.sendChatMessage();

        verify(mockChat, never()).post_message(anyString(), anyString(), anyString());
    }

    @Test
    void testApplyGlowAddsEffect() {
        javafx.scene.layout.Pane pane = new javafx.scene.layout.Pane();
        assertNull(pane.getEffect());

        controller.applyGlow(pane);

        assertNotNull(pane.getEffect());
    }

    @Test
    void testRemoveGlowRemovesEffect() {
        javafx.scene.layout.Pane pane = new javafx.scene.layout.Pane();
        controller.applyGlow(pane);
        assertNotNull(pane.getEffect());

        controller.removeGlow(pane);
        assertNull(pane.getEffect());
    }

    @Test
    void testHandleCosmicAbilityCallsShowAlert() {
        GameBoardController spyController = spy(controller);
        doNothing().when(spyController).showAlert(anyString());

        spyController.handleCosmicAbility();

        verify(spyController).showAlert("Kosmische Fähigkeit aktiviert (Platzhalter).");
    }

    /*@Test
    void testUpdateStichUIAddsPlayedCards() {
        // Set up controller
        controller.playedCardsHbox = new HBox();
        controller.localPlayer = new PlayerModel("Alice");  // Set localPlayer BEFORE setGameService
        when(mockGameService.getPlayerModels()).thenReturn(Map.of(
                "Alice", controller.localPlayer,
                "Bob", new PlayerModel("Bob")
        ));

        // Set up an observable map for the trick
        ObservableMap<String, String> observableTrick = FXCollections.observableHashMap();
        observableTrick.put("Bob", "card1");
        observableTrick.put("Alice", "card2");

        StichModel mockStich = mock(StichModel.class);
        when(mockStich.getCardsInTrick()).thenReturn(observableTrick);

        controller.stich = mockStich;

        // Run the method
        controller.updateStichUI();

        // Assert
        assertEquals(2, controller.playedCardsHbox.getChildren().size());
    }

     */


    @Test
    void testOnClickLeaveGameCallsEndGame() throws Exception {
        controller.onClickLeaveGame();

        verify(mockGameService).endGame();
    }
    @Test
    void testInitializeSetsChatPaddingAndStartsChatUpdater() {
        controller.chatMessagesBox = new VBox();
        controller.chatScrollPane = new ScrollPane();
        controller.initialize();

        Insets padding = controller.chatMessagesBox.getPadding();
        assertEquals(new Insets(5, 5, 5, 5), padding);
        assertNotNull(controller.chatUpdateTimer);
    }

    @Test
    void testMakeGameModeLabel_Obenabe() throws RemoteException {
        when(mockSpiel.getGameMode(anyString())).thenReturn("OBENABE");
        when(mockGameService.getGame()).thenReturn(mockSpiel);
        controller.trumpCard = new Pane();
        controller.raum = mockRaum;
        when(mockRaum.getRaumName(anyString())).thenReturn("room");
        controller.roomName = "room";
        controller.localPlayer = new PlayerModel("TestPlayer");

        controller.makeGameModeLabel();

        Label label = (Label) controller.trumpCard.getChildren().get(0);
        assertTrue(label.getText().contains("Obenabe"));
    }

    @Test
    void testSetLocalPlayerSetsCorrectPlayer() {
        PlayerModel player = new PlayerModel("Test");
        controller.setLocalPlayer(player);
        assertEquals("Test", controller.localPlayer.getName());
    }


}