package com.example.viticulture2.Model;

import javafx.scene.control.Button;
import javafx.scene.paint.Color;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * Represents the current state of the game.
 * This class is serializable to support network transmission.
 */
public class GameState implements Serializable {

    @Serial
    private static final long serialVersionUID = 8039747030694510364L;

    public List<Player> players;
    public Map<String, String> serializableButtons; // String button id, String button color
    private int turn;
    private boolean gameOver;
    private String winnerName;

    public GameState() {
        this.turn = 0;
        this.gameOver = false;
        this.winnerName = null;
        this.serializableButtons = new HashMap<>();
    }

    public Map<String, String> getSerializableButtons() {
        return serializableButtons;
    }

    public void setSerializableButtons(Map<String, String> serializableButtons) {
        this.serializableButtons = serializableButtons;
    }

    /**
     * Updates a button's state in the serializable map
     */
    public void updateButtonState(String buttonId, String color) {
        serializableButtons.put(buttonId, color);
    }

    public List<Player> getPlayers() {
        return players;
    }

    public int getTurn() {
        return turn;
    }

    public Player getCurrentPlayer() {
        return players.get(turn);
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public String getWinnerName() {
        return winnerName;
    }

    public void setPlayers(List<Player> players) {
        this.players = players;
    }

    public void setTurn(int turn) {
        this.turn = turn;
    }

    public void setGameOver(boolean gameOver) {
        this.gameOver = gameOver;
    }

    public void setWinnerName(String winnerName) {
        this.winnerName = winnerName;
    }

    public boolean checkForWin() {
        for (Player p : players) {
            if (p.getCoinsNumber() >= 10) {
                gameOver = true;
                winnerName = p.getName();
                return true;
            }
        }
        return false;
    }

    public void nextTurn() {
        this.turn = (this.turn + 1) % players.size();
    }

    public Player getCurrentPlayerByTurn(){
        if (this.turn == 0)
            return players.get(0);
        return players.get(1);
    }

    /**
     * Checks if the current player is the specified player
     */
    public boolean isCurrentPlayer(Player player) {
        return getCurrentPlayer().getName().equals(player.getName());
    }
}
