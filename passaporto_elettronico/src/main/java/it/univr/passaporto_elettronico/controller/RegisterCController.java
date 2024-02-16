package it.univr.passaporto_elettronico.controller;

import it.univr.passaporto_elettronico.DataSingleton;
import it.univr.passaporto_elettronico.DataStorage;
import it.univr.passaporto_elettronico.UserC;
import it.univr.passaporto_elettronico.switchScene;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;

import java.io.*;
import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class RegisterCController implements Initializable {

    @FXML
    private BorderPane registerCScene;

    @FXML
    private TextField nomeRegisterC;

    @FXML
    private TextField cognomeRegisterC;

    @FXML
    private DatePicker data_nascitaRegisterC;

    @FXML
    private TextField luogo_nascitaRegisterC;

    @FXML
    private TextField CFRegisterC;

    @FXML
    private Label warning_empty_label;

    @FXML
    private Label warning_CF_format;

    @FXML
    private Label warning_user_unautorized;

    @FXML
    private Label user_already_exist;

    private final DataStorage userDataStorage = new DataStorage("userDataStorageC.dat");

    DataSingleton currentUser = DataSingleton.getInstance();

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        registerCScene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                event.consume();
                try {
                    Node currentNode = ((Node) event.getTarget());
                    if (currentNode instanceof DatePicker) {
                        KeyEvent tabEvent = new KeyEvent(KeyEvent.KEY_PRESSED, "", "", KeyCode.TAB, false, false, false, false);
                        Event.fireEvent(currentNode, tabEvent);
                    }

                    onClickNextFromRegisterCToSetC(new ActionEvent());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    @FXML
    void onClickUndoFromRegisterCCToLoginC() throws IOException {
        new switchScene(registerCScene, "login_C.fxml");
    }

    @FXML
    void onClickNextFromRegisterCToSetC(ActionEvent event) throws IOException {
        String name = nomeRegisterC.getText();
        String surname = cognomeRegisterC.getText();
        String taxIdCode = CFRegisterC.getText();
        String birthPlace = luogo_nascitaRegisterC.getText();
        LocalDate birthDate = data_nascitaRegisterC.getValue();

        if (name.isEmpty() ||
                surname.isEmpty() ||
                taxIdCode.isEmpty() ||
                birthPlace.isEmpty() ||
                birthDate == null) {

            user_already_exist.setVisible(false);
            warning_CF_format.setVisible(false);
            warning_user_unautorized.setVisible(false);

            warning_empty_label.setVisible(true);
            javafx.util.Duration duration = javafx.util.Duration.seconds(5);
            new Timeline(new KeyFrame(duration, e -> warning_empty_label.setVisible(false))).play();

        } else if (taxIdCode.length() != 16 ||
                !taxIdCode.matches("[a-zA-Z0-9]+") ||
                !Character.isLetter(taxIdCode.charAt(0)) ||
                !Character.isLetter(taxIdCode.charAt(1)) ||
                !Character.isLetter(taxIdCode.charAt(2)) ||
                !Character.isLetter(taxIdCode.charAt(3)) ||
                !Character.isLetter(taxIdCode.charAt(4)) ||
                !Character.isLetter(taxIdCode.charAt(5)) ||
                !Character.isDigit(taxIdCode.charAt(6)) ||
                !Character.isDigit(taxIdCode.charAt(7)) ||
                !Character.isLetter(taxIdCode.charAt(8)) ||
                !Character.isDigit(taxIdCode.charAt(9)) ||
                !Character.isDigit(taxIdCode.charAt(10)) ||
                !Character.isLetter(taxIdCode.charAt(11)) ||
                !Character.isDigit(taxIdCode.charAt(12)) ||
                !Character.isDigit(taxIdCode.charAt(13)) ||
                !Character.isDigit(taxIdCode.charAt(14)) ||
                !Character.isLetter(taxIdCode.charAt(15))) {

            user_already_exist.setVisible(false);
            warning_empty_label.setVisible(false);
            warning_user_unautorized.setVisible(false);

            warning_CF_format.setVisible(true);
            javafx.util.Duration duration = javafx.util.Duration.seconds(5);
            new Timeline(new KeyFrame(duration, e -> warning_CF_format.setVisible(false))).play();

        } else {
            currentUser.setUser(new UserC(name, surname, taxIdCode, birthPlace, birthDate, userDataStorage.getMedicalCard(taxIdCode), userDataStorage.getUserType(taxIdCode), userDataStorage.getUserPassport(taxIdCode)));

            if(userDataStorage.searchUser(currentUser.getUser()) == 1) {
                new switchScene(registerCScene, "set_C.fxml");

            } else if(userDataStorage.searchUser(currentUser.getUser()) == -1){
                warning_CF_format.setVisible(false);
                warning_empty_label.setVisible(false);
                warning_user_unautorized.setVisible(false);

                user_already_exist.setVisible(true);
                javafx.util.Duration duration = javafx.util.Duration.seconds(5);
                new Timeline(new KeyFrame(duration, e -> user_already_exist.setVisible(false))).play();

            } else {
                user_already_exist.setVisible(false);
                warning_CF_format.setVisible(false);
                warning_empty_label.setVisible(false);

                warning_user_unautorized.setVisible(true);
                javafx.util.Duration duration = javafx.util.Duration.seconds(5);
                new Timeline(new KeyFrame(duration, e -> warning_user_unautorized.setVisible(false))).play();
            }
        }
    }
}
