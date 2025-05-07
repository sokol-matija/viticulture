package com.example.viticulture2.Model;

import java.io.Serializable;
import java.util.List;

public class GameState implements Serializable {
    public static List<Player> players;
    private int turn;
    private boolean gameOver;
    private String winnerName;

    public GameState() {
        this.turn = 0;
        this.gameOver = false;
        this.winnerName = null;
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
        this.turn = (this.turn == 0) ? 1 : 0;
    }

    public Player getCurrentPlayerByTurn(){
        if (this.turn == 0)
            return players.get(0);
        return players.get(1);
    }

    public boolean isCurrentPlayer(Player player) {
        return getCurrentPlayer().getName().equals(player.getName());
    }
}
