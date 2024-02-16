package it.univr.passaporto_elettronico.controller;

import it.univr.passaporto_elettronico.DataSingleton;
import it.univr.passaporto_elettronico.DataStorage;
import it.univr.passaporto_elettronico.switchScene;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import javafx.util.Callback;

public class NotifyAvailabilityCController implements Initializable {

    @FXML
    private DatePicker data_startNotifyC;

    @FXML
    private DatePicker data_endNotifyC;

    @FXML
    private BorderPane notifyAvailabilityCScene;

    @FXML
    private MenuButton notify_reservation_selection;

    @FXML
    private Label previus_notify;

    @FXML
    private Label warning_empty_label;

    @FXML
    private Label welcomeLabel;

    private final DataStorage userDataStorage = new DataStorage("userDataStorageC.dat");

    DataSingleton currentUser = DataSingleton.getInstance();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        welcomeLabel.setText("Ciao, " + LoginCController.getFullName());
        previus_notify.setVisible(false);
        if(currentUser.getUser().getNotifyPeriod().getStartDate() != null){
            previus_notify.setText("Notifica attiva per " + currentUser.getUser().getNotifyPeriod().getItem()
            + " dal " + currentUser.getUser().getNotifyPeriod().getStartDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
            + " al " + currentUser.getUser().getNotifyPeriod().getEndDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            previus_notify.setVisible(true);
        }

        data_endNotifyC.setDisable(true);
        // Aggiungi un listener per monitorare le modifiche alla selezione di data_startNotifyC
        data_startNotifyC.valueProperty().addListener((observable, oldValue, newValue) -> {
            // Disabilita data_endNotifyC se viene deselezionata la data in data_startNotifyC
            data_endNotifyC.setDisable(newValue == null); // Abilita data_endNotifyC quando viene selezionata una data in data_startNotifyC
            // Quando viene cambiata la data iniziale, cancella la data finale
            data_endNotifyC.setValue(null);
        });

        // Imposta la data minima consentita come oggi per data_startNotifyC
        final Callback<DatePicker, DateCell> startNotifyDayCellFactory = new Callback<>() {
            @Override
            public DateCell call(final DatePicker datePicker) {
                return new DateCell() {
                    @Override
                    public void updateItem(LocalDate item, boolean empty) {
                        super.updateItem(item, empty);
                        // Disabilita i giorni precedenti a oggi per data_startNotifyC
                        if (item.isBefore(LocalDate.now())) {
                            setDisable(true);
                            setStyle("-fx-background-color: #ffc0cb;"); // Imposta lo stile per i giorni disabilitati
                        }
                        // Se è oggi e l'ora attuale è dopo le 16:30, disabilita anche oggi
                        if (item.equals(LocalDate.now()) && LocalTime.now().isAfter(LocalTime.of(16, 30))) {
                            setDisable(true);
                            setStyle("-fx-background-color: #ffc0cb;"); // Imposta lo stile per i giorni disabilitati
                        }
                    }
                };
            }
        };
        data_startNotifyC.setDayCellFactory(startNotifyDayCellFactory);

        // Imposta la data minima consentita come la data selezionata in data_startNotifyC per data_endNotifyC
        final Callback<DatePicker, DateCell> endNotifyDayCellFactory = new Callback<>() {
            @Override
            public DateCell call(final DatePicker datePicker) {
                return new DateCell() {
                    @Override
                    public void updateItem(LocalDate item, boolean empty) {
                        super.updateItem(item, empty);
                        // Disabilita le date precedenti alla data selezionata in data_startNotifyC per data_endNotifyC
                        if (item.isBefore(data_startNotifyC.getValue())) {
                            setDisable(true);
                            setStyle("-fx-background-color: #ffc0cb;"); // Imposta lo stile per i giorni disabilitati
                        }
                    }
                };
            }
        };
        data_endNotifyC.setDayCellFactory(endNotifyDayCellFactory);

        notifyAvailabilityCScene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                event.consume();
            }
        });
    }

    @FXML
    void onClickLogoutC(ActionEvent event) throws IOException {
        new switchScene(notifyAvailabilityCScene, "login.fxml");
    }

    @FXML
    void onClickUndoFromNotifyAvailabilityCToMainPageC(ActionEvent event) throws IOException {
        new switchScene(notifyAvailabilityCScene, "mainPage_C.fxml");
    }

    @FXML
    void saveNotify(ActionEvent event) throws IOException {
        if (data_startNotifyC.getValue() == null || data_endNotifyC.getValue() == null || notify_reservation_selection.getText().equalsIgnoreCase("seleziona il servizio desiderato")) {
            warning_empty_label.setVisible(true);
            javafx.util.Duration duration = javafx.util.Duration.seconds(5);
            new Timeline(new KeyFrame(duration, e -> warning_empty_label.setVisible(false))).play();
        }else {
            userDataStorage.removeUser(currentUser.getUser());
            currentUser.getUser().getNotifyPeriod().setNotification(data_startNotifyC.getValue(), data_endNotifyC.getValue(), notify_reservation_selection.getText());
            userDataStorage.addUser(currentUser.getUser());
            System.out.println(currentUser.getUser().getNotifyPeriod());
            new switchScene(notifyAvailabilityCScene, "confirm_notify_C.fxml");
        }
    }

    @FXML
    void getNotifyReservationSelection(ActionEvent event) throws IOException{
        notify_reservation_selection.setText(((MenuItem) event.getSource()).getText());
    }
}
