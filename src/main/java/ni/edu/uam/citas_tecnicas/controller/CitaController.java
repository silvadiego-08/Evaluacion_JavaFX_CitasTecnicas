package ni.edu.uam.citas_tecnicas.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import ni.edu.uam.citas_tecnicas.dao.CitaTecnicaDAO;
import ni.edu.uam.citas_tecnicas.dao.ClienteDAO;
import ni.edu.uam.citas_tecnicas.model.CitaTecnica;
import ni.edu.uam.citas_tecnicas.model.Cliente;
import ni.edu.uam.citas_tecnicas.util.Alertas;
import ni.edu.uam.citas_tecnicas.util.Validador;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class CitaController {
    // --- FORMULARIO ---
    @FXML private ComboBox<Cliente> cbCliente;
    @FXML private DatePicker dpFecha;
    @FXML private ComboBox<LocalTime> cbHora;
    @FXML private TextField txtTipoServicio;
    @FXML private TextArea txtDescripcion;
    @FXML private ComboBox<String> cbEstado;

    // --- FILTROS ---
    @FXML private ComboBox<String> cbFiltroEstado;
    @FXML private DatePicker dpFiltroFecha;

    // --- TABLA DE CITAS ---
    @FXML private TableView<CitaTecnica> tablaCitas;
    @FXML private TableColumn<CitaTecnica, Integer> colId;
    @FXML private TableColumn<CitaTecnica, String> colCliente;
    @FXML private TableColumn<CitaTecnica, LocalDate> colFecha;
    @FXML private TableColumn<CitaTecnica, LocalTime> colHora;
    @FXML private TableColumn<CitaTecnica, String> colServicio;
    @FXML private TableColumn<CitaTecnica, String> colEstado;

    // --- DAOs Y VARIABLES DE ESTADO ---
    private final CitaTecnicaDAO citaDAO = new CitaTecnicaDAO();
    private final ClienteDAO clienteDAO = new ClienteDAO();
    private CitaTecnica citaSeleccionada;

    @FXML
    public void initialize() {
        // 1. Mapeo de columnas con los atributos de CitaTecnica
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colCliente.setCellValueFactory(new PropertyValueFactory<>("clienteNombre"));
        colFecha.setCellValueFactory(new PropertyValueFactory<>("fecha"));
        colHora.setCellValueFactory(new PropertyValueFactory<>("hora"));
        colServicio.setCellValueFactory(new PropertyValueFactory<>("tipoServicio"));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));

        // 2. Cargar Estados fijos en los ComboBox de estado
        ObservableList<String> estados = FXCollections.observableArrayList(
                "Pendiente", "En proceso", "Finalizada", "Cancelada"
        );
        cbEstado.setItems(estados);
        cbFiltroEstado.setItems(estados);

        // 3. Cargar Horas en el ComboBox de hora (08:00 a 17:30 cada 30 min)
        ObservableList<LocalTime> horas = FXCollections.observableArrayList();
        LocalTime hora = LocalTime.of(8, 0);
        while (!hora.isAfter(LocalTime.of(17, 30))) {
            horas.add(hora);
            hora = hora.plusMinutes(30);
        }
        cbHora.setItems(horas);

        // 4. Listener para seleccionar fila de la tabla y llenar el formulario
        tablaCitas.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel != null) {
                citaSeleccionada = newSel;
                cargarFormulario(newSel);
            }
        });

        // 5. Cargar datos iniciales
        cargarTabla();
        cargarClientes();
    }

    /**
     * Carga todos los registros de citas desde la base de datos a la TableView.
     */
    private void cargarTabla() {
        try {
            List<CitaTecnica> lista = citaDAO.listar();
            tablaCitas.setItems(FXCollections.observableArrayList(lista));
        } catch (SQLException e) {
            e.printStackTrace();
            Alertas.error("No se pudo cargar la lista de citas.", e);
        }
    }

    /**
     * Carga la lista de clientes en el ComboBox usando clienteDAO.listar().
     * Invoca este método también cuando se alterne o abra la pestaña de citas.
     */
    public void cargarClientes() {
        try {
            List<Cliente> clientes = clienteDAO.listar();
            cbCliente.setItems(FXCollections.observableArrayList(clientes));
        } catch (SQLException e) {
            e.printStackTrace();
            Alertas.error("No se pudo cargar la lista de clientes.", e);
        }
    }

    // --- ACCIONES DE BOTONES ---

    @FXML
    private void onGuardar() {
        try {
            if (!Validador.validarCita(cbCliente.getValue(), dpFecha.getValue(), cbHora.getValue(),
                    txtTipoServicio.getText(), txtDescripcion.getText(), cbEstado.getValue())) {
                 return;
             }

            Cliente clienteSeleccionado = cbCliente.getValue();
            if (clienteSeleccionado == null || dpFecha.getValue() == null || cbHora.getValue() == null || cbEstado.getValue() == null) {
                Alertas.advertencia("Campos incompletos", "Por favor complete los campos obligatorios.");
                return;
            }

            CitaTecnica nuevaCita = new CitaTecnica();
            nuevaCita.setClienteId(clienteSeleccionado.getId());
            nuevaCita.setFecha(dpFecha.getValue());
            nuevaCita.setHora(cbHora.getValue());
            nuevaCita.setTipoServicio(txtTipoServicio.getText().trim());
            nuevaCita.setDescripcion(txtDescripcion.getText().trim());
            nuevaCita.setEstado(cbEstado.getValue());

            citaDAO.insertar(nuevaCita);
            Alertas.info("Éxito", "Cita registrada correctamente.");

            cargarTabla();
            onLimpiarFormulario();
        } catch (SQLException e) {
            e.printStackTrace();
            Alertas.error("No se pudo guardar la cita.", e);
        }
    }

    @FXML
    private void onActualizar() {
        if (citaSeleccionada == null) {
            Alertas.advertencia("Atención", "Seleccione una cita de la tabla para actualizar.");
            return;
        }

        try {
            Cliente clienteSeleccionado = cbCliente.getValue();
            if (clienteSeleccionado != null) {
                citaSeleccionada.setClienteId(clienteSeleccionado.getId());
            }
            citaSeleccionada.setFecha(dpFecha.getValue());
            citaSeleccionada.setHora(cbHora.getValue());
            citaSeleccionada.setTipoServicio(txtTipoServicio.getText().trim());
            citaSeleccionada.setDescripcion(txtDescripcion.getText().trim());
            citaSeleccionada.setEstado(cbEstado.getValue());

            citaDAO.actualizar(citaSeleccionada);
            Alertas.info("Éxito", "Cita actualizada correctamente.");

            cargarTabla();
            onLimpiarFormulario();
        } catch (SQLException e) {
            e.printStackTrace();
            Alertas.error("No se pudo actualizar la cita.", e);
        }
    }

    @FXML
    private void onEliminar() {
        if (citaSeleccionada == null) {
            Alertas.advertencia("Atención", "Seleccione una cita de la tabla para eliminar.");
            return;
        }

        boolean confirmado = Alertas.confirmar("Confirmar Eliminación", "¿Está seguro de eliminar esta cita?");
        if (!confirmado) return;

        try {
            citaDAO.eliminar(citaSeleccionada.getId());
            Alertas.info("Éxito", "Cita eliminada correctamente.");

            cargarTabla();
            onLimpiarFormulario();
        } catch (SQLException e) {
            e.printStackTrace();
            Alertas.error("No se pudo eliminar la cita.", e);
        }
    }

    @FXML
    private void onFiltrar() {
        try {
            String estado = cbFiltroEstado.getValue();
            LocalDate fecha = dpFiltroFecha.getValue();

            // Pasa null si está vacío para que el DAO ignore dicho filtro
            List<CitaTecnica> filtradas = citaDAO.filtrar(estado, fecha);
            tablaCitas.setItems(FXCollections.observableArrayList(filtradas));
        } catch (SQLException e) {
            e.printStackTrace();
            Alertas.error("Error al filtrar las citas.", e);
        }
    }

    @FXML
    private void onLimpiarFiltros() {
        cbFiltroEstado.setValue(null);
        dpFiltroFecha.setValue(null);
        cargarTabla();
    }

    @FXML
    private void onNuevo() {
        onLimpiarFormulario();
    }

    @FXML
    private void onLimpiarFormulario() {
        citaSeleccionada = null;
        cbCliente.setValue(null);
        dpFecha.setValue(null);
        cbHora.setValue(null);
        txtTipoServicio.clear();
        txtDescripcion.clear();
        cbEstado.setValue(null);
        tablaCitas.getSelectionModel().clearSelection();
    }

    /**
     * Llena los controles del formulario cuando se selecciona una fila en la TableView.
     */
    private void cargarFormulario(CitaTecnica cita) {
        dpFecha.setValue(cita.getFecha());
        cbHora.setValue(cita.getHora());
        txtTipoServicio.setText(cita.getTipoServicio());
        txtDescripcion.setText(cita.getDescripcion());
        cbEstado.setValue(cita.getEstado());

        // Selecciona en el ComboBox al cliente con el mismo clienteId de la cita
        if (cbCliente.getItems() != null) {
            for (Cliente c : cbCliente.getItems()) {
                if (c.getId() != null && c.getId().equals(cita.getClienteId())) {
                    cbCliente.setValue(c);
                    break;
                }
            }
        }
    }

}
