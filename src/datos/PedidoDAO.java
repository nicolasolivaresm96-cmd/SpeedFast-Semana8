package datos;

import modelo.Pedido;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class PedidoDAO {

    /**
     * Registra un nuevo pedido en la base de datos.
     */
    public static boolean create(Pedido pedido) throws SQLException {

        String sql =
                "INSERT INTO pedidos (direccion, tipo, estado) VALUES (?, ?, ?)";

        try (Connection conexion = ConexionBD.conectar();
             PreparedStatement sentencia = conexion.prepareStatement(
                     sql, Statement.RETURN_GENERATED_KEYS)) {

            sentencia.setString(1, pedido.getDireccion());
            sentencia.setString(2, pedido.getTipo());
            sentencia.setString(3, pedido.getEstado());

            int filasAfectadas = sentencia.executeUpdate();

            if (filasAfectadas > 0) {

                try (ResultSet claves = sentencia.getGeneratedKeys()) {

                    if (claves.next()) {
                        pedido.setId(claves.getInt(1));
                    }
                }

                return true;
            }

            return false;
        }
    }

    /**
     * Obtiene todos los pedidos registrados.
     */
    public static List<Pedido> readAll() throws SQLException {

        List<Pedido> pedidos = new ArrayList<>();

        String sql =
                "SELECT id, direccion, tipo, estado FROM pedidos ORDER BY id";

        try (Connection conexion = ConexionBD.conectar();
             PreparedStatement sentencia = conexion.prepareStatement(sql);
             ResultSet resultado = sentencia.executeQuery()) {

            while (resultado.next()) {

                int id = resultado.getInt("id");
                String direccion = resultado.getString("direccion");
                String tipo = resultado.getString("tipo");
                String estado = resultado.getString("estado");

                Pedido pedido =
                        new Pedido(id, direccion, tipo, estado);

                pedidos.add(pedido);
            }
        }

        return pedidos;
    }

    /**
     * Actualiza un pedido existente.
     */
    public static boolean update(Pedido pedido) throws SQLException {

        String sql =
                "UPDATE pedidos SET direccion = ?, tipo = ?, estado = ? WHERE id = ?";

        try (Connection conexion = ConexionBD.conectar();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {

            sentencia.setString(1, pedido.getDireccion());
            sentencia.setString(2, pedido.getTipo());
            sentencia.setString(3, pedido.getEstado());
            sentencia.setInt(4, pedido.getId());

            return sentencia.executeUpdate() > 0;
        }
    }

    /**
     * Elimina un pedido según su ID.
     */
    public static boolean delete(int id) throws SQLException {

        String sql = "DELETE FROM pedidos WHERE id = ?";

        try (Connection conexion = ConexionBD.conectar();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {

            sentencia.setInt(1, id);

            return sentencia.executeUpdate() > 0;
        }
    }
}