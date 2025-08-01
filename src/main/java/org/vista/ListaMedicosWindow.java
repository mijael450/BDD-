package org.vista;

import org.config.ConexionSQL;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ListaMedicosWindow extends JFrame {

    private JTable tablaMedicos;
    private DefaultTableModel modeloTabla;
    private String sedeSelect;

    public ListaMedicosWindow( String sedeSelect) {

        this.sedeSelect = sedeSelect;

        setTitle("Lista de Médicos" );
        setSize(850, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // ---------- PANEL SUPERIOR ----------
        JPanel panelSuperior = new JPanel(new BorderLayout());
        panelSuperior.setBackground(new Color(157, 209, 241));
        panelSuperior.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel lblTitulo = new JLabel("Lista de Médicos");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 22));
        lblTitulo.setForeground(new Color(0, 53, 84));
        panelSuperior.add(lblTitulo, BorderLayout.WEST);

        add(panelSuperior, BorderLayout.NORTH);

        // ---------- TABLA ----------
        String[] columnas = {"ID Médico", "Nombre", "Teléfono", "ID Especialidad", "ID Centro", "Ciudad"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tablaMedicos = new JTable(modeloTabla);
        tablaMedicos.setRowHeight(25);
        tablaMedicos.setFont(new Font("Arial", Font.PLAIN, 13));
        tablaMedicos.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));

        JScrollPane scrollPane = new JScrollPane(tablaMedicos);
        add(scrollPane, BorderLayout.CENTER);

        // ---------- PANEL INFERIOR ----------
        JPanel panelSur = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        panelSur.setBackground(Color.WHITE);
        panelSur.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JButton btnRegresar = new JButton("Regresar");
        btnRegresar.setBackground(new Color(200, 200, 200));
        btnRegresar.setForeground(Color.BLACK);
        btnRegresar.setFocusPainted(false);
        btnRegresar.setFont(new Font("Arial", Font.PLAIN, 13));
        btnRegresar.addActionListener(e -> {
            new AdminWindow(sedeSelect).setVisible(true);  // Regresa al panel del administrador
            dispose();
        });

        panelSur.add(btnRegresar);
        add(panelSur, BorderLayout.SOUTH);

        // ---------- CONSULTA AUTOMÁTICA ----------
        buscarMedicosDesdeDB();

        setVisible(true);
    }


    private void buscarMedicosDesdeDB() {
        String tableSuffix = this.sedeSelect.equalsIgnoreCase("QUITO") ? "Q" : "G";
        modeloTabla.setRowCount(0);

        // Cambia el nombre de la base si es necesario según tu conexión
        String nombreVista = "Medico";  // Vista en la base de datos
        String sql = "SELECT ID_MEDICO, NOMBRE, TELEFONO, ID_ESPECIALIDAD, ID_CENTRO, CIUDAD FROM " + nombreVista;

        try (Connection conn = ConexionSQL.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Object[] fila = {
                        rs.getInt("ID_MEDICO"),
                        rs.getString("NOMBRE"),
                        rs.getString("TELEFONO"),
                        rs.getInt("ID_ESPECIALIDAD"),
                        rs.getInt("ID_CENTRO"),
                        rs.getString("CIUDAD")
                };
                modeloTabla.addRow(fila);
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Error al buscar médicos: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }


    private void exportarMedicos() {
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
}
