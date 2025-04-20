package com.example.viticulture2.Controller;

import com.example.viticulture2.HelloApplication;
import com.example.viticulture2.Model.GameState;
import com.example.viticulture2.Model.PlayerType;
import com.example.viticulture2.Service.MamaAndPapaService;
import com.example.viticulture2.Model.Player;
import com.example.viticulture2.Utils.GameStateAware;
import com.example.viticulture2.Utils.PlayerHelper;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.Optional;

import static com.example.viticulture2.Utils.DialogHandler.displayMessageDialog;

public class MamaAndPapaController implements GameStateAware {
    private GameState gameState;

    @Override
    public void setGameState(GameState gameState) {
        this.gameState = gameState;
    }

    @FXML
    private Button mamaCardFirst;

    private Player currentPlayer;

    @FXML
    public void initialize() {
        Player player_one = new Player(); Player player_two = new Player();
        currentPlayer = PlayerHelper.initializeLoggedInPlayer(player_one, player_two);
        Platform.runLater(MamaAndPapaService.showInitialDialog(currentPlayer, mamaCardFirst));
    }



    @FXML
    private void handleButtonClick(ActionEvent event) {
        Button clickedButton = (Button) event.getSource();
        Stage cardChoiceWindow = (Stage) clickedButton.getScene().getWindow();


        currentPlayer = gameState.getCurrentPlayerByTurn();
        currentPlayer = PlayerHelper.resetPlayerStats(currentPlayer);

        switch (clickedButton.getId()) {
            case "mamaCardFirst" -> {
                currentPlayer.setWorkerNumber(3);
                currentPlayer.setField(5);
                currentPlayer.setCardPicked(1);
                displayMessageDialog(clickedButton, "Card info" , MamaAndPapaService.displayPickedCardMessage(currentPlayer));
            }
            case "papaCardFirst" -> {
                currentPlayer.setWorkerNumber(2);
                currentPlayer.setCoinsNumber(2);
                currentPlayer.setField(5);
                currentPlayer.setCardPicked(2);
                displayMessageDialog(clickedButton, "Card info" ,MamaAndPapaService.displayPickedCardMessage(currentPlayer));
            }
            case "mamaCardSecond" -> {
                currentPlayer.setCoinsNumber(2);
                currentPlayer.setField(7);
                currentPlayer.setWorkerNumber(1);
                currentPlayer.setCardPicked(3);
                displayMessageDialog(clickedButton, "Card info" ,MamaAndPapaService.displayPickedCardMessage(currentPlayer));
            }
            case "papaCardSecond" -> {
                currentPlayer.setCoinsNumber(3);
                currentPlayer.setField(6);
                currentPlayer.setWorkerNumber(1);
                currentPlayer.setCardPicked(4);
                displayMessageDialog(clickedButton, "Card info" ,MamaAndPapaService.displayPickedCardMessage(currentPlayer));
            }
        }
        cardChoiceWindow.close();
    }


}
