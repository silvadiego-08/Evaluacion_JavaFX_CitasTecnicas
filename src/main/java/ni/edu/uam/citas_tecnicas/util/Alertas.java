package ni.edu.uam.citas_tecnicas.util;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;

import java.net.URL;

/**
 * mensaje reutilizables para todos los controladores

 * Se deben llamar desde el hilo de JavaFX (por ejemplo, dentro de un evento de botón).
 */
public final class Alertas {

    private static final String RUTA_CSS = "/ni/edu/uam/citas_tecnicas/css/styles.css";

    // Botones propios para que la confirmación diga si o no

    private static final ButtonType BOTON_SI = new ButtonType("Sí", ButtonBar.ButtonData.YES);
    private static final ButtonType BOTON_NO = new ButtonType("No", ButtonBar.ButtonData.NO);

    private Alertas() {
    }

    // Operación exitosa
    public static void info(String titulo, String mensaje) {
        mostrar(Alert.AlertType.INFORMATION, titulo, mensaje);
    }

    // Datos inválidos o acción no permitida
    public static void advertencia(String titulo, String mensaje) {
        mostrar(Alert.AlertType.WARNING, titulo, mensaje);
    }

    // Error que el usuario no puede corregir desde el formulario
    public static void error(String titulo, String mensaje) {
        mostrar(Alert.AlertType.ERROR, titulo, mensaje);
    }

    /**
     * Error con su causa técnica, para el catch de una SQLException:
     * El mensaje de la excepción se muestra debajo para poder diagnosticar el problema
     */
    public static void error(String mensaje, Exception e) {
        mostrar(Alert.AlertType.ERROR, "Error", mensaje + "\n\nDetalle: " + e.getMessage());
    }

    /**
     * Pide confirmación antes de una acción irreversible (eliminar, cancelar una cita).
     * Devuelve true solo si el usuario pulsa si, cerrar la ventana cuenta como No.
     */
    public static boolean confirmar(String titulo, String mensaje) {
        Alert alerta = crear(Alert.AlertType.CONFIRMATION, titulo, mensaje);
        alerta.getButtonTypes().setAll(BOTON_SI, BOTON_NO);
        return alerta.showAndWait().orElse(BOTON_NO) == BOTON_SI;
    }

    private static void mostrar(Alert.AlertType tipo, String titulo, String mensaje) {
        crear(tipo, titulo, mensaje).showAndWait();
    }

    private static Alert crear(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);

        // Una alerta es una ventana aparte, entonces no hereda el CSS de la ventana principal
        URL css = Alertas.class.getResource(RUTA_CSS);
        if (css != null) {
            alerta.getDialogPane().getStylesheets().add(css.toExternalForm());
        }
        return alerta;
    }
}
