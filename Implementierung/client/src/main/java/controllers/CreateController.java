package controllers;

import app.RemoteServiceLocator;
import exceptions.WrongPassword;
import api.Raumverwaltung;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

/**
 * Controller class for creating a new game room.
 * Handles user input for room name and password and communicates with the RMI server.
 */
public class CreateController {
    private Raumverwaltung raum;
    private String user = AccountManager.getAccount().get_username();

    @FXML
    public TextField room_name;

    @FXML
    public PasswordField password;

    @FXML
    public TextField error_field;

    private final SceneSwitching sceneSwitcher = new SceneSwitching();

    /**
     * Initializes the controller by connecting to RMI registry and retrieving remote interfaces.
     *
     * @throws RemoteException if RMI communication fails
     * @throws NotBoundException if server objects are not found in the registry
     */
    public CreateController() throws Exception {
        this.raum = RemoteServiceLocator.getRaum();
    }

    /**
     * Handles the create button click event. Tries to create a new game room.
     *
     * @param event the ActionEvent triggered by the button
     * @throws WrongPassword never actually thrown here but declared for consistency
     * @throws IOException if switching scenes fails
     */
    @FXML
    void create_handle(ActionEvent event) throws WrongPassword, IOException {
        String name = room_name.getText();
        String pass = password.getText();
        if (name.isEmpty()) {
            error_field.setVisible(true);
            error_field.setText("Room name can not be empty!");
        } else {
            raum.spielraumErstellen(user, name, pass);
            sceneSwitcher.max_Scene(event, "/gameroom.fxml");
        }
    }

    /**
     * Handles the cancel button click event. Returns to the lobby scene.
     *
     * @param event the ActionEvent triggered by the button
     * @throws IOException if scene switching fails
     */
    @FXML
    void cancel_handle(ActionEvent event) throws IOException {
        sceneSwitcher.max_Scene(event, "/Lobby.fxml");
    }
}
