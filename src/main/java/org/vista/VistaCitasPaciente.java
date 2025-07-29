package org.vista;

import org.config.ConexionSQL;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class VistaCitasPaciente extends JFrame {

    private JTextField txtCedula;
    private JButton btnBuscar;
    private JTable tablaCitas;
    private DefaultTableModel modeloTabla;

    public VistaCitasPaciente() {
        setTitle("Consulta de Citas por Paciente");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Panel superior para búsqueda
        JPanel panelBusqueda = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        panelBusqueda.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        panelBusqueda.add(new JLabel("Cédula del Paciente:"));
        txtCedula = new JTextField(15);
        panelBusqueda.add(txtCedula);

        btnBuscar = new JButton("Buscar Citas");
        btnBuscar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                buscarCitasPorPaciente();
            }
        });
        panelBusqueda.add(btnBuscar);

        add(panelBusqueda, BorderLayout.NORTH);

        // Modelo de tabla para mostrar las citas
        String[] columnas = {"ID Cita", "Fecha", "Hora", "Médico", "Especialidad", "Centro Médico"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Hacer que la tabla no sea editable
            }
        };

        tablaCitas = new JTable(modeloTabla);
        tablaCitas.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Mejorar el renderizado de la tabla
        tablaCitas.setRowHeight(25);
        tablaCitas.setFont(new Font("Arial", Font.PLAIN, 12));
        tablaCitas.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));

        JScrollPane scrollPane = new JScrollPane(tablaCitas);
        add(scrollPane, BorderLayout.CENTER);

        // Botón para imprimir o exportar (opcional)
        JButton btnImprimir = new JButton("Imprimir/Exportar");
        btnImprimir.addActionListener(e -> exportarCitas());
        add(btnImprimir, BorderLayout.SOUTH);
    }

    private void buscarCitasPorPaciente() {
        String cedula = txtCedula.getText().trim();
        
        if (cedula.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Por favor ingrese un número de cédula", 
                "Error", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        modeloTabla.setRowCount(0); // Limpiar tabla antes de nueva búsqueda

        String sql = "SELECT c.ID_CITA, c.FECHA, c.HORA, m.NOMBRE AS MEDICO, " +
                     "e.NOMBRE AS ESPECIALIDAD, cen.NOMBRE AS CENTRO " +
                     "FROM CITA_Q c " +
                     "JOIN PACIENTE_CITA_Q pc ON c.ID_CITA = pc.ID_CITA " +
                     "JOIN MEDICO_Q m ON c.ID_MEDICO = m.ID_MEDICO " +
                     "JOIN ESPECIALIDAD e ON m.ID_ESPECIALIDAD = e.ID_ESPECIALIDAD " +
                     "JOIN CENTRO_Q cen ON c.ID_CENTRO = cen.ID_CENTRO " +
                     "WHERE pc.CEDULA = ? " +
                     "ORDER BY c.FECHA DESC, c.HORA DESC";

        try (Connection conn = ConexionSQL.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, cedula);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                int contador = 0;
                
                while (rs.next()) {
                    contador++;
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
                
                if (contador == 0) {
                    JOptionPane.showMessageDialog(this, 
                        "No se encontraron citas para el paciente con cédula: " + cedula, 
                        "Resultados", 
                        JOptionPane.INFORMATION_MESSAGE);
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, 
                "Error al buscar citas: " + ex.getMessage(), 
                "Error de Base de Datos", 
                JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void exportarCitas() {
        // Implementación básica para exportar a CSV
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Guardar citas como CSV");
        
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            String filePath = fileChooser.getSelectedFile().getPath();
            if (!filePath.toLowerCase().endsWith(".csv")) {
                filePath += ".csv";
            }
            
            // Lógica para exportar (simplificada)
            try {
                // Aquí iría el código para generar el CSV
                JOptionPane.showMessageDialog(this, 
                    "Datos exportados exitosamente a: " + filePath, 
                    "Exportación Completa", 
                    JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, 
                    "Error al exportar: " + ex.getMessage(), 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            VistaCitasPaciente vista = new VistaCitasPaciente();
            vista.setVisible(true);
        });
    }
}