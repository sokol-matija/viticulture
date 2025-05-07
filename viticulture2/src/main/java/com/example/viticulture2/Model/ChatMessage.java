package com.example.viticulture2.Model;

import java.io.Serializable;

/**
 * Represents a chat message that can be sent between players
 */
public class ChatMessage implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private final String sender;
    private final String message;
    private final long timestamp;

    public ChatMessage(String sender, String message) {
        this.sender = sender;
        this.message = message;
        this.timestamp = System.currentTimeMillis();
    }

    public String getSender() {
        return sender;
    }

    public String getMessage() {
        return message;
    }

    public long getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return sender + ": " + message;
    }
} 