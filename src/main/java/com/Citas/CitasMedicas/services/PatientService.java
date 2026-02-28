package com.Citas.CitasMedicas.services;

import com.Citas.CitasMedicas.entities.Patient;
import com.Citas.CitasMedicas.repositories.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PatientService {

    @Autowired
    private PatientRepository patientRepository;

    public void savePatient(Patient patient) {
        patientRepository.save(patient);
    }

    public List<Patient> findAll() {
        return patientRepository.findAll();
    }

    public Optional<Patient> findById(Long id) {
        return patientRepository.findById(id);
    }

    public void deletePatient(Long id) {
        patientRepository.deleteById(id);
    }

    public Optional<Patient> findByEmail(String email) {
        return patientRepository.findByEmailIgnoreCase(email);
    }
    public long count() {
        return patientRepository.count();
    }
}
