package org.vista;

import org.config.ConexionSQL;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ListaPacientesWindow extends JFrame {

    private JTable tablaPacientes;
    private DefaultTableModel modeloTabla;

    public ListaPacientesWindow(String sedeSelect) {

        setTitle("Lista de Pacientes");
        setSize(850, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // ---------- PANEL SUPERIOR ----------
        JPanel panelSuperior = new JPanel(new BorderLayout());
        panelSuperior.setBackground(new Color(157, 209, 241));
        panelSuperior.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel lblTitulo = new JLabel("Lista de Pacientes");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 22));
        lblTitulo.setForeground(new Color(0, 53, 84));
        panelSuperior.add(lblTitulo, BorderLayout.WEST);

        add(panelSuperior, BorderLayout.NORTH);

        // ---------- TABLA ----------
        String[] columnas = {"Cédula", "Nombre", "Fecha Nacimiento", "Sexo", "Teléfono", "Email", "Ciudad", "Dirección"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tablaPacientes = new JTable(modeloTabla);
        tablaPacientes.setRowHeight(25);
        tablaPacientes.setFont(new Font("Arial", Font.PLAIN, 13));
        tablaPacientes.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));

        JScrollPane scrollPane = new JScrollPane(tablaPacientes);
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
        btnRegresar.addActionListener(e -> { new AdminWindow(sedeSelect).setVisible(true); // Regresa al panel del administrador 
        dispose();
        });

        panelSur.add(btnRegresar);
        add(panelSur, BorderLayout.SOUTH);

        // ---------- CONSULTA AUTOMÁTICA ----------
        buscarPacientesDesdeDB();

        setVisible(true);
    }

    private void buscarPacientesDesdeDB() {
        modeloTabla.setRowCount(0);

        String sql = "SELECT TOP (1000) CEDULA, NOMBRE, FECHA_NAC, SEXO, TELEFONO, EMAIL, CIUDAD, DIRECCION " +
                     "FROM [BQuito2].[dbo].[PACIENTE]";

        try (Connection conn = ConexionSQL.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Object[] fila = {
                        rs.getString("CEDULA"),
                        rs.getString("NOMBRE"),
                        rs.getDate("FECHA_NAC"),
                        rs.getString("SEXO"),
                        rs.getString("TELEFONO"),
                        rs.getString("EMAIL"),
                        rs.getString("CIUDAD"),
                        rs.getString("DIRECCION")
                };
                modeloTabla.addRow(fila);
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Error al buscar pacientes: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
}
