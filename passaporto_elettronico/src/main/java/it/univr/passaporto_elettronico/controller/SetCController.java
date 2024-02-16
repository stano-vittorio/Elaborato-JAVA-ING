package it.univr.passaporto_elettronico.controller;

import it.univr.passaporto_elettronico.DataSingleton;
import it.univr.passaporto_elettronico.DataStorage;
import it.univr.passaporto_elettronico.switchScene;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;

import java.io.*;
import java.net.URL;
import java.util.ResourceBundle;

public class SetCController implements Initializable {

    @FXML
    private BorderPane setCScene;

    @FXML
    private TextField setUserLoginC;

    @FXML
    private TextField setUserPasswordC;

    @FXML
    private Button psw_visibility_button;

    @FXML
    private TextField visiblePasswordField;

    @FXML
    private Label error_password_wrong;

    DataSingleton currentUser = DataSingleton.getInstance();
    private final DataStorage userDataStorage = new DataStorage("userDataStorageC.dat");

    @Override
    public void  initialize(URL location, ResourceBundle resources){

        setCScene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                event.consume();
                try {
                    onClickRegisterSetC(new ActionEvent());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    @FXML
    void onClickUndoFromSetCToRegisterC(ActionEvent event) throws IOException {
        new switchScene(setCScene, "register_C.fxml");
    }

    @FXML
    void onClickRegisterSetC(ActionEvent event) throws IOException {
        psw_visibility_button.fire();
        psw_visibility_button.fire();

        String user = setUserLoginC.getText();
        String password = setUserPasswordC.getText();

        if(password.length() < 6) {
            error_password_wrong.setVisible(true);
            javafx.util.Duration duration = javafx.util.Duration.seconds(5);
            new Timeline(new KeyFrame(duration, e -> error_password_wrong.setVisible(false))).play();

        }else{
            userDataStorage.removeUser(currentUser.getUser());

            currentUser.getUser().setUsername(user);
            currentUser.getUser().setPassword(password);

            userDataStorage.addUser(currentUser.getUser());

            new switchScene(setCScene, "confirm_registration_C.fxml");
        }
    }

    private boolean passwordVisible = false;
    @FXML
    public void psw_visibility(ActionEvent actionEvent) {
        if (!passwordVisible) {
            String password = setUserPasswordC.getText();
            visiblePasswordField.setText(password);
            visiblePasswordField.setVisible(true);
            setUserPasswordC.setVisible(false);
            passwordVisible = true;
        } else {
            String password = visiblePasswordField.getText();
            setUserPasswordC.setText(password);
            visiblePasswordField.setVisible(false);
            setUserPasswordC.setVisible(true);
            passwordVisible = false;
        }
    }
}
