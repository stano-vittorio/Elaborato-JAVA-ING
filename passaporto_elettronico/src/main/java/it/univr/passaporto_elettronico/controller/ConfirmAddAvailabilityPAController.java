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

public class ConfirmAddAvailabilityPAController implements Initializable {

    @FXML
    private BorderPane confirmAddAvailabilityPAScene;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        confirmAddAvailabilityPAScene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                event.consume();
            }
        });
    }

    @FXML
    void onClickBackToAddAvailabilityPA(ActionEvent event) throws IOException {
        new switchScene(confirmAddAvailabilityPAScene, "addAvailability_PA.fxml");
    }

    @FXML
    void onClickBackToListReservationPA(ActionEvent event) throws IOException {
        new switchScene(confirmAddAvailabilityPAScene, "listReservation_PA.fxml");
    }

    @FXML
    void onClickBackToMainPagePA(ActionEvent event) throws IOException {
        new switchScene(confirmAddAvailabilityPAScene, "mainPage_PA.fxml");
    }

    @FXML
    void onClickLogoutPA(ActionEvent event) throws IOException {
        new switchScene(confirmAddAvailabilityPAScene, "login.fxml");
    }
}
