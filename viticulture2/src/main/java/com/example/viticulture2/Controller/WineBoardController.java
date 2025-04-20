package com.example.viticulture2.Controller;

import com.example.viticulture2.Model.GameState;
import com.example.viticulture2.Utils.GameStateAware;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.*;

public class WineBoardController implements GameStateAware {
    private GameState gameState;

    @Override
    public void setGameState(GameState gameState) {
        this.gameState = gameState;
    }

    @FXML
    public void initialize() {
    }
}