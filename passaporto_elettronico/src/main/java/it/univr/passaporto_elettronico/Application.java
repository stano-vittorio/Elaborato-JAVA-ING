package it.univr.passaporto_elettronico;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Application extends javafx.application.Application {

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Application.class.getResource("login.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1280, 760);
        stage.setResizable(false);
        stage.setTitle("PASSAPORTO ELETTRONICO");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) throws IOException {
        final DataStorage userCDataStorage = new DataStorage("userDataStorageC.dat");
        userCDataStorage.createUserCDataStorage();

        launch();
    }
}