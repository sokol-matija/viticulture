package com.example.viticulture2.Utils;

import com.example.viticulture2.Model.GameState;

/**
 * Interface for controllers that need to access and update the game state.
 */
public interface GameStateAware {
    /**
     * Sets the current game state for this controller.
     */
    void setGameState(GameState gameState);
    
    /**
     * Called when the game state has been updated from another source (e.g., network).
     * Default implementation does nothing. Controllers should override this method
     * if they need to react to external game state changes.
     */
    default void onGameStateUpdated() {
        // Default implementation does nothing
    }
}
