package com.example.viticulture2.Utils;

import com.example.viticulture2.HelloApplication;
import com.example.viticulture2.Model.GameState;
import com.example.viticulture2.Model.Player;
import com.example.viticulture2.Model.PlayerType;
import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;

public class PlayerHelper {


    private PlayerHelper(){}

    public static void checkEndGameRequirements(Player player, Button btn){
        String message = player.getName() + " is the winner! Congratulations! \n\nThank you for playing!";
        if (player.getCoinsNumber() >= 10){
            DialogHandler.displayMessageDialog(btn, "", message);
            Platform.exit();
        }
    }

    public static void displayCurrentPlayerState(Button btn, Player player){
        String message = "Your current state:"
                + "\n\n Worker number: " + player.getWorkerNumber()
                + "\n Coins: " + player.getCoinsNumber()
                + "\n Field: " + player.getField()
                + "\n Grape tokens: " + player.getGrapeTokenNumber()
                + "\n Color: " + getStringOfColor(player);
        DialogHandler.displayMessageDialog(btn, "Player stats", message);
    }
    private static String getStringOfColor(Player player){
        // available player colors:
        // 0xff0000ff -> red
        // 0xffff0000 -> blue
        String red = "0xff0000ff";
        if(player.getPlayerColor().toString().equals(red))
            return "red";
        return "blue";
    }

    public static Player resetPlayerStats(Player player) {
        player.setWorkerNumber(1);
        player.setGrapeTokenNumber(0);
        player.setCoinsNumber(0);
        player.setField(0);
        player.setCardPicked(0);
        return player;
    }

    public static Player getLoggedInPlayer(Player p1, Player p2){
        if(HelloApplication.playerType.name().equals(PlayerType.PLAYER_ONE.name())
        || HelloApplication.playerType.name().equals(PlayerType.SINGLE_PLAYER.name()))
            return p1;
        return p2;

    }

    /**
     * Initialize a new player based on the current player type
     * @param gameState The current game state
     * @return The initialized player
     */
    public static Player initializeLoggedInPlayer(GameState gameState) {
        if (gameState.getPlayers() == null) {
            gameState.setPlayers(new ArrayList<>());
        }
        
        Player newPlayer;
        
        if (HelloApplication.playerType == PlayerType.PLAYER_ONE || HelloApplication.playerType == PlayerType.SINGLE_PLAYER) {
            newPlayer = PlayerHelper.initializePlayerHelper("Player 1", 0, 0, Color.RED,
                    0, 0, 0, HelloApplication.playerType);
        } else {
            newPlayer = PlayerHelper.initializePlayerHelper("Player 2", 0, 0, Color.BLUE,
                    0, 0, 0, PlayerType.PLAYER_TWO);
        }
        
        // Only add the player if they're not already in the list
        if (!gameState.getPlayers().contains(newPlayer)) {
            gameState.getPlayers().add(newPlayer);
        }
        
        return newPlayer;
    }



    public static Player initializePlayerHelper(String name, int workerNumber, int coinsNumber, Color color,
                                 int field, int cardPicked, int grapeTokenNumber, PlayerType playerType) {
        String colorString = color != null ? color.toString() : "";
        return new Player(name, workerNumber, coinsNumber, color, field, cardPicked, grapeTokenNumber, playerType, colorString);
    }


}
