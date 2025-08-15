package org.vista;


import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import com.toedter.calendar.JDateChooser;
import org.config.ConexionSQL;

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

    public AgendamientoWindow(String nombrePaciente, String cedulaPaciente, String sedeSelect) {
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
        panelFormulario.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));
        panelFormulario.setBackground(new Color(157, 209, 241));

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
        jButton_AgendarCita.setForeground(Color.BLACK);
        jButton_AgendarCita.addActionListener(e -> agendarCitaEnDB());
        panelFormulario.add(jButton_AgendarCita);

        // Tabla de horarios
        String[] columnas = {"Hora", "Estado"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;  // tabla de solo lectura
            }
        };

        tablaHorarios = new JTable(modeloTabla);
        tablaHorarios.setRowHeight(25);
        tablaHorarios.setFont(new Font("Arial", Font.PLAIN, 13));
        tablaHorarios.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));

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
        String tableSuffix = this.sedeSelect.equalsIgnoreCase("QUITO") ? "Q" : "G";
        String sql = String.format("SELECT nombre FROM ESPECIALIDAD_%s ORDER BY nombre", tableSuffix);

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

        String sql = "SELECT DISTINCT mi.ID_MEDICO, mi.NOMBRE, mi.TELEFONO " +
                "FROM MEDICO_IDENTIFICACION mi " +
                "INNER JOIN MEDICO_PERFIL_PROFESIONAL_" + tableSuffix + " mp " +
                "    ON mi.ID_MEDICO = mp.ID_MEDICO " +
                "INNER JOIN ESPECIALIDAD_" + tableSuffix + " e " +
                "    ON mp.ID_ESPECIALIDAD = e.ID_ESPECIALIDAD " +
                "WHERE e.NOMBRE = ? " +
                "ORDER BY mi.NOMBRE";

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

            String sqlOcupados = "SELECT CAST(HORA AS TIME) as HORA FROM CITA_"+tableSuffix+" WHERE ID_MEDICO = ? AND FECHA = ?";
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

    // Método para obtener ID del médico
    private int obtenerIdMedico(Connection conn, String nombreMedico) throws SQLException {
        String tableSuffix = this.sedeSelect.equalsIgnoreCase("QUITO") ? "Q" : "G";

        String sql = "SELECT mi.ID_MEDICO " +
                "FROM MEDICO_IDENTIFICACION mi " +
                "INNER JOIN MEDICO_PERFIL_PROFESIONAL_" + tableSuffix + " mp " +
                "    ON mi.ID_MEDICO = mp.ID_MEDICO " +
                "WHERE mi.NOMBRE = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, nombreMedico);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("ID_MEDICO");
                }
            }
        }
        return -1; // Si no se encuentra
    }

    private void agendarCitaEnDB() {
        int selectedRow = tablaHorarios.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Por favor seleccione un horario.", "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String estado = (String) modeloTabla.getValueAt(selectedRow, 1);
        if (!"Libre".equals(estado)) {
            JOptionPane.showMessageDialog(this, "El horario seleccionado no está disponible.", "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String cedulaPaciente = this.cedulaPaciente.trim();
        if (cedulaPaciente.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor ingrese la cédula del paciente.", "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            if (!validarPaciente(cedulaPaciente)) {
                JOptionPane.showMessageDialog(this, "La cédula ingresada no corresponde a un paciente registrado.", "Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al validar paciente: " + ex.getMessage(), "Error de Base de Datos", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String nombreMedico = (String) cmbMedico.getSelectedItem();
        if (nombreMedico == null || nombreMedico.equals("Seleccione un médico...")) {
            JOptionPane.showMessageDialog(this, "Por favor seleccione un médico.", "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (dateChooser.getDate() == null) {
            JOptionPane.showMessageDialog(this, "Por favor seleccione una fecha.", "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String horaStr = (String) modeloTabla.getValueAt(selectedRow, 0);
        Time horaTime = convertirHoraStringATime(horaStr);
        if (horaTime == null) {
            JOptionPane.showMessageDialog(this, "Formato de hora inválido: " + horaStr, "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        java.sql.Date fecha = new java.sql.Date(dateChooser.getDate().getTime());
        Connection conn = null;
        boolean transactionStarted = false;

        try {
            conn = ConexionSQL.conectar();

            // Verificar disponibilidad antes de iniciar transacción
            if (verificarDisponibilidadHorario(conn, nombreMedico, fecha, horaTime)) {
                JOptionPane.showMessageDialog(this, "El horario ya no está disponible. Por favor seleccione otro.", "Horario Ocupado", JOptionPane.WARNING_MESSAGE);
                buscarHorarios();
                return;
            }

            int idMedico = obtenerIdMedico(conn, nombreMedico);
            if (idMedico == -1) {
                JOptionPane.showMessageDialog(this, "No se pudo obtener el ID del médico", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int idCita = generarNuevoIdCita(conn);

            // Iniciar transacción
            conn.setAutoCommit(false);
            try (Statement stmt = conn.createStatement()) {
                stmt.execute("SET XACT_ABORT ON");
            }

            try (CallableStatement cs = conn.prepareCall("BEGIN DISTRIBUTED TRANSACTION")) {
                cs.execute();
                transactionStarted = true;
            }

            // Insertar SOLO en la tabla correspondiente a la ciudad
            String tableSuffix = this.sedeSelect.equalsIgnoreCase("QUITO") ? "Q" : "G";
            String sqlInsert = String.format(
                    "INSERT INTO CITA_%s (ID_CITA, FECHA, CEDULA, HORA, ID_MEDICO, ID_CENTRO) VALUES (?, ?, ?, ?, ?, ?)",
                    tableSuffix
            );

            try (PreparedStatement pstmt = conn.prepareStatement(sqlInsert)) {
                pstmt.setInt(1, idCita);
                pstmt.setDate(2, fecha);
                pstmt.setString(3, cedulaPaciente);
                pstmt.setTime(4, horaTime);
                pstmt.setInt(5, idMedico);
                pstmt.setString(6, tableSuffix);

                int filas = pstmt.executeUpdate();
                if (filas <= 0) {
                    throw new SQLException("No se pudo insertar la cita en la sede " + tableSuffix);
                }
            }

            // Commit de la transacción
            try (CallableStatement cs = conn.prepareCall("COMMIT TRANSACTION")) {
                cs.execute();
            }

            String sqlInsertHistorial = String.format(
                    "INSERT INTO HISTORIAL_%s (ID_CONSULTA, FECHA, CEDULA, ID_CENTRO, ID_MEDICO, ID_ESPECIALIDAD) " +
                            "VALUES (?, ?, ?, ?, ?, ?)",
                    tableSuffix
            );

            try (PreparedStatement pstmtHist = conn.prepareStatement(sqlInsertHistorial)) {
                pstmtHist.setInt(1, idCita); // Usamos el mismo ID de cita como ID_CONSULTA
                pstmtHist.setDate(2, fecha);
                pstmtHist.setString(3, cedulaPaciente);
                pstmtHist.setString(4, tableSuffix);
                pstmtHist.setInt(5, idMedico);

                // Obtener ID de especialidad seleccionada
                String nombreEspecialidad = (String) cmbEspecialidad.getSelectedItem();
                int idEspecialidad = obtenerIdEspecialidad(conn, nombreEspecialidad, tableSuffix);
                pstmtHist.setInt(6, idEspecialidad);

                int filasHist = pstmtHist.executeUpdate();
                if (filasHist <= 0) {
                    throw new SQLException("No se pudo insertar en HISTORIAL_" + tableSuffix);
                }
            }

            JOptionPane.showMessageDialog(this,
                    "Cita agendada exitosamente en la sede " + tableSuffix + ":\n" +
                            "Paciente: " + this.nombrePaciente + "\n" +
                            "Cédula: " + cedulaPaciente + "\n" +
                            "Fecha: " + fecha + "\n" +
                            "Hora: " + horaStr + "\n" +
                            "Médico: " + nombreMedico,
                    "Éxito",
                    JOptionPane.INFORMATION_MESSAGE);

            modeloTabla.setValueAt("Ocupado", selectedRow, 1);

        } catch (SQLException ex) {
            try {
                if (transactionStarted && conn != null) {
                    try (CallableStatement cs = conn.prepareCall("ROLLBACK TRANSACTION")) {
                        cs.execute();
                    }
                }
            } catch (SQLException rollbackEx) {
                System.err.println("Error en rollback: " + rollbackEx.getMessage());
            }

            JOptionPane.showMessageDialog(this, "Error al agendar cita: " + ex.getMessage(), "Error de Base de Datos", JOptionPane.ERROR_MESSAGE);
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


    // Método para verificar disponibilidad del horario antes de insertar
    private boolean verificarDisponibilidadHorario(Connection conn, String nombreMedico, java.sql.Date fecha, Time hora) throws SQLException {
        int idMedico = obtenerIdMedico(conn, nombreMedico);
        if (idMedico == -1) return false;

        String tableSuffix = this.sedeSelect.equalsIgnoreCase("QUITO") ? "Q" : "G";
        String sql = "SELECT COUNT(*) FROM CITA_" + tableSuffix + " WHERE ID_MEDICO = ? AND FECHA = ? AND CAST(HORA AS TIME) = CAST(? AS TIME)";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idMedico);
            pstmt.setDate(2, fecha);
            pstmt.setTime(3, hora);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0; // true si ya existe una cita
                }
            }
        }
        return false;
    }

    // Método para generar nuevo ID de cita usando vista global
    private int generarNuevoIdCita(Connection conn) throws SQLException {
        // Intentar usar la vista global primero
        try {
            String sqlGlobal = "SELECT MAX(ID_CITA) FROM [VID].[BQuito2].dbo.[CITA_GLOBAL]";
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sqlGlobal)) {
                if (rs.next()) {
                    int maxId = rs.getInt(1);
                    return maxId > 0 ? maxId + 1 : 1;
                }
            }
        } catch (SQLException e) {
            System.err.println("No se pudo acceder a CITA_GLOBAL: " + e.getMessage());
        }

        // Fallback: usar máximo de ambas tablas
        String tableSuffix = this.sedeSelect.equalsIgnoreCase("QUITO") ? "Q" : "G";
        String sqlLocal = "SELECT MAX(ID_CITA) FROM CITA_" + tableSuffix;

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sqlLocal)) {
            if (rs.next()) {
                int maxId = rs.getInt(1);
                return maxId > 0 ? maxId + 1 : 1;
            }
            return 1;
        }
    }

    private Time convertirHoraStringATime(String horaStr) {
        try {
            SimpleDateFormat displayFormat = new SimpleDateFormat("hh:mm a", Locale.US);
            Date horaDate = displayFormat.parse(horaStr);

            // Crear un Time solo con la parte de tiempo, no con fecha
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

    private boolean validarPaciente(String cedula) throws SQLException {
        String sql = "SELECT 1 FROM PACIENTE WHERE CEDULA = ?";
        try (Connection conn = ConexionSQL.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, cedula);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    private int obtenerIdEspecialidad(Connection conn, String nombreEspecialidad, String tableSuffix) throws SQLException {
        String sql = "SELECT ID_ESPECIALIDAD FROM ESPECIALIDAD_" + tableSuffix + " WHERE NOMBRE = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, nombreEspecialidad);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                } else {
                    throw new SQLException("No se encontró la especialidad " + nombreEspecialidad + " en la sede " + tableSuffix);
                }
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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new AgendamientoWindow());
    }
}