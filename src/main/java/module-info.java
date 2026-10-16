module com.desarrollo.proyectocorteii {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.desarrollo.proyectocorteii to javafx.fxml;
    exports com.desarrollo.proyectocorteii;
}