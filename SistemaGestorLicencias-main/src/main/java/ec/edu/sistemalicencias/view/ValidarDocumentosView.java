package ec.edu.sistemalicencias.view;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import ec.edu.sistemalicencias.controller.LicenciaController;
import ec.edu.sistemalicencias.model.entities.Conductor;
import ec.edu.sistemalicencias.model.exceptions.LicenciaException;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;

/**
 * Vista para validación de documentos de conductores.
 * Verifica la entrega de documentos requeridos para licencias no profesionales.
 */
@SuppressWarnings("unused")
public class ValidarDocumentosView extends JFrame {
    private final LicenciaController controller;
    private Conductor conductorActual;

    // Componentes enlazados desde el .form (UI Designer)
    private JPanel panelPrincipal;
    private JPanel panelBusqueda;
    private JPanel panelDocumentos;
    private JPanel panelBotones;
    private JTextField txtCedula;
    private JButton btnBuscar;
    private JTextArea txtInfo;
    private JScrollPane scrollInfo;
    private JCheckBox chkTituloConductor;
    private JCheckBox chkPermisoAprendizaje;
    private JCheckBox chkExamenPsicosensometrico;
    private JCheckBox chkCedulaPapeleta;
    private JCheckBox chkCertificadoSangre;
    private JCheckBox chkComprobantePago;
    private JCheckBox chkTurnoImpreso;
    private JCheckBox chkCertificadoEducacion;
    private JButton btnMarcarTodos;
    private JButton btnDesmarcarTodos;
    private JButton btnGuardar;
    private JButton btnCerrar;

    public ValidarDocumentosView(LicenciaController controller) {
        this.controller = controller;
        setTitle("Validar Documentos - Licencia No Profesional");
        setContentPane(panelPrincipal);
        setSize(750, 700);
        setLocationRelativeTo(null);
        configurarEventos();
    }

    private void configurarEventos() {
        btnBuscar.addActionListener(e -> buscarConductor());
        btnMarcarTodos.addActionListener(e -> marcarTodosDocumentos(true));
        btnDesmarcarTodos.addActionListener(e -> marcarTodosDocumentos(false));
        btnGuardar.addActionListener(e -> guardarValidacion());
        btnCerrar.addActionListener(e -> dispose());
    }

    private void marcarTodosDocumentos(boolean seleccionar) {
        chkTituloConductor.setSelected(seleccionar);
        chkPermisoAprendizaje.setSelected(seleccionar);
        chkExamenPsicosensometrico.setSelected(seleccionar);
        chkCedulaPapeleta.setSelected(seleccionar);
        chkCertificadoSangre.setSelected(seleccionar);
        chkComprobantePago.setSelected(seleccionar);
        chkTurnoImpreso.setSelected(seleccionar);
        chkCertificadoEducacion.setSelected(seleccionar);
    }

    private void buscarConductor() {
        try {
            conductorActual = controller.buscarConductorPorCedula(txtCedula.getText().trim());
            if (conductorActual != null) {
                mostrarInfoConductor();
            } else {
                controller.mostrarError("No se encontró el conductor con esa cédula");
                txtInfo.setText("");
                limpiarCheckboxes();
            }
        } catch (LicenciaException ex) {
            controller.mostrarError("Error: " + ex.getMessage());
        }
    }

    private void mostrarInfoConductor() {
        StringBuilder sb = new StringBuilder();
        sb.append("Cédula: ").append(conductorActual.getCedula()).append("\n");
        sb.append("Nombre: ").append(conductorActual.getNombreCompleto()).append("\n");
        sb.append("Edad: ").append(conductorActual.calcularEdad()).append(" años\n");
        sb.append("Dirección: ").append(conductorActual.getDireccion()).append("\n");
        sb.append("Teléfono: ").append(conductorActual.getTelefono()).append("\n");
        sb.append("Estado actual: ").append(conductorActual.isDocumentosValidados() ? "DOCUMENTOS VALIDADOS" : "PENDIENTE DE VALIDACIÓN");

        txtInfo.setText(sb.toString());

        if (conductorActual.isDocumentosValidados()) {
            marcarTodosDocumentos(true);
        } else {
            limpiarCheckboxes();
        }
    }

    private void limpiarCheckboxes() {
        marcarTodosDocumentos(false);
    }

    private boolean todosDocumentosValidados() {
        return chkTituloConductor.isSelected() &&
                chkPermisoAprendizaje.isSelected() &&
                chkExamenPsicosensometrico.isSelected() &&
                chkCedulaPapeleta.isSelected() &&
                chkCertificadoSangre.isSelected() &&
                chkComprobantePago.isSelected() &&
                chkTurnoImpreso.isSelected() &&
                chkCertificadoEducacion.isSelected();
    }

    private String obtenerDocumentosFaltantes() {
        StringBuilder faltantes = new StringBuilder();
        if (!chkTituloConductor.isSelected()) faltantes.append("- Título de conductor\n");
        if (!chkPermisoAprendizaje.isSelected()) faltantes.append("- Permiso de aprendizaje\n");
        if (!chkExamenPsicosensometrico.isSelected()) faltantes.append("- Examen psicosensométrico\n");
        if (!chkCedulaPapeleta.isSelected()) faltantes.append("- Cédula y papeleta de votación\n");
        if (!chkCertificadoSangre.isSelected()) faltantes.append("- Certificado de tipo sanguíneo\n");
        if (!chkComprobantePago.isSelected()) faltantes.append("- Comprobante de pago\n");
        if (!chkTurnoImpreso.isSelected()) faltantes.append("- Turno impreso\n");
        if (!chkCertificadoEducacion.isSelected()) faltantes.append("- Certificado de educación básica\n");
        return faltantes.toString();
    }

    private void guardarValidacion() {
        if (conductorActual == null) {
            controller.mostrarError("Debe buscar un conductor primero");
            return;
        }

        try {
            boolean validacionCompleta = todosDocumentosValidados();
            String observaciones = "";

            if (!validacionCompleta) {
                observaciones = "Documentos faltantes:\n" + obtenerDocumentosFaltantes();
            }

            conductorActual.setDocumentosValidados(validacionCompleta);
            conductorActual.setObservaciones(observaciones);

            controller.actualizarConductor(conductorActual);

            if (validacionCompleta) {
                controller.mostrarExito("Validación completada exitosamente.\nTodos los documentos han sido verificados.");
            } else {
                controller.mostrarError("Validación guardada como INCOMPLETA.\n\n" + observaciones);
            }

            mostrarInfoConductor();

        } catch (LicenciaException ex) {
            controller.mostrarError("Error al guardar: " + ex.getMessage());
        }
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
        panelPrincipal.setLayout(new GridLayoutManager(4, 1, new Insets(15, 15, 15, 15), -1, -1));
        panelPrincipal.setMinimumSize(new Dimension(750, 700));
        panelPrincipal.setPreferredSize(new Dimension(750, 700));
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
        panelPrincipal.add(scrollInfo, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        scrollInfo.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black), "Información del Conductor", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        txtInfo = new JTextArea();
        txtInfo.setEditable(false);
        txtInfo.setRows(6);
        scrollInfo.setViewportView(txtInfo);
        panelDocumentos = new JPanel();
        panelDocumentos.setLayout(new GridLayoutManager(9, 1, new Insets(10, 10, 10, 10), -1, 5));
        panelPrincipal.add(panelDocumentos, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        panelDocumentos.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black), "Documentos Requeridos - Verificar Entrega", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        chkTituloConductor = new JCheckBox();
        chkTituloConductor.setText("1. Original del título de conductor no profesional");
        panelDocumentos.add(chkTituloConductor, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        chkPermisoAprendizaje = new JCheckBox();
        chkPermisoAprendizaje.setText("2. Original del permiso de aprendizaje");
        panelDocumentos.add(chkPermisoAprendizaje, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        chkExamenPsicosensometrico = new JCheckBox();
        chkExamenPsicosensometrico.setText("3. Original del examen psicosensométrico");
        panelDocumentos.add(chkExamenPsicosensometrico, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        chkCedulaPapeleta = new JCheckBox();
        chkCedulaPapeleta.setText("4. Cédula de identidad original y papeleta de votación vigente");
        panelDocumentos.add(chkCedulaPapeleta, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        chkCertificadoSangre = new JCheckBox();
        chkCertificadoSangre.setText("5. Original del certificado de tipo sanguíneo (Cruz Roja Ecuatoriana)");
        panelDocumentos.add(chkCertificadoSangre, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        chkComprobantePago = new JCheckBox();
        chkComprobantePago.setText("6. Original del comprobante de pago");
        panelDocumentos.add(chkComprobantePago, new GridConstraints(5, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JSeparator separator1 = new JSeparator();
        panelDocumentos.add(separator1, new GridConstraints(6, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        chkTurnoImpreso = new JCheckBox();
        chkTurnoImpreso.setText("7. Turno impreso");
        panelDocumentos.add(chkTurnoImpreso, new GridConstraints(7, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        chkCertificadoEducacion = new JCheckBox();
        chkCertificadoEducacion.setText("8. Certificado de educación básica culminada");
        panelDocumentos.add(chkCertificadoEducacion, new GridConstraints(8, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        panelBotones = new JPanel();
        panelBotones.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
        panelPrincipal.add(panelBotones, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        btnMarcarTodos = new JButton();
        btnMarcarTodos.setText("Marcar Todos");
        panelBotones.add(btnMarcarTodos);
        btnDesmarcarTodos = new JButton();
        btnDesmarcarTodos.setText("Desmarcar Todos");
        panelBotones.add(btnDesmarcarTodos);
        btnGuardar = new JButton();
        btnGuardar.setText("Guardar Validación");
        panelBotones.add(btnGuardar);
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
