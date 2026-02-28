package com.Citas.CitasMedicas.services;

import com.Citas.CitasMedicas.entities.Doctor;
import com.Citas.CitasMedicas.repositories.DoctorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DoctorService {

    @Autowired
    private DoctorRepository doctorRepository;

    public void saveDoctor(Doctor doctor) {
        doctorRepository.save(doctor);
    }

    public List<Doctor> findAll() {
        return doctorRepository.findAll();
    }

    public Optional<Doctor> findById(Long id) {
        return doctorRepository.findById(id);
    }

    public void deleteDoctor(Long id) {
        doctorRepository.deleteById(id);
    }

    // Obtener especialidades distintas (optimizado con query nativa)
    public List<String> getAllEspecialidades() {
        return doctorRepository.findDistinctEspecialidades();
    }

    // Buscar doctor por email (optimizado con query del repository)
    public Optional<Doctor> findByEmail(String email) {
        return doctorRepository.findByEmailIgnoreCase(email);
    }
    
    public long count() {
        return doctorRepository.count();
    }

    // Cambiar estado activo/inactivo del doctor
    public void cambiarEstado(Long id, Boolean activo) {
        Optional<Doctor> doctorOpt = doctorRepository.findById(id);
        if (doctorOpt.isPresent()) {
            Doctor doctor = doctorOpt.get();
            doctor.setActivo(activo);
            doctorRepository.save(doctor);
        }
    }
}
