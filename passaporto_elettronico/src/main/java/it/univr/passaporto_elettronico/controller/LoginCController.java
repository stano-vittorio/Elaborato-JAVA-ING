package it.univr.passaporto_elettronico.controller;

import it.univr.passaporto_elettronico.*;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;

import java.io.*;
import java.net.URL;
import java.util.ResourceBundle;

public class LoginCController implements Initializable {

    @FXML
    private BorderPane loginCScene;

    @FXML
    private TextField userLoginC;

    @FXML
    private PasswordField userPasswordC;

    @FXML
    private Label reset_password_message_C;

    @FXML
    private Label error_login_message_C;

    @FXML
    private Button psw_visibility_button;

    @FXML
    private TextField visiblePasswordField;
    DataSingleton currentUser = DataSingleton.getInstance();
    private final DataStorage userDataStorage = new DataStorage("userDataStorageC.dat");

    private static String fullName;

    public static String getFullName() {
        return fullName;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        loginCScene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                event.consume();
                try {
                    onClickLoginC(new ActionEvent());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    @FXML
    void onClickUndoFromLoginCToLogin() throws IOException {
        new switchScene(loginCScene, "login.fxml");
    }

    @FXML
    void onClickRegisterFromLoginCToRegisterC() throws IOException {
        new switchScene(loginCScene, "register_C.fxml");
    }

    @FXML
    void reset_password_button_C() {
        error_login_message_C.setVisible(false);

        reset_password_message_C.setVisible(true);
        javafx.util.Duration duration = javafx.util.Duration.seconds(15);
        new Timeline(new KeyFrame(duration, e -> reset_password_message_C.setVisible(false))).play();
    }

    @FXML
    void onClickLoginC(ActionEvent event) throws IOException {


        psw_visibility_button.fire();
        psw_visibility_button.fire();

        String username = userLoginC.getText();
        String password = userPasswordC.getText();

        if (username.isEmpty() || password.isEmpty()) {
            reset_password_message_C.setVisible(false);

            error_login_message_C.setVisible(true);
            javafx.util.Duration duration = javafx.util.Duration.seconds(5);
            new Timeline(new KeyFrame(duration, e -> error_login_message_C.setVisible(false))).play();

        } else {

            if(userDataStorage.searchUser(userDataStorage.getUser(username, password)) == 1){
               currentUser.setUser(userDataStorage.getUser(username, password));

               // show current user
               System.out.println(getClass().getSimpleName() + " current user: " + currentUser.getUser());

               fullName = currentUser.getUser().getName() + " " + currentUser.getUser().getSurname();
               new switchScene(loginCScene, "mainPage_C.fxml");

            } else {
                reset_password_message_C.setVisible(false);

                error_login_message_C.setVisible(true);
                javafx.util.Duration duration = javafx.util.Duration.seconds(5);
                new Timeline(new KeyFrame(duration, e -> error_login_message_C.setVisible(false))).play();
            }
        }
    }

    private boolean passwordVisible = false;
    @FXML
    public void psw_visibility() {
        if (!passwordVisible) {
            String password = userPasswordC.getText();
            visiblePasswordField.setText(password);
            visiblePasswordField.setVisible(true);
            userPasswordC.setVisible(false);
            passwordVisible = true;
        } else {
            String password = visiblePasswordField.getText();
            userPasswordC.setText(password);
            visiblePasswordField.setVisible(false);
            userPasswordC.setVisible(true);
            passwordVisible = false;
        }
    }
}



