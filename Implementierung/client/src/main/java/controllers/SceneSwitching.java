package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

/**
 * Utility class for switching between JavaFX scenes.
 * Provides methods for loading FXML files and setting the scene
 * to either maximized or minimized window modes.
 */
public class SceneSwitching {

    /**
     * Switches to a new scene with the specified FXML file and maximizes the window.
     *
     * @param event the event that triggered the scene change (typically a button click)
     * @param fxml  the path to the FXML file
     * @throws IOException if the FXML file cannot be loaded
     */
    public void max_Scene(ActionEvent event, String fxml) throws IOException {
        Parent newPage = FXMLLoader.load(Objects.requireNonNull(getClass().getResource(fxml)));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene newScene = new Scene(newPage);
        stage.setScene(newScene);
        stage.setMaximized(true);
    }


    /**
     * Switches to a new scene using an existing Scene object and maximizes the window.
     *
     * @param currentScene the current JavaFX Scene
     * @param fxml         the path to the FXML file
     * @throws IOException if the FXML file cannot be loaded
     */
    public void max_Scene(Scene currentScene, String fxml) throws IOException {
        Parent newPage = FXMLLoader.load(Objects.requireNonNull(getClass().getResource(fxml)));
        currentScene.setRoot(newPage);
        ((Stage) currentScene.getWindow()).setMaximized(true);
    }

    /**
     * Switches to a new scene with the specified FXML file and sets the window to a fixed smaller size.
     *
     * @param event the event that triggered the scene change (typically a button click)
     * @param fxml  the path to the FXML file
     * @throws IOException if the FXML file cannot be loaded
     */
    public void min_Scene(ActionEvent event, String fxml) throws IOException {
        Parent newPage = FXMLLoader.load(Objects.requireNonNull(getClass().getResource(fxml)));
        Scene scene = ((Node) event.getSource()).getScene();
        scene.setRoot(newPage);
        Stage stage = (Stage) scene.getWindow();
        stage.setMaximized(false);
        stage.setWidth(600);
        stage.setHeight(400);
    }

    /**
     * Switches to a new scene using an existing Scene object and sets the window to a fixed smaller size.
     *
     * @param currentScene the current JavaFX Scene
     * @param fxml         the path to the FXML file
     * @throws IOException if the FXML file cannot be loaded
     */
    public void min_Scene(Scene currentScene, String fxml) throws IOException {
        Parent newPage = FXMLLoader.load(Objects.requireNonNull(getClass().getResource(fxml)));
        currentScene.setRoot(newPage);
        Stage stage = (Stage) currentScene.getWindow();
        stage.setMaximized(false);
        stage.setWidth(600);
        stage.setHeight(400);
    }
}
