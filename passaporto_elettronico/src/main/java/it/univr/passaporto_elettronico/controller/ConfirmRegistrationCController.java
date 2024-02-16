package it.univr.passaporto_elettronico.controller;

import it.univr.passaporto_elettronico.switchScene;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import javafx.fxml.Initializable;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;

import java.io.*;
import java.net.URL;
import java.util.ResourceBundle;

public class ConfirmRegistrationCController implements Initializable {

    @FXML
    private BorderPane confirmRegistrationCScene;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        confirmRegistrationCScene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                event.consume();
                try {
                    onClickBackToLoginC(new ActionEvent());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    @FXML
    void onClickBackToLoginC(ActionEvent event) throws IOException {
        new switchScene(confirmRegistrationCScene, "login_C.fxml");
    }
}