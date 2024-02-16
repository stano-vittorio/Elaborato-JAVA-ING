module it.univr.passaporto_elettronico {
    requires javafx.controls;
    requires javafx.fxml;


    opens it.univr.passaporto_elettronico to javafx.fxml;
    exports it.univr.passaporto_elettronico;
    exports it.univr.passaporto_elettronico.controller;
    opens it.univr.passaporto_elettronico.controller to javafx.fxml;
}