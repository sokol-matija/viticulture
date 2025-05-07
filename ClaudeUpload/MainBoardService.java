package com.example.viticulture2.Service;

import com.example.viticulture2.Model.Player;
import com.example.viticulture2.Utils.DialogHandler;
import javafx.scene.control.Button;
import javafx.scene.paint.Color;

public class MainBoardService {

    private MainBoardService(){}
    public static void convertCoinsToWorkers(Player player, Button btn, String message){
        if (player.getCoinsNumber() < 2){
            message = "You do not have enough coins to purchase a worker!\nTo purchase a worker, you need 2 coins.\n\nPlease pick another action!";
            DialogHandler.displayMessageDialog(btn, "Alert!", message);
        }else{
            message = "You successfully purchased a worker. Use him wisely!";
            player.setWorkerNumber(player.getWorkerNumber() + 1);
            player.setCoinsNumber(player.getCoinsNumber() - 2);
            DialogHandler.displayMessageDialog(btn, "", message);
        }
    }

    public static void maxNumGrapeTokenAlertMsg(Player player, Button btn, String message){
        if (player.getGrapeTokenNumber() > player.getField()){
            player.setGrapeTokenNumber(player.getField());
            message = "You have reached the maximum number of grape tokens for the field you own.\n\n" +
                    "The number of grape tokens has been reduced to your field's maximum capacity: " + player.getField();
            DialogHandler.displayMessageDialog(btn, "Alert", message);
        }
    }

    public static void endOfRoundWindow(Player player, Button btn){
        DialogHandler.displayMessageDialog(btn, "Round end", "End of round for " + player.getName());
    }

    public static String colorToHex(Color color) {
        return String.format("#%02X%02X%02X",
                (int)(color.getRed() * 255),
                (int)(color.getGreen() * 255),
                (int)(color.getBlue() * 255));
    }

}
