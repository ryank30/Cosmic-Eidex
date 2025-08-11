package controllers;

import account.Account;
import api.Zugriffsverwaltung;
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
 * Controller class responsible for editing user account credentials.
 * Handles username and password change requests.
 */
public class EditController {
    private Zugriffsverwaltung zugriff;
    Account current = AccountManager.getAccount();
    private String current_name = AccountManager.getAccount().get_username();
    private String current_password = AccountManager.getAccount().get_password();

    @FXML public TextField new_username;
    @FXML public PasswordField repeat_password;
    @FXML public PasswordField new_password;
    @FXML public Button save_button;
    @FXML public Button cancel_button;
    @FXML public TextField error_field;

    private final SceneSwitching sceneSwitcher = new SceneSwitching();

    /**
     * Constructor that connects to the RMI registry and retrieves required server stubs.
     *
     * @throws RemoteException if RMI registry connection fails
     * @throws NotBoundException if any of the servers are not found in the registry
     */
    public EditController() throws Exception {
        this.zugriff = RemoteServiceLocator.getZugriff();
    }

    /**
     * Handles the save button. Updates the user's credentials on the server.
     *
     * @param event the action triggered by the save button
     * @throws Exception if the server call fails or if inputs are invalid
     */
    @FXML
    void save_handle(ActionEvent event) throws Exception {
        String name = new_username.getText();
        String pass = new_password.getText();
        if (name.isEmpty()) {
            error_field.setVisible(true);
            error_field.setText("Username field can not be empty!");
        }

        zugriff.edit_account(current, name, pass);
        sceneSwitcher.max_Scene(event, "/Lobby.fxml");
    }

    /**
     * Handles the cancel button. Returns to the lobby screen without saving changes.
     *
     * @param event the action triggered by the cancel button
     * @throws IOException if scene switching fails
     */
    @FXML
    void cancel_handle(ActionEvent event) throws IOException {
        sceneSwitcher.max_Scene(event, "/Lobby.fxml");
    }
}
