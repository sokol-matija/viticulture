package com.example.viticulture2.Controller;

import com.example.viticulture2.HelloApplication;
import com.example.viticulture2.Model.ChatMessage;
import com.example.viticulture2.Model.GameState;
import com.example.viticulture2.Utils.GameStateAware;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class ChatController implements GameStateAware {
    @FXML
    private TextArea chatArea;
    
    @FXML
    private TextField messageField;
    
    private GameState gameState;
    private HelloApplication app;

    @FXML
    public void initialize() {
        chatArea.setEditable(false);
    }

    @Override
    public void setGameState(GameState gameState) {
        this.gameState = gameState;
    }

    public void setApp(HelloApplication app) {
        this.app = app;
    }

    @FXML
    private void sendMessage() {
        String message = messageField.getText().trim();
        if (!message.isEmpty()) {
            String sender = "Player " + (HelloApplication.playerType.name().equals("PLAYER_ONE") ? "1" : "2");
            ChatMessage chatMessage = new ChatMessage(sender, message);
            
            // Add message to local chat
            appendMessage(chatMessage);
            
            // Send message to other player
            if (app != null) {
                app.sendChatMessage(chatMessage);
            }
            
            // Clear input field
            messageField.clear();
        }
    }

    public void receiveMessage(ChatMessage message) {
        Platform.runLater(() -> appendMessage(message));
    }

    private void appendMessage(ChatMessage message) {
        chatArea.appendText(message.toString() + "\n");
    }

    @Override
    public void onGameStateUpdated() {
        // Not needed for chat functionality
    }
} 