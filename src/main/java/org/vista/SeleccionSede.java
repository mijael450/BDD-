/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.vista;

import org.config.Config;
import javax.swing.*;
import java.awt.*;

public class SeleccionSede extends JFrame {

    private JPanel rightPanel;
    private JComboBox<String> comboSede;

    public SeleccionSede() {
        setTitle("Sistema Hospitalario Médico");
        setSize(900, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // -------- PANEL PRINCIPAL: división en izquierda y derecha --------
        JPanel mainPanel = new JPanel(new GridLayout(1, 2));
        add(mainPanel, BorderLayout.CENTER);

        // ----------------- PANEL IZQUIERDO (igual al de HomeWindow) -----------------
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setBackground(new Color(230, 230, 230));
        leftPanel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));

        JLabel titulo = new JLabel("SISTEMA HOSPITALARIO MÉDICO");
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        titulo.setFont(new Font("Arial", Font.BOLD, 20));
        titulo.setForeground(new Color(50, 50, 50));
        leftPanel.add(titulo);
        leftPanel.add(Box.createRigidArea(new Dimension(0, 40)));

          // Espacio para imagen PNG
        JLabel imagePlaceholder = new JLabel(escalarImagen("/pngs/hospital_icon.png", 180, 180));
        imagePlaceholder.setAlignmentX(Component.CENTER_ALIGNMENT); // Centrado horizontal
        imagePlaceholder.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0)); // Márgenes opcionales
        leftPanel.add(imagePlaceholder);


        mainPanel.add(leftPanel);

        // ----------------- PANEL DERECHO CAMBIANTE -----------------
        rightPanel = new JPanel();
        rightPanel.setLayout(null);
        rightPanel.setBackground(new Color(245, 245, 245));
        mainPanel.add(rightPanel);

        mostrarSeleccionSede();
    }

    private void mostrarSeleccionSede() {
        rightPanel.removeAll();
        rightPanel.repaint();
        rightPanel.revalidate();

        JLabel lblSede = new JLabel("Seleccione la sede:");
        lblSede.setBounds(100, 100, 200, 30);
        lblSede.setFont(new Font("Arial", Font.BOLD, 16));
        rightPanel.add(lblSede);

        comboSede = new JComboBox<>(new String[]{"Quito", "Guayaquil"});
        comboSede.setBounds(100, 140, 250, 30);
        comboSede.setFont(new Font("Arial", Font.PLAIN, 14));
        rightPanel.add(comboSede);

        JButton btnContinuar = new JButton("Continuar");
        btnContinuar.setBounds(100, 190, 250, 35);
        btnContinuar.setFont(new Font("Arial", Font.BOLD, 14));
        rightPanel.add(btnContinuar);

        btnContinuar.addActionListener(e -> {
            Config.sedeSeleccionada = comboSede.getSelectedItem().toString();
            mostrarLogin();
        });
    }

    private void mostrarLogin() {
        rightPanel.removeAll();
        rightPanel.repaint();
        rightPanel.revalidate();

        JLabel lblCedula = new JLabel("Cédula:");
        lblCedula.setBounds(50, 80, 100, 30);
        rightPanel.add(lblCedula);

        JTextField txtCedula = new JTextField();
        txtCedula.setBounds(150, 80, 200, 30);
        rightPanel.add(txtCedula);

        JLabel lblPassword = new JLabel("Contraseña:");
        lblPassword.setBounds(50, 130, 100, 30);
        rightPanel.add(lblPassword);

        JPasswordField txtPassword = new JPasswordField();
        txtPassword.setBounds(150, 130, 200, 30);
        rightPanel.add(txtPassword);

        JButton btnLogin = new JButton("Iniciar Sesión");
        btnLogin.setBounds(150, 190, 200, 35);
        btnLogin.setBackground(new Color(70, 132, 184));
        btnLogin.setForeground(Color.BLACK);
        btnLogin.setFocusPainted(false);
        btnLogin.setFont(new Font("Arial", Font.BOLD, 14));
        rightPanel.add(btnLogin);

        JLabel lblCambiarSede = new JLabel("Cambiar sede");
        lblCambiarSede.setBounds(rightPanel.getWidth() - 120, rightPanel.getHeight() - 40, 100, 20);
        lblCambiarSede.setForeground(Color.BLUE);
        lblCambiarSede.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        lblCambiarSede.setFont(new Font("Arial", Font.PLAIN, 12));
        rightPanel.add(lblCambiarSede);
        
        rightPanel.addComponentListener(new java.awt.event.ComponentAdapter() {
        public void componentResized(java.awt.event.ComponentEvent evt) {
            lblCambiarSede.setBounds(rightPanel.getWidth() - 120, rightPanel.getHeight() - 40, 100, 20);
        }
    });


        lblCambiarSede.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                mostrarSeleccionSede();
            }
        });

        JLabel lblRegistrar = new JLabel("¿No tienes cuenta? Regístrate");
        lblRegistrar.setBounds(165, 250, 300, 30);
        lblRegistrar.setForeground(new Color(0, 102, 204));
        lblRegistrar.setFont(new Font("Arial", Font.BOLD, 12));
        lblRegistrar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        rightPanel.add(lblRegistrar);

        // Aquí puedes copiar el mismo listener de login que tenías en HomeWindow,
        // si quieres que lo maneje todo desde esta clase, o simplemente abrir HomeWindow si prefieres.

        btnLogin.addActionListener(e -> {
            new HomeWindow().setVisible(true);
            dispose();
        });

        lblRegistrar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                new RegisterWindow().setVisible(true);
                dispose();
            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            new SeleccionSede().setVisible(true);
        });
    }
    
    private ImageIcon escalarImagen(String ruta, int ancho, int alto) {
    ImageIcon iconoOriginal = new ImageIcon(getClass().getResource(ruta));
    Image imagenOriginal = iconoOriginal.getImage();
    Image imagenEscalada = imagenOriginal.getScaledInstance(ancho, alto, Image.SCALE_SMOOTH);
    return new ImageIcon(imagenEscalada);
}

}

