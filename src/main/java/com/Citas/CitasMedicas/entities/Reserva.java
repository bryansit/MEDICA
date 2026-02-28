package com.Citas.CitasMedicas.entities;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Reserva {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "patient_id")
    private Patient patient;

    @Column(nullable = false)
    private Long doctorId;

    @Column(length = 100)
    private String areaMedica;

    @Column(nullable = false)
    private String dateTime;

    @Column(nullable = false, length = 50)
    private String status;

    @Column(length = 20)
    private String dniPatient;

    @Column(columnDefinition = "TEXT")
    private String diagnosis;

    @Column
    private Double costo;

    // Métodos auxiliares para Thymeleaf (puedes mantenerlos)
    public String getDniPaciente() {
        return (dniPatient != null) ? dniPatient : (patient != null ? patient.getDni() : "");
    }

    public String getFechaConsulta() {
        return dateTime;
    }

    public String getDiagnostico() {
        return diagnosis != null ? diagnosis : "";
    }

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Patient getPatient() {
		return patient;
	}

	public void setPatient(Patient patient) {
		this.patient = patient;
	}

	public Long getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(Long doctorId) {
		this.doctorId = doctorId;
	}

	public String getAreaMedica() {
		return areaMedica;
	}

	public void setAreaMedica(String areaMedica) {
		this.areaMedica = areaMedica;
	}

	public String getDateTime() {
		return dateTime;
	}

	public void setDateTime(String dateTime) {
		this.dateTime = dateTime;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getDniPatient() {
		return dniPatient;
	}

	public void setDniPatient(String dniPatient) {
		this.dniPatient = dniPatient;
	}

	public String getDiagnosis() {
		return diagnosis;
	}

	public void setDiagnosis(String diagnosis) {
		this.diagnosis = diagnosis;
	}

	public Double getCosto() {
		return costo;
	}

	public void setCosto(Double costo) {
		this.costo = costo;
	}
    
    
}
