package com.example.viticulture2.Controller;

import com.example.viticulture2.HelloApplication;
import com.example.viticulture2.Model.GameState;
import com.example.viticulture2.Model.Player;
import com.example.viticulture2.Utils.GameStateAware;
import com.example.viticulture2.Utils.PlayerHelper;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.Optional;

public class PlayerColorChoiceController implements GameStateAware {
    private GameState gameState;

    @Override
    public void setGameState(GameState gameState) {
        this.gameState = gameState;
    }

    private Player player;

    @FXML
    Button redButton, blueButton;

    @FXML
    public void initialize() {
    }

    @FXML
    private void handlePlayerColorChoice(ActionEvent event) {
        player = gameState.getCurrentPlayerByTurn();

        Button clickedButton = (Button) event.getSource();
        String buttonId = clickedButton.getId();
        Stage currentWindow = (Stage) clickedButton.getScene().getWindow();

        if (buttonId.equals("redButton"))
            player.setPlayerColor(Color.RED);
        player.setPlayerColor(Color.BLUE);

        if (currentWindow != null){
            currentWindow.close();
            PlayerHelper.displayCurrentPlayerState(clickedButton, player);
        }

    }
}
