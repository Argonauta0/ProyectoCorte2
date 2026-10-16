package com.desarrollo.proyectocorteii.DAO;

import com.desarrollo.proyectocorteii.Model.Producto;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProductoDAO {

    // Lista los productos aplicando los tres filtros a la vez (AND)
    public List<Producto> listar(String texto, String categoria, boolean soloExistencias) throws SQLException {
        List<Producto> lista = new ArrayList<>();

        String sql = "SELECT id_producto, codigo, nombre, categoria, precio, stock FROM producto " +
                "WHERE (nombre ILIKE ? OR codigo ILIKE ?) " +
                "AND (? = 'Todas' OR categoria = ?) " +
                "AND (? = false OR stock > 0) " +
                "ORDER BY codigo ASC";

        try (
                Connection connection = ConexionBD.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            String patron = "%" + texto.trim() + "%";
            statement.setString(1, patron);
            statement.setString(2, patron);
            statement.setString(3, categoria);
            statement.setString(4, categoria);
            statement.setBoolean(5, soloExistencias);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Producto producto = new Producto();
                    producto.setIdProducto(resultSet.getInt("id_producto"));
                    producto.setCodigo(resultSet.getString("codigo"));
                    producto.setNombre(resultSet.getString("nombre"));
                    producto.setCategoria(resultSet.getString("categoria"));
                    producto.setPrecio(resultSet.getBigDecimal("precio"));
                    producto.setStock(resultSet.getInt("stock"));
                    lista.add(producto);
                }
            }
        } catch (SQLException ex) {
            registrarError("LISTAR", ex);
            throw ex;
        }
        return lista;
    }

    // Devuelve el ID del producto que tiene ese código, o null si nadie lo usa (para V9)
    public Integer buscarIdPorCodigo(String codigo) throws SQLException {
        String sql = "SELECT id_producto FROM producto WHERE codigo = ?";

        try (
                Connection connection = ConexionBD.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setString(1, codigo);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("id_producto");
                }
                return null;
            }
        } catch (SQLException ex) {
            registrarError("BUSCAR_CODIGO", ex);
            throw ex;
        }
    }

    public void insertar(Producto producto) throws SQLException {
        String sql = "INSERT INTO producto (codigo, nombre, categoria, precio, stock) VALUES (?, ?, ?, ?, ?)";

        try (
                Connection connection = ConexionBD.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setString(1, producto.getCodigo());
            statement.setString(2, producto.getNombre());
            statement.setString(3, producto.getCategoria());
            statement.setBigDecimal(4, producto.getPrecio());
            statement.setInt(5, producto.getStock());
            statement.executeUpdate();
        } catch (SQLException ex) {
            registrarError("INSERTAR", ex);
            throw ex;
        }
    }

    // Devuelve cuántas filas se actualizaron (0 = el producto ya no existe)
    public int actualizar(Producto producto) throws SQLException {
        String sql = "UPDATE producto SET codigo = ?, nombre = ?, categoria = ?, precio = ?, stock = ? " +
                "WHERE id_producto = ?";

        try (
                Connection connection = ConexionBD.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setString(1, producto.getCodigo());
            statement.setString(2, producto.getNombre());
            statement.setString(3, producto.getCategoria());
            statement.setBigDecimal(4, producto.getPrecio());
            statement.setInt(5, producto.getStock());
            statement.setInt(6, producto.getIdProducto());
            return statement.executeUpdate();
        } catch (SQLException ex) {
            registrarError("ACTUALIZAR", ex);
            throw ex;
        }
    }

    // Devuelve cuántas filas se eliminaron (0 = el producto ya no existe)
    public int eliminar(int idProducto) throws SQLException {
        String sql = "DELETE FROM producto WHERE id_producto = ?";

        try (
                Connection connection = ConexionBD.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setInt(1, idProducto);
            return statement.executeUpdate();
        } catch (SQLException ex) {
            registrarError("ELIMINAR", ex);
            throw ex;
        }
    }

    // E4: registra en consola la operación y el SQLState, sin contraseñas
    private void registrarError(String operacion, SQLException ex) {
        System.err.println("[ERROR BD] Operación: " + operacion + " | SQLState: " + ex.getSQLState());
    }
}