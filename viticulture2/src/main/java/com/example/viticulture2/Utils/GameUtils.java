package com.example.viticulture2.Utils;

import com.example.viticulture2.Model.GameState;

import java.io.*;

public class GameUtils {

    private static final String SAVE_GAME_FILE_PATH = "resources/save.dat";

    public static void saveGame(GameState gameState) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(SAVE_GAME_FILE_PATH))) {
            oos.writeObject(gameState);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static GameState loadGame() {
        GameState loadedGameState;
        try(ObjectInputStream ois = new ObjectInputStream(new FileInputStream(SAVE_GAME_FILE_PATH))) {
            loadedGameState =  (GameState) ois.readObject();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        return loadedGameState;
    }

}
