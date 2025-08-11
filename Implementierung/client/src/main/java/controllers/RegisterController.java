package controllers;

import account.Account;
import app.RemoteServiceLocator;
import exceptions.PasswordsDoNotMatch;
import exceptions.UsernameNotAvailable;
import api.Zugriffsverwaltung;
import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;

/**
 * Controller responsible for user registration UI logic.
 * Connects to the account server via RMI to register a new account.
 */
public class RegisterController {

    Zugriffsverwaltung register;

    @FXML public TextField username;
    @FXML public PasswordField password;
    @FXML public Button cancel_button;
    @FXML public Button register_button;
    @FXML public PasswordField repeat_password;
    @FXML public TextField error_field;

    private final SceneSwitching sceneSwitcher = new SceneSwitching();

    /**
     * Constructor: Initializes RMI registry and connects to the account server.
     *
     * @throws RemoteException if registry connection fails
     * @throws NotBoundException if "AccountServer" not found
     */
    public RegisterController() throws Exception {
        this.register = RemoteServiceLocator.getZugriff();
    }

    /**
     * Handles the register button click.
     * Registers a new user if the username is available and passwords match.
     *
     * @param event the button click event
     * @throws PasswordsDoNotMatch if the repeated password does not match
     * @throws UsernameNotAvailable if the username is already taken
     * @throws IOException if scene switching fails
     */
    @FXML
    void register_handle(ActionEvent event) throws PasswordsDoNotMatch, UsernameNotAvailable, IOException {
        String name = username.getText();
        String pass = password.getText();
        String repeated_pass = repeat_password.getText();

        if (name.isEmpty() || pass.isEmpty() || repeated_pass.isEmpty()) {
            error_field.setVisible(true);
            error_field.setText("Username or Password can not be empty!");
        }

        try {
            register.register(name, pass, repeated_pass);
            AccountManager.setAccount(new Account(name, String.valueOf(pass)));
            sceneSwitcher.max_Scene(event, "/Lobby.fxml");
        } catch (PasswordsDoNotMatch ex) {
            error_field.setVisible(true);
            error_field.setText("Passwords do not match!");
        } catch (UsernameNotAvailable ex) {
            error_field.setVisible(true);
            error_field.setText("Username not available!");
        }
    }

    /**
     * Cancels the registration process and returns to login screen.
     *
     * @param event the button click event
     * @throws IOException if scene switching fails
     */
    @FXML
    void cancel_handle(ActionEvent event) throws IOException {
        sceneSwitcher.min_Scene(event, "/login.fxml");
    }
}
