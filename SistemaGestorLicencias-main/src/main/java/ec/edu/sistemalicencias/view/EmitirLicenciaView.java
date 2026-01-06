package ec.edu.sistemalicencias.view;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import ec.edu.sistemalicencias.controller.LicenciaController;
import ec.edu.sistemalicencias.model.TipoLicenciaConstantes;
import ec.edu.sistemalicencias.model.entities.Conductor;
import ec.edu.sistemalicencias.model.entities.Licencia;
import ec.edu.sistemalicencias.model.entities.PruebaPsicometrica;
import ec.edu.sistemalicencias.model.exceptions.LicenciaException;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.plaf.FontUIResource;
import javax.swing.text.StyleContext;
import java.awt.*;
import java.util.Locale;

/**
 * Vista para emitir licencias de conducir.
 */
@SuppressWarnings("unused")
public class EmitirLicenciaView extends JFrame {
    private final LicenciaController controller;
    private Conductor conductorActual;
    private PruebaPsicometrica pruebaActual;

    // Componentes enlazados desde el .form (UI Designer)
    private JPanel panelPrincipal;
    private JPanel panelBusqueda;
    private JPanel panelPrueba;
    private JPanel panelLicencia;
    private JPanel panelBotones;
    private JTextField txtCedula;
    private JButton btnBuscar;
    private JTextArea txtInfoConductor;
    private JScrollPane scrollInfo;
    private JLabel lblPruebaInfo;
    private JComboBox<String> cmbTipoLicencia;
    private JButton btnEmitir;
    private JButton btnCerrar;

    public EmitirLicenciaView(LicenciaController controller) {
        this.controller = controller;
        setTitle("Emitir Licencia de Conducir");
        setContentPane(panelPrincipal);
        setSize(650, 500);
        setLocationRelativeTo(null);
        inicializarDatos();
        configurarEventos();
    }

    private void inicializarDatos() {
        // Cargar tipos de licencia en el combo
        cmbTipoLicencia.setModel(new DefaultComboBoxModel<>(TipoLicenciaConstantes.NOMBRES_TIPOS_LICENCIA));
    }

    private void configurarEventos() {
        btnBuscar.addActionListener(e -> buscarConductor());
        btnEmitir.addActionListener(e -> emitirLicencia());
        btnCerrar.addActionListener(e -> dispose());
    }

    private void buscarConductor() {
        try {
            conductorActual = controller.buscarConductorPorCedula(txtCedula.getText().trim());

            if (conductorActual == null) {
                controller.mostrarError("Conductor no encontrado");
                limpiarInfo();
                return;
            }

            // Mostrar info conductor
            StringBuilder sb = new StringBuilder();
            sb.append("Nombre: ").append(conductorActual.getNombreCompleto()).append("\n");
            sb.append("Cédula: ").append(conductorActual.getCedula()).append("\n");
            sb.append("Edad: ").append(conductorActual.calcularEdad()).append(" años\n");
            sb.append("Documentos Validados: ").append(conductorActual.isDocumentosValidados() ? "SÍ" : "NO");
            txtInfoConductor.setText(sb.toString());

            // Buscar última prueba aprobada
            pruebaActual = controller.obtenerUltimaPruebaAprobada(conductorActual.getId());

            if (pruebaActual != null) {
                lblPruebaInfo.setText(String.format(
                        "<html>Prueba ID: %d | Promedio: %.2f | Estado: %s</html>",
                        pruebaActual.getId(),
                        pruebaActual.calcularPromedio(),
                        pruebaActual.obtenerEstado()
                ));
                lblPruebaInfo.setForeground(new Color(34, 139, 34));
            } else {
                lblPruebaInfo.setText("No tiene pruebas aprobadas");
                lblPruebaInfo.setForeground(Color.RED);
            }

        } catch (LicenciaException ex) {
            controller.mostrarError("Error: " + ex.getMessage());
        }
    }

    private void emitirLicencia() {
        if (conductorActual == null) {
            controller.mostrarError("Debe buscar un conductor primero");
            return;
        }

        try {
            // Obtener el tipo de licencia seleccionado (convertir índice a constante)
            int indiceSeleccionado = cmbTipoLicencia.getSelectedIndex();
            String tipoLicencia = TipoLicenciaConstantes.TIPOS_LICENCIA[indiceSeleccionado];
            Long pruebaId = pruebaActual != null ? pruebaActual.getId() : null;

            Licencia licencia = controller.emitirLicencia(conductorActual.getId(), tipoLicencia, pruebaId);

            StringBuilder mensaje = new StringBuilder();
            mensaje.append("¡LICENCIA EMITIDA EXITOSAMENTE!\n\n");
            mensaje.append("Número: ").append(licencia.getNumeroLicencia()).append("\n");
            mensaje.append("Tipo: ").append(TipoLicenciaConstantes.obtenerNombre(licencia.getTipoLicencia())).append("\n");
            mensaje.append("Conductor: ").append(conductorActual.getNombreCompleto()).append("\n");
            mensaje.append("Válida hasta: ").append(licencia.getFechaVencimiento()).append("\n");

            controller.mostrarExito(mensaje.toString());

            // Preguntar si desea generar PDF
            if (controller.confirmar("¿Desea generar el documento PDF de la licencia?")) {
                controller.generarDocumentoLicenciaConDialogo(licencia.getId());
            }

            limpiarInfo();

        } catch (LicenciaException ex) {
            controller.mostrarError("Error al emitir licencia:\n" + ex.getMessage());
        }
    }

    private void limpiarInfo() {
        conductorActual = null;
        pruebaActual = null;
        txtCedula.setText("");
        txtInfoConductor.setText("");
        lblPruebaInfo.setText("Sin prueba psicométrica registrada");
        lblPruebaInfo.setForeground(Color.BLACK);
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
        panelPrincipal.setMinimumSize(new Dimension(650, 550));
        panelPrincipal.setPreferredSize(new Dimension(650, 550));
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
        panelPrincipal.add(scrollInfo, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        scrollInfo.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black), "Información del Conductor", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        txtInfoConductor = new JTextArea();
        txtInfoConductor.setEditable(false);
        txtInfoConductor.setRows(8);
        scrollInfo.setViewportView(txtInfoConductor);
        panelPrueba = new JPanel();
        panelPrueba.setLayout(new GridLayoutManager(1, 1, new Insets(10, 10, 10, 10), -1, -1));
        panelPrincipal.add(panelPrueba, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        panelPrueba.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black), "Información de Prueba Psicométrica", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        lblPruebaInfo = new JLabel();
        Font lblPruebaInfoFont = this.$$$getFont$$$(null, -1, 12, lblPruebaInfo.getFont());
        if (lblPruebaInfoFont != null) lblPruebaInfo.setFont(lblPruebaInfoFont);
        lblPruebaInfo.setText("Sin prueba psicométrica registrada");
        panelPrueba.add(lblPruebaInfo, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        panelLicencia = new JPanel();
        panelLicencia.setLayout(new GridLayoutManager(1, 2, new Insets(10, 10, 10, 10), -1, -1));
        panelPrincipal.add(panelLicencia, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        panelLicencia.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black), "Emisión de Licencia", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        final JLabel label2 = new JLabel();
        label2.setText("Tipo de Licencia:");
        panelLicencia.add(label2, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        cmbTipoLicencia = new JComboBox();
        panelLicencia.add(cmbTipoLicencia, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        panelBotones = new JPanel();
        panelBotones.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
        panelPrincipal.add(panelBotones, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        btnEmitir = new JButton();
        btnEmitir.setText("Emitir Licencia");
        panelBotones.add(btnEmitir);
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
