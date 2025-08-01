package org.vista;

import org.config.ConexionSQL;
import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DoctorDelete extends JFrame {

    private JComboBox<String> cbMedicos;
    private String sedeSelect;

    public DoctorDelete(String sedeSelect) {
        this.sedeSelect = sedeSelect;
        setTitle("Eliminar Doctor");
        setSize(900, 500);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Panel principal dividido en izquierda y derecha
        JPanel mainPanel = new JPanel(new GridLayout(1, 2));
        add(mainPanel, BorderLayout.CENTER);

        // ----------------- PANEL IZQUIERDO -----------------
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setBackground(new Color(230, 230, 230));
        leftPanel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));

        JLabel titulo = new JLabel("ELIMINACIÓN DE DOCTOR");
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        titulo.setFont(new Font("Arial", Font.BOLD, 20));
        titulo.setForeground(new Color(50, 50, 50));
        leftPanel.add(titulo);
        leftPanel.add(Box.createRigidArea(new Dimension(0, 40)));

        // Icono de hospital
        JLabel imagePlaceholder = new JLabel(escalarImagen("/pngs/hospital_icon.png", 180, 180));
        imagePlaceholder.setAlignmentX(Component.CENTER_ALIGNMENT);
        imagePlaceholder.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        leftPanel.add(imagePlaceholder);

        mainPanel.add(leftPanel);

        // ----------------- PANEL DERECHO (ELIMINACIÓN) -----------------
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new GridBagLayout());
        rightPanel.setBackground(new Color(245, 245, 245));
        rightPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Campo para seleccionar médico
        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel lblMedicos = new JLabel("Seleccione Médico:");
        lblMedicos.setFont(new Font("Arial", Font.BOLD, 14));
        rightPanel.add(lblMedicos, gbc);

        gbc.gridx = 1;
        cbMedicos = new JComboBox<>();
        cbMedicos.setPreferredSize(new Dimension(200, 30));
        cbMedicos.setFont(new Font("Arial", Font.PLAIN, 14));
        cargarMedicos();
        rightPanel.add(cbMedicos, gbc);

        // Botón de eliminación
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.CENTER;
        JButton btnEliminar = new JButton("Eliminar Doctor");
        btnEliminar.setPreferredSize(new Dimension(200, 35));
        btnEliminar.setBackground(new Color(220, 53, 69));
        btnEliminar.setForeground(Color.WHITE);
        btnEliminar.setFocusPainted(false);
        btnEliminar.setFont(new Font("Arial", Font.BOLD, 14));
        rightPanel.add(btnEliminar, gbc);

        btnEliminar.addActionListener(e -> eliminarMedico());

        // Botón de regresarqz  
        gbc.gridy = 2;
        JButton btnRegresar = new JButton("Regresar");
        btnRegresar.setPreferredSize(new Dimension(200, 35));
        btnRegresar.setBackground(new Color(100, 100, 100));
        btnRegresar.setForeground(Color.WHITE);
        btnRegresar.setFocusPainted(false);
        btnRegresar.setFont(new Font("Arial", Font.BOLD, 14));
        rightPanel.add(btnRegresar, gbc);

        btnRegresar.addActionListener(e -> {
            dispose(); // Cierra ventana actual
            new AdminWindow(this.sedeSelect).setVisible(true); // Abre AdminWindow
        });

        mainPanel.add(rightPanel);
    }

    private void cargarMedicos() {
    cbMedicos.removeAllItems(); // Limpiar el comboBox
    
    // Añadir un ítem por defecto si no hay médicos
    cbMedicos.addItem("Seleccione un médico...");
    
    String tabla = sedeSelect.equals("Quito") ? "MEDICO_Q" : "MEDICO_G";

    try (Connection conn = ConexionSQL.conectar()) {
        // Verificar si la conexión es exitosa
        if (conn == null || conn.isClosed()) {
            JOptionPane.showMessageDialog(this,
                "Error al conectar a la base de datos.",
                "Error",
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        String sql = "SELECT ID_MEDICO, NOMBRE FROM " + tabla + " ORDER BY NOMBRE";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            boolean hayMedicos = false;
            
            while (rs.next()) {
                cbMedicos.addItem(rs.getInt("ID_MEDICO") + " - " + rs.getString("NOMBRE"));
                hayMedicos = true;
            }
            
            // Si no hay médicos, mostrar un mensaje
            if (!hayMedicos) {
                cbMedicos.addItem("No hay médicos registrados");
                cbMedicos.setEnabled(false); // Opcional: deshabilitar el ComboBox
            }
        }
    } catch (SQLException ex) {
        JOptionPane.showMessageDialog(this,
            "Error al cargar médicos: " + ex.getMessage(),
            "Error",
            JOptionPane.ERROR_MESSAGE);
    }
}

    private void eliminarMedico() {
        String medicoSeleccionado = (String) cbMedicos.getSelectedItem();
        if (medicoSeleccionado == null || medicoSeleccionado.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor seleccione un médico.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int idMedico = Integer.parseInt(medicoSeleccionado.split(" - ")[0]);

        int confirmacion = JOptionPane.showConfirmDialog(
                this,
                "¿Está seguro que desea eliminar al médico: " + medicoSeleccionado + "?",
                "Confirmar Eliminación",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirmacion != JOptionPane.YES_OPTION) {
            return;
        }

        Connection conn = null;
        try {
            conn = ConexionSQL.conectar();
            conn.setAutoCommit(false);

            if (sedeSelect.equals("Quito")) {
                eliminarMedicoVerticalQ(conn, idMedico);

                String sqlMedico = "DELETE FROM MEDICO_Q WHERE ID_MEDICO = ?";
                try (PreparedStatement ps = conn.prepareStatement(sqlMedico)) {
                    ps.setInt(1, idMedico);
                    int filas = ps.executeUpdate();

                    if (filas > 0) {
                        JOptionPane.showMessageDialog(this,
                                "Médico eliminado exitosamente de Quito!",
                                "Eliminación exitosa",
                                JOptionPane.INFORMATION_MESSAGE);
                        cargarMedicos();
                    }
                }
            } else {
                eliminarMedicoVerticalG(conn, idMedico);

                String sqlMedico = "DELETE FROM MEDICO_G WHERE ID_MEDICO = ?";
                try (PreparedStatement ps = conn.prepareStatement(sqlMedico)) {
                    ps.setInt(1, idMedico);
                    int filas = ps.executeUpdate();

                    if (filas > 0) {
                        JOptionPane.showMessageDialog(this,
                                "Médico eliminado exitosamente de Guayaquil!",
                                "Eliminación exitosa",
                                JOptionPane.INFORMATION_MESSAGE);
                        cargarMedicos();
                    }
                }
            }

            conn.commit();
        } catch (SQLException ex) {
            try {
                if (conn != null) {
                    conn.rollback();
                }
            } catch (SQLException rollbackEx) {
                rollbackEx.printStackTrace();
            }

            JOptionPane.showMessageDialog(this,
                    "Error al eliminar el médico: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException closeEx) {
                closeEx.printStackTrace();
            }
        }
    }

    private void eliminarMedicoVerticalQ(Connection conn, int idMedico) throws SQLException {
        String[] tablas = {
                "MEDICO_IDENTIFICACION_Q",
                "MEDICO_PERFIL_PROFESIONAL_Q",
                "HISTORIALCONSULTA_MEDICO_Q"
        };

        for (String tabla : tablas) {
            String sql = "DELETE FROM " + tabla + " WHERE ID_MEDICO = ?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, idMedico);
                ps.executeUpdate();
            }
        }
    }

    private void eliminarMedicoVerticalG(Connection conn, int idMedico) throws SQLException {
        String[] tablas = {
                "MEDICO_IDENTIFICACION_G",
                "MEDICO_PERFIL_PROFESIONAL_G",
                "HISTORIALCONSULTA_MEDICO_G"
        };

        for (String tabla : tablas) {
            String sql = "DELETE FROM " + tabla + " WHERE ID_MEDICO = ?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, idMedico);
                ps.executeUpdate();
            }
        }
    }

    private ImageIcon escalarImagen(String ruta, int ancho, int alto) {
        try {
            java.net.URL imgURL = getClass().getResource(ruta);
            if (imgURL != null) {
                return new ImageIcon(new ImageIcon(imgURL).getImage().getScaledInstance(ancho, alto, Image.SCALE_SMOOTH));
            } else {
                System.err.println("No se encontró la imagen: " + ruta);
                return new ImageIcon();
            }
        } catch (Exception e) {
            System.err.println("Error al cargar la imagen: " + e.getMessage());
            return new ImageIcon();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                new DoctorDelete("Quito").setVisible(true); // Puedes cambiar a "Guayaquil"
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
