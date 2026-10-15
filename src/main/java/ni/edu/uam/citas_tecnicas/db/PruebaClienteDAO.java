package ni.edu.uam.citas_tecnicas.db;

import ni.edu.uam.citas_tecnicas.dao.ClienteDAO;
import ni.edu.uam.citas_tecnicas.model.Cliente;

import java.sql.SQLException;

public class PruebaClienteDAO {

    public static void main(String[] args) {
        ClienteDAO dao = new ClienteDAO();
        try {
            // 1. Insertar (id null: lo genera la base)
            Cliente nuevo = new Cliente(null, "Prueba", "Temporal", "8888-0000", "prueba.temporal@test.com", true);
            dao.insertar(nuevo);
            System.out.println("1. Insertado: " + nuevo);

            // 2. Listar: debe aparecer el cliente nuevo
            System.out.println("2. Listar:");
            for (Cliente c : dao.listar()) {
                System.out.println("   " + c.getId() + " | " + c + " | " + c.getCorreo());
            }

            // 3. Buscar por parte del apellido (prueba de aceptación del enunciado)
            System.out.println("3. Buscar 'tempo': " + dao.buscar("tempo"));

            // Tomamos el id real desde la base
            Cliente guardado = dao.buscar("prueba.temporal@test.com").get(0);
            int id = guardado.getId();

            // 4. Correo duplicado: al crear (idExcluir null) debe ser true; excluyéndose a sí mismo, false
            System.out.println("4. existeCorreo al crear (esperado true): " + dao.existeCorreo("prueba.temporal@test.com", null));
            System.out.println("   existeCorreo al editar (esperado false): " + dao.existeCorreo("prueba.temporal@test.com", id));

            // 5. Actualizar teléfono
            guardado.setTelefono("7777-1111");
            dao.actualizar(guardado);
            System.out.println("5. Teléfono tras actualizar (esperado 7777-1111): " + dao.buscar("prueba.temporal@test.com").get(0).getTelefono());

            // 6. tieneCitas: un cliente recién creado no tiene citas
            System.out.println("6. tieneCitas (esperado false): " + dao.tieneCitas(id));

            // 7. Eliminar y comprobar que ya no está
            dao.eliminar(id);
            System.out.println("7. Tras eliminar, buscar (esperado []): " + dao.buscar("prueba.temporal@test.com"));

            // 8. Insertar un correo repetido debe fallar con SQLState 23505
            Cliente a = new Cliente(null, "Dup", "Uno", "1", "dup@test.com", true);
            Cliente b = new Cliente(null, "Dup", "Dos", "2", "dup@test.com", true);
            dao.insertar(a);
            try {
                dao.insertar(b);
                System.out.println("8. ERROR: aceptó un correo repetido");
            } catch (SQLException e) {
                System.out.println("8. Correo repetido rechazado, SQLState (esperado 23505): " + e.getSQLState());
            }
            dao.eliminar(dao.buscar("dup@test.com").get(0).getId());

            System.out.println("Fin: todas las operaciones se ejecutaron.");
        } catch (Exception e) {
            System.out.println("Falló: " + e.getClass().getSimpleName() + " - " + e.getMessage());
        }
    }
}