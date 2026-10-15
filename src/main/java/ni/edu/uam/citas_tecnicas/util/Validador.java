package ni.edu.uam.citas_tecnicas.util;

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
}
