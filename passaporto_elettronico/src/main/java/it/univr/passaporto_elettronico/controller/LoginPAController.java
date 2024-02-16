package it.univr.passaporto_elettronico.controller;

import it.univr.passaporto_elettronico.UserPA;
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
import java.util.Set;
import java.util.TreeSet;

public class LoginPAController implements Initializable {

    @FXML
    private BorderPane loginPAScene;

    @FXML
    private TextField userLoginPA;

    @FXML
    private TextField userPasswordPA;

    @FXML
    private Label error_login_message_PA;

    @FXML
    private Button psw_visibility_button;

    @FXML
    private TextField visiblePasswordField;

    private final File userDataStorage = new File("userDataStoragePA.dat");

    public static String fullName;

    public static String getFullName() {
        return fullName;
    }

    @Override
    public void  initialize(URL location, ResourceBundle resources){
        if(!userDataStorage.exists()){
            createUserDataStorage();
        }

        loginPAScene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                event.consume();
                try {
                    onClickLoginPA(new ActionEvent());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    public Set<UserPA> dStorage = new TreeSet<>();

    private void saveData(Set<UserPA> data){
        try {
            FileOutputStream fos = new FileOutputStream(userDataStorage);
            BufferedOutputStream bos = new BufferedOutputStream(fos);
            ObjectOutputStream oos = new ObjectOutputStream(bos);

            oos.writeObject(data);

            oos.close();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    private Set<UserPA> getData(){
        try{
            FileInputStream fis = new FileInputStream(userDataStorage);
            BufferedInputStream bis = new BufferedInputStream(fis);
            ObjectInputStream ois = new ObjectInputStream(bis);

            dStorage = (Set<UserPA>) ois.readObject();

            ois.close();
        }catch (IOException | ClassNotFoundException exception){
            exception.printStackTrace();
        }

        return dStorage;
    }

    private void createUserDataStorage() {

        UserPA userA = new UserPA("VR457793", "VittorioMariaStano");
        dStorage.add(userA);

        UserPA userB = new UserPA("VR461049","StefanoEsposito");
        dStorage.add(userB);

        saveData(dStorage);
    }

    @FXML
    void onClickUndoFromLoginPAToLogin(ActionEvent event) throws IOException {
        new switchScene(loginPAScene, "login.fxml");
    }

    @FXML
    void onClickLoginPA(ActionEvent event) throws IOException {
        psw_visibility_button.fire();
        psw_visibility_button.fire();

        String user = userLoginPA.getText();
        String password = userPasswordPA.getText();

        dStorage = getData();

        if(user.isEmpty() || password.isEmpty()){
            error_login_message_PA.setVisible(true);
            javafx.util.Duration duration = javafx.util.Duration.seconds(5);
            new Timeline(new KeyFrame(duration, e -> error_login_message_PA.setVisible(false))).play();

        }else {
            for (UserPA checkUserPA : dStorage) {
                try {
                    if (checkUserPA.getUsername().equalsIgnoreCase(user) && checkUserPA.getPassword().equals(password)) {
                        fullName = checkUserPA.getUsername();
                        new switchScene(loginPAScene, "mainPage_PA.fxml");

                    } else {
                        error_login_message_PA.setVisible(true);
                        javafx.util.Duration duration = javafx.util.Duration.seconds(5);
                        new Timeline(new KeyFrame(duration, e -> error_login_message_PA.setVisible(false))).play();
                    }
                }catch (NullPointerException exception){
                    exception.printStackTrace();
                }
            }
        }
    }

    private boolean passwordVisible = false;
    @FXML
    public void psw_visibility(ActionEvent actionEvent) {
        if (!passwordVisible) {
            String password = userPasswordPA.getText();
            visiblePasswordField.setText(password);
            visiblePasswordField.setVisible(true);
            userPasswordPA.setVisible(false);
            passwordVisible = true;
        } else {
            String password = visiblePasswordField.getText();
            userPasswordPA.setText(password);
            visiblePasswordField.setVisible(false);
            userPasswordPA.setVisible(true);
            passwordVisible = false;
        }
    }
}
