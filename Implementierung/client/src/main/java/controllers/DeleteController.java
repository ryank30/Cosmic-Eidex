package controllers;

import api.Zugriffsverwaltung;
import app.RemoteServiceLocator;
import exceptions.WrongPassword;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;


/**
 * Controller for deleting user accounts.
 * Handles user input validation and communicates with the account server to delete an account.
 */
public class DeleteController {
    private Zugriffsverwaltung zugriff;

    @FXML public TextField username;
    @FXML public PasswordField password;
    @FXML public Button delete_button;
    @FXML public Button cancel_button;
    @FXML public TextField error_field;

    private final SceneSwitching sceneSwitcher = new SceneSwitching();

    /**
     * Initializes the controller by connecting to the RMI registry and retrieving the account server stub.
     *
     * @throws RemoteException if connection to RMI registry fails
     * @throws NotBoundException if the account Server is not registered
     */
    public DeleteController() throws Exception {
        this.zugriff = RemoteServiceLocator.getZugriff();
    }

    /**
     * Handles the delete button event. Validates credentials and attempts to delete the account.
     *
     * @param event the action triggered by the delete button
     * @throws WrongPassword if the password is incorrect
     * @throws IOException if scene switching fails
     */
    @FXML
    void delete_handle(ActionEvent event) throws WrongPassword, IOException {
        String name = username.getText();
        String pass = password.getText();
        if (name.isEmpty()) {
            error_field.setVisible(true);
            error_field.setText("Username or Password can not be empty!");
        }
        try {
            zugriff.delete_account(name, pass);
            sceneSwitcher.min_Scene(event, "/login.fxml");
        } catch (WrongPassword ex) {
            error_field.setVisible(true);
            error_field.setText(ex.getMessage());
        }
    }

    /**
     * Handles the cancel button event. Returns to the lobby screen.
     *
     * @param event the action triggered by the cancel button
     * @throws IOException if scene switching fails
     */
    @FXML
    void cancel_handle(ActionEvent event) throws IOException {
        sceneSwitcher.max_Scene(event, "/Lobby.fxml");
    }
}
