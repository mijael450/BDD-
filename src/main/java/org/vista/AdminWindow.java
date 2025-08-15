package org.vista;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class AdminWindow extends JFrame {
    private String sedeSelect;

    public AdminWindow(String sedeSelect) {
        this.sedeSelect = sedeSelect;
        initializeWindow();
        createComponents();
    }

    AdminWindow() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    private void initializeWindow() {
        setTitle("Panel del Administrador");
        setSize(900, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
    }

    private void createComponents() {
        JPanel mainPanel = new JPanel(new GridLayout(1, 2));
        add(mainPanel, BorderLayout.CENTER);

        mainPanel.add(createLeftPanel());
        mainPanel.add(createRightPanel());
    }

    private JPanel createLeftPanel() {
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setBackground(new Color(0, 53, 84));
        leftPanel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));

        JLabel titulo = new JLabel("ADMINISTRADOR");
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        titulo.setFont(new Font("Arial", Font.BOLD, 24));
        titulo.setForeground(Color.WHITE);
        leftPanel.add(titulo);
        leftPanel.add(Box.createRigidArea(new Dimension(0, 40)));

        try {
            JLabel image = new JLabel(escalarImagen("/pngs/hospital_icon.png", 180, 180));
            image.setAlignmentX(Component.CENTER_ALIGNMENT);
            leftPanel.add(image);
        } catch (Exception e) {
            JLabel placeholder = new JLabel("🏥");
            placeholder.setAlignmentX(Component.CENTER_ALIGNMENT);
            placeholder.setFont(new Font("Arial", Font.PLAIN, 80));
            leftPanel.add(placeholder);
        }

        return leftPanel;
    }

    private JPanel createRightPanel() {
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(null);
        rightPanel.setBackground(new Color(157, 209, 241));

        Font fontBoton = new Font("Arial", Font.BOLD, 14);
        Color colorBoton = new Color(255, 255, 255);  // Blanco
        Color colorTexto = Color.BLACK;               // Texto negro

        JButton btnRegistrarMedico = new JButton("Registrar Médico");
        JButton btnVerMedicos = new JButton("Ver Lista de Médicos");
        JButton btnVerPacientes = new JButton("Ver Lista de Pacientes");
        JButton btnVerEspecialidades = new JButton("Ver Especialidades");
        JButton btnAgregarEspecialidad = new JButton("Agregar Especialidad");
        JButton btnVerCitas = new JButton("Ver Citas"); // Nuevo botón
        JButton btnCerrarSesion = new JButton("Cerrar Sesión");

        // Ajustamos el arreglo de botones para incluir el nuevo botón
        JButton[] botones = {btnRegistrarMedico, btnVerMedicos, btnVerPacientes, 
                            btnVerEspecialidades, btnAgregarEspecialidad, btnVerCitas, btnCerrarSesion};
        
        // Ajustamos el espaciado para que entren todos los botones
        int y = 50;
        for (JButton boton : botones) {
            boton.setBounds(100, y, 250, 35); // Reducimos la altura para que quepan más botones
            boton.setFont(fontBoton);
            boton.setBackground(colorBoton);
            boton.setForeground(colorTexto);
            boton.setFocusPainted(false);
            boton.setCursor(new Cursor(Cursor.HAND_CURSOR));
            rightPanel.add(boton);
            y += 45; // Reducimos el espacio entre botones
        }

        // Eventos
        btnCerrarSesion.addActionListener(e -> {
            new LoginWindow(this.sedeSelect).setVisible(true);
            dispose();
        });

        btnRegistrarMedico.addActionListener(e -> {
            new DoctorRegister(this.sedeSelect).setVisible(true);
            dispose();
        });

        btnVerMedicos.addActionListener(e -> {
            new ListaMedicosWindow(this.sedeSelect).setVisible(true);
            dispose();
        });

        btnVerPacientes.addActionListener(e -> {
            new ListaPacientesWindow(this.sedeSelect).setVisible(true);
            dispose();
        });

        btnVerEspecialidades.addActionListener(e -> {
            new ListaEspecialidadesWindow(this.sedeSelect).setVisible(true);
            dispose();
        });

        btnAgregarEspecialidad.addActionListener(e -> {
            new EspecialidadRegisterWindow(this.sedeSelect).setVisible(true);
            dispose();
        });

        // Evento para el nuevo botón Ver Citas
        btnVerCitas.addActionListener(e -> {
            new ListaCitasWindow(this.sedeSelect).setVisible(true);
            dispose();
        });

        return rightPanel;
    }

    private ImageIcon escalarImagen(String ruta, int ancho, int alto) {
        try {
            ImageIcon iconoOriginal = new ImageIcon(getClass().getResource(ruta));
            if (iconoOriginal.getIconWidth() == -1) {
                throw new IllegalArgumentException("Imagen no encontrada: " + ruta);
            }
            Image imagenOriginal = iconoOriginal.getImage();
            Image imagenEscalada = imagenOriginal.getScaledInstance(ancho, alto, Image.SCALE_SMOOTH);
            return new ImageIcon(imagenEscalada);
        } catch (Exception e) {
            System.err.println("Error al cargar imagen: " + ruta);
            return null;
        }
    }
}