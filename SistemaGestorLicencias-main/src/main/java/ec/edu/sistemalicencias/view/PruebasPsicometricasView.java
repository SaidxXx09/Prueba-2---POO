package ec.edu.sistemalicencias.view;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import ec.edu.sistemalicencias.controller.LicenciaController;
import ec.edu.sistemalicencias.model.entities.Conductor;
import ec.edu.sistemalicencias.model.entities.PruebaPsicometrica;
import ec.edu.sistemalicencias.model.exceptions.LicenciaException;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.plaf.FontUIResource;
import javax.swing.text.StyleContext;
import java.awt.*;
import java.util.Locale;

/**
 * Vista para registrar pruebas psicométricas.
 */
@SuppressWarnings("unused")
public class PruebasPsicometricasView extends JFrame {
    private final LicenciaController controller;
    private Conductor conductorActual;

    // Componentes enlazados desde el .form (UI Designer)
    private JPanel panelPrincipal;
    private JPanel panelBusqueda;
    private JPanel panelNotas;
    private JPanel panelResultados;
    private JPanel panelObservaciones;
    private JPanel panelBotones;
    private JScrollPane scrollInfo;
    private JScrollPane scrollObservaciones;
    private JTextField txtCedula;
    private JButton btnBuscar;
    private JTextArea txtInfoConductor;
    private JTextField txtNotaReaccion;
    private JTextField txtNotaAtencion;
    private JTextField txtNotaCoordinacion;
    private JTextField txtNotaPercepcion;
    private JTextField txtNotaPsicologica;
    private JButton btnCalcular;
    private JLabel lblPromedio;
    private JLabel lblResultado;
    private JTextArea txtObservaciones;
    private JButton btnGuardar;
    private JButton btnLimpiar;
    private JButton btnCerrar;

    public PruebasPsicometricasView(LicenciaController controller) {
        this.controller = controller;
        setTitle("Pruebas Psicométricas");
        setContentPane(panelPrincipal);
        setSize(700, 600);
        setLocationRelativeTo(null);
        configurarEventos();
    }

    private void configurarEventos() {
        btnBuscar.addActionListener(e -> buscarConductor());
        btnCalcular.addActionListener(e -> calcularPromedio());
        btnGuardar.addActionListener(e -> guardarPrueba());
        btnLimpiar.addActionListener(e -> limpiarFormulario());
        btnCerrar.addActionListener(e -> dispose());
    }

    private void buscarConductor() {
        try {
            conductorActual = controller.buscarConductorPorCedula(txtCedula.getText().trim());
            if (conductorActual != null) {
                txtInfoConductor.setText(String.format("Nombre: %s\nCédula: %s\nEdad: %d años",
                        conductorActual.getNombreCompleto(),
                        conductorActual.getCedula(),
                        conductorActual.calcularEdad()));
            } else {
                controller.mostrarError("Conductor no encontrado");
                txtInfoConductor.setText("");
            }
        } catch (LicenciaException ex) {
            controller.mostrarError("Error: " + ex.getMessage());
        }
    }

    private void calcularPromedio() {
        try {
            double reaccion = Double.parseDouble(txtNotaReaccion.getText().trim());
            double atencion = Double.parseDouble(txtNotaAtencion.getText().trim());
            double coordinacion = Double.parseDouble(txtNotaCoordinacion.getText().trim());
            double percepcion = Double.parseDouble(txtNotaPercepcion.getText().trim());
            double psicologica = Double.parseDouble(txtNotaPsicologica.getText().trim());

            double promedio = (reaccion + atencion + coordinacion + percepcion + psicologica) / 5.0;
            lblPromedio.setText(String.format("Promedio: %.2f", promedio));

            if (promedio >= 70) {
                lblResultado.setText("Estado: APROBADO");
                lblResultado.setForeground(Color.GREEN.darker());
            } else {
                lblResultado.setText("Estado: REPROBADO");
                lblResultado.setForeground(Color.RED);
            }
        } catch (NumberFormatException ex) {
            controller.mostrarError("Ingrese todas las notas correctamente");
        }
    }

    private void guardarPrueba() {
        if (conductorActual == null) {
            controller.mostrarError("Debe buscar un conductor primero");
            return;
        }

        try {
            PruebaPsicometrica prueba = new PruebaPsicometrica(conductorActual.getId());
            prueba.setNotaReaccion(Double.parseDouble(txtNotaReaccion.getText().trim()));
            prueba.setNotaAtencion(Double.parseDouble(txtNotaAtencion.getText().trim()));
            prueba.setNotaCoordinacion(Double.parseDouble(txtNotaCoordinacion.getText().trim()));
            prueba.setNotaPercepcion(Double.parseDouble(txtNotaPercepcion.getText().trim()));
            prueba.setNotaPsicologica(Double.parseDouble(txtNotaPsicologica.getText().trim()));
            prueba.setObservaciones(txtObservaciones.getText().trim());

            Long id = controller.registrarPruebaPsicometrica(prueba);
            controller.mostrarExito("Prueba registrada exitosamente con ID: " + id +
                    "\nPromedio: " + String.format("%.2f", prueba.calcularPromedio()) +
                    "\nEstado: " + prueba.obtenerEstado());

            limpiarFormulario();

        } catch (NumberFormatException ex) {
            controller.mostrarError("Error en formato de notas");
        } catch (LicenciaException ex) {
            controller.mostrarError("Error: " + ex.getMessage());
        }
    }

    private void limpiarFormulario() {
        txtNotaReaccion.setText("");
        txtNotaAtencion.setText("");
        txtNotaCoordinacion.setText("");
        txtNotaPercepcion.setText("");
        txtNotaPsicologica.setText("");
        txtObservaciones.setText("");
        lblPromedio.setText("Promedio: --");
        lblResultado.setText("Estado: --");
        lblResultado.setForeground(Color.BLACK);
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
        panelPrincipal.setLayout(new GridLayoutManager(5, 1, new Insets(15, 15, 15, 15), -1, -1));
        panelPrincipal.setMinimumSize(new Dimension(700, 600));
        panelPrincipal.setPreferredSize(new Dimension(700, 600));
        panelBusqueda = new JPanel();
        panelBusqueda.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
        panelPrincipal.add(panelBusqueda, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label1 = new JLabel();
        label1.setText("Cédula:");
        panelBusqueda.add(label1);
        txtCedula = new JTextField();
        txtCedula.setColumns(15);
        panelBusqueda.add(txtCedula);
        btnBuscar = new JButton();
        btnBuscar.setText("Buscar");
        panelBusqueda.add(btnBuscar);
        scrollInfo = new JScrollPane();
        panelPrincipal.add(scrollInfo, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        scrollInfo.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black), "Información del Conductor", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        txtInfoConductor = new JTextArea();
        txtInfoConductor.setEditable(false);
        txtInfoConductor.setRows(5);
        scrollInfo.setViewportView(txtInfoConductor);
        panelNotas = new JPanel();
        panelNotas.setLayout(new GridLayoutManager(3, 4, new Insets(10, 10, 10, 10), -1, -1));
        panelPrincipal.add(panelNotas, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        panelNotas.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black), "Calificaciones Prueba Psicométrica", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        final JLabel label2 = new JLabel();
        label2.setText("Reacción (0-100):");
        panelNotas.add(label2, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        txtNotaReaccion = new JTextField();
        txtNotaReaccion.setText("");
        panelNotas.add(txtNotaReaccion, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(80, -1), null, 0, false));
        final JLabel label3 = new JLabel();
        label3.setText("Atención (0-100):");
        panelNotas.add(label3, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        txtNotaAtencion = new JTextField();
        panelNotas.add(txtNotaAtencion, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(80, -1), null, 0, false));
        final JLabel label4 = new JLabel();
        label4.setText("Coordinación (0-100):");
        panelNotas.add(label4, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        txtNotaCoordinacion = new JTextField();
        panelNotas.add(txtNotaCoordinacion, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(80, -1), null, 0, false));
        final JLabel label5 = new JLabel();
        label5.setText("Percepción (0-100):");
        panelNotas.add(label5, new GridConstraints(1, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        txtNotaPercepcion = new JTextField();
        panelNotas.add(txtNotaPercepcion, new GridConstraints(1, 3, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(80, -1), null, 0, false));
        final JLabel label6 = new JLabel();
        label6.setText("Psicológica (0-100):");
        panelNotas.add(label6, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        txtNotaPsicologica = new JTextField();
        panelNotas.add(txtNotaPsicologica, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(80, -1), null, 0, false));
        btnCalcular = new JButton();
        btnCalcular.setText("Calcular Promedio");
        panelNotas.add(btnCalcular, new GridConstraints(2, 2, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        panelResultados = new JPanel();
        panelResultados.setLayout(new GridLayoutManager(3, 1, new Insets(10, 10, 10, 10), -1, -1));
        panelPrincipal.add(panelResultados, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        panelResultados.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black), "Resultados", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        lblPromedio = new JLabel();
        Font lblPromedioFont = this.$$$getFont$$$(null, Font.BOLD, 14, lblPromedio.getFont());
        if (lblPromedioFont != null) lblPromedio.setFont(lblPromedioFont);
        lblPromedio.setText("Promedio: --");
        panelResultados.add(lblPromedio, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        lblResultado = new JLabel();
        Font lblResultadoFont = this.$$$getFont$$$(null, Font.BOLD, 14, lblResultado.getFont());
        if (lblResultadoFont != null) lblResultado.setFont(lblResultadoFont);
        lblResultado.setText("Estado: --");
        panelResultados.add(lblResultado, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JScrollPane scrollPane1 = new JScrollPane();
        panelResultados.add(scrollPane1, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        scrollPane1.setBorder(BorderFactory.createTitledBorder(null, "Observaciones", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        txtObservaciones = new JTextArea();
        txtObservaciones.setRows(3);
        scrollPane1.setViewportView(txtObservaciones);
        panelBotones = new JPanel();
        panelBotones.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
        panelPrincipal.add(panelBotones, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        btnGuardar = new JButton();
        btnGuardar.setText("Guardar Resultados");
        panelBotones.add(btnGuardar);
        btnLimpiar = new JButton();
        btnLimpiar.setText("Limpiar");
        panelBotones.add(btnLimpiar);
        btnCerrar = new JButton();
        btnCerrar.setText("Cerrar");
        panelBotones.add(btnCerrar);
    }

    /**
     * @noinspection ALL
     */
    private Font $$$getFont$$$(String fontName, int style, int size, Font currentFont) {
        if (currentFont == null) return null;
        String resultName;
        if (fontName == null) {
            resultName = currentFont.getName();
        } else {
            Font testFont = new Font(fontName, Font.PLAIN, 10);
            if (testFont.canDisplay('a') && testFont.canDisplay('1')) {
                resultName = fontName;
            } else {
                resultName = currentFont.getName();
            }
        }
        Font font = new Font(resultName, style >= 0 ? style : currentFont.getStyle(), size >= 0 ? size : currentFont.getSize());
        boolean isMac = System.getProperty("os.name", "").toLowerCase(Locale.ENGLISH).startsWith("mac");
        Font fontWithFallback = isMac ? new Font(font.getFamily(), font.getStyle(), font.getSize()) : new StyleContext().getFont(font.getFamily(), font.getStyle(), font.getSize());
        return fontWithFallback instanceof FontUIResource ? fontWithFallback : new FontUIResource(fontWithFallback);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return panelPrincipal;
    }
}
