package org.vista;


import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.config.ConexionSQL;

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

        JButton btnAgendarCita = new JButton("Agendar Cita");
        JButton btnVerCitas = new JButton("Ver citas agendadas");
        JButton btnCerrarSesion = new JButton("Cerrar Sesión");
        btnCerrarSesion.setBackground(Color.red);

        panelBotones.add(btnAgendarCita);
        panelBotones.add(btnVerCitas);
        panelBotones.add(btnCerrarSesion);

        add(panelBotones, BorderLayout.CENTER);
        
        //Accion de Agendar cita
        btnAgendarCita.addActionListener(e-> {
            
          new AgendamientoWindow().setVisible(true);
            dispose();
           
        }       
        );
        
        //Accion de Ver citas agendadas
        btnVerCitas.addActionListener(e-> {
            
          new VistaCitasPaciente().setVisible(true);
            dispose();
           
        }       
        );
        
        // Acción de cerrar sesión
        btnCerrarSesion.addActionListener(e -> {
            new LoginWindow().setVisible(true);
            dispose();
        });
        
        
    }}