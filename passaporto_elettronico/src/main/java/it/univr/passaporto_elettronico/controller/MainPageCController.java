package it.univr.passaporto_elettronico.controller;

import it.univr.passaporto_elettronico.*;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class MainPageCController implements Initializable {

    @FXML
    private BorderPane mainPageCScene;

    @FXML
    private Label welcomeLabel;

    @FXML
    private Label notify_banner1;

    @FXML
    private Label notify_banner2;

    @FXML
    private Label notify_banner3;

    @FXML
    private Label notify_banner4;

    @FXML
    private Button notify_banner5;

    private final DataStorage userDataStorage = new DataStorage("userDataStorageC.dat");

    DataSingleton currentUser = DataSingleton.getInstance();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        welcomeLabel.setText("Ciao, " + LoginCController.getFullName());

        removePastReservations();

        AvailabilityData availabilityData = AvailabilityData.loadFromFile("availabilityData.dat");

        if(currentUser.getUser().getNotifyPeriod().getStartDate() != null){
        // Itera attraverso i giorni nel periodo specificato
        for (LocalDate date = currentUser.getUser().getNotifyPeriod().getStartDate();
             !date.isAfter(currentUser.getUser().getNotifyPeriod().getEndDate());
             date = date.plusDays(1)) {
            String dateString = date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            AvailabilityData.DayData dayData = availabilityData.getAvailabilityDay(dateString);

            // Se il giorno ha dati di disponibilità
            if (dayData != null) {
                // Controlla se contiene il servizio desiderato
                if (dayData.getServices().contains(currentUser.getUser().getNotifyPeriod().getItem())) {
                    // Mostra il banner
                    notify_banner4.setText(currentUser.getUser().getNotifyPeriod().getItem()
                            + "\ndal " + currentUser.getUser().getNotifyPeriod().getStartDate()
                            + "\nal " + currentUser.getUser().getNotifyPeriod().getEndDate());
                    notify_banner5.setText("OK");

                    notify_banner1.setVisible(true);
                    notify_banner2.setVisible(true);
                    notify_banner3.setVisible(true);
                    notify_banner4.setVisible(true);
                    notify_banner5.setVisible(true);

                    javafx.util.Duration duration = javafx.util.Duration.seconds(10);
                    Timeline timeline = new Timeline(new KeyFrame(duration, e -> {
                        notify_banner1.setVisible(false);
                        notify_banner2.setVisible(false);
                        notify_banner3.setVisible(false);
                        notify_banner4.setVisible(false);
                        notify_banner5.setVisible(false);
                    }));
                    timeline.play();

                    currentUser.getUser().getNotifyPeriod().setNotification(null, null, "");
                    break; // Esci dal ciclo una volta trovata una corrispondenza
                }
            }
        }
        }

        mainPageCScene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                event.consume();
            }
        });
    }

    @FXML
    void onClickLogoutC(ActionEvent event) throws IOException {
        // to do current user set null
        new switchScene(mainPageCScene, "login.fxml");
    }
    @FXML
    public void editReservationC(ActionEvent actionEvent) throws IOException {
        new switchScene(mainPageCScene, "editReservation_C.fxml");
    }
    @FXML
    public void addReservationC(ActionEvent actionEvent) throws IOException {
        new switchScene(mainPageCScene, "addReservation_C.fxml");
    }
    @FXML
    public void personalProfileC(ActionEvent actionEvent) throws IOException {
        new switchScene(mainPageCScene, "personalProfile_C.fxml");
    }

    private void removePastReservations() {
        LocalDate today = LocalDate.now();
        UserC currentUserInstance = currentUser.getUser();
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
        System.out.println("Eventuali prenotazioni passate rimosse");
    }

    @FXML
    void notifyAvailabilityC(ActionEvent actionEvent) throws IOException {
        new switchScene(mainPageCScene, "notify_availability_C.fxml");
    }

    @FXML
    void onClickConfirmBannerOK(ActionEvent event) throws IOException{
        notify_banner1.setVisible(false);
        notify_banner2.setVisible(false);
        notify_banner3.setVisible(false);
        notify_banner4.setVisible(false);
        notify_banner5.setVisible(false);
    }
}
