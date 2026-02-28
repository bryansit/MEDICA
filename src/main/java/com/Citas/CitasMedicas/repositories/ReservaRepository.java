package com.Citas.CitasMedicas.repositories;

import com.Citas.CitasMedicas.entities.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ReservaRepository extends JpaRepository<Reserva, Long> {
    List<Reserva> findByPatientEmail(String email);
    
    List<Reserva> findByPatientIdAndStatus(Long id, String status);
    
    List<Reserva> findByDoctorIdAndStatus(Long doctorId, String status);
    
    List<Reserva> findByAreaMedica(String areaMedica);
    
    List<Reserva> findByDoctorId(Long doctorId);
    
    List<Reserva> findByDoctorIdAndStatusIn(Long doctorId, List<String> statuses);
    
    List<Reserva> findAll();
    
    // Contar por estado
    long countByStatus(String status);
}
