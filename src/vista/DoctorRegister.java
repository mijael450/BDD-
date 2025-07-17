package vista;
import config.ConexionSQL;
import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class DoctorRegister extends JFrame {

    private JComboBox<String> cbEspecialidad;
    private JComboBox<String> cbCentro;

    public DoctorRegister() {
        setTitle("Registro de Nuevo Doctor");
        setSize(900, 500);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
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

        JLabel titulo = new JLabel("REGISTRO DE NUEVO DOCTOR");
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        titulo.setFont(new Font("Arial", Font.BOLD, 20));
        titulo.setForeground(new Color(50, 50, 50));
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
        rightPanel.setBackground(new Color(245, 245, 245));
        rightPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Campos de registro
        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel lblNombres = new JLabel("Nombres:");
        rightPanel.add(lblNombres, gbc);

        gbc.gridx = 1;
        JTextField txtNombres = new JTextField(20);
        rightPanel.add(txtNombres, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        JLabel lblApellidos = new JLabel("Apellidos:");
        rightPanel.add(lblApellidos, gbc);

        gbc.gridx = 1;
        JTextField txtApellidos = new JTextField(20);
        rightPanel.add(txtApellidos, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        JLabel lblTelefono = new JLabel("Teléfono:");
        rightPanel.add(lblTelefono, gbc);

        gbc.gridx = 1;
        JTextField txtTelefono = new JTextField(20);
        rightPanel.add(txtTelefono, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        JLabel lblEspecialidad = new JLabel("Especialidad:");
        rightPanel.add(lblEspecialidad, gbc);

        gbc.gridx = 1;
        cbEspecialidad = new JComboBox<>();
        cargarEspecialidades();
        rightPanel.add(cbEspecialidad, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        JLabel lblCentro = new JLabel("Centro Médico:");
        rightPanel.add(lblCentro, gbc);

        gbc.gridx = 1;
        cbCentro = new JComboBox<>();
        cargarCentros();
        rightPanel.add(cbCentro, gbc);

        // Botón de registro
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.CENTER;
        JButton btnRegistrar = new JButton("Registrar Doctor");
        btnRegistrar.setPreferredSize(new Dimension(200, 35));
        btnRegistrar.setBackground(new Color(70, 130, 180));
        btnRegistrar.setForeground(Color.WHITE);
        btnRegistrar.setFocusPainted(false);
        btnRegistrar.setFont(new Font("Arial", Font.BOLD, 14));
        rightPanel.add(btnRegistrar, gbc);

        // Acción del botón registrar
        
        btnRegistrar.addActionListener(e -> {
    String nombres = txtNombres.getText().trim();
    String apellidos = txtApellidos.getText().trim();
    String telefono = txtTelefono.getText().trim();
    int idEspecialidad = ObtenerID_Especialidad((String) cbEspecialidad.getSelectedItem());
    String ciudadCentro = (String) cbCentro.getSelectedItem();
    
    if (nombres.isEmpty() || apellidos.isEmpty() || telefono.isEmpty()) {
        JOptionPane.showMessageDialog(this, "Por favor complete todos los campos.", "Campos vacíos", JOptionPane.WARNING_MESSAGE);
        return;
    }

    try (Connection conn = ConexionSQL.conectar()) {
        // Primero determinamos si es Quito o Guayaquil
        if(ciudadCentro.equals("Quito")) {
            // Insertar en CENTRO_Q (asumiendo que ya existe el centro)
            // Obtener ID_CENTRO disponible para Quito
            int idCentro = obtenerIdCentroDisponible(conn, "Q");
            
            // Insertar en MEDICO_Q (tabla completa)
            String sqlMedico = "INSERT INTO MEDICO_Q (ID_MEDICO, NOMBRE, TELEFONO, ID_ESPECIALIDAD, ID_CENTRO) VALUES (?, ?, ?, ?, ?)";
            int idMedico = obtenerNuevoIdMedico(conn, "Q");
            
            try (PreparedStatement ps = conn.prepareStatement(sqlMedico)) {
                ps.setInt(1, idMedico);
                ps.setString(2, nombres + " " + apellidos);
                ps.setString(3, telefono);
                ps.setInt(4, idEspecialidad);
                ps.setInt(5, idCentro);
                
                int filas = ps.executeUpdate();
                
                if(filas > 0) {
                    // Insertar en las tablas fragmentadas verticalmente
                    insertarMedicoVerticalQ(conn, idMedico, nombres + " " + apellidos, telefono, idCentro, idEspecialidad);
                    JOptionPane.showMessageDialog(this, "Doctor registrado exitosamente en Quito!", "Registro exitoso", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        } else { // Guayaquil
            // Insertar en CENTRO_G (asumiendo que ya existe el centro)
            // Obtener ID_CENTRO disponible para Guayaquil
            int idCentro = obtenerIdCentroDisponible(conn, "G");
            
            // Insertar en MEDICO_G (tabla completa)
            String sqlMedico = "INSERT INTO MEDICO_G (ID_MEDICO, NOMBRE, TELEFONO, ID_ESPECIALIDAD, ID_CENTRO) VALUES (?, ?, ?, ?, ?)";
            int idMedico = obtenerNuevoIdMedico(conn, "G");
            
            try (PreparedStatement ps = conn.prepareStatement(sqlMedico)) {
                ps.setInt(1, idMedico);
                ps.setString(2, nombres + " " + apellidos);
                ps.setString(3, telefono);
                ps.setInt(4, idEspecialidad);
                ps.setInt(5, idCentro);
                
                int filas = ps.executeUpdate();
                
                if(filas > 0) {
                    // Insertar en las tablas fragmentadas verticalmente
                    insertarMedicoVerticalG(conn, idMedico, nombres + " " + apellidos, telefono, idCentro, idEspecialidad);
                    JOptionPane.showMessageDialog(this, "Doctor registrado exitosamente en Guayaquil!", "Registro exitoso", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        }
        
        limpiarCampos(txtNombres, txtApellidos, txtTelefono);
    } catch (SQLException ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(this, "Error al registrar el doctor: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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

private void insertarMedicoVerticalQ(Connection conn, int idMedico, String nombre, String telefono, int idCentro, int idEspecialidad) throws SQLException {
    // Insertar en identificación
    String sqlIdent = "INSERT INTO MEDICO_IDENTIFICACION_Q (ID_MEDICO, NOMBRE, TELEFONO, ID_CENTRO) VALUES (?, ?, ?, ?)";
    try (PreparedStatement ps = conn.prepareStatement(sqlIdent)) {
        ps.setInt(1, idMedico);
        ps.setString(2, nombre);
        ps.setString(3, telefono);
        ps.setInt(4, idCentro);
        ps.executeUpdate();
    }
    
    // Insertar en perfil profesional
    String sqlPerfil = "INSERT INTO MEDICO_PERFIL_PROFESIONAL_Q (ID_MEDICO, ID_ESPECIALIDAD) VALUES (?, ?)";
    try (PreparedStatement ps = conn.prepareStatement(sqlPerfil)) {
        ps.setInt(1, idMedico);
        ps.setInt(2, idEspecialidad);
        ps.executeUpdate();
    }
}

private void insertarMedicoVerticalG(Connection conn, int idMedico, String nombre, String telefono, int idCentro, int idEspecialidad) throws SQLException {
    // Similar al método anterior pero para Guayaquil
    String sqlIdent = "INSERT INTO MEDICO_IDENTIFICACION_G (ID_MEDICO, NOMBRE, TELEFONO, ID_CENTRO) VALUES (?, ?, ?, ?)";
    try (PreparedStatement ps = conn.prepareStatement(sqlIdent)) {
        ps.setInt(1, idMedico);
        ps.setString(2, nombre);
        ps.setString(3, telefono);
        ps.setInt(4, idCentro);
        ps.executeUpdate();
    }
    
    String sqlPerfil = "INSERT INTO MEDICO_PERFIL_PROFESIONAL_G (ID_MEDICO, ID_ESPECIALIDAD) VALUES (?, ?)";
    try (PreparedStatement ps = conn.prepareStatement(sqlPerfil)) {
        ps.setInt(1, idMedico);
        ps.setInt(2, idEspecialidad);
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
            new DoctorRegister().setVisible(true);
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