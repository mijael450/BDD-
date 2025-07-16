/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vista;

import config.ConexionSQL;
import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class HomeWindow extends JFrame {

    public HomeWindow() {
        setTitle("Sistema Hospitalario Médico");
        setSize(900, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Panel principal dividido en izquierda y derecha
        JPanel mainPanel = new JPanel(new GridLayout(1, 2));
        add(mainPanel, BorderLayout.CENTER);

        // ----------------- PANEL IZQUIERDO -----------------
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setBackground(new Color(230, 230, 230)); // Gris claro
        leftPanel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));

        JLabel titulo = new JLabel("SISTEMA HOSPITALARIO MÉDICO");
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        titulo.setFont(new Font("Arial", Font.BOLD, 20));
        titulo.setForeground(new Color(50, 50, 50));
        leftPanel.add(titulo);
        leftPanel.add(Box.createRigidArea(new Dimension(0, 40)));

        // Espacio para imagen PNG
        JLabel imagePlaceholder = new JLabel(escalarImagen("/pngs/hospital_icon.png", 180, 180));
        imagePlaceholder.setAlignmentX(Component.CENTER_ALIGNMENT); // Centrado horizontal
        imagePlaceholder.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0)); // Márgenes opcionales
        leftPanel.add(imagePlaceholder);


        mainPanel.add(leftPanel);

        // ----------------- PANEL DERECHO (LOGIN) -----------------
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(null); // Posicionamiento absoluto
        rightPanel.setBackground(new Color(245, 245, 245));

        JLabel lblCedula = new JLabel("Cédula:");
        lblCedula.setBounds(50, 80, 100, 30);
        rightPanel.add(lblCedula);

        JTextField txtCedula = new JTextField();
        txtCedula.setBounds(150, 80, 200, 30);
        rightPanel.add(txtCedula);

        JLabel lblPassword = new JLabel("Contraseña:");
        lblPassword.setBounds(50, 130, 100, 30);
        rightPanel.add(lblPassword);

        JPasswordField txtPassword = new JPasswordField();
        txtPassword.setBounds(150, 130, 200, 30);
        rightPanel.add(txtPassword);

        JButton btnLogin = new JButton("Iniciar Sesión");
        btnLogin.setBounds(150, 190, 200, 35);
        btnLogin.setBackground(new Color(70, 130, 180));
        btnLogin.setForeground(Color.BLACK);
        btnLogin.setFocusPainted(false);
        btnLogin.setFont(new Font("Arial", Font.BOLD, 14)); // Texto en negrita
        rightPanel.add(btnLogin);

        btnLogin.addActionListener(e -> {
    String cedula = txtCedula.getText().trim();
    String contrasena = new String(txtPassword.getPassword()).trim();

    if (cedula.isEmpty() || contrasena.isEmpty()) {
        JOptionPane.showMessageDialog(this, "Por favor ingrese cédula y contraseña.", "Campos vacíos", JOptionPane.WARNING_MESSAGE);
        return;
    }

    try (Connection conn = ConexionSQL.conectar()) {

        // Intentar login como ADMINISTRADOR
        String sqlAdmin = "SELECT * FROM Administrador WHERE cedula = ? AND contrasena_admin = ?";
        PreparedStatement psAdmin = conn.prepareStatement(sqlAdmin);
        psAdmin.setString(1, cedula);
        psAdmin.setString(2, contrasena);
        ResultSet rsAdmin = psAdmin.executeQuery();

        if (rsAdmin.next()) {
            JOptionPane.showMessageDialog(this, "Bienvenido administrador: " + rsAdmin.getString("nombres"), "Acceso concedido", JOptionPane.INFORMATION_MESSAGE);
            new AdminWindow().setVisible(true);
            dispose();
            rsAdmin.close();
            psAdmin.close();
            return;
        }

        rsAdmin.close();
        psAdmin.close();

        // Intentar login como DOCTOR
        String sqlDoctor = "SELECT * FROM Doctor WHERE cedula = ? AND contrasena_doctor = ?";
        PreparedStatement psDoctor = conn.prepareStatement(sqlDoctor);
        psDoctor.setString(1, cedula);
        psDoctor.setString(2, contrasena);
        ResultSet rsDoctor = psDoctor.executeQuery();

        if (rsDoctor.next()) {
            JOptionPane.showMessageDialog(this, "Bienvenido doctor: " + rsDoctor.getString("nombres"), "Acceso concedido", JOptionPane.INFORMATION_MESSAGE);
            new DoctorWindow().setVisible(true);
            dispose();
            rsDoctor.close();
            psDoctor.close();
            return;
        }

        rsDoctor.close();
        psDoctor.close();

        // Intentar login como PACIENTE
        String sqlPaciente = "SELECT * FROM Paciente WHERE cedula = ? AND contrasena_paciente = ?";
        PreparedStatement psPaciente = conn.prepareStatement(sqlPaciente);
        psPaciente.setString(1, cedula);
        psPaciente.setString(2, contrasena);
        ResultSet rsPaciente = psPaciente.executeQuery();

        if (rsPaciente.next()) {
            JOptionPane.showMessageDialog(this, "Bienvenido paciente: " + rsPaciente.getString("nombres"), "Acceso concedido", JOptionPane.INFORMATION_MESSAGE);
            new PacienteWindow().setVisible(true);
            dispose();
            rsPaciente.close();
            psPaciente.close();
            return;
        }

        rsPaciente.close();
        psPaciente.close();

        // Si todos fallan:
        JOptionPane.showMessageDialog(this, "Credenciales inválidas o usuario no registrado.", "Acceso denegado", JOptionPane.ERROR_MESSAGE);

    } catch (SQLException ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(this, "Error al conectar a la base de datos:\n" + ex.getMessage(), "Error de conexión", JOptionPane.ERROR_MESSAGE);
    }
});




        JLabel lblRegistrar = new JLabel("¿No tienes cuenta? Regístrate");
        lblRegistrar.setBounds(165, 250, 300, 30);
        lblRegistrar.setForeground(new Color(0, 102, 204));
        lblRegistrar.setFont(new Font("Arial", Font.BOLD, 12));
        rightPanel.add(lblRegistrar);

        // Evento al hacer clic en "REGISTRARSE"
        lblRegistrar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        lblRegistrar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                new RegisterWindow().setVisible(true);
                dispose();
            }
        });

        mainPanel.add(rightPanel);
        JLabel lblCambiarSede = new JLabel("Cambiar sede");
        lblCambiarSede.setBounds(rightPanel.getWidth() - 120, rightPanel.getHeight() - 40, 100, 20);
        lblCambiarSede.setForeground(Color.BLUE);
        lblCambiarSede.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        lblCambiarSede.setFont(new Font("Arial", Font.PLAIN, 12));
        rightPanel.add(lblCambiarSede);
        
        rightPanel.addComponentListener(new java.awt.event.ComponentAdapter() {
        public void componentResized(java.awt.event.ComponentEvent evt) {
            lblCambiarSede.setBounds(rightPanel.getWidth() - 120, rightPanel.getHeight() - 40, 100, 20);
        }
    });

        lblCambiarSede.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                new SeleccionSede().setVisible(true);
                dispose();
    }
});

    }
    
    

    public static void main(String[] args) {
    SwingUtilities.invokeLater(() -> {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        new SeleccionSede().setVisible(true);
    });
    
}private ImageIcon escalarImagen(String ruta, int ancho, int alto) {
    ImageIcon iconoOriginal = new ImageIcon(getClass().getResource(ruta));
    Image imagenOriginal = iconoOriginal.getImage();
    Image imagenEscalada = imagenOriginal.getScaledInstance(ancho, alto, Image.SCALE_SMOOTH);
    return new ImageIcon(imagenEscalada);

}}

