module com.esprit3b5 {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    opens com.thesphynx to javafx.fxml;
    exports com.thesphynx;
}
