// Server.java
package com.example.viticulture2.Networking;

import com.example.viticulture2.Model.GameState;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class Server extends Thread {

    private int port;
    private Consumer<GameState> onGameStateReceived;
    private List<ClientHandler> clients = new ArrayList<>(); // List of connected clients
    private ServerSocket serverSocket;
    private boolean running = true;

    public Server(int port, Consumer<GameState> onGameStateReceived) {
        this.port = port;
        this.onGameStateReceived = onGameStateReceived;
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
            if (!serverSocket.isClosed()) {
                e.printStackTrace();
            }
        } finally {
            shutdown();
        }
    }

    public void broadcastGameState(GameState gameState) {
        for (ClientHandler client : clients) {
            client.sendGameState(gameState);
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
            for (ClientHandler client : clients) {
                client.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class ClientHandler extends Thread {
        private Socket clientSocket;
        private ObjectOutputStream out;
        private ObjectInputStream in;

        public ClientHandler(Socket socket) {
            this.clientSocket = socket;
            try {
                out = new ObjectOutputStream(clientSocket.getOutputStream());
                in = new ObjectInputStream(clientSocket.getInputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            try {
                while (running) {
                    GameState receivedState = (GameState) in.readObject();
                    onGameStateReceived.accept(receivedState);
                    broadcastGameState(receivedState);
                }
            } catch (IOException | ClassNotFoundException e) {
                System.out.println("Client disconnected: " + clientSocket.getInetAddress().getHostAddress());
                removeClient(this);
            } finally {
                close();
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

        public void close() {
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
