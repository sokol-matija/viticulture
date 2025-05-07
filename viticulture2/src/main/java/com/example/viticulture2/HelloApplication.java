package com.example.viticulture2;

import com.example.viticulture2.Model.GameState;
import com.example.viticulture2.Model.Player;
import com.example.viticulture2.Model.PlayerType;
import com.example.viticulture2.Model.ChatMessage;
import com.example.viticulture2.Networking.Client;
import com.example.viticulture2.Networking.NetworkManager;
import com.example.viticulture2.Networking.Server;
import com.example.viticulture2.Utils.DialogHandler;
import com.example.viticulture2.Utils.GameStateAware;
import com.example.viticulture2.Utils.PlayerHelper;
import com.example.viticulture2.Controller.ChatController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class HelloApplication extends Application {

    private GameState gameState;
    public static PlayerType playerType;
    private static final int MULTIPLAYER_PORT = 5555;
    private NetworkManager networkManager;
    private Server server;
    private Client client;
    private ChatController chatController;
    
    // List of controllers that need to be notified of game state changes
    private List<GameStateAware> gameStateAwareControllers = new ArrayList<>();

    @Override
    public void start(Stage primaryStage) throws Exception {
        gameState = new GameState();
        
        // Initialize the player
        Player player = PlayerHelper.initializeLoggedInPlayer(gameState);
        
        // Initialize multiplayer if needed
        if (playerType == PlayerType.PLAYER_ONE || playerType == PlayerType.PLAYER_TWO) {
            initializeMultiplayer();
            // Load chat window
            loadChatWindow();
        }
        
        loadMainBoard(primaryStage);
        loadWineBoard();
        loadPlayerColorChoice();
        loadMamaAndPapaCards();
    }

    private void loadChatWindow() throws IOException {
        Stage chatStage = new Stage();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("chat-window.fxml"));
        chatStage.setTitle("Chat - " + playerType);
        chatStage.setScene(new Scene(loader.load()));
        chatStage.setResizable(false);
        
        // Get the controller and set up references
        chatController = loader.getController();
        chatController.setApp(this);
        
        chatStage.show();
    }

    public void sendChatMessage(ChatMessage message) {
        if (isMultiplayerGame()) {
            if (server != null) {
                server.sendChatMessage(message);
            } else if (client != null) {
                client.sendChatMessage(message);
            }
        }
    }

    public void receiveChatMessage(ChatMessage message) {
        if (chatController != null) {
            chatController.receiveMessage(message);
        }
    }

    private void initializeMultiplayer() {
        if (playerType == PlayerType.PLAYER_ONE) {
            // First client acts as server
            try {
                System.out.println("Starting server on port " + MULTIPLAYER_PORT);
                server = new Server(MULTIPLAYER_PORT, this::handleReceivedGameState, this::receiveChatMessage);
                server.start();
                
                Platform.runLater(() -> {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Server Started");
                    alert.setHeaderText("You are hosting the game");
                    alert.setContentText("Waiting for Player 2 to connect on localhost...");
                    alert.show();
                });
            } catch (Exception e) {
                showErrorAlert("Server Error", "Failed to start server: " + e.getMessage());
            }
        } else if (playerType == PlayerType.PLAYER_TWO) {
            // Second client connects to server
            try {
                System.out.println("Connecting to server at localhost:" + MULTIPLAYER_PORT);
                client = new Client("localhost", MULTIPLAYER_PORT, this::handleReceivedGameState, this::receiveChatMessage);
                client.start();
                
                Platform.runLater(() -> {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Connected to Server");
                    alert.setHeaderText("Successfully connected to the game");
                    alert.setContentText("You are now connected as Player 2");
                    alert.show();
                });
            } catch (Exception e) {
                showErrorAlert("Connection Error", "Failed to connect to server: " + e.getMessage());
            }
        }
    }
    
    /**
     * Send updated game state to other players.
     * Controllers should call this method whenever the game state changes.
     */
    public void sendGameStateUpdate() {
        if (isMultiplayerGame()) {
            if (server != null) {
                server.broadcastGameState(gameState);
            } else if (client != null) {
                client.sendGameState(gameState);
            }
        }
    }
    
    /**
     * Check if we're in a multiplayer game
     */
    public boolean isMultiplayerGame() {
        return playerType == PlayerType.PLAYER_ONE || playerType == PlayerType.PLAYER_TWO;
    }
    
    /**
     * Register a controller to be notified of game state changes
     */
    public void registerController(GameStateAware controller) {
        if (!gameStateAwareControllers.contains(controller)) {
            gameStateAwareControllers.add(controller);
        }
    }
    
    /**
     * Unregister a controller from game state change notifications
     */
    public void unregisterController(GameStateAware controller) {
        gameStateAwareControllers.remove(controller);
    }
    
    /**
     * Notify all controllers that the game state has changed
     */
    private void notifyControllers() {
        for (GameStateAware controller : gameStateAwareControllers) {
            controller.onGameStateUpdated();
        }
    }
    
    private void handleReceivedGameState(GameState receivedState) {
        // Update the local game state with the received one
        Platform.runLater(() -> {
            this.gameState = receivedState;
            // Notify all controllers that the game state has changed
            notifyControllers();
        });
    }
    
    private void showErrorAlert(String title, String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.show();
        });
    }

    private void loadMainBoard(Stage stage) throws IOException {
        FXMLLoader loader = createLoader("main-board.fxml");
        stage.setTitle("Main Board - " + playerType);
        stage.setScene(new Scene(loader.load()));
        stage.setResizable(false);
        stage.show();
    }

    private void loadWineBoard() throws IOException {
        Stage wineStage = new Stage();
        FXMLLoader loader = createLoader("wine-board.fxml");
        wineStage.setTitle("Wine Board - " + playerType);
        wineStage.setScene(new Scene(loader.load()));
        wineStage.setResizable(false);
        wineStage.show();
    }

    private void loadPlayerColorChoice() throws IOException {
        Stage colorStage = new Stage();
        FXMLLoader loader = createLoader("player-color-choice.fxml");
        colorStage.setTitle("Player Color - " + playerType);
        colorStage.setScene(new Scene(loader.load()));
        colorStage.setResizable(false);
        colorStage.show();
    }

    private void loadMamaAndPapaCards() throws IOException {
        Stage cardsStage = new Stage();
        FXMLLoader loader = createLoader("mama-and-papa.fxml");
        cardsStage.setTitle("Mama/Papa Cards - " + playerType);
        cardsStage.setScene(new Scene(loader.load()));
        cardsStage.setResizable(false);
        cardsStage.show();
    }

    private FXMLLoader createLoader(String fxmlFile) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
        loader.setControllerFactory(type -> {
            try {
                Object controller = type.getDeclaredConstructor().newInstance();
                if (controller instanceof GameStateAware) {
                    GameStateAware gameStateController = (GameStateAware) controller;
                    gameStateController.setGameState(gameState);
                    registerController(gameStateController);
                }
                return controller;
            } catch (Exception e) {
                throw new RuntimeException("Failed to create controller", e);
            }
        });
        return loader;
    }

    public static void main(String[] args) {
        if (args.length == 0) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Game Mode Selection");
            alert.setHeaderText("Select Game Mode");
            alert.setContentText("Choose whether to play in single player or multiplayer mode");
            
            ButtonType singlePlayerButton = new ButtonType("Single Player");
            ButtonType hostGameButton = new ButtonType("Host Game (Player 1)");
            ButtonType joinGameButton = new ButtonType("Join Game (Player 2)");
            
            alert.getButtonTypes().setAll(singlePlayerButton, hostGameButton, joinGameButton);
            
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent()) {
                if (result.get() == singlePlayerButton) {
                    playerType = PlayerType.SINGLE_PLAYER;
                } else if (result.get() == hostGameButton) {
                    playerType = PlayerType.PLAYER_ONE;
                } else if (result.get() == joinGameButton) {
                    playerType = PlayerType.PLAYER_TWO;
                }
                launch();
            }
        } else {
            String firstCommandLineArg = args[0];
            
            boolean playerTypeExists = false;
            
            for (PlayerType type : PlayerType.values()) {
                if (firstCommandLineArg.equals(type.toString())) {
                    playerTypeExists = true;
                }
            }
            
            if (!playerTypeExists) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Wrong player type");
                alert.setHeaderText(null);
                alert.setContentText("Wrong player type.");
                alert.showAndWait();
                System.exit(0);
            } else {
                playerType = PlayerType.valueOf(firstCommandLineArg);
                launch();
            }
        }
    }
    
    @Override
    public void stop() {
        // Clean up networking resources when application closes
        if (server != null) {
            server.shutdown();
        }
        if (client != null) {
            client.close();
        }
    }
}