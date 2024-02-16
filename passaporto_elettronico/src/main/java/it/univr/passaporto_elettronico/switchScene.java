package it.univr.passaporto_elettronico;

import javafx.fxml.FXMLLoader;
import javafx.scene.layout.BorderPane;

import java.io.IOException;
import java.util.Objects;

public class switchScene {
    public switchScene(BorderPane currentAnchorPane, String fxml) throws IOException {
        BorderPane nextBorderPane = FXMLLoader.load(Objects.requireNonNull(Application.class.getResource(fxml)));
        currentAnchorPane.getChildren().removeAll();
        currentAnchorPane.getChildren().setAll(nextBorderPane);
    }
}
