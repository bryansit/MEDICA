package com.Citas.CitasMedicas.repositories;

import com.Citas.CitasMedicas.entities.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Long> {
    
    Optional<Doctor> findByEmailIgnoreCase(String email);
    
    // Query personalizada para obtener especialidades distintas de doctores ACTIVOS
    @Query("SELECT DISTINCT d.areaMedica FROM Doctor d WHERE d.activo = true")
    List<String> findDistinctEspecialidades();
    
    // Encontrar solo doctores activos
    List<Doctor> findByActivoTrue();
}
