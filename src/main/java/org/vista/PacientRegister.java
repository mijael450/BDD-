package org.vista;
import org.config.ConexionSQL;
import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PacientRegister extends JFrame {

    private JComboBox<String> cbSexo;
    private JComboBox<String> cbCiudad;
    private JFormattedTextField txtFechaNac;

    public PacientRegister() {
        setTitle("Registro de Nuevo Paciente");
        setSize(1000, 600);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Panel principal dividido en izquierda y derecha
        JPanel mainPanel = new JPanel(new GridLayout(1, 2));
        add(mainPanel, BorderLayout.CENTER);

        // ----------------- PANEL IZQUIERDO -----------------
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setBackground(new Color(230, 230, 230));
        leftPanel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));

        JLabel titulo = new JLabel("REGISTRO DE NUEVO PACIENTE");
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
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Campos de registro
        int row = 0;
        
        // Cédula
        gbc.gridx = 0;
        gbc.gridy = row++;
        rightPanel.add(new JLabel("Cédula:"), gbc);
        
        gbc.gridx = 1;
        JTextField txtCedula = new JTextField(20);
        rightPanel.add(txtCedula, gbc);

        // Nombres
        gbc.gridx = 0;
        gbc.gridy = row++;
        rightPanel.add(new JLabel("Nombres:"), gbc);
        
        gbc.gridx = 1;
        JTextField txtNombres = new JTextField(20);
        rightPanel.add(txtNombres, gbc);

        // Apellidos
        gbc.gridx = 0;
        gbc.gridy = row++;
        rightPanel.add(new JLabel("Apellidos:"), gbc);
        
        gbc.gridx = 1;
        JTextField txtApellidos = new JTextField(20);
        rightPanel.add(txtApellidos, gbc);

        // Fecha de Nacimiento
        gbc.gridx = 0;
        gbc.gridy = row++;
        rightPanel.add(new JLabel("Fecha Nacimiento (dd/MM/yyyy):"), gbc);
        
        gbc.gridx = 1;
        txtFechaNac = new JFormattedTextField(new SimpleDateFormat("dd/MM/yyyy"));
        txtFechaNac.setColumns(20);
        rightPanel.add(txtFechaNac, gbc);

        // Sexo
        gbc.gridx = 0;
        gbc.gridy = row++;
        rightPanel.add(new JLabel("Sexo:"), gbc);
        
        gbc.gridx = 1;
        cbSexo = new JComboBox<>(new String[]{"Masculino", "Femenino", "Otro"});
        rightPanel.add(cbSexo, gbc);

        // Teléfono
        gbc.gridx = 0;
        gbc.gridy = row++;
        rightPanel.add(new JLabel("Teléfono:"), gbc);
        
        gbc.gridx = 1;
        JTextField txtTelefono = new JTextField(20);
        rightPanel.add(txtTelefono, gbc);

        // Email
        gbc.gridx = 0;
        gbc.gridy = row++;
        rightPanel.add(new JLabel("Email:"), gbc);
        
        gbc.gridx = 1;
        JTextField txtEmail = new JTextField(20);
        rightPanel.add(txtEmail, gbc);

        // Ciudad
        gbc.gridx = 0;
        gbc.gridy = row++;
        rightPanel.add(new JLabel("Ciudad:"), gbc);
        
        gbc.gridx = 1;
        cbCiudad = new JComboBox<>(new String[]{"Quito", "Guayaquil", "Cuenca", "Manta", "Portoviejo"});
        rightPanel.add(cbCiudad, gbc);

        // Dirección
        gbc.gridx = 0;
        gbc.gridy = row++;
        rightPanel.add(new JLabel("Dirección:"), gbc);
        
        gbc.gridx = 1;
        JTextField txtDireccion = new JTextField(20);
        rightPanel.add(txtDireccion, gbc);

        // Botón de registro
        gbc.gridx = 0;
        gbc.gridy = row++;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.CENTER;
        JButton btnRegistrar = new JButton("Registrar Paciente");
        btnRegistrar.setPreferredSize(new Dimension(200, 35));
        btnRegistrar.setBackground(new Color(70, 130, 180));
        btnRegistrar.setForeground(Color.WHITE);
        btnRegistrar.setFocusPainted(false);
        btnRegistrar.setFont(new Font("Arial", Font.BOLD, 14));
        rightPanel.add(btnRegistrar, gbc);

        // Acción del botón registrar
        btnRegistrar.addActionListener(e -> {
            String cedula = txtCedula.getText().trim();
            String nombres = txtNombres.getText().trim();
            String apellidos = txtApellidos.getText().trim();
            String fechaNacStr = txtFechaNac.getText().trim();
            String sexo = (String) cbSexo.getSelectedItem();
            String telefono = txtTelefono.getText().trim();
            String email = txtEmail.getText().trim();
            String ciudad = (String) cbCiudad.getSelectedItem();
            String direccion = txtDireccion.getText().trim();

            // Validación de campos obligatorios
            if (cedula.isEmpty() || nombres.isEmpty() || apellidos.isEmpty() || fechaNacStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Por favor complete los campos obligatorios.", "Campos vacíos", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Validación de cédula (ejemplo: 10 dígitos)
            if (!cedula.matches("\\d{10}")) {
                JOptionPane.showMessageDialog(this, "La cédula debe tener 10 dígitos.", "Cédula inválida", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Convertir fecha
            Date fechaNac = null;
            try {
                fechaNac = new SimpleDateFormat("dd/MM/yyyy").parse(fechaNacStr);
            } catch (ParseException ex) {
                JOptionPane.showMessageDialog(this, "Formato de fecha inválido. Use dd/MM/yyyy", "Fecha inválida", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try (Connection conn = ConexionSQL.conectar()) {
                // Determinar si es Quito o Guayaquil para la fragmentación
                boolean esQuito = ciudad.equalsIgnoreCase("Quito");
                boolean esGuayaquil = ciudad.equalsIgnoreCase("Guayaquil");

                // Insertar en la tabla principal de pacientes
                String sqlPaciente = esQuito ? 
                    "INSERT INTO PACIENTE_Q (CEDULA, NOMBRE, FECHA_NAC, SEXO, TELEFONO, EMAIL, CIUDAD, DIRECCION) VALUES (?, ?, ?, ?, ?, ?, ?, ?)" :
                    esGuayaquil ? 
                    "INSERT INTO PACIENTE_G (CEDULA, NOMBRE, FECHA_NAC, SEXO, TELEFONO, EMAIL, CIUDAD, DIRECCION) VALUES (?, ?, ?, ?, ?, ?, ?, ?)" :
                    "INSERT INTO PACIENTE_Q (CEDULA, NOMBRE, FECHA_NAC, SEXO, TELEFONO, EMAIL, CIUDAD, DIRECCION) VALUES (?, ?, ?, ?, ?, ?, ?, ?)"; // Por defecto Quito

                try (PreparedStatement ps = conn.prepareStatement(sqlPaciente)) {
                    ps.setString(1, cedula);
                    ps.setString(2, nombres + " " + apellidos);
                    ps.setDate(3, new java.sql.Date(fechaNac.getTime()));
                    ps.setString(4, sexo);
                    ps.setString(5, telefono);
                    ps.setString(6, email);
                    ps.setString(7, ciudad);
                    ps.setString(8, direccion);

                    int filas = ps.executeUpdate();

                    if (filas > 0) {
                        // Insertar en tablas fragmentadas verticalmente
                        insertarPacienteVertical(conn, cedula, nombres, apellidos, fechaNac, sexo, ciudad, telefono, email, direccion, esQuito);
                        
                        JOptionPane.showMessageDialog(this, "Paciente registrado exitosamente!", "Registro exitoso", JOptionPane.INFORMATION_MESSAGE);
                        limpiarCampos(txtCedula, txtNombres, txtApellidos, txtFechaNac, txtTelefono, txtEmail, txtDireccion);
                    }
                }
            } catch (SQLException ex) {
                if (ex.getErrorCode() == 2627 || ex.getErrorCode() == 2601) { // Violación de clave primaria
                    JOptionPane.showMessageDialog(this, "La cédula ingresada ya está registrada.", "Error", JOptionPane.ERROR_MESSAGE);
                } else {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Error al registrar el paciente: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        mainPanel.add(rightPanel);
    }

    private void insertarPacienteVertical(Connection conn, String cedula, String nombres, String apellidos, 
                                        Date fechaNac, String sexo, String ciudad, 
                                        String telefono, String email, String direccion,
                                        boolean esQuito) throws SQLException {
        // Insertar en datos básicos
        String sqlDatosBasicos = esQuito ?
            "INSERT INTO PACIENTE_DATOS_BASICOS_Q (CEDULA, NOMBRE, FECHA_NAC, SEXO, CIUDAD) VALUES (?, ?, ?, ?, ?)" :
            "INSERT INTO PACIENTE_DATOS_BASICOS_G (CEDULA, NOMBRE, FECHA_NAC, SEXO, CIUDAD) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(sqlDatosBasicos)) {
            ps.setString(1, cedula);
            ps.setString(2, nombres + " " + apellidos);
            ps.setDate(3, new java.sql.Date(fechaNac.getTime()));
            ps.setString(4, sexo);
            ps.setString(5, ciudad);
            ps.executeUpdate();
        }

        // Insertar en datos de contacto
        String sqlContacto = esQuito ?
            "INSERT INTO PACIENTE_CONTACTO_Q (CEDULA, TELEFONO, EMAIL, DIRECCION) VALUES (?, ?, ?, ?)" :
            "INSERT INTO PACIENTE_CONTACTO_G (CEDULA, TELEFONO, EMAIL, DIRECCION) VALUES (?, ?, ?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(sqlContacto)) {
            ps.setString(1, cedula);
            ps.setString(2, telefono);
            ps.setString(3, email);
            ps.setString(4, direccion);
            ps.executeUpdate();
        }
    }

    private void limpiarCampos(JTextField... campos) {
        for (JTextField campo : campos) {
            campo.setText("");
        }
        cbSexo.setSelectedIndex(0);
        cbCiudad.setSelectedIndex(0);
        txtFechaNac.setValue(null);
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
            new PacientRegister().setVisible(true);
        });
    }
}