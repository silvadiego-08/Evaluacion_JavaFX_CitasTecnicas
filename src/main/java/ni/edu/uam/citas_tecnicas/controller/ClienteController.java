package ni.edu.uam.citas_tecnicas.controller;

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
        colActivo.setCellValueFactory(new PropertyValueFactory<>("activo"));

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

    try (Connection conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/gestion_citas", "postgres", "1234");
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

    if (Validador.esVacio(nombres) || Validador.esVacio(apellidos) || Validador.esVacio(telefono) || Validador.esVacio(correo)){
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

        cargarClientes();
        handleLimpiar();

    }catch (SQLException e){
        mostrarAlerta("Error SQL", "No se pudo guardar el cliente, revise sus parametros. " + e.getMessage(), Alert.AlertType.ERROR);

    }
 }


 @FXML
 private void handleActualizar(){
        Cliente seleccionado = tableClientes.getSelectionModel().getSelectedItem();
        if (seleccionado == null){
            mostrarAlerta("Se necesita campo", "Por favor seleccione un cliente de la tabla para actualizar", Alert.AlertType.WARNING);
            return;
        }

        String nombres = txtNombres.getText();
        String apellidos = txtApellidos.getText();
        String telefono = txtTelefono.getText();
        String correo = txtCorreo.getText();
        boolean activo  = chkActivo.isSelected();

     if (Validador.esVacio(nombres) || Validador.esVacio(apellidos) || Validador.esVacio(telefono) || Validador.esVacio(correo)) {
         mostrarAlerta("Campos Vacios", "Hay campos vacios en el formulario.", Alert.AlertType.WARNING);
         return;
     }
     String sql = "UPDATE cliente SET nombres = ?, apellidos = ?, telefono = ?, correo = ?, activo = ? WHERE id = ?";
     try (Connection conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/gestion_citas", "postgres", "1234");
          PreparedStatement pstmt = conn.prepareStatement(sql)) {
         pstmt.setString(1, nombres);
         pstmt.setString(2, apellidos);
         pstmt.setString(3, telefono);
         pstmt.setString(4, correo);
         pstmt.setBoolean(5, activo);
         pstmt.setInt(6, seleccionado.getId());

         pstmt.executeUpdate();
         mostrarAlerta("Exito", "Cliente actualizado correctamente.", Alert.AlertType.INFORMATION);
         cargarClientes();
         handleLimpiar();
     }catch (SQLException e) {
         mostrarAlerta("Error SQL", "No se pudo actualizar el cliente:" + e.getMessage(), Alert.AlertType.ERROR);
     }

 }


  @FXML
  private void handleEliminar() {
        Cliente seleccionado = tableClientes.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarAlerta("Seleccion Requerida", "Por favor seleccione un cliente de la tabla para eliminar.", Alert.AlertType.WARNING);
            return;
        }
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION, "¿Está seguro de eliminar este cliente?", ButtonType.YES, ButtonType.NO);
        confirmacion.showAndWait();

        if (confirmacion.getResult() ==  ButtonType.YES) {
            String sql = "DELETE FROM cliente WHERE id = ?";

            try (Connection conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/gestion_citas", "postgres", "1234");
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {

                pstmt.setInt(1, seleccionado.getId());
                pstmt.executeUpdate();

                mostrarAlerta("Exito", "Cliente eliminado correctamente", Alert.AlertType.INFORMATION);
                cargarClientes();
                handleLimpiar();
            }catch (SQLException e){
                mostrarAlerta("Eliminacion Restringida", "No se puede eliminar al cliente porque tiene citas tecnicas asociadas.", Alert.AlertType.ERROR);
            }
      }
  }


   @FXML
  private void handleBuscar() {
        String filtro = txtBuscar.getText().trim();
        if (Validador.esVacio(filtro)) {
            cargarClientes();
            return;
        }

        listaClientes.clear();
        String sql = "SELECT id, nombres, apellidos, telefono, correo, activo FROM cliente WHERE LOWER(nombres) LIKE ? OR LOWER(apellidos) LIKE ? OR LOWER(correo) LIKE ?";

        try (Connection conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/gestion_citas", "postgres", "1234");
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            String wildcard = "%" + filtro.toLowerCase() + "%";
            pstmt.setString(1, wildcard);
            pstmt.setString(2, wildcard);
            pstmt.setString(3, wildcard);

            try (ResultSet rs = pstmt.executeQuery()) {
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
            }
            tableClientes.setItems(listaClientes);

        } catch (SQLException e) {
            mostrarAlerta("Error de Búsqueda", "Ocurrió un error al buscar: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }


  @FXML
  private void handleLimpiar(){
        txtNombres.clear();
        txtApellidos.clear();
        txtTelefono.clear();
        txtCorreo.clear();
        chkActivo.setSelected(false);
        txtBuscar.clear();
        tableClientes.getSelectionModel().clearSelection();
 }


  private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo){
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();


  }

}



