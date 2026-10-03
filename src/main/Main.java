package main;

import datos.ConexionBD;
import vista.VentanaPrincipal;

import javax.swing.*;
import java.sql.Connection;
import java.sql.SQLException;

public class Main {

    public static void main(String[] args) {

        // Probar conexión con MySQL
        try (Connection conexion = ConexionBD.conectar()) {

            System.out.println(
                    "Base de datos conectada correctamente."
            );

        } catch (SQLException e) {

            JOptionPane.showMessageDialog(
                    null,
                    "No se pudo conectar a la base de datos.\n"
                            + e.getMessage(),
                    "Error de conexión",
                    JOptionPane.ERROR_MESSAGE
            );

            return;
        }

        // Abrir la ventana principal de SpeedFast
        SwingUtilities.invokeLater(() -> {
            new VentanaPrincipal().setVisible(true);
        });
    }
}