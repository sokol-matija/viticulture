// Client.java (simplified)
package com.example.viticulture2.Networking;

import com.example.viticulture2.Model.GameState;

import java.io.*;
import java.net.Socket;
import java.util.function.Consumer;

public class Client extends Thread {

    private String host;
    private int port;
    private Consumer<GameState> onGameStateReceived;
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;

    public Client(String host, int port, Consumer<GameState> onGameStateReceived) {
        this.host = host;
        this.port = port;
        this.onGameStateReceived = onGameStateReceived;
    }

    @Override
    public void run() {
        try {
            socket = new Socket(host, port);
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());

            while (true) {
                GameState receivedState = (GameState) in.readObject();
                onGameStateReceived.accept(receivedState);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendGameState(GameState gameState) {
        try {
            out.writeObject(gameState);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
