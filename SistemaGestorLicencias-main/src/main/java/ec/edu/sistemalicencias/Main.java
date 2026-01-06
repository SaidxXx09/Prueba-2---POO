
package ec.edu.sistemalicencias;

import ec.edu.sistemalicencias.config.DatabaseConfig;
import ec.edu.sistemalicencias.view.MainView;

import javax.swing.*;

/**
 * Clase principal del Sistema de Licencias de Conducir del Ecuador.
 * Punto de entrada de la aplicación.
 *
 * @author Sistema Licencias Ecuador
 * @version 1.0
 */
public class Main {

    /**
     * Método principal que inicia la aplicación
     */
    public static void main(String[] args) {
        // Configurar Look and Feel del sistema operativo
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.err.println("No se pudo establecer el Look and Feel: " + e.getMessage());
        }

        // Verificar conexión a base de datos
        DatabaseConfig dbConfig = DatabaseConfig.getInstance();

        SwingUtilities.invokeLater(() -> {
            // Mostrar splash screen o mensaje de inicio
            mostrarPantallaInicio();

            // Verificar conexión a BD
            if (!dbConfig.verificarConexion()) {
                mostrarErrorConexion();
                return;
            }

            // Iniciar ventana principal
            MainView mainView = new MainView();
            mainView.setVisible(true);
        });
    }

    /**
     * Muestra una pantalla de inicio con información del sistema
     */
    private static void mostrarPantallaInicio() {
        JOptionPane.showMessageDialog(
                null,
                "SISTEMA DE LICENCIAS DE CONDUCIR - ECUADOR\n\n" +
                        "Agencia Nacional de Tránsito\n" +
                        "Versión 1.0\n\n" +
                        "Desarrollado con:\n" +
                        "- Java 21\n" +
                        "- MySQL Database\n" +
                        "- Arquitectura MVC\n" +
                        "- iText PDF\n\n" +
                        "Iniciando sistema...",
                "Bienvenido",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    /**
     * Muestra un mensaje de error si no se puede conectar a la base de datos
     */
    private static void mostrarErrorConexion() {
        String mensaje = "ERROR DE CONEXIÓN A BASE DE DATOS\n\n" +
                "No se pudo establecer conexión con MySQL.\n\n" +
                "Verifique que:\n" +
                "1. MySQL Server esté ejecutándose\n" +
                "2. La base de datos 'sistema_licencias' exista\n" +
                "3. Las credenciales sean correctas (usuario: root, password: root)\n" +
                "4. El servidor esté en localhost:3306\n\n" +
                "Para crear la base de datos, ejecute el script:\n" +
                "src/main/resources/schema.sql\n\n" +
                "La aplicación se cerrará.";

        JOptionPane.showMessageDialog(
                null,
                mensaje,
                "Error de Conexión",
                JOptionPane.ERROR_MESSAGE
        );

        System.exit(1);
    }
}
