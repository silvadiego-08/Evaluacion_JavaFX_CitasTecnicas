module ni.edu.uam.citas_tecnicas {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires org.postgresql.jdbc;
    requires static lombok;
<<<<<<< HEAD
    requires jdk.compiler;
=======
>>>>>>> 63e9535c0e895dc8734c94a23b5ffbbcfe0d08e2

    opens ni.edu.uam.citas_tecnicas to javafx.fxml;
    opens ni.edu.uam.citas_tecnicas.controller to javafx.fxml;
    opens ni.edu.uam.citas_tecnicas.model to javafx.base;

    exports ni.edu.uam.citas_tecnicas;
}
