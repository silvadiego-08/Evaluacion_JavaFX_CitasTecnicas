package ni.edu.uam.citas_tecnicas.db;

public class PruebaConexion {
    public static void main(String[] args) {
        try {
            boolean ok = ConexionDB.probarConexion();
            System.out.println(ok ? "Conexión OK: SELECT 1 devolvió 1" : "Conectó, pero SELECT 1 no devolvió 1");
        } catch (Exception e) {
            System.out.println("Falló: " + e.getClass().getSimpleName() + " - " + e.getMessage());
        }
    }
}