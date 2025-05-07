package com.example.viticulture2.Service;

import com.example.viticulture2.Model.Player;
import com.example.viticulture2.Utils.DialogHandler;
import javafx.scene.control.Button;

import static com.example.viticulture2.Utils.DialogHandler.displayMessageDialog;

public class MamaAndPapaService {
    public static Runnable showInitialDialog(final Player player, Button cardForFetchingCurrentWindow) {
        return () -> DialogHandler.displayInputDialog(
                cardForFetchingCurrentWindow,
                "Player Name",
                "Welcome to Viticulture",
                "Please enter your name:",
                name -> {
                    player.setName(name);
                    displayCardChoosingMessage(cardForFetchingCurrentWindow, name);
                }
        );
    }

    private static void displayCardChoosingMessage(Button cardForFetchingCurrentWindow, String name) {
        String msg = "Hi " + name + "! Please pick 1 mama or papa card to gain early-game advantage!";
        displayMessageDialog(cardForFetchingCurrentWindow, "Welcome", msg);
    }
    public static String displayPickedCardMessage(Player player){
        return switch (player.getCardPicked()) {
            case 1 -> "You picked Mama card: Emily! \n\nYou have been granted 2 additional workers and default field(5)!";
            case 2 -> "You picked Papa card: Joel! \n\nYou have been granted 1 additional worker and 2 coins!";
            case 3 -> "You picked Mama card: Ariel! \n\nYou have been granted field(7) and 2 coins!";
            case 4 -> "You picked Papa card: Andrew! \n\nYou have been granted field(6) and 3 coins!";
            default -> "";
        };
    }
}
