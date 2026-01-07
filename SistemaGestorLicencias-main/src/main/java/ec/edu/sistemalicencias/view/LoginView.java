package ec.edu.sistemalicencias.view;

import ec.edu.sistemalicencias.config.DatabaseConfig;
import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class LoginView extends JFrame {
    private JTextField txtUser;
    private JPasswordField txtPass;
    private JButton btnLogin;

    public LoginView() {
        setTitle("Login");
        setSize(300, 200);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(4, 1, 10, 10));

        add(new JLabel("Usuario:", SwingConstants.CENTER));
        txtUser = new JTextField(); add(txtUser);

        add(new JLabel("Contraseña:", SwingConstants.CENTER));
        txtPass = new JPasswordField(); add(txtPass);

        btnLogin = new JButton("Ingresar");
        btnLogin.setBackground(new Color(40, 60, 100));
        btnLogin.setForeground(Color.WHITE);
        add(btnLogin);

        btnLogin.addActionListener(e -> validar());
    }

    private void validar() {
        String u = txtUser.getText();
        String p = new String(txtPass.getPassword());

        try (Connection conn = DatabaseConfig.getInstance().obtenerConexion();
             PreparedStatement ps = conn.prepareStatement("SELECT rol FROM usuarios WHERE username=? AND password=?")) {

            ps.setString(1, u);
            ps.setString(2, p);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String rol = rs.getString("rol");
                JOptionPane.showMessageDialog(this, "Bienvenido " + rol);
                new MainView(rol).setVisible(true); // Pasamos el rol
                this.dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Datos incorrectos");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        new LoginView().setVisible(true);
    }
}