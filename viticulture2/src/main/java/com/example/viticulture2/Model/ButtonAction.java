package com.example.viticulture2.Model;

import java.io.Serializable;

/**
 * Represents a button action that needs to be synchronized between clients
 */
public class ButtonAction implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private final String buttonId;
    private final PlayerType playerType;

    public ButtonAction(String buttonId, PlayerType playerType) {
        this.buttonId = buttonId;
        this.playerType = playerType;
    }

    public String getButtonId() {
        return buttonId;
    }

    public PlayerType getPlayerType() {
        return playerType;
    }
    
    @Override
    public String toString() {
        return "ButtonAction{buttonId='" + buttonId + "', playerType=" + playerType + "}";
    }
} 