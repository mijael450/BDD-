package org.vista;

import org.config.ConexionSQL;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.awt.event.MouseEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import org.config.Config;  // ✅ correcto



public class LoginWindow extends JFrame {

    private JTextField txtCedula;
    private JPasswordField txtPassword;
    private JButton btnLogin;
    private JComboBox<String> comboRol;
    private String admin_id="123";
    private String admin_name="admin";
    private String userName;
    private String userpass;
    private String userType;
    private String sedeSelect;
    
    public LoginWindow(String sedeSelect) {
        initializeWindow();
        createComponents();
        addEventListeners();
        this.sedeSelect = sedeSelect;
    }

    private void initializeWindow() {
        setTitle("Sistema Hospitalario Médico");
        setSize(900, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
    }

    private void createComponents() {
        JPanel mainPanel = new JPanel(new GridLayout(1, 2));
        add(mainPanel, BorderLayout.CENTER);

        mainPanel.add(createLeftPanel());
        mainPanel.add(createRightPanel());
    }

    private JPanel createLeftPanel() {
       
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setBackground(new Color(0, 53, 84));
        leftPanel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));

        JLabel titulo = new JLabel("SISTEMA HOSPITALARIO MÉDICO");
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        titulo.setFont(new Font("Arial", Font.BOLD, 20));
        titulo.setForeground(Color.WHITE);
        leftPanel.add(titulo);
        leftPanel.add(Box.createRigidArea(new Dimension(0, 40)));

        try {
            JLabel image = new JLabel(escalarImagen("/pngs/hospital_icon.png", 180, 180));
            image.setAlignmentX(Component.CENTER_ALIGNMENT);
            leftPanel.add(image);
        } catch (Exception e) {
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

        JLabel lblNombre = new JLabel("Nombre:");
        lblNombre.setBounds(50, 80, 100, 30);
        lblNombre.setFont(new Font("Arial", Font.BOLD, 14));
        rightPanel.add(lblNombre);

        txtCedula = new JTextField();
        txtCedula.setBounds(150, 80, 200, 30);
        rightPanel.add(txtCedula);

        JLabel lblCedula = new JLabel("Cédula:");
        lblCedula.setBounds(50, 130, 100, 30);
        lblCedula.setFont(new Font("Arial", Font.BOLD, 14));
        rightPanel.add(lblCedula);

        txtPassword = new JPasswordField();
        txtPassword.setBounds(150, 130, 200, 30);
        rightPanel.add(txtPassword);

        // Combo box para iniciar como paciente o administrador 
        comboRol = new JComboBox<>(new String[]{"Paciente", "Administrador"});
        comboRol.setBounds(150, 165, 200, 25);
        comboRol.setFont(new Font("Arial", Font.PLAIN, 14));
        comboRol.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        rightPanel.add(comboRol);
        
        //Boton de login
        btnLogin = new JButton("Iniciar Sesión");

        btnLogin.setBackground(new Color(255, 255, 255));
        btnLogin.setForeground(Color.BLACK); // Texto blanco para mejor contraste
        btnLogin.setFocusPainted(false);

        btnLogin.setBounds(150, 190, 200, 35);

        btnLogin.setFont(new Font("Arial", Font.BOLD, 14));
        rightPanel.add(btnLogin);

        JLabel lblRegistrar = new JLabel("<html><u>¿No tienes cuenta? Regístrate</u></html>");
        lblRegistrar.setBounds(165, 250, 300, 30);
        lblRegistrar.setForeground(new Color(0, 72, 151));
        lblRegistrar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        rightPanel.add(lblRegistrar);

        JLabel lblCambiarSede = new JLabel("<html><u>Cambiar sede</u></html>");
        lblCambiarSede.setBounds(280, 420, 100, 20);
        lblCambiarSede.setForeground(Color.BLUE);
        lblCambiarSede.setFont(new Font("Arial", Font.PLAIN, 12));
        lblCambiarSede.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        rightPanel.add(lblCambiarSede);

        lblRegistrar.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                openRegisterWindow();
            }
        });

        lblCambiarSede.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                openSedeSelectionWindow();
            }
        });

        rightPanel.addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent evt) {
                lblCambiarSede.setBounds(rightPanel.getWidth() - 120, rightPanel.getHeight() - 40, 100, 20);
            }
        });

        return rightPanel;
    }

    private void addEventListeners() {
        btnLogin.addActionListener(new LoginActionListener());
        txtPassword.addActionListener(e -> performLogin());
        txtCedula.addActionListener(e -> txtPassword.requestFocus());
    }

    private void performLogin() {
        String nombreCompleto = txtCedula.getText().trim();
        String cedula = new String(txtPassword.getPassword()).trim();

        if (nombreCompleto.isEmpty() || cedula.isEmpty()) {
            showMessage("Por favor ingrese nombre y cédula.", "Campos vacíos", JOptionPane.WARNING_MESSAGE);
            return;
        }

        btnLogin.setText("Validando...");
        btnLogin.setEnabled(false);

        SwingWorker<Boolean, Void> loginWorker = new SwingWorker<>() {
            
            @Override
            protected Boolean doInBackground() {
                return authenticateUser(nombreCompleto, cedula);
            }

            

            @Override
            protected void done() {
                btnLogin.setText("Iniciar Sesión");
                btnLogin.setEnabled(true);
                try {
                    if (get()) {
                        showMessage("Bienvenido: " + nombreCompleto, "Acceso concedido", JOptionPane.INFORMATION_MESSAGE);
                        openUserWindow(userType);
                    } else {
                        showMessage("Credenciales inválidas.", "Acceso denegado", JOptionPane.ERROR_MESSAGE);
                        clearFields();
                    }
                } catch (Exception e) {
                    showMessage("Error durante el login: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    e.printStackTrace();
                }
            }


            private Boolean authenticateUser(String nombre, String cedula) {
                try (Connection conn = ConexionSQL.conectar()) {
                     String tableSuffix = LoginWindow.this.sedeSelect.equalsIgnoreCase("QUITO") ? "Q" : "G";
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
                    String sql = "SELECT NOMBRE FROM PACIENTE_"+ tableSuffix +" WHERE NOMBRE = ? AND CEDULA = ?";
                    try (PreparedStatement ps = conn.prepareStatement(sql)) {
                        ps.setString(1, nombre);
                        ps.setString(2, cedula);
                        try (ResultSet rs = ps.executeQuery()) {
                            if (rs.next()) {
                                userName = rs.getString("NOMBRE");
                                userType = "paciente";
                                userpass = cedula;
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

    private void openUserWindow(String type) {
        SwingUtilities.invokeLater(() -> {
            try {
                if(type.equalsIgnoreCase("paciente")){
                    new PacienteWindow(userName, userpass,this.sedeSelect).setVisible(true);
                    dispose();
                }else{
                    new AdminWindow(this.sedeSelect).setVisible(true);
                    dispose();
                }
            } catch (Exception e) {
                showMessage("Error al abrir la ventana del paciente: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    private void openRegisterWindow() {
        try {
            new PacienteRegister(this.sedeSelect).setVisible(true);
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

    private void showMessage(String message, String title, int type) {
        JOptionPane.showMessageDialog(this, message, title, type);
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
            throw e;
        }
    }

    private class LoginActionListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            performLogin();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {}
            new SeleccionSede().setVisible(true);
        });
    }
}
