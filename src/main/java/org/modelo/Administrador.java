package org.modelo;



public class Administrador {
    private String cedula;
    private String nombres;
    private String apellidos;
    private String correo;
    private String contrasena;
    private String sexo;
    private String fechaNacimiento;
    private String rol;

    public Administrador(String cedula, String nombres, String apellidos, String correo,
                         String contrasena, String fechaNacimiento, String sexo, String rol) {
        this.cedula = cedula;
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.correo = correo;
        this.contrasena = contrasena;
        this.fechaNacimiento = fechaNacimiento;
        this.sexo = sexo;
        this.rol = rol;
    }

    // Getters
    public String getCedula() { return cedula; }
    public String getNombres() { return nombres; }
    public String getApellidos() { return apellidos; }
    public String getCorreo() { return correo; }
    public String getContrasena() { return contrasena; }
    public String getFechaNacimiento() { return fechaNacimiento; }
    public String getSexo() { return sexo; }
    public String getRol() { return rol; }
    }