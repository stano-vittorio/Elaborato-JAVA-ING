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

public class ConfirmNotifyCController implements Initializable {

    @FXML
    private BorderPane confirmNotifyCScene;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        confirmNotifyCScene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                event.consume();
            }
        });
    }

    @FXML
    void onClickBackToMainPageC(ActionEvent event) throws IOException {
        new switchScene(confirmNotifyCScene, "mainPage_C.fxml");
    }
    @FXML
    void onClickBackToListReservationC(ActionEvent event) throws IOException {
        new switchScene(confirmNotifyCScene, "editReservation_C.fxml");
    }

    @FXML
    void onClickLogoutC(ActionEvent event) throws IOException {
        new switchScene(confirmNotifyCScene, "login.fxml");
    }

    @FXML
    void onClickBackToAddReservationC(ActionEvent event) throws IOException {
        new switchScene(confirmNotifyCScene, "addReservation_C.fxml");
    }
}