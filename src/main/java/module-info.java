module com.example.tkachukkursovaya {
    requires javafx.controls;
    requires javafx.fxml;


    exports Server.mvc;
    opens Server.mvc to javafx.fxml;
}