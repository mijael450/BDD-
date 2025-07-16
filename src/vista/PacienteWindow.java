package vista;


import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
public class PacienteWindow extends JFrame {

    public PacienteWindow() {
        setTitle("Panel del Paciente");
        setSize(600, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Encabezado
        JLabel titulo = new JLabel("Panel de Administración del Sistema Médico", SwingConstants.CENTER);
        titulo.setFont(new Font("Arial", Font.BOLD, 18));
        titulo.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        add(titulo, BorderLayout.NORTH);

        // Panel central con botones
        JPanel panelBotones = new JPanel();
        panelBotones.setLayout(new GridLayout(4, 1, 10, 10));
        panelBotones.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));

        JButton btnRegistrarMedico = new JButton("Registrar Médico");
        JButton btnVerMedicos = new JButton("Ver Lista de Médicos");
        JButton btnVerPacientes = new JButton("Ver Lista de Pacientes");
        JButton btnCerrarSesion = new JButton("Cerrar Sesión");

        panelBotones.add(btnRegistrarMedico);
        panelBotones.add(btnVerMedicos);
        panelBotones.add(btnVerPacientes);
        panelBotones.add(btnCerrarSesion);

        add(panelBotones, BorderLayout.CENTER);

        // Acción de cerrar sesión
        btnCerrarSesion.addActionListener(e -> {
            new HomeWindow().setVisible(true);
            dispose();
        });
    }}