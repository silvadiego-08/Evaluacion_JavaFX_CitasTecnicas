package ni.edu.uam.citas_tecnicas.controller;

import com.sun.source.tree.TryTree;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import ni.edu.uam.citas_tecnicas.model.Cliente;
import ni.edu.uam.citas_tecnicas.util.Validador;

import java.sql.*;

public class ClienteController {

    @FXML
    private TextField txtNombres;
    @FXML
    private TextField txtApellidos;
    @FXML
    private TextField txtTelefono;
    @FXML
    private TextField txtCorreo;
    @FXML
    private CheckBox chkActivo;
    @FXML
    private TextField txtBuscar;

    @FXML
    private TableView<Cliente> tableClientes;
    @FXML
    private TableColumn<Cliente, Integer> colId;
    @FXML
    private TableColumn<Cliente, String> colNombres;
    @FXML
    private TableColumn<Cliente, String> colApellidos;
    @FXML
    private TableColumn<Cliente, String> colTelefono;
    @FXML
    private TableColumn<Cliente, String> colCorreo;
    @FXML
    private TableColumn<Cliente, Boolean> colActivo;

    private ObservableList<Cliente> listaClientes = FXCollections.observableArrayList();
    @FXML
    public void initialize(){
        colId.setCellValueFactory((new PropertyValueFactory<>("id")));
        colNombres.setCellValueFactory(new PropertyValueFactory<>("nombres"));
        colApellidos.setCellValueFactory((new PropertyValueFactory<>("apellidos")));
        colTelefono.setCellValueFactory(new PropertyValueFactory<>("telefono"));
        colCorreo.setCellValueFactory(new PropertyValueFactory<>("correo"));
        colActivo.setCellValueFactory(new PropertyValueFactory<>("correo"));

        cargarClientes();

        tableClientes.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null){
                mostrarDetalleCliente(newSelection);
            }

        });

    }

    private void cargarClientes() {
    listaClientes.clear();
    String sql = "SELECT id, nombres, apellidos, telefono, correo, activo FROM cliente";

    try (Connection conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/gestion_citas", "postgres", "tu_password");
         Statement stmt = conn.createStatement();
         ResultSet rs = stmt.executeQuery(sql)) {

        while (rs.next()) {
            Cliente c = new Cliente(
                    rs.getInt("id"),
                    rs.getString("nombres"),
                    rs.getString("apellidos"),
                    rs.getString("telefono"),
                    rs.getString("correo"),
                    rs.getBoolean("activo")
            );
            listaClientes.add(c);
        }
        tableClientes.setItems(listaClientes);

    } catch (SQLException e) {
        mostrarAlerta("Error de BD", "No se pudieron cargar los clientes: " + e.getMessage(), Alert.AlertType.ERROR);
    }
}

private void mostrarDetalleCliente(Cliente cliente){
    txtNombres.setText(cliente.getNombres());
    txtApellidos.setText(cliente.getApellidos());
    txtTelefono.setText(cliente.getTelefono());
    txtCorreo.setText(cliente.getCorreo());
    chkActivo.setSelected(cliente.isActivo());
}
@FXML
private void handleNuevo() {
    handleLimpiar();
    tableClientes.getSelectionModel().clearSelection();
}
@FXML
private void handleGuardar(){
    String nombres = txtNombres.getText();
    String apellidos = txtApellidos.getText();
    String telefono = txtTelefono.getText();
    String correo = txtCorreo.getText();
    boolean activo = chkActivo.isSelected();

    if (!Validador.esVacio(nombres)) || Validador.esVacio(apellidos)|| Validador.esVacio(telefono) || Validador.esVacio(correo){
        mostrarAlerta("Campos Vacios", "Todos los campos obligatorios deben estar llenos.", Alert.AlertType.WARNING);
        return;
    }
    if (!Validador.correoValido(correo)) {
        mostrarAlerta("Correo Inválido", "El formato del correo electrónico no es válido.", Alert.AlertType.WARNING);
        return;
    }

    if (!Validador.telefonoValido(telefono)) {
        mostrarAlerta("Teléfono Inválido", "El teléfono solo debe contener dígitos, guiones o espacios.", Alert.AlertType.WARNING);
        return;
    }

    String sql = "INSERT INTO cliente (nombres, apellidos, telefono, correo, activo) VALUES (?, ?, ?, ?, ?)";

    try (Connection conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/gestion_citas", "postgres", "1234");
         PreparedStatement pstmt = conn.prepareStatement(sql)){
        pstmt.setString(1, nombres);
        pstmt.setString(2, apellidos);
        pstmt.setString(3, telefono);
        pstmt.setString(4, correo);
        pstmt.setBoolean(5, activo);

        pstmt.executeUpdate();
        mostrarAlerta ("Exito", "Cliente registrado correctamente.", Alert.AlertType.INFORMATION);
    }catch (SQLException e){
        mostrarAlerta("Error SQL", "No se pudo guardar el cliente, revise sus parametros. " + e.getMessage(), Alert.AlertType.ERROR);

    }
 }

 private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo){
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();

 }

}


