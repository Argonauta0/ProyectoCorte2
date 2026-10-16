module com.desarrollo.proyectocorteii {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    opens com.desarrollo.proyectocorteii to javafx.fxml;
    opens com.desarrollo.proyectocorteii.Controller to javafx.fxml;
    opens com.desarrollo.proyectocorteii.Model to javafx.base, javafx.fxml;

    exports com.desarrollo.proyectocorteii;
    exports com.desarrollo.proyectocorteii.Controller;
    exports com.desarrollo.proyectocorteii.Model;
    exports com.desarrollo.proyectocorteii.DAO;
}