package com.example.viticulture2.Controller;

import com.example.viticulture2.HelloApplication;
import com.example.viticulture2.Model.GameState;
import com.example.viticulture2.Model.Player;
import com.example.viticulture2.Model.ButtonAction;
import com.example.viticulture2.Model.PlayerType;
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
    private Player player;
    private NetworkManager networkManager;
    private boolean processingRemoteAction = false;

    @FXML
    private AnchorPane rootAnchorPane;

    @Override
    public void setGameState(GameState gameState) {
        this.gameState = gameState;
    }

    @FXML
    public void initialize() {
        Platform.runLater(() -> {
            try {
                String css = Objects.requireNonNull(getClass().getResource("/styles.css")).toExternalForm();
                rootAnchorPane.getScene().getStylesheets().add(css);
                
                // Create test button for debugging
                Button testButton = new Button("Test Button Sync");
                testButton.setLayoutX(10);
                testButton.setLayoutY(10);
                testButton.setOnAction(e -> {
                    System.out.println("Test button clicked");
                    if (networkManager != null) {
                        ButtonAction testAction = new ButtonAction("btn1", HelloApplication.playerType);
                        System.out.println("Sending test button action: " + testAction);
                        networkManager.sendButtonAction(testAction);
                    } else {
                        System.err.println("NetworkManager is null in test button handler");
                    }
                });
                rootAnchorPane.getChildren().add(testButton);
                
                System.out.println("MainBoardController initialized with rootAnchorPane: " + rootAnchorPane);
                System.out.println("Child count: " + rootAnchorPane.getChildren().size());
            } catch (Exception e) {
                System.err.println("Error initializing MainBoardController: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }

    public void setNetworkManager(NetworkManager networkManager) {
        this.networkManager = networkManager;
    }

    /**
     * Handle a button action received from the network
     */
    public void handleButtonAction(ButtonAction action) {
        System.out.println("MainBoardController received remote button action: " + action.getButtonId());
        Platform.runLater(() -> {
            try {
                // Find the button in our UI
                Button button = findButtonById(action.getButtonId());
                System.out.println("Button found: " + (button != null));
                
                if (button != null) {
                    System.out.println("Processing button: " + button.getId() + " with current style: " + button.getStyle());
                    
                    // Set flag to avoid sending the action back
                    processingRemoteAction = true;
                    
                    // Apply color directly for immediate visual feedback
                    player = gameState.getCurrentPlayerByTurn();
                    if (player != null) {
                        String colorStyle = "-fx-background-color: " + MainBoardService.colorToHex(player.getPlayerColor());
                        System.out.println("Setting style to: " + colorStyle);
                        button.setStyle(colorStyle);
                        button.setOpacity(1);
                    }
                    
                    // Simulate a click for game logic
                    System.out.println("Simulating click on button: " + action.getButtonId());
                    handleButtonClick(new ActionEvent(button, null));
                    
                    // Reset flag
                    processingRemoteAction = false;
                } else {
                    System.err.println("Button not found: " + action.getButtonId());
                    // Dump all button IDs for debugging
                    System.out.println("Available buttons in the UI:");
                    rootAnchorPane.lookupAll(".button").forEach(node -> {
                        if (node instanceof Button b) {
                            System.out.println("Available button: " + b.getId());
                        }
                    });
                    
                    // Try an alternative lookup method
                    System.out.println("Trying alternative lookup method...");
                    rootAnchorPane.getChildrenUnmodifiable().forEach(node -> {
                        if (node instanceof Button) {
                            System.out.println("Found button: " + ((Button)node).getId());
                        }
                    });
                }
            } catch (Exception e) {
                System.err.println("Error handling remote button action: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }
    
    private Button findButtonById(String id) {
        Button button = (Button) rootAnchorPane.lookup("#" + id);
        if (button == null) {
            System.out.println("Button with ID '" + id + "' not found, trying broader search...");
            // Try a different lookup approach
            for (javafx.scene.Node node : rootAnchorPane.getChildren()) {
                if (node instanceof Button && id.equals(((Button)node).getId())) {
                    return (Button) node;
                }
            }
        }
        return button;
    }

    @FXML
    private void handleButtonClick(ActionEvent event) {
        Button clickedButton = (Button) event.getSource();
        String buttonId = clickedButton.getId();
        
        System.out.println("Button clicked: " + buttonId);
        
        // Only send the action if this is a local click (not a simulated one)
        // and we're connected to a client
        if (!processingRemoteAction && networkManager != null) {
            boolean readyToSend = HelloApplication.playerType == PlayerType.PLAYER_ONE ? 
                ((HelloApplication)networkManager.getApplication()).isClientConnected() : 
                true; // Player 2 can always send
                
            if (readyToSend) {
                System.out.println("Sending button action to other client: " + buttonId);
                ButtonAction action = new ButtonAction(buttonId, HelloApplication.playerType);
                networkManager.sendButtonAction(action);
            } else {
                System.out.println("Not sending button action because client is not connected yet");
            }
        }

        // Update our local game state based on the button clicked
        player = gameState.getCurrentPlayerByTurn();
        String message = "";

        switch (buttonId) {
            case "btn1":
                clickedButton.setStyle("-fx-background-color: " + MainBoardService.colorToHex(player.getPlayerColor()));
                clickedButton.setOpacity(1);
                gameState.nextTurn();
                player = gameState.getCurrentPlayerByTurn();
                break;
                
            case "gainCoinBtn_1":
            case "gainCoinBtn_2":
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
                break;
                
            case "convertCoinsToWorkersBtn":
                clickedButton.setStyle("-fx-background-color: " + MainBoardService.colorToHex(player.getPlayerColor()));
                clickedButton.setOpacity(1);
                MainBoardService.convertCoinsToWorkers(player, clickedButton, message);
                MainBoardService.endOfRoundWindow(player, clickedButton);
                gameState.nextTurn();
                player = gameState.getCurrentPlayerByTurn();
                break;
                
            case "checkPlayerStatsBtn":
                player = gameState.getCurrentPlayerByTurn();
                PlayerHelper.displayCurrentPlayerState(clickedButton, player);
                break;
                
            default:
                // Handle other button cases if needed
                clickedButton.setStyle("-fx-background-color: " + MainBoardService.colorToHex(player.getPlayerColor()));
                clickedButton.setOpacity(1);
                break;
        }
    }
}
