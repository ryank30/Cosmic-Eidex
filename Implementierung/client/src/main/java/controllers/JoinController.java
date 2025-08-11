package controllers;

import api.Raumverwaltung;
import app.RemoteServiceLocator;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

/**
 * Controller for handling user actions related to joining a game room.
 */
public class JoinController {

    private Raumverwaltung raum;

    @FXML
    public TextField room_name;
    @FXML
    public PasswordField password;
    @FXML
    public Button join_button;
    @FXML
    public Button cancel_button;
    @FXML
    public TextField error_field;

    public String username = AccountManager.getAccount().get_username();

    private final SceneSwitching sceneSwitcher = new SceneSwitching();

    /**
     * Constructs the JoinController and initializes RMI service references.
     *
     * @throws RemoteException if RMI communication fails
     * @throws NotBoundException if registry lookup fails
     */
    public JoinController() throws Exception {
        this.raum = RemoteServiceLocator.getRaum();
    }

    /**
     * Handles the event when the user attempts to join a game room.
     *
     * @param event the action event triggered by clicking the join button
     * @throws IOException if scene transition fails
     */
    @FXML
    void join_handle(ActionEvent event) throws IOException {
        String name = room_name.getText();
        String pass = password.getText();
        if (name.isEmpty()) {
            error_field.setVisible(true);
            error_field.setText("Room name can not be empty!");
        }
        raum.spielraumBeitreten(username, name, pass);
        sceneSwitcher.max_Scene(event, "/gameroom.fxml");
    }

    /**
     * Handles the cancel button click by returning the user to the Lobby.
     *
     * @param event the cancel button action event
     * @throws IOException if scene transition fails
     */
    @FXML
    void cancel_handle(ActionEvent event) throws IOException {
        sceneSwitcher.max_Scene(event, "/Lobby.fxml");
    }
}
