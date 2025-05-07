// Client.java
package com.example.viticulture2.Networking;

import com.example.viticulture2.Model.GameState;
import com.example.viticulture2.Model.ChatMessage;
import com.example.viticulture2.Model.ButtonAction;
import com.example.viticulture2.Model.ConnectionMessage;

import java.io.*;
import java.net.Socket;
import java.util.function.Consumer;

public class Client extends Thread {
    private final String host;
    private final int port;
    private final Consumer<GameState> onGameStateReceived;
    private final Consumer<ChatMessage> onChatMessageReceived;
    private final Consumer<ButtonAction> onButtonActionReceived;
    private final Consumer<ConnectionMessage> onConnectionMessageReceived;
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private volatile boolean running = true;
    private boolean connected = false;

    public Client(String host, int port, Consumer<GameState> onGameStateReceived, 
                 Consumer<ChatMessage> onChatMessageReceived,
                 Consumer<ButtonAction> onButtonActionReceived) {
        this(host, port, onGameStateReceived, onChatMessageReceived, onButtonActionReceived, null);
    }
    
    public Client(String host, int port, Consumer<GameState> onGameStateReceived, 
                 Consumer<ChatMessage> onChatMessageReceived,
                 Consumer<ButtonAction> onButtonActionReceived,
                 Consumer<ConnectionMessage> onConnectionMessageReceived) {
        this.host = host;
        this.port = port;
        this.onGameStateReceived = onGameStateReceived;
        this.onChatMessageReceived = onChatMessageReceived;
        this.onButtonActionReceived = onButtonActionReceived;
        this.onConnectionMessageReceived = onConnectionMessageReceived;
    }

    @Override
    public void run() {
        try {
            socket = new Socket(host, port);
            System.out.println("Client connected to server: " + socket.getInetAddress().getHostAddress());
            
            // Important: Output stream must be created before input stream
            out = new ObjectOutputStream(socket.getOutputStream());
            out.flush(); // Important to flush immediately after creation
            in = new ObjectInputStream(socket.getInputStream());
            System.out.println("Client I/O streams initialized");
            
            // Send connection message to server
            ConnectionMessage connectMsg = new ConnectionMessage(
                ConnectionMessage.ConnectionType.CONNECT,
                null, // We'll set the player type in the HelloApplication
                "Client connected"
            );
            sendConnectionMessage(connectMsg);
            connected = true;

            while (running) {
                try {
                    System.out.println("Client waiting for data from server...");
                    Object received = in.readObject();
                    System.out.println("Client received object: " + received);
                    System.out.println("Client received object of type: " + received.getClass().getSimpleName());
                    
                    if (received instanceof GameState gameState) {
                        System.out.println("Client received GameState");
                        onGameStateReceived.accept(gameState);
                    } else if (received instanceof ChatMessage chatMessage) {
                        System.out.println("Client received ChatMessage: " + chatMessage.getMessage());
                        onChatMessageReceived.accept(chatMessage);
                    } else if (received instanceof ButtonAction buttonAction) {
                        System.out.println("Client received ButtonAction with ID: " + buttonAction.getButtonId() + " from " + buttonAction.getPlayerType());
                        System.out.println("Processing ButtonAction through callback...");
                        onButtonActionReceived.accept(buttonAction);
                        System.out.println("ButtonAction processing completed");
                    } else if (received instanceof ConnectionMessage connMessage) {
                        System.out.println("Client received ConnectionMessage: " + connMessage);
                        if (onConnectionMessageReceived != null) {
                            onConnectionMessageReceived.accept(connMessage);
                        }
                    } else {
                        System.out.println("Unknown object type received: " + received.getClass().getName());
                    }
                } catch (ClassNotFoundException e) {
                    System.err.println("Error deserializing object: " + e.getMessage());
                    e.printStackTrace();
                } catch (IOException e) {
                    if (running) {
                        System.err.println("Connection error in receiving loop: " + e.getMessage());
                        e.printStackTrace();
                        break;
                    }
                }
            }
        } catch (Exception e) {
            if (running) {
                System.err.println("Connection error: " + e.getMessage());
                e.printStackTrace();
            }
        } finally {
            connected = false;
            close();
        }
    }
    
    public boolean isConnected() {
        return connected && running && socket != null && socket.isConnected();
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

    public void sendButtonAction(ButtonAction action) {
        try {
            if (out != null && running && connected) {
                System.out.println("Client sending ButtonAction: " + action.getButtonId());
                out.writeObject(action);
                out.flush();
                System.out.println("ButtonAction sent successfully");
            } else {
                System.err.println("Cannot send ButtonAction - connected: " + connected);
            }
        } catch (IOException e) {
            System.err.println("Error sending button action: " + e.getMessage());
            e.printStackTrace();
            close();
        }
    }
    
    public void sendConnectionMessage(ConnectionMessage message) {
        try {
            if (out != null && running) {
                System.out.println("Client sending ConnectionMessage: " + message);
                out.writeObject(message);
                out.flush();
                System.out.println("ConnectionMessage sent successfully");
            }
        } catch (IOException e) {
            System.err.println("Error sending connection message: " + e.getMessage());
            e.printStackTrace();
            close();
        }
    }
    
    public void close() {
        running = false;
        connected = false;
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
