package ec.edu.sistemalicencias.view;

import ec.edu.sistemalicencias.config.DatabaseConfig;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class LoginView extends JFrame {
    private JTextField txtUser;
    private JPasswordField txtPass;
    private JButton btnLogin;

    public LoginView() {
        
        setTitle("Acceso al Sistema");
        setSize(750, 450); // Hacemos la ventana más grande y panorámica
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        setLayout(new BorderLayout());

   
        JPanel pnlIzq = new JPanel();
        pnlIzq.setBackground(new Color(41, 128, 185)); // Azul Institucional
        pnlIzq.setPreferredSize(new Dimension(300, 450));
        pnlIzq.setLayout(new BoxLayout(pnlIzq, BoxLayout.Y_AXIS));

       
        pnlIzq.add(Box.createVerticalGlue()); 

        JLabel lblTitulo = new JLabel("SISTEMA DE");
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        pnlIzq.add(lblTitulo);

        JLabel lblSubtitulo = new JLabel("LICENCIAS");
        lblSubtitulo.setForeground(Color.WHITE);
        lblSubtitulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblSubtitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        pnlIzq.add(lblSubtitulo);

        JLabel lblVersion = new JLabel("");
        lblVersion.setForeground(new Color(200, 200, 200));
        lblVersion.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblVersion.setAlignmentX(Component.CENTER_ALIGNMENT);
        pnlIzq.add(Box.createRigidArea(new Dimension(0, 10)));
        pnlIzq.add(lblVersion);

        pnlIzq.add(Box.createVerticalGlue()); 
        
        JPanel pnlDer = new JPanel();
        pnlDer.setBackground(Color.WHITE);
        pnlDer.setLayout(null); 
        
        JLabel lblLogin = new JLabel("INICIAR SESIÓN");
        lblLogin.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblLogin.setForeground(new Color(50, 50, 50));
        lblLogin.setBounds(50, 50, 200, 30);
        pnlDer.add(lblLogin);

       
        JLabel lblUser = new JLabel("Usuario:");
        lblUser.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblUser.setBounds(50, 100, 100, 20);
        pnlDer.add(lblUser);

        txtUser = new JTextField();
        txtUser.setBounds(50, 125, 300, 35);
        txtUser.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        txtUser.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                new EmptyBorder(5, 5, 5, 5)));
        pnlDer.add(txtUser);

        
        JLabel lblPass = new JLabel("Contraseña:");
        lblPass.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblPass.setBounds(50, 180, 100, 20);
        pnlDer.add(lblPass);

        txtPass = new JPasswordField();
        txtPass.setBounds(50, 205, 300, 35);
        txtPass.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                new EmptyBorder(5, 5, 5, 5)));
        pnlDer.add(txtPass);

        
        btnLogin = new JButton("INGRESAR");
        btnLogin.setBounds(50, 270, 300, 45);
        btnLogin.setBackground(new Color(41, 128, 185)); // Mismo azul del panel
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnLogin.setFocusPainted(false);
        btnLogin.setBorderPainted(false);
        btnLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));

        
        btnLogin.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnLogin.setBackground(new Color(31, 97, 141)); // Azul más oscuro
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btnLogin.setBackground(new Color(41, 128, 185)); // Azul original
            }
        });

        pnlDer.add(btnLogin);
        
        add(pnlIzq, BorderLayout.WEST);
        add(pnlDer, BorderLayout.CENTER);

       
        btnLogin.addActionListener(e -> validar());

        
        getRootPane().setDefaultButton(btnLogin);
    }

    private void validar() {
        String u = txtUser.getText();
        String p = new String(txtPass.getPassword());

        if (u.isEmpty() || p.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor ingrese usuario y contraseña", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

     
        try (Connection conn = DatabaseConfig.getInstance().obtenerConexion();
             PreparedStatement ps = conn.prepareStatement("SELECT rol FROM usuarios WHERE username=? AND password=?")) {

            ps.setString(1, u);
            ps.setString(2, p);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String rol = rs.getString("rol");
            
                new MainView(rol).setVisible(true); // Pasamos el rol a la vista principal
                this.dispose(); // Cerramos login
            } else {
                JOptionPane.showMessageDialog(this, "Credenciales incorrectas", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error de conexión: " + ex.getMessage(), "Error Crítico", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception ignored) {}

        new LoginView().setVisible(true);
    }
}
