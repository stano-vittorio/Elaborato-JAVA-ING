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
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ResourceBundle;

public class PersonalProfileCController implements Initializable {

    @FXML
    private BorderPane personalProfileCScene;

    @FXML
    private Label welcomeLabel;

    @FXML
    private Label nameLabel;

    @FXML
    private Label surnameLabel;

    @FXML
    private Label birthDateLabel;

    @FXML
    private Label birthPlaceLabel;

    @FXML
    private Label taxIdCodeLabel;

    @FXML
    private Label medicalCardLabel;

    @FXML
    private Label userTypeLabel;

    @FXML
    private Label userPassportLabel;

    @FXML
    private Label usernameLabel;

    @FXML
    private Text oldPasswordText;

    @FXML
    private TextField oldPasswordField;

    @FXML
    private Text newPasswordText;

    @FXML
    private TextField newPasswordField;

    @FXML
    private Button saveButton;

    @FXML
    private Label error_old_password_wrong;

    @FXML
    private Label error_new_password_wrong;

    @FXML
    private Label password_changed;

    private final DataStorage userDataStorage = new DataStorage("userDataStorageC.dat");

    DataSingleton currentUser = DataSingleton.getInstance();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        welcomeLabel.setText("Ciao, " + LoginCController.getFullName());

        try {
            nameLabel.setText(currentUser.getUser().getName());
            surnameLabel.setText(currentUser.getUser().getSurname());
            birthDateLabel.setText(currentUser.getUser().getBirthDate().toString());
            birthPlaceLabel.setText(currentUser.getUser().getBirthPlace());
            taxIdCodeLabel.setText(currentUser.getUser().getTaxIdCode());
            usernameLabel.setText(currentUser.getUser().getUsername());
            medicalCardLabel.setText(currentUser.getUser().getMedicalCard());
            userTypeLabel.setText(currentUser.getUser().getUserType());

            if (currentUser.getUser().getUserPassport()) {
                userPassportLabel.setText("RICHIESTO");
            } else {
                userPassportLabel.setText("NON RICHIESTO");
            }

        }catch (NullPointerException exception){
            exception.printStackTrace();
        }

        personalProfileCScene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                event.consume();

                if (!password_changed.isVisible() && oldPasswordText.isVisible() && oldPasswordField.isVisible() &&
                        newPasswordText.isVisible() && newPasswordField.isVisible() && saveButton.isVisible()) {
                    try {
                        onClickSetNewPassword(new ActionEvent());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });
    }

    @FXML
    void onClickUndoFromPersonalProfileCToMainPageC(ActionEvent event) throws IOException {
        new switchScene(personalProfileCScene, "mainPage_C.fxml");
    }

    @FXML
    void onClickLogoutC(ActionEvent event) throws IOException {
        currentUser.setUser(null);
        new switchScene(personalProfileCScene, "login.fxml");
    }

    private boolean passwordFieldsVisible = false;

    @FXML
    void onClickChangePassword(ActionEvent event) throws IOException {
        if (passwordFieldsVisible) {
            // SE SONO VISIBILI --> NASCONDO
            password_changed.setVisible(false);
            oldPasswordText.setVisible(false);
            oldPasswordField.setVisible(false);
            newPasswordText.setVisible(false);
            newPasswordField.setVisible(false);
            saveButton.setVisible(false);
            error_old_password_wrong.setVisible(false);
            error_new_password_wrong.setVisible(false);
            oldPasswordField.clear();
            newPasswordField.clear();
            passwordFieldsVisible = false;
        } else {
            // SE NON SONO VISIBILI --> MOSTRO
            oldPasswordText.setVisible(true);
            oldPasswordField.setVisible(true);
            newPasswordText.setVisible(true);
            newPasswordField.setVisible(true);
            saveButton.setVisible(true);
            passwordFieldsVisible = true;
        }
    }

    @FXML
    void onClickSetNewPassword(ActionEvent event) throws IOException{
        String oldPassword = oldPasswordField.getText();
        String newPassword = newPasswordField.getText();

        try{
            if(oldPassword.equals(currentUser.getUser().getPassword()) && newPassword.length() > 5){

                userDataStorage.removeUser(currentUser.getUser());

                currentUser.getUser().setPassword(newPassword);

                userDataStorage.addUser(currentUser.getUser());

                oldPasswordText.setVisible(false);
                oldPasswordField.setVisible(false);
                newPasswordText.setVisible(false);
                newPasswordField.setVisible(false);
                saveButton.setVisible(false);
                oldPasswordField.clear();
                newPasswordField.clear();

                error_old_password_wrong.setVisible(false);
                error_new_password_wrong.setVisible(false);
                password_changed.setVisible(true);
                javafx.util.Duration duration = javafx.util.Duration.seconds(5);
                new Timeline(new KeyFrame(duration, e -> password_changed.setVisible(false))).play();

            }else if (!oldPassword.equals(currentUser.getUser().getPassword())){

                error_old_password_wrong.setVisible(false);

                error_new_password_wrong.setVisible(false);
                password_changed.setVisible(false);
                error_old_password_wrong.setVisible(true);
                javafx.util.Duration duration = javafx.util.Duration.seconds(5);
                new Timeline(new KeyFrame(duration, e -> error_old_password_wrong.setVisible(false))).play();

            }else if(newPassword.length() < 6){
                error_new_password_wrong.setVisible(false);

                password_changed.setVisible(false);
                error_old_password_wrong.setVisible(false);
                error_new_password_wrong.setVisible(true);
                javafx.util.Duration duration = javafx.util.Duration.seconds(5);
                new Timeline(new KeyFrame(duration, e -> error_new_password_wrong.setVisible(false))).play();
            }

        }catch(NullPointerException exception){
            exception.printStackTrace();
        }

    }

    @FXML
    void onClickDownloadForm(ActionEvent event) throws IOException {

        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save PDF form");

            FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("File PDF (*.pdf)", "*.pdf");
            fileChooser.getExtensionFilters().add(extFilter);

            fileChooser.setInitialFileName("Modulo richiesta Passaporto.pdf");

            File file = fileChooser.showSaveDialog(new Stage());

            if (file != null) {
                String pdfPath = "src/main/resources/it/univr/passaporto_elettronico/Modulo richiesta Passaporto.pdf";
                File pdfFile = new File(pdfPath);

                Files.copy(pdfFile.toPath(), file.toPath(), StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
