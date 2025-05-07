// Server.java
package com.example.viticulture2.Networking;

import com.example.viticulture2.Model.GameState;
import com.example.viticulture2.Model.ChatMessage;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class Server extends Thread {
    private final int port;
    private final Consumer<GameState> onGameStateReceived;
    private final Consumer<ChatMessage> onChatMessageReceived;
    private final List<ClientHandler> clients = new ArrayList<>();
    private ServerSocket serverSocket;
    private volatile boolean running = true;

    public Server(int port, Consumer<GameState> onGameStateReceived, Consumer<ChatMessage> onChatMessageReceived) {
        this.port = port;
        this.onGameStateReceived = onGameStateReceived;
        this.onChatMessageReceived = onChatMessageReceived;
    }

    @Override
    public void run() {
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("Server started on port " + port);

            while (running) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected: " + clientSocket.getInetAddress().getHostAddress());
                ClientHandler clientHandler = new ClientHandler(clientSocket);
                clients.add(clientHandler);
                clientHandler.start();
            }
        } catch (IOException e) {
            if (serverSocket != null && !serverSocket.isClosed()) {
                e.printStackTrace();
            }
        } finally {
            shutdown();
        }
    }

    public void broadcastGameState(GameState gameState) {
        for (ClientHandler client : new ArrayList<>(clients)) {
            client.sendGameState(gameState);
        }
    }

    public void sendChatMessage(ChatMessage message) {
        for (ClientHandler client : new ArrayList<>(clients)) {
            client.sendChatMessage(message);
        }
    }

    public void removeClient(ClientHandler client) {
        clients.remove(client);
    }

    public void shutdown() {
        running = false;
        try {
            if (serverSocket != null) {
                serverSocket.close();
            }
            for (ClientHandler client : new ArrayList<>(clients)) {
                client.close();
            }
            clients.clear();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class ClientHandler extends Thread {
        private final Socket clientSocket;
        private ObjectOutputStream out;
        private ObjectInputStream in;
        private volatile boolean clientRunning = true;

        public ClientHandler(Socket socket) {
            this.clientSocket = socket;
            try {
                out = new ObjectOutputStream(clientSocket.getOutputStream());
                in = new ObjectInputStream(clientSocket.getInputStream());
            } catch (IOException e) {
                e.printStackTrace();
                close();
            }
        }

        @Override
        public void run() {
            try {
                while (clientRunning && running) {
                    Object received = in.readObject();
                    if (received instanceof GameState gameState) {
                        onGameStateReceived.accept(gameState);
                        broadcastGameState(gameState);
                    } else if (received instanceof ChatMessage chatMessage) {
                        onChatMessageReceived.accept(chatMessage);
                        sendChatMessage(chatMessage);
                    }
                }
            } catch (IOException | ClassNotFoundException e) {
                System.out.println("Client disconnected: " + clientSocket.getInetAddress().getHostAddress());
            } finally {
                removeClient(this);
                close();
            }
        }

        public void sendGameState(GameState gameState) {
            try {
                if (out != null && clientRunning) {
                    out.writeObject(gameState);
                    out.flush();
                }
            } catch (IOException e) {
                e.printStackTrace();
                close();
            }
        }

        public void sendChatMessage(ChatMessage message) {
            try {
                if (out != null && clientRunning) {
                    out.writeObject(message);
                    out.flush();
                }
            } catch (IOException e) {
                e.printStackTrace();
                close();
            }
        }

        public void close() {
            clientRunning = false;
            try {
                if (out != null) out.close();
                if (in != null) in.close();
                if (clientSocket != null) clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
