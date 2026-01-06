package ec.edu.sistemalicencias.view;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import ec.edu.sistemalicencias.controller.LicenciaController;
import ec.edu.sistemalicencias.model.TipoSangreConstantes;
import ec.edu.sistemalicencias.model.entities.Conductor;
import ec.edu.sistemalicencias.model.exceptions.LicenciaException;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

/**
 * Vista para gestión de conductores.
 * Permite registrar, consultar y actualizar datos de conductores.
 *
 * @author Sistema Licencias Ecuador
 * @version 1.0
 */
@SuppressWarnings("unused")
public class GestionConductoresView extends JFrame {
    private final LicenciaController controller;
    private DefaultTableModel modeloTabla;
    private Conductor conductorSeleccionado;

    // Componentes enlazados desde el .form (UI Designer)
    private JPanel panelPrincipal;
    private JPanel panelFormulario;
    private JPanel panelBotones;
    private JScrollPane scrollTabla;
    private JTextField txtCedula;
    private JTextField txtNombres;
    private JTextField txtApellidos;
    private JTextField txtFechaNacimiento;
    private JTextField txtDireccion;
    private JTextField txtTelefono;
    private JTextField txtEmail;
    private JComboBox<String> cmbTipoSangre;
    private JTable tablaConductores;
    private JButton btnGuardar;
    private JButton btnLimpiar;
    private JButton btnActualizar;
    private JButton btnCerrar;

    /**
     * Constructor de la vista
     *
     * @param controller Controlador del sistema
     */
    public GestionConductoresView(LicenciaController controller) {
        this.controller = controller;
        setTitle("Gestión de Conductores");
        setContentPane(panelPrincipal);
        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        inicializarDatos();
        inicializarTabla();
        configurarEventos();
        cargarConductores();
    }

    /**
     * Inicializa los datos de los combos
     */
    private void inicializarDatos() {
        cmbTipoSangre.setModel(new DefaultComboBoxModel<>(TipoSangreConstantes.TIPOS_SANGRE));
    }

    /**
     * Inicializa la tabla de conductores
     */
    private void inicializarTabla() {
        String[] columnas = {"ID", "Cédula", "Nombres", "Apellidos", "Fecha Nac.", "Teléfono", "Docs. Validados"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tablaConductores.setModel(modeloTabla);
        tablaConductores.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }

    /**
     * Configura los eventos de los componentes
     */
    private void configurarEventos() {
        btnGuardar.addActionListener(e -> guardarConductor());
        btnLimpiar.addActionListener(e -> limpiarFormulario());
        btnActualizar.addActionListener(e -> cargarConductores());
        btnCerrar.addActionListener(e -> dispose());

        // Selección en la tabla para editar
        tablaConductores.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int filaSeleccionada = tablaConductores.getSelectedRow();
                if (filaSeleccionada >= 0) {
                    cargarConductorEnFormulario(filaSeleccionada);
                }
            }
        });
    }

    /**
     * Guarda o actualiza un conductor
     */
    private void guardarConductor() {
        try {
            // Validar campos obligatorios
            if (txtCedula.getText().trim().isEmpty() ||
                    txtNombres.getText().trim().isEmpty() ||
                    txtApellidos.getText().trim().isEmpty()) {
                controller.mostrarError("Los campos Cédula, Nombres y Apellidos son obligatorios");
                return;
            }

            // Crear o actualizar conductor
            Conductor conductor;
            if (conductorSeleccionado != null) {
                conductor = conductorSeleccionado;
            } else {
                conductor = new Conductor();
            }

            // Establecer datos
            conductor.setCedula(txtCedula.getText().trim());
            conductor.setNombres(txtNombres.getText().trim());
            conductor.setApellidos(txtApellidos.getText().trim());

            // Parsear fecha
            try {
                LocalDate fechaNac = LocalDate.parse(txtFechaNacimiento.getText().trim());
                conductor.setFechaNacimiento(fechaNac);
            } catch (DateTimeParseException ex) {
                controller.mostrarError("Formato de fecha inválido. Use AAAA-MM-DD");
                return;
            }

            conductor.setDireccion(txtDireccion.getText().trim());
            conductor.setTelefono(txtTelefono.getText().trim());
            conductor.setEmail(txtEmail.getText().trim());

            String tipoSangre = (String) cmbTipoSangre.getSelectedItem();
            if (tipoSangre != null) {
                conductor.setTipoSangre(tipoSangre);
            }

            // Guardar
            Long id = controller.registrarConductor(conductor);

            if (conductorSeleccionado != null) {
                controller.mostrarExito("Conductor actualizado exitosamente");
            } else {
                controller.mostrarExito("Conductor registrado exitosamente con ID: " + id);
            }

            limpiarFormulario();
            cargarConductores();

        } catch (LicenciaException ex) {
            controller.mostrarError("Error al guardar conductor: " + ex.getMessage());
        } catch (Exception ex) {
            controller.mostrarError("Error: " + ex.getMessage());
        }
    }

    /**
     * Carga los conductores en la tabla
     */
    private void cargarConductores() {
        try {
            List<Conductor> conductores = controller.obtenerTodosConductores();
            modeloTabla.setRowCount(0);

            for (Conductor c : conductores) {
                Object[] fila = {
                        c.getId(),
                        c.getCedula(),
                        c.getNombres(),
                        c.getApellidos(),
                        c.getFechaNacimiento(),
                        c.getTelefono(),
                        c.isDocumentosValidados() ? "SÍ" : "NO"
                };
                modeloTabla.addRow(fila);
            }
        } catch (LicenciaException ex) {
            controller.mostrarError("Error al cargar conductores: " + ex.getMessage());
        }
    }

    /**
     * Carga los datos del conductor seleccionado en el formulario
     *
     * @param fila Índice de la fila seleccionada
     */
    private void cargarConductorEnFormulario(int fila) {
        try {
            Long id = (Long) modeloTabla.getValueAt(fila, 0);
            conductorSeleccionado = controller.buscarConductorPorId(id);

            if (conductorSeleccionado != null) {
                txtCedula.setText(conductorSeleccionado.getCedula());
                txtNombres.setText(conductorSeleccionado.getNombres());
                txtApellidos.setText(conductorSeleccionado.getApellidos());
                txtFechaNacimiento.setText(conductorSeleccionado.getFechaNacimiento().toString());
                txtDireccion.setText(conductorSeleccionado.getDireccion());
                txtTelefono.setText(conductorSeleccionado.getTelefono());
                txtEmail.setText(conductorSeleccionado.getEmail());

                // Seleccionar tipo de sangre
                String tipoSangre = conductorSeleccionado.getTipoSangre();
                for (int i = 0; i < cmbTipoSangre.getItemCount(); i++) {
                    if (cmbTipoSangre.getItemAt(i).equals(tipoSangre)) {
                        cmbTipoSangre.setSelectedIndex(i);
                        break;
                    }
                }
            }
        } catch (LicenciaException ex) {
            controller.mostrarError("Error al cargar conductor: " + ex.getMessage());
        }
    }

    /**
     * Limpia el formulario
     */
    private void limpiarFormulario() {
        conductorSeleccionado = null;
        txtCedula.setText("");
        txtNombres.setText("");
        txtApellidos.setText("");
        txtFechaNacimiento.setText("1990-01-01");
        txtDireccion.setText("");
        txtTelefono.setText("");
        txtEmail.setText("");
        cmbTipoSangre.setSelectedIndex(0);
        tablaConductores.clearSelection();
    }

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        panelPrincipal = new JPanel();
        panelPrincipal.setLayout(new GridLayoutManager(3, 1, new Insets(15, 15, 15, 15), -1, -1));
        panelPrincipal.setMinimumSize(new Dimension(900, 600));
        panelPrincipal.setPreferredSize(new Dimension(900, 600));
        panelFormulario = new JPanel();
        panelFormulario.setLayout(new GridLayoutManager(4, 4, new Insets(10, 10, 10, 10), -1, -1));
        panelPrincipal.add(panelFormulario, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, 1, null, null, null, 0, false));
        panelFormulario.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black), "Datos del Conductor", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        final JLabel label1 = new JLabel();
        label1.setText("Cédula:");
        panelFormulario.add(label1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        txtCedula = new JTextField();
        panelFormulario.add(txtCedula, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JLabel label2 = new JLabel();
        label2.setText("Nombres:");
        panelFormulario.add(label2, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        txtNombres = new JTextField();
        panelFormulario.add(txtNombres, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JLabel label3 = new JLabel();
        label3.setText("Apellidos:");
        panelFormulario.add(label3, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        txtApellidos = new JTextField();
        panelFormulario.add(txtApellidos, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JLabel label4 = new JLabel();
        label4.setText("Fecha Nac (AAAA-MM-DD):");
        panelFormulario.add(label4, new GridConstraints(1, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        txtFechaNacimiento = new JTextField();
        txtFechaNacimiento.setText("1990-01-01");
        panelFormulario.add(txtFechaNacimiento, new GridConstraints(1, 3, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JLabel label5 = new JLabel();
        label5.setText("Dirección:");
        panelFormulario.add(label5, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        txtDireccion = new JTextField();
        panelFormulario.add(txtDireccion, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JLabel label6 = new JLabel();
        label6.setText("Teléfono:");
        panelFormulario.add(label6, new GridConstraints(2, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        txtTelefono = new JTextField();
        panelFormulario.add(txtTelefono, new GridConstraints(2, 3, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JLabel label7 = new JLabel();
        label7.setText("Email:");
        panelFormulario.add(label7, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        txtEmail = new JTextField();
        panelFormulario.add(txtEmail, new GridConstraints(3, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JLabel label8 = new JLabel();
        label8.setText("Tipo Sangre:");
        panelFormulario.add(label8, new GridConstraints(3, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        cmbTipoSangre = new JComboBox();
        panelFormulario.add(cmbTipoSangre, new GridConstraints(3, 3, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        scrollTabla = new JScrollPane();
        panelPrincipal.add(scrollTabla, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        scrollTabla.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black), "Conductores Registrados", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        tablaConductores = new JTable();
        scrollTabla.setViewportView(tablaConductores);
        panelBotones = new JPanel();
        panelBotones.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
        panelPrincipal.add(panelBotones, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        btnGuardar = new JButton();
        btnGuardar.setText("Guardar Conductor");
        panelBotones.add(btnGuardar);
        btnLimpiar = new JButton();
        btnLimpiar.setText("Limpiar");
        panelBotones.add(btnLimpiar);
        btnActualizar = new JButton();
        btnActualizar.setText("Actualizar Lista");
        panelBotones.add(btnActualizar);
        btnCerrar = new JButton();
        btnCerrar.setText("Cerrar");
        panelBotones.add(btnCerrar);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return panelPrincipal;
    }
}
