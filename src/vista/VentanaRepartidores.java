package vista;

import datos.RepartidorDAO;
import modelo.Repartidor;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class VentanaRepartidores extends JFrame {

    private JTextField campoNombre;
    private JTable tablaRepartidores;
    private DefaultTableModel modeloTabla;

    private int idRepartidorSeleccionado = -1;

    public VentanaRepartidores() {

        setTitle("SpeedFast - Gestión de Repartidores");
        setSize(600, 450);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        setLayout(new BorderLayout(10, 10));

        // ---------------- FORMULARIO ----------------

        JPanel panelFormulario = new JPanel(
                new GridLayout(1, 2, 10, 10)
        );

        panelFormulario.setBorder(
                BorderFactory.createEmptyBorder(15, 15, 10, 15)
        );

        panelFormulario.add(new JLabel("Nombre:"));

        campoNombre = new JTextField();
        panelFormulario.add(campoNombre);

        add(panelFormulario, BorderLayout.NORTH);

        // ---------------- TABLA ----------------

        modeloTabla = new DefaultTableModel(
                new Object[]{
                        "ID",
                        "Nombre"
                },
                0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tablaRepartidores = new JTable(modeloTabla);

        tablaRepartidores.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );

        add(
                new JScrollPane(tablaRepartidores),
                BorderLayout.CENTER
        );

        // ---------------- BOTONES ----------------

        JPanel panelBotones = new JPanel();

        JButton botonCrear = new JButton("Crear");
        JButton botonActualizar = new JButton("Actualizar");
        JButton botonEliminar = new JButton("Eliminar");
        JButton botonLimpiar = new JButton("Limpiar");

        panelBotones.add(botonCrear);
        panelBotones.add(botonActualizar);
        panelBotones.add(botonEliminar);
        panelBotones.add(botonLimpiar);

        add(panelBotones, BorderLayout.SOUTH);

        // ---------------- EVENTOS ----------------

        botonCrear.addActionListener(
                e -> crearRepartidor()
        );

        botonActualizar.addActionListener(
                e -> actualizarRepartidor()
        );

        botonEliminar.addActionListener(
                e -> eliminarRepartidor()
        );

        botonLimpiar.addActionListener(
                e -> limpiarFormulario()
        );

        tablaRepartidores.getSelectionModel()
                .addListSelectionListener(e -> {

                    if (!e.getValueIsAdjusting()) {
                        cargarRepartidorSeleccionado();
                    }
                });

        // Cargar registros al abrir
        cargarRepartidores();
    }

    private void cargarRepartidores() {

        modeloTabla.setRowCount(0);

        try {

            List<Repartidor> repartidores =
                    RepartidorDAO.readAll();

            for (Repartidor repartidor : repartidores) {

                modeloTabla.addRow(
                        new Object[]{
                                repartidor.getId(),
                                repartidor.getNombre()
                        }
                );
            }

        } catch (SQLException e) {

            mostrarError(
                    "Error al cargar los repartidores:\n"
                            + e.getMessage()
            );
        }
    }

    private void crearRepartidor() {

        String nombre = campoNombre.getText().trim();

        if (nombre.isEmpty()) {

            mostrarError(
                    "Debe ingresar el nombre del repartidor."
            );

            return;
        }

        try {

            Repartidor repartidor =
                    new Repartidor(nombre);

            if (RepartidorDAO.create(repartidor)) {

                JOptionPane.showMessageDialog(
                        this,
                        "Repartidor registrado correctamente.\n"
                                + "ID generado: "
                                + repartidor.getId()
                );

                cargarRepartidores();
                limpiarFormulario();

            } else {

                mostrarError(
                        "No se pudo registrar el repartidor."
                );
            }

        } catch (SQLException e) {

            mostrarError(
                    "Error al registrar el repartidor:\n"
                            + e.getMessage()
            );
        }
    }

    private void actualizarRepartidor() {

        if (idRepartidorSeleccionado == -1) {

            mostrarError(
                    "Debe seleccionar un repartidor de la tabla."
            );

            return;
        }

        String nombre = campoNombre.getText().trim();

        if (nombre.isEmpty()) {

            mostrarError(
                    "Debe ingresar el nombre del repartidor."
            );

            return;
        }

        try {

            Repartidor repartidor = new Repartidor(
                    idRepartidorSeleccionado,
                    nombre
            );

            if (RepartidorDAO.update(repartidor)) {

                JOptionPane.showMessageDialog(
                        this,
                        "Repartidor actualizado correctamente."
                );

                cargarRepartidores();
                limpiarFormulario();

            } else {

                mostrarError(
                        "No se pudo actualizar el repartidor."
                );
            }

        } catch (SQLException e) {

            mostrarError(
                    "Error al actualizar el repartidor:\n"
                            + e.getMessage()
            );
        }
    }

    private void eliminarRepartidor() {

        if (idRepartidorSeleccionado == -1) {

            mostrarError(
                    "Debe seleccionar un repartidor de la tabla."
            );

            return;
        }

        int respuesta = JOptionPane.showConfirmDialog(
                this,
                "¿Desea eliminar el repartidor seleccionado?",
                "Confirmar eliminación",
                JOptionPane.YES_NO_OPTION
        );

        if (respuesta != JOptionPane.YES_OPTION) {
            return;
        }

        try {

            if (RepartidorDAO.delete(
                    idRepartidorSeleccionado)) {

                JOptionPane.showMessageDialog(
                        this,
                        "Repartidor eliminado correctamente."
                );

                cargarRepartidores();
                limpiarFormulario();

            } else {

                mostrarError(
                        "No se pudo eliminar el repartidor."
                );
            }

        } catch (SQLException e) {

            mostrarError(
                    "No se pudo eliminar el repartidor.\n"
                            + "Puede tener una entrega asociada.\n\n"
                            + e.getMessage()
            );
        }
    }

    private void cargarRepartidorSeleccionado() {

        int fila = tablaRepartidores.getSelectedRow();

        if (fila == -1) {
            return;
        }

        idRepartidorSeleccionado =
                (int) modeloTabla.getValueAt(fila, 0);

        campoNombre.setText(
                modeloTabla.getValueAt(fila, 1).toString()
        );
    }

    private void limpiarFormulario() {

        idRepartidorSeleccionado = -1;

        tablaRepartidores.clearSelection();

        campoNombre.setText("");
    }

    private void mostrarError(String mensaje) {

        JOptionPane.showMessageDialog(
                this,
                mensaje,
                "Error",
                JOptionPane.ERROR_MESSAGE
        );
    }
}