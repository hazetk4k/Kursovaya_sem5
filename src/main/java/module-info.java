module com.example.tkachukkursovaya {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.tkachukkursovaya to javafx.fxml;
    exports com.example.tkachukkursovaya;
}