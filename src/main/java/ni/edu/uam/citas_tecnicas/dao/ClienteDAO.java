package ni.edu.uam.citas_tecnicas.dao;

import ni.edu.uam.citas_tecnicas.model.Cliente;
import ni.edu.uam.citas_tecnicas.db.ConexionDB;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ClienteDAO {

    // obtiene la lista completa de clientes ordenados por su ID
    public List<Cliente> listar() throws SQLException {
        String sql = "SELECT id, nombres, apellidos, telefono, correo, activo FROM cliente ORDER BY id";
        List<Cliente> lista = new ArrayList<>();

        // try q cierra automáticamente la conexión, el statement y el resultset al terminar
        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapResultSetToCliente(rs));
            }
        }
        return lista;
    }

    // busca clientes por coincidencias en nombres, apellidos o correo (insensible a mayúsculas/minúsculas)
    public List<Cliente> buscar(String texto) throws SQLException {
        String sql = "SELECT id, nombres, apellidos, telefono, correo, activo FROM cliente " +
                "WHERE nombres ILIKE ? OR apellidos ILIKE ? OR correo ILIKE ? ORDER BY id";
        List<Cliente> lista = new ArrayList<>();
        String filtro = "%" + texto + "%";

        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            // asigna el filtro a los tres parámetros de la consulta (? ? ?)
            ps.setString(1, filtro);
            ps.setString(2, filtro);
            ps.setString(3, filtro);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapResultSetToCliente(rs));
                }
            }
        }
        return lista;
    }

    public void insertar(Cliente c) throws SQLException {
        String sql = "INSERT INTO cliente (nombres, apellidos, telefono, correo, activo) VALUES (?, ?, ?, ?, ?)";

        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, c.getNombres());
            ps.setString(2, c.getApellidos());
            ps.setString(3, c.getTelefono());
            ps.setString(4, c.getCorreo());
            ps.setBoolean(5, c.isActivo());

            ps.executeUpdate();
        }
    }

    public void actualizar(Cliente c) throws SQLException {
        String sql = "UPDATE cliente SET nombres = ?, apellidos = ?, telefono = ?, correo = ?, activo = ? WHERE id = ?";

        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, c.getNombres());
            ps.setString(2, c.getApellidos());
            ps.setString(3, c.getTelefono());
            ps.setString(4, c.getCorreo());
            ps.setBoolean(5, c.isActivo());
            // Condición WHERE id = ?
            ps.setInt(6, c.getId());

            ps.executeUpdate();
        }
    }

    // elimina de la base de datos un cliente por su ID
    public void eliminar(int id) throws SQLException {
        String sql = "DELETE FROM cliente WHERE id = ?";

        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    // idExcluir es null al CREAR un cliente nuevo, o el ID del cliente al EDITAR (para q no choque consigo mismo)
    public boolean existeCorreo(String correo, Integer idExcluir) throws SQLException {
        StringBuilder sql = new StringBuilder("SELECT 1 FROM cliente WHERE correo = ?");
        if (idExcluir != null) {
            sql.append(" AND id <> ?");
        }

        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql.toString())) {

            ps.setString(1, correo);
            // si estamos editando, asigna el ID a ignorar en el parámetro 2
            if (idExcluir != null) {
                ps.setInt(2, idExcluir);
            }

            try (ResultSet rs = ps.executeQuery()) {
                // devuelve true si encontró al menos un registro (el correo ya existe)
                return rs.next();
            }
        }
    }

    public boolean tieneCitas(int clienteId) throws SQLException {
        String sql = "SELECT 1 FROM cita_tecnica WHERE cliente_id = ?";

        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, clienteId);

            try (ResultSet rs = ps.executeQuery()) {
                // devuelve true si el cliente tiene citas asociadas
                return rs.next();
            }
        }
    }

    // método auxiliar privado para convertir la fila actual del ResultSet en un objeto Cliente
    private Cliente mapResultSetToCliente(ResultSet rs) throws SQLException {
        return new Cliente(
                rs.getInt("id"),
                rs.getString("nombres"),
                rs.getString("apellidos"),
                rs.getString("telefono"),
                rs.getString("correo"),
                rs.getBoolean("activo")
        );
    }
}