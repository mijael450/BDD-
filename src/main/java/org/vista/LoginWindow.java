package org.vista;

import org.config.ConexionSQL;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginWindow extends JFrame {

    // Componentes como campos de instancia para mejor acceso
    private JTextField txtCedula;
    private JPasswordField txtPassword;
    private JButton btnLogin;
    private JComboBox<String> comboRol;
    private String admin_id="123";
    private String admin_name="admin";
    public LoginWindow() {
        initializeWindow();
        createComponents();
        setupLayout();
        addEventListeners();
    }

    private void initializeWindow() {
        setTitle("Sistema Hospitalario Médico");
        setSize(900, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
    }

    private void createComponents() {
        // Panel principal dividido en izquierda y derecha
        JPanel mainPanel = new JPanel(new GridLayout(1, 2));
        add(mainPanel, BorderLayout.CENTER);

        // Panel izquierdo
        JPanel leftPanel = createLeftPanel();
        mainPanel.add(leftPanel);

        // Panel derecho (login)
        JPanel rightPanel = createRightPanel();
        mainPanel.add(rightPanel);
    }

    private JPanel createLeftPanel() {
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setBackground(new Color(0, 53, 84));
        leftPanel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));

        JLabel titulo = new JLabel("SISTEMA HOSPITALARIO MÉDICO");
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        titulo.setFont(new Font("Arial", Font.BOLD, 20));
        titulo.setForeground(new Color(255, 255, 255));
        leftPanel.add(titulo);
        leftPanel.add(Box.createRigidArea(new Dimension(0, 40)));

        // Imagen con manejo de errores
        try {
            JLabel imagePlaceholder = new JLabel(escalarImagen("/pngs/hospital_icon.png", 180, 180));
            imagePlaceholder.setAlignmentX(Component.CENTER_ALIGNMENT);
            imagePlaceholder.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
            leftPanel.add(imagePlaceholder);
        } catch (Exception e) {
            System.err.println("No se pudo cargar la imagen: " + e.getMessage());
            // Placeholder alternativo si la imagen no se encuentra
            JLabel placeholder = new JLabel("🏥");
            placeholder.setAlignmentX(Component.CENTER_ALIGNMENT);
            placeholder.setFont(new Font("Arial", Font.PLAIN, 80));
            leftPanel.add(placeholder);
        }

        return leftPanel;
    }

    private JPanel createRightPanel() {
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(null);
        rightPanel.setBackground(new Color(157, 209, 241));

        // Campos de login
        JLabel lblCedula = new JLabel("Nombre:");
        lblCedula.setBounds(50, 80, 100, 30);
        lblCedula.setFont(new Font("Arial", Font.BOLD, 14));
        rightPanel.add(lblCedula);

        txtCedula = new JTextField();
        txtCedula.setBounds(150, 80, 200, 30);
        txtCedula.setFont(new Font("Arial", Font.PLAIN, 12));
        rightPanel.add(txtCedula);

        JLabel lblPassword = new JLabel("Cédula:");
        lblPassword.setBounds(50, 130, 100, 30);
        lblPassword.setFont(new Font("Arial", Font.BOLD, 14));
        rightPanel.add(lblPassword);

        txtPassword = new JPasswordField();
        txtPassword.setBounds(150, 130, 200, 30);
        txtPassword.setFont(new Font("Arial", Font.PLAIN, 12));
        rightPanel.add(txtPassword);

        // Combo box para iniciar como paciente o administrador 
        comboRol = new JComboBox<>(new String[]{"Paciente", "Administrador"});
        comboRol.setBounds(150, 165, 200, 25);
        comboRol.setFont(new Font("Arial", Font.PLAIN, 14));
        comboRol.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        rightPanel.add(comboRol);
        
        //Boton de login
        btnLogin = new JButton("Iniciar Sesión");
        btnLogin.setBounds(150, 200, 200, 35);
        btnLogin.setBackground(new Color(255, 255, 255));
        btnLogin.setForeground(Color.BLACK); // Texto blanco para mejor contraste
        btnLogin.setFocusPainted(false);
        btnLogin.setFont(new Font("Arial", Font.BOLD, 14));
        rightPanel.add(btnLogin);

        // Link de registro
        JLabel lblRegistrar = new JLabel("<html><u>¿No tienes cuenta? Regístrate</u></html>");
        lblRegistrar.setBounds(165, 250, 300, 30);
        lblRegistrar.setForeground(new Color(0, 72, 151));
        lblRegistrar.setFont(new Font("Arial", Font.BOLD, 12));
        lblRegistrar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        rightPanel.add(lblRegistrar);

        // Link cambiar sede
        JLabel lblCambiarSede = new JLabel("<html><u>Cambiar sede</u></html>");
        lblCambiarSede.setBounds(280, 420, 100, 20);
        lblCambiarSede.setForeground(Color.BLUE);
        lblCambiarSede.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        lblCambiarSede.setFont(new Font("Arial", Font.PLAIN, 12));
        rightPanel.add(lblCambiarSede);

        // Evento de registro
        lblRegistrar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                openRegisterWindow();
            }
        });

        // Evento de cambio de sede
        lblCambiarSede.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                openSedeSelectionWindow();
            }
        });

        // Responsive positioning para cambiar sede
        rightPanel.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                lblCambiarSede.setBounds(rightPanel.getWidth() - 120, rightPanel.getHeight() - 40, 100, 20);
            }
        });

        return rightPanel;
    }

    private void setupLayout() {
        // Ya implementado en createComponents()
    }

    private void addEventListeners() {
        // Login button action
        btnLogin.addActionListener(new LoginActionListener());

        // Enter key para login
        txtPassword.addActionListener(e -> performLogin());
        txtCedula.addActionListener(e -> txtPassword.requestFocus());
    }

    private void performLogin() {
        String nombre = txtCedula.getText().trim();
        String cedula = new String(txtPassword.getPassword()).trim();

        if (nombre.isEmpty() || cedula.isEmpty()) {
            showMessage("Por favor ingrese nombre y cédula.", "Campos vacíos", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Mostrar indicador de carga
        btnLogin.setText("Validando...");
        btnLogin.setEnabled(false);

        // Ejecutar login en hilo separado para no bloquear la UI
        SwingWorker<Boolean, Void> loginWorker = new SwingWorker<Boolean, Void>() {
            private String userType = "";
            private String userName = "";

            @Override
            protected Boolean doInBackground() throws Exception {
                return authenticateUser(nombre, cedula);
            }

            @Override
            protected void done() {
                btnLogin.setText("Iniciar Sesión");
                btnLogin.setEnabled(true);

                try {
                    if (get()) {
                        showMessage("Bienvenido " + userType + ": " + userName, "Acceso concedido", JOptionPane.INFORMATION_MESSAGE);
                        openUserWindow(userType);
                    } else {
                        showMessage("Credenciales inválidas o usuario no registrado.", "Acceso denegado", JOptionPane.ERROR_MESSAGE);
                        clearFields();
                    }
                } catch (Exception e) {
                    showMessage("Error durante el login: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    e.printStackTrace();
                }
            }

            private Boolean authenticateUser(String nombre, String cedula) {
                try (Connection conn = ConexionSQL.conectar()) {
                    
                    // Realiza el login en base al rol elegido en el combo box (Administrador existe internamente)
                    
                    //Login si es Administrador (Internamente)
                    if(comboRol.getSelectedItem().toString().equalsIgnoreCase("Administrador")){
                        if(nombre.equalsIgnoreCase(admin_name)&& cedula.equalsIgnoreCase(admin_id)){
                            userName = "Admin";
                            userType = "Administrador";
                            return true;
                            }
                    }
                    
                    
                    
                    //  login como PACIENTE
                    if(comboRol.getSelectedItem().toString().equalsIgnoreCase("Paciente")){
                    String sql = "SELECT NOMBRE FROM PACIENTE_Q WHERE NOMBRE = ? AND CEDULA = ?";
                    try (PreparedStatement ps = conn.prepareStatement(sql)) {
                        ps.setString(1, nombre);
                        ps.setString(2, cedula);
                        try (ResultSet rs = ps.executeQuery()) {
                            if (rs.next()) {
                                userName = rs.getString("NOMBRE");
                                userType = "paciente";
                                return true;
                            }
                        }
                    }
                }
                    return false;
                    
                } catch (SQLException e) {
                    throw new RuntimeException("Error de base de datos: " + e.getMessage(), e);
                }
            }
        };

        loginWorker.execute();
    }

    private void openUserWindow(String userType) {
        SwingUtilities.invokeLater(() -> {
            try {
                switch (userType.toLowerCase()) {
                    case "administrador":
                        new AdminWindow().setVisible(true);
                        break;
                    case "doctor":
                        new DoctorWindow().setVisible(true);
                        break;
                    case "paciente":
                        new PacienteWindow().setVisible(true);
                        break;
                    default:
                        showMessage("Tipo de usuario no reconocido", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                }
                dispose();
            } catch (Exception e) {
                showMessage("Error al abrir la ventana: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        });
    }

    private void openRegisterWindow() {
        try {
            new PacienteRegister().setVisible(true);
            dispose();
        } catch (Exception e) {
            showMessage("Error al abrir ventana de registro: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void openSedeSelectionWindow() {
        try {
            new SeleccionSede().setVisible(true);
            dispose();
        } catch (Exception e) {
            showMessage("Error al abrir selección de sede: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearFields() {
        txtCedula.setText("");
        txtPassword.setText("");
        txtCedula.requestFocus();
    }

    private void showMessage(String message, String title, int messageType) {
        JOptionPane.showMessageDialog(this, message, title, messageType);
    }

    private ImageIcon escalarImagen(String ruta, int ancho, int alto) {
        try {
            ImageIcon iconoOriginal = new ImageIcon(getClass().getResource(ruta));
            if (iconoOriginal.getIconWidth() == -1) {
                throw new IllegalArgumentException("Imagen no encontrada: " + ruta);
            }
            Image imagenOriginal = iconoOriginal.getImage();
            Image imagenEscalada = imagenOriginal.getScaledInstance(ancho, alto, Image.SCALE_SMOOTH);
            return new ImageIcon(imagenEscalada);
        } catch (Exception e) {
            System.err.println("Error cargando imagen " + ruta + ": " + e.getMessage());
            // Retornar un icono por defecto o lanzar excepción
            throw e;
        }
    }

    // Clase interna para manejar el evento de login
    private class LoginActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            performLogin();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                System.err.println("No se pudo establecer Look and Feel: " + e.getMessage());
            }
            new SeleccionSede().setVisible(true);
        });
    }
}