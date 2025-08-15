package org.vista;

import com.sun.tools.javac.Main;
import org.config.ConexionSQL;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
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
            conn = ConexionSQL.conectar();
            //  Verificar DTC antes de continuar
            if (!verificarDTC()) {
                JOptionPane.showMessageDialog(this,
                        "El servicio DTC no está disponible. ",
                        "DTC", JOptionPane.ERROR_MESSAGE);
                return;
            }
            conn.setAutoCommit(false); // Evitar commit local

            String ciudad = (String) comboCiudad.getSelectedItem();
            String tabla = ciudad.equalsIgnoreCase("Quito")
                    ? "[VID].[BQuito2].dbo.ESPECIALIDAD_Q"
                    : "[LAPTOP-J4CMJHBK].[BGuayaquil].dbo.ESPECIALIDAD_G";

            String sql =
                    "SET XACT_ABORT ON; " +
                            "BEGIN DISTRIBUTED TRANSACTION; " +
                            "DECLARE @NuevoId INT; " +
                            "SELECT @NuevoId = ISNULL(MAX(ID_ESPECIALIDAD), ?) + 1 FROM " + tabla + "; " +
                            "INSERT INTO " + tabla + " (ID_ESPECIALIDAD, NOMBRE, DESCRIPCION) VALUES (@NuevoId, ?, ?); " +
                            "COMMIT TRANSACTION;";

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, ciudad.equalsIgnoreCase("Quito") ? 100 : 200);
                ps.setString(2, nombre);
                ps.setString(3, descripcion);
                ps.executeUpdate();
            }

            conn.commit();
            JOptionPane.showMessageDialog(this, "Especialidad registrada exitosamente en " + ciudad,
                    "Éxito", JOptionPane.INFORMATION_MESSAGE);
            limpiarCampos();

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error DTC o SQL: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException e) {
                e.printStackTrace();
            }

        } finally {
            try {
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * Verifica si el servicio MS-DTC está activo
     */
    private boolean verificarDTC() {
        try {
            // Verificar mediante comando del sistema
            ProcessBuilder pb = new ProcessBuilder("sc", "query", "MSDTC");
            Process process = pb.start();

            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.contains("STATE") && line.contains("RUNNING")) {
                        return true;
                    }
                }
            }
            return false;

        } catch (Exception ex) {
            System.err.println("Error verificando DTC: " + ex.getMessage());
            return false;
        }
    }


    private int obtenerIdEspecialidadDisponible(Connection conn) throws SQLException {
        String ciudad = (String) comboCiudad.getSelectedItem();
        String tabla;

        // Determinar tabla según ciudad
        if ("Quito".equalsIgnoreCase(ciudad)) {
            tabla = "[VID].[BQuito2].dbo.ESPECIALIDAD_Q";
        } else {
            tabla = "[LAPTOP-J4CMJHBK].[BGuayaquil].dbo.ESPECIALIDAD_G";
        }

        String sql = "SELECT MAX(ID_ESPECIALIDAD) FROM " + tabla;
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            int maxId = 0;
            if (rs.next()) {
                maxId = rs.getInt(1);
            }
            return maxId > 0 ? maxId + 1 : (ciudad.equalsIgnoreCase("Quito") ? 100 : 200);
        }
    }


    private void limpiarCampos() {
        txtNombreEspecialidad.setText("");
        txtDescripcion.setText("");
    }
}
