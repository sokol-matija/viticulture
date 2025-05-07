package com.example.viticulture2.Networking;

import com.example.viticulture2.Model.GameState;
import com.example.viticulture2.Model.PlayerType;

import java.util.function.Consumer;

public class NetworkManager {

    private Client client;
    private Server server;
    private Consumer<GameState> onGameStateReceived;

    public NetworkManager(PlayerType playerType, Consumer<GameState> onGameStateReceived) {
        this.onGameStateReceived = onGameStateReceived;
        if (playerType == PlayerType.PLAYER_ONE) {
            client = new Client("localhost", 5555, this::handleReceivedGameState);
            client.start();
        } else if (playerType == PlayerType.PLAYER_TWO) {
            server = new Server(5555, this::handleReceivedGameState);
            server.start();
        }
    }

    private void handleReceivedGameState(GameState gameState) {
        onGameStateReceived.accept(gameState);
    }

    public void sendGameState(GameState gameState) {
        if (client != null) {
            client.sendGameState(gameState);
        } else if (server != null) {
            server.broadcastGameState(gameState);
        }
    }

    public void stop() {
        //todo
    }
}
