package com.example.viticulture2.Model;

import javafx.scene.paint.Color;

import java.io.Serial;
import java.io.Serializable;

/**
 * Represents a player in the game.
 * This class is serializable to support network transmission.
 * Note: JavaFX Color objects are marked as transient since they are not serializable.
 */
public class Player implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private String name;
    private int workerNumber;
    private int coinsNumber;
    private String stringOfColor;  // Serializable representation of color
    private transient Color playerColor;  // Transient because JavaFX Color is not serializable
    private int field;
    private int cardPicked;
    private int grapeTokenNumber;
    private PlayerType playerType;

    public Player(String name, int workerNumber, int coinsNumber, Color playerColor,
                 int field, int cardPicked, int grapeTokenNumber, PlayerType playerType, String stringOfColor) {
        this.name = name;
        this.workerNumber = workerNumber;
        this.coinsNumber = coinsNumber;
        this.playerColor = playerColor;
        this.field = field;
        this.cardPicked = cardPicked;
        this.grapeTokenNumber = grapeTokenNumber;
        this.playerType = playerType;
        this.stringOfColor = stringOfColor;
    }

    public Player() {
        // Default constructor for serialization
    }

    // After deserialization, reconstruct the Color object from the string
    private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, ClassNotFoundException {
        in.defaultReadObject();
        if (stringOfColor != null) {
            this.playerColor = Color.valueOf(stringOfColor);
        }
    }

    // Before serialization, ensure the color string is set
    private void writeObject(java.io.ObjectOutputStream out) throws java.io.IOException {
        if (playerColor != null && stringOfColor == null) {
            stringOfColor = playerColor.toString();
        }
        out.defaultWriteObject();
    }

    public String getStringOfColor() {
        return stringOfColor;
    }

    public void setStringOfColor(String stringOfColor) {
        this.stringOfColor = stringOfColor;
        if (stringOfColor != null) {
            this.playerColor = Color.valueOf(stringOfColor);
        }
    }

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
        if (playerColor != null) {
            this.stringOfColor = playerColor.toString();
        }
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
