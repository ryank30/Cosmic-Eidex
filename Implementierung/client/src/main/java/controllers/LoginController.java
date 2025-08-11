package controllers;

import api.Zugriffsverwaltung;
import account.Account;
import app.RemoteServiceLocator;
import exceptions.UsernameNotFound;
import exceptions.WrongPassword;
import javafx.fxml.FXML;

import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.event.ActionEvent;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 * Controller for handling user login and registration navigation.
 * Connects to the RMI-based account server for authentication.
 */
public class LoginController {
    private Zugriffsverwaltung zugriff;

    @FXML public TextField username;
    @FXML public PasswordField password;
    @FXML public Button login_button;
    @FXML public Button register_button;
    @FXML public TextField error_field;

    private final SceneSwitching sceneSwitcher = new SceneSwitching();

    /**
     * Initializes the RMI registry and looks up the account server.
     *
     * @throws RemoteException if RMI connection fails
     * @throws NotBoundException if "AccountServer" not found in registry
     */
    public LoginController() throws Exception {
        this.zugriff = RemoteServiceLocator.getZugriff();
    }

    /**
     * Handles the login process. Validates user credentials via RMI call.
     *
     * @param event ActionEvent from login button
     * @throws WrongPassword if the entered password is incorrect
     * @throws UsernameNotFound if the username is not registered
     * @throws IOException if scene switch fails
     */
    @FXML
    void login_handle(ActionEvent event) throws WrongPassword, UsernameNotFound, IOException {
        String name = username.getText();
        String pass = password.getText();

        if (name.isEmpty() || pass.isEmpty()) {
            error_field.setVisible(true);
            error_field.setText("Username or Password can not be empty!");
        }

        try {
            zugriff.login(name, pass);
            AccountManager.setAccount(new Account(name, String.valueOf(pass)));
            sceneSwitcher.max_Scene(event, "/Lobby.fxml");
        } catch (WrongPassword ex) {
            error_field.setVisible(true);
            error_field.setText("Entered the wrong Password!");
        } catch (UsernameNotFound ex) {
            error_field.setVisible(true);
            error_field.setText("Username not found!");
        }
    }

    /**
     * Switches to the registration scene.
     *
     * @param event ActionEvent from register button
     * @throws IOException if scene switch fails
     */
    @FXML
    void register_handle(ActionEvent event) throws IOException {
        sceneSwitcher.min_Scene(event, "/register.fxml");
    }
}
