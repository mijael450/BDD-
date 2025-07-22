package org.modelo;



import org.config.ConexionSQL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Date;
import java.util.HashMap;

public class UserDatabase {

    private static final HashMap<String, Usuario> users = new HashMap<>();

    public static boolean userExists(String cedula) {
        return users.containsKey(cedula);
    }

    public static Usuario getUser(String cedula) {
        return users.get(cedula);
    }

    public static void addUser(Usuario user) {
        if (!userExists(user.getCedula())) {
            users.put(user.getCedula(), user);
            insertarEnBaseDeDatos(user);
        }
    }

    public static boolean isMedico(String cedula) {
        Usuario user = users.get(cedula);
        return user != null && "medico".equals(user.getRol());
    }

    public static boolean isPaciente(String cedula) {
        Usuario user = users.get(cedula);
        return user != null && "paciente".equals(user.getRol());
    }

    private static void insertarEnBaseDeDatos(Usuario u) {
        String sql = "INSERT INTO paciente (cedula, nombres, apellidos, fecha_nacimiento, sexo, correo, contrasena_paciente, alergias, oxigenacion, id_antecedetes, rol) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = ConexionSQL.conectar(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, u.getCedula());
            ps.setString(2, u.getNombres());
            ps.setString(3, u.getApellidos());

            java.sql.Date sqlDate = new java.sql.Date(u.getFechaNacimiento().getTime());
            ps.setDate(4, sqlDate);

            ps.setString(5, u.getSexo());
            ps.setString(6, u.getCorreo());
            ps.setString(7, u.getContrasena());
            ps.setString(8, u.getAlergias());
            ps.setString(9, u.getOxigenacion());
            ps.setString(10, u.getIdAntecedentes());
            ps.setString(11, u.getRol());

            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void cargarUsuariosDesdeBaseDeDatos() {
        try (Connection conn = ConexionSQL.conectar();
             PreparedStatement ps = conn.prepareStatement("SELECT cedula, nombres, apellidos, fecha_nacimiento, sexo, correo, contrasena_paciente, alergias, oxigenacion, id_antecedetes, rol FROM paciente");
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Usuario u = new Usuario(
                    rs.getString("cedula"),
                    rs.getString("nombres"),
                    rs.getString("apellidos"),
                    rs.getDate("fecha_nacimiento"),
                    rs.getString("sexo"),
                    rs.getString("correo"),
                    rs.getString("contrasena_paciente"),
                    rs.getString("rol"),
                    rs.getString("alergias"),
                    rs.getString("oxigenacion"),
                    rs.getString("id_antecedetes")
                );
                users.put(u.getCedula(), u);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}