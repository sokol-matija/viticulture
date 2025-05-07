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

    public static Player initializeLoggedInPlayer(Player player_one, Player player_two){
        GameState.players = new ArrayList<>();
        if(HelloApplication.playerType.name().equals(PlayerType.PLAYER_ONE.name())){
            player_one = PlayerHelper.initializePlayerHelper("default_player_one", 0, 0, Color.WHITE,
                    0, 0, 0, PlayerType.PLAYER_ONE);
            GameState.players.add(player_one);
            return player_one;
        }else{
            player_two = PlayerHelper.initializePlayerHelper("default_player_two", 0, 0, Color.WHITE,
                    0, 0, 0, PlayerType.PLAYER_TWO);
            GameState.players.add(player_two);
            return player_two;
        }
    }



    public static Player initializePlayerHelper(String name, int workerNumber, int coinsNumber, Color color,
                                 int field, int cardPicked, int grapeTokenNumber, PlayerType playerType) {
        return new Player(name, workerNumber, coinsNumber, color, field, cardPicked, grapeTokenNumber, playerType);
    }


}
