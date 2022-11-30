module com.example.tkachukkursovaya {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    exports Server;
    opens Server to javafx.fxml;
    exports Server.Interfaces;
    opens Server.Interfaces to javafx.fxml;
}