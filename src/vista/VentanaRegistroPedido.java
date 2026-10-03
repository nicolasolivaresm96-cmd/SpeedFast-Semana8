package vista;

import datos.PedidoDAO;
import modelo.Pedido;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class VentanaRegistroPedido extends JFrame {

    private JTextField campoDireccion;
    private JComboBox<String> comboTipo;
    private JComboBox<String> comboEstado;

    private JTable tablaPedidos;
    private DefaultTableModel modeloTabla;

    private int idPedidoSeleccionado = -1;

    public VentanaRegistroPedido() {

        setTitle("SpeedFast - Gestión de Pedidos");
        setSize(750, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        setLayout(new BorderLayout(10, 10));

        // ---------------- FORMULARIO ----------------

        JPanel panelFormulario = new JPanel(
                new GridLayout(3, 2, 10, 10)
        );

        panelFormulario.setBorder(
                BorderFactory.createEmptyBorder(15, 15, 10, 15)
        );

        panelFormulario.add(new JLabel("Dirección:"));

        campoDireccion = new JTextField();
        panelFormulario.add(campoDireccion);

        panelFormulario.add(new JLabel("Tipo:"));

        comboTipo = new JComboBox<>(
                new String[]{
                        "COMIDA",
                        "ENCOMIENDA",
                        "EXPRESS"
                }
        );

        panelFormulario.add(comboTipo);

        panelFormulario.add(new JLabel("Estado:"));

        comboEstado = new JComboBox<>(
                new String[]{
                        "PENDIENTE",
                        "EN_REPARTO",
                        "ENTREGADO"
                }
        );

        panelFormulario.add(comboEstado);

        add(panelFormulario, BorderLayout.NORTH);

        // ---------------- TABLA ----------------

        modeloTabla = new DefaultTableModel(
                new Object[]{
                        "ID",
                        "Dirección",
                        "Tipo",
                        "Estado"
                },
                0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tablaPedidos = new JTable(modeloTabla);

        tablaPedidos.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );

        add(
                new JScrollPane(tablaPedidos),
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
                e -> crearPedido()
        );

        botonActualizar.addActionListener(
                e -> actualizarPedido()
        );

        botonEliminar.addActionListener(
                e -> eliminarPedido()
        );

        botonLimpiar.addActionListener(
                e -> limpiarFormulario()
        );

        tablaPedidos.getSelectionModel()
                .addListSelectionListener(e -> {

                    if (!e.getValueIsAdjusting()) {
                        cargarPedidoSeleccionado();
                    }
                });

        // Cargar registros al abrir la ventana
        cargarPedidos();
    }

    private void cargarPedidos() {

        modeloTabla.setRowCount(0);

        try {

            List<Pedido> pedidos = PedidoDAO.readAll();

            for (Pedido pedido : pedidos) {

                modeloTabla.addRow(
                        new Object[]{
                                pedido.getId(),
                                pedido.getDireccion(),
                                pedido.getTipo(),
                                pedido.getEstado()
                        }
                );
            }

        } catch (SQLException e) {

            mostrarError(
                    "Error al cargar los pedidos:\n"
                            + e.getMessage()
            );
        }
    }

    private void crearPedido() {

        String direccion =
                campoDireccion.getText().trim();

        String tipo =
                (String) comboTipo.getSelectedItem();

        String estado =
                (String) comboEstado.getSelectedItem();

        if (direccion.isEmpty()) {

            mostrarError(
                    "Debe ingresar una dirección."
            );

            return;
        }

        try {

            Pedido pedido = new Pedido(
                    direccion,
                    tipo,
                    estado
            );

            if (PedidoDAO.create(pedido)) {

                JOptionPane.showMessageDialog(
                        this,
                        "Pedido registrado correctamente.\n"
                                + "ID generado: "
                                + pedido.getId()
                );

                cargarPedidos();
                limpiarFormulario();

            } else {

                mostrarError(
                        "No se pudo registrar el pedido."
                );
            }

        } catch (SQLException e) {

            mostrarError(
                    "Error al registrar el pedido:\n"
                            + e.getMessage()
            );
        }
    }

    private void actualizarPedido() {

        if (idPedidoSeleccionado == -1) {

            mostrarError(
                    "Debe seleccionar un pedido de la tabla."
            );

            return;
        }

        String direccion =
                campoDireccion.getText().trim();

        String tipo =
                (String) comboTipo.getSelectedItem();

        String estado =
                (String) comboEstado.getSelectedItem();

        if (direccion.isEmpty()) {

            mostrarError(
                    "Debe ingresar una dirección."
            );

            return;
        }

        try {

            Pedido pedido = new Pedido(
                    idPedidoSeleccionado,
                    direccion,
                    tipo,
                    estado
            );

            if (PedidoDAO.update(pedido)) {

                JOptionPane.showMessageDialog(
                        this,
                        "Pedido actualizado correctamente."
                );

                cargarPedidos();
                limpiarFormulario();

            } else {

                mostrarError(
                        "No se pudo actualizar el pedido."
                );
            }

        } catch (SQLException e) {

            mostrarError(
                    "Error al actualizar el pedido:\n"
                            + e.getMessage()
            );
        }
    }

    private void eliminarPedido() {

        if (idPedidoSeleccionado == -1) {

            mostrarError(
                    "Debe seleccionar un pedido de la tabla."
            );

            return;
        }

        int respuesta = JOptionPane.showConfirmDialog(
                this,
                "¿Desea eliminar el pedido seleccionado?",
                "Confirmar eliminación",
                JOptionPane.YES_NO_OPTION
        );

        if (respuesta != JOptionPane.YES_OPTION) {
            return;
        }

        try {

            if (PedidoDAO.delete(idPedidoSeleccionado)) {

                JOptionPane.showMessageDialog(
                        this,
                        "Pedido eliminado correctamente."
                );

                cargarPedidos();
                limpiarFormulario();

            } else {

                mostrarError(
                        "No se pudo eliminar el pedido."
                );
            }

        } catch (SQLException e) {

            mostrarError(
                    "No se pudo eliminar el pedido.\n"
                            + "Puede tener una entrega asociada.\n\n"
                            + e.getMessage()
            );
        }
    }

    private void cargarPedidoSeleccionado() {

        int fila = tablaPedidos.getSelectedRow();

        if (fila == -1) {
            return;
        }

        idPedidoSeleccionado =
                (int) modeloTabla.getValueAt(fila, 0);

        campoDireccion.setText(
                modeloTabla.getValueAt(fila, 1).toString()
        );

        comboTipo.setSelectedItem(
                modeloTabla.getValueAt(fila, 2).toString()
        );

        comboEstado.setSelectedItem(
                modeloTabla.getValueAt(fila, 3).toString()
        );
    }

    private void limpiarFormulario() {

        idPedidoSeleccionado = -1;

        tablaPedidos.clearSelection();

        campoDireccion.setText("");
        comboTipo.setSelectedIndex(0);
        comboEstado.setSelectedIndex(0);
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