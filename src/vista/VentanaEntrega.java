package vista;

import datos.EntregaDAO;
import datos.PedidoDAO;
import datos.RepartidorDAO;
import modelo.Entrega;
import modelo.Pedido;
import modelo.Repartidor;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.List;

public class VentanaEntrega extends JFrame {

    private JComboBox<Pedido> comboPedidos;
    private JComboBox<Repartidor> comboRepartidores;
    private JTextField campoFecha;
    private JTextField campoHora;
    private JTable tablaEntregas;
    private DefaultTableModel modeloTabla;

    private int idEntregaSeleccionada = -1;

    public VentanaEntrega() {

        setTitle("SpeedFast - Gestión de Entregas");
        setSize(750, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        setLayout(new BorderLayout(10, 10));

        // ---------------- FORMULARIO ----------------

        JPanel panelFormulario = new JPanel(
                new GridLayout(4, 2, 10, 10)
        );

        panelFormulario.setBorder(
                BorderFactory.createEmptyBorder(15, 15, 10, 15)
        );

        panelFormulario.add(new JLabel("Pedido:"));
        comboPedidos = new JComboBox<>();
        panelFormulario.add(comboPedidos);

        panelFormulario.add(new JLabel("Repartidor:"));
        comboRepartidores = new JComboBox<>();
        panelFormulario.add(comboRepartidores);

        panelFormulario.add(new JLabel("Fecha (AAAA-MM-DD):"));
        campoFecha = new JTextField(LocalDate.now().toString());
        panelFormulario.add(campoFecha);

        panelFormulario.add(new JLabel("Hora (HH:MM):"));
        campoHora = new JTextField(
                LocalTime.now().withSecond(0).withNano(0).toString()
        );
        panelFormulario.add(campoHora);

        add(panelFormulario, BorderLayout.NORTH);

        // ---------------- TABLA ----------------

        modeloTabla = new DefaultTableModel(
                new Object[]{
                        "ID",
                        "Pedido",
                        "Repartidor",
                        "Fecha",
                        "Hora"
                },
                0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tablaEntregas = new JTable(modeloTabla);
        tablaEntregas.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );

        add(
                new JScrollPane(tablaEntregas),
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

        botonCrear.addActionListener(e -> crearEntrega());

        botonActualizar.addActionListener(e -> actualizarEntrega());

        botonEliminar.addActionListener(e -> eliminarEntrega());

        botonLimpiar.addActionListener(e -> limpiarFormulario());

        tablaEntregas.getSelectionModel().addListSelectionListener(e -> {

            if (!e.getValueIsAdjusting()) {
                cargarEntregaSeleccionada();
            }
        });

        // ---------------- CARGA INICIAL ----------------

        cargarPedidos();
        cargarRepartidores();
        cargarEntregas();
    }

    private void cargarPedidos() {

        comboPedidos.removeAllItems();

        try {

            List<Pedido> pedidos = PedidoDAO.readAll();

            for (Pedido pedido : pedidos) {
                comboPedidos.addItem(pedido);
            }

        } catch (SQLException e) {

            mostrarError(
                    "Error al cargar los pedidos:\n" + e.getMessage()
            );
        }
    }

    private void cargarRepartidores() {

        comboRepartidores.removeAllItems();

        try {

            List<Repartidor> repartidores =
                    RepartidorDAO.readAll();

            for (Repartidor repartidor : repartidores) {
                comboRepartidores.addItem(repartidor);
            }

        } catch (SQLException e) {

            mostrarError(
                    "Error al cargar los repartidores:\n"
                            + e.getMessage()
            );
        }
    }

    private void cargarEntregas() {

        modeloTabla.setRowCount(0);

        try {

            List<Entrega> entregas = EntregaDAO.readAll();

            for (Entrega entrega : entregas) {

                modeloTabla.addRow(new Object[]{
                        entrega.getId(),
                        entrega.getPedido(),
                        entrega.getRepartidor(),
                        entrega.getFecha(),
                        entrega.getHora()
                });
            }

        } catch (SQLException e) {

            mostrarError(
                    "Error al cargar las entregas:\n"
                            + e.getMessage()
            );
        }
    }

    private void crearEntrega() {

        Pedido pedido =
                (Pedido) comboPedidos.getSelectedItem();

        Repartidor repartidor =
                (Repartidor) comboRepartidores.getSelectedItem();

        if (pedido == null || repartidor == null) {

            mostrarError(
                    "Debe seleccionar un pedido y un repartidor."
            );

            return;
        }

        try {

            LocalDate fecha =
                    LocalDate.parse(campoFecha.getText().trim());

            LocalTime hora =
                    LocalTime.parse(campoHora.getText().trim());

            Entrega entrega = new Entrega(
                    pedido,
                    repartidor,
                    fecha,
                    hora
            );

            if (EntregaDAO.create(entrega)) {

                JOptionPane.showMessageDialog(
                        this,
                        "Entrega registrada correctamente."
                );

                cargarEntregas();
                limpiarFormulario();

            } else {

                mostrarError(
                        "No se pudo registrar la entrega."
                );
            }

        } catch (DateTimeParseException e) {

            mostrarError(
                    "La fecha u hora no tienen un formato válido."
            );

        } catch (SQLException e) {

            mostrarError(
                    "Error al registrar la entrega:\n"
                            + e.getMessage()
            );
        }
    }

    private void actualizarEntrega() {

        if (idEntregaSeleccionada == -1) {

            mostrarError(
                    "Debe seleccionar una entrega de la tabla."
            );

            return;
        }

        Pedido pedido =
                (Pedido) comboPedidos.getSelectedItem();

        Repartidor repartidor =
                (Repartidor) comboRepartidores.getSelectedItem();

        if (pedido == null || repartidor == null) {

            mostrarError(
                    "Debe seleccionar un pedido y un repartidor."
            );

            return;
        }

        try {

            LocalDate fecha =
                    LocalDate.parse(campoFecha.getText().trim());

            LocalTime hora =
                    LocalTime.parse(campoHora.getText().trim());

            Entrega entrega = new Entrega(
                    idEntregaSeleccionada,
                    pedido,
                    repartidor,
                    fecha,
                    hora
            );

            if (EntregaDAO.update(entrega)) {

                JOptionPane.showMessageDialog(
                        this,
                        "Entrega actualizada correctamente."
                );

                cargarEntregas();
                limpiarFormulario();

            } else {

                mostrarError(
                        "No se pudo actualizar la entrega."
                );
            }

        } catch (DateTimeParseException e) {

            mostrarError(
                    "La fecha u hora no tienen un formato válido."
            );

        } catch (SQLException e) {

            mostrarError(
                    "Error al actualizar la entrega:\n"
                            + e.getMessage()
            );
        }
    }

    private void eliminarEntrega() {

        if (idEntregaSeleccionada == -1) {

            mostrarError(
                    "Debe seleccionar una entrega de la tabla."
            );

            return;
        }

        int respuesta = JOptionPane.showConfirmDialog(
                this,
                "¿Desea eliminar la entrega seleccionada?",
                "Confirmar eliminación",
                JOptionPane.YES_NO_OPTION
        );

        if (respuesta != JOptionPane.YES_OPTION) {
            return;
        }

        try {

            if (EntregaDAO.delete(idEntregaSeleccionada)) {

                JOptionPane.showMessageDialog(
                        this,
                        "Entrega eliminada correctamente."
                );

                cargarEntregas();
                limpiarFormulario();

            } else {

                mostrarError(
                        "No se pudo eliminar la entrega."
                );
            }

        } catch (SQLException e) {

            mostrarError(
                    "Error al eliminar la entrega:\n"
                            + e.getMessage()
            );
        }
    }

    private void cargarEntregaSeleccionada() {

        int fila = tablaEntregas.getSelectedRow();

        if (fila == -1) {
            return;
        }

        idEntregaSeleccionada =
                (int) modeloTabla.getValueAt(fila, 0);

        Pedido pedido =
                (Pedido) modeloTabla.getValueAt(fila, 1);

        Repartidor repartidor =
                (Repartidor) modeloTabla.getValueAt(fila, 2);

        LocalDate fecha =
                (LocalDate) modeloTabla.getValueAt(fila, 3);

        LocalTime hora =
                (LocalTime) modeloTabla.getValueAt(fila, 4);

        seleccionarPedido(pedido.getId());
        seleccionarRepartidor(repartidor.getId());

        campoFecha.setText(fecha.toString());
        campoHora.setText(hora.toString());
    }

    private void seleccionarPedido(int idPedido) {

        for (int i = 0; i < comboPedidos.getItemCount(); i++) {

            Pedido pedido = comboPedidos.getItemAt(i);

            if (pedido.getId() == idPedido) {

                comboPedidos.setSelectedIndex(i);
                break;
            }
        }
    }

    private void seleccionarRepartidor(int idRepartidor) {

        for (int i = 0;
             i < comboRepartidores.getItemCount();
             i++) {

            Repartidor repartidor =
                    comboRepartidores.getItemAt(i);

            if (repartidor.getId() == idRepartidor) {

                comboRepartidores.setSelectedIndex(i);
                break;
            }
        }
    }

    private void limpiarFormulario() {

        idEntregaSeleccionada = -1;

        tablaEntregas.clearSelection();

        if (comboPedidos.getItemCount() > 0) {
            comboPedidos.setSelectedIndex(0);
        }

        if (comboRepartidores.getItemCount() > 0) {
            comboRepartidores.setSelectedIndex(0);
        }

        campoFecha.setText(LocalDate.now().toString());

        campoHora.setText(
                LocalTime.now()
                        .withSecond(0)
                        .withNano(0)
                        .toString()
        );
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