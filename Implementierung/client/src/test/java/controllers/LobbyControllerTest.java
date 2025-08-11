package controllers;

import account.Account;
import api.Chatservice;
import api.Raumverwaltung;
import api.Spielverwaltung;
import api.Zugriffsverwaltung;
import chat.ChatNachricht;
import dto.GameroomDTO;
import dto.LeaderboardEntryDTO;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import models.GameroomFX;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import app.RemoteServiceLocator;
import org.testfx.framework.junit5.ApplicationExtension;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(ApplicationExtension.class)
class LobbyControllerTest {

    private LobbyController controller;

    // Mocks
    private Zugriffsverwaltung mockAccount;
    private Raumverwaltung mockLobby;
    private Chatservice mockChat;
    private Spielverwaltung mockSpiel;

    private LeaderboardEntryDTO leaderboardEntry;
    private GameroomDTO roomDTO;

    @BeforeAll
    static void initFX() {
        try {
            Platform.startup(() -> {});
        } catch (IllegalStateException e) {
            // Platform already started
        }
    }


    @BeforeEach
    void setup() throws Exception {
        mockAccount = mock(Zugriffsverwaltung.class);
        mockLobby = mock(Raumverwaltung.class);
        mockChat = mock(Chatservice.class);
        mockSpiel = mock(Spielverwaltung.class);

        try (
                MockedStatic<AccountManager> accountManagerStatic = mockStatic(AccountManager.class);
                MockedStatic<RemoteServiceLocator> remoteLocator = mockStatic(RemoteServiceLocator.class)
        ) {
            Account mockAcc = mock(Account.class);
            when(mockAcc.get_username()).thenReturn("TestUser");
            accountManagerStatic.when(AccountManager::getAccount).thenReturn(mockAcc);

            remoteLocator.when(RemoteServiceLocator::getZugriff).thenReturn(mockAccount);
            remoteLocator.when(RemoteServiceLocator::getRaum).thenReturn(mockLobby);
            remoteLocator.when(RemoteServiceLocator::getSpiel).thenReturn(mockSpiel);
            remoteLocator.when(RemoteServiceLocator::getChat).thenReturn(mockChat);

            controller = new LobbyController();
        }

        controller.message_input = new TextField();
        controller.write_message = new TextArea();

        controller.rooms = new TableView<>();
        controller.room_name = new TableColumn<>();
        controller.player_1 = new TableColumn<>();
        controller.player_2 = new TableColumn<>();
        controller.player_3 = new TableColumn<>();

        controller.bestliste = new TableView<>();
        controller.usernameColumn = new TableColumn<>();
        controller.winsColumn = new TableColumn<>();

        controller.join_button = new Button();
        //controller.join_button.setScene(new Scene(new StackPane(), 300, 200)); // Needed for max_Scene()
    }

    @Test
    void testSendMessage_success() throws Exception {
        controller.message_input.setText("Hello");
        controller.send_message();

        verify(mockChat).post_message("global", "Hello", "TestUser");
        assertTrue(controller.write_message.getText().contains("Hello"));
        assertEquals("", controller.message_input.getText());
    }

    @Test
    void testSendMessage_emptyMessage() {
        controller.message_input.setText("   ");
        controller.send_message();
        verifyNoInteractions(mockChat);
    }


    @Test
    void testInitialize_setsUpColumnsAndAutoUpdate() throws Exception {
        controller.initialize();

        assertNotNull(controller.rooms.getItems());
        assertNotNull(controller.bestliste.getItems());
        assertNotNull(controller.room_name.getCellValueFactory());
        assertNotNull(controller.usernameColumn.getCellValueFactory());
        assertNotNull(controller.winsColumn.getCellValueFactory());
    }

    @Test
    void testStopAutoUpdate_shutsDownExecutor() {
        controller.initialize();
        controller.stopAutoUpdate();
        assertTrue(controller.scheduler.isShutdown());
    }

    @Test
    void testLeaderboardAutoUpdate_fetchesData() throws Exception {
        leaderboardEntry = new LeaderboardEntryDTO("TestUser", 5);
        when(mockLobby.getLeaderboard()).thenReturn(List.of(leaderboardEntry));

        controller.initialize();
        TimeUnit.SECONDS.sleep(4); // Wait for scheduler to trigger at least once

        Platform.runLater(() -> {
            assertEquals(1, controller.bestliste.getItems().size());
            assertEquals("TestUser", controller.bestliste.getItems().get(0).getUsername());
        });
    }

    @Test
    void testDoubleClickRoomJoin_callsJoinRoom() throws Exception {
        GameroomFX roomFX = new GameroomFX("Room1", "P1", "P2", "P3");
        controller.initialize();
        Platform.runLater(() -> {
            controller.rooms.getItems().add(roomFX);
            controller.rooms.getSelectionModel().select(roomFX);

            MouseEvent doubleClick = new MouseEvent(MouseEvent.MOUSE_CLICKED,
                    0, 0, 0, 0, MouseButton.PRIMARY,
                    2, false, false, false, false,
                    true, false, false, true,
                    false, false, null);

            controller.rooms.fireEvent(doubleClick);
        });

        TimeUnit.MILLISECONDS.sleep(500);
    }

}
