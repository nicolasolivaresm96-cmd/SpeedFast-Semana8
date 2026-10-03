package datos;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionBD {

    private static final String URL =
            "jdbc:mysql://localhost:3306/speedfast_db";

    private static final String USUARIO = "root";

    /*
     * La contraseña de MySQL se obtiene desde una variable de entorno
     * para evitar guardar credenciales personales en el código fuente.
     */
    private static final String PASSWORD =
            System.getenv("SPEEDFAST_DB_PASSWORD");

    /**
     * Obtiene una conexión con la base de datos SpeedFast.
     *
     * @return conexión activa con MySQL
     * @throws SQLException si ocurre un error al conectar
     */
    public static Connection conectar() throws SQLException {

        if (PASSWORD == null || PASSWORD.isBlank()) {
            throw new SQLException(
                    "No se ha configurado la variable de entorno SPEEDFAST_DB_PASSWORD."
            );
        }

        return DriverManager.getConnection(URL, USUARIO, PASSWORD);
    }
}