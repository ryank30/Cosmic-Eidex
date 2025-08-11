package app;

import javafx.application.Platform;
import util.HeartbeatManager;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.util.Objects;

/**
 * Loads the initial login scene and handles heartbeat shutdown on exit.
 */
public class Client extends Application {

    /**
     * Launches the JavaFX application.
     *
     * @param args command line arguments (not used)
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Starts the JavaFX application and loads the login.fxml scene.
     *
     * @param primaryStage the primary stage for this application
     * @throws Exception if loading the FXML file fails
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        //try udp
        String serverIP = ServerDiscoveryClient.discoverServerIP();

        //http as fallback
        if (serverIP == null) {
            System.err.println("UDP discovery failed, trying HTTP fallback...");
            serverIP = getServerIPOverHttp();
        }

        //fallback to localhost
        if (serverIP == null) {
            System.err.println("Could not find server automatically.");
            // Optional: show dialog asking user to enter IP manually
            serverIP = "localhost"; // temporary fallback
        }

        System.out.println("Connecting to server at IP: " + serverIP);

        // Set the discovered IP before connecting RMI
        RemoteServiceLocator.connect(serverIP);

        URL url = getClass().getResource("/login.fxml");
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/login.fxml")));
        primaryStage.setTitle("Cosmic Eidex");
        primaryStage.setScene(new Scene(root, 600, 400));
        primaryStage.show();
        primaryStage.setOnCloseRequest(event -> {
            Platform.exit(); // Ensures the JavaFX app exits
            System.exit(0);
        });
    }

    /**
     * Called when the application is stopping.
     * Stops the HeartbeatManager before exiting.
     *
     * @throws Exception if an error occurs during shutdown
     */
    @Override
    public void stop() throws Exception {
        HeartbeatManager.stop();
        super.stop();
    }

    private String getServerIPOverHttp() throws IOException {
        URI uri = java.net.URI.create("http://<router_ip>:8000/ip");
        URL url = uri.toURL();
        URLConnection conn = url.openConnection();
        conn.setConnectTimeout(2000);
        conn.setReadTimeout(2000);
        try (BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
            return in.readLine().trim();
        }
    }
}
