package it.univr.passaporto_elettronico.controller;

import it.univr.passaporto_elettronico.switchScene;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ResourceBundle;

public class ConfirmReservationCController implements Initializable {

    @FXML
    private BorderPane confirmReservationCScene;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        confirmReservationCScene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                event.consume();
            }
        });
    }

    @FXML
    void onClickBackToMainPageC(ActionEvent event) throws IOException {
        new switchScene(confirmReservationCScene, "mainPage_C.fxml");
    }
    @FXML
    void onClickBackToListReservationC(ActionEvent event) throws IOException {
        new switchScene(confirmReservationCScene, "editReservation_C.fxml");
    }

    @FXML
    void onClickLogoutC(ActionEvent event) throws IOException {
        new switchScene(confirmReservationCScene, "login.fxml");
    }

    @FXML
    void onClickBackToAddReservationC(ActionEvent event) throws IOException {
        new switchScene(confirmReservationCScene, "addReservation_C.fxml");
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