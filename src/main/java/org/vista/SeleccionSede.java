package org.vista;

import org.config.Config;
import javax.swing.*;
import java.awt.*;

public class SeleccionSede extends JFrame {

    private final JComboBox<String> comboSede;

    public SeleccionSede() {
        setTitle("Sistema Hospitalario Médico - Selección de Sede");
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
        leftPanel.setBackground(new Color(0, 53, 84));
        leftPanel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));

        JLabel titulo = new JLabel("SISTEMA HOSPITALARIO MÉDICO");
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        titulo.setFont(new Font("Arial", Font.BOLD, 20));
        titulo.setForeground(new Color(255, 255, 255));
        leftPanel.add(titulo);
        leftPanel.add(Box.createRigidArea(new Dimension(0, 40)));

        // Espacio para imagen PNG con manejo de errores
        try {
            JLabel imagePlaceholder = new JLabel(escalarImagen("/pngs/hospital_icon.png", 180, 180));
            imagePlaceholder.setAlignmentX(Component.CENTER_ALIGNMENT);
            imagePlaceholder.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
            leftPanel.add(imagePlaceholder);
        } catch (Exception e) {
            System.err.println("No se pudo cargar la imagen: " + e.getMessage());
            // Placeholder alternativo si la imagen no se encuentra
            JLabel placeholder = new JLabel("🏥");
            placeholder.setAlignmentX(Component.CENTER_ALIGNMENT);
            placeholder.setFont(new Font("Arial", Font.PLAIN, 80));
            leftPanel.add(placeholder);
        }

        mainPanel.add(leftPanel);

        // ----------------- PANEL DERECHO - SELECCIÓN DE SEDE -----------------
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(null);
        rightPanel.setBackground(new Color(157, 209, 241));
        mainPanel.add(rightPanel);

        // Título de selección de sede
        JLabel lblTitulo = new JLabel("Seleccionar Sede");
        lblTitulo.setBounds(100, 50, 250, 40);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 24));
        lblTitulo.setForeground(new Color(0, 53, 84));
        rightPanel.add(lblTitulo);

        // Label para selección de sede
        JLabel lblSede = new JLabel("Seleccione la sede:");
        lblSede.setBounds(100, 120, 200, 30);
        lblSede.setFont(new Font("Arial", Font.BOLD, 16));
        lblSede.setForeground(new Color(0, 53, 84));
        rightPanel.add(lblSede);

        // ComboBox para seleccionar sede
        comboSede = new JComboBox<>(new String[]{"Quito", "Guayaquil"});
        comboSede.setBounds(100, 160, 250, 35);
        comboSede.setFont(new Font("Arial", Font.PLAIN, 14));
        comboSede.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        rightPanel.add(comboSede);

        // Botón continuar
        JButton btnContinuar = new JButton("Continuar");
        btnContinuar.setBounds(100, 220, 250, 40);
        btnContinuar.setFont(new Font("Arial", Font.BOLD, 14));
        btnContinuar.setBackground(new Color(0, 53, 84));
        btnContinuar.setForeground(Color.black);
        btnContinuar.setFocusPainted(false);
        btnContinuar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        rightPanel.add(btnContinuar);
        btnContinuar.setOpaque(true);
        btnContinuar.setContentAreaFilled(true);

        // Evento del botón continuar
        btnContinuar.addActionListener(e -> {
            // Guardar la sede seleccionada en la configuración
            Config.sedeSeleccionada = comboSede.getSelectedItem().toString();

            // Confirmar la sede seleccionada con el usuario
            int confSedeSelecc = JOptionPane.showConfirmDialog(
                    this,
                    "¿Está seguro de que desea continuar con la sede " + Config.sedeSeleccionada + "?",
                    "Confirmar Sede",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE);

            // Si el usuario no confirma, no hacemos nada
            if (confSedeSelecc != JOptionPane.YES_OPTION) {
                return; // Si el usuario no confirma, no hacemos nada
            }

            // Si el usuario confirma, procedemos a abrir la ventana de login
            // Abrir la ventana de login (HomeWindow)
            SwingUtilities.invokeLater(() -> {
                try {
                    new HomeWindow().setVisible(true);
                    dispose();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this,
                            "Error al abrir la ventana de login: " + ex.getMessage(),
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                }
            });
        });

        // Información adicional sobre la sede
        JLabel lblInfo = new JLabel("<html><center>Seleccione la sede donde<br>desea acceder al sistema</center></html>");
        lblInfo.setBounds(100, 280, 250, 50);
        lblInfo.setFont(new Font("Arial", Font.PLAIN, 12));
        lblInfo.setForeground(new Color(0, 53, 84));
        lblInfo.setHorizontalAlignment(SwingConstants.CENTER);
        rightPanel.add(lblInfo);
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
            System.err.println("Error cargando imagen " + ruta + ": " + e.getMessage());
            throw e;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                System.err.println("No se pudo establecer Look and Feel: " + e.getMessage());
            }
            new SeleccionSede().setVisible(true);
        });
    }
}