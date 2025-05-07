// Server.java
package com.example.viticulture2.Networking;

import com.example.viticulture2.Model.GameState;
import com.example.viticulture2.Model.ChatMessage;
import com.example.viticulture2.Model.ButtonAction;
import com.example.viticulture2.Model.ConnectionMessage;

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
    private final Consumer<ButtonAction> onButtonActionReceived;
    private final Consumer<ConnectionMessage> onConnectionMessageReceived;
    private final List<ClientHandler> clients = new ArrayList<>();
    private ServerSocket serverSocket;
    private volatile boolean running = true;

    public Server(int port, Consumer<GameState> onGameStateReceived, Consumer<ChatMessage> onChatMessageReceived, Consumer<ButtonAction> onButtonActionReceived) {
        this(port, onGameStateReceived, onChatMessageReceived, onButtonActionReceived, null);
    }

    public Server(int port, Consumer<GameState> onGameStateReceived, Consumer<ChatMessage> onChatMessageReceived, Consumer<ButtonAction> onButtonActionReceived, Consumer<ConnectionMessage> onConnectionMessageReceived) {
        this.port = port;
        this.onGameStateReceived = onGameStateReceived;
        this.onChatMessageReceived = onChatMessageReceived;
        this.onButtonActionReceived = onButtonActionReceived;
        this.onConnectionMessageReceived = onConnectionMessageReceived;
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

    public void sendButtonAction(ButtonAction action) {
        System.out.println("Server broadcasting ButtonAction: " + action.getButtonId() + " to " + clients.size() + " clients");
        for (ClientHandler client : new ArrayList<>(clients)) {
            client.sendButtonAction(action);
        }
    }

    public void sendConnectionMessage(ConnectionMessage message) {
        System.out.println("Server broadcasting ConnectionMessage: " + message);
        for (ClientHandler client : new ArrayList<>(clients)) {
            client.sendConnectionMessage(message);
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
                System.out.println("ClientHandler started for client: " + clientSocket.getInetAddress().getHostAddress());
                
                // Send a connect message to notify the client is connected
                ConnectionMessage connectMsg = new ConnectionMessage(
                    ConnectionMessage.ConnectionType.CONNECT,
                    null, // Server doesn't have a PlayerType
                    "Client connected from " + clientSocket.getInetAddress().getHostAddress()
                );
                if (onConnectionMessageReceived != null) {
                    onConnectionMessageReceived.accept(connectMsg);
                }
                
                while (clientRunning && running) {
                    Object received = in.readObject();
                    System.out.println("Server received object of type: " + received.getClass().getSimpleName());
                    
                    if (received instanceof GameState gameState) {
                        System.out.println("Server received GameState");
                        onGameStateReceived.accept(gameState);
                        broadcastGameState(gameState);
                    } else if (received instanceof ChatMessage chatMessage) {
                        System.out.println("Server received ChatMessage: " + chatMessage.getMessage());
                        onChatMessageReceived.accept(chatMessage);
                        sendChatMessage(chatMessage);
                    } else if (received instanceof ButtonAction buttonAction) {
                        System.out.println("Server received ButtonAction: " + buttonAction.getButtonId());
                        onButtonActionReceived.accept(buttonAction);
                        sendButtonAction(buttonAction);
                    } else if (received instanceof ConnectionMessage connMessage) {
                        System.out.println("Server received ConnectionMessage: " + connMessage);
                        if (onConnectionMessageReceived != null) {
                            onConnectionMessageReceived.accept(connMessage);
                        }
                        sendConnectionMessage(connMessage);
                    } else {
                        System.out.println("Unknown object type received: " + received.getClass().getName());
                    }
                }
            } catch (IOException | ClassNotFoundException e) {
                System.out.println("Client disconnected: " + clientSocket.getInetAddress().getHostAddress());
                System.err.println("Error in ClientHandler: " + e.getMessage());
                e.printStackTrace();
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

        public void sendButtonAction(ButtonAction action) {
            try {
                if (out != null && clientRunning) {
                    System.out.println("ClientHandler sending ButtonAction: " + action.getButtonId() + 
                                      " to client: " + clientSocket.getInetAddress().getHostAddress());
                    out.writeObject(action);
                    out.flush();
                    System.out.println("ButtonAction sent successfully");
                }
            } catch (IOException e) {
                System.err.println("Error sending ButtonAction: " + e.getMessage());
                e.printStackTrace();
                close();
            }
        }

        public void sendConnectionMessage(ConnectionMessage message) {
            try {
                if (out != null && clientRunning) {
                    System.out.println("ClientHandler sending ConnectionMessage: " + message + 
                                      " to client: " + clientSocket.getInetAddress().getHostAddress());
                    out.writeObject(message);
                    out.flush();
                    System.out.println("ConnectionMessage sent successfully");
                }
            } catch (IOException e) {
                System.err.println("Error sending ConnectionMessage: " + e.getMessage());
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
