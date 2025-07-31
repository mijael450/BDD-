package org.vista;


import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.config.ConexionSQL;

public class PacienteWindow extends JFrame {

    private String nombre;
    private String cedula;
    private String sedeSelect;
    public PacienteWindow(String nombre, String cedula, String sedeSelect) {
        this.nombre = nombre;
        this.cedula = cedula;
        this.sedeSelect = sedeSelect;
            
        setTitle("Panel del Paciente");
        setSize(700, 450);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // 🎨 Color de fondo principal
        Color fondo = new Color(157, 209, 241); // azul claro
        Color botonColor = new Color(255, 255, 255); // blanco para los botones
        Color textoBotonColor = Color.BLACK; // texto negro

        getContentPane().setBackground(fondo);

        // 👤 Panel superior con datos del usuario y título
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(fondo);

        JLabel lblBienvenida = new JLabel("¡Bienvenido/a, " + nombre + "!", SwingConstants.CENTER);
        lblBienvenida.setFont(new Font("Arial", Font.BOLD, 20));
        lblBienvenida.setBorder(BorderFactory.createEmptyBorder(20, 10, 5, 10));

        JLabel lblCedula = new JLabel("Cédula: " + cedula, SwingConstants.CENTER);
        lblCedula.setFont(new Font("Arial", Font.PLAIN, 14));
        lblCedula.setBorder(BorderFactory.createEmptyBorder(0, 10, 20, 10));

        JPanel infoUsuario = new JPanel(new GridLayout(2, 1));
        infoUsuario.setBackground(fondo);
        infoUsuario.add(lblBienvenida);
        infoUsuario.add(lblCedula);

        JLabel titulo = new JLabel("Sistema de Agendamiento Médico", SwingConstants.CENTER);
        titulo.setFont(new Font("Arial", Font.BOLD, 22));
        titulo.setBorder(BorderFactory.createEmptyBorder(10, 10, 20, 10));

        header.add(infoUsuario, BorderLayout.NORTH);
        header.add(titulo, BorderLayout.SOUTH);
        add(header, BorderLayout.NORTH);

        // 🎛️ Panel central con botones de acción
        JPanel panelBotones = new JPanel(new GridLayout(3, 1, 15, 15));
        panelBotones.setBorder(BorderFactory.createEmptyBorder(30, 150, 30, 150));
        panelBotones.setBackground(fondo);

        JButton btnAgendarCita = new JButton("📅 Agendar Cita");
        JButton btnVerCitas = new JButton("📖 Ver Citas Agendadas");
        JButton btnCerrarSesion = new JButton("🔒 Cerrar Sesión");

        JButton[] botones = { btnAgendarCita, btnVerCitas, btnCerrarSesion };
        for (JButton btn : botones) {
            btn.setFocusPainted(false);
            btn.setFont(new Font("Arial", Font.BOLD, 16));
            btn.setBackground(botonColor);
            btn.setForeground(textoBotonColor); // 🔧 letras negras
            btn.setPreferredSize(new Dimension(200, 40));
        }
        btnCerrarSesion.setBackground(new Color(255, 102, 102)); // rojo suave

        panelBotones.add(btnAgendarCita);
        panelBotones.add(btnVerCitas);
        panelBotones.add(btnCerrarSesion);

        add(panelBotones, BorderLayout.CENTER);

        // ✅ Acciones de botones
        btnAgendarCita.addActionListener(e -> {
            new AgendamientoWindow(nombre,cedula,this.sedeSelect).setVisible(true);
            dispose();
        });

        btnVerCitas.addActionListener(e -> {
            new VistaCitasPaciente(nombre, cedula,this.sedeSelect).setVisible(true);
            dispose();
        });

        btnCerrarSesion.addActionListener(e -> {
            new LoginWindow(this.sedeSelect).setVisible(true);
            dispose();
        });
    }
        
        
    }