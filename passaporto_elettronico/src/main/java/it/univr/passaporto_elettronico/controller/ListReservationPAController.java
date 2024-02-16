package it.univr.passaporto_elettronico.controller;

import it.univr.passaporto_elettronico.DataStorage;
import it.univr.passaporto_elettronico.Reservation;
import it.univr.passaporto_elettronico.UserC;
import it.univr.passaporto_elettronico.switchScene;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class ListReservationPAController implements Initializable {

    @FXML
    private BorderPane listReservationPAScene;

    @FXML
    private Label welcomeLabel;

    @FXML
    private GridPane reservationList;

    @FXML
    private Label delete_banner_backgroud;

    @FXML
    private Label delete_banner_text1;

    @FXML
    private Label delete_banner_text2;

    @FXML
    private Label delete_banner_text3;

    @FXML
    private Label delete_banner_grey;

    @FXML
    private Label warning_multiple_delete;

    @FXML
    private Button delete_banner_buttonOK;

    @FXML
    private Button delete_banner_buttonNO;

    @FXML
    private Button viewAll;

    @FXML
    private TextField searchTextField;

    private int selectedIndex;
    private Timeline timeline;

    private final List<String> testList = new ArrayList<>();

    private final DataStorage userDataStorage = new DataStorage("userDataStorageC.dat");

    public void initialize(URL url, ResourceBundle resourceBundle) {
        welcomeLabel.setText("Ciao, " + LoginPAController.getFullName());

        System.out.println("Data Storage [GET request]\n" + userDataStorage);

        try {
            for (UserC user : userDataStorage.getAllUsers()) {
                for (Reservation r : user.getReservations()) {
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(r.getDate());

                    testList.add(user.getTaxIdCode());

                    testList.add((calendar.get(Calendar.DATE) < 10 ? "0" + calendar.get(Calendar.DATE) : String.valueOf(calendar.get(Calendar.DATE))) + "/"
                            + ((calendar.get(Calendar.MONTH) + 1) < 10 ? "0" + (calendar.get(Calendar.MONTH) + 1) : String.valueOf(calendar.get(Calendar.MONTH) + 1)) + "/"
                            + calendar.get(Calendar.YEAR));

                    testList.add((calendar.get(Calendar.HOUR_OF_DAY) < 10 ? "0" + calendar.get(Calendar.HOUR_OF_DAY) : String.valueOf(calendar.get(Calendar.HOUR_OF_DAY))) + ":" +
                            (calendar.get(Calendar.MINUTE) == 0 ? "00" : String.valueOf(calendar.get(Calendar.MINUTE))));

                    testList.add(r.getItem());
                    testList.add(r.getSite());
                }
            }
        } catch (NullPointerException exception) {
            exception.printStackTrace();
        }

        // STAMPA DI VERIFICA
        System.out.println("testList content:");
        for (int i = 0; i < testList.size(); i++) {
            System.out.print(testList.get(i));
            if ((i + 1) % 5 == 0) {
                System.out.println();
            } else {
                System.out.print(" ");
            }
        }

        ListIterator<String> listIterator = testList.listIterator();

        int rowIndex = 0;
        int nButtonToShow = 0;

        try {
            while (listIterator.hasNext()) {

                if (rowIndex >= reservationList.getRowCount()) {
                    RowConstraints rowConstraints = new RowConstraints();
                    rowConstraints.setMinHeight(45);
                    rowConstraints.setPrefHeight(45);
                    rowConstraints.setMaxHeight(45);
                    reservationList.getRowConstraints().add(rowConstraints);

                    reservationList.addRow(rowIndex);
                }

                reservationList.add(createFormattedLabel(listIterator.next()), 0, rowIndex);
                reservationList.add(createFormattedLabel(listIterator.next()), 1, rowIndex);
                reservationList.add(createFormattedLabel(listIterator.next()), 2, rowIndex);
                reservationList.add(createFormattedLabel(listIterator.next().toUpperCase()), 3, rowIndex);
                reservationList.add(createFormattedLabel(listIterator.next().toUpperCase()), 4, rowIndex);

                Button deleteButton = createDeleteButton();
                reservationList.add(deleteButton, 5, rowIndex);
                deleteButton.setVisible(nButtonToShow < reservationList.getRowCount());

                nButtonToShow++;
                rowIndex++;
            }
        } catch (NullPointerException exception) {
            exception.printStackTrace();
        }

        searchTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            viewAll.setVisible(!newValue.trim().isEmpty());
        });

        listReservationPAScene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                event.consume();
            }
        });
    }

    private Button createDeleteButton() {
        Button deleteButton = new Button();
        deleteButton.setMnemonicParsing(false);
        deleteButton.setPrefHeight(11.0);
        deleteButton.setPrefWidth(14.0);
        deleteButton.setStyle("-fx-background-color: transparent;");
        deleteButton.setVisible(false);

        deleteButton.setOnAction(event -> {
            try {
                deleteReservation(event);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        ImageView imageView = new ImageView(new Image(Objects.requireNonNull(getClass().getResource("/it/univr/passaporto_elettronico/icon_delete.png")).toExternalForm()));
        imageView.setFitHeight(32.0);
        imageView.setFitWidth(32.0);
        deleteButton.setGraphic(imageView);

        deleteButton.setCursor(Cursor.HAND);
        return deleteButton;
    }

    private Label createFormattedLabel(String text) {
        Label label = new Label(text);
        label.setStyle("-fx-text-fill: #333333; -fx-font-family: 'Avenir Next'; -fx-font-size: 16px; -fx-font-weight: bold;");
        GridPane.setMargin(label, new Insets(0, 10, 0, 10));
        return label;
    }

    @FXML
    void deleteReservation(ActionEvent event) throws IOException {
        selectedIndex = GridPane.getRowIndex((Button) event.getSource());

        StringBuilder selectedReservationData = new StringBuilder();

        for (Node element : reservationList.getChildren()) {
            if (element instanceof Label && GridPane.getRowIndex(element) == selectedIndex) {
                selectedReservationData.append(((Label) element).getText()).append(" - ");
            }
        }

        if (selectedReservationData.length() > 2) {
            selectedReservationData.setLength(selectedReservationData.length() - 2);
        }

        String[] reservationLines = selectedReservationData.toString().split(" - ");
        String taxIdCode = reservationLines[0]; // Codice fiscale dell'utente
        String selectedReservationType = reservationLines[3]; // Tipo di prenotazione

        delete_banner_text2.setText("Utente: " + reservationLines[0] + "\n" + reservationLines[1] + " - "
                + reservationLines[2] + "\n" + reservationLines[3] + "\n" + reservationLines[4]);

        delete_banner_backgroud.setVisible(true);
        delete_banner_buttonOK.setVisible(true);
        delete_banner_buttonNO.setVisible(true);
        delete_banner_text1.setVisible(true);
        delete_banner_text2.setVisible(true);
        delete_banner_text3.setVisible(true);
        delete_banner_grey.setVisible(true);

        if (timeline != null) {
            timeline.stop();
        }

        javafx.util.Duration duration = javafx.util.Duration.seconds(10);
        timeline = new Timeline();
        timeline.getKeyFrames().addAll(
                new KeyFrame(duration, e -> {
                    warning_multiple_delete.setVisible(false);
                    delete_banner_backgroud.setVisible(false);
                    delete_banner_buttonOK.setVisible(false);
                    delete_banner_buttonNO.setVisible(false);
                    delete_banner_text1.setVisible(false);
                    delete_banner_text2.setVisible(false);
                    delete_banner_text3.setVisible(false);
                    delete_banner_grey.setVisible(false);
                }));
        timeline.play();

        if (selectedReservationType.toLowerCase().contains("rilascio passaporto")) {
            // Se è una prenotazione di "rilascio passaporto...",
            // cerca un'altra prenotazione "ritiro passaporto" per lo stesso utente
            warning_multiple_delete.setVisible(true);
            UserC currentUser = userDataStorage.getAllUsers().stream()
                    .filter(user -> user.getTaxIdCode().equals(taxIdCode))
                    .findFirst()
                    .orElse(null);

            if (currentUser != null) {
                currentUser.getReservations().removeIf(reservation ->
                        reservation.getItem().equalsIgnoreCase("ritiro passaporto"));
                userDataStorage.removeUser(currentUser);
                userDataStorage.addUser(currentUser);
            }
        }
    }

    @FXML
    void onClickDeleteBannerOK(ActionEvent event) throws IOException {
        int rowIndex = selectedIndex;
        int reservationIndex = rowIndex * 5;

        String taxIdCode;
        String dateString;
        String timeString;
        String item;
        String site;

        if (searchTextField.getText().trim().isEmpty()) {
            // Se il campo di ricerca è vuoto, utilizza testList
            taxIdCode = testList.get(reservationIndex);
            dateString = testList.get(reservationIndex + 1);
            timeString = testList.get(reservationIndex + 2);
            item = testList.get(reservationIndex + 3);
            site = testList.get(reservationIndex + 4);
        } else {
            // Se il campo di ricerca non è vuoto, utilizza visibleRows
            List<Node> rowNodes = visibleRows.get(rowIndex);
            String[] reservationLines = new String[rowNodes.size() - 1];
            int i = 0;
            for (Node node : rowNodes) {
                if (node instanceof Label) {
                    reservationLines[i] = ((Label) node).getText();
                    i++;
                }
            }

            taxIdCode = reservationLines[0];
            dateString = reservationLines[1];
            timeString = reservationLines[2];
            item = reservationLines[3];
            site = reservationLines[4];
        }

        UserC currentUser = userDataStorage.getAllUsers().stream()
                .filter(user -> user.getTaxIdCode().equalsIgnoreCase(taxIdCode))
                .findFirst()
                .orElse(null);

        if (currentUser != null) {
            try {
                Date dateToRemove = new SimpleDateFormat("dd/MM/yyyy HH:mm").parse(dateString + " " + timeString);

                currentUser.getReservations().removeIf(reservation ->
                        reservation.getDate().toString().equals(dateToRemove.toString()) &&
                                reservation.getItem().equalsIgnoreCase(item) &&
                                reservation.getSite().equalsIgnoreCase(site));

                userDataStorage.removeUser(currentUser);
                userDataStorage.addUser(currentUser);

                if (item.toLowerCase().contains("rilascio passaporto")) {
                    // Se è una prenotazione di "rilascio passaporto...",
                    // cerca un'altra prenotazione "ritiro passaporto" per lo stesso utente
                    currentUser.getReservations().removeIf(reservation ->
                            reservation.getItem().equalsIgnoreCase("ritiro passaporto"));
                    userDataStorage.removeUser(currentUser);
                    userDataStorage.addUser(currentUser);
                }

            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        warning_multiple_delete.setVisible(false);
        delete_banner_backgroud.setVisible(false);
        delete_banner_buttonOK.setVisible(false);
        delete_banner_buttonNO.setVisible(false);
        delete_banner_text1.setVisible(false);
        delete_banner_text2.setVisible(false);
        delete_banner_text3.setVisible(false);
        delete_banner_grey.setVisible(false);

        new switchScene(listReservationPAScene, "listReservation_PA.fxml");
    }

    @FXML
    void onClickDeleteBannerNO(ActionEvent event) throws IOException {
        for (Node element : reservationList.getChildren()) {
            if (element instanceof Label) {
                element.setStyle("-fx-text-fill: #333333; -fx-font-family: 'Avenir Next'; -fx-font-size: 16px; -fx-font-weight: bold;");
                ;
            }
        }

        warning_multiple_delete.setVisible(false);
        delete_banner_backgroud.setVisible(false);
        delete_banner_buttonOK.setVisible(false);
        delete_banner_buttonNO.setVisible(false);
        delete_banner_text1.setVisible(false);
        delete_banner_text2.setVisible(false);
        delete_banner_text3.setVisible(false);
        delete_banner_grey.setVisible(false);
    }

    @FXML
    void onClickUndoFromListReservationPAToMainPagePA(ActionEvent event) throws IOException {
        new switchScene(listReservationPAScene, "mainPage_PA.fxml");
    }

    @FXML
    void onClickLogoutPA(ActionEvent event) throws IOException {
        new switchScene(listReservationPAScene, "login.fxml");
    }

    private String getLocationDescription(String location) {
        if ("questura di verona".equalsIgnoreCase(location)) {
            return "Questura di Verona - Lungadige Galtarossa 11 | 37133 Verona VR";
        } else if ("commissariato di veronetta".equalsIgnoreCase(location)) {
            return "Commissariato di Veronetta - Via S. Vitale 32 | 37129 Verona VR";
        }
        return location;
    }

    private Date parseDate(String dateString) {
        try {
            if (dateString.matches("\\d{2}/\\d{2}/\\d{4}")) {
                return new SimpleDateFormat("dd/MM/yyyy").parse(dateString);
            } else {
                return new SimpleDateFormat("yyyy-MM-dd").parse(dateString);
            }
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    private List<List<Node>> visibleRows;

    @FXML
    public void searchReservations(KeyEvent keyEvent) throws IOException {
        String searchText = searchTextField.getText().toLowerCase();

        visibleRows = new ArrayList<>();

        ListIterator<String> listIterator = testList.listIterator();

        while (listIterator.hasNext()) {
            List<Node> rowNodes = new ArrayList<>();
            for (int i = 0; i < 5 && listIterator.hasNext(); i++) {
                rowNodes.add(createFormattedLabel(listIterator.next().toUpperCase()));
            }
            Button deleteButton = createDeleteButton();
            rowNodes.add(deleteButton);
            deleteButton.setVisible(false);

            boolean containsText = rowNodes.stream()
                    .filter(node -> node instanceof Label)
                    .map(node -> ((Label) node).getText().toLowerCase())
                    .anyMatch(nodeText -> nodeText.contains(searchText));

            if (containsText) {
                rowNodes.get(rowNodes.size() - 1).setVisible(true);
                visibleRows.add(rowNodes);
            }
        }

        rebuildGridPane(visibleRows);
    }

    private void rebuildGridPane(List<List<Node>> visibleRows) {
        reservationList.getChildren().clear();
        reservationList.getRowConstraints().clear();

        int rowIndex = 0;

        for (List<Node> rowNodes : visibleRows) {
            if (rowIndex >= reservationList.getRowCount()) {
                RowConstraints rowConstraints = new RowConstraints();
                rowConstraints.setMinHeight(45);
                rowConstraints.setPrefHeight(45);
                rowConstraints.setMaxHeight(45);
                reservationList.getRowConstraints().add(rowConstraints);

                reservationList.addRow(rowIndex);
            }

            int columnIndex = 0;
            for (Node node : rowNodes) {
                reservationList.add(node, columnIndex, rowIndex);
                columnIndex++;
            }

            rowIndex++;
        }
    }

    @FXML
    void exportToText(ActionEvent event) throws IOException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save as Text and iCal");
        fileChooser.setInitialFileName("Lista prenotazioni utenti.txt");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Text Files", "*.txt"),
                new FileChooser.ExtensionFilter("iCal Files", "*.ics"));

        File file = fileChooser.showSaveDialog(new Stage());

        if (file != null) {
            String fileName = file.getName();
            int dotIndex = fileName.lastIndexOf('.');

            if (dotIndex > 0 && dotIndex < fileName.length() - 1) {
                String fileNameWithoutExtension = fileName.substring(0, dotIndex);

                BufferedWriter textWriter = null;
                BufferedWriter icalWriter = null;

                try {
                    String icalFilePath = file.getParent() + File.separator + fileNameWithoutExtension + ".ics";

                    textWriter = new BufferedWriter(new FileWriter(file));
                    icalWriter = new BufferedWriter(new FileWriter(icalFilePath));

                    icalWriter.write("BEGIN:VCALENDAR\n");
                    icalWriter.write("VERSION:2.0\n");

                    int prenotazioneNumber = 1;

                    // Controlla se è attiva la ricerca
                    if (searchTextField.getText().trim().isEmpty()) {
                        // Nessuna ricerca, esporta tutte le prenotazioni
                        textWriter.write("""
                                ** ELENCO PRENOTAZIONI UTENTI **

                                """);
                        textWriter.write("Totale prenotazioni: " + (testList.size() / 5) + "\n\n");
                        textWriter.write("Stampa effettuata il " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + " alle " +
                                LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) + " da " + LoginPAController.getFullName() + "\n\n");

                        // Copia il codice da exportAllReservations
                        for (int i = 0; i < testList.size(); i += 5) {
                            textWriter.write("Prenotazione " + prenotazioneNumber + "\n");
                            textWriter.write("Utente: " + "\t" + testList.get(i) + "\n");
                            textWriter.write("Data: " + "\t\t" + testList.get(i + 1) + "\n");
                            textWriter.write("Ora: " + "\t\t" + testList.get(i + 2) + "\n");
                            textWriter.write("Servizio: " + "\t" + testList.get(i + 3) + "\n");
                            textWriter.write("Sede: " + "\t\t" + testList.get(i + 4) + "\n\n");

                            SimpleDateFormat sdfDate = new SimpleDateFormat("yyyyMMdd");
                            Date startDate = parseDate(testList.get(i + 1));

                            if (startDate != null) {
                                String startTime = testList.get(i + 2);
                                String formattedStartTime = startTime.replaceAll(":", "") + "00";

                                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HHmmss");
                                LocalTime startTimeObject = LocalTime.parse(formattedStartTime, formatter);

                                String formattedStartTimePlus30Minutes = startTimeObject.plusMinutes(30).format(formatter);

                                icalWriter.write("BEGIN:VEVENT\n");
                                icalWriter.write("DTSTART;TZID=Europe/Rome:" + sdfDate.format(startDate) + "T" + formattedStartTime + "\n");
                                icalWriter.write("DTEND;TZID=Europe/Rome:" + sdfDate.format(startDate) + "T" + formattedStartTimePlus30Minutes + "\n");
                                icalWriter.write("SUMMARY:" + testList.get(i + 3) + "\n");
                                icalWriter.write("LOCATION:" + (getLocationDescription(testList.get(i + 4))) + "\n");
                                icalWriter.write("DESCRIPTION:Utente: " + testList.get(i) + "\n");

                                icalWriter.write("END:VEVENT\n\n");

                                prenotazioneNumber++;
                            }
                        }

                    } else {
                        // Ricerca attiva, esporta solo le prenotazioni visibili durante la ricerca
                        textWriter.write("""
                                ** ELENCO PRENOTAZIONI UTENTI - ELENCO PERSONALIZZATO **

                                """);
                        textWriter.write("Totale prenotazioni: " + (visibleRows.size()) + "\n\n");
                        textWriter.write("Stampa effettuata il " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + " alle " +
                                LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) + " da " + LoginPAController.getFullName() + "\n\n");

                        // Copia il codice da exportVisibleReservations
                        for (List<Node> rowNodes : visibleRows) {
                            String[] reservationLines = new String[rowNodes.size() - 1];
                            int i = 0;
                            for (Node node : rowNodes) {
                                if (node instanceof Label) {
                                    reservationLines[i] = ((Label) node).getText();
                                    i++;
                                }
                            }

                            textWriter.write("Prenotazione " + prenotazioneNumber + "\n");
                            textWriter.write("Utente: " + "\t" + reservationLines[0] + "\n");
                            textWriter.write("Data: " + "\t\t" + reservationLines[1] + "\n");
                            textWriter.write("Ora: " + "\t\t" + reservationLines[2] + "\n");
                            textWriter.write("Servizio: " + "\t" + reservationLines[3] + "\n");
                            textWriter.write("Sede: " + "\t\t" + reservationLines[4] + "\n\n");

                            SimpleDateFormat sdfDate = new SimpleDateFormat("yyyyMMdd");
                            Date startDate = parseDate(reservationLines[1]);

                            if (startDate != null) {
                                String startTime = reservationLines[2];
                                String formattedStartTime = startTime.replaceAll(":", "") + "00";

                                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HHmmss");
                                LocalTime startTimeObject = LocalTime.parse(formattedStartTime, formatter);

                                String formattedStartTimePlus30Minutes = startTimeObject.plusMinutes(30).format(formatter);

                                icalWriter.write("BEGIN:VEVENT\n");
                                icalWriter.write("DTSTART;TZID=Europe/Rome:" + sdfDate.format(startDate) + "T" + formattedStartTime + "\n");
                                icalWriter.write("DTEND;TZID=Europe/Rome:" + sdfDate.format(startDate) + "T" + formattedStartTimePlus30Minutes + "\n");
                                icalWriter.write("SUMMARY:" + reservationLines[3] + "\n");
                                icalWriter.write("LOCATION:" + (getLocationDescription(reservationLines[4])) + "\n");
                                icalWriter.write("DESCRIPTION:Utente: " + reservationLines[0] + "\n");

                                icalWriter.write("END:VEVENT\n\n");

                                prenotazioneNumber++;
                            }
                        }
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if (textWriter != null) {
                            textWriter.close();
                        }
                        if (icalWriter != null) {
                            icalWriter.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    @FXML
    void onClickViewAll(ActionEvent event) throws IOException {
        searchTextField.setText("");
        searchReservations(null);
    }
}
