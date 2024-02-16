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

import java.io.*;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class AddReservationCController implements Initializable {

    @FXML
    private BorderPane addReservationCScene;

    @FXML
    private Label warning_empty_fields;

    @FXML
    private Label welcomeLabel;

    @FXML
    private Label monthLabel;

    @FXML
    private Label yearLabel;

    @FXML
    private Label warning_passport_pickup;

    @FXML
    private Label date_passport_pickup;

    @FXML
    private Label info_passport_1monthLater;

    @FXML
    private Label warning_empty_availability;

    @FXML
    private GridPane reservationDay;

    @FXML
    private GridPane reservationTimeTable;

    @FXML
    private Button goBackMonthButton;

    @FXML
    private Button goForwardMonthButton;

    @FXML
    private MenuButton reservation_selection;

    @FXML
    private MenuButton site_selection;

    private final Date reservationDate = new Date();
    private Date lastSelectedDate;

    private LocalDate newDate;

    private final DataStorage userDataStorage = new DataStorage("userDataStorageC.dat");

    DataSingleton currentUser = DataSingleton.getInstance();

    private boolean isAvailabilityFound = false;

    private String day;
    private String time;
    private String item;
    private String site;

    private static final DropShadow shadow = new DropShadow();

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

    private AvailabilityData availabilityData, availabilityData2;

    private void initializeGridPanesIndexes() {
        for (Node node : reservationDay.getChildren()) {
            if (GridPane.getColumnIndex(node) == null) {
                GridPane.setColumnIndex(node, 0);
            }
            if (GridPane.getRowIndex(node) == null) {
                GridPane.setRowIndex(node, 0);
            }

            if (node instanceof Button button) {
                button.setDisable(true);
            }
        }

        for (Node node : reservationTimeTable.getChildren()) {
            if (GridPane.getColumnIndex(node) == null) {
                GridPane.setColumnIndex(node, 0);
            }
            if (GridPane.getRowIndex(node) == null) {
                GridPane.setRowIndex(node, 0);
            }

            if (node instanceof Button button) {
                button.setDisable(true);
            }
        }
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        welcomeLabel.setText("Ciao, " + LoginCController.getFullName());
        initializeGridPanesIndexes();

        currentDate = Calendar.getInstance();

        updateScene(0, "disable previous");

        System.out.println(day + time + site + item);

        availabilityData = AvailabilityData.loadFromFile("availabilityData.dat");

        for (String day : availabilityData.getAllDays()) {
            AvailabilityData.DayData dayData = availabilityData.getAvailabilityDay(day);
            System.out.println(day + " = " + dayData.getTimes() + ", " + dayData.getServices() + ", " + dayData.getSites());
        }

        for (Node node : reservationDay.getChildren()) {
            Button button = (Button) node;
            int buttonDate = Integer.parseInt(button.getText());

            boolean isCurrentMonthAndYear = currentDate.get(Calendar.YEAR) == today.get(Calendar.YEAR) &&
                    currentDate.get(Calendar.MONTH) == today.get(Calendar.MONTH);

            boolean isBeforeToday = buttonDate < today.get(Calendar.DATE);

            if (isCurrentMonthAndYear && isBeforeToday) {
                button.setDisable(true);
            }
        }

        goForwardMonthButton.setVisible(false);

        shadow.setSpread(0.75);
        shadow.setBlurType(BlurType.GAUSSIAN);
        shadow.setColor(Color.rgb(26, 142, 232));

        boolean checkItem = false;

        for (MenuItem menuItem : reservation_selection.getItems()) {
            menuItem.setDisable(false);
        }

        if (currentUser.getUser().getUserPassport()) { // HO IL PASSAPORTO
            System.out.println("ho il passaporto");

            for (MenuItem menuItem : reservation_selection.getItems()) { // SE TROVO NEL MENU "RILASCIO PASSAPORTO PER LA PRIMA VOLTA" LO DISATTIVO
                if (menuItem.getText().equalsIgnoreCase("rilascio passaporto per la prima volta")) {
                    menuItem.setDisable(true);
                }

                if (!checkItem) {

                    for (Reservation reservation : currentUser.getUser().getReservations()) { // SE TROVO NELLE PRENOTAZIONI UNA VOCE DIVERSA
                        if (reservation.getItem().equalsIgnoreCase("rilascio passaporto per scadenza") ||
                                reservation.getItem().equalsIgnoreCase("rilascio passaporto per furto") ||
                                reservation.getItem().equalsIgnoreCase("rilascio passaporto per smarrimento") ||
                                reservation.getItem().equalsIgnoreCase("rilascio passaporto per deterioramento")) {
                            checkItem = true;
                            break;
                        }
                    }
                }
            }

            if (checkItem) {
                for (MenuItem menuItem : reservation_selection.getItems()) {
                    if (menuItem.getText().equalsIgnoreCase("rilascio passaporto per scadenza") ||
                            menuItem.getText().equalsIgnoreCase("rilascio passaporto per furto") ||
                            menuItem.getText().equalsIgnoreCase("rilascio passaporto per smarrimento") ||
                            menuItem.getText().equalsIgnoreCase("rilascio passaporto per deterioramento")) {
                        menuItem.setDisable(true);
                    }
                }
            } else {

                for (MenuItem menuItem : reservation_selection.getItems()) {
                    if (menuItem.getText().equalsIgnoreCase("ritiro passaporto")) {
                        menuItem.setDisable(true);
                    }
                }
            }

        } else { // NON HO IL PASSAPORTO
            System.out.println("non ho il passaporto");
            for (MenuItem menuItem : reservation_selection.getItems()) {

                if (currentUser.getUser().getReservations().isEmpty()) {
                    if (!menuItem.getText().equalsIgnoreCase("rilascio passaporto per la prima volta")) { // disabilita tutto tranne rilascio passaporto per la prima volta
                        menuItem.setDisable(true);
                    }
                } else {

                    for (Reservation reservation : currentUser.getUser().getReservations()) {

                        if (reservation.getItem().equalsIgnoreCase("rilascio passaporto per la prima volta")) {

                            if (!menuItem.getText().equalsIgnoreCase("ritiro passaporto")) {
                                menuItem.setDisable(true);
                            }

                        } else {
                            System.out.println("OK");
                            if (!menuItem.getText().equalsIgnoreCase("rilascio passaporto per la prima volta")) { // disabilita tutto tranne rilascio passaporto per la prima volta
                                menuItem.setDisable(true);
                            }
                        }
                    }
                }
            }
        }

        for (MenuItem menuItem : reservation_selection.getItems()) {
            for (Reservation reservation : currentUser.getUser().getReservations()) {
                if (reservation.getItem().equalsIgnoreCase(menuItem.getText())) {
                    menuItem.setDisable(true);
                }
            }
        }

        addReservationCScene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                event.consume();
                try {
                    saveReservation(new ActionEvent());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    @FXML
    void onClickUndoFromAddReservationCToMainPageC() throws IOException {
        new switchScene(addReservationCScene, "mainPage_C.fxml");
    }

    @FXML
    void onClickLogoutC() throws IOException {
        currentUser.setUser(null);
        new switchScene(addReservationCScene, "login.fxml");
    }

    @FXML
    void onClickGetReservationTime(ActionEvent event) throws IOException {
        time = ((Button) event.getSource()).getText();

        for (Node node : reservationTimeTable.getChildren()) {
            Button button = (Button) node;
            if (button.getText().equals(time)) {
                button.setEffect(shadow);
            } else {
                button.setEffect(null);
            }
        }
    }

    @FXML
    void saveReservation(ActionEvent event) throws IOException {
        boolean aD = true;

        if (day != null &&
                time != null &&
                item != null &&
                site != null) {


            String currentDateString = String.format("%02d/%02d/%04d",
                    Integer.parseInt(day),
                    currentDate.get(Calendar.MONTH) + 1,
                    currentDate.get(Calendar.YEAR));

            // CONTROLLO PA
            AvailabilityData availabilityData2 = AvailabilityData.loadFromFile("availabilityData.dat");
            AvailabilityData.DayData dayData2 = availabilityData2.getAvailabilityDay(currentDateString);

            System.out.println(dayData2);

            if(dayData2 == null) {

                aD = false;

            } else {

                if (dayData2.getTimes().contains(time) && dayData2.getSites().contains(site) && dayData2.getServices().contains(item)) {

                    System.out.println("DATA SI");

                } else {
                    aD = false;
                    System.out.println("NON CE LA DATA");
                    System.out.println(currentDateString + dayData2.getTimes().contains(time) +
                            dayData2.getSites().contains(site) +
                            dayData2.getServices().contains(item));
                }
            }


            for (UserC user : userDataStorage.getAllUsers()) {

                // CONTROLLO ALTRI CITTADINI
                for (Reservation reservation : user.getReservations()) {
                    LocalDate reservationLocalDate = reservation.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

                    String currentDateFormatted = String.format("%02d/%02d/%04d",
                            Integer.parseInt(day),
                            currentDate.get(Calendar.MONTH) + 1,
                            currentDate.get(Calendar.YEAR));

                    LocalTime timeToCompare = LocalTime.parse(time);

                    SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
                    String dateAsString = dateFormat.format(reservation.getDate());
                    LocalTime timeFromDate = LocalTime.parse(dateAsString);


                    if (reservationLocalDate.equals(LocalDate.parse(currentDateFormatted, DateTimeFormatter.ofPattern("dd/MM/yyyy"))) &&
                            timeToCompare.equals(timeFromDate) &&
                            reservation.getSite().equals(site)) {
                        System.out.println(reservation);
                        aD = false;
                    }
                }
            }

            if(aD == true) {
                addReservation();
                new switchScene(addReservationCScene, "confirm_reservation_C.fxml");
            } else {
            new switchScene(addReservationCScene, "prohibited_reservation_C.fxml");
            }

        } else {
            warning_empty_fields.setVisible(true);
            javafx.util.Duration duration = javafx.util.Duration.seconds(5);
            new Timeline(new KeyFrame(duration, e -> warning_empty_fields.setVisible(false))).play();
        }
    }

    private void addReservation() {
        userDataStorage.removeUser(currentUser.getUser());

        setReservationDate(day, time);

        currentUser.getUser().getReservations().add(new Reservation(reservationDate, item, site));

        userDataStorage.addUser(currentUser.getUser());
    }

    private void setReservationDate(String day, String time) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), Integer.parseInt(day),
                Integer.parseInt(time.substring(0, 2)), Integer.parseInt(time.substring(3)), 0);

        reservationDate.setTime(calendar.getTimeInMillis());
    }


    @FXML
    void goBackMonth() {
        goForwardMonthButton.setVisible(true);
        updateScene(-1, "disable previous");
        updateFullDaysColor(item, site);
        disableTimeDay();
    }

    @FXML
    void goForwardMonth() {
        goBackMonthButton.setVisible(true);
        updateScene(1, "");
        enablePastTimes();
        updateFullDaysColor(item, site);
        disableTimeDay();
    }

    private void updateScene(int offset, String condition) {
        day = null;
        time = null;

        currentDate.set(currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH) + offset, 1);

        monthLabel.setText(monthName[currentDate.get(Calendar.MONTH)]);
        yearLabel.setText(String.valueOf(currentDate.get(Calendar.YEAR)));

        int counterA = 1;
        int counterB = 2;
        int dayOfWeek = currentDate.get(Calendar.DAY_OF_WEEK);

        for (Node node : reservationDay.getChildren()) {
            Button button = (Button) node;

            button.setEffect(null);

            if (counterB < dayOfWeek) {
                button.setText("0");
                button.setVisible(false);

                counterB++;

            } else if (counterA <= currentDate.getActualMaximum(Calendar.DAY_OF_MONTH)) {
                button.setVisible(true);
                button.setText(String.valueOf(counterA));

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
                    goBackMonthButton.setVisible(false);

                }
            }
        }

        for (Node node : reservationTimeTable.getChildren()) {
            Button button = (Button) node;
            button.setEffect(null);
        }
    }

    @FXML
    void getReservationSelection(ActionEvent event) {
        if (site_selection.getText().isEmpty()) {
            return;
        }

        reservation_selection.setText(((MenuItem) event.getSource()).getText());
        item = reservation_selection.getText();

        info_passport_1monthLater.setVisible(false);

        if ("ritiro passaporto".equalsIgnoreCase(item)) {
            MenuItem firstTimeReleaseMenuItem = null;

            for (MenuItem menuItem : reservation_selection.getItems()) {
                String menuItemText = menuItem.getText().toLowerCase();
                if (menuItemText.contains("rilascio passaporto")) {
                    firstTimeReleaseMenuItem = menuItem;
                    break;
                }
            }


            if (firstTimeReleaseMenuItem != null && !firstTimeReleaseMenuItem.isDisable()) {
                site_selection.setDisable(true);
                //warning_passport_pickup.setVisible(true);
                warning_empty_availability.setVisible(false);
                goBackMonthButton.setVisible(false);
                goForwardMonthButton.setVisible(false);
                site_selection.setText("Seleziona la sede desiderata");

                for (Node node : reservationDay.getChildren()) {
                    if (node instanceof Button button) {
                        button.setDisable(true);
                        button.setEffect(null);
                        button.setStyle("-fx-background-color: #CCCC");
                    }
                }

                for (Node node : reservationTimeTable.getChildren()) {
                    if (node instanceof Button button) {
                        button.setDisable(true);
                        button.setEffect(null);
                        button.setStyle("-fx-background-color: #CCCC");
                    }
                }
            } else {

                UserC currentUser = DataSingleton.getInstance().getUser();
                Date releasePassportDate = null;

                for (Reservation reservation : currentUser.getReservations()) {
                    String reservationItem = reservation.getItem().toLowerCase();
                    if (reservationItem.contains("rilascio passaporto")) {
                        releasePassportDate = reservation.getDate();
                        break;
                    }
                }

                if (releasePassportDate != null) {

                    if (!site_selection.getText().equals("Seleziona la sede desiderata") && !reservation_selection.getText().equals("Seleziona il servizio desiderato")) {
                        goBackMonthButton.setVisible(true);
                    }

                    // Stampa la data prima di aggiungere un mese
                    SimpleDateFormat dateFormatBefore = new SimpleDateFormat("dd/MM/yyyy");
                    String formattedDateBefore = dateFormatBefore.format(releasePassportDate);
                    System.out.println("***** Data prima di aggiungere un mese: " + formattedDateBefore);

                    // Converte la data di prenotazione a LocalDate
                    LocalDate releasePassportLocalDate = releasePassportDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

                    // Aggiungi un mese e un giorno alla data di prenotazione
                    newDate = releasePassportLocalDate.plusMonths(1).plusDays(1);

                    // Stampa la nuova data formattata
                    System.out.println("***** Nuova data corrente: " + newDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));

                    date_passport_pickup.setText("Ritiro prenotabile da " + newDate.format(DateTimeFormatter.ofPattern("MMMM yyyy")));
                    date_passport_pickup.setVisible(true);
                    warning_empty_availability.setVisible(false);

                    // Imposta la currentDate alla nuova data
                    currentDate.setTime(Date.from(newDate.atStartOfDay(ZoneId.systemDefault()).toInstant()));
                    updateScene(0, "");

                    info_passport_1monthLater.setVisible(true);
                    warning_empty_availability.setVisible(false);
                    javafx.util.Duration duration = javafx.util.Duration.seconds(5);
                    new Timeline(new KeyFrame(duration, e -> info_passport_1monthLater.setVisible(false))).play();
                }
            }
        } else {
            date_passport_pickup.setVisible(false);
            currentDate = Calendar.getInstance();
            updateScene(0, "");
            goBackMonthButton.setVisible(false);
            site_selection.setDisable(false);
            //warning_passport_pickup.setVisible(false);
        }


        if (!site_selection.getText().equals("Seleziona la sede desiderata") && !reservation_selection.getText().equals("Seleziona il servizio desiderato")) {
            goForwardMonthButton.setVisible(true);
            site = site_selection.getText();
            lastSelectedDate = null;
            disableTimeDay();
            updateFullDaysColor(item, site);
        }
    }

    @FXML
    void getSiteSelection(ActionEvent event) {
        if (reservation_selection.getText().isEmpty()) {
            return;
        }

        site_selection.setText(((MenuItem) event.getSource()).getText());
        site = site_selection.getText();

        if (!reservation_selection.getText().equals("Seleziona il servizio desiderato") && !site_selection.getText().equals("Seleziona la sede desiderata")) {
            goForwardMonthButton.setVisible(true);
            item = reservation_selection.getText();
            lastSelectedDate = null;
            disableTimeDay();
            updateFullDaysColor(item, site);
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
                    button.setStyle("-fx-background-color: #CCCC");

                }
            }
        }
    }

    private void enablePastTimes() {
        for (Node node : reservationTimeTable.getChildren()) {
            if (node instanceof Button button) {
                button.setDisable(false);
            }
        }
    }

    private void disableTimeDay() {
        for (Node node : reservationTimeTable.getChildren()) {
            if (node instanceof Button button) {
                button.setDisable(true);
                button.setEffect(null);
                button.setStyle("-fx-background-color: #CCCC");
            }
        }

        for (Node node : reservationDay.getChildren()) {
            if (node instanceof Button button) {
                button.setEffect(null);
            }
        }
    }

    // ALLO STESSO ORARIO POSSO AVERE UTENTI DIVERSI CHE PRENOTANO IN SEDI DIVERSE
    // QUINDI PER OGNI ORA E GIORNO AVRO' DUE UTENTI ALLA STESSA ORA NELLE 2 SEDI DIVERSE
    @FXML
    void onClickGetReservationDay(ActionEvent event) {
        day = ((Button) event.getSource()).getText();
        currentDate.set(currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), Integer.parseInt(day));

        String currentDateFormatted = String.format("%02d/%02d/%04d",
                Integer.parseInt(day),
                currentDate.get(Calendar.MONTH) + 1,
                currentDate.get(Calendar.YEAR));

        Set<String> availableTimes = availabilityData.getAvailabilityDay(currentDateFormatted).getTimes();

        // Utilizza un Map per tenere traccia del numero di prenotazioni per ciascun orario
        Map<String, Long> reservationsCountForTime = new HashMap<>();

        for (UserC user : userDataStorage.getAllUsers()) {
            for (Reservation reservation : user.getReservations()) {
                LocalDate reservationLocalDate = reservation.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                LocalTime reservationLocalTime = reservation.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalTime();

                // Confronto delle date e degli orari
                if (reservationLocalDate.equals(LocalDate.parse(currentDateFormatted, DateTimeFormatter.ofPattern("dd/MM/yyyy")))) {
                    String timeKey = reservationLocalTime.format(DateTimeFormatter.ofPattern("HH:mm"));
                    reservationsCountForTime.put(timeKey, reservationsCountForTime.getOrDefault(timeKey, 0L) + 1);
                }
            }
        }

        for (Node node : reservationTimeTable.getChildren()) {
            Button button = (Button) node;
            setReservationDate(day, button.getText());

            // Usa reservationsCountForTime per ottenere il numero di prenotazioni per l'orario corrente
            long numberOfReservationsForTime = reservationsCountForTime.getOrDefault(button.getText(), 0L);

            // Se ci sono almeno 2 prenotazioni per l'orario, colora in lightcoral
            if (numberOfReservationsForTime == 2) {
                button.setDisable(true);
                button.setStyle("-fx-background-color: lightcoral;");
            } else if (numberOfReservationsForTime == 1) {
                // Se c'è solo una prenotazione, controlla la sede e l'utente
                Reservation lastReservation = null;
                boolean isCurrentUserBooking = false;

                for (UserC user : userDataStorage.getAllUsers()) {
                    for (Reservation reservation : user.getReservations()) {
                        LocalDate reservationLocalDate = reservation.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                        LocalTime reservationLocalTime = reservation.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalTime();

                        // Confronta data, orario, sede e utente
                        if (reservationLocalDate.equals(LocalDate.parse(currentDateFormatted, DateTimeFormatter.ofPattern("dd/MM/yyyy"))) &&
                                reservationLocalTime.format(DateTimeFormatter.ofPattern("HH:mm")).equals(button.getText())) {
                            if (user.equals(currentUser.getUser())) {
                                // Utente attuale ha già prenotato nello stesso orario, nella stessa sede
                                isCurrentUserBooking = true;
                            } else {
                                // Altrimenti, controlla l'ultima prenotazione in modo da capire la sede
                                lastReservation = reservation;
                            }
                        }
                    }
                }

                if (isCurrentUserBooking) {
                    // Se l'utente attuale ha già prenotato, disabilita il pulsante e coloralo in lightcoral
                    button.setDisable(true);
                    button.setStyle("-fx-background-color: lightcoral;");
                } else if (lastReservation != null && lastReservation.getSite().equalsIgnoreCase(site_selection.getText())) {
                    // Se la sede è la stessa, colora in lightcoral
                    button.setDisable(true);
                    button.setStyle("-fx-background-color: lightcoral;");
                } else {
                    // Altrimenti, colora in lightgreen
                    button.setStyle("-fx-background-color: #abceb8;");
                    button.setDisable(false);
                }
            } else {
                // Altrimenti, controlla la disponibilità normale
                if (availableTimes.contains(button.getText())) {
                    // Puoi prenotare per questo orario
                    button.setStyle("-fx-background-color: #abceb8;");
                    button.setDisable(false);
                } else {
                    button.setStyle("-fx-background-color: #CCCC");
                    button.setDisable(true);
                    button.setEffect(null);
                    time = null;
                }
            }

            // Gestione degli effetti sui pulsanti del giorno
            for (Node dayNode : reservationDay.getChildren()) {
                Button dayButton = (Button) dayNode;
                dayButton.setEffect(dayButton.getText().equals(day) ? shadow : null);
            }
        }

        // Se il giorno selezionato è oggi, disabilita gli orari passati rispetto all'orario attuale
        if (currentDate.get(Calendar.YEAR) == today.get(Calendar.YEAR) &&
                currentDate.get(Calendar.MONTH) == today.get(Calendar.MONTH) &&
                Integer.parseInt(day) == today.get(Calendar.DATE)) {
            disablePastTimes();
        }

        for (MenuItem menuItem : reservation_selection.getItems()) {
            for (Reservation reservation : currentUser.getUser().getReservations()) {
                if (reservation.getItem().equalsIgnoreCase(menuItem.getText())) {
                    menuItem.setDisable(true);
                }
            }
        }
    }

    // GESTIONE COLORI/DISABILITAZIONE GIORNI CALENDARIO
    // SE PER UN GIORNO CI SONO 5 ORARI DISPONIBILI --> UTENTE CORRENTE MAX 5 PRENOTAZIONI (UN UTENTE NON PUò ESSERE CONTEMPORANEAMENTE IN 2 LUOGHI DIVERSI)
    // SE PER UN GIORNO CI SONO 5 ORARI DISPONIBILI --> MAX 5 PRENOTAZIONI PER "QUESTURA DI VERONA" E 5 PER "COMMISSARIATO DI VERONETTA"
    // GESTIONE PULSANTE AVANTI MESE OLTRE LA DATA DISPONIBILE
    private void updateFullDaysColor(String selectedService, String selectedSite) {
        UserC currentUser = DataSingleton.getInstance().getUser();
        Calendar today = Calendar.getInstance();
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MILLISECOND, 0);

        isAvailabilityFound = false;

        for (Node node : reservationDay.getChildren()) {
            if (node instanceof Button button) {
                String day = button.getText();

                try {
                    int dayValue = Integer.parseInt(day);
                    if (dayValue <= 0) {
                        continue;
                    }
                } catch (NumberFormatException e) {
                    continue;
                }

                String currentDateString = String.format("%02d/%02d/%04d",
                        Integer.parseInt(day),
                        currentDate.get(Calendar.MONTH) + 1,
                        currentDate.get(Calendar.YEAR));

                Calendar currentCalendar = convertStringToCalendar(currentDateString);

                // Controllo se la data è precedente a oggi o se è oggi e l'orario è dopo le 16:30
                if (currentCalendar.before(today) ||
                        (currentCalendar.equals(today) && LocalTime.now().isAfter(LocalTime.of(16, 30)))) {
                    button.setDisable(true);
                    button.setStyle("-fx-background-color: #CCCC");
                    continue;
                }

                // Controllo se il giorno è nell'ultima colonna
                if (GridPane.getColumnIndex(node) == 6) {
                    button.setDisable(true);
                    continue;
                }

                AvailabilityData.DayData dayData = availabilityData.getAvailabilityDay(currentDateString);

                if (dayData != null) {
                    // Controllo la disponibilità del giorno per il servizio e la sede selezionati
                    boolean isDayAvailable = dayData.getServices().contains(selectedService) && dayData.getSites().contains(selectedSite);

                    int maxReservationsPerDay;

                    // Controllo se il giorno è oggi
                    if (currentCalendar.equals(today) && LocalTime.now().isBefore(LocalTime.of(16, 30))) {
                        // Imposta maxReservationsPerDay come il numero di orari disponibili da questo momento in poi
                        List<String> futureTimes = dayData.getTimes().stream()
                                .filter(time -> LocalTime.parse(time).isAfter(LocalTime.now()))
                                .toList();
                        maxReservationsPerDay = futureTimes.size();
                    } else {
                        // Altrimenti, imposta maxReservationsPerDay come il numero totale di orari disponibili
                        maxReservationsPerDay = dayData.getTimes().size();
                    }

                    // GESTIONE VERIFICA PRENOTAZIONE INTER-UTENTI
                    // CONTROLLO PRENOTAZIONI DEL SIGNOLO UTENTE PER OGNI GIORNO (SE OGGI CONTO SOLO ORARI DA CORRENTE IN POI)
                    // CONTROLLO PRENOTAZIONI DI TUTTI CONTROLLANDO ANCHE LE SEDI (SE OGGI CONTO SOLO ORARI DA CORRENTE IN POI)

                    int currentUserReservations = 0;
                    LocalDate currentDate = LocalDate.now();
                    LocalTime currentTime = LocalTime.now(); // Ottieni l'ora corrente

                    // Conta le prenotazioni dell'utente corrente per il giorno che sto verificando
                    for (Reservation reservation : currentUser.getReservations()) {
                        LocalDate reservationLocalDate = reservation.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                        String reservationDay = String.format("%02d/%02d/%04d",
                                reservationLocalDate.getDayOfMonth(),
                                reservationLocalDate.getMonthValue(),
                                reservationLocalDate.getYear());

                        // Confronta solo la data
                        if (reservationDay.equals(currentDateString)) {
                            LocalTime reservationLocalTime = reservation.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalTime();

                            // Confronta solo le prenotazioni non passate rispetto all'ora attuale se nel giorno attuale
                            if (currentDate.isEqual(reservationLocalDate) && currentTime.isBefore(reservationLocalTime)) {
                                currentUserReservations++;
                            } else if (!currentDate.isEqual(reservationLocalDate)) {
                                // Se la data è diversa da oggi, tutte le prenotazioni vengono conteggiate
                                currentUserReservations++;
                            }
                        }
                    }

                    int totalReservationsForSite = 0;

                    // Conta le prenotazioni totali per il sito e il giorno che sto verificando
                    for (UserC user : userDataStorage.getAllUsers()) {
                        for (Reservation reservation : user.getReservations()) {
                            LocalDate reservationLocalDate = reservation.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                            String reservationDay = String.format("%02d/%02d/%04d",
                                    reservationLocalDate.getDayOfMonth(),
                                    reservationLocalDate.getMonthValue(),
                                    reservationLocalDate.getYear());

                            // Confronta data, sede e ora
                            if (reservationDay.equals(currentDateString) && reservation.getSite().equalsIgnoreCase(selectedSite)) {
                                LocalTime reservationLocalTime = reservation.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalTime();

                                // Confronta solo le prenotazioni non passate rispetto all'ora attuale se nel giorno attuale
                                if (currentDate.isEqual(reservationLocalDate) && currentTime.isBefore(reservationLocalTime)) {
                                    totalReservationsForSite++;
                                } else if (!currentDate.isEqual(reservationLocalDate)) {
                                    // Se la data è diversa da oggi, tutte le prenotazioni vengono conteggiate
                                    totalReservationsForSite++;
                                }
                            }
                        }
                    }

                    // Controllo se l'utente attuale o il servizio hanno raggiunto il numero massimo di prenotazioni per il giorno
                    if(!isDayAvailable) {
                        button.setStyle("-fx-background-color: #CCCCCC;");
                        button.setDisable(true);
                    } else if (currentUserReservations == maxReservationsPerDay || totalReservationsForSite == maxReservationsPerDay) {
                        button.setStyle("-fx-background-color: lightcoral;");
                        button.setDisable(true);
                    } else {
                        button.setStyle("-fx-background-color: #abceb8;");
                        button.setDisable(false);
                    }
                } else {
                    button.setStyle("-fx-background-color: #CCCCCC;");
                    button.setDisable(true);
                }

                Date latestReservationDate = getLatestReservationDate(selectedService, selectedSite);

                //se non ho trovato NESSUNA disponibilità per quel servizio e sede allora disabilita il tasto
                goForwardMonthButton.setVisible(isAvailabilityFound);
                warning_empty_availability.setVisible(true);

                if (latestReservationDate != null) {
                    Calendar latestCalendar = Calendar.getInstance();
                    latestCalendar.setTime(latestReservationDate);

                    goForwardMonthButton.setVisible(currentDate.get(Calendar.YEAR) <= latestCalendar.get(Calendar.YEAR) &&
                            (currentDate.get(Calendar.YEAR) != latestCalendar.get(Calendar.YEAR) ||
                                    currentDate.get(Calendar.MONTH) < latestCalendar.get(Calendar.MONTH)));
                    warning_empty_availability.setVisible(false);
                }

                if ("ritiro passaporto".equalsIgnoreCase(selectedService)) {
                    LocalDate buttonDate = LocalDate.of(currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH) + 1, Integer.parseInt(button.getText()));
                    if (buttonDate.isBefore(newDate)) {
                        button.setStyle("-fx-background-color: #CCCC");
                        button.setDisable(true);
                    }
                }
            }
        }
    }

    private Calendar convertStringToCalendar(String dateString) {
        Calendar calendar = Calendar.getInstance();
        try {
            Date date = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(dateString);
            calendar.setTime(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return calendar;
    }

    public Date getLatestReservationDate(String selectedService, String selectedSite) {
        if (lastSelectedDate != null) {
            return lastSelectedDate;
        }

        try {
            AvailabilityData availabilityData = AvailabilityData.loadFromFile("availabilityData.dat");
            List<String> allDays = availabilityData.getAllDays();
            TreeSet<Date> allDates = new TreeSet<>();

            for (String day : allDays) {
                AvailabilityData.DayData dayData = availabilityData.getAvailabilityDay(day);

                if (dayData != null && !dayData.getTimes().isEmpty() &&
                        dayData.getServices().contains(selectedService) &&
                        dayData.getSites().contains(selectedSite)) {

                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                    Date latestDate = dateFormat.parse(day);

                    allDates.add(latestDate);
                }
            }

            if (!allDates.isEmpty()) {
                lastSelectedDate = allDates.last();
                SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy");
                String formattedDate = outputFormat.format(lastSelectedDate);
                System.out.println("Data più grande trovata per " + selectedService + " e " + selectedSite + ": " + formattedDate);
                isAvailabilityFound = true;
                return lastSelectedDate;
            }


        } catch (ParseException e) {
            e.printStackTrace();
        }

        return null;
    }
}