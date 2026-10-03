package vista;

import javax.swing.*;
import java.awt.*;

public class VentanaPrincipal extends JFrame {

    public VentanaPrincipal() {

        setTitle("SpeedFast - Sistema de Gestión");
        setSize(450, 350);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel(
                new GridLayout(4, 1, 10, 10)
        );

        panel.setBorder(
                BorderFactory.createEmptyBorder(30, 40, 30, 40)
        );

        JButton botonPedidos =
                new JButton("Gestión de Pedidos");

        JButton botonRepartidores =
                new JButton("Gestión de Repartidores");

        JButton botonEntregas =
                new JButton("Gestión de Entregas");

        JButton botonSalir =
                new JButton("Salir");

        panel.add(botonPedidos);
        panel.add(botonRepartidores);
        panel.add(botonEntregas);
        panel.add(botonSalir);

        add(panel);

        botonPedidos.addActionListener(e ->
                new VentanaRegistroPedido().setVisible(true)
        );

        botonRepartidores.addActionListener(e ->
                new VentanaRepartidores().setVisible(true)
        );

        botonEntregas.addActionListener(e ->
                new VentanaEntrega().setVisible(true)
        );

        botonSalir.addActionListener(e ->
                System.exit(0)
        );
    }
}