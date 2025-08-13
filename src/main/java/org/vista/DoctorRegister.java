package org.vista;
import org.config.ConexionSQL;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class DoctorRegister extends JFrame {

    private JComboBox<String> cbEspecialidad;
    private JComboBox<String> cbCentro;
    private String sedeSelect;

    public DoctorRegister(String sedeSelect) {
        this.sedeSelect = sedeSelect;
        setTitle("Registro de Nuevo Doctor");
        setSize(950, 500);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Panel principal dividido en izquierda y derecha
        JPanel mainPanel = new JPanel(new GridLayout(1, 2));
        add(mainPanel, BorderLayout.CENTER);

        // ----------------- PANEL IZQUIERDO -----------------
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setBackground(new Color(0, 53, 84)); // Gris claro
        leftPanel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));

        JLabel titulo = new JLabel("REGISTRO DE NUEVO DOCTOR");
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        titulo.setFont(new Font("Arial", Font.BOLD, 20));
        titulo.setForeground(new Color(255, 255, 255));
        leftPanel.add(titulo);
        leftPanel.add(Box.createRigidArea(new Dimension(0, 40)));

        // Espacio para imagen PNG
        JLabel imagePlaceholder = new JLabel(escalarImagen("/pngs/hospital_icon.png", 180, 180));
        imagePlaceholder.setAlignmentX(Component.CENTER_ALIGNMENT);
        imagePlaceholder.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        leftPanel.add(imagePlaceholder);

        mainPanel.add(leftPanel);

        // ----------------- PANEL DERECHO (REGISTRO) -----------------
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new GridBagLayout());
        rightPanel.setBackground(new Color(157, 209, 241));
        rightPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Campos de registro
        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel lblNombres = new JLabel("Nombres:");
        lblNombres.setFont(new Font("Arial", Font.BOLD, 14));
        rightPanel.add(lblNombres, gbc);

        gbc.gridx = 1;
        JTextField txtNombres = new JTextField(20);
        txtNombres.setFont(new Font("Arial", Font.PLAIN, 14));
        rightPanel.add(txtNombres, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        JLabel lblApellidos = new JLabel("Apellidos:");
        lblApellidos.setFont(new Font("Arial", Font.BOLD, 14));
        rightPanel.add(lblApellidos, gbc);

        gbc.gridx = 1;
        JTextField txtApellidos = new JTextField(20);
        txtApellidos.setFont(new Font("Arial", Font.PLAIN, 14));
        rightPanel.add(txtApellidos, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        JLabel lblEspecialidad = new JLabel("Especialidad:");
        lblEspecialidad.setFont(new Font("Arial", Font.BOLD, 14));
        rightPanel.add(lblEspecialidad, gbc);

        gbc.gridx = 1;
        cbEspecialidad = new JComboBox<>();
        cargarEspecialidades();
        cbEspecialidad.setFont(new Font("Arial", Font.PLAIN, 14));
        rightPanel.add(cbEspecialidad, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        JLabel lblCentro = new JLabel("Centro Médico:");
        lblCentro.setFont(new Font("Arial", Font.BOLD, 14));
        rightPanel.add(lblCentro, gbc);

        gbc.gridx = 1;
        cbCentro = new JComboBox<>();
        cargarCentros();
        cbCentro.setFont(new Font("Arial", Font.PLAIN, 14));
        rightPanel.add(cbCentro, gbc);


        gbc.gridx = 0;
        gbc.gridy = 4;
        JLabel lblTelefono = new JLabel("Teléfono:");
        lblTelefono.setFont(new Font("Arial", Font.BOLD, 14));
        rightPanel.add(lblTelefono, gbc);


        gbc.gridx = 1;
        JTextField txtTelefono = new JTextField(20);
        txtTelefono.setFont(new Font("Arial", Font.PLAIN, 14));
        rightPanel.add(txtTelefono, gbc);

        gbc.gridy = 5;
        gbc.gridx = 0;
        JLabel lblCorreo = new JLabel("Correo Electrónico:");
        lblCorreo.setFont(new Font("Arial", Font.BOLD, 14));
        rightPanel.add(lblCorreo, gbc);

        gbc.gridx = 1;
        JTextField txtCorreo = new JTextField(20);
        txtCorreo.setFont(new Font("Arial", Font.PLAIN, 14));
        rightPanel.add(txtCorreo, gbc);

        gbc.gridy = 6;
        gbc.gridx = 0;
        JLabel lblSueldo = new JLabel("Sueldo:");
        lblSueldo.setFont(new Font("Arial", Font.BOLD, 14));
        rightPanel.add(lblSueldo, gbc);

        gbc.gridx = 1;
        JTextField txtSueldo = new JTextField(20);
        txtSueldo.setFont(new Font("Arial", Font.PLAIN, 14));
        rightPanel.add(txtSueldo, gbc);


        // Botón de registro
        gbc.gridx = 1;
        gbc.gridy = 8;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.CENTER;
        JButton btnRegistrar = new JButton("Registrar Doctor");
        btnRegistrar.setPreferredSize(new Dimension(200, 35));
        btnRegistrar.setBackground(new Color(70, 130, 180));
        btnRegistrar.setForeground(Color.BLACK);
        btnRegistrar.setFocusPainted(false);
        btnRegistrar.setFont(new Font("Arial", Font.BOLD, 14));
        rightPanel.add(btnRegistrar, gbc);


        JLabel lblRegresar = new JLabel("<html><u>Regresar</u></html>");
        lblRegresar.setForeground(Color.BLUE);
        lblRegresar.setFont(new Font("Arial", Font.PLAIN, 14));
        lblRegresar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // Configurar GridBagConstraints para esquina inferior derecha
        gbc.gridx = 1; // Columna derecha
        gbc.gridy = 8; // Fila después del botón
        gbc.gridwidth = 1; // Solo una columna
        gbc.anchor = GridBagConstraints.SOUTHEAST; // Esquina inferior derecha
        gbc.fill = GridBagConstraints.NONE; // No expandir
        gbc.weightx = 1.0; // Empujar hacia la derecha
        gbc.weighty = 3.5; // Empujar hacia abajo
        gbc.insets = new Insets(20, 10, 0, 5); // Margen desde los bordes

        rightPanel.add(lblRegresar, gbc);

        lblRegresar.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                dispose();
                new AdminWindow(sedeSelect).setVisible(true);
            }
        });


        // Acción del botón registrar
        btnRegistrar.addActionListener(e -> {
        String nombres = txtNombres.getText().trim();
        String apellidos = txtApellidos.getText().trim();
        String telefono = txtTelefono.getText().trim();
        int idEspecialidad = ObtenerID_Especialidad((String) cbEspecialidad.getSelectedItem());
        String ciudadCentro = (String) cbCentro.getSelectedItem();
        System.out.println("Ciudad elegida para registrar al medico: " + ciudadCentro);

        if (nombres.isEmpty() || apellidos.isEmpty() || telefono.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor complete todos los campos.", "Campos vacíos", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Connection conn = null;
        try {
            if(ciudadCentro.equalsIgnoreCase("Quito")){
                conn = ConexionSQL.conectar();
            }else{
                conn = ConexionSQL.conectarGYE(true);
            }

            conn.setAutoCommit(false); // Iniciar transacción

            if(ciudadCentro.equals("Quito")) {
                // Insertar en Quito
                int idCentro = obtenerIdCentroDisponible(conn, "Q");
                int idMedico = obtenerNuevoIdMedico(conn, "Q");
                String nombreCompleto = nombres + " " + apellidos;

                // Insertar en tabla principal MEDICO_Q
                String sqlMedico = "INSERT INTO MEDICO_Q (ID_MEDICO, NOMBRE, TELEFONO, ID_ESPECIALIDAD, ID_CENTRO, CIUDAD) VALUES (?, ?, ?, ?, ?, ?)";
                try (PreparedStatement ps = conn.prepareStatement(sqlMedico)) {
                    ps.setInt(1, idMedico);
                    ps.setString(2, nombreCompleto);
                    ps.setString(3, telefono);
                    ps.setInt(4, idEspecialidad);
                    ps.setInt(5, idCentro);
                    ps.setString(6, ciudadCentro);
                    ps.executeUpdate();
                }

                // Insertar en fragmentos verticales
                insertarMedicoVerticalQ(conn, idMedico, nombreCompleto, telefono, idCentro, idEspecialidad, ciudadCentro);

                JOptionPane.showMessageDialog(this, "Doctor registrado exitosamente en Quito!", "Registro exitoso", JOptionPane.INFORMATION_MESSAGE);
            } else {

                // Insertar en Guayaquil
                int idCentro = obtenerIdCentroDisponible(conn, "G");
                int idMedico = obtenerNuevoIdMedico(conn, "G");
                String nombreCompleto = nombres + " " + apellidos;

                // Insertar en tabla principal MEDICO_G
                String sqlMedico = "INSERT INTO MEDICO_G (ID_MEDICO, NOMBRE, TELEFONO, ID_ESPECIALIDAD, ID_CENTRO, CIUDAD) VALUES (?, ?, ?, ?, ?, ?)";
                try (PreparedStatement ps = conn.prepareStatement(sqlMedico)) {
                    ps.setInt(1, idMedico);
                    ps.setString(2, nombreCompleto);
                    ps.setString(3, telefono);
                    ps.setInt(4, idEspecialidad);
                    ps.setInt(5, idCentro);
                    ps.setString(6, ciudadCentro);
                    ps.executeUpdate();
                }

                // Insertar en fragmentos verticales
                insertarMedicoVerticalG(conn, idMedico, nombreCompleto, telefono, idCentro, idEspecialidad, ciudadCentro);

                JOptionPane.showMessageDialog(this, "Doctor registrado exitosamente en Guayaquil!", "Registro exitoso", JOptionPane.INFORMATION_MESSAGE);
            }

                conn.commit(); // Confirmar transacción
                limpiarCampos(txtNombres, txtApellidos, txtTelefono);
            } catch (SQLException ex) {
                try {
                    if (conn != null) {
                        conn.rollback(); // Revertir en caso de error
                    }
                } catch (SQLException rollbackEx) {
                    rollbackEx.printStackTrace();
                }

                String errorMsg = "Error al registrar el doctor: ";
                if (ex.getMessage().contains("CIUDAD")) {
                    errorMsg += "Falta especificar la ciudad o es inválida";
                } else {
                    errorMsg += ex.getMessage();
                }

                JOptionPane.showMessageDialog(this, errorMsg, "Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            } finally {
                try {
                    if (conn != null) {
                        conn.setAutoCommit(true); // Restaurar autocommit
                        conn.close();
                    }
                } catch (SQLException closeEx) {
                    closeEx.printStackTrace();
                }
            }
        });

        mainPanel.add(rightPanel);
    }


    private int obtenerNuevoIdMedico(Connection conn, String ciudad) throws SQLException {
    String sql = "SELECT MAX(ID_MEDICO) FROM MEDICO_" + ciudad;
    try (PreparedStatement ps = conn.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {
        return rs.next() ? rs.getInt(1) + 1 : 1;
    }
}

private int obtenerIdCentroDisponible(Connection conn, String ciudad) throws SQLException {
    // En una implementación real, esto debería venir de la selección del centro
    // Aquí asumimos que el centro ya existe y obtenemos el primero disponible
    String sql = "SELECT MIN(ID_CENTRO) FROM CENTRO_" + ciudad;
    try (PreparedStatement ps = conn.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {
        return rs.next() ? rs.getInt(1) : 1;
    }
}

// Método modificado para insertar en fragmentos verticales de Quito
    private void insertarMedicoVerticalQ(Connection conn, int idMedico, String nombre, String telefono, int idCentro, int idEspecialidad, String ciudad) throws SQLException {
        // Insertar en identificación
        String sqlIdent = "INSERT INTO MEDICO_IDENTIFICACION_Q (ID_MEDICO, NOMBRE, TELEFONO, ID_CENTRO, CIUDAD) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sqlIdent)) {
            ps.setInt(1, idMedico);
            ps.setString(2, nombre);
            ps.setString(3, telefono);
            ps.setInt(4, idCentro);
            ps.setString(5, ciudad);
            ps.executeUpdate();
        }

        // Insertar en perfil profesional
        String sqlPerfil = "INSERT INTO MEDICO_PERFIL_PROFESIONAL_Q (ID_MEDICO, ID_ESPECIALIDAD, CIUDAD) VALUES (?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sqlPerfil)) {
            ps.setInt(1, idMedico);
            ps.setInt(2, idEspecialidad);
            ps.setString(3, ciudad);
            ps.executeUpdate();
        }
    }

// Método para insertar en fragmentos verticales de Guayaquil (similar al de Quito)
    private void insertarMedicoVerticalG(Connection conn, int idMedico, String nombre, String telefono, int idCentro, int idEspecialidad, String ciudad) throws SQLException {
        // Insertar en identificación
        String sqlIdent = "INSERT INTO MEDICO_IDENTIFICACION_G (ID_MEDICO, NOMBRE, TELEFONO, ID_CENTRO, CIUDAD) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sqlIdent)) {
            ps.setInt(1, idMedico);
            ps.setString(2, nombre);
            ps.setString(3, telefono);
            ps.setInt(4, idCentro);
            ps.setString(5, ciudad);
            ps.executeUpdate();
        }

        // Insertar en perfil profesional
        String sqlPerfil = "INSERT INTO MEDICO_PERFIL_PROFESIONAL_G (ID_MEDICO, ID_ESPECIALIDAD, CIUDAD) VALUES (?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sqlPerfil)) {
            ps.setInt(1, idMedico);
            ps.setInt(2, idEspecialidad);
            ps.setString(3, ciudad);
            ps.executeUpdate();
        }
    }

    private void cargarEspecialidades() {
        // Aquí cargarías las especialidades desde la base de datos
        // Ejemplo con datos estáticos:
        cbEspecialidad.addItem("Cardiologia");
        cbEspecialidad.addItem("Pediatria");
        cbEspecialidad.addItem("Neurologia");
        cbEspecialidad.addItem("Dermatologia");
        cbEspecialidad.addItem("Ortopedia");
    }

    private void cargarCentros() {
        // Aquí cargarías los centros médicos desde la base de datos
        // Ejemplo con datos estáticos:
        cbCentro.addItem("Quito");
        cbCentro.addItem("Guayaquil");
    }

    private void limpiarCampos(JTextField... campos) {
        for (JTextField campo : campos) {
            campo.setText("");
        }
        cbEspecialidad.setSelectedIndex(0);
        cbCentro.setSelectedIndex(0);
    }

    private ImageIcon escalarImagen(String ruta, int ancho, int alto) {
        ImageIcon iconoOriginal = new ImageIcon(getClass().getResource(ruta));
        Image imagenOriginal = iconoOriginal.getImage();
        Image imagenEscalada = imagenOriginal.getScaledInstance(ancho, alto, Image.SCALE_SMOOTH);
        return new ImageIcon(imagenEscalada);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            new DoctorRegister("").setVisible(true);
        });
    }
    
    public static int ObtenerID_Especialidad(String especialidad){ 
        Map<String,Integer> esp = new HashMap<>();
        esp.put("Cardiologia", 1);
        esp.put("Pediatria", 2);
        esp.put("Neurologia", 3);
        esp.put("Dermatologia", 4);
        esp.put("Ortopedia", 5);
        int valor_esp = esp.get(especialidad); // 3
        return valor_esp;
    }
    
    public static int ObtenerID_Centro(String centro){ 
        int valor_centro;
        if(centro=="Quito"){ 
            valor_centro = 1;
        }else{ 
            valor_centro = 2;
        }
     return valor_centro;   
    }
}