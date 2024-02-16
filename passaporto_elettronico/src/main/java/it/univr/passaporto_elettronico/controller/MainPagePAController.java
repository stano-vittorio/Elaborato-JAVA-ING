package it.univr.passaporto_elettronico.controller;

import it.univr.passaporto_elettronico.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

public class MainPagePAController implements Initializable {

    @FXML
    private BorderPane mainPagePAScene;

    @FXML
    private Label welcomeLabel;

    private AvailabilityData availabilityData = new AvailabilityData();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        welcomeLabel.setText("Ciao, " + LoginPAController.getFullName());

        removePastReservations();

        if (AvailabilityData.loadFromFile("availabilityData.dat") != null) {
            availabilityData = AvailabilityData.loadFromFile("availabilityData.dat");
        }

        try {
            availabilityData.removePreviousAvailability(); // Rimuove le disponibilità precedenti
            availabilityData.saveToFile("availabilityData.dat");
            System.out.println("Eventuali disponibilità passate rimosse");
        } catch (IOException e) {
            e.printStackTrace();
        }

        mainPagePAScene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                event.consume();
            }
        });
    }

    @FXML
    void onClickLogoutPA(ActionEvent event) throws IOException {
        new switchScene(mainPagePAScene, "login.fxml");
    }

    public void listReservationPA(ActionEvent actionEvent) throws IOException {
        new switchScene(mainPagePAScene, "listReservation_PA.fxml");
    }

    public void addAvailabilityPA(ActionEvent actionEvent) throws IOException {
        new switchScene(mainPagePAScene, "addAvailability_PA.fxml");
    }

    private final DataStorage userDataStorage = new DataStorage("userDataStorageC.dat");

    //provare a vedere se bisogna rimuoverle anche da availabilityDate.dat
    private void removePastReservations() {
        LocalDate today = LocalDate.now();

        for (UserC currentUserInstance : userDataStorage.getAllUsers()) {
            Iterator<Reservation> iterator = currentUserInstance.getReservations().iterator();

            while (iterator.hasNext()) {
                Reservation reservation = iterator.next();
                LocalDate reservationDate = reservation.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

                if (reservationDate.isBefore(today)) {
                    iterator.remove();
                }
            }

            userDataStorage.removeUser(currentUserInstance);
            userDataStorage.addUser(currentUserInstance);
        }
        System.out.println("Eventuali prenotazioni passate rimosse");
    }
}
