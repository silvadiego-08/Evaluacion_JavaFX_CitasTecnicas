module ni.edu.uam.citas_tecnicas {
    requires javafx.controls;
    requires javafx.fxml;


    opens ni.edu.uam.citas_tecnicas to javafx.fxml;
    exports ni.edu.uam.citas_tecnicas;
}