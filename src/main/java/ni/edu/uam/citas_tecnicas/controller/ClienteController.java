package ni.edu.uam.citas_tecnicas.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import ni.edu.uam.citas_tecnicas.dao.ClienteDAO;
import ni.edu.uam.citas_tecnicas.model.Cliente;
import ni.edu.uam.citas_tecnicas.util.Alertas;
import ni.edu.uam.citas_tecnicas.util.Validador;

import java.sql.SQLException;
import java.util.List;

public class ClienteController {

    @FXML private TextField txtNombres;
    @FXML private TextField txtApellidos;
    @FXML private TextField txtTelefono;
    @FXML private TextField txtCorreo;
    @FXML private CheckBox chkActivo;
    @FXML private TextField txtBuscar;

    @FXML private TableView<Cliente> tableClientes;
    @FXML private TableColumn<Cliente, Integer> colId;
    @FXML private TableColumn<Cliente, String> colNombres;
    @FXML private TableColumn<Cliente, String> colApellidos;
    @FXML private TableColumn<Cliente, String> colTelefono;
    @FXML private TableColumn<Cliente, String> colCorreo;
    @FXML private TableColumn<Cliente, Boolean> colActivo;

    // Todo el acceso a la base de datos pasa por el DAO (que usa ConexionDB)
    private final ClienteDAO clienteDAO = new ClienteDAO();

    @FXML
    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNombres.setCellValueFactory(new PropertyValueFactory<>("nombres"));
        colApellidos.setCellValueFactory(new PropertyValueFactory<>("apellidos"));
        colTelefono.setCellValueFactory(new PropertyValueFactory<>("telefono"));
        colCorreo.setCellValueFactory(new PropertyValueFactory<>("correo"));
        colActivo.setCellValueFactory(new PropertyValueFactory<>("activo"));

        cargarClientes();

        tableClientes.getSelectionModel().selectedItemProperty().addListener((obs, anterior, seleccionado) -> {
            if (seleccionado != null) {
                mostrarDetalleCliente(seleccionado);
            }
        });
    }

    private void cargarClientes() {
        try {
            mostrarEnTabla(clienteDAO.listar());
        } catch (SQLException e) {
            Alertas.error("No se pudieron cargar los clientes.", e);
        }
    }

    private void mostrarEnTabla(List<Cliente> clientes) {
        tableClientes.setItems(FXCollections.observableArrayList(clientes));
    }

    private void mostrarDetalleCliente(Cliente cliente) {
        txtNombres.setText(cliente.getNombres());
        txtApellidos.setText(cliente.getApellidos());
        txtTelefono.setText(cliente.getTelefono());
        txtCorreo.setText(cliente.getCorreo());
        chkActivo.setSelected(cliente.isActivo());
    }

    @FXML
    private void handleNuevo() {
        handleLimpiar();
    }

    @FXML
    private void handleGuardar() {
        Cliente nuevo = leerFormulario();
        if (!datosValidos(nuevo, null)) {
            return;
        }

        try {
            clienteDAO.insertar(nuevo);
            Alertas.info("Éxito", "Cliente registrado correctamente.");
            cargarClientes();
            handleLimpiar();
        } catch (SQLException e) {
            Alertas.error("No se pudo guardar el cliente.", e);
        }
    }

    @FXML
    private void handleActualizar() {
        Cliente seleccionado = tableClientes.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            Alertas.advertencia("Selección requerida", "Seleccione un cliente de la tabla para actualizarlo.");
            return;
        }

        Cliente editado = leerFormulario();
        editado.setId(seleccionado.getId());
        if (!datosValidos(editado, seleccionado.getId())) {
            return;
        }

        try {
            clienteDAO.actualizar(editado);
            Alertas.info("Éxito", "Cliente actualizado correctamente.");
            cargarClientes();
            handleLimpiar();
        } catch (SQLException e) {
            Alertas.error("No se pudo actualizar el cliente.", e);
        }
    }

    @FXML
    private void handleEliminar() {
        Cliente seleccionado = tableClientes.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            Alertas.advertencia("Selección requerida", "Seleccione un cliente de la tabla para eliminarlo.");
            return;
        }

        try {
            // Regla del negocio: no se elimina un cliente que tenga citas asociadas
            if (clienteDAO.tieneCitas(seleccionado.getId())) {
                Alertas.advertencia("Eliminación restringida",
                        "No se puede eliminar a " + seleccionado + " porque tiene citas técnicas asociadas. "
                                + "Elimine primero sus citas.");
                return;
            }

            if (!Alertas.confirmar("Confirmar eliminación", "¿Está seguro de eliminar a " + seleccionado + "?")) {
                return;
            }

            clienteDAO.eliminar(seleccionado.getId());
            Alertas.info("Éxito", "Cliente eliminado correctamente.");
            cargarClientes();
            handleLimpiar();
        } catch (SQLException e) {
            Alertas.error("No se pudo eliminar el cliente.", e);
        }
    }

    @FXML
    private void handleBuscar() {
        String texto = txtBuscar.getText();
        if (Validador.esVacio(texto)) {
            cargarClientes();
            return;
        }

        try {
            mostrarEnTabla(clienteDAO.buscar(texto.trim()));
        } catch (SQLException e) {
            Alertas.error("Ocurrió un error al buscar clientes.", e);
        }
    }

    @FXML
    private void handleLimpiar() {
        txtNombres.clear();
        txtApellidos.clear();
        txtTelefono.clear();
        txtCorreo.clear();
        chkActivo.setSelected(false);
        txtBuscar.clear();
        tableClientes.getSelectionModel().clearSelection();
    }

    private Cliente leerFormulario() {
        Cliente c = new Cliente();
        c.setNombres(txtNombres.getText().trim());
        c.setApellidos(txtApellidos.getText().trim());
        c.setTelefono(txtTelefono.getText().trim());
        c.setCorreo(txtCorreo.getText().trim());
        c.setActivo(chkActivo.isSelected());
        return c;
    }

    /**
     * Valida el formulario antes de guardar o actualizar.
     * idExcluir es null al crear; al editar es el ID del propio cliente,
     * para que su correo actual no cuente como duplicado.
     */
    private boolean datosValidos(Cliente c, Integer idExcluir) {
        if (Validador.esVacio(c.getNombres()) || Validador.esVacio(c.getApellidos())
                || Validador.esVacio(c.getTelefono()) || Validador.esVacio(c.getCorreo())) {
            Alertas.advertencia("Campos vacíos", "Todos los campos obligatorios deben estar llenos.");
            return false;
        }
        if (!Validador.correoValido(c.getCorreo())) {
            Alertas.advertencia("Correo inválido", "El formato del correo electrónico no es válido.");
            return false;
        }
        if (!Validador.telefonoValido(c.getTelefono())) {
            Alertas.advertencia("Teléfono inválido", "El teléfono solo debe contener dígitos, guiones o espacios.");
            return false;
        }

        try {
            if (clienteDAO.existeCorreo(c.getCorreo(), idExcluir)) {
                Alertas.advertencia("Correo duplicado", "Ya existe un cliente registrado con ese correo.");
                return false;
            }
        } catch (SQLException e) {
            Alertas.error("No se pudo verificar el correo.", e);
            return false;
        }
        return true;
    }
}
