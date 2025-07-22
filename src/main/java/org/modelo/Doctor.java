package org.modelo;



import java.util.Date;

public class Doctor extends Usuario {
    private String especialidad;

    public Doctor(String cedula, String nombres, String apellidos, Date fechaNacimiento,
                  String sexo, String correo, String contrasena, String rol, String especialidad) {
        super(cedula, nombres, apellidos, fechaNacimiento, sexo, correo, contrasena, rol);
        this.especialidad = especialidad;
    }

    public String getEspecialidad() {
        return especialidad;
    }

    public void setEspecialidad(String especialidad) {
        this.especialidad = especialidad;
    }

    @Override
    public String toString() {
        return "Dr. " + getNombres() + " " + getApellidos() + " - Especialidad: " + especialidad;
    }
}
