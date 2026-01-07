package ec.edu.sistemalicencias;

import ec.edu.sistemalicencias.config.DatabaseConfig;
import ec.edu.sistemalicencias.view.LoginView; // <--- OJO: Importamos el Login, no el MainView
import javax.swing.*;

public class Main {

    public static void main(String[] args) {
        // 1. Configurar el estilo visual (para que se vea bonito en Windows)
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.err.println("No se pudo establecer el Look and Feel: " + e.getMessage());
        }

        // 2. Preparar la configuración de Base de Datos
        DatabaseConfig dbConfig = DatabaseConfig.getInstance();

        // 3. Iniciar la interfaz gráfica
        SwingUtilities.invokeLater(() -> {

            // Mensaje de bienvenida (Splash)
            mostrarPantallaInicio();

            // Verificar conexión a Railway antes de abrir nada
            if (!dbConfig.verificarConexion()) {
                mostrarErrorConexion();
                return; // Si falla, se detiene aquí
            }

            // --- AQUÍ ESTABA EL ERROR ---
            // Ya no abrimos MainView directamente. Abrimos LoginView.
            LoginView login = new LoginView();
            login.setVisible(true);
        });
    }

    private static void mostrarPantallaInicio() {
        JOptionPane.showMessageDialog(
                null,
                "SISTEMA DE LICENCIAS - VERSIÓN CLOUD\n\n" +
                        "Conectando a Railway (PostgreSQL)...\n" +
                        "Por favor espere un momento.",
                "Iniciando Sistema",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    private static void mostrarErrorConexion() {
        JOptionPane.showMessageDialog(
                null,
                "ERROR CRÍTICO: No se pudo conectar a la nube (Railway).\n\n" +
                        "Verifique su internet o las credenciales en DatabaseConfig.",
                "Error de Conexión",
                JOptionPane.ERROR_MESSAGE
        );
        System.exit(1);
    }
}
