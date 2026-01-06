package ec.edu.sistemalicencias.view;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import ec.edu.sistemalicencias.controller.LicenciaController;
import ec.edu.sistemalicencias.model.entities.Conductor;
import ec.edu.sistemalicencias.model.entities.Licencia;
import ec.edu.sistemalicencias.model.entities.PruebaPsicometrica;
import ec.edu.sistemalicencias.model.exceptions.LicenciaException;
import ec.edu.sistemalicencias.util.PDFGenerator;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.plaf.FontUIResource;
import javax.swing.text.StyleContext;
import java.awt.*;
import java.io.File;
import java.util.List;
import java.util.Locale;

/**
 * Vista principal del Sistema de Licencias de Conducir.
 * Contiene el menú principal con acceso a todos los módulos del sistema.
 *
 * @author Sistema Licencias Ecuador
 * @version 1.0
 */
public class MainView extends JFrame {

    // Controlador del sistema
    private final LicenciaController controller;

    // Componentes de la interfaz
    private JPanel mainPanel;
    private JButton btnGestionConductores;
    private JButton btnValidarDocumentos;
    private JButton btnPruebasPsicometricas;
    private JButton btnEmitirLicencia;
    private JButton btnConsultarLicencias;
    private JButton btnGenerarDocumento;
    private JButton btnSalir;

    /**
     * Constructor de la vista principal
     */
    public MainView() {
        this.controller = new LicenciaController();

        // Inicializar componentes programáticamente
        inicializarComponentes();

        setTitle("Sistema de Licencias de Conducir - Ecuador");
        setContentPane(mainPanel);
        setSize(800, 600);
        setMinimumSize(new Dimension(800, 600));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        configurarEventos();
        configurarEstilos();
    }

    /**
     * Inicializa todos los componentes de la interfaz gráfica
     */
    private void inicializarComponentes() {
        // Panel principal
        mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        mainPanel.setPreferredSize(new Dimension(800, 600));

        // === Panel de Encabezado ===
        JPanel panelEncabezado = new JPanel();
        panelEncabezado.setLayout(new BoxLayout(panelEncabezado, BoxLayout.Y_AXIS));
        panelEncabezado.setBorder(new EmptyBorder(0, 0, 20, 0));

        JLabel lblTitulo = new JLabel("SISTEMA DE LICENCIAS DE CONDUCIR - ECUADOR");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 24));
        lblTitulo.setForeground(new Color(40, 60, 100));
        lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblSubtitulo = new JLabel("Agencia Nacional de Tránsito");
        lblSubtitulo.setFont(new Font("Arial", Font.PLAIN, 14));
        lblSubtitulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        panelEncabezado.add(lblTitulo);
        panelEncabezado.add(Box.createVerticalStrut(5));
        panelEncabezado.add(lblSubtitulo);

        // === Panel de Módulos (Botones) ===
        JPanel panelModulos = new JPanel();
        panelModulos.setLayout(new GridLayout(4, 2, 15, 15));
        panelModulos.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.GRAY),
                "Módulos del Sistema",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 12)
        ));
        ((EmptyBorder) BorderFactory.createEmptyBorder(10, 10, 10, 10)).getBorderInsets(panelModulos);
        panelModulos.setBorder(BorderFactory.createCompoundBorder(
                panelModulos.getBorder(),
                new EmptyBorder(10, 10, 10, 10)
        ));

        // Crear botones
        btnGestionConductores = crearBoton("Gestión de Conductores");
        btnValidarDocumentos = crearBoton("Validar Documentos");
        btnPruebasPsicometricas = crearBoton("Pruebas Psicométricas");
        btnEmitirLicencia = crearBoton("Emitir Licencia");
        btnConsultarLicencias = crearBoton("Consultar Licencias");
        btnGenerarDocumento = crearBoton("Generar Documento PDF");
        btnSalir = crearBoton("Salir");

        // Agregar botones al panel
        panelModulos.add(btnGestionConductores);
        panelModulos.add(btnValidarDocumentos);
        panelModulos.add(btnPruebasPsicometricas);
        panelModulos.add(btnEmitirLicencia);
        panelModulos.add(btnConsultarLicencias);
        panelModulos.add(btnGenerarDocumento);
        panelModulos.add(btnSalir);
        panelModulos.add(new JLabel()); // Celda vacía para balancear

        // === Panel de Pie de Página ===
        JPanel panelPie = new JPanel();
        panelPie.setBorder(new EmptyBorder(10, 0, 0, 0));

        JLabel lblVersion = new JLabel("Sistema de Licencias v1.0 - Desarrollado con Java y MySQL");
        lblVersion.setFont(new Font("Arial", Font.PLAIN, 10));
        lblVersion.setForeground(Color.GRAY);
        panelPie.add(lblVersion);

        // Agregar paneles al panel principal
        mainPanel.add(panelEncabezado, BorderLayout.NORTH);
        mainPanel.add(panelModulos, BorderLayout.CENTER);
        mainPanel.add(panelPie, BorderLayout.SOUTH);
    }

    /**
     * Crea un botón con estilo uniforme
     *
     * @param texto Texto del botón
     * @return JButton configurado
     */
    private JButton crearBoton(String texto) {
        JButton boton = new JButton(texto);
        boton.setFont(new Font("Arial", Font.PLAIN, 14));
        boton.setPreferredSize(new Dimension(150, 80));
        boton.setFocusPainted(false);
        return boton;
    }

    /**
     * Configura los eventos de los botones del menú principal
     */
    private void configurarEventos() {
        // Botón Gestión de Conductores
        btnGestionConductores.addActionListener(e -> abrirGestionConductores());

        // Botón Validar Documentos
        btnValidarDocumentos.addActionListener(e -> abrirValidarDocumentos());

        // Botón Pruebas Psicométricas
        btnPruebasPsicometricas.addActionListener(e -> abrirPruebasPsicometricas());

        // Botón Emitir Licencia
        btnEmitirLicencia.addActionListener(e -> abrirEmitirLicencia());

        // Botón Consultar Licencias
        btnConsultarLicencias.addActionListener(e -> abrirConsultarLicencias());

        // Botón Generar Documento PDF
        btnGenerarDocumento.addActionListener(e -> generarDocumentoPDF());

        // Botón Salir
        btnSalir.addActionListener(e -> salirAplicacion());
    }

    /**
     * Configura los estilos adicionales de los componentes
     */
    private void configurarEstilos() {
        // Estilo del botón de salir (color rojo)
        btnSalir.setBackground(new Color(220, 53, 69));
        btnSalir.setForeground(Color.WHITE);
        btnSalir.setOpaque(true);
        btnSalir.setBorderPainted(false);

        // Cursor de mano para todos los botones
        btnGestionConductores.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnValidarDocumentos.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnPruebasPsicometricas.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnEmitirLicencia.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnConsultarLicencias.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnGenerarDocumento.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSalir.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    /**
     * Abre la ventana de Gestión de Conductores
     */
    private void abrirGestionConductores() {
        try {
            GestionConductoresView gestionView = new GestionConductoresView(controller);
            gestionView.setVisible(true);
        } catch (Exception ex) {
            mostrarError("Error al abrir Gestión de Conductores: " + ex.getMessage());
        }
    }

    /**
     * Abre la ventana de Validar Documentos
     */
    private void abrirValidarDocumentos() {
        try {
            ValidarDocumentosView validarView = new ValidarDocumentosView(controller);
            validarView.setVisible(true);
        } catch (Exception ex) {
            mostrarError("Error al abrir Validar Documentos: " + ex.getMessage());
        }
    }

    /**
     * Abre la ventana de Pruebas Psicométricas
     */
    private void abrirPruebasPsicometricas() {
        try {
            PruebasPsicometricasView pruebasView = new PruebasPsicometricasView(controller);
            pruebasView.setVisible(true);
        } catch (Exception ex) {
            mostrarError("Error al abrir Pruebas Psicométricas: " + ex.getMessage());
        }
    }

    /**
     * Abre la ventana de Emitir Licencia
     */
    private void abrirEmitirLicencia() {
        try {
            EmitirLicenciaView emitirView = new EmitirLicenciaView(controller);
            emitirView.setVisible(true);
        } catch (Exception ex) {
            mostrarError("Error al abrir Emitir Licencia: " + ex.getMessage());
        }
    }

    /**
     * Abre la ventana de Consultar Licencias
     */
    private void abrirConsultarLicencias() {
        try {
            ConsultarLicenciasView consultarView = new ConsultarLicenciasView(controller);
            consultarView.setVisible(true);
        } catch (Exception ex) {
            mostrarError("Error al abrir Consultar Licencias: " + ex.getMessage());
        }
    }

    /**
     * Genera un documento PDF de una licencia seleccionada
     */
    private void generarDocumentoPDF() {
        try {
            // Solicitar cédula del conductor
            String cedula = JOptionPane.showInputDialog(
                    this,
                    "Ingrese la cédula del conductor para generar el PDF:",
                    "Generar Documento PDF",
                    JOptionPane.QUESTION_MESSAGE
            );

            if (cedula == null || cedula.trim().isEmpty()) {
                return; // Usuario canceló
            }

            // Buscar conductor
            Conductor conductor = controller.buscarConductorPorCedula(cedula.trim());
            if (conductor == null) {
                mostrarError("No se encontró un conductor con la cédula: " + cedula);
                return;
            }

            // Buscar licencias del conductor
            List<Licencia> licencias = controller.obtenerLicenciasConductor(conductor.getId());
            if (licencias == null || licencias.isEmpty()) {
                mostrarError("El conductor no tiene licencias emitidas.");
                return;
            }

            // Si hay múltiples licencias, usar la más reciente
            Licencia licencia = licencias.get(0);

            // Obtener última prueba aprobada (puede ser null)
            PruebaPsicometrica prueba = controller.obtenerUltimaPruebaAprobada(conductor.getId());

            // Seleccionar ubicación para guardar
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Guardar Licencia PDF");
            fileChooser.setSelectedFile(new File("Licencia_" + conductor.getCedula() + ".pdf"));

            int resultado = fileChooser.showSaveDialog(this);
            if (resultado == JFileChooser.APPROVE_OPTION) {
                String rutaArchivo = fileChooser.getSelectedFile().getAbsolutePath();

                // Asegurar extensión .pdf
                if (!rutaArchivo.toLowerCase().endsWith(".pdf")) {
                    rutaArchivo += ".pdf";
                }

                // Generar PDF
                PDFGenerator.generarLicenciaPDF(licencia, conductor, prueba, rutaArchivo);

                mostrarExito("Documento PDF generado exitosamente:\n" + rutaArchivo);

                // Preguntar si desea abrir el archivo
                int abrir = JOptionPane.showConfirmDialog(
                        this,
                        "¿Desea abrir el documento generado?",
                        "Abrir PDF",
                        JOptionPane.YES_NO_OPTION
                );

                if (abrir == JOptionPane.YES_OPTION) {
                    Desktop.getDesktop().open(new File(rutaArchivo));
                }
            }

        } catch (LicenciaException ex) {
            mostrarError("Error al generar PDF: " + ex.getMessage());
        } catch (Exception ex) {
            mostrarError("Error inesperado: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    /**
     * Cierra la aplicación con confirmación
     */
    private void salirAplicacion() {
        int opcion = JOptionPane.showConfirmDialog(
                this,
                "¿Está seguro que desea salir del sistema?",
                "Confirmar Salida",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (opcion == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }

    /**
     * Muestra un mensaje de error
     *
     * @param mensaje Mensaje a mostrar
     */
    private void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(
                this,
                mensaje,
                "Error",
                JOptionPane.ERROR_MESSAGE
        );
    }

    /**
     * Muestra un mensaje de éxito
     *
     * @param mensaje Mensaje a mostrar
     */
    private void mostrarExito(String mensaje) {
        JOptionPane.showMessageDialog(
                this,
                mensaje,
                "Éxito",
                JOptionPane.INFORMATION_MESSAGE
        );
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
        mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayoutManager(3, 1, new Insets(20, 20, 20, 20), -1, -1));
        mainPanel.setMinimumSize(new Dimension(800, 600));
        mainPanel.setPreferredSize(new Dimension(800, 600));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 20, 0), -1, -1));
        mainPanel.add(panel1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, 1, null, null, null, 0, false));
        final JLabel label1 = new JLabel();
        Font label1Font = this.$$$getFont$$$("Arial", Font.BOLD, 24, label1.getFont());
        if (label1Font != null) label1.setFont(label1Font);
        label1.setForeground(new Color(-14121484));
        label1.setText("SISTEMA DE LICENCIAS DE CONDUCIR - ECUADOR");
        panel1.add(label1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label2 = new JLabel();
        Font label2Font = this.$$$getFont$$$("Arial", -1, 14, label2.getFont());
        if (label2Font != null) label2.setFont(label2Font);
        label2.setText("Agencia Nacional de Tránsito");
        panel1.add(label2, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayoutManager(4, 2, new Insets(10, 10, 10, 10), -1, -1));
        mainPanel.add(panel2, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        panel2.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black), "Módulos del Sistema", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        btnGestionConductores = new JButton();
        Font btnGestionConductoresFont = this.$$$getFont$$$("Arial", -1, 14, btnGestionConductores.getFont());
        if (btnGestionConductoresFont != null) btnGestionConductores.setFont(btnGestionConductoresFont);
        btnGestionConductores.setText("Gestión de Conductores");
        panel2.add(btnGestionConductores, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, 80), null, 0, false));
        btnValidarDocumentos = new JButton();
        Font btnValidarDocumentosFont = this.$$$getFont$$$("Arial", -1, 14, btnValidarDocumentos.getFont());
        if (btnValidarDocumentosFont != null) btnValidarDocumentos.setFont(btnValidarDocumentosFont);
        btnValidarDocumentos.setText("Validar Documentos");
        panel2.add(btnValidarDocumentos, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, 80), null, 0, false));
        btnPruebasPsicometricas = new JButton();
        Font btnPruebasPsicometricasFont = this.$$$getFont$$$("Arial", -1, 14, btnPruebasPsicometricas.getFont());
        if (btnPruebasPsicometricasFont != null) btnPruebasPsicometricas.setFont(btnPruebasPsicometricasFont);
        btnPruebasPsicometricas.setText("Pruebas Psicométricas");
        panel2.add(btnPruebasPsicometricas, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, 80), null, 0, false));
        btnEmitirLicencia = new JButton();
        Font btnEmitirLicenciaFont = this.$$$getFont$$$("Arial", -1, 14, btnEmitirLicencia.getFont());
        if (btnEmitirLicenciaFont != null) btnEmitirLicencia.setFont(btnEmitirLicenciaFont);
        btnEmitirLicencia.setText("Emitir Licencia");
        panel2.add(btnEmitirLicencia, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, 80), null, 0, false));
        btnConsultarLicencias = new JButton();
        Font btnConsultarLicenciasFont = this.$$$getFont$$$("Arial", -1, 14, btnConsultarLicencias.getFont());
        if (btnConsultarLicenciasFont != null) btnConsultarLicencias.setFont(btnConsultarLicenciasFont);
        btnConsultarLicencias.setText("Consultar Licencias");
        panel2.add(btnConsultarLicencias, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, 80), null, 0, false));
        btnGenerarDocumento = new JButton();
        Font btnGenerarDocumentoFont = this.$$$getFont$$$("Arial", -1, 14, btnGenerarDocumento.getFont());
        if (btnGenerarDocumentoFont != null) btnGenerarDocumento.setFont(btnGenerarDocumentoFont);
        btnGenerarDocumento.setText("Generar Documento PDF");
        panel2.add(btnGenerarDocumento, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, 80), null, 0, false));
        btnSalir = new JButton();
        btnSalir.setBackground(new Color(-2104859));
        Font btnSalirFont = this.$$$getFont$$$("Arial", Font.BOLD, 14, btnSalir.getFont());
        if (btnSalirFont != null) btnSalir.setFont(btnSalirFont);
        btnSalir.setForeground(new Color(-1));
        btnSalir.setText("Salir");
        panel2.add(btnSalir, new GridConstraints(3, 0, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, 50), null, 0, false));
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new GridLayoutManager(1, 1, new Insets(10, 0, 0, 0), -1, -1));
        mainPanel.add(panel3, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, 1, null, null, null, 0, false));
        final JLabel label3 = new JLabel();
        Font label3Font = this.$$$getFont$$$("Arial", -1, 10, label3.getFont());
        if (label3Font != null) label3.setFont(label3Font);
        label3.setForeground(new Color(-6710887));
        label3.setText("Sistema de Licencias v1.0 - Desarrollado con Java y MySQL");
        panel3.add(label3, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
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
        return mainPanel;
    }
}
