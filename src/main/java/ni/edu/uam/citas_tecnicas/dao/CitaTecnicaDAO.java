package ni.edu.uam.citas_tecnicas.dao;

import ni.edu.uam.citas_tecnicas.db.ConexionDB;
import ni.edu.uam.citas_tecnicas.model.CitaTecnica;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class CitaTecnicaDAO {

    // JOIN con cliente para traer el nombre completo que se muestra en la TableView.
    private static final String SELECT_CON_CLIENTE =
            "SELECT ct.id, ct.cliente_id, c.nombres || ' ' || c.apellidos AS cliente_nombre, "
                    + "ct.fecha, ct.hora, ct.tipo_servicio, ct.descripcion, ct.estado "
                    + "FROM cita_tecnica ct "
                    + "JOIN cliente c ON c.id = ct.cliente_id";

    private static final String ORDEN = " ORDER BY ct.fecha, ct.hora";

    public List<CitaTecnica> listar() throws SQLException {
        return filtrar(null, null);
    }

    public List<CitaTecnica> filtrar(String estado, LocalDate fecha) throws SQLException {
        StringBuilder sql = new StringBuilder(SELECT_CON_CLIENTE).append(" WHERE 1=1");
        List<Object> parametros = new ArrayList<>();

        if (estado != null && !estado.isBlank()) {
            sql.append(" AND ct.estado = ?");
            parametros.add(estado);
        }
        if (fecha != null) {
            sql.append(" AND ct.fecha = ?");
            parametros.add(Date.valueOf(fecha));
        }
        sql.append(ORDEN);

        List<CitaTecnica> citas = new ArrayList<>();
        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql.toString())) {
            for (int i = 0; i < parametros.size(); i++) {
                ps.setObject(i + 1, parametros.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    citas.add(mapear(rs));
                }
            }
        }
        return citas;
    }

    public void insertar(CitaTecnica cita) throws SQLException {
        String sql = "INSERT INTO cita_tecnica "
                + "(cliente_id, fecha, hora, tipo_servicio, descripcion, estado) "
                + "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            llenarCampos(ps, cita);
            ps.executeUpdate();
            // La base genera el id
            try (ResultSet claves = ps.getGeneratedKeys()) {
                if (claves.next()) {
                    cita.setId(claves.getInt(1));
                }
            }
        }
    }

    public void actualizar(CitaTecnica cita) throws SQLException {
        String sql = "UPDATE cita_tecnica SET cliente_id = ?, fecha = ?, hora = ?, "
                + "tipo_servicio = ?, descripcion = ?, estado = ? WHERE id = ?";
        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            llenarCampos(ps, cita);
            ps.setInt(7, cita.getId());
            ps.executeUpdate();
        }
    }

    public void eliminar(int id) throws SQLException {
        String sql = "DELETE FROM cita_tecnica WHERE id = ?";
        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    // Parámetros 1 a 6, comunes a INSERT y UPDATE (mismo orden de columnas).
    private void llenarCampos(PreparedStatement ps, CitaTecnica cita) throws SQLException {
        ps.setInt(1, cita.getClienteId());
        ps.setDate(2, Date.valueOf(cita.getFecha()));
        ps.setTime(3, Time.valueOf(cita.getHora()));
        ps.setString(4, cita.getTipoServicio());
        ps.setString(5, cita.getDescripcion());
        ps.setString(6, cita.getEstado());
    }

    // Convierte la fila actual del ResultSet en un objeto CitaTecnica.
    private CitaTecnica mapear(ResultSet rs) throws SQLException {
        return new CitaTecnica(
                rs.getInt("id"),
                rs.getInt("cliente_id"),
                rs.getString("cliente_nombre"),
                rs.getDate("fecha").toLocalDate(),
                rs.getTime("hora").toLocalTime(),
                rs.getString("tipo_servicio"),
                rs.getString("descripcion"),
                rs.getString("estado"));
    }
}
