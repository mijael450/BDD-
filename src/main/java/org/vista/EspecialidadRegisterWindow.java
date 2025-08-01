package org.vista;

import org.config.ConexionSQL;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class EspecialidadRegisterWindow extends JFrame {
    private JTextField txtNombreEspecialidad;
    private JTextField txtDescripcion;
    private JButton btnRegistrar;
    private JButton btnRegresar;
    private String sedeSelect;

    public EspecialidadRegisterWindow(String sedeSelect) {
        this.sedeSelect = sedeSelect;
        setTitle("Registrar Especialidad");
        setSize(400, 250);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(4, 2, 10, 10));
        panel.setBackground(new Color(157, 209, 241));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        panel.add(new JLabel("Nombre Especialidad:"));
        txtNombreEspecialidad = new JTextField();
        panel.add(txtNombreEspecialidad);

        panel.add(new JLabel("Descripción:"));
        txtDescripcion = new JTextField();
        panel.add(txtDescripcion);

        btnRegistrar = new JButton("Registrar");
        panel.add(btnRegistrar);

        btnRegresar = new JButton("Regresar");
        panel.add(btnRegresar);

        // Espacio vacío para alinear
        panel.add(new JLabel(""));

        add(panel);

        btnRegistrar.addActionListener(e -> registrarEspecialidad());
        btnRegresar.addActionListener(e -> {
            dispose();
            // Regresar a la ventana anterior (por ejemplo, AdminWindow)
            JFrame adminWindow = new AdminWindow(sedeSelect);
            adminWindow.setVisible(true);
        });
    }

    private void registrarEspecialidad() {
        String nombre = txtNombreEspecialidad.getText().trim();
        String descripcion = txtDescripcion.getText().trim();

        if (nombre.isEmpty() || descripcion.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor complete todos los campos.",
                    "Campos Vacíos", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Connection conn = null;
        try {
            conn = sedeSelect.equalsIgnoreCase("Quito")
                    ? ConexionSQL.conectar()
                    : ConexionSQL.conectarGYE(true);

            String tableSuffix = sedeSelect.equalsIgnoreCase("Quito") ? "Q" : "G";
            int nuevoId = obtenerIdEspecialidadDisponible(conn, tableSuffix);

            String sql = "INSERT INTO ESPECIALIDAD (ID_ESPECIALIDAD, NOMBRE, DESCRIPCION) VALUES (?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, nuevoId);
                ps.setString(2, nombre);
                ps.setString(3, descripcion);
                ps.executeUpdate();
            }

            JOptionPane.showMessageDialog(this, "Especialidad registrada exitosamente.",
                    "Éxito", JOptionPane.INFORMATION_MESSAGE);
            limpiarCampos();

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al registrar especialidad: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        } finally {
            try {
                if (conn != null) conn.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    private int obtenerIdEspecialidadDisponible(Connection conn, String tableSuffix) throws SQLException {
        String sql = "SELECT MAX(ID_ESPECIALIDAD) FROM ESPECIALIDAD";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt(1) + 1 : 1;
        }
    }

    private void limpiarCampos() {
        txtNombreEspecialidad.setText("");
        txtDescripcion.setText("");
    }
}
