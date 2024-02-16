package it.univr.passaporto_elettronico.controller;

import it.univr.passaporto_elettronico.*;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class AddAvailabilityPAController implements Initializable {

    @FXML
    private BorderPane addAvailabilityPAScene;

    @FXML
    private Label welcomeLabel;

    @FXML
    private Label monthLabel;

    @FXML
    private Label yearLabel;

    @FXML
    private Label warning_empty_fields;

    @FXML
    private Label warning_delete_prohibited;

    @FXML
    private GridPane availabilityDay;

    @FXML
    private GridPane reservationTimeTable;

    @FXML
    private GridPane reservation_selection;

    @FXML
    private GridPane site_selection;

    @FXML
    private Button goBackMonthButton;

    @FXML
    private Button goForwardMonthButton;

    private final DataStorage userDataStorage = new DataStorage("userDataStorageC.dat");
    private final Set<String> bookedDays = new TreeSet<>();

    private static final DropShadow shadow = new DropShadow();

    private AvailabilityData availabilityData = new AvailabilityData();

    private static final String[] monthName = {
            "GENNAIO",
            "FEBBRAIO",
            "MARZO",
            "APRILE",
            "MAGGIO",
            "GIUGNO",
            "LUGLIO",
            "AGOSTO",
            "SETTEMBRE",
            "OTTOBRE",
            "NOVEMBRE",
            "DICEMBRE"
    };

    private static Calendar currentDate = new GregorianCalendar();
    private static final Calendar today = new GregorianCalendar();
    DataSingleton currentUser = DataSingleton.getInstance();

    private final Set<String> selectedDays = new TreeSet<>();
    private final Set<String> selectedTimes = new TreeSet<>();
    private final Set<String> selectedServices = new TreeSet<>();
    private final Set<String> selectedSites = new TreeSet<>();

    private void initializeGridPanesIndexes(GridPane gridPane) {
        for (Node node : gridPane.getChildren()) {
            if (GridPane.getColumnIndex(node) == null) {
                GridPane.setColumnIndex(node, 0);
            }
            if (GridPane.getRowIndex(node) == null) {
                GridPane.setRowIndex(node, 0);
            }
        }
    }

    private void initializeAllGridPanesIndexes() {
        initializeGridPanesIndexes(availabilityDay);
        initializeGridPanesIndexes(reservationTimeTable);
        initializeGridPanesIndexes(reservation_selection);
        initializeGridPanesIndexes(site_selection);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        welcomeLabel.setText("Ciao, " + LoginPAController.getFullName());

        if (AvailabilityData.loadFromFile("availabilityData.dat") != null) {
            availabilityData = AvailabilityData.loadFromFile("availabilityData.dat");
        }

        for (UserC user : userDataStorage.getAllUsers()) {
            for (Reservation r : user.getReservations()) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(r.getDate());

                bookedDays.add(String.format("%02d/%02d/%04d",
                        calendar.get(Calendar.DAY_OF_MONTH),
                        calendar.get(Calendar.MONTH) + 1,
                        calendar.get(Calendar.YEAR)));
            }
        }

        initializeAllGridPanesIndexes();

        currentDate = Calendar.getInstance();

        System.out.println("Elenco delle prenotazioni presenti:");
        for (String day : availabilityData.getAllDays()) {
            AvailabilityData.DayData dayData = availabilityData.getAvailabilityDay(day);
            System.out.println(day + " = " + dayData.getTimes() + ", " + dayData.getServices() + ", " + dayData.getSites());
        }

        updateScene(0, "disable previous");

        for (Node node : availabilityDay.getChildren()) {
            Button button = (Button) node;
            int buttonDate = Integer.parseInt(button.getText());

            boolean isCurrentMonthAndYear = currentDate.get(Calendar.YEAR) == today.get(Calendar.YEAR) &&
                    currentDate.get(Calendar.MONTH) == today.get(Calendar.MONTH);

            boolean isBeforeToday = buttonDate < today.get(Calendar.DATE);

            if (isCurrentMonthAndYear && isBeforeToday) {
                button.setDisable(true);
            }
        }

        goBackMonthButton.setVisible(false);
        goForwardMonthButton.setVisible(true);

        shadow.setSpread(0.75);
        shadow.setBlurType(BlurType.GAUSSIAN);
        shadow.setColor(Color.rgb(26, 142, 232));

        addAvailabilityPAScene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                event.consume();
                try {
                    saveAvailability(new ActionEvent());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    @FXML
    void onClickUndoFromAddAvailabilityPAToMainPagePA(ActionEvent event) throws IOException {
        new switchScene(addAvailabilityPAScene, "mainPage_PA.fxml");
    }

    @FXML
    void onClickLogoutPA(ActionEvent event) throws IOException {
        new switchScene(addAvailabilityPAScene, "login.fxml");
        currentUser.setUser(null);
    }

    @FXML
    void goBackMonth() {
        goForwardMonthButton.setVisible(true);
        updateScene(-1, "disable previous");
        selectedDays.clear();
    }

    @FXML
    void goForwardMonth() {
        goBackMonthButton.setVisible(true);
        updateScene(1, "disable subsequent ones");
        selectedDays.clear();
        enableAllTimes();
    }

    private void updateScene(int offset, String condition) {

        currentDate.set(currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH) + offset, 1);

        monthLabel.setText(monthName[currentDate.get(Calendar.MONTH)]);
        yearLabel.setText(String.valueOf(currentDate.get(Calendar.YEAR)));

        int counterA = 1;

        // Nel caso di 1 Dicembre 2023, ritorna 6 in day of week, ovvero venerdì
        // Per sapere le caselle da escludere (5), tolgo 1, ma dal momento che il conteggio parte da 0, tolgo un ulteriore unità
        int counterB = 2;

        int dayOfWeek = currentDate.get(Calendar.DAY_OF_WEEK);

        for (Node node : availabilityDay.getChildren()) {
            Button button = (Button) node;

            button.setEffect(null);

            if (counterB < dayOfWeek) {
                button.setText("0");
                button.setVisible(false);

                counterB++;

            } else if (counterA <= currentDate.getActualMaximum(Calendar.DAY_OF_MONTH)) {
                button.setVisible(true);
                button.setDisable(false);
                button.setText(String.valueOf(counterA));

                String currentDateFormatted = String.format("%02d/%02d/%04d",
                        counterA,
                        currentDate.get(Calendar.MONTH) + 1,
                        currentDate.get(Calendar.YEAR));

                if (bookedDays.contains(currentDateFormatted)) {
                    button.setStyle("-fx-background-color: #abceb8;");
                } else if (availabilityData.containsDay(currentDateFormatted)) {
                    button.setStyle("-fx-background-color: lightblue;");
                } else {
                    button.setStyle("-fx-background-color: #CCCC");
                }

                counterA++;

            } else {
                button.setText("0");
                button.setVisible(false);
            }

            if (condition.equals("disable previous")) {
                if (currentDate.get(Calendar.YEAR) == today.get(Calendar.YEAR) &&
                        currentDate.get(Calendar.MONTH) == today.get(Calendar.MONTH) &&
                        Integer.parseInt(button.getText()) < today.get(Calendar.DATE)) {

                    button.setDisable(true);
                    button.setStyle("-fx-background-color: #CCCC");
                    goBackMonthButton.setVisible(false);

                } else if (GridPane.getColumnIndex(node) == 6) {
                    button.setDisable(true);
                }

                if (currentDate.get(Calendar.YEAR) == today.get(Calendar.YEAR) &&
                        currentDate.get(Calendar.MONTH) == today.get(Calendar.MONTH) &&
                        Integer.parseInt(button.getText()) == today.get(Calendar.DATE) &&
                        LocalTime.now().isAfter(LocalTime.of(16, 30))) {

                    button.setDisable(true);
                    button.setStyle("-fx-background-color: #CCCC");
                }
            } else if (condition.equals("disable subsequent ones")) {
                if (GridPane.getColumnIndex(node) == 6) {
                    button.setDisable(true);
                }
            }
        }
    }

    @FXML
    void saveAvailability(ActionEvent event) throws IOException {

        bookedDays.clear(); // Svuota la lista per ricostruirla
        for (UserC user : userDataStorage.getAllUsers()) {
            for (Reservation r : user.getReservations()) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(r.getDate());

                bookedDays.add(String.format("%02d/%02d/%04d",
                        calendar.get(Calendar.DAY_OF_MONTH),
                        calendar.get(Calendar.MONTH) + 1,
                        calendar.get(Calendar.YEAR)));
            }
        }

        if (selectedDays.isEmpty() || selectedTimes.isEmpty() || selectedServices.isEmpty() || selectedSites.isEmpty()) {
            warning_delete_prohibited.setVisible(false);
            warning_empty_fields.setVisible(true);
            javafx.util.Duration duration = javafx.util.Duration.seconds(5);
            new Timeline(new KeyFrame(duration, e -> warning_empty_fields.setVisible(false))).play();
            warning_delete_prohibited.setVisible(false);

        } else {
            for (String selectedDay : selectedDays) {
                if (bookedDays.contains(selectedDay)) {
                    warning_empty_fields.setVisible(false);
                    warning_delete_prohibited.setVisible(true);
                    javafx.util.Duration duration = javafx.util.Duration.seconds(5);
                    new Timeline(new KeyFrame(duration, e -> warning_delete_prohibited.setVisible(false))).play();
                    return;
                }
            }

            for (String selectedDay : selectedDays) {
                availabilityData.addAvailability(selectedDay, selectedTimes, selectedServices, selectedSites);
            }

            availabilityData.saveToFile("availabilityData.dat");

            for (String day : availabilityData.getAllDays()) {
                AvailabilityData.DayData dayData = availabilityData.getAvailabilityDay(day);
                System.out.println(day + " = " + dayData.getTimes() + ", " + dayData.getServices() + ", " + dayData.getSites());
            }

            new switchScene(addAvailabilityPAScene, "confirm_add_availability_PA.fxml");
        }
    }



    @FXML
    void onClickGetAvailabilityDay(ActionEvent event) throws IOException {
        Button clickedButton = (Button) event.getSource();
        String clickedDay = clickedButton.getText();

        String fullDate = String.format("%02d/%02d/%04d",
                Integer.parseInt(clickedDay),
                currentDate.get(Calendar.MONTH) + 1,
                currentDate.get(Calendar.YEAR));

        if (clickedButton.getEffect() != null) {
            //When Day is Not Selected

            //Clear all
            for (Node node : availabilityDay.getChildren()) {
                if (node instanceof Button button && button.isVisible() && !button.isDisabled()) {
                    button.setEffect(null);
                }
            }
            removeEffectsForAll();

            selectedDays.remove(fullDate);
            selectedTimes.clear();
            selectedSites.clear();
            selectedServices.clear();

            if(!selectedDays.isEmpty()) {
                for (String day : selectedDays) {
                    try {
                        for (Node node : availabilityDay.getChildren())
                            if (node instanceof Button button && button.isVisible() && !button.isDisabled()) {
                                if(day.substring(0, 1).equalsIgnoreCase("0")) {
                                    if (button.getText().equalsIgnoreCase(day.substring(1, 2))) {
                                        button.setEffect(shadow);
                                    }
                                } else {
                                    if (button.getText().equalsIgnoreCase(day.substring(0, 2))) {
                                        button.setEffect(shadow);
                                    }
                                }
                            }
                    } catch (NullPointerException ignore) {
                    }

                    //Get from availabilityData day selected info
                    AvailabilityData.DayData dayData = availabilityData.getAvailabilityDay(day);

                    //Check which services are present
                    for (Node node : reservation_selection.getChildren()) {
                        if (node instanceof Button button && button.isVisible() && !button.isDisabled()) {
                            try {
                                for (String service : dayData.getServices()) {
                                    if (button.getText().equalsIgnoreCase(service)) {
                                        button.setEffect(shadow);
                                        selectedServices.add(button.getText());
                                    }
                                }
                            } catch (NullPointerException ignored) {
                            }
                        }
                    }

                    //Check which sites are present
                    for (Node node : site_selection.getChildren()) {
                        if (node instanceof Button button && button.isVisible() && !button.isDisabled()) {
                            try {
                                for (String site : dayData.getSites()) {
                                    if (button.getText().equalsIgnoreCase(site)) {
                                        button.setEffect(shadow);
                                        selectedSites.add(button.getText());
                                    }
                                }
                            } catch (NullPointerException ignored) {
                            }
                        }
                    }

                    //Check which times are present
                    for (Node node : reservationTimeTable.getChildren()) {
                        if (node instanceof Button button && button.isVisible() && !button.isDisabled()) {
                            try {
                                for (String time : dayData.getTimes()) {
                                    if (button.getText().equalsIgnoreCase(time)) {
                                        button.setEffect(shadow);
                                        selectedTimes.add(button.getText());
                                    }
                                }
                            } catch (NullPointerException ignored) {
                            }
                        }
                    }
                }
            }else {
                enableAllTimes();
            }

            String todayDateFormatted = String.format("%02d/%02d/%04d",
                    today.get(Calendar.DAY_OF_MONTH),
                    today.get(Calendar.MONTH) + 1,
                    today.get(Calendar.YEAR));

            if(selectedDays.size() == 1 && selectedDays.contains(todayDateFormatted)){
                disablePastTimes();
            }

            System.out.println(selectedDays + "\n" + selectedServices + "\n" + selectedSites + "\n" + selectedTimes);

        } else {
            //When Day is Selected
            clickedButton.setEffect(shadow);
            selectedDays.add(fullDate);

            //Get from availabilityData day selected info
            AvailabilityData.DayData dayData = availabilityData.getAvailabilityDay(fullDate);

            //Check which services are present
            for (Node node : reservation_selection.getChildren()) {
                if (node instanceof Button button && button.isVisible() && !button.isDisabled()) {
                    try {
                        for (String service : dayData.getServices()) {
                            if (button.getText().equalsIgnoreCase(service)) {
                                button.setEffect(shadow);
                                selectedServices.add(button.getText());
                            }
                        }
                    }catch (NullPointerException ignored){
                    }
                }
            }

            //Check which sites are present
            for (Node node : site_selection.getChildren() ) {
                if (node instanceof Button button && button.isVisible() && !button.isDisabled()) {
                    try {
                        for (String site : dayData.getSites()) {
                            if (button.getText().equalsIgnoreCase(site)) {
                                button.setEffect(shadow);
                                selectedSites.add(button.getText());
                            }
                        }
                    }catch (NullPointerException ignored){
                }
                }
            }

            //Check which times are present
            for (Node node : reservationTimeTable.getChildren()) {
                if (node instanceof Button button && button.isVisible()) {
                    button.setDisable(false);
                    try{
                        for(String time : dayData.getTimes()) {
                            if (button.getText().equalsIgnoreCase(time)) {
                                button.setEffect(shadow);
                                selectedTimes.add(button.getText());
                            }
                        }
                    }catch (NullPointerException ignored){
                    }
                }
            }

            String todayDateFormatted = String.format("%02d/%02d/%04d",
                    today.get(Calendar.DAY_OF_MONTH),
                    today.get(Calendar.MONTH) + 1,
                    today.get(Calendar.YEAR));

            if(selectedDays.size() == 1 && selectedDays.contains(todayDateFormatted)){
                disablePastTimes();
            }

            System.out.println(selectedDays + "\n" + selectedServices + "\n" + selectedSites + "\n" + selectedTimes);
        }
    }

    @FXML
    void onClickGetReservationTime(ActionEvent event) throws IOException {
        Button clickedButton = (Button) event.getSource();
        String time = clickedButton.getText();

        if (clickedButton.getEffect() != null) {
            selectedTimes.remove(time);
            clickedButton.setEffect(null);
        } else {
            selectedTimes.add(time);
            System.out.println(selectedTimes);
            clickedButton.setEffect(shadow);
        }
    }

    @FXML
    void onClickAvailabilitySelection(ActionEvent event) throws IOException {
        Button clickedButton = (Button) event.getSource();
        String serviceName = clickedButton.getText();

        if (clickedButton.getEffect() != null) {
            selectedServices.remove(serviceName);
            clickedButton.setEffect(null);
        } else {
            selectedServices.add(serviceName);
            System.out.println(selectedServices);
            clickedButton.setEffect(shadow);
        }
    }

    @FXML
    void onClickGetSiteSelection(ActionEvent event) throws IOException {
        Button clickedButton = (Button) event.getSource();
        String siteName = clickedButton.getText();

        if (clickedButton.getEffect() != null) {
            selectedSites.remove(siteName);
            clickedButton.setEffect(null);
        } else {
            selectedSites.add(siteName);
            System.out.println(selectedSites);
            clickedButton.setEffect(shadow);
        }
    }

    private void disablePastTimes() {
        LocalTime currentTime = LocalTime.now();

        for (Node node : reservationTimeTable.getChildren()) {
            if (node instanceof Button button) {
                String buttonText = button.getText();

                LocalTime buttonTime = LocalTime.parse(buttonText);

                if (currentTime.isAfter(buttonTime)) {
                    button.setDisable(true);
                    button.setEffect(null);
                    selectedTimes.remove(button.getText());
                }
            }
        }
    }

    private void enableAllTimes() {
        for (Node node : reservationTimeTable.getChildren()) {
            if (node instanceof Button button) {
                button.setDisable(false);
            }
        }
    }

    private void removeEffectsForAll() {
        for (Node node : reservation_selection.getChildren()) {
            if (node instanceof Button button) {
                button.setEffect(null);
            }
        }

        for (Node node : site_selection.getChildren()) {
            if (node instanceof Button button) {
                button.setEffect(null);
            }
        }

        for (Node node : reservationTimeTable.getChildren()) {
            if (node instanceof Button button) {
                button.setEffect(null);
            }
        }
    }

    @FXML
    void onClickDeleteDay(ActionEvent event) throws IOException {

        bookedDays.clear(); // Svuota la lista per ricostruirla
        for (UserC user : userDataStorage.getAllUsers()) {
            for (Reservation r : user.getReservations()) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(r.getDate());

                bookedDays.add(String.format("%02d/%02d/%04d",
                        calendar.get(Calendar.DAY_OF_MONTH),
                        calendar.get(Calendar.MONTH) + 1,
                        calendar.get(Calendar.YEAR)));
            }
        }

        if (!selectedDays.isEmpty()) {
            Iterator<String> iterator = selectedDays.iterator();
            while (iterator.hasNext()) {
                String selectedDay = iterator.next();

                if (bookedDays.contains(selectedDay)) {
                    warning_empty_fields.setVisible(false);
                    warning_delete_prohibited.setVisible(true);
                    javafx.util.Duration duration = javafx.util.Duration.seconds(5);
                    new Timeline(new KeyFrame(duration, e -> warning_delete_prohibited.setVisible(false))).play();
                } else {
                    iterator.remove(); // Rimuovi l'elemento dalla lista attraverso l'iteratore

                    // Rimuovi dall'oggetto availabilityData
                    availabilityData.removeDay(selectedDay);
                    warning_delete_prohibited.setVisible(false);
                }
            }

            // Salva le modifiche
            availabilityData.saveToFile("availabilityData.dat");

            // Rimuovi effetti visivi
            for (Node node : availabilityDay.getChildren()) {
                if (node instanceof Button button && button.isVisible() && !button.isDisabled()) {
                    button.setEffect(null);
                }
            }

            // Pulisci le liste
            selectedSites.clear();
            selectedServices.clear();
            selectedTimes.clear();

            enableAllTimes();
            removeEffectsForAll();
            updateScene(0, "disable previous");
        }
    }


    @FXML
    void onClickSelectAllDays(ActionEvent event) throws IOException {
        if(((Button) event.getSource()).getText().equalsIgnoreCase("Seleziona tutto")){
            ((Button) event.getSource()).setText("Deseleziona tutto");

            LocalDate localDate = currentDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            int currentMonth = localDate.getMonthValue();
            int currentYear = localDate.getYear();

            for (Node node : availabilityDay.getChildren()) {
                if (node instanceof Button button && button.isVisible() && !button.isDisabled()) {
                    String day = button.getText();

                    String fullDate = String.format("%02d/%02d/%04d", Integer.parseInt(day), currentMonth, currentYear);
                    button.setEffect(shadow);
                    selectedDays.add(fullDate);

                    //Get from availabilityData day selected info
                    AvailabilityData.DayData dayData = availabilityData.getAvailabilityDay(fullDate);

                    //Check which services are present
                    for (Node nodeServices : reservation_selection.getChildren()) {
                        if (nodeServices instanceof Button buttonServices && buttonServices.isVisible() && !buttonServices.isDisabled()) {
                            try {
                                for (String service : dayData.getServices()) {
                                    if (buttonServices.getText().equalsIgnoreCase(service)) {
                                        buttonServices.setEffect(shadow);
                                        selectedServices.add(buttonServices.getText());
                                    }
                                }
                            }catch (NullPointerException ignored){
                            }
                        }
                    }

                    //Check which sites are present
                    for (Node nodeSite : site_selection.getChildren() ) {
                        if (nodeSite instanceof Button buttonSite && buttonSite.isVisible() && !buttonSite.isDisabled()) {
                            try {
                                for (String site : dayData.getSites()) {
                                    if (buttonSite.getText().equalsIgnoreCase(site)) {
                                        buttonSite.setEffect(shadow);
                                        selectedSites.add(buttonSite.getText());
                                    }
                                }
                            }catch (NullPointerException ignored){
                            }
                        }
                    }

                    //Check which times are present
                    for (Node nodeTime : reservationTimeTable.getChildren()) {
                        if (nodeTime instanceof Button buttonTime && buttonTime.isVisible()) {
                            button.setDisable(false);
                            try{
                                for(String time : dayData.getTimes()) {
                                    if (buttonTime.getText().equalsIgnoreCase(time)) {
                                        buttonTime.setEffect(shadow);
                                        selectedTimes.add(buttonTime.getText());
                                    }
                                }
                            }catch (NullPointerException ignored){
                            }
                        }
                    }

                    System.out.println(selectedDays + "\n" + selectedServices + "\n" + selectedSites + "\n" + selectedTimes);
                }
            }

            System.out.println(selectedDays);

        }else{
            ((Button) event.getSource()).setText("Seleziona tutto");
            for (Node node : availabilityDay.getChildren()) {
                if (node instanceof Button button && button.isVisible() && !button.isDisabled()) {
                    button.setEffect(null);
                }
            }

            removeEffectsForAll();
            selectedDays.clear();
            selectedServices.clear();
            selectedSites.clear();
            selectedTimes.clear();
            System.out.println(selectedDays);
        }
    }

    @FXML
    void onClickSelectAllServices(ActionEvent event) throws IOException {
        if (((Button) event.getSource()).getText().equalsIgnoreCase("Tutto")) {
            ((Button) event.getSource()).setText("Nessuno");

            for (Node node : reservation_selection.getChildren()) {
                if (node instanceof Button button && button.isVisible() && !button.isDisabled()) {
                    String service = button.getText();

                    selectedServices.add(service);
                    button.setEffect(shadow);
                }
            }
        }else {
            ((Button) event.getSource()).setText("Tutto");

            for (Node node : reservation_selection.getChildren()) {
                if (node instanceof Button button && button.isVisible() && !button.isDisabled()) {
                    String service = button.getText();

                    selectedServices.remove(service);
                    button.setEffect(null);
                }
            }
        }
    }

    @FXML
    void onClickSelectAllSites(ActionEvent event) throws IOException {
        if (((Button) event.getSource()).getText().equalsIgnoreCase("Tutto")) {
            ((Button) event.getSource()).setText("Nessuno");

            for (Node node : site_selection.getChildren()) {
                if (node instanceof Button button && button.isVisible() && !button.isDisabled()) {
                    String service = button.getText();

                    selectedSites.add(service);
                    button.setEffect(shadow);
                }
            }
        }else {
            ((Button) event.getSource()).setText("Tutto");

            for (Node node : site_selection.getChildren()) {
                if (node instanceof Button button && button.isVisible() && !button.isDisabled()) {
                    String service = button.getText();

                    selectedSites.remove(service);
                    button.setEffect(null);
                }
            }
        }
    }

    @FXML
    void onClickSelectAllTimes(ActionEvent event) throws IOException {
        if (((Button) event.getSource()).getText().equalsIgnoreCase("Seleziona tutto")) {
            ((Button) event.getSource()).setText("Deseleziona tutto");

            for (Node node : reservationTimeTable.getChildren()) {
                if (node instanceof Button button && button.isVisible() && !button.isDisabled()) {
                    String service = button.getText();

                    selectedTimes.add(service);
                    button.setEffect(shadow);
                }
            }
        }else {
            ((Button) event.getSource()).setText("Seleziona tutto");

            for (Node node : reservationTimeTable.getChildren()) {
                if (node instanceof Button button && button.isVisible() && !button.isDisabled()) {
                    String service = button.getText();

                    selectedTimes.remove(service);
                    button.setEffect(null);
                }
            }
        }
    }

    @FXML
    void onClickExportAvailability(ActionEvent event) throws IOException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save as Text");
        fileChooser.setInitialFileName("Elenco disponibilità future.txt");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));
        File file = fileChooser.showSaveDialog(new Stage());

        if (file != null) {
            try (FileWriter writer = new FileWriter(file)) {
                int numberOfDays = availabilityData.getAllDays().size();
                writer.write("***** Disponibilità a partire dal " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + " *****\n\n");
                writer.write("Stampa effettuata il " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + " alle " +
                        LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) + " da " + LoginPAController.getFullName() + "\n\n");

                writer.write("Numero di giorni disponibili: " + numberOfDays + "\n\n");

                for (String day : availabilityData.getAllDays()) {
                    AvailabilityData.DayData dayData = availabilityData.getAvailabilityDay(day);
                    writer.write("Giorno " + day + "\n" +
                            "Orari: " + "\t " + dayData.getTimes() + "\n" +
                            "Servizi: " + dayData.getServices() + "\n" +
                            "Sedi: " + "\t " + dayData.getSites() + "\n\n");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}