package org.vista;

import org.config.ConexionSQL;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class EspecialidadRegisterWindow extends JFrame {
    private JTextField txtNombreEspecialidad;
    private JTextField txtDescripcion;
    private JButton btnRegistrar;
    private JButton btnRegresar;
    private JComboBox<String> comboCiudad; // Selección de ciudad
    private String sedeSelect;

    public EspecialidadRegisterWindow(String sedeSelect) {
        this.sedeSelect = sedeSelect;
        setTitle("Registrar Especialidad");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(5, 2, 10, 10));
        panel.setBackground(new Color(157, 209, 241));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // ComboBox ciudad
        panel.add(new JLabel("Ciudad:"));
        comboCiudad = new JComboBox<>(new String[]{"Quito", "Guayaquil"});
        comboCiudad.setSelectedItem(sedeSelect);
        panel.add(comboCiudad);

        // Campo nombre especialidad
        panel.add(new JLabel("Nombre Especialidad:"));
        txtNombreEspecialidad = new JTextField(15);
        panel.add(txtNombreEspecialidad);

        // Campo descripción
        panel.add(new JLabel("Descripción:"));
        txtDescripcion = new JTextField(15);
        panel.add(txtDescripcion);

        // Botones
        btnRegistrar = new JButton("Registrar");
        panel.add(btnRegistrar);

        btnRegresar = new JButton("Regresar");
        panel.add(btnRegresar);

        // Espacio vacío para alineación
        panel.add(new JLabel(""));

        add(panel);

        // Acción Registrar
        btnRegistrar.addActionListener(e -> {
            this.sedeSelect = (String) comboCiudad.getSelectedItem();
            registrarEspecialidad();
        });

        // Acción Regresar
        btnRegresar.addActionListener(e -> {
            dispose();
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
        // Conexión a la base central (vista)
        conn = ConexionSQL.conectar(); // Debes crear este método para conectar a la BD que contiene la vista
        conn.setAutoCommit(false); // Control manual de commit

        int nuevoId = obtenerIdEspecialidadDisponible(conn);

        // Armamos la transacción distribuida
        String sql = "SET XACT_ABORT ON; " +
                     "BEGIN DISTRIBUTED TRANSACTION; " +
                     "INSERT INTO Especialidad (ID_ESPECIALIDAD, NOMBRE, DESCRIPCION) " +
                     "VALUES (?, ?, ?); " +
                     "COMMIT TRANSACTION;";

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
        try {
            if (conn != null) conn.rollback();
        } catch (SQLException rollbackEx) {
            rollbackEx.printStackTrace();
        }
    } finally {
        try {
            if (conn != null) conn.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}


    private int obtenerIdEspecialidadDisponible(Connection conn) throws SQLException {
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
