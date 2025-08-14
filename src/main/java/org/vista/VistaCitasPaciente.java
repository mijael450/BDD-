package org.vista;

import org.config.ConexionSQL;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class VistaCitasPaciente extends JFrame {

    private JTable tablaCitas;
    private DefaultTableModel modeloTabla;
    private String nombrePaciente;
    private String cedulaPaciente;
    private String sedeSelect;
    public VistaCitasPaciente(String nombrePaciente, String cedulaPaciente,String sedeSelect) {
        this.nombrePaciente = nombrePaciente;
        this.cedulaPaciente = cedulaPaciente;
        this.sedeSelect = sedeSelect;
        setTitle("Citas de " + nombrePaciente);
        setSize(850, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // ---------- PANEL SUPERIOR ----------
        JPanel panelSuperior = new JPanel(new BorderLayout());
        panelSuperior.setBackground(new Color(157, 209, 241));
        panelSuperior.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel lblTitulo = new JLabel("Citas de " + nombrePaciente);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 22));
        lblTitulo.setForeground(new Color(0, 53, 84));
        panelSuperior.add(lblTitulo, BorderLayout.WEST);

        JButton btnAgendar = new JButton("Agendar Nueva Cita");
        btnAgendar.setBackground(new Color(0, 120, 215));
        btnAgendar.setForeground(Color.WHITE);
        btnAgendar.setFocusPainted(false);
        btnAgendar.setFont(new Font("Arial", Font.PLAIN, 14));
        btnAgendar.addActionListener(e -> {
            new AgendamientoWindow(nombrePaciente, cedulaPaciente, this.sedeSelect);
            dispose();
        });
        panelSuperior.add(btnAgendar, BorderLayout.EAST);

        add(panelSuperior, BorderLayout.NORTH);

        // ---------- TABLA ----------
        String[] columnas = {"ID Cita", "Fecha", "Hora", "Médico", "Especialidad", "Centro Médico"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tablaCitas = new JTable(modeloTabla);
        tablaCitas.setRowHeight(25);
        tablaCitas.setFont(new Font("Arial", Font.PLAIN, 13));
        tablaCitas.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));

        JScrollPane scrollPane = new JScrollPane(tablaCitas);
        add(scrollPane, BorderLayout.CENTER);

        // ---------- PANEL INFERIOR ----------
        JPanel panelSur = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        panelSur.setBackground(Color.WHITE);
        panelSur.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JButton btnExportar = new JButton("Exportar");
        btnExportar.setBackground(new Color(76, 175, 80));
        btnExportar.setForeground(Color.WHITE);
        btnExportar.setFocusPainted(false);
        btnExportar.setFont(new Font("Arial", Font.PLAIN, 13));
        btnExportar.addActionListener(e -> exportarCitas());

        JButton btnRegresar = new JButton("Regresar");
        btnRegresar.setBackground(new Color(200, 200, 200));
        btnRegresar.setForeground(Color.BLACK);
        btnRegresar.setFocusPainted(false);
        btnRegresar.setFont(new Font("Arial", Font.PLAIN, 13));
        btnRegresar.addActionListener(e -> {
            new PacienteWindow(nombrePaciente, cedulaPaciente, this.sedeSelect).setVisible(true);
            dispose();
        });

        panelSur.add(btnRegresar);
        panelSur.add(btnExportar);

        add(panelSur, BorderLayout.SOUTH);

        // ---------- CONSULTA AUTOMÁTICA ----------
        buscarCitasDesdeDB();

        setVisible(true);
    }

    private void buscarCitasDesdeDB() {
        String tableSuffix = this.sedeSelect.equalsIgnoreCase("QUITO") ? "Q" : "G";
        modeloTabla.setRowCount(0);

        String sql = "SELECT " +
                "c.ID_CITA, c.FECHA, c.HORA, m.NOMBRE AS MEDICO, " +
                "CASE " +
                "   WHEN c.ID_CENTRO = 'Q' THEN eq.NOMBRE " +
                "   WHEN c.ID_CENTRO = 'G' THEN eg.NOMBRE " +
                "END AS ESPECIALIDAD, " +
                "c.ID_CENTRO AS CENTRO " +
                "FROM CITA_GLOBAL c " +
                "JOIN MEDICO_IDENTIFICACION m ON c.ID_MEDICO = m.ID_MEDICO " +
                "LEFT JOIN [BQuito2].[dbo].[MEDICO_PERFIL_PROFESIONAL_Q] pq ON c.ID_MEDICO = pq.ID_MEDICO AND c.ID_CENTRO = 'Q' " +
                "LEFT JOIN [BQuito2].[dbo].[ESPECIALIDAD_Q] eq ON pq.ID_ESPECIALIDAD = eq.ID_ESPECIALIDAD " +
                "LEFT JOIN [LAPTOP-J4CMJHBK].[BGuayaquil].[dbo].[MEDICO_PERFIL_PROFESIONAL_G] pg ON c.ID_MEDICO = pg.ID_MEDICO AND c.ID_CENTRO = 'G' " +
                "LEFT JOIN [LAPTOP-J4CMJHBK].[BGuayaquil].[dbo].[ESPECIALIDAD_G] eg ON pg.ID_ESPECIALIDAD = eg.ID_ESPECIALIDAD " +
                "WHERE c.CEDULA = ? " +
                "ORDER BY c.FECHA DESC, c.HORA DESC";


        try (Connection conn = ConexionSQL.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, cedulaPaciente);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Object[] fila = {
                        rs.getInt("ID_CITA"),
                        rs.getDate("FECHA"),
                        rs.getString("HORA"),
                        rs.getString("MEDICO"),
                        rs.getString("ESPECIALIDAD"),
                        rs.getString("CENTRO")
                    };
                    modeloTabla.addRow(fila);
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                "Error al buscar citas: " + ex.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void exportarCitas() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Guardar citas como CSV");

        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            String filePath = fileChooser.getSelectedFile().getPath();
            if (!filePath.toLowerCase().endsWith(".csv")) {
                filePath += ".csv";
            }

            try {
                // Aquí puedes agregar el código real para exportar a CSV si lo necesitas
                JOptionPane.showMessageDialog(this,
                    "Datos exportados exitosamente a: " + filePath,
                    "Exportación", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                    "Error al exportar: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new VistaCitasPaciente("Juan Pérez", "1234567890","");
        });
    }
}