
package org.vista;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Date;
import com.toedter.calendar.JDateChooser;

import org.modelo.UserDatabase;
import org.modelo.Usuario;

public class RegisterWindow extends JFrame {
    private JTextField cedulaField, nombresField, apellidosField, correoField;
    private JPasswordField contrasenaField;
    private JComboBox<String> sexoComboBox;
    private JDateChooser fechaNacimientoChooser;

    public RegisterWindow() {
        setTitle("Registro de Paciente");
        setSize(600, 500);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);

        JLabel lblTitulo = new JLabel("Registro de Paciente");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 18));
        lblTitulo.setBounds(200, 10, 300, 30);
        add(lblTitulo);

        addFormField("Cédula:", cedulaField = new JTextField(), 50);
        addFormField("Nombres:", nombresField = new JTextField(), 90);
        addFormField("Apellidos:", apellidosField = new JTextField(), 130);

        JLabel fechaLabel = new JLabel("Fecha Nacimiento:");
        fechaLabel.setBounds(30, 170, 200, 25);
        add(fechaLabel);
        fechaNacimientoChooser = new JDateChooser();
        fechaNacimientoChooser.setBounds(250, 170, 200, 25);
        fechaNacimientoChooser.setDateFormatString("dd/MM/yyyy");
        add(fechaNacimientoChooser);

        sexoComboBox = new JComboBox<>(new String[]{"Masculino", "Femenino", "Otro"});
        addFormField("Sexo:", sexoComboBox, 210);

        addFormField("Correo:", correoField = new JTextField(), 250);
        addFormField("Contraseña:", contrasenaField = new JPasswordField(), 290);

// Botón Registrar con efecto hover azul (ESTILO EXACTO)
JButton registrarBtn = new JButton("Registrar");
registrarBtn.setBounds(150, 350, 120, 30); // Posición Y ajustada a 350 para el formulario de paciente
registrarBtn.setForeground(Color.BLACK);
registrarBtn.setFont(new Font("Arial", Font.BOLD, 12));
registrarBtn.setBackground(new Color(70, 130, 180));
registrarBtn.setOpaque(true);
registrarBtn.setBorderPainted(false);
registrarBtn.addMouseListener(new MouseAdapter() {
    public void mouseEntered(MouseEvent evt) {
        registrarBtn.setBackground(new Color(100, 150, 200));
    }
    public void mouseExited(MouseEvent evt) {
        registrarBtn.setBackground(new Color(70, 130, 180));
    }
});
registrarBtn.addActionListener(e -> registrarPaciente()); // Cambiado a registrarPaciente()
add(registrarBtn);

// Botón Volver con efecto hover rojo (ESTILO EXACTO)
JButton volverBtn = new JButton("← Volver");
volverBtn.setBounds(300, 350, 120, 30); // Posición Y ajustada a 350
volverBtn.setForeground(Color.BLACK);
volverBtn.setFont(new Font("Arial", Font.BOLD, 12));
volverBtn.setBackground(new Color(220, 60, 60));
volverBtn.setOpaque(true);
volverBtn.setBorderPainted(false);
volverBtn.addMouseListener(new MouseAdapter() {
    public void mouseEntered(MouseEvent evt) {
        volverBtn.setBackground(new Color(240, 80, 80));
    }
    public void mouseExited(MouseEvent evt) {
        volverBtn.setBackground(new Color(220, 60, 60));
    }
});
volverBtn.addActionListener(e -> {
    new LoginWindow().setVisible(true);
    dispose();
});
add(volverBtn);

// Botón Autocompletar para pruebas (ESTILO EXACTO)
JButton autocompletarBtn = new JButton("Autocompletar");
autocompletarBtn.setBounds(430, 350, 120, 30); // Posición Y ajustada a 350
autocompletarBtn.setForeground(Color.BLACK);
autocompletarBtn.setFont(new Font("Arial", Font.BOLD, 12));
autocompletarBtn.setBackground(new Color(100, 180, 100));
autocompletarBtn.setOpaque(true);
autocompletarBtn.setBorderPainted(false);
autocompletarBtn.addMouseListener(new MouseAdapter() {
    public void mouseEntered(MouseEvent evt) {
        autocompletarBtn.setBackground(new Color(130, 200, 130));
    }
    public void mouseExited(MouseEvent evt) {
        autocompletarBtn.setBackground(new Color(100, 180, 100));
    }
});
autocompletarBtn.addActionListener(e -> {
    // Autocompletado modificado para paciente (sin roleComboBox y usuarioField)
    cedulaField.setText("1234567890");
    nombresField.setText("Juan");
    apellidosField.setText("Pérez");
    fechaNacimientoChooser.setDate(new Date());
    sexoComboBox.setSelectedItem("Masculino");
    correoField.setText("juan.perez@example.com");
    contrasenaField.setText("clave123");
});
add(autocompletarBtn);

aplicarRestricciones();
    }

    private void addFormField(String labelText, JComponent field, int y) {
        JLabel label = new JLabel(labelText);
        label.setBounds(30, y, 200, 25);
        add(label);
        field.setBounds(250, y, 200, 25);
        add(field);
    }

    private void aplicarRestricciones() {
        cedulaField.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (!Character.isDigit(c) || cedulaField.getText().length() >= 10) {
                    e.consume();
                }
            }
        });

        KeyAdapter soloLetras = new KeyAdapter() {
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (!Character.isLetter(c) && !Character.isWhitespace(c)) {
                    e.consume();
                }
            }
        };

        nombresField.addKeyListener(soloLetras);
        apellidosField.addKeyListener(soloLetras);
    }

    private void registrarPaciente() {
        try {
            String cedula = cedulaField.getText().trim();
            if (cedula.length() != 10) {
                throw new IllegalArgumentException("La cédula debe tener 10 dígitos");
            }

            String nombres = nombresField.getText().trim();
            String apellidos = apellidosField.getText().trim();
            if (nombres.isEmpty() || apellidos.isEmpty()) {
                throw new IllegalArgumentException("Nombres y apellidos son obligatorios");
            }

            Date fechaNacimiento = fechaNacimientoChooser.getDate();
            if (fechaNacimiento == null) {
                throw new IllegalArgumentException("Seleccione una fecha de nacimiento");
            }

            String contrasena = new String(contrasenaField.getPassword());
            if (contrasena.length() < 6) {
                throw new IllegalArgumentException("La contraseña debe tener al menos 6 caracteres");
            }

            Usuario nuevoPaciente = new Usuario(
                cedula, nombres, apellidos, fechaNacimiento,
                sexoComboBox.getSelectedItem().toString(),
                correoField.getText(), contrasena, "paciente",
                null, null, null // Campos médicos se llenarán después
            );

            UserDatabase.addUser(nuevoPaciente);
            JOptionPane.showMessageDialog(this, "Paciente registrado exitosamente");
            dispose();
            
             // Agregar estas líneas:
        new LoginWindow().setVisible(true);
        dispose();

        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.WARNING_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al registrar: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
