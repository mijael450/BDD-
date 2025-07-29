package org.vista;


import javax.swing.*;
import java.awt.*;

public class DoctorWindow extends JFrame {

    public DoctorWindow() {
        setTitle("Panel del Doctor");
        setSize(600, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Título
        JLabel titulo = new JLabel("Panel Principal del Médico", SwingConstants.CENTER);
        titulo.setFont(new Font("Arial", Font.BOLD, 20));
        titulo.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        add(titulo, BorderLayout.NORTH);

        // Panel con botones
        JPanel panelBotones = new JPanel(new GridLayout(4, 1, 10, 10));
        panelBotones.setBorder(BorderFactory.createEmptyBorder(20, 60, 20, 60));

        JButton btnVerCitas = new JButton("Ver Citas Asignadas");
        JButton btnIngresarDiagnostico = new JButton("Ingresar Diagnóstico");
        JButton btnHistorialPacientes = new JButton("Ver Historial de Pacientes");
        JButton btnCerrarSesion = new JButton("Cerrar Sesión");

        panelBotones.add(btnVerCitas);
        panelBotones.add(btnIngresarDiagnostico);
        panelBotones.add(btnHistorialPacientes);
        panelBotones.add(btnCerrarSesion);

        add(panelBotones, BorderLayout.CENTER);

        // Acción cerrar sesión
        btnCerrarSesion.addActionListener(e -> {
            new LoginWindow().setVisible(true);
            dispose();
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new DoctorWindow().setVisible(true));
    }
}
