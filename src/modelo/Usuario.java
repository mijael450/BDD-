package modelo;
import java.util.Date;

public class Usuario {
    // Campos comunes
    private String cedula;
    private String nombres;
    private String apellidos;
    private Date fechaNacimiento;
    private String sexo;
    private String correo;
    private String contrasena; // Se eliminó el campo usuario y se mantiene contrasena
    private String rol; // "paciente" o "medico"
    
    // Campos específicos de pacientes
    private String alergias;
    private String oxigenacion;
    private String idAntecedentes;
    
    // Constructor para médicos
    public Usuario(String cedula, String nombres, String apellidos, Date fechaNacimiento, 
                  String sexo, String correo, String contrasena, String rol) {
        this.cedula = cedula;
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.fechaNacimiento = fechaNacimiento;
        this.sexo = sexo;
        this.correo = correo;
        this.contrasena = contrasena;
        this.rol = rol;
    }
    
    // Constructor para pacientes
    public Usuario(String cedula, String nombres, String apellidos, Date fechaNacimiento, 
                  String sexo, String correo, String contrasena, String rol,
                  String alergias, String oxigenacion, String idAntecedentes) {
        this(cedula, nombres, apellidos, fechaNacimiento, sexo, correo, contrasena, rol);
        this.alergias = alergias;
        this.oxigenacion = oxigenacion;
        this.idAntecedentes = idAntecedentes;
    }
    
    // Getters
    public String getCedula() { return cedula; }
    public String getNombres() { return nombres; }
    public String getApellidos() { return apellidos; }
    public Date getFechaNacimiento() { return fechaNacimiento; }
    public String getSexo() { return sexo; }
    public String getCorreo() { return correo; }
    public String getContrasena() { return contrasena; }
    public String getRol() { return rol; }
    public String getAlergias() { return alergias; }
    public String getOxigenacion() { return oxigenacion; }
    public String getIdAntecedentes() { return idAntecedentes; }
    
    // Método para verificar si es paciente
    public boolean esPaciente() {
        return "paciente".equals(rol);
    }
}