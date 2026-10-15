package ni.edu.uam.citas_tecnicas.db;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

/**
 * Punto único de acceso a PostgreSQL.
 * Los datos de conexión se leen de db.properties (en la raíz del proyecto),
 */

public final class ConexionDB {

    private static final Path ARCHIVO_CONFIG = Path.of("db.properties");

    // Se carga una sola vez, la primera vez que alguien pide una conexión
    private static Properties config;

    // Clase de utilidades
    private ConexionDB() {
    }

    public static Connection getConexion() throws SQLException {
        Properties p = getConfig();
        return DriverManager.getConnection(
                p.getProperty("db.url"),
                p.getProperty("db.user"),
                p.getProperty("db.password"));
    }


     // Verifica que la base responde ejecutando SELECT 1 con un Statement.
    public static boolean probarConexion() throws SQLException {
        try (Connection con = getConexion();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery("SELECT 1")) {
            return rs.next() && rs.getInt(1) == 1;
        }
    }

    private static Properties getConfig() {
        if (config == null) {
            config = cargarConfig();
        }
        return config;
    }

    private static Properties cargarConfig() {
        if (!Files.exists(ARCHIVO_CONFIG)) {
            throw new IllegalStateException(
                    "No se encontró " + ARCHIVO_CONFIG.toAbsolutePath()
                    + ". Copia db.properties.example como db.properties y escribe tu contraseña.");
        }

        Properties p = new Properties();
        // Se lee como UTF-8 para que una contraseña con tildes o ñ no se corrompa
        try (Reader lector = Files.newBufferedReader(ARCHIVO_CONFIG, StandardCharsets.UTF_8)) {
            p.load(lector);
        } catch (IOException e) {
            throw new IllegalStateException("No se pudo leer " + ARCHIVO_CONFIG.toAbsolutePath(), e);
        }

        for (String clave : new String[]{"db.url", "db.user", "db.password"}) {
            if (p.getProperty(clave) == null) {
                throw new IllegalStateException("Falta la clave '" + clave + "' en db.properties.");
            }
        }
        return p;
    }
}
