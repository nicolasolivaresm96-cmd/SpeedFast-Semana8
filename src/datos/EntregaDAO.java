package datos;

import modelo.Entrega;
import modelo.Pedido;
import modelo.Repartidor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class EntregaDAO {

    /**
     * Registra una nueva entrega en la base de datos.
     */
    public static boolean create(Entrega entrega) throws SQLException {

        String sql = "INSERT INTO entregas " +
                "(id_pedido, id_repartidor, fecha, hora) " +
                "VALUES (?, ?, ?, ?)";

        try (Connection conexion = ConexionBD.conectar();
             PreparedStatement sentencia = conexion.prepareStatement(
                     sql, Statement.RETURN_GENERATED_KEYS)) {

            sentencia.setInt(1, entrega.getPedido().getId());
            sentencia.setInt(2, entrega.getRepartidor().getId());
            sentencia.setDate(
                    3,
                    java.sql.Date.valueOf(entrega.getFecha())
            );
            sentencia.setTime(
                    4,
                    java.sql.Time.valueOf(entrega.getHora())
            );

            int filasAfectadas = sentencia.executeUpdate();

            if (filasAfectadas > 0) {

                try (ResultSet claves = sentencia.getGeneratedKeys()) {

                    if (claves.next()) {
                        entrega.setId(claves.getInt(1));
                    }
                }

                return true;
            }

            return false;
        }
    }

    /**
     * Obtiene todas las entregas registradas.
     */
    public static List<Entrega> readAll() throws SQLException {

        List<Entrega> entregas = new ArrayList<>();

        String sql =
                "SELECT e.id, e.fecha, e.hora, " +
                        "p.id AS pedido_id, p.direccion, p.tipo, p.estado, " +
                        "r.id AS repartidor_id, r.nombre " +
                        "FROM entregas e " +
                        "INNER JOIN pedidos p ON e.id_pedido = p.id " +
                        "INNER JOIN repartidores r ON e.id_repartidor = r.id " +
                        "ORDER BY e.id";

        try (Connection conexion = ConexionBD.conectar();
             PreparedStatement sentencia = conexion.prepareStatement(sql);
             ResultSet resultado = sentencia.executeQuery()) {

            while (resultado.next()) {

                Pedido pedido = new Pedido(
                        resultado.getInt("pedido_id"),
                        resultado.getString("direccion"),
                        resultado.getString("tipo"),
                        resultado.getString("estado")
                );

                Repartidor repartidor = new Repartidor(
                        resultado.getInt("repartidor_id"),
                        resultado.getString("nombre")
                );

                Entrega entrega = new Entrega(
                        resultado.getInt("id"),
                        pedido,
                        repartidor,
                        resultado.getDate("fecha").toLocalDate(),
                        resultado.getTime("hora").toLocalTime()
                );

                entregas.add(entrega);
            }
        }

        return entregas;
    }

    /**
     * Actualiza una entrega existente.
     */
    public static boolean update(Entrega entrega) throws SQLException {

        String sql =
                "UPDATE entregas " +
                        "SET id_pedido = ?, id_repartidor = ?, fecha = ?, hora = ? " +
                        "WHERE id = ?";

        try (Connection conexion = ConexionBD.conectar();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {

            sentencia.setInt(1, entrega.getPedido().getId());
            sentencia.setInt(2, entrega.getRepartidor().getId());
            sentencia.setDate(
                    3,
                    java.sql.Date.valueOf(entrega.getFecha())
            );
            sentencia.setTime(
                    4,
                    java.sql.Time.valueOf(entrega.getHora())
            );
            sentencia.setInt(5, entrega.getId());

            return sentencia.executeUpdate() > 0;
        }
    }

    /**
     * Elimina una entrega según su ID.
     */
    public static boolean delete(int id) throws SQLException {

        String sql = "DELETE FROM entregas WHERE id = ?";

        try (Connection conexion = ConexionBD.conectar();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {

            sentencia.setInt(1, id);

            return sentencia.executeUpdate() > 0;
        }
    }
}