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

public class ProhibitedReservationCController implements Initializable {

    @FXML
    private BorderPane prohibitedReservationCScene;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        prohibitedReservationCScene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                event.consume();
            }
        });
    }

    @FXML
    void onClickLogoutC(ActionEvent event) throws IOException {
        new switchScene(prohibitedReservationCScene, "login.fxml");
    }

    @FXML
    void onClickBackToAddReservationC(ActionEvent event) throws IOException {
        new switchScene(prohibitedReservationCScene, "addReservation_C.fxml");
    }
}