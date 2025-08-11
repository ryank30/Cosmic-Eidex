package controllers;

import account.Account;
import api.Raumverwaltung;
import api.Spielverwaltung;
import api.Chatservice;
import javafx.scene.control.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javafx.event.ActionEvent;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.framework.junit5.ApplicationExtension;

@ExtendWith(ApplicationExtension.class)
public class GameroomControllerTest {

    private GameRoomController controller;

    private Raumverwaltung mockRaum;
    private Spielverwaltung mockSpiel;
    private Chatservice mockChat;

    @BeforeAll
    public static void initToolkit() {
        try {
            Platform.startup(() -> {});
        } catch (IllegalStateException e) {}
    }


    @BeforeEach
    public void setUp() {
        mockRaum = mock(Raumverwaltung.class);
        mockSpiel = mock(Spielverwaltung.class);
        mockChat = mock(Chatservice.class);

        Account mockAccount = mock(Account.class);
        when(mockAccount.get_username()).thenReturn("TestUser");
        AccountManager.setAccount(mockAccount);

        controller = new GameRoomController(mockRaum, mockSpiel, mockChat);

        controller.Spieler1Field = new TextField();
        controller.Spieler2Field = new TextField();
        controller.Spieler3Field = new TextField();
        controller.spieler2RemoveBtn = new Button();
        controller.spieler3RemoveBtn = new Button();
        controller.startGameButton = new Button();
    }
    @Test
    public void testIsCurrentUserHost_1() {
        controller.Spieler1Field.setText(AccountManager.getAccount().get_username());
        assertTrue(controller.isCurrentUserHost());
    }

    @Test
    public void testIsCurrentUserHost_2() {
        controller.Spieler1Field.setText("OtherUser");
        assertFalse(controller.isCurrentUserHost());
    }

    @Test
    public void testOnRemoveSpieler2() throws RemoteException {
        controller.Spieler2Field.setText("PlayerTwo");
        when(mockRaum.getRaumName(any())).thenReturn("RoomX");

        controller.onRemoveSpieler2();

        assertEquals("", controller.Spieler2Field.getText());
        assertFalse(controller.Spieler2Field.isVisible());
        assertTrue(controller.Spieler2Field.isDisabled());
        verify(mockRaum).spielraumVerlassen(eq("PlayerTwo"), eq("RoomX"));
    }

    @Test
    public void testPlayerUpdater() {
        List<String> players = Arrays.asList("Alice", "Bob", "Charlie");

        controller.PlayerUpdater(players);

        assertEquals("Alice", controller.Spieler1Field.getText());
        assertEquals("Bob", controller.Spieler2Field.getText());
        assertEquals("Charlie", controller.Spieler3Field.getText());

        assertTrue(controller.Spieler1Field.isVisible());
        assertTrue(controller.Spieler2Field.isVisible());
        assertTrue(controller.Spieler3Field.isVisible());
    }

    @Test
    public void testUpdateHostControls_1() {
        controller.Spieler1Field.setText(AccountManager.getAccount().get_username());

        controller.updateHostControls();

        assertFalse(controller.spieler2RemoveBtn.isDisabled());
        assertFalse(controller.spieler3RemoveBtn.isDisabled());
        assertFalse(controller.startGameButton.isDisabled());
    }

    @Test
    public void testUpdateHostControls_2() {
        controller.Spieler1Field.setText("AnotherPlayer");

        controller.updateHostControls();

        assertTrue(controller.spieler2RemoveBtn.isDisabled());
        assertTrue(controller.spieler3RemoveBtn.isDisabled());
        assertTrue(controller.startGameButton.isDisabled());
    }
    @Test
    public void testOnRemoveSpieler3() throws RemoteException {
        controller.Spieler3Field.setText("PlayerThree");
        when(mockRaum.getRaumName(any())).thenReturn("RoomY");

        controller.onRemoveSpieler3();

        assertEquals("", controller.Spieler3Field.getText());
        assertFalse(controller.Spieler3Field.isVisible());
        assertTrue(controller.Spieler3Field.isDisabled());
        verify(mockRaum).spielraumVerlassen(eq("PlayerThree"), eq("RoomY"));
    }

    @Test
    public void testHandleAddEasyBot() throws RemoteException {
        when(mockRaum.getRaumName(any())).thenReturn("TestRoom");

        controller.handleAddEasyBot();

        verify(mockRaum).easyBotHinzufuegen("TestRoom");
    }

    @Test
    public void testHandleAddHardBot() throws RemoteException {
        when(mockRaum.getRaumName(any())).thenReturn("TestRoom");

        controller.handleAddHardBot();

        verify(mockRaum).hardBotHinzufuegen("TestRoom");
    }

    @Test
    public void testOnSendChatMessage_1() throws Exception {
        controller.message_input = new TextField("Hello world");
        controller.write_message = new TextArea();
        controller.roomName = "RoomZ";

        controller.onSendChatMessage();

        assertEquals("", controller.message_input.getText());
        verify(mockChat).post_message("RoomZ", "Hello world", AccountManager.getAccount().get_username());
    }

    @Test
    public void testOnSendChatMessage_2() {
        controller.message_input = new TextField("   ");
        controller.write_message = new TextArea();
        controller.roomName = "RoomZ";

        controller.onSendChatMessage();

        verifyNoInteractions(mockChat);
    }

    @Test
    public void testEditHandle() throws Exception {
        SceneSwitching mockSwitcher = mock(SceneSwitching.class);
        controller = new GameRoomController(mockRaum, mockSpiel, mockChat);
        controller.Spieler1Field = new TextField();
        controller.sceneSwitcher = mockSwitcher;

        javafx.event.ActionEvent event = mock(String.valueOf(ActionEvent.class));
        controller.edit_handle(event);

        verify(mockSwitcher).min_Scene(eq(event), eq("/edit.fxml"));
    }

    @Test
    public void testHandleLeave() throws Exception {
        when(mockRaum.getRaumName(any())).thenReturn("RoomTest");
        when(mockRaum.getRaumSpieler("RoomTest")).thenReturn(new ArrayList<>(List.of("bot_1", "bot_2")));


        controller.roomName = "RoomTest";
        controller.sceneSwitcher = mock(SceneSwitching.class);

        javafx.event.ActionEvent event = mock(String.valueOf(ActionEvent.class));

        controller.HandleLeave(event);

        verify(mockRaum).spielraumVerlassen("TestUser", "RoomTest");
        verify(mockRaum, times(2)).spielraumVerlassen(contains("bot"), eq("RoomTest"));
        verify(controller.sceneSwitcher).max_Scene(eq(event), eq("/Lobby.fxml"));
    }

    @Test
    public void testOnStartGameClick_roomNotFull() throws Exception {
        when(mockRaum.getRaumName(any())).thenReturn("TestRoom");
        when(mockRaum.raumVoll("TestRoom")).thenReturn(false);

        javafx.event.ActionEvent mockEvent = mock(ActionEvent.class);

        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            try {
                controller.onStartGameClick(mockEvent);
            } finally {
                latch.countDown();
            }
        });

        latch.await();
        verify(mockRaum).raumVoll("TestRoom");
    }

}