package ni.edu.uam.citas_tecnicas.util;

import ni.edu.uam.citas_tecnicas.model.Cliente;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.regex.Pattern;

public class Validador {
    public static boolean esVacio(String texto){
        return texto == null || texto.trim().isEmpty();
    }
    public static boolean correoValido(String correo){
        if (esVacio(correo)) {
            return false;
        }
        String regex = "^[\\w.+-]+@[\\w-]+\\.[\\w.]+$";
        return Pattern.matches(regex, correo);

    }
    public static boolean telefonoValido(String telefono){
        if (esVacio(telefono)){
            return false;
        }
        String regex = "^[0-9\\-\\s]+$";
        return Pattern.matches(regex, telefono);
    }
    public static boolean validarCita(Cliente cliente, LocalDate fecha, LocalTime hora,
                                      String tipoServicio, String descripcion, String estado) {
        if (cliente == null || fecha == null || hora == null || esVacio(estado) || esVacio(tipoServicio)) {
            Alertas.advertencia("Campos incompletos", "Cliente, fecha, hora, tipo de servicio y estado son obligatorios.");
            return false;
        }
        return true;
    }
}
