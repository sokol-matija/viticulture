package com.example.viticulture2.Model;

import javafx.scene.paint.Color;

public class Player {
    private String name;
    private int workerNumber;
    private int coinsNumber;
    private Color playerColor;
    private int field;
    private int cardPicked;
    private int grapeTokenNumber;
    private PlayerType playerType;


    public Player(String name, int workerNumber, int coinsNumber, Color playerColor, int field, int cardPicked, int grapeTokenNumber, PlayerType playerType) {
        this.name = name;
        this.workerNumber = workerNumber;
        this.coinsNumber = coinsNumber;
        this.playerColor = playerColor;
        this.field = field;
        this.cardPicked = cardPicked;
        this.grapeTokenNumber = grapeTokenNumber;
        this.playerType = playerType;
    }

    public Player(){};

    public PlayerType getPlayerType() {
        return playerType;
    }

    public void setPlayerType(PlayerType playerType) {
        this.playerType = playerType;
    }

    public int getGrapeTokenNumber() {
        return grapeTokenNumber;
    }

    public void setGrapeTokenNumber(int grapeTokenNumber) {
        this.grapeTokenNumber = grapeTokenNumber;
    }

    public int getCardPicked() {
        return cardPicked;
    }

    public void setCardPicked(int cardPicked) {
        this.cardPicked = cardPicked;
    }

    public int getField() {
        return field;
    }

    public void setField(int field) {
        this.field = field;
    }

    public Color getPlayerColor() {
        return playerColor;
    }

    public void setPlayerColor(Color playerColor) {
        this.playerColor = playerColor;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getWorkerNumber() {
        return workerNumber;
    }

    public void setWorkerNumber(int workerNumber) {
        this.workerNumber = workerNumber;
    }

    public int getCoinsNumber() {
        return coinsNumber;
    }

    public void setCoinsNumber(int coinsNumber) {
        this.coinsNumber = coinsNumber;
    }

}
