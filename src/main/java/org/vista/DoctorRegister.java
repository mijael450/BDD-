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

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class DoctorRegister extends JFrame {

    // Constants for better maintainability
    private static final String QUITO = "Quito";
    private static final String GUAYAQUIL = "Guayaquil";
    private static final String QUITO_CENTER_ID = "Q";
    private static final String GUAYAQUIL_CENTER_ID = "G";
    private static final String QUITO_SUFFIX = "Q";
    private static final String GUAYAQUIL_SUFFIX = "G";

    private JComboBox<String> cbEspecialidad;
    private JComboBox<String> cbCentro;
    private JTextField txtNombres;
    private JTextField txtApellidos;
    private JTextField txtTelefono;
    private String sedeSelect;

    public DoctorRegister(String sedeSelect) {
        this.sedeSelect = sedeSelect;
        initializeComponents();
    }

    private void initializeComponents() {
        setTitle("Registro de Nuevo Doctor");
        setSize(950, 500);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Panel principal dividido en izquierda y derecha
        JPanel mainPanel = new JPanel(new GridLayout(1, 2));
        add(mainPanel, BorderLayout.CENTER);

        // Panel izquierdo (branding)
        JPanel leftPanel = createLeftPanel();
        mainPanel.add(leftPanel);

        // Panel derecho (formulario)
        JPanel rightPanel = createRightPanel();
        mainPanel.add(rightPanel);
    }

    private JPanel createLeftPanel() {
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setBackground(new Color(0, 53, 84));
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

        return leftPanel;
    }

    private JPanel createRightPanel() {
        JPanel rightPanel = new JPanel(new GridBagLayout());
        rightPanel.setBackground(new Color(157, 209, 241));
        rightPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Crear campos del formulario
        createFormFields(rightPanel, gbc);

        // Crear botones
        createButtons(rightPanel, gbc);

        return rightPanel;
    }

    private void createFormFields(JPanel panel, GridBagConstraints gbc) {
        // Nombres
        gbc.gridx = 0; gbc.gridy = 0;
        JLabel lblNombres = new JLabel("Nombres:");
        lblNombres.setFont(new Font("Arial", Font.BOLD, 14));
        panel.add(lblNombres, gbc);

        gbc.gridx = 1;
        txtNombres = new JTextField(20);
        txtNombres.setFont(new Font("Arial", Font.PLAIN, 14));
        panel.add(txtNombres, gbc);

        // Apellidos
        gbc.gridx = 0; gbc.gridy = 1;
        JLabel lblApellidos = new JLabel("Apellidos:");
        lblApellidos.setFont(new Font("Arial", Font.BOLD, 14));
        panel.add(lblApellidos, gbc);

        gbc.gridx = 1;
        txtApellidos = new JTextField(20);
        txtApellidos.setFont(new Font("Arial", Font.PLAIN, 14));
        panel.add(txtApellidos, gbc);

        // Especialidad
        gbc.gridx = 0; gbc.gridy = 3;
        JLabel lblEspecialidad = new JLabel("Especialidad:");
        lblEspecialidad.setFont(new Font("Arial", Font.BOLD, 14));
        panel.add(lblEspecialidad, gbc);

        gbc.gridx = 1;
        cbEspecialidad = new JComboBox<>();
        cbEspecialidad.setFont(new Font("Arial", Font.PLAIN, 14));
        cargarEspecialidades(QUITO); // Cargar especialidades por defecto
        panel.add(cbEspecialidad, gbc);

        // Centro Médico
        gbc.gridx = 0; gbc.gridy = 2;
        JLabel lblCentro = new JLabel("Centro Médico:");
        lblCentro.setFont(new Font("Arial", Font.BOLD, 14));
        panel.add(lblCentro, gbc);

        gbc.gridx = 1;
        cbCentro = new JComboBox<>();
        cargarCentros();
        cbCentro.setFont(new Font("Arial", Font.PLAIN, 14));

        // Listener para cambio de ciudad
        cbCentro.addActionListener(e -> {
            String ciudadSeleccionada = (String) cbCentro.getSelectedItem();
            if (ciudadSeleccionada != null) {
                cargarEspecialidades(ciudadSeleccionada);
            }
        });
        panel.add(cbCentro, gbc);

        // Teléfono
        gbc.gridx = 0; gbc.gridy = 4;
        JLabel lblTelefono = new JLabel("Teléfono:");
        lblTelefono.setFont(new Font("Arial", Font.BOLD, 14));
        panel.add(lblTelefono, gbc);

        gbc.gridx = 1;
        txtTelefono = new JTextField(20);
        txtTelefono.setFont(new Font("Arial", Font.PLAIN, 14));
        panel.add(txtTelefono, gbc);
    }

    private void createButtons(JPanel panel, GridBagConstraints gbc) {
        // Botón Registrar
        gbc.gridx = 1; gbc.gridy = 8;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.CENTER;
        JButton btnRegistrar = new JButton("Registrar Doctor");
        btnRegistrar.setPreferredSize(new Dimension(200, 35));
        btnRegistrar.setBackground(new Color(70, 130, 180));
        btnRegistrar.setForeground(Color.BLACK);
        btnRegistrar.setFocusPainted(false);
        btnRegistrar.setFont(new Font("Arial", Font.BOLD, 14));
        btnRegistrar.addActionListener(e -> registrarDoctor());
        panel.add(btnRegistrar, gbc);

        // Link Regresar
        JLabel lblRegresar = new JLabel("<html><u>Regresar</u></html>");
        lblRegresar.setForeground(Color.BLUE);
        lblRegresar.setFont(new Font("Arial", Font.PLAIN, 14));
        lblRegresar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        gbc.gridx = 1; gbc.gridy = 9;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.SOUTHEAST;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 1.0;
        gbc.weighty = 3.5;
        gbc.insets = new Insets(20, 10, 0, 5);
        panel.add(lblRegresar, gbc);

        lblRegresar.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                dispose();
                new AdminWindow(sedeSelect).setVisible(true);
            }
        });
    }

    // Clase para encapsular información del doctor
    private static class DoctorInfo {
        private final String nombres;
        private final String apellidos;
        private final String telefono;
        private final String especialidad;
        private final String ciudad;

        public DoctorInfo(String nombres, String apellidos, String telefono, String especialidad, String ciudad) {
            this.nombres = nombres;
            this.apellidos = apellidos;
            this.telefono = telefono;
            this.especialidad = especialidad;
            this.ciudad = ciudad;
        }

        public String getNombreCompleto() {
            return nombres + " " + apellidos;
        }

        // Getters
        public String getNombres() { return nombres; }
        public String getApellidos() { return apellidos; }
        public String getTelefono() { return telefono; }
        public String getEspecialidad() { return especialidad; }
        public String getCiudad() { return ciudad; }
    }

    private void registrarDoctor() {
        // Validar entrada
        if (!validateInput()) {
            return;
        }

        // Crear objeto doctor
        DoctorInfo doctor = new DoctorInfo(
                txtNombres.getText().trim(),
                txtApellidos.getText().trim(),
                txtTelefono.getText().trim(),
                (String) cbEspecialidad.getSelectedItem(),
                (String) cbCentro.getSelectedItem()
        );

        // Registrar en base de datos
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                insertarDoctorEnBaseDatos(doctor);
                return null;
            }

            @Override
            protected void done() {
                try {
                    get(); // Verificar si hubo errores
                    JOptionPane.showMessageDialog(DoctorRegister.this,
                            "Doctor registrado exitosamente en " + doctor.getCiudad() + "!",
                            "Registro exitoso",
                            JOptionPane.INFORMATION_MESSAGE);
                    limpiarCampos();
                } catch (Exception ex) {
                    handleError(ex.getCause() instanceof SQLException ?
                            (SQLException) ex.getCause() :
                            new SQLException("Error desconocido", ex));
                }
            }
        };
        worker.execute();
    }

    private boolean validateInput() {
        String nombres = txtNombres.getText().trim();
        String apellidos = txtApellidos.getText().trim();
        String telefono = txtTelefono.getText().trim();
        String especialidad = (String) cbEspecialidad.getSelectedItem();

        if (nombres.isEmpty() || apellidos.isEmpty() || telefono.isEmpty()) {
            showError("Por favor complete todos los campos obligatorios.");
            return false;
        }

        if (especialidad == null || especialidad.equals("No hay especialidades disponibles")) {
            showError("Por favor seleccione una especialidad válida.");
            return false;
        }

        // Validar formato de teléfono (números y longitud básica)
        if (!telefono.matches("\\d{7,10}")) {
            showError("El teléfono debe contener solo números y tener entre 7 y 15 dígitos.");
            return false;
        }

        // Validar nombres y apellidos (solo letras, espacios y algunos caracteres especiales)
        if (!nombres.matches("^[a-zA-ZÀ-ÿ\\s]+$") || !apellidos.matches("^[a-zA-ZÀ-ÿ\\s]+$")) {
            showError("Los nombres y apellidos solo pueden contener letras y espacios.");
            return false;
        }

        return true;
    }

    private void insertarDoctorEnBaseDatos(DoctorInfo doctor) throws SQLException {
        Connection conn = null;
        try {
            conn = getConnection(doctor.getCiudad());
            conn.setAutoCommit(false);

            // Obtener IDs necesarios
            int idEspecialidad = obtenerIdEspecialidad(doctor.getEspecialidad(), doctor.getCiudad());
            String idCentro = obtenerIdCentro(doctor.getCiudad());
            int idMedico = obtenerNuevoIdMedico(conn, doctor.getCiudad());

            if (existeDoctor(conn, doctor.getNombreCompleto(), doctor.getTelefono())) {
                throw new SQLException("Duplicate doctor", "23000");
            }

            // PASO 1: Insertar primero en tabla específica de la ciudad (MEDICO_PERFIL_PROFESIONAL_X)
            insertarPerfilProfesional(conn, idMedico, idEspecialidad, idCentro, doctor.getCiudad());

            // PASO 2: Insertar en tabla replicada (MEDICO_IDENTIFICACION)
            insertarIdentificacion(conn, idMedico, doctor.getNombreCompleto(), doctor.getTelefono());

            conn.commit();

        } catch (SQLException ex) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException rollbackEx) {
                    rollbackEx.printStackTrace();
                }
            }
            throw ex;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException closeEx) {
                    closeEx.printStackTrace();
                }
            }
        }
    }

    private void insertarPerfilProfesional(Connection conn, int idMedico, int idEspecialidad,
                                           String idCentro, String ciudad) throws SQLException {
        String tablaSufijo = ciudad.equals(QUITO) ? QUITO_SUFFIX : GUAYAQUIL_SUFFIX;
        String sql = "INSERT INTO MEDICO_PERFIL_PROFESIONAL_" + tablaSufijo +
                " (ID_MEDICO, ID_ESPECIALIDAD, ID_CENTRO) VALUES (?, ?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idMedico);
            ps.setInt(2, idEspecialidad);
            ps.setString(3, idCentro);
            ps.executeUpdate();
        }
    }

    private void insertarIdentificacion(Connection conn, int idMedico, String nombre,
                                        String telefono) throws SQLException {
        String sql = "INSERT INTO MEDICO_IDENTIFICACION (ID_MEDICO, NOMBRE, TELEFONO) VALUES (?, ?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idMedico);
            ps.setString(2, nombre);
            ps.setString(3, telefono);
            ps.executeUpdate();
        }
    }

    private Connection getConnection(String ciudad) throws SQLException {
        return ciudad.equalsIgnoreCase(QUITO) ?
                ConexionSQL.conectar() :
                ConexionSQL.conectarGYE(true);
    }

    private int obtenerNuevoIdMedico(Connection conn, String ciudad) throws SQLException {
        String sufijo = ciudad.equals(QUITO) ? QUITO_SUFFIX : GUAYAQUIL_SUFFIX;
        int base = ciudad.equals(QUITO) ? 100 : 200;

        String sql = "SELECT MAX(ID_MEDICO) FROM MEDICO_PERFIL_PROFESIONAL_" + sufijo;
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                int maxId = rs.getInt(1);
                if (rs.wasNull()) {
                    return base;
                }
                return maxId + 1;
            }
            return base;
        }
    }

    private String obtenerIdCentro(String ciudad) {
        return ciudad.equals(QUITO) ? QUITO_CENTER_ID : GUAYAQUIL_CENTER_ID;
    }

    private int obtenerIdEspecialidad(String especialidad, String ciudad) {
        Connection conn = null;
        try {
            conn = getConnection(ciudad);
            String sufijo = ciudad.equals(QUITO) ? QUITO_SUFFIX : GUAYAQUIL_SUFFIX;
            String sql = "SELECT ID_ESPECIALIDAD FROM ESPECIALIDAD_" + sufijo + " WHERE NOMBRE = ?";

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, especialidad);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return rs.getInt("ID_ESPECIALIDAD");
                    }
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            // Fallback a mapeo por defecto
            return getDefaultEspecialidadId(especialidad);
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException closeEx) {
                    closeEx.printStackTrace();
                }
            }
        }
        return getDefaultEspecialidadId(especialidad);
    }

    private boolean existeDoctor(Connection conn, String nombre, String telefono) throws SQLException {
        String sql = "SELECT COUNT(*) FROM MEDICO_IDENTIFICACION WHERE NOMBRE = ? AND TELEFONO = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nombre);
            ps.setString(2, telefono);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1) > 0;
            }
        }
    }

    private int getDefaultEspecialidadId(String especialidad) {
        Map<String, Integer> especialidades = new HashMap<>();
        especialidades.put("Cardiologia", 1);
        especialidades.put("Pediatria", 2);
        especialidades.put("Neurologia", 3);
        especialidades.put("Dermatologia", 4);
        especialidades.put("Ortopedia", 5);
        return especialidades.getOrDefault(especialidad, 1);
    }

    private void cargarEspecialidades(String ciudad) {
        cbEspecialidad.removeAllItems();

        Connection conn = null;
        try {
            conn = getConnection(ciudad);
            String sufijo = ciudad.equals(QUITO) ? QUITO_SUFFIX : GUAYAQUIL_SUFFIX;
            String sql = "SELECT NOMBRE FROM ESPECIALIDAD_" + sufijo + " ORDER BY NOMBRE";

            try (PreparedStatement ps = conn.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    cbEspecialidad.addItem(rs.getString("NOMBRE"));
                }

                if (cbEspecialidad.getItemCount() == 0) {
                    cbEspecialidad.addItem("No hay especialidades disponibles");
                }
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            loadDefaultEspecialidades();
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException closeEx) {
                    closeEx.printStackTrace();
                }
            }
        }
    }

    private void loadDefaultEspecialidades() {
        String[] defaultEspecialidades = {
                "Cardiologia", "Pediatria", "Neurologia", "Dermatologia", "Ortopedia"
        };
        for (String especialidad : defaultEspecialidades) {
            cbEspecialidad.addItem(especialidad);
        }
    }

    private void cargarCentros() {
        cbCentro.addItem(QUITO);
        cbCentro.addItem(GUAYAQUIL);
    }

    private void limpiarCampos() {
        txtNombres.setText("");
        txtApellidos.setText("");
        txtTelefono.setText("");
        if (cbEspecialidad.getItemCount() > 0) {
            cbEspecialidad.setSelectedIndex(0);
        }
        if (cbCentro.getItemCount() > 0) {
            cbCentro.setSelectedIndex(0);
        }
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error de Validación", JOptionPane.WARNING_MESSAGE);
    }

    private void handleError(SQLException ex) {
        String userMessage = "Error al registrar el doctor: ";

        if (ex.getMessage().contains("Duplicate") || ex.getSQLState().equals("23000")) {
            userMessage += "Ya existe un doctor con estos datos.";
        } else if (ex.getMessage().contains("Connection")) {
            userMessage += "Problema de conexión con la base de datos.";
        } else {
            userMessage += "Error interno del sistema.";
        }

        JOptionPane.showMessageDialog(this, userMessage, "Error", JOptionPane.ERROR_MESSAGE);
        ex.printStackTrace();
    }

    private ImageIcon escalarImagen(String ruta, int ancho, int alto) {
        try {
            ImageIcon iconoOriginal = new ImageIcon(getClass().getResource(ruta));
            Image imagenOriginal = iconoOriginal.getImage();
            Image imagenEscalada = imagenOriginal.getScaledInstance(ancho, alto, Image.SCALE_SMOOTH);
            return new ImageIcon(imagenEscalada);
        } catch (Exception e) {
            // Retornar un ícono vacío si no se puede cargar la imagen
            return new ImageIcon();
        }
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
}