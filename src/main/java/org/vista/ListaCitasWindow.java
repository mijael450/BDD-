package org.vista;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import org.config.ConexionSQL;
public class ListaCitasWindow extends JFrame {
    private String sedeSelect;

    public ListaCitasWindow(String sedeSelect) {
        this.sedeSelect = sedeSelect;
        initializeWindow();
        createComponents();
    }

    private void initializeWindow() {
        setTitle("Lista de Citas - " + sedeSelect);
        setSize(800, 600);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    private void createComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        add(mainPanel);

        // Modelo de tabla
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("ID Cita");
        model.addColumn("Fecha");
        model.addColumn("Hora");
        model.addColumn("Paciente");
        model.addColumn("Médico");
        model.addColumn("Centro");

        // Llenar la tabla con datos de la base de datos
        try {
            Connection conn = ConexionSQL.conectar();
            String sql = "SELECT c.ID_CITA, c.FECHA, c.HORA, p.NOMBRE AS PACIENTE, " +
                         "m.NOMBRE AS MEDICO, c.ID_CENTRO " +
                         "FROM CITA_GLOBAL c " +
                         "JOIN PACIENTE p ON c.CEDULA = p.CEDULA " +
                         "JOIN MEDICO_IDENTIFICACION m ON c.ID_MEDICO = m.ID_MEDICO " +
                         "ORDER BY c.FECHA, c.HORA";
            
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("ID_CITA"),
                    rs.getDate("FECHA"),
                    rs.getTime("HORA"),
                    rs.getString("PACIENTE"),
                    rs.getString("MEDICO"),
                    rs.getString("ID_CENTRO")
                });
            }

            conn.close();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al cargar citas: " + ex.getMessage(), 
                                       "Error", JOptionPane.ERROR_MESSAGE);
        }

        // Tabla
        JTable table = new JTable(model);
        table.setAutoCreateRowSorter(true);
        JScrollPane scrollPane = new JScrollPane(table);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Botón de regreso
        JButton btnRegresar = new JButton("Regresar");
        btnRegresar.addActionListener(e -> {
            new AdminWindow(sedeSelect).setVisible(true);
            dispose();
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(btnRegresar);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
    }
}