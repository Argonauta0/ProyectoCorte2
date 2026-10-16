package com.desarrollo.proyectocorteii.Controller;

import com.desarrollo.proyectocorteii.DAO.ProductoDAO;
import com.desarrollo.proyectocorteii.Model.Producto;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class ProductoController {

    // ---------- Filtros ----------
    @FXML
    private TextField txtBuscar;
    @FXML
    private ComboBox<String> cmbFiltroCategoria;
    @FXML
    private CheckBox chkSoloExistencias;

    // ---------- Tabla ----------
    @FXML
    private TableView<Producto> tablaProductos;
    @FXML
    private TableColumn<Producto, Integer> colId;
    @FXML
    private TableColumn<Producto, String> colCodigo;
    @FXML
    private TableColumn<Producto, String> colNombre;
    @FXML
    private TableColumn<Producto, String> colCategoria;
    @FXML
    private TableColumn<Producto, BigDecimal> colPrecio;
    @FXML
    private TableColumn<Producto, Integer> colStock;
    @FXML
    private Label lblContador;
    @FXML
    private Label lblEstado;

    // ---------- Formulario ----------
    @FXML
    private TextField txtId;
    @FXML
    private TextField txtCodigo;
    @FXML
    private TextField txtNombre;
    @FXML
    private ComboBox<String> cmbCategoria;
    @FXML
    private TextField txtPrecio;
    @FXML
    private TextField txtStock;

    private final ProductoDAO dao = new ProductoDAO();

    // Guarda el ID de la fila seleccionada (el usuario no lo puede editar)
    private Integer idSeleccionado = null;

    private static final BigDecimal PRECIO_MAXIMO = new BigDecimal("99999999.99");

    @FXML
    public void initialize() {
        configurarTabla();
        configurarCombobox();
        cargarProductos();

        // Al seleccionar una fila se cargan sus datos en el formulario
        tablaProductos.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    if (newValue != null) {
                        cargarProductoSeleccionado(newValue);
                    }
                });
    }

    private void configurarTabla() {
        colId.setCellValueFactory(new PropertyValueFactory<>("idProducto"));
        colCodigo.setCellValueFactory(new PropertyValueFactory<>("codigo"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colCategoria.setCellValueFactory(new PropertyValueFactory<>("categoria"));
        colPrecio.setCellValueFactory(new PropertyValueFactory<>("precio"));
        colStock.setCellValueFactory(new PropertyValueFactory<>("stock"));
    }

    private void configurarCombobox() {
        cmbFiltroCategoria.getItems().clear();
        cmbFiltroCategoria.getItems().addAll("Todas", "Papelería", "Tecnología", "Accesorios");
        cmbFiltroCategoria.setValue("Todas");

        cmbCategoria.getItems().clear();
        cmbCategoria.getItems().addAll("Papelería", "Tecnología", "Accesorios");
    }

    private void cargarProductoSeleccionado(Producto producto) {
        idSeleccionado = producto.getIdProducto();
        txtId.setText(String.valueOf(producto.getIdProducto()));
        txtCodigo.setText(producto.getCodigo());
        txtNombre.setText(producto.getNombre());
        cmbCategoria.setValue(producto.getCategoria());
        txtPrecio.setText(producto.getPrecio().toPlainString());
        txtStock.setText(String.valueOf(producto.getStock()));
    }

    // Consulta PostgreSQL con los filtros que estén puestos en ese momento
    private void cargarProductos() {
        try {
            List<Producto> lista = dao.listar(
                    txtBuscar.getText(),
                    cmbFiltroCategoria.getValue(),
                    chkSoloExistencias.isSelected());

            tablaProductos.setItems(FXCollections.observableArrayList(lista));
            lblContador.setText("Registros visibles: " + lista.size());

            if (lista.isEmpty()) {
                lblEstado.setText("Sin coincidencias");
            } else {
                lblEstado.setText("");
            }
        } catch (SQLException ex) {
            // E1: no se presenta como si fueran 0 resultados
            tablaProductos.getItems().clear();
            lblContador.setText("Registros visibles: —");
            lblEstado.setText("Error de conexión");
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se pudo cargar el listado", mensajeError(ex));
        }
    }

    // ---------- Botones de filtros ----------
    @FXML
    private void onBuscarClick() {
        cargarProductos();
    }

    @FXML
    private void onLimpiarFiltrosClick() {
        txtBuscar.clear();
        cmbFiltroCategoria.setValue("Todas");
        chkSoloExistencias.setSelected(false);
        cargarProductos();
    }

    @FXML
    private void onActualizarTablaClick() {
        cargarProductos();
    }

    // ---------- Botones del formulario ----------
    @FXML
    private void onLimpiarClick() {
        idSeleccionado = null;
        txtId.clear();
        txtCodigo.clear();
        txtNombre.clear();
        cmbCategoria.getSelectionModel().clearSelection();
        txtPrecio.clear();
        txtStock.clear();
        tablaProductos.getSelectionModel().clearSelection();
    }

    @FXML
    private void onGuardarClick() {
        Producto producto = leerFormulario();
        if (producto == null) {
            return;
        }

        try {
            // V9: el código no puede usarlo otro producto
            if (codigoDuplicado(producto.getCodigo(), null)) {
                return;
            }

            dao.insertar(producto);

            mostrarAlerta(Alert.AlertType.INFORMATION, "Registro almacenado",
                    "Producto guardado", "El producto se guardó correctamente.");
            onLimpiarClick();
            cargarProductos();

        } catch (SQLException ex) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se pudo guardar", mensajeError(ex));
        }
    }

    @FXML
    private void onActualizarClick() {
        // V10
        if (idSeleccionado == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Selección requerida",
                    "No hay un producto seleccionado", "Seleccione un producto de la tabla.");
            return;
        }

        Producto producto = leerFormulario();
        if (producto == null) {
            return;
        }
        producto.setIdProducto(idSeleccionado);

        try {
            // V9: permite conservar su propio código, pero no usar el de otro
            if (codigoDuplicado(producto.getCodigo(), idSeleccionado)) {
                return;
            }

            int filasActualizadas = dao.actualizar(producto);

            if (filasActualizadas > 0) {
                mostrarAlerta(Alert.AlertType.INFORMATION, "Registro actualizado",
                        "Actualización completada", "El producto se actualizó correctamente.");
            } else {
                // E3: el producto fue borrado desde fuera de la aplicación
                mostrarAlerta(Alert.AlertType.WARNING, "Registro no encontrado",
                        "No se actualizó ningún producto",
                        "El producto ya no existe en la base de datos. La tabla se recargará.");
            }
            onLimpiarClick();
            cargarProductos();

        } catch (SQLException ex) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se pudo actualizar", mensajeError(ex));
        }
    }

    @FXML
    private void onEliminarClick() {
        // V10
        if (idSeleccionado == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Selección requerida",
                    "No hay un producto seleccionado", "Seleccione un producto de la tabla.");
            return;
        }

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmación");
        confirmacion.setHeaderText(null);
        confirmacion.setContentText("¿Está seguro de que desea eliminar el producto " + txtCodigo.getText() + "?");

        Optional<ButtonType> respuesta = confirmacion.showAndWait();
        if (respuesta.isEmpty() || respuesta.get() != ButtonType.OK) {
            return; // Canceló: el registro se conserva
        }

        try {
            int filasEliminadas = dao.eliminar(idSeleccionado);

            if (filasEliminadas > 0) {
                mostrarAlerta(Alert.AlertType.INFORMATION, "Registro eliminado",
                        "Eliminación completada", "El producto se eliminó correctamente.");
            } else {
                // E3
                mostrarAlerta(Alert.AlertType.WARNING, "Registro no encontrado",
                        "No se eliminó ningún producto",
                        "El producto ya no existe en la base de datos. La tabla se recargará.");
            }
            onLimpiarClick();
            cargarProductos();

        } catch (SQLException ex) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se pudo eliminar", mensajeError(ex));
        }
    }

    // ---------- Validaciones V1 a V8 ----------
    // Devuelve el producto ya normalizado, o null si algún dato es inválido
    private Producto leerFormulario() {
        String codigo = txtCodigo.getText().trim().toUpperCase();
        String nombre = txtNombre.getText().trim();
        String categoria = cmbCategoria.getValue();
        String textoPrecio = txtPrecio.getText().trim();
        String textoStock = txtStock.getText().trim();

        // V1
        if (codigo.isEmpty() || codigo.length() > 10) {
            mostrarAlerta(Alert.AlertType.WARNING, "Código inválido", "Valor incorrecto",
                    "El código debe tener de 1 a 10 caracteres.");
            return null;
        }

        // V2
        if (nombre.length() < 3 || nombre.length() > 80) {
            mostrarAlerta(Alert.AlertType.WARNING, "Nombre inválido", "Valor incorrecto",
                    "El nombre debe tener de 3 a 80 caracteres.");
            return null;
        }

        // V3
        if (categoria == null || !cmbCategoria.getItems().contains(categoria)) {
            mostrarAlerta(Alert.AlertType.WARNING, "Categoría inválida", "Valor incorrecto",
                    "Seleccione una categoría: Papelería, Tecnología o Accesorios.");
            return null;
        }

        // V4: precio numérico
        BigDecimal precio;
        try {
            precio = new BigDecimal(textoPrecio);
        } catch (NumberFormatException ex) {
            mostrarAlerta(Alert.AlertType.WARNING, "Precio inválido", "Valor incorrecto",
                    "El precio debe ser un número. Use punto como separador decimal (ej. 65.00).");
            return null;
        }

        // V5: mayor que 0
        if (precio.compareTo(BigDecimal.ZERO) <= 0) {
            mostrarAlerta(Alert.AlertType.WARNING, "Precio inválido", "Valor incorrecto",
                    "El precio debe ser mayor que 0.");
            return null;
        }

        // V6: máximo 2 decimales (sin redondear) y dentro del rango
        if (precio.stripTrailingZeros().scale() > 2) {
            mostrarAlerta(Alert.AlertType.WARNING, "Precio inválido", "Valor incorrecto",
                    "El precio no puede tener más de 2 decimales.");
            return null;
        }
        if (precio.compareTo(PRECIO_MAXIMO) > 0) {
            mostrarAlerta(Alert.AlertType.WARNING, "Precio inválido", "Valor incorrecto",
                    "El precio máximo permitido es 99999999.99.");
            return null;
        }

        // V7 y V8: entero de 0 a 2147483647
        int stock;
        try {
            stock = Integer.parseInt(textoStock); // rechaza "abc", "2.5" y "2147483648"
        } catch (NumberFormatException ex) {
            mostrarAlerta(Alert.AlertType.WARNING, "Stock inválido", "Valor incorrecto",
                    "El stock debe ser un número entero entre 0 y 2147483647.");
            return null;
        }
        if (stock < 0) {
            mostrarAlerta(Alert.AlertType.WARNING, "Stock inválido", "Valor incorrecto",
                    "El stock no puede ser negativo.");
            return null;
        }

        return new Producto(codigo, nombre, categoria, precio, stock);
    }

    // V9: true si el código ya lo usa OTRO producto (muestra la alerta)
    private boolean codigoDuplicado(String codigo, Integer idActual) throws SQLException {
        Integer idExistente = dao.buscarIdPorCodigo(codigo);

        if (idExistente != null && !idExistente.equals(idActual)) {
            mostrarAlerta(Alert.AlertType.WARNING, "Código duplicado", "Valor repetido",
                    "Ya existe otro producto con el código " + codigo + ".");
            return true;
        }
        return false;
    }

    // ---------- Mensajes ----------
    // Mensaje limpio para el usuario según el SQLState (E4)
    private String mensajeError(SQLException ex) {
        String estado = ex.getSQLState();

        if (estado == null || estado.startsWith("08")) {
            return "No se pudo conectar con la base de datos. Verifique que PostgreSQL esté encendido.";
        } else if (estado.startsWith("28")) {
            return "Usuario o contraseña de la base de datos incorrectos.";
        } else if (estado.equals("23505")) {
            return "Ya existe un producto con ese código.";
        } else if (estado.equals("23514")) {
            return "Los datos no cumplen las reglas de la base de datos.";
        } else {
            return "Ocurrió un error al acceder a la base de datos.";
        }
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String encabezado, String mensaje) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(encabezado);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}