package com.example.viticulture2.Utils;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.util.function.Consumer;

public class DialogHandler {

    private DialogHandler(){};

    private static Stage createBaseDialog(Window window, String dialogTitle, String backgroundColor) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.WINDOW_MODAL);
        dialog.initOwner(window);
        dialog.setTitle(dialogTitle);

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));
        layout.setStyle(backgroundColor);
        layout.setAlignment(Pos.CENTER);

        Scene scene = new Scene(layout, 300, 200);
        dialog.setScene(scene);

        return dialog;
    }

    public static void displayMessageDialog(Button btn, String dialogTitle, String messageLabel) {
        String backgroundColor = "-fx-background-color: linear-gradient(to bottom, #8B4513, #D2691E);";
        String messageStyle = "-fx-text-fill: white; -fx-font-size: 16px; -fx-font-family: 'Garamond';";
        String buttonStyle = "-fx-background-color: #7D3C98; -fx-text-fill: white; -fx-font-family: 'Garamond';";

        Stage dialog = createBaseDialog(btn.getScene().getWindow(), dialogTitle, backgroundColor);
        VBox layout = (VBox) dialog.getScene().getRoot();

        Label ml = new Label(messageLabel);
        ml.setStyle(messageStyle);
        ml.setWrapText(true);

        Button okButton = new Button("OK");
        okButton.setStyle(buttonStyle);
        okButton.setOnAction(e -> dialog.close());

        layout.getChildren().addAll(ml, okButton);

        dialog.showAndWait();
    }

    public static void displayInputDialog(Button btn, String dialogTitle, String headerText, String promptText,
                                          Consumer<String> onConfirm) {
        String backgroundColor = "-fx-background-color: linear-gradient(to bottom, #8B4513, #D2691E);";
        String messageStyle = "-fx-text-fill: white; -fx-font-size: 16px; -fx-font-family: 'Garamond';";
        String headerStyle = "-fx-text-fill: white; -fx-font-size: 24px; -fx-font-family: 'Garamond';";
        String buttonStyle = "-fx-background-color: #7D3C98; -fx-text-fill: white; -fx-font-family: 'Garamond';";

        Stage dialog = createBaseDialog(btn.getScene().getWindow(), dialogTitle, backgroundColor);
        VBox layout = (VBox) dialog.getScene().getRoot();

        Label titleLabel = new Label(headerText);
        titleLabel.setStyle(headerStyle);

        Label promptLabel = new Label(promptText);
        promptLabel.setStyle(messageStyle);

        TextField nameField = new TextField();
        nameField.setPromptText("Your name");

        Button confirmButton = new Button("Confirm");
        confirmButton.setStyle(buttonStyle);
        confirmButton.setOnAction(e -> {
            String input = nameField.getText().trim();
            if (!input.isEmpty()) {
                onConfirm.accept(input);
                dialog.close();
            }
        });

        layout.getChildren().addAll(titleLabel, promptLabel, nameField, confirmButton);

        dialog.showAndWait();
    }

    public static void showGenericDialog(String title, String content, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }




}
