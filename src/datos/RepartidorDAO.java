package datos;

import modelo.Repartidor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class RepartidorDAO {

    /**
     * Registra un nuevo repartidor en la base de datos.
     */
    public static boolean create(Repartidor repartidor) throws SQLException {

        String sql = "INSERT INTO repartidores (nombre) VALUES (?)";

        try (Connection conexion = ConexionBD.conectar();
             PreparedStatement sentencia = conexion.prepareStatement(
                     sql, Statement.RETURN_GENERATED_KEYS)) {

            sentencia.setString(1, repartidor.getNombre());

            int filasAfectadas = sentencia.executeUpdate();

            if (filasAfectadas > 0) {
                try (ResultSet claves = sentencia.getGeneratedKeys()) {
                    if (claves.next()) {
                        repartidor.setId(claves.getInt(1));
                    }
                }
                return true;
            }

            return false;
        }
    }

    /**
     * Obtiene todos los repartidores registrados.
     */
    public static List<Repartidor> readAll() throws SQLException {

        List<Repartidor> repartidores = new ArrayList<>();

        String sql = "SELECT id, nombre FROM repartidores ORDER BY id";

        try (Connection conexion = ConexionBD.conectar();
             PreparedStatement sentencia = conexion.prepareStatement(sql);
             ResultSet resultado = sentencia.executeQuery()) {

            while (resultado.next()) {

                int id = resultado.getInt("id");
                String nombre = resultado.getString("nombre");

                Repartidor repartidor = new Repartidor(id, nombre);
                repartidores.add(repartidor);
            }
        }

        return repartidores;
    }

    /**
     * Actualiza el nombre de un repartidor existente.
     */
    public static boolean update(Repartidor repartidor) throws SQLException {

        String sql = "UPDATE repartidores SET nombre = ? WHERE id = ?";

        try (Connection conexion = ConexionBD.conectar();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {

            sentencia.setString(1, repartidor.getNombre());
            sentencia.setInt(2, repartidor.getId());

            return sentencia.executeUpdate() > 0;
        }
    }

    /**
     * Elimina un repartidor según su ID.
     */
    public static boolean delete(int id) throws SQLException {

        String sql = "DELETE FROM repartidores WHERE id = ?";

        try (Connection conexion = ConexionBD.conectar();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {

            sentencia.setInt(1, id);

            return sentencia.executeUpdate() > 0;
        }
    }
}