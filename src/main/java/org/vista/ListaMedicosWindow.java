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

    public ListaMedicosWindow(String sedeSelect) {

        setTitle("Lista de Médicos");
        setSize(700, 500);
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
        String[] columnas = {"ID Médico", "Nombre", "Teléfono"};
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
            new AdminWindow(sedeSelect).setVisible(true);
            dispose();
        });

        panelSur.add(btnRegresar);
        add(panelSur, BorderLayout.SOUTH);

        // ---------- CONSULTA AUTOMÁTICA ----------
        buscarMedicosDesdeDB();

        setVisible(true);
    }

    private void buscarMedicosDesdeDB() {
        modeloTabla.setRowCount(0);

        String sql = "SELECT TOP (1000) ID_MEDICO, NOMBRE, TELEFONO " +
                     "FROM [BQuito2].[dbo].[MEDICO_IDENTIFICACION]";

        try (Connection conn = ConexionSQL.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Object[] fila = {
                        rs.getInt("ID_MEDICO"),
                        rs.getString("NOMBRE"),
                        rs.getString("TELEFONO")
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
}
