package controllers;

import api.Raumverwaltung;
import app.RemoteServiceLocator;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import java.io.IOException;
import java.rmi.RemoteException;

/**
 * This class handles the display of player rankings, scores, and provides
 * navigation back to the game lobby.
 *
 */
public class WinController {
    @FXML Label playerWinnerField;
    @FXML Label playerSecondField;
    @FXML Label playerThirdField;
    @FXML Button lobbyButton;
    private final SceneSwitching sceneSwitcher = new SceneSwitching();

    private String playerWinner;
    private String playerSecond;
    private String playerThird;
    private int pW;
    private int pS;
    private int pD;
    private Raumverwaltung raum;
    /**
     * Default constructor for WinController.
     * Initializes the controller instance.
     */
    public WinController() {

    }
    /**
     * FXML initialization method called automatically after FXML loading.
     * Sets up the room management component through the remote service locator.
     *
     * @throws Exception if there's an error accessing the remote room service
     */
    @FXML
    public void initialize() throws Exception {
        this.raum = RemoteServiceLocator.getRaum();
    }
    /**
     * Initializes the UI components after dependency injection.
     */
    public void initializeAfterInjection(){
        playerWinnerField.setText(playerWinner + ": " + pW + " Punkte");
        playerSecondField.setText(playerSecond + ": " + pS + " Punkte");
        playerThirdField.setText(playerThird + ": " + pD + " Punkte");
    }

    /**
     * sets the names for the ranking displays
     * @param playerWinner name of the winning player.
     * @param playerSecond name of the second place player.
     * @param playerThird name of the third place player.
     */
    public void setPlayers (String playerWinner, String playerSecond, String playerThird) {
        this.playerWinner = playerWinner;
        this.playerSecond = playerSecond;
        this.playerThird = playerThird;
    }

    /**
     * Sets the points scored by each player.
     * @param pW points scored by the winner
     * @param pS points scored by the second player
     * @param pD points scored by the third place player.
     */
    public void setPoints (int pW, int pS, int pD) {
        this.pW = pW;
        this.pS = pS;
        this.pD = pD;
    }

    /**
     * Event handler for the lobby button click.
     * @param event the ActionEvent triggered by button click
     * @throws IOException if there's an error during scene switching.
     */
    @FXML
    private void onClickLobbyButton(ActionEvent event) throws IOException {
        sceneSwitcher.max_Scene(event, "/gameroom.fxml");
        updateWinAmountWinner();
    }

    /**
     * Updates the win count for the winning player if they are not a bot.
     * @throws RemoteException if there's an error accessing the remote account service.
     */
    private void updateWinAmountWinner () throws RemoteException {
        if (!(playerWinner.equals("EasyBot1") || playerWinner.equals("EasyBot2") || playerWinner.equals("HardBot1") || playerWinner.equals("HardBot2"))) {
            raum.getAccount(playerWinner).add_wins();
        }
    }
}

