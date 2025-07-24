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

/**
 * Ventana principal para la gestión de citas médicas.
 * Permite a los usuarios agendar citas con médicos según especialidad y fecha.
 */
public class AgendamientoWindow extends JFrame{


    private JTextField txtCedulaPaciente;
    private JComboBox<String> cmbEspecialidad;
    private JComboBox<String> cmbMedico;
    private JDateChooser dateChooser;
    private JButton jButton_BuscarHorarios;
    private JButton jButton_AgendarCita;

    // Tabla de horarios
    private JTable tablaHorarios;
    private DefaultTableModel modeloTabla;
    private JScrollPane scrollTabla;

    public AgendamientoWindow() {
        setTitle("Gestión de Citas Médicas");
        setSize(700, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Panel principal
        JPanel panelFormulario = new JPanel();
        panelFormulario.setLayout(new GridLayout(6, 2, 10, 10));
        panelFormulario.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Campo: Cédula del Paciente
        panelFormulario.add(new JLabel("Cédula del Paciente:"));
        txtCedulaPaciente = new JTextField();
        panelFormulario.add(txtCedulaPaciente);

        // Campo: Especialidad Médica
        panelFormulario.add(new JLabel("Especialidad Médica:"));
        // Inicializar el ComboBox vacío (sin datos estáticos)
        cmbEspecialidad = new JComboBox<>();
        panelFormulario.add(cmbEspecialidad);

        // Campo: Médico
        panelFormulario.add(new JLabel("Médico:"));
        cmbMedico = new JComboBox<>();
        panelFormulario.add(cmbMedico);

        cmbEspecialidad.addActionListener(e -> cargarMedicos());
        // Cargar las especialidades desde la base de datos
        cargarEspecialidadesDesdeDB();

        // Campo: Fecha
        panelFormulario.add(new JLabel("Fecha:"));
        dateChooser = new JDateChooser();
        dateChooser.setDateFormatString("yyyy-MM-dd");
        panelFormulario.add(dateChooser);

        // Botón buscar horarios
        JButton btnBuscarHorarios = new JButton("Buscar Horarios Disponibles");
        btnBuscarHorarios.addActionListener(e -> buscarHorarios());
        panelFormulario.add(btnBuscarHorarios);

        // Panel para tabla de horarios
        String[] columnas = {"Hora", "Estado"};
        modeloTabla = new DefaultTableModel(columnas, 0);
        tablaHorarios = new JTable(modeloTabla);
        scrollTabla = new JScrollPane(tablaHorarios);
        scrollTabla.setVisible(false); // Oculto al inicio

        // Botón para agendar cita
        jButton_AgendarCita = new JButton("Agendar Cita");
        jButton_AgendarCita.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = tablaHorarios.getSelectedRow();
                if (selectedRow != -1) {
                    String hora = (String) modeloTabla.getValueAt(selectedRow, 0);
                    String estado = (String) modeloTabla.getValueAt(selectedRow, 1);
                    if ("Libre".equals(estado)) {
                        JOptionPane.showMessageDialog(AgendamientoWindow.this, "Cita agendada para " + hora);
                        modeloTabla.setValueAt("Ocupado", selectedRow, 1); // Actualizar estado a Ocupado
                    } else {
                        JOptionPane.showMessageDialog(AgendamientoWindow.this, "El horario ya está ocupado.");
                    }
                } else {
                    JOptionPane.showMessageDialog(AgendamientoWindow.this, "Por favor seleccione un horario.");

                }
            }
        });

        // Agregar todo al JFrameG
        add(panelFormulario, BorderLayout.NORTH);
        add(scrollTabla, BorderLayout.CENTER);

        dateChooser.setMinSelectableDate(new Date());
        ((JTextField) dateChooser.getDateEditor().getUiComponent()).setEditable(false); // Hacer el campo de fecha no editable

        setVisible(true);
    }

    private void cargarEspecialidadesDesdeDB() {
        String sql = "SELECT nombre FROM ESPECIALIDAD ORDER BY nombre";

        try (Connection conn = ConexionSQL.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            // Limpiar el ComboBox
            cmbEspecialidad.removeAllItems();
            cmbEspecialidad.addItem("Seleccione una especialidad...");

            // Cargar especialidades
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

        // Verificar si hay una especialidad válida seleccionada
        if (especialidad == null || especialidad.equals("Seleccione una especialidad...")) {
            cmbMedico.addItem("Seleccione primero una especialidad");
            return;
        }

        // Cargar médicos desde la base de datos
        cargarMedicosDesdeDB(especialidad);
    }

    private void cargarMedicosDesdeDB(String especialidad) {
        String sql = "SELECT DISTINCT m.ID_MEDICO, m.NOMBRE, m.TELEFONO " +
                "FROM MEDICO m " +
                "INNER JOIN ESPECIALIDAD e ON m.ID_ESPECIALIDAD = e.ID_ESPECIALIDAD " +
                "WHERE e.nombre = ? " +
                "ORDER BY m.NOMBRE";

        try (Connection conn = ConexionSQL.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, especialidad);
            ResultSet rs = pstmt.executeQuery();

            // Agregar opción por defecto
            cmbMedico.addItem("Seleccione un médico...");

            // Cargar médicos desde la base de datos
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

        // Limpiar tabla
        modeloTabla.setRowCount(0);

        // Simular horarios (8:00 AM a 5:00 PM)
        String[] horas = {
                "08:00 AM", "09:00 AM", "10:00 AM", "11:00 AM",
                "12:00 PM", "01:00 PM", "02:00 PM", "03:00 PM", "04:00 PM"
        };

        // Simular algunos horarios ocupados (aleatorio)
        for (String hora : horas) {
            boolean ocupado = Math.random() > 0.5; // Simulación aleatoria
            String estado = ocupado ? "Ocupado" : "Libre";
            modeloTabla.addRow(new Object[]{hora, estado});
        }

        // Mostrar la tabla
        scrollTabla.setVisible(true);
        revalidate();
        repaint();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new AgendamientoWindow());
    }

}
