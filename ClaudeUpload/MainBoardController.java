package com.example.viticulture2.Controller;

import com.example.viticulture2.HelloApplication;
import com.example.viticulture2.Model.GameState;
import com.example.viticulture2.Model.Player;
import com.example.viticulture2.Networking.NetworkManager;
import com.example.viticulture2.Service.MainBoardService;
import com.example.viticulture2.Utils.DialogHandler;
import com.example.viticulture2.Utils.GameStateAware;
import com.example.viticulture2.Utils.PlayerHelper;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;

import java.util.Objects;
import java.util.Optional;

public class MainBoardController implements GameStateAware {
    private GameState gameState;

    @Override
    public void setGameState(GameState gameState) {
        this.gameState = gameState;
    }
    private Player player;

    @FXML
    private AnchorPane rootAnchorPane;

    private NetworkManager networkManager;

    @FXML
    public void initialize() {
        Platform.runLater(() -> {
            String css = Objects.requireNonNull(getClass().getResource("/styles.css")).toExternalForm();
            rootAnchorPane.getScene().getStylesheets().add(css);
        });
    }

    public void setNetworkManager(NetworkManager networkManager) {
        this.networkManager = networkManager;
    }

    private void sendGameStateUpdate() {
        if (this.networkManager != null) {
            this.networkManager.sendGameState(gameState);
        }
    }

    @FXML
    private void handleButtonClick(ActionEvent event) {

        player = gameState.getCurrentPlayerByTurn();
        String message = "";
        Button clickedButton = (Button) event.getSource();

        switch (clickedButton.getId()) {
            case "btn1":
                clickedButton.setStyle("-fx-background-color: " + MainBoardService.colorToHex(player.getPlayerColor()));
                clickedButton.setOpacity(1);
                gameState.nextTurn();
                player = gameState.getCurrentPlayerByTurn();
                break;
            case "gainCoinBtn_1", "gainCoinBtn_2":
                clickedButton.setStyle("-fx-background-color: " + MainBoardService.colorToHex(player.getPlayerColor()));
                clickedButton.setOpacity(1);

                player.setWorkerNumber(player.getWorkerNumber() - 1);
                player.setCoinsNumber(player.getCoinsNumber() + 1);
                message = "You just gained 1 coin!";
                DialogHandler.displayMessageDialog(clickedButton, "", message);
                PlayerHelper.checkEndGameRequirements(player, clickedButton);
                MainBoardService.endOfRoundWindow(player, clickedButton);
                gameState.nextTurn();
                player = gameState.getCurrentPlayerByTurn();
                sendGameStateUpdate();
                break;
            case "plantActionBtn":
                clickedButton.setStyle("-fx-background-color: " + MainBoardService.colorToHex(player.getPlayerColor()));
                clickedButton.setOpacity(1);
                player.setWorkerNumber(player.getWorkerNumber() - 1);
                player.setGrapeTokenNumber(player.getGrapeTokenNumber() + 2);

                message = "You just planted grapes! 2 grape tokens will be added to you after this round!" +
                        "\n\nMake sure to harvest them when your grape field is full!";
                MainBoardService.maxNumGrapeTokenAlertMsg(player, clickedButton, message);

                DialogHandler.displayMessageDialog(clickedButton, "Info", message);
                if (player.getGrapeTokenNumber() == player.getField())
                    DialogHandler.displayMessageDialog(clickedButton, "Alert!", "Your grape field is full! Time to harvest!");
                if (player.getWorkerNumber() <= 0)
                    DialogHandler.displayMessageDialog(clickedButton, "Alert!", "You are out of workers for this round.");
                MainBoardService.endOfRoundWindow(player, clickedButton);
                gameState.nextTurn();
                player = gameState.getCurrentPlayerByTurn();
                sendGameStateUpdate();
                break;
            case "btn4":
                clickedButton.setStyle("-fx-background-color: " + MainBoardService.colorToHex(player.getPlayerColor()));
                clickedButton.setOpacity(1);
                break;
            case "btn5":
                clickedButton.setStyle("-fx-background-color: " + MainBoardService.colorToHex(player.getPlayerColor()));
                clickedButton.setOpacity(1);
                break;
            case "btn6":
                clickedButton.setStyle("-fx-background-color: " + MainBoardService.colorToHex(player.getPlayerColor()));
                clickedButton.setOpacity(1);
                break;
            case "btn7":
                clickedButton.setStyle("-fx-background-color: " + MainBoardService.colorToHex(player.getPlayerColor()));
                clickedButton.setOpacity(1);
                break;
            case "btn8":
                clickedButton.setStyle("-fx-background-color: " + MainBoardService.colorToHex(player.getPlayerColor()));
                clickedButton.setOpacity(1);
                break;
            case "btn9":
                clickedButton.setStyle("-fx-background-color: " + MainBoardService.colorToHex(player.getPlayerColor()));
                clickedButton.setOpacity(1);
                break;
            case "convertCoinsToWorkersBtn":
                clickedButton.setStyle("-fx-background-color: " + MainBoardService.colorToHex(player.getPlayerColor()));
                clickedButton.setOpacity(1);
                MainBoardService.convertCoinsToWorkers(player, clickedButton, message);
                MainBoardService.endOfRoundWindow(player, clickedButton);
                gameState.nextTurn();
                player = gameState.getCurrentPlayerByTurn();
                sendGameStateUpdate();
                break;
            case "btn11":
                clickedButton.setStyle("-fx-background-color: " + MainBoardService.colorToHex(player.getPlayerColor()));
                clickedButton.setOpacity(1);
                break;
            case "btn12":
                clickedButton.setStyle("-fx-background-color: " + MainBoardService.colorToHex(player.getPlayerColor()));
                clickedButton.setOpacity(1);
                break;
            case "btn14":
                clickedButton.setStyle("-fx-background-color: " + MainBoardService.colorToHex(player.getPlayerColor()));
                clickedButton.setOpacity(1);
                break;
            case "checkPlayerStatsBtn":
                player = gameState.getCurrentPlayerByTurn();
                PlayerHelper.displayCurrentPlayerState(clickedButton, player);
                sendGameStateUpdate();
            default:
                break;
        }
    }


}
