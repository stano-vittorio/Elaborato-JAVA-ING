package it.univr.passaporto_elettronico.controller;

import it.univr.passaporto_elettronico.*;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class EditReservationCController implements Initializable {

    @FXML
    private BorderPane editReservationCScene;

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

    private int selectedIndex;
    private Timeline timeline;

    private List<String> testList = new ArrayList<>();

    private final DataStorage userDataStorage = new DataStorage("userDataStorageC.dat");

    DataSingleton currentUser = DataSingleton.getInstance();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        welcomeLabel.setText("Ciao, " + LoginCController.getFullName());

        System.out.println("Data Storage [GET request]\n" + userDataStorage);

        System.out.println("Current user: " + currentUser.getUser());

        try {
            for (Reservation r : currentUser.getUser().getReservations()) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(r.getDate());

                testList.add((calendar.get(Calendar.DATE) < 10 ? "0" + calendar.get(Calendar.DATE) : calendar.get(Calendar.DATE)) + "/"
                        + ((calendar.get(Calendar.MONTH) + 1) < 10 ? "0" + (calendar.get(Calendar.MONTH) + 1) : (calendar.get(Calendar.MONTH) + 1)) + "/"
                        + calendar.get(Calendar.YEAR));

                testList.add((calendar.get(Calendar.HOUR_OF_DAY) < 10 ? "0" + calendar.get(Calendar.HOUR_OF_DAY) : calendar.get(Calendar.HOUR_OF_DAY)) + ":" +
                        (calendar.get(Calendar.MINUTE) == 0 ? "00" : calendar.get(Calendar.MINUTE)));

                testList.add(r.getItem());
                testList.add(r.getSite());
            }
        }catch (NullPointerException exception){
            exception.printStackTrace();
        }

        System.out.println("testList content: " + testList);

        ListIterator<String> listIterator = testList.listIterator();

        int nButtonToShow = 0;

        try {
            for (Node element : reservationList.getChildren()) {
                if (element instanceof Label l && listIterator.hasNext()) {
                    l.setText(listIterator.next().toUpperCase());
                }

                if (element instanceof Button b && (nButtonToShow < (testList.size()/4))) {
                    b.setVisible(true);
                    nButtonToShow ++;
                }
            }
        }catch (NullPointerException exception){
            exception.printStackTrace();
        }

        editReservationCScene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                event.consume();
            }
        });
    }

    @FXML
    void onClickUndoFromEditReservationCToMainPageC(ActionEvent event) throws IOException {
        new switchScene(editReservationCScene, "mainPage_C.fxml");
    }

    @FXML
    void onClickLogoutC(ActionEvent event) throws IOException {
        currentUser.setUser(null);
        new switchScene(editReservationCScene, "login.fxml");
    }

    private String selectedReservationType; // tipo della prenotazione selezionata

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
        selectedReservationType = reservationLines[2]; // Salva il tipo della prenotazione

        if (selectedReservationType.toLowerCase().contains("rilascio passaporto")) {
            System.out.println("Showing warning label");
            warning_multiple_delete.setVisible(true);
        }


        delete_banner_text2.setText(reservationLines[0] + " - " + reservationLines[1] + "\n"
                + reservationLines[2] + "\n" + reservationLines[3]);

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
                    delete_banner_backgroud.setVisible(false);
                    delete_banner_buttonOK.setVisible(false);
                    delete_banner_buttonNO.setVisible(false);
                    delete_banner_text1.setVisible(false);
                    delete_banner_text2.setVisible(false);
                    delete_banner_text3.setVisible(false);
                    delete_banner_grey.setVisible(false);
                }));
        timeline.play();
    }

    @FXML
    void onClickDeleteBannerOK(ActionEvent event) throws IOException {
        userDataStorage.removeUser(currentUser.getUser());

        // Rimuovi il ritiro passaporto solo se il tipo è "rilascio passaporto per la prima volta"
        if (selectedReservationType.toLowerCase().contains("rilascio passaporto")) {
            currentUser.getUser().getReservations().removeIf(reservation -> reservation.getItem().equalsIgnoreCase("ritiro passaporto"));
        }

        Iterator<Reservation> iterator = currentUser.getUser().getReservations().iterator();

        int index = 1;

        while (iterator.hasNext()) {
            iterator.next();

            System.out.println(index + " " + selectedIndex);

            if (index == selectedIndex) {
                iterator.remove();
            }
            index++;
        }

        userDataStorage.addUser(currentUser.getUser());

        System.out.println("Data Storage [GET request]\n" + userDataStorage);

        warning_multiple_delete.setVisible(false);
        delete_banner_backgroud.setVisible(false);
        delete_banner_buttonOK.setVisible(false);
        delete_banner_buttonNO.setVisible(false);
        delete_banner_text1.setVisible(false);
        delete_banner_text2.setVisible(false);
        delete_banner_text3.setVisible(false);
        delete_banner_grey.setVisible(false);

        new switchScene(editReservationCScene, "editReservation_C.fxml");
    }

    @FXML
    void onClickDeleteBannerNO(ActionEvent event) throws IOException {
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
    void exportToText(ActionEvent event) throws IOException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save as Text and iCal");
        fileChooser.setInitialFileName("Promemoria prenotazione.txt");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Text Files", "*.txt"),
                new FileChooser.ExtensionFilter("iCal Files", "*.ics")
        );
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

                    textWriter.write("** ELENCO PRENOTAZIONI PER " + LoginCController.getFullName().toUpperCase() + " **" + "\n\n");
                    textWriter.write("Totale prenotazioni: " + (testList.size() / 4) + "\n\n");

                    icalWriter.write("BEGIN:VCALENDAR\n");
                    icalWriter.write("VERSION:2.0\n");

                    int prenotazioneNumber = 1;

                    for (int i = 0; i < testList.size(); i += 4) {

                        textWriter.write("Prenotazione " + prenotazioneNumber + "\n");
                        textWriter.write("Data: " + "\t\t" + testList.get(i) + "\n");
                        textWriter.write("Ora: " + "\t\t" + testList.get(i + 1) + "\n");
                        textWriter.write("Servizio: " + "\t" + testList.get(i + 2) + "\n");
                        textWriter.write("Sede: " + "\t\t" + testList.get(i + 3) + "\n\n");

                        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyyMMdd");

                        Date startDate = parseDate(testList.get(i));

                        if (startDate != null) {

                            String startTime = testList.get(i + 1);
                            String formattedStartTime = startTime.replaceAll(":", "") + "00";

                            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HHmmss");
                            LocalTime startTimeObject = LocalTime.parse(formattedStartTime, formatter);

                            String formattedStartTimePlus30Minutes = startTimeObject.plusMinutes(30).format(formatter);

                            icalWriter.write("BEGIN:VEVENT\n");
                            icalWriter.write("DTSTART;TZID=Europe/Rome:" + sdfDate.format(startDate) + "T" + formattedStartTime + "\n");
                            icalWriter.write("DTEND;TZID=Europe/Rome:" + sdfDate.format(startDate) + "T" + formattedStartTimePlus30Minutes + "\n");
                            icalWriter.write("SUMMARY:" + testList.get(i + 2) + "\n");
                            icalWriter.write("LOCATION:" + getLocationDescription(testList.get(i + 3)) + "\n");
                            icalWriter.write("DESCRIPTION:Attenzione!! Si ricorda ai cittadini di presentarsi all'appuntamento" +
                                    " muniti dei seguenti documenti: modulo di richiesta compilato, marca da bollo, " +
                                    "ricevuta del versamento su C/C postale, due fototessera su sfondo bianco, passaporto precedente\n");
                            icalWriter.write("END:VEVENT\n\n");

                            prenotazioneNumber++;
                        }
                    }

                    icalWriter.write("END:VCALENDAR\n");

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
}
