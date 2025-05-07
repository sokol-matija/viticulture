// Client.java
package com.example.viticulture2.Networking;

import com.example.viticulture2.Model.GameState;
import com.example.viticulture2.Model.ChatMessage;

import java.io.*;
import java.net.Socket;
import java.util.function.Consumer;

public class Client extends Thread {
    private final String host;
    private final int port;
    private final Consumer<GameState> onGameStateReceived;
    private final Consumer<ChatMessage> onChatMessageReceived;
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private volatile boolean running = true;

    public Client(String host, int port, Consumer<GameState> onGameStateReceived, Consumer<ChatMessage> onChatMessageReceived) {
        this.host = host;
        this.port = port;
        this.onGameStateReceived = onGameStateReceived;
        this.onChatMessageReceived = onChatMessageReceived;
    }

    @Override
    public void run() {
        try {
            socket = new Socket(host, port);
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());

            while (running) {
                Object received = in.readObject();
                if (received instanceof GameState gameState) {
                    onGameStateReceived.accept(gameState);
                } else if (received instanceof ChatMessage chatMessage) {
                    onChatMessageReceived.accept(chatMessage);
                }
            }
        } catch (Exception e) {
            if (running) {
                System.err.println("Connection error: " + e.getMessage());
                e.printStackTrace();
            }
        } finally {
            close();
        }
    }

    public void sendGameState(GameState gameState) {
        try {
            if (out != null && running) {
                out.writeObject(gameState);
                out.flush();
            }
        } catch (IOException e) {
            System.err.println("Error sending game state: " + e.getMessage());
            e.printStackTrace();
            close();
        }
    }

    public void sendChatMessage(ChatMessage message) {
        try {
            if (out != null && running) {
                out.writeObject(message);
                out.flush();
            }
        } catch (IOException e) {
            System.err.println("Error sending chat message: " + e.getMessage());
            e.printStackTrace();
            close();
        }
    }
    
    public void close() {
        running = false;
        try {
            if (out != null) out.close();
            if (in != null) in.close();
            if (socket != null) socket.close();
        } catch (IOException e) {
            System.err.println("Error closing client: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
