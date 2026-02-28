package com.Citas.CitasMedicas.services;

import com.Citas.CitasMedicas.entities.Patient;
import com.Citas.CitasMedicas.entities.Reserva;
import com.Citas.CitasMedicas.repositories.ReservaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ReservaService {

    @Autowired
    private ReservaRepository reservaRepository;

    public void saveAppointment(Reserva appointment) {
        reservaRepository.save(appointment);
    }

    public List<Reserva> findByPatientEmail(String email) {
        return reservaRepository.findByPatientEmail(email);
    }

    public List<Reserva> findCitasActivasPorPaciente(Long id) {
        return reservaRepository.findByPatientIdAndStatus(id, "RESERVADA");
    }

    public Optional<Reserva> findById(Long citaId) {
        return reservaRepository.findById(citaId);
    }

    public List<Reserva> findByDoctorIdAndStatus(Long doctorId, String status) {
        return reservaRepository.findByDoctorIdAndStatus(doctorId, status);
    }

    public List<Reserva> findByAreaMedica(String area) {
        return reservaRepository.findByAreaMedica(area);
    }

    public List<Reserva> findAll() {
        return reservaRepository.findAll();
    }

    public List<Reserva> findPendientesPorDoctor(Long doctorId) {
        return reservaRepository.findByDoctorIdAndStatusIn(
            doctorId, Arrays.asList("RESERVADA", "PENDIENTE_REPROGRAMACION"));
    }

    public List<Reserva> findByDoctorId(Long doctorId) {
        return reservaRepository.findByDoctorId(doctorId);
    }

    // SOLO PACIENTES YA ATENDIDOS POR EL MÉDICO
    public List<Patient> findPacientesAtendidosPorDoctor(Long doctorId) {
        List<Reserva> reservasAtendidas = reservaRepository.findByDoctorId(doctorId).stream()
            .filter(r -> "ATENDIDA".equalsIgnoreCase(r.getStatus()) || "FINALIZADA".equalsIgnoreCase(r.getStatus()))
            .filter(r -> r.getPatient() != null)
            .collect(Collectors.toList());
        Set<Long> pacientesUnicos = new HashSet<>();
        return reservasAtendidas.stream()
            .map(Reserva::getPatient)
            .filter(p -> pacientesUnicos.add(p.getId()))
            .collect(Collectors.toList());
    }

    // Citas pendientes para agenda del médico
    public List<Reserva> findCitasPorAtenderDeDoctor(Long doctorId) {
        return reservaRepository.findByDoctorId(doctorId).stream()
            .filter(r -> 
                "RESERVADA".equalsIgnoreCase(r.getStatus())
                || "CONFIRMADA".equalsIgnoreCase(r.getStatus())
            )
            .collect(Collectors.toList());
    }

    // Eliminar (limpiar) historial del paciente
    public void deleteAllByPatientEmail(String email) {
        List<Reserva> reservas = reservaRepository.findByPatientEmail(email);
        reservaRepository.deleteAll(reservas);
    }

    // Historial completo de citas de un paciente específico con un doctor
    public List<Reserva> findCitasByPatientAndDoctor(Long patientId, Long doctorId) {
        return reservaRepository.findByDoctorId(doctorId).stream()
            .filter(r -> r.getPatient() != null && r.getPatient().getId().equals(patientId))
            .collect(Collectors.toList());
    }

    // Todas las citas atendidas de un doctor (para su historial)
    public List<Reserva> findCitasAtendidasPorDoctor(Long doctorId) {
        return reservaRepository.findByDoctorIdAndStatus(doctorId, "ATENDIDA");
    }

    // ===== MÉTODOS PARA REPORTES =====
    
    // Contar total de citas
    public long count() {
        return reservaRepository.count();
    }

    // Contar citas por estado
    public long countByStatus(String status) {
        return reservaRepository.countByStatus(status);
    }

    // Contar citas atendidas por doctor
    public long countCitasAtendidasPorDoctor(Long doctorId) {
        return reservaRepository.findByDoctorId(doctorId).stream()
            .filter(r -> "ATENDIDA".equalsIgnoreCase(r.getStatus()))
            .count();
    }

    // Contar citas pendientes por doctor
    public long countCitasPendientesPorDoctor(Long doctorId) {
        return reservaRepository.findByDoctorId(doctorId).stream()
            .filter(r -> "RESERVADA".equalsIgnoreCase(r.getStatus()) 
                      || "CONFIRMADA".equalsIgnoreCase(r.getStatus()))
            .count();
    }
}
