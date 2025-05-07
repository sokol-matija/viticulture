package com.example.viticulture2.Model;

import java.io.Serializable;

/**
 * Message for establishing and maintaining connection between clients
 */
public class ConnectionMessage implements Serializable {
    private static final long serialVersionUID = 1L;
    
    public enum ConnectionType {
        CONNECT,
        DISCONNECT,
        PING,
        PONG
    }
    
    private final ConnectionType type;
    private final PlayerType playerType;
    private final String message;
    
    public ConnectionMessage(ConnectionType type, PlayerType playerType, String message) {
        this.type = type;
        this.playerType = playerType;
        this.message = message;
    }
    
    public ConnectionType getType() {
        return type;
    }
    
    public PlayerType getPlayerType() {
        return playerType;
    }
    
    public String getMessage() {
        return message;
    }
    
    @Override
    public String toString() {
        return "ConnectionMessage{type=" + type + ", playerType=" + playerType + ", message='" + message + "'}";
    }
} 