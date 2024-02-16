package it.univr.passaporto_elettronico.controller;

import it.univr.passaporto_elettronico.switchScene;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable {

    @FXML
    private BorderPane loginScene;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loginScene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                event.consume();
            }
        });
    }

    @FXML
    void onClickFromLoginToLoginC(ActionEvent event) throws IOException {
        new switchScene(loginScene, "login_C.fxml");
    }

    @FXML
    void onClickFromLoginToLoginPA(ActionEvent event) throws IOException {
        new switchScene(loginScene, "login_PA.fxml");
    }
}
