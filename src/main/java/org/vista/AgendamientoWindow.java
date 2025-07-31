package org.vista;
import com.toedter.calendar.JDateChooser;
import org.config.ConexionSQL;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;
import java.util.Random;
import java.util.Set;

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
    private String sedeSelect;

    public AgendamientoWindow() throws HeadlessException {
        this.nombrePaciente = "";
        this.cedulaPaciente = "";
    }

    public AgendamientoWindow(String nombrePaciente, String cedulaPaciente,  String sedeSelect) {
        this.nombrePaciente = nombrePaciente; 
        this.cedulaPaciente = cedulaPaciente;
        this.sedeSelect = sedeSelect;
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
        JLabel lblNombreValue = new JLabel(this.nombrePaciente);
        lblNombreValue.setForeground(Color.WHITE);
        lblNombreValue.setFont(new Font("Arial", Font.PLAIN, 14));
        JLabel lblCedula = new JLabel("Cédula:");
        lblCedula.setForeground(Color.WHITE);
        lblCedula.setFont(new Font("Arial", Font.BOLD, 14));
        JLabel lblCedulaValue = new JLabel(this.cedulaPaciente);
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
        // Botón "Regresar"
        JButton btnRegresar = new JButton("Regresar");
        btnRegresar.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnRegresar.setBackground(new Color(200, 200, 200));
        btnRegresar.setForeground(Color.BLACK);
        btnRegresar.setFocusPainted(false);
        btnRegresar.setFont(new Font("Arial", Font.PLAIN, 13));
        btnRegresar.setMaximumSize(new Dimension(200, 30));
        btnRegresar.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Acción del botón: volver a PacienteWindow
        btnRegresar.addActionListener(e -> {
            new PacienteWindow(nombrePaciente, cedulaPaciente, this.sedeSelect).setVisible(true);
            dispose();
        });

        panelIzquierdo.add(Box.createVerticalGlue()); // empuja el botón hacia abajo
        panelIzquierdo.add(Box.createRigidArea(new Dimension(0, 20)));
        panelIzquierdo.add(btnRegresar);

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
        String tableSuffix = this.sedeSelect.equalsIgnoreCase("QUITO") ? "Q" : "G";
        String sql = "SELECT DISTINCT m.ID_MEDICO, m.NOMBRE, m.TELEFONO " +
                "FROM MEDICO_"+tableSuffix+" m " +
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
    java.util.Date fechaUtil = dateChooser.getDate();

    if (fechaUtil == null || medico == null || medico.equals("Seleccione un médico...")) {
        JOptionPane.showMessageDialog(this, "Por favor seleccione un médico y una fecha válida.");
        return;
    }

    java.sql.Date fecha = new java.sql.Date(fechaUtil.getTime());
    modeloTabla.setRowCount(0);
    
    Connection conn = null;
    try {
        conn = ConexionSQL.conectar();
        
        // Obtener ID del médico
        int idMedico = obtenerIdMedico(conn, medico);
        if (idMedico == -1) {
            JOptionPane.showMessageDialog(this, "No se encontró el médico seleccionado.");
            return;
        }
        
        // Obtener horarios ocupados
        Set<String> horariosOcupados = new HashSet<>();
        String tableSuffix = this.sedeSelect.equalsIgnoreCase("QUITO") ? "Q" : "G";
        
        String sqlOcupados = "SELECT HORA FROM CITA_"+tableSuffix+" WHERE ID_MEDICO = ? AND FECHA = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sqlOcupados)) {
            pstmt.setInt(1, idMedico);
            pstmt.setDate(2, fecha);

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Time horaTime = rs.getTime("HORA");
                String horaFormateada = formatearHoraAMPM(horaTime);
                horariosOcupados.add(horaFormateada);
            }
        }
        
        // Mostrar todos los horarios posibles
        String[] horasDisponibles = {
            "08:00 AM", "09:00 AM", "10:00 AM", "11:00 AM",
            "12:00 PM", "01:00 PM", "02:00 PM", "03:00 PM", "04:00 PM"
        };
        
        for (String hora : horasDisponibles) {
            String estado = horariosOcupados.contains(hora) ? "Ocupado" : "Libre";
            modeloTabla.addRow(new Object[]{hora, estado});
        }
        
    } catch (SQLException ex) {
        JOptionPane.showMessageDialog(this, 
            "Error al consultar horarios: " + ex.getMessage(),
            "Error de Base de Datos", 
            JOptionPane.ERROR_MESSAGE);
        ex.printStackTrace();
    } finally {
        try {
            if (conn != null) conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    scrollTabla.setVisible(true);
    revalidate();
    repaint();
}
    
    

    // Método auxiliar para formatear la hora de Time a String
    private String formatearHora(Time horaTime) {
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");
        return sdf.format(horaTime).toUpperCase();
    }

    private String formatearHoraAMPM(Time horaTime) {
    SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.US);
    return sdf.format(horaTime);
}
    
    // Método para obtener ID del médico (reutilizado del código anterior)
    private int obtenerIdMedico(Connection conn, String nombreMedico) throws SQLException {
        String tableSuffix = this.sedeSelect.equalsIgnoreCase("QUITO") ? "Q" : "G";
        String sql = "SELECT ID_MEDICO FROM MEDICO_"+tableSuffix+" WHERE NOMBRE = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, nombreMedico);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("ID_MEDICO");
                }
            }
        }
        return -1;
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
    String cedulaPaciente = this.cedulaPaciente.trim();
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

    // Obtener y convertir hora seleccionada
    String horaStr = (String) modeloTabla.getValueAt(selectedRow, 0);
    Time horaTime = convertirHoraStringATime(horaStr);
    if (horaTime == null) {
        JOptionPane.showMessageDialog(this, 
            "Formato de hora inválido: " + horaStr,
            "Error", 
            JOptionPane.ERROR_MESSAGE);
        return;
    }

    java.util.Date fechaUtil = dateChooser.getDate();
    java.sql.Date fecha = new java.sql.Date(fechaUtil.getTime());
    
    Connection conn = null;
    try {
        conn = ConexionSQL.conectar();
        //System.out.println("Base de datos conectada: " + conn.getCatalog());

        conn.setAutoCommit(false); // Iniciar transacción

        // 1. Obtener ID del médico
        int idMedico = obtenerIdMedico(conn, nombreMedico);
        if (idMedico == -1) {
            throw new SQLException("No se pudo obtener el ID del médico");
        }

        // 2. Obtener ID del centro (aleatorio entre 101, 102, 103)
        int idCentro = obtenerIdCentroMedico(idMedico);//obtenerCentroAleatorio();

        // 3. Generar ID de cita
        int idCita = generarNuevoIdCita(conn);

        // 4. Insertar en CITA_Q
        String tableSuffix = this.sedeSelect.equalsIgnoreCase("QUITO") ? "Q" : "G";
        String sqlCita = "INSERT INTO CITA_"+tableSuffix+" (ID_CITA, FECHA, HORA, ID_MEDICO, ID_CENTRO) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pstmtCita = conn.prepareStatement(sqlCita)) {
            pstmtCita.setInt(1, idCita);
            pstmtCita.setDate(2, fecha);
            pstmtCita.setTime(3, horaTime);
            pstmtCita.setInt(4, idMedico);
            pstmtCita.setInt(5, idCentro);
            
            if (pstmtCita.executeUpdate() == 0) {
                throw new SQLException("No se pudo insertar la cita");
            }
        }

        // 5. Insertar en PACIENTE_CITA_Q
        String sqlPacienteCita = "INSERT INTO PACIENTE_CITA_"+tableSuffix+" (CEDULA, ID_CITA) VALUES (?, ?)";
        try (PreparedStatement pstmtPacienteCita = conn.prepareStatement(sqlPacienteCita)) {
            pstmtPacienteCita.setString(1, cedulaPaciente);
            pstmtPacienteCita.setInt(2, idCita);
            
            if (pstmtPacienteCita.executeUpdate() == 0) {
                throw new SQLException("No se pudo registrar la relación paciente-cita");
            }
        }

        conn.commit(); // Confirmar transacción
        
        JOptionPane.showMessageDialog(this, 
            "Cita agendada exitosamente:\n" +
            "Paciente: " + cedulaPaciente + "\n" +
            "Fecha: " + fecha + "\n" +
            "Hora: " + horaStr + "\n" +
            "Médico: " + nombreMedico + "\n" +
            "Centro: " + idCentro,
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

    
    

// Método para seleccionar un centro médico aleatorio (101, 102 o 103)
    private int obtenerCentroAleatorio() {
        if(this.sedeSelect.equalsIgnoreCase("QUITO")) {
        int[] centros = {101, 102, 103};
        Random random = new Random();
        return centros[random.nextInt(centros.length)];
        }else{
        int[] centros = {201, 202, 203};
        Random random = new Random();
        return centros[random.nextInt(centros.length)];
        }
    }

    // Método para generar un nuevo ID de cita
    private int generarNuevoIdCita(Connection conn) throws SQLException {
        String tableSuffix = this.sedeSelect.equalsIgnoreCase("QUITO") ? "Q" : "G";
        String sql = "SELECT MAX(ID_CITA) FROM CITA_"+tableSuffix;
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt(1) + 1;
            }
            return 1; // Si no hay citas, empezar con 1
        }
    }

    // Método para obtener ID del médico
 
    
    private Time convertirHoraStringATime(String horaStr) {
    try {
        // Crear formateador con Locale.US para asegurar que entienda AM/PM
        SimpleDateFormat displayFormat = new SimpleDateFormat("hh:mm a", Locale.US);
        Date horaDate = displayFormat.parse(horaStr);
        
        // Convertir a java.sql.Time
        return new Time(horaDate.getTime());
    } catch (ParseException e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, 
            "Error al convertir la hora: " + horaStr,
            "Error de formato", 
            JOptionPane.ERROR_MESSAGE);
        return null;
    }
}
    
    private int obtenerIdMedico(String nombreMedico) throws SQLException {
        String tableSuffix = this.sedeSelect.equalsIgnoreCase("QUITO") ? "Q" : "G";
        String sql = "SELECT ID_MEDICO FROM MEDICO_"+tableSuffix+" WHERE NOMBRE = ?";
        try (Connection conn = ConexionSQL.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, nombreMedico);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next() ? rs.getInt("ID_MEDICO") : -1;
            }
        }
    }

    private int obtenerIdCentroMedico(int idMedico) throws SQLException {
        String tableSuffix = this.sedeSelect.equalsIgnoreCase("QUITO") ? "Q" : "G";
        String sql = "SELECT ID_CENTRO FROM MEDICO_"+tableSuffix+" WHERE ID_MEDICO = ?";
        try (Connection conn = ConexionSQL.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idMedico);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next() ? rs.getInt("ID_CENTRO") : -1;
            }
        }
    }

    private int generarNuevoIdCita() throws SQLException {
        String tableSuffix = this.sedeSelect.equalsIgnoreCase("QUITO") ? "Q" : "G";
        String sql = "SELECT MAX(ID_CITA) + 1 AS NUEVO_ID FROM CITA_"+tableSuffix;
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
        String tableSuffix = this.sedeSelect.equalsIgnoreCase("QUITO") ? "Q" : "G";
        String sql = "SELECT 1 FROM PACIENTE_"+tableSuffix+" WHERE CEDULA = ?";
        try (Connection conn = ConexionSQL.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, cedula);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    private int obtenerIdCentroMedico(Connection conn, int idMedico) throws SQLException {
        String tableSuffix = this.sedeSelect.equalsIgnoreCase("QUITO") ? "Q" : "G";
        String sql = "SELECT ID_CENTRO FROM MEDICO_"+tableSuffix+" WHERE ID_MEDICO = ?";
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
 
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new AgendamientoWindow());
    }

}