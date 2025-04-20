package com.example.viticulture2;

import com.example.viticulture2.Model.GameState;
import com.example.viticulture2.Model.Player;
import com.example.viticulture2.Model.PlayerType;
import com.example.viticulture2.Networking.Server;
import com.example.viticulture2.Utils.DialogHandler;
import com.example.viticulture2.Utils.GameStateAware;
import com.example.viticulture2.Utils.PlayerHelper;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class HelloApplication extends Application {

    private GameState gameState;
    public static PlayerType playerType;

    @Override
    public void start(Stage primaryStage) throws Exception {
        gameState = new GameState();
        loadMainBoard(primaryStage);
        loadWineBoard();
        loadPlayerColorChoice();
        loadMamaAndPapaCards();
    }

    private void loadMainBoard(Stage stage) throws IOException {
        FXMLLoader loader = createLoader("main-board.fxml");
        stage.setTitle("Main Board - " + playerType);
        stage.setScene(new Scene(loader.load()));
        stage.setResizable(false);
        stage.show();
    }

    private void loadWineBoard() throws IOException {
        Stage wineStage = new Stage();
        FXMLLoader loader = createLoader("wine-board.fxml");
        wineStage.setTitle("Wine Board - " + playerType);
        wineStage.setScene(new Scene(loader.load()));
        wineStage.setResizable(false);
        wineStage.show();
    }

    private void loadPlayerColorChoice() throws IOException {
        Stage colorStage = new Stage();
        FXMLLoader loader = createLoader("player-color-choice.fxml");
        colorStage.setTitle("Player Color - " + playerType);
        colorStage.setScene(new Scene(loader.load()));
        colorStage.setResizable(false);
        colorStage.show();
    }

    private void loadMamaAndPapaCards() throws IOException {
        Stage cardsStage = new Stage();
        FXMLLoader loader = createLoader("mama-and-papa.fxml");
        cardsStage.setTitle("Mama/Papa Cards - " + playerType);
        cardsStage.setScene(new Scene(loader.load()));
        cardsStage.setResizable(false);
        cardsStage.show();
    }

    private FXMLLoader createLoader(String fxmlFile) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
        loader.setControllerFactory(type -> {
            try {
                Object controller = type.getDeclaredConstructor().newInstance();
                if (controller instanceof GameStateAware) {
                    ((GameStateAware) controller).setGameState(gameState);
                }
                return controller;
            } catch (Exception e) {
                throw new RuntimeException("Failed to create controller", e);
            }
        });
        return loader;
    }

    public static void main(String[] args) {


        String firstCommandLineArg = args[0];

        Boolean playerTypeExists = false;

        for (PlayerType playerType : PlayerType.values()){
            if (firstCommandLineArg.equals(playerType.toString())){
                playerTypeExists = true;
            }
        }

        if (!playerTypeExists) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Wrong player type");
            alert.setHeaderText(null);
            alert.setContentText("Wrong player type.");
            alert.showAndWait();
            System.exit(0);
        }
        else{
            playerType = PlayerType.valueOf(firstCommandLineArg);
            launch();

        }
    }








}