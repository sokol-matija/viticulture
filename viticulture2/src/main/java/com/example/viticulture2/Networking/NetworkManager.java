package com.example.viticulture2.Networking;

import com.example.viticulture2.Model.ChatMessage;
import com.example.viticulture2.Model.GameState;
import com.example.viticulture2.Model.PlayerType;

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
    private Server server;
    private Client client;

    /**
     * Creates a network manager based on player type and handles game state updates.
     * 
     * @param playerType The type of player (PLAYER_ONE or PLAYER_TWO)
     * @param onGameStateReceived Callback for when game state updates are received
     * @param onChatMessageReceived Callback for when chat messages are received
     */
    public NetworkManager(PlayerType playerType, Consumer<GameState> onGameStateReceived, Consumer<ChatMessage> onChatMessageReceived) {
        this.onGameStateReceived = onGameStateReceived;
        this.onChatMessageReceived = onChatMessageReceived;
        
        if (playerType == PlayerType.PLAYER_ONE) {
            // Player one is the server
            server = new Server(DEFAULT_PORT, this::handleReceivedGameState, this::handleReceivedChatMessage);
            server.start();
        } else if (playerType == PlayerType.PLAYER_TWO) {
            // Player two is the client
            client = new Client("localhost", DEFAULT_PORT, this::handleReceivedGameState, this::handleReceivedChatMessage);
            client.start();
        }
    }

    /**
     * Handles game state updates from the network
     */
    private void handleReceivedGameState(GameState gameState) {
        if (onGameStateReceived != null) {
            onGameStateReceived.accept(gameState);
        }
    }

    private void handleReceivedChatMessage(ChatMessage message) {
        if (onChatMessageReceived != null) {
            onChatMessageReceived.accept(message);
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

    public void sendChatMessage(ChatMessage message) {
        if (server != null) {
            server.sendChatMessage(message);
        } else if (client != null) {
            client.sendChatMessage(message);
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
     * Checks if we're connected to the network
     */
    public boolean isConnected() {
        // This is a simplistic implementation and could be improved
        return client != null || server != null;
    }
}

