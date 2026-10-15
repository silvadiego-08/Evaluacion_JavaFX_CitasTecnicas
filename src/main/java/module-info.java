module ni.edu.uam.citas_tecnicas {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires org.postgresql.jdbc;
    requires static lombok;
    requires jdk.compiler;

    opens ni.edu.uam.citas_tecnicas to javafx.fxml;
    opens ni.edu.uam.citas_tecnicas.controller to javafx.fxml;
    opens ni.edu.uam.citas_tecnicas.model to javafx.base;

    exports ni.edu.uam.citas_tecnicas;
}
