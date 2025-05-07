package com.example.viticulture2.Networking;

import com.example.viticulture2.Model.ChatMessage;
import com.example.viticulture2.Model.GameState;
import com.example.viticulture2.Model.PlayerType;
import com.example.viticulture2.Model.ButtonAction;
import com.example.viticulture2.Controller.MainBoardController;

import java.util.function.Consumer;

/**
 * Manages network connections for multiplayer games.
 * Abstracts the server and client implementations.
 * This implementation only works on localhost.
 */
public class NetworkManager {

    private static final int DEFAULT_PORT = 5555;
    private final Consumer<GameState> onGameStateReceived;
    private final Consumer<ChatMessage> onChatMessageReceived;
    private final MainBoardController mainBoardController;
    private final Object application; // Reference to the main application
    private Server server;
    private Client client;

    /**
     * Creates a network manager based on player type and handles network communications.
     * 
     * @param playerType The type of player (PLAYER_ONE or PLAYER_TWO)
     * @param onGameStateReceived Callback for when game state updates are received
     * @param onChatMessageReceived Callback for when chat messages are received
     * @param mainBoardController The main board controller for handling button actions
     */
    public NetworkManager(PlayerType playerType, Consumer<GameState> onGameStateReceived, 
                         Consumer<ChatMessage> onChatMessageReceived,
                         MainBoardController mainBoardController) {
        this(playerType, onGameStateReceived, onChatMessageReceived, mainBoardController, null);
    }
    
    /**
     * Creates a network manager with reference to the main application.
     */
    public NetworkManager(PlayerType playerType, Consumer<GameState> onGameStateReceived, 
                         Consumer<ChatMessage> onChatMessageReceived,
                         MainBoardController mainBoardController,
                         Object application) {
        this.onGameStateReceived = onGameStateReceived;
        this.onChatMessageReceived = onChatMessageReceived;
        this.mainBoardController = mainBoardController;
        this.application = application;
        
        System.out.println("NetworkManager constructor called for " + playerType);
        
        // Note: Server and Client are now initialized in HelloApplication
        // We're just using NetworkManager to handle communication logic
    }

    /**
     * Connects this NetworkManager to an existing server instance
     */
    public void connectToServer(Server server) {
        this.server = server;
        System.out.println("NetworkManager connected to server");
    }
    
    /**
     * Connects this NetworkManager to an existing client instance
     */
    public void connectToClient(Client client) {
        this.client = client;
        System.out.println("NetworkManager connected to client");
    }

    /**
     * Gets the MainBoardController associated with this NetworkManager
     */
    public MainBoardController getMainBoardController() {
        return mainBoardController;
    }

    /**
     * Handles game state updates from the network
     */
    private void handleReceivedGameState(GameState gameState) {
        System.out.println("Received game state update");
        if (onGameStateReceived != null) {
            onGameStateReceived.accept(gameState);
        }
    }

    private void handleReceivedChatMessage(ChatMessage message) {
        System.out.println("Received chat message: " + message.getMessage());
        if (onChatMessageReceived != null) {
            onChatMessageReceived.accept(message);
        }
    }

    private void handleReceivedButtonAction(ButtonAction action) {
        System.out.println("NetworkManager received button action: " + action.getButtonId());
        if (mainBoardController != null) {
            System.out.println("Forwarding to MainBoardController: " + mainBoardController);
            mainBoardController.handleButtonAction(action);
        } else {
            System.err.println("MainBoardController is null, cannot handle button action");
        }
    }

    /**
     * Sends the current game state to all connected players
     */
    public void sendGameState(GameState gameState) {
        if (server != null) {
            server.broadcastGameState(gameState);
        } else if (client != null) {
            client.sendGameState(gameState);
        }
    }

    /**
     * Sends a chat message to all connected players
     */
    public void sendChatMessage(ChatMessage message) {
        if (server != null) {
            server.sendChatMessage(message);
        } else if (client != null) {
            client.sendChatMessage(message);
        }
    }

    /**
     * Sends a button action to all connected players
     */
    public void sendButtonAction(ButtonAction action) {
        System.out.println("NetworkManager sending button action: " + action.getButtonId());
        try {
            if (server != null) {
                System.out.println("Sending through server");
                server.sendButtonAction(action);
            } else if (client != null) {
                System.out.println("Sending through client");
                client.sendButtonAction(action);
            } else {
                System.err.println("No server or client available to send action");
            }
        } catch (Exception e) {
            System.err.println("Error sending button action: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Closes all network connections
     */
    public void shutdown() {
        if (server != null) {
            server.shutdown();
        }
        if (client != null) {
            client.close();
        }
    }

    /**
     * Gets the application reference
     */
    public Object getApplication() {
        return application;
    }
}

