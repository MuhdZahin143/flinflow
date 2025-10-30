module com.example.flinflow {
    requires javafx.controls;
    requires javafx.fxml;
        requires java.sql;   

    opens com.example.flinflow to javafx.fxml;
    exports com.example.flinflow;
}
