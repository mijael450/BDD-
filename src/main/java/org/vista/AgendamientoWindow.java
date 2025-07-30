package org.vista;

import com.toedter.calendar.JDateChooser;
import org.config.ConexionSQL;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.Date;

public class AgendamientoWindow extends JFrame {

    private JTextField txtCedulaPaciente;
    private JComboBox<String> cmbEspecialidad;
    private JComboBox<String> cmbMedico;
    private JDateChooser dateChooser;
    private JButton jButton_BuscarHorarios;
    private JButton jButton_AgendarCita;
    private JTable tablaHorarios;
    private DefaultTableModel modeloTabla;
    private JScrollPane scrollTabla;
    private String nombrePaciente;
    private String cedulaPaciente;

    public AgendamientoWindow(String nombrePaciente, String cedulaPaciente) throws HeadlessException {
        this.nombrePaciente = nombrePaciente;
        this.cedulaPaciente = cedulaPaciente;
    }

    public AgendamientoWindow() {
        setTitle("Gestión de Citas Médicas");
        setSize(900, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());


        // -------- PANEL IZQUIERDO (Imagen + info paciente) --------
        JPanel panelIzquierdo = new JPanel();
        panelIzquierdo.setLayout(new BoxLayout(panelIzquierdo, BoxLayout.Y_AXIS));
        panelIzquierdo.setPreferredSize(new Dimension(250, 500));
        panelIzquierdo.setBackground(new Color(0, 53, 84));
        panelIzquierdo.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));

        try {
            JLabel imagePlaceholder = new JLabel(escalarImagen("/pngs/medcalen.png", 180, 180));
            imagePlaceholder.setAlignmentX(Component.CENTER_ALIGNMENT);
            imagePlaceholder.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));
            panelIzquierdo.add(imagePlaceholder);
        } catch (Exception e) {
            System.err.println("No se pudo cargar la imagen: " + e.getMessage());
            JLabel placeholder = new JLabel("🏥");
            placeholder.setAlignmentX(Component.CENTER_ALIGNMENT);
            placeholder.setFont(new Font("Arial", Font.PLAIN, 80));
            panelIzquierdo.add(placeholder);
        }

        JLabel lblNombre = new JLabel("Nombre del Paciente:");
        lblNombre.setForeground(Color.WHITE);
        lblNombre.setFont(new Font("Arial", Font.BOLD, 14));
        JLabel lblNombreValue = new JLabel(nombrePaciente);
        lblNombreValue.setForeground(Color.WHITE);
        lblNombreValue.setFont(new Font("Arial", Font.PLAIN, 14));
        JLabel lblCedula = new JLabel("Cédula:");
        lblCedula.setForeground(Color.WHITE);
        lblCedula.setFont(new Font("Arial", Font.BOLD, 14));
        JLabel lblCedulaValue = new JLabel(cedulaPaciente);
        lblCedulaValue.setForeground(Color.WHITE);
        lblCedulaValue.setFont(new Font("Arial", Font.PLAIN, 14));



        lblNombre.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblNombreValue.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblCedula.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblCedulaValue.setAlignmentX(Component.CENTER_ALIGNMENT);

        panelIzquierdo.add(Box.createRigidArea(new Dimension(0, 20)));
        panelIzquierdo.add(lblNombre);
        panelIzquierdo.add(lblNombreValue);
        panelIzquierdo.add(Box.createRigidArea(new Dimension(0, 10)));
        panelIzquierdo.add(lblCedula);
        panelIzquierdo.add(lblCedulaValue);

        // -------- PANEL DERECHO (Formulario + tabla) --------
        JPanel panelDerecho = new JPanel();
        panelDerecho.setLayout(new BorderLayout());
        panelDerecho.setBackground(new Color(157, 209, 241));

        JPanel panelFormulario = new JPanel(new GridLayout(6, 2, 10, 10));
        panelFormulario.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panelFormulario.setBackground(new Color(157, 209, 241));


//        panelFormulario.add(new JLabel("Cédula del Paciente:"));
//        txtCedulaPaciente = new JTextField();
//        panelFormulario.add(txtCedulaPaciente);

        panelFormulario.add(new JLabel("Especialidad Médica:"));
        cmbEspecialidad = new JComboBox<>();
        panelFormulario.add(cmbEspecialidad);

        panelFormulario.add(new JLabel("Médico:"));
        cmbMedico = new JComboBox<>();
        panelFormulario.add(cmbMedico);

        cmbEspecialidad.addActionListener(e -> cargarMedicos());
        cargarEspecialidadesDesdeDB();

        panelFormulario.add(new JLabel("Fecha:"));
        dateChooser = new JDateChooser();
        dateChooser.setDateFormatString("yyyy-MM-dd");
        panelFormulario.add(dateChooser);

        jButton_BuscarHorarios = new JButton("Buscar Horarios Disponibles");
        jButton_BuscarHorarios.addActionListener(e -> buscarHorarios());
        panelFormulario.add(jButton_BuscarHorarios);

        jButton_AgendarCita = new JButton("Agendar Cita");
        jButton_AgendarCita.setBackground(new Color(76, 175, 80));
        jButton_AgendarCita.setForeground(Color.WHITE);
        jButton_AgendarCita.addActionListener(e -> agendarCitaEnDB());
        panelFormulario.add(jButton_AgendarCita);

        // Tabla de horarios
        String[] columnas = {"Hora", "Estado"};
        modeloTabla = new DefaultTableModel(columnas, 0);
        tablaHorarios = new JTable(modeloTabla);
        scrollTabla = new JScrollPane(tablaHorarios);
        scrollTabla.setVisible(false);

        panelDerecho.add(panelFormulario, BorderLayout.NORTH);
        panelDerecho.add(scrollTabla, BorderLayout.CENTER);

        // Añadir los paneles al frame principal
        add(panelIzquierdo, BorderLayout.WEST);
        add(panelDerecho, BorderLayout.CENTER);

        // Configurar el selector de fechas
        dateChooser.setMinSelectableDate(new Date());
        ((JTextField) dateChooser.getDateEditor().getUiComponent()).setEditable(false);

        setVisible(true);
    }

    private void cargarEspecialidadesDesdeDB() {
        String sql = "SELECT nombre FROM ESPECIALIDAD ORDER BY nombre";

        try (Connection conn = ConexionSQL.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            cmbEspecialidad.removeAllItems();
            cmbEspecialidad.addItem("Seleccione una especialidad...");

            while (rs.next()) {
                cmbEspecialidad.addItem(rs.getString("nombre"));
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Error al cargar especialidades: " + e.getMessage(),
                    "Error de Base de Datos",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void cargarMedicos() {
        String especialidad = (String) cmbEspecialidad.getSelectedItem();
        cmbMedico.removeAllItems();

        if (especialidad == null || especialidad.equals("Seleccione una especialidad...")) {
            cmbMedico.addItem("Seleccione primero una especialidad");
            return;
        }

        cargarMedicosDesdeDB(especialidad);
    }

    private void cargarMedicosDesdeDB(String especialidad) {
        String sql = "SELECT DISTINCT m.ID_MEDICO, m.NOMBRE, m.TELEFONO " +
                "FROM MEDICO_Q m " +
                "INNER JOIN ESPECIALIDAD e ON m.ID_ESPECIALIDAD = e.ID_ESPECIALIDAD " +
                "WHERE e.nombre = ? " +
                "ORDER BY m.NOMBRE";

        try (Connection conn = ConexionSQL.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, especialidad);
            ResultSet rs = pstmt.executeQuery();

            cmbMedico.addItem("Seleccione un médico...");

            while (rs.next()) {
                String nombreCompleto = rs.getString("nombre");
                cmbMedico.addItem(nombreCompleto);
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Error al cargar médicos: " + e.getMessage() +
                            "\nSe cargarán valores por defecto.",
                    "Error de Base de Datos",
                    JOptionPane.WARNING_MESSAGE);
        }
    }

    private void buscarHorarios() {
        String medico = (String) cmbMedico.getSelectedItem();
        java.util.Date fecha = dateChooser.getDate();

        if (fecha == null || medico == null) {
            JOptionPane.showMessageDialog(this, "Por favor seleccione un médico y una fecha.");
            return;
        }

        modeloTabla.setRowCount(0);

        String[] horas = {
                "08:00 AM", "09:00 AM", "10:00 AM", "11:00 AM",
                "12:00 PM", "01:00 PM", "02:00 PM", "03:00 PM", "04:00 PM"
        };

        for (String hora : horas) {
            boolean ocupado = Math.random() > 0.5;
            String estado = ocupado ? "Ocupado" : "Libre";
            modeloTabla.addRow(new Object[]{hora, estado});
        }

        scrollTabla.setVisible(true);
        revalidate();
        repaint();
    }

    
    
    private void agendarCitaEnDB() {
    // Validar selección de horario
    int selectedRow = tablaHorarios.getSelectedRow();
    if (selectedRow == -1) {
        JOptionPane.showMessageDialog(this, 
            "Por favor seleccione un horario.", 
            "Error", 
            JOptionPane.WARNING_MESSAGE);
        return;
    }

    // Validar que el horario esté libre
    String estado = (String) modeloTabla.getValueAt(selectedRow, 1);
    if (!"Libre".equals(estado)) {
        JOptionPane.showMessageDialog(this, 
            "El horario seleccionado no está disponible.", 
            "Error", 
            JOptionPane.WARNING_MESSAGE);
        return;
    }

    // Validar cédula del paciente
    String cedulaPaciente = txtCedulaPaciente.getText().trim();
    if (cedulaPaciente.isEmpty()) {
        JOptionPane.showMessageDialog(this, 
            "Por favor ingrese la cédula del paciente.", 
            "Error", 
            JOptionPane.WARNING_MESSAGE);
        return;
    }

    // Validar que el paciente exista
    try {
        if (!validarPaciente(cedulaPaciente)) {
            JOptionPane.showMessageDialog(this, 
                "La cédula ingresada no corresponde a un paciente registrado.", 
                "Error", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
    } catch (SQLException ex) {
        JOptionPane.showMessageDialog(this, 
            "Error al validar paciente: " + ex.getMessage(), 
            "Error de Base de Datos", 
            JOptionPane.ERROR_MESSAGE);
        return;
    }

    // Validar médico seleccionado
    String nombreMedico = (String) cmbMedico.getSelectedItem();
    if (nombreMedico == null || nombreMedico.equals("Seleccione un médico...")) {
        JOptionPane.showMessageDialog(this, 
            "Por favor seleccione un médico.", 
            "Error", 
            JOptionPane.WARNING_MESSAGE);
        return;
    }

    // Validar fecha seleccionada
    if (dateChooser.getDate() == null) {
        JOptionPane.showMessageDialog(this, 
            "Por favor seleccione una fecha.", 
            "Error", 
            JOptionPane.WARNING_MESSAGE);
        return;
    }

    // Obtener datos para la cita
    String hora = (String) modeloTabla.getValueAt(selectedRow, 0);
    java.util.Date fechaUtil = dateChooser.getDate();
    java.sql.Date fecha = new java.sql.Date(fechaUtil.getTime());
    
    Connection conn = null;
    try {
        conn = ConexionSQL.conectar();
        conn.setAutoCommit(false); // Iniciar transacción

        // Obtener ID del médico
        int idMedico = obtenerIdMedico(conn, nombreMedico);
        if (idMedico == -1) {
            throw new SQLException("No se pudo obtener el ID del médico");
        }

        // Obtener ID del centro
        int idCentro = obtenerIdCentroMedico(conn, idMedico);
        if (idCentro == -1) {
            throw new SQLException("No se pudo obtener el centro del médico");
        }

        // Generar ID de cita
        int idCita = generarNuevoIdCita(conn);

        // 1. Insertar en CITA_Q
        String sqlCita = "INSERT INTO CITA_Q (ID_CITA, FECHA, HORA, ID_MEDICO, ID_CENTRO) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pstmtCita = conn.prepareStatement(sqlCita)) {
            pstmtCita.setInt(1, idCita);
            pstmtCita.setDate(2, fecha);
            pstmtCita.setString(3, hora);
            pstmtCita.setInt(4, idMedico);
            pstmtCita.setInt(5, idCentro);
            
            int filasAfectadas = pstmtCita.executeUpdate();
            
            if (filasAfectadas == 0) {
                throw new SQLException("No se pudo insertar la cita");
            }
        }

        // 2. Insertar en PACIENTE_CITA_Q
        String sqlPacienteCita = "INSERT INTO PACIENTE_CITA_Q (CEDULA, ID_CITA) VALUES (?, ?)";
        try (PreparedStatement pstmtPacienteCita = conn.prepareStatement(sqlPacienteCita)) {
            pstmtPacienteCita.setString(1, cedulaPaciente);
            pstmtPacienteCita.setInt(2, idCita);
            
            int filasPacienteCita = pstmtPacienteCita.executeUpdate();
            
            if (filasPacienteCita == 0) {
                throw new SQLException("No se pudo registrar la relación paciente-cita");
            }
        }

        conn.commit(); // Confirmar transacción
        
        JOptionPane.showMessageDialog(this, 
            "Cita agendada exitosamente:\n" +
            "Paciente: " + cedulaPaciente + "\n" +
            "Fecha: " + fecha + "\n" +
            "Hora: " + hora + "\n" +
            "Médico: " + nombreMedico,
            "Éxito", 
            JOptionPane.INFORMATION_MESSAGE);
        
        modeloTabla.setValueAt("Ocupado", selectedRow, 1);
    } catch (SQLException ex) {
        try {
            if (conn != null) {
                conn.rollback(); // Revertir en caso de error
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        JOptionPane.showMessageDialog(this, 
            "Error al agendar cita: " + ex.getMessage(),
            "Error de Base de Datos", 
            JOptionPane.ERROR_MESSAGE);
        ex.printStackTrace();
    } finally {
        try {
            if (conn != null) {
                conn.setAutoCommit(true);
                conn.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
    
    
    
    private int obtenerIdMedico(String nombreMedico) throws SQLException {
        String sql = "SELECT ID_MEDICO FROM MEDICO_Q WHERE NOMBRE = ?";
        try (Connection conn = ConexionSQL.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, nombreMedico);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next() ? rs.getInt("ID_MEDICO") : -1;
            }
        }
    }

    private int obtenerIdCentroMedico(int idMedico) throws SQLException {
        String sql = "SELECT ID_CENTRO FROM MEDICO_Q WHERE ID_MEDICO = ?";
        try (Connection conn = ConexionSQL.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idMedico);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next() ? rs.getInt("ID_CENTRO") : -1;
            }
        }
    }

    private int generarNuevoIdCita() throws SQLException {
        String sql = "SELECT MAX(ID_CITA) + 1 AS NUEVO_ID FROM CITA_Q";
        try (Connection conn = ConexionSQL.conectar();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            return rs.next() ? rs.getInt("NUEVO_ID") : 1;
        }
    }

    /**
     * Valida si un paciente existe en la base de datos
     */
    private boolean validarPaciente(String cedula) throws SQLException {
        String sql = "SELECT 1 FROM PACIENTE_Q WHERE CEDULA = ?";
        try (Connection conn = ConexionSQL.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, cedula);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    /**
     * Obtiene el ID de un médico por su nombre
     */
    private int obtenerIdMedico(Connection conn, String nombreMedico) throws SQLException {
        String sql = "SELECT ID_MEDICO FROM MEDICO_Q WHERE NOMBRE = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, nombreMedico);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next() ? rs.getInt("ID_MEDICO") : -1;
            }
        }
    }

    /**
     * Obtiene el ID del centro donde trabaja un médico
     */
    private int obtenerIdCentroMedico(Connection conn, int idMedico) throws SQLException {
        String sql = "SELECT ID_CENTRO FROM MEDICO_Q WHERE ID_MEDICO = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idMedico);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next() ? rs.getInt("ID_CENTRO") : -1;
            }
        }
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
            throw e;
        }
    }

    /**
     * Genera un nuevo ID para la cita
     */
    private int generarNuevoIdCita(Connection conn) throws SQLException {
        String sql = "SELECT MAX(ID_CITA) + 1 AS NUEVO_ID FROM CITA_Q";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            return rs.next() ? rs.getInt("NUEVO_ID") : 1;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new AgendamientoWindow());
    }

}