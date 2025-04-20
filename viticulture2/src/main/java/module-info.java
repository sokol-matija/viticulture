module com.example.viticulture2 {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.viticulture2 to javafx.fxml;
    exports com.example.viticulture2;
    exports com.example.viticulture2.Controller;
    opens com.example.viticulture2.Controller to javafx.fxml;
}