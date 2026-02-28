package com.Citas.CitasMedicas.entities;

public class EstadisticaMedico {
    
    private String nombreMedico;
    private String especialidad;
    private long citasAtendidas;
    private long pacientesUnicos;
    
    // Constructor vacío
    public EstadisticaMedico() {
    }
    
    // Constructor con parámetros
    public EstadisticaMedico(String nombreMedico, String especialidad, long citasAtendidas, long pacientesUnicos) {
        this.nombreMedico = nombreMedico;
        this.especialidad = especialidad;
        this.citasAtendidas = citasAtendidas;
        this.pacientesUnicos = pacientesUnicos;
    }
    
    // Getters y Setters
    public String getNombreMedico() {
        return nombreMedico;
    }
    
    public void setNombreMedico(String nombreMedico) {
        this.nombreMedico = nombreMedico;
    }
    
    public String getEspecialidad() {
        return especialidad;
    }
    
    public void setEspecialidad(String especialidad) {
        this.especialidad = especialidad;
    }
    
    public long getCitasAtendidas() {
        return citasAtendidas;
    }
    
    public void setCitasAtendidas(long citasAtendidas) {
        this.citasAtendidas = citasAtendidas;
    }
    
    public long getPacientesUnicos() {
        return pacientesUnicos;
    }
    
    public void setPacientesUnicos(long pacientesUnicos) {
        this.pacientesUnicos = pacientesUnicos;
    }
}
