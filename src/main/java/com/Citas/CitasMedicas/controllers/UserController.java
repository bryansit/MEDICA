package com.Citas.CitasMedicas.controllers;

import com.Citas.CitasMedicas.entities.Doctor;
import com.Citas.CitasMedicas.entities.EstadisticaMedico;
import com.Citas.CitasMedicas.entities.Patient;
import com.Citas.CitasMedicas.entities.Reserva;
import com.Citas.CitasMedicas.services.DoctorService;
import com.Citas.CitasMedicas.services.PatientService;
import com.Citas.CitasMedicas.services.ReservaService;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
@Controller
public class UserController {

    @Autowired
    private PatientService patientService;

    @Autowired
    private DoctorService doctorService;
    
    @Autowired
    private ReservaService reservaService;

    // HOME
    @GetMapping("/")
    public String showHome(HttpSession session) {
        String email = (String) session.getAttribute("loggedInEmail");
        if (email == null)
            return "login";
        if (email.endsWith("@medico.com")) {
            return "redirect:/doctor/dashboard";
        }
        return "redirect:/index";
    }

    // LOGIN
    @GetMapping("/login")
    public String showLoginForm(Model model, HttpSession session, @RequestParam(value = "error", required = false) String error) {
        if (session.getAttribute("loggedInEmail") != null) {
            String email = (String) session.getAttribute("loggedInEmail");
            if (email.endsWith("@medico.com")) {
                return "redirect:/doctor/dashboard";
            }
            return "redirect:/index";
        }
        if (error != null) {
            model.addAttribute("error", error);
        }
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String email,
                        @RequestParam String password,
                        HttpSession session,
                        Model model) {

        System.out.println("Intentando login con: " + email);

        if ("administrador@medico.com".equals(email) && "12345678".equals(password)) {
            session.setAttribute("loggedInEmail", email);
            System.out.println("Login administrador exitoso");
            return "redirect:/admin/dashboard";
        }

        if ((email.endsWith("@paciente.com") || email.endsWith("@medico.com")) 
            && !email.isBlank() && !password.isBlank()) {
            session.setAttribute("loggedInEmail", email);
            System.out.println("Login exitoso: " + email);
            if (email.endsWith("@medico.com")) {
                return "redirect:/doctor/dashboard";
            }
            return "redirect:/index";
        }

        model.addAttribute("error", "Correo o contraseña incorrectos");
        return "login";
    }

    // LOGOUT
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        System.out.println("Sesión cerrada");
        return "redirect:/login";
    }
    @PostMapping("/logout")
    public String logoutPost(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }

    // DASHBOARD PACIENTE (PÁGINA PRINCIPAL)
    @GetMapping("/index")
    public String showIndexPage(Model model, HttpSession session) {
        String email = (String) session.getAttribute("loggedInEmail");
        if (email == null || !email.endsWith("@paciente.com")) {
            return "redirect:/login?error=Acceso denegado";
        }
        model.addAttribute("loggedInEmail", email);
        return "index";
    }

    // DASHBOARD MÉDICO
    @GetMapping("/doctor/dashboard")
    public String showDoctorDashboard(Model model, HttpSession session) {
        String email = (String) session.getAttribute("loggedInEmail");
        if (email == null || !email.endsWith("@medico.com")) {
            return "redirect:/login?error=Acceso denegado";
        }
        Doctor doctor = doctorService.findAll().stream()
            .filter(d -> d.getEmail().equals(email)).findFirst().orElse(null);
        model.addAttribute("loggedInEmail", email);
        model.addAttribute("doctor", doctor);
        return "doctor-dashboard";
    }

    // REGISTRO PACIENTE
    @GetMapping("/register/patient")
    public String showRegisterPatientForm(Model model, HttpSession session) {
        if (session.getAttribute("loggedInEmail") != null) {
            String email = (String) session.getAttribute("loggedInEmail");
            if (email.endsWith("@medico.com")) {
                return "redirect:/doctor/dashboard";
            }
            return "redirect:/index";
        }
        model.addAttribute("patient", new Patient());
        return "register-patient";
    }

    @PostMapping("/register/patient")
    public String registerPatient(@RequestParam String email,
                                  @RequestParam String password,
                                  @RequestParam String firstName,
                                  @RequestParam String lastName,
                                  @RequestParam String dni,
                                  @RequestParam String phone,
                                  Model model,
                                  HttpSession session) {

        if (!email.endsWith("@paciente.com")) {
            model.addAttribute("error", "El correo debe terminar en @paciente.com");
            model.addAttribute("patient", new Patient());
            return "register-patient";
        }

        Patient patient = new Patient();
        patient.setFirstName(firstName);
        patient.setLastName(lastName);
        patient.setDni(dni);
        patient.setEmail(email);
        patient.setPassword(password);
        patient.setPhone(phone);

        try {
            patientService.savePatient(patient);
            session.setAttribute("loggedInEmail", email);
            return "redirect:/index";
        } catch (Exception e) {
            model.addAttribute("error", "Error al registrar: " + e.getMessage());
            return "register-patient";
        }
    }

    // REGISTRO MÉDICO
    @GetMapping("/register/doctor")
    public String showRegisterDoctorForm(Model model, HttpSession session) {
        if (session.getAttribute("loggedInEmail") != null) {
            String email = (String) session.getAttribute("loggedInEmail");
            if (email.endsWith("@medico.com")) {
                return "redirect:/doctor/dashboard";
            }
            return "redirect:/index";
        }
        model.addAttribute("doctor", new Doctor());
        return "register-doctor";
    }

    @PostMapping("/register/doctor")
    public String registerDoctor(@RequestParam String email,
                                 @RequestParam String password,
                                 @RequestParam String firstName,
                                 @RequestParam String lastName,
                                 @RequestParam String dni,
                                 @RequestParam String phone,
                                 @RequestParam String areaMedica,
                                 Model model,
                                 HttpSession session) {

        if (!email.endsWith("@medico.com")) {
            model.addAttribute("error", "El correo debe terminar en @medico.com");
            model.addAttribute("doctor", new Doctor());
            return "register-doctor";
        }

        Doctor doctor = new Doctor();
        doctor.setFirstName(firstName);
        doctor.setLastName(lastName);
        doctor.setDni(dni);
        doctor.setEmail(email);
        doctor.setPassword(password);
        doctor.setPhone(phone);
        doctor.setAreaMedica(areaMedica);

        try {
            doctorService.saveDoctor(doctor);
            session.setAttribute("loggedInEmail", email);
            return "redirect:/doctor/dashboard";
        } catch (Exception e) {
            model.addAttribute("error", "Error al registrar: " + e.getMessage());
            return "register-doctor";
        }
    }

    // GESTIÓN DE ESPECIALIDADES (solo médicos)
    @GetMapping("/especialidades")
    public String showSpecialtiesManagement(Model model, HttpSession session) {
        String email = (String) session.getAttribute("loggedInEmail");
        if (email == null || !email.endsWith("@medico.com")) {
            return "redirect:/login?error=Acceso denegado";
        }
        try {
            Iterable<Doctor> doctors = doctorService.findAll();
            model.addAttribute("doctors", doctors != null ? doctors : Collections.emptyList());
            model.addAttribute("loggedInEmail", email);
        } catch (Exception e) {
            model.addAttribute("doctors", Collections.emptyList());
            System.out.println("Error en /especialidades: " + e.getMessage());
        }
        return "especialidades";
    }

    // LISTADOS SOLO MÉDICO
    @GetMapping("/list-patients")
    public String listPatients(Model model, HttpSession session) {
        String email = (String) session.getAttribute("loggedInEmail");
        if (email == null || !email.endsWith("@medico.com")) {
            return "redirect:/login?error=Acceso denegado";
        }
        model.addAttribute("patients", patientService.findAll());
        return "list-patients";
    }

    @GetMapping("/list-doctors")
    public String showListDoctorsForPatient(Model model, HttpSession session) {
        String email = (String) session.getAttribute("loggedInEmail");
        if (email == null || !email.endsWith("@paciente.com")) {
            return "redirect:/login?error=Acceso denegado";
        }
        model.addAttribute("doctors", doctorService.findAll());
        return "especialidades";
    }



    // ADMIN DASHBOARD Y CRUD
    @GetMapping("/admin/dashboard")
    public String showAdminDashboard(Model model, HttpSession session) {
        String email = (String) session.getAttribute("loggedInEmail");
        if (!isAdmin(session)) {
            return "redirect:/login?error=Acceso denegado";
        }
        model.addAttribute("doctors", doctorService.findAll());
        model.addAttribute("patients", patientService.findAll());
        return "admin-dashboard";
    }

    @GetMapping("/admin/patients")
    public String showAdminPatientsForm(@RequestParam(required = false) Long id, Model model, HttpSession session) {
        if (!isAdmin(session)) return "redirect:/login";
        Patient patient = id != null ? patientService.findById(id).orElse(new Patient()) : new Patient();
        model.addAttribute("patient", patient);
        model.addAttribute("patients", patientService.findAll());
        return "admin-patients";
    }

    @PostMapping("/admin/patients")
    public String adminSavePatient(@RequestParam(required = false) Long id,
                                   @RequestParam String email, @RequestParam String password,
                                   @RequestParam String firstName, @RequestParam String lastName,
                                   @RequestParam String dni, @RequestParam String phone,
                                   Model model, HttpSession session) {
        if (!isAdmin(session)) return "redirect:/login";
        if (!email.endsWith("@paciente.com")) {
            model.addAttribute("error", "El correo debe terminar en @paciente.com");
            model.addAttribute("patients", patientService.findAll());
            return "admin-patients";
        }
        Patient patient = id != null ? patientService.findById(id).orElse(new Patient()) : new Patient();
        patient.setFirstName(firstName);
        patient.setLastName(lastName);
        patient.setDni(dni);
        patient.setEmail(email);
        patient.setPassword(password);
        patient.setPhone(phone);

        try {
            patientService.savePatient(patient);
        } catch (Exception e) {
            model.addAttribute("error", "Error: " + e.getMessage());
            model.addAttribute("patients", patientService.findAll());
            return "admin-patients";
        }
        return "redirect:/admin/patients";
    }

    @GetMapping("/admin/delete/patient/{id}")
    public String deletePatient(@PathVariable Long id, HttpSession session) {
        if (!isAdmin(session)) return "redirect:/login";
        patientService.deletePatient(id);
        return "redirect:/admin/patients";
    }

    @GetMapping("/admin/doctors")
    public String showAdminDoctorsForm(@RequestParam(required = false) Long id, Model model, HttpSession session) {
        if (!isAdmin(session)) return "redirect:/login";
        Doctor doctor = id != null ? doctorService.findById(id).orElse(new Doctor()) : new Doctor();
        model.addAttribute("doctor", doctor);
        model.addAttribute("doctors", doctorService.findAll());
        return "admin-doctors";
    }

    @PostMapping("/admin/doctors")
    public String adminSaveDoctor(@RequestParam(required = false) Long id,
                                  @RequestParam String email, @RequestParam String password,
                                  @RequestParam String firstName, @RequestParam String lastName,
                                  @RequestParam String dni, @RequestParam String phone,
                                  @RequestParam String areaMedica,
                                  Model model, HttpSession session) {
        if (!isAdmin(session)) return "redirect:/login";
        if (!email.endsWith("@medico.com")) {
            model.addAttribute("error", "El correo debe terminar en @medico.com");
            model.addAttribute("doctors", doctorService.findAll());
            return "admin-doctors";
        }
        Doctor doctor = id != null ? doctorService.findById(id).orElse(new Doctor()) : new Doctor();
        doctor.setFirstName(firstName);
        doctor.setLastName(lastName);
        doctor.setDni(dni);
        doctor.setEmail(email);
        doctor.setPassword(password);
        doctor.setPhone(phone);
        doctor.setAreaMedica(areaMedica);

        try {
            doctorService.saveDoctor(doctor);
        } catch (Exception e) {
            model.addAttribute("error", "Error: " + e.getMessage());
            model.addAttribute("doctors", doctorService.findAll());
            return "admin-doctors";
        }
        return "redirect:/admin/doctors";
    }

    @GetMapping("/admin/delete/doctor/{id}")
    public String deleteDoctor(@PathVariable Long id, HttpSession session) {
        if (!isAdmin(session)) return "redirect:/login";
        doctorService.deleteDoctor(id);
        return "redirect:/admin/doctors";
    }

    // ===== MÉTODO AUXILIAR ADMIN =====
    private boolean isAdmin(HttpSession session) {
        String email = (String) session.getAttribute("loggedInEmail");
        return "administrador@medico.com".equals(email);
    }
 // ==========================================
 // CAMBIAR ESTADO DEL DOCTOR (ADMIN)
 // ==========================================
 @PostMapping("/admin/doctors/cambiar-estado/{id}")
 public String cambiarEstadoDoctor(@PathVariable Long id, 
                                   @RequestParam Boolean activo,
                                   HttpSession session) {
     if (!isAdmin(session)) {
         return "redirect:/login";
     }
     
     doctorService.cambiarEstado(id, activo);
     return "redirect:/admin/doctors";
 }
    
    // ========================
    // HISTORIAL DE CITAS (PACIENTE)
    // ========================
    @GetMapping("/historial")
    public String showHistorialCitas(Model model, HttpSession session) {
        String email = (String) session.getAttribute("loggedInEmail");
        if (email == null || !email.endsWith("@paciente.com")) {
            return "redirect:/login?error=Acceso denegado";
        }
        // Trae las reservas del paciente autenticado (si el método se llama diferente, cámbialo)
        List<Reserva> reservas = reservaService.findByPatientEmail(email);
        model.addAttribute("reservas", reservas);
        model.addAttribute("loggedInEmail", email);
        return "historial-de-citas";
    }

    // ========================
    // RESERVA DE CITA (PACIENTE)
    // ========================
 // Mostrar formulario de reserva
    @GetMapping("/reserva")
    public String showReservaForm(@RequestParam(required = false) String especialidad, Model model, HttpSession session) {
        String email = (String) session.getAttribute("loggedInEmail");
        if (email == null || !email.endsWith("@paciente.com")) {
            return "redirect:/login?error=Acceso denegado";
        }
        List<String> especialidades = doctorService.getAllEspecialidades();
        model.addAttribute("especialidades", especialidades);
        model.addAttribute("especialidad", especialidad); // Para saber cuál está seleccionada

        List<Doctor> medicos;
        if (especialidad != null && !especialidad.isBlank()) {
            // Solo doctores de esa especialidad
            medicos = doctorService.findAll()
                .stream().filter(d -> especialidad.equalsIgnoreCase(d.getAreaMedica()))
                .toList();
        } else {
            // Ninguno o todos, según tu preferencia
            medicos = List.of();
        }
        model.addAttribute("medicos", medicos);
        model.addAttribute("precioFijo", "35.00 S/."); // Para mostrar en la vista si lo deseas
        return "reserva";
    }

    // Procesar reserva POST
    @PostMapping("/reserva")
    public String reservarCita(@RequestParam String especialidad,
                               @RequestParam Long medico,
                               @RequestParam String fecha,
                               @RequestParam String hora,
                               Model model, HttpSession session) {
        String email = (String) session.getAttribute("loggedInEmail");
        if (email == null || !email.endsWith("@paciente.com")) {
            return "redirect:/login?error=Acceso denegado";
        }
        try {
            Optional<Patient> pacienteOpt = patientService.findByEmail(email);
            if (pacienteOpt.isEmpty()) {
                model.addAttribute("error", "No se encontró el paciente con el email dado.");
                model.addAttribute("especialidades", doctorService.getAllEspecialidades());
                model.addAttribute("medicos", doctorService.findAll());
                model.addAttribute("precioFijo", "35.00 S/.");
                return "reserva";
            }
            Patient paciente = pacienteOpt.get();
            Doctor doctor = doctorService.findById(medico).orElse(null);

            Reserva reserva = new Reserva();
            reserva.setPatient(paciente);
            reserva.setDoctorId(doctor != null ? doctor.getId() : null);
            reserva.setAreaMedica(especialidad);  // <-- GUÁRDALO AQUÍ
            reserva.setDateTime(fecha + " " + hora);
            reserva.setStatus("RESERVADA");
            reserva.setDniPatient(paciente.getDni());
            reserva.setDiagnosis(""); // Diagnóstico lo llenas después de la cita
            reserva.setCosto(35.00); // PRECIO FIJO

            reservaService.saveAppointment(reserva);
            model.addAttribute("success", "¡Cita reservada correctamente!");
        } catch (Exception e) {
            model.addAttribute("error", "No se pudo reservar la cita: " + e.getMessage());
        }
        model.addAttribute("especialidades", doctorService.getAllEspecialidades());
        model.addAttribute("medicos", doctorService.findAll());
        model.addAttribute("precioFijo", "35.00 S/.");
        return "reserva";
    }

    @GetMapping("/reprogramar")
    public String showReprogramarForm(Model model, HttpSession session) {
        String email = (String) session.getAttribute("loggedInEmail");
        if (email == null || !email.endsWith("@paciente.com")) {
            return "redirect:/login?error=Acceso denegado";
        }
        Optional<Patient> pacienteOpt = patientService.findByEmail(email);
        if (pacienteOpt.isEmpty()) {
            model.addAttribute("error", "No se encontró el paciente.");
            return "reprogramar";
        }
        Patient paciente = pacienteOpt.get();
        List<Reserva> citas = reservaService.findCitasActivasPorPaciente(paciente.getId()); // Método personalizado
        model.addAttribute("citas", citas);
        return "reprogramar-cita";
    }
    
    @PostMapping("/reprogramar")
    public String procesarReprogramarCita(
            @RequestParam Long citaId,
            @RequestParam String fecha,
            @RequestParam String hora,
            @RequestParam String motivo, // <-- nuevo
            Model model,
            HttpSession session) 
    {
        String email = (String) session.getAttribute("loggedInEmail");
        if (email == null || !email.endsWith("@paciente.com")) {
            return "redirect:/login?error=Acceso denegado";
        }
        Optional<Patient> pacienteOpt = patientService.findByEmail(email);
        if (pacienteOpt.isEmpty()) {
            model.addAttribute("error", "No se encontró el paciente.");
            return "reprogramar-cita";
        }
        try {
            Optional<Reserva> reservaOpt = reservaService.findById(citaId);
            if (reservaOpt.isEmpty()) {
                model.addAttribute("error", "No se encontró la cita seleccionada.");
            } else {
                Reserva reserva = reservaOpt.get();
                if (!reserva.getPatient().getId().equals(pacienteOpt.get().getId()) ||
                    !"RESERVADA".equalsIgnoreCase(reserva.getStatus())) {
                    model.addAttribute("error", "No puedes reprogramar esta cita.");
                } else {
                    reserva.setDateTime(fecha + " " + hora);
                    reserva.setDiagnosis(motivo); // <-- GUARDAR MOTIVO EN diagnosis
                    reserva.setStatus("PENDIENTE_REPROGRAMACION");
                    reservaService.saveAppointment(reserva);
                    model.addAttribute("success", "Cita reprogramada exitosamente. Espera confirmación del médico.");
                }
            }
        } catch (Exception e) {
            model.addAttribute("error", "No se pudo reprogramar la cita: " + e.getMessage());
        }
        List<Reserva> citas = reservaService.findCitasActivasPorPaciente(pacienteOpt.get().getId());
        model.addAttribute("citas", citas);
        return "reprogramar-cita";
    }


    
    @GetMapping("/cancelar")
    public String showCancelarForm(Model model, HttpSession session) {
        String email = (String) session.getAttribute("loggedInEmail");
        if (email == null || !email.endsWith("@paciente.com")) {
            return "redirect:/login?error=Acceso denegado";
        }
        Optional<Patient> pacienteOpt = patientService.findByEmail(email);
        if (pacienteOpt.isEmpty()) {
            model.addAttribute("error", "No se encontró el paciente.");
            return "cancelar-cita";
        }
        Patient paciente = pacienteOpt.get();
        List<Reserva> citas = reservaService.findCitasActivasPorPaciente(paciente.getId());
        model.addAttribute("citas", citas);
        return "cancelar-cita";
    }
    
    @PostMapping("/cancelar")
    public String procesarCancelarCita(@RequestParam Long citaId,
                                       @RequestParam(required = false) String motivo,
                                       Model model,
                                       HttpSession session) {
        String email = (String) session.getAttribute("loggedInEmail");
        if (email == null || !email.endsWith("@paciente.com")) {
            return "redirect:/login?error=Acceso denegado";
        }
        Optional<Patient> pacienteOpt = patientService.findByEmail(email);
        if (pacienteOpt.isEmpty()) {
            model.addAttribute("error", "No se encontró el paciente.");
            return "cancelar-cita";
        }
        try {
            Optional<Reserva> reservaOpt = reservaService.findById(citaId);
            if (reservaOpt.isEmpty()) {
                model.addAttribute("error", "No se encontró la cita seleccionada.");
            } else {
                Reserva reserva = reservaOpt.get();
                // Solo cancela si sigue activa/reservada y es del paciente
                if (!reserva.getPatient().getId().equals(pacienteOpt.get().getId()) ||
                    !"RESERVADA".equalsIgnoreCase(reserva.getStatus())) {
                    model.addAttribute("error", "No puedes cancelar esta cita.");
                } else {
                    reserva.setStatus("CANCELADA");
                    // Aquí podrías guardar el motivo en diagnosis, o crea campo "cancelReason"
                    if (motivo != null && !motivo.isBlank()) {
                        reserva.setDiagnosis("Motivo de cancelación: " + motivo);
                    }
                    reservaService.saveAppointment(reserva);
                    model.addAttribute("success", "Cita cancelada exitosamente.");
                }
            }
        } catch (Exception e) {
            model.addAttribute("error", "No se pudo cancelar la cita: " + e.getMessage());
        }
        List<Reserva> citas = reservaService.findCitasActivasPorPaciente(pacienteOpt.get().getId());
        model.addAttribute("citas", citas);
        return "cancelar-cita";
    }
    @GetMapping("/detalle")
    public String detalleReserva(@RequestParam Long reservaId, Model model) {
        Optional<Reserva> reservaOpt = reservaService.findById(reservaId);
        if (reservaOpt.isPresent()) {
            model.addAttribute("reserva", reservaOpt.get());
            return "detalle-cita";
        } else {
            return "redirect:/historial?error=No encontrada";
        }
    }
    @GetMapping("/mi-perfil")
    public String showPerfilPaciente(Model model, HttpSession session) {
        String email = (String) session.getAttribute("loggedInEmail");
        if (email == null || !email.endsWith("@paciente.com"))
            return "redirect:/login?error=Acceso denegado";
        Patient paciente = patientService.findByEmail(email).orElse(null);
        model.addAttribute("patient", paciente);
        return "mi-perfil";
    }
 // GET editar-perfil
    @GetMapping("/editar-perfil")
    public String showEditarPerfil(Model model, HttpSession session) {
        String email = (String) session.getAttribute("loggedInEmail");
        if (email == null || !email.endsWith("@paciente.com"))
            return "redirect:/login?error=Acceso denegado";
        Patient paciente = patientService.findByEmail(email).orElse(null);
        model.addAttribute("patient", paciente);
        return "editar-perfil";
    }

    // POST editar-perfil
    @PostMapping("/editar-perfil")
    public String editarPerfilSubmit(@ModelAttribute Patient patient, Model model, HttpSession session) {
        try {
            // Recupera paciente actual (para evitar cambio de correo)
            Patient actual = patientService.findById(patient.getId()).orElse(null);
            if (actual == null) throw new Exception("Paciente no encontrado");
            actual.setFirstName(patient.getFirstName());
            actual.setLastName(patient.getLastName());
            actual.setPhone(patient.getPhone());
            actual.setDni(patient.getDni());
            if (patient.getPassword() != null && !patient.getPassword().isBlank())
                actual.setPassword(patient.getPassword());
            patientService.savePatient(actual);
            model.addAttribute("success", "Perfil actualizado correctamente.");
            model.addAttribute("patient", actual);
        } catch (Exception e) {
            model.addAttribute("error", "No se pudo actualizar: " + e.getMessage());
            model.addAttribute("patient", patient);
        }
        return "editar-perfil";
    }

    @GetMapping("/horarios")
    public String showHorariosFiltrados(
            @RequestParam(required = false) String area,
            Model model) {
        List<String> especialidades = doctorService.getAllEspecialidades();
        model.addAttribute("especialidades", especialidades);
        if (area != null && !area.isBlank()) {
            List<Doctor> doctores = doctorService.findAll().stream()
                .filter(d -> area.equalsIgnoreCase(d.getAreaMedica()))
                .toList();
            model.addAttribute("doctores", doctores);
            model.addAttribute("selectedArea", area);
        }
        return "disponibilidad-horarios";
    }

    @GetMapping("/notificaciones")
    public String notificacionesCitas(Model model, HttpSession session) {
        String email = (String) session.getAttribute("loggedInEmail");
        if (email == null || !email.endsWith("@medico.com")) {
            return "redirect:/login?error=Acceso denegado";
        }
        Long doctorId = doctorService.findByEmail(email)
            .map(com.Citas.CitasMedicas.entities.Doctor::getId)
            .orElse(null);

        // Todas las citas que estén RESERVADAS y no hayan sido atendidas ni canceladas
        List<Reserva> nuevasCitas = reservaService.findByDoctorIdAndStatus(doctorId, "RESERVADA");
        model.addAttribute("nuevasCitas", nuevasCitas);
        return "notificaciones";
    }
    @GetMapping("/doctor/citas-reprogramadas")
    public String citasReprogramadas(Model model, HttpSession session) {
        String email = (String) session.getAttribute("loggedInEmail");
        if (email == null || !email.endsWith("@medico.com")) {
            return "redirect:/login?error=Acceso denegado";
        }
        Long doctorId = doctorService.findByEmail(email)
                .map(Doctor::getId)
                .orElse(null);
        List<Reserva> reprogramadas = reservaService.findByDoctorIdAndStatus(doctorId, "PENDIENTE_REPROGRAMACION");
        model.addAttribute("citasReprogramadas", reprogramadas);
        return "citas-reprogramadas"; // crea este HTML Thymeleaf
    }


 // Confirmar reprogramación
    @PostMapping("/doctor/confirmarCita")
    public String confirmarCita(@RequestParam Long citaId, HttpSession session, Model model) {
        Optional<Reserva> reservaOpt = reservaService.findById(citaId);
        if (reservaOpt.isPresent()) {
            Reserva reserva = reservaOpt.get();
            reserva.setStatus("CONFIRMADA");
            reservaService.saveAppointment(reserva);
        }
        return "redirect:/doctor/citas-reprogramadas";
    }

 // Mostrar formulario para cambiar la fecha de la cita
    @GetMapping("/doctor/cambiarFechaCita")
    public String showCambiarFechaCitaForm(@RequestParam Long citaId, HttpSession session, Model model) {
        Optional<Reserva> reservaOpt = reservaService.findById(citaId);
        model.addAttribute("reserva", reservaOpt.orElse(null));
        return "cambiar-fecha-cita";
    }
    // Guardar nuevo horario
    @PostMapping("/doctor/cambiarFechaCita")
    public String cambiarFechaCita(@RequestParam Long citaId, @RequestParam String nuevaFecha, @RequestParam String nuevaHora, HttpSession session, Model model) {
        Optional<Reserva> reservaOpt = reservaService.findById(citaId);
        if (reservaOpt.isPresent()) {
            Reserva reserva = reservaOpt.get();
            reserva.setDateTime(nuevaFecha + " " + nuevaHora);
            reserva.setStatus("CONFIRMADA");
            reservaService.saveAppointment(reserva);
            model.addAttribute("success", "Fecha actualizada correctamente.");
        }
        return "redirect:/doctor/citas-reprogramadas";
    }
    @GetMapping("/pacientes")
    public String pacientesAsignados(Model model, HttpSession session) {
        String email = (String) session.getAttribute("loggedInEmail");
        if (email == null || !email.endsWith("@medico.com")) {
            return "redirect:/login?error=Acceso denegado";
        }
        Long doctorId = doctorService.findByEmail(email).map(Doctor::getId).orElse(null);
        List<Patient> pacientesAtendidos = reservaService.findPacientesAtendidosPorDoctor(doctorId);
        model.addAttribute("pacientes", pacientesAtendidos);
        return "pacientes-asignados";
    }

    @PostMapping("/doctor/atenderCita")
    public String atenderCita(@RequestParam Long citaId) {
        Optional<Reserva> reservaOpt = reservaService.findById(citaId);
        if (reservaOpt.isPresent()) {
            Reserva reserva = reservaOpt.get();
            reserva.setStatus("ATENDIDA");
            reservaService.saveAppointment(reserva);
        }
        return "redirect:/agenda-medica"; // <--- También debe unir con la vista y GET correcto
    }


    @GetMapping("/agenda-medica")
    public String showAgendaMedica(Model model, HttpSession session) {
        String email = (String) session.getAttribute("loggedInEmail");
        if (email == null || !email.endsWith("@medico.com")) {
            return "redirect:/login?error=Acceso denegado";
        }
        Long doctorId = doctorService.findByEmail(email)
            .map(Doctor::getId)
            .orElse(null);

        List<Reserva> citasPorAtender = reservaService.findCitasPorAtenderDeDoctor(doctorId);
        model.addAttribute("citasPorAtender", citasPorAtender);

        // AQUÍ el return:
        return "agenda-medica"; // <--- Debe coincidir exactamente con el HTML
    }

    @GetMapping("/doctor/historial")
    public String showDoctorHistorial(Model model, HttpSession session) {
        String email = (String) session.getAttribute("loggedInEmail");
        if (email == null || !email.endsWith("@medico.com")) {
            return "redirect:/login?error=Acceso denegado";
        }
        Long doctorId = doctorService.findByEmail(email).map(Doctor::getId).orElse(null);
        // Solo citas ATENDIDAS, pero puedes mostrar más si quieres (también CANCELADA, REPROGRAMADA, etc.)
        List<Reserva> citasAtendidas = reservaService.findByDoctorIdAndStatus(doctorId, "ATENDIDA");
        model.addAttribute("citasAtendidas", citasAtendidas);
        return "Historial-de-Citas-Atendidas"; // crea este archivo HTML
    }


    @GetMapping("/doctor/editar-diagnostico")
    public String editarDiagnosticoForm(@RequestParam("citaId") Long citaId, Model model) {
        Optional<Reserva> cita = reservaService.findById(citaId);
        if (cita.isPresent()) {
            model.addAttribute("cita", cita.get());
            return "editar-diagnostico";
        }
        return "redirect:/agenda-medica";
    }

    @PostMapping("/doctor/editar-diagnostico")
    public String guardarDiagnostico(@RequestParam Long citaId, @RequestParam String diagnosis) {
        Optional<Reserva> citaOpt = reservaService.findById(citaId);
        if (citaOpt.isPresent()) {
            Reserva cita = citaOpt.get();
            cita.setDiagnosis(diagnosis);
            reservaService.saveAppointment(cita);
        }
        return "redirect:/agenda-medica";
    }
    

    @GetMapping("/doctor/paciente-detalle")
    public String verHistorialPaciente(@RequestParam Long id, Model model, HttpSession session) {
        String email = (String) session.getAttribute("loggedInEmail");
        if (email == null || !email.endsWith("@medico.com")) {
            return "redirect:/login?error=Acceso denegado";
        }
        Long doctorId = doctorService.findByEmail(email).map(Doctor::getId).orElse(null);
        Optional<Patient> paciente = patientService.findById(id);
        
        if (paciente.isPresent()) {
            // Todas las citas de ese paciente con este doctor
            List<Reserva> citas = reservaService.findCitasByPatientAndDoctor(id, doctorId);
            model.addAttribute("paciente", paciente.get());
            model.addAttribute("citas", citas);
            return "paciente-detalle";
        }
        return "redirect:/pacientes";
    }
 // ==========================================
 // REPORTE DEL MÉDICO (Personal)
 // ==========================================
 @GetMapping("/reporte")
 public String reporteMedico(Model model, HttpSession session) {
     String email = (String) session.getAttribute("loggedInEmail");
     if (email == null || !email.endsWith("@medico.com")) {
         return "redirect:/login?error=Acceso denegado";
     }
     
     Optional<Doctor> doctorOpt = doctorService.findByEmail(email);
     if (doctorOpt.isEmpty()) {
         return "redirect:/doctor/dashboard";
     }
     
     Doctor doctor = doctorOpt.get();
     Long doctorId = doctor.getId();
     
     // Estadísticas del médico
     long totalCitasAtendidas = reservaService.countCitasAtendidasPorDoctor(doctorId);
     long pacientesUnicos = reservaService.findPacientesAtendidosPorDoctor(doctorId).size();
     long citasPendientes = reservaService.countCitasPendientesPorDoctor(doctorId);
     
     model.addAttribute("nombreMedico", doctor.getFirstName() + " " + doctor.getLastName());
     model.addAttribute("especialidad", doctor.getAreaMedica());
     model.addAttribute("totalCitasAtendidas", totalCitasAtendidas);
     model.addAttribute("pacientesUnicos", pacientesUnicos);
     model.addAttribute("citasPendientes", citasPendientes);
     
     return "doctor-reporte";
 }

 // ==========================================
 // REPORTE DEL ADMINISTRADOR (Global)
 // ==========================================
 @GetMapping("/admin/reporte")
 public String reporteAdmin(Model model, HttpSession session) {
     if (!isAdmin(session)) {
         return "redirect:/login?error=Acceso denegado";
     }
     
     // Estadísticas globales
     long totalCitas = reservaService.count();
     long totalMedicos = doctorService.count();
     long totalPacientes = patientService.count();
     long citasAtendidas = reservaService.countByStatus("ATENDIDA");
     
     // Estadísticas por médico
     List<EstadisticaMedico> estadisticasMedicos = new ArrayList<>();
     List<Doctor> doctors = doctorService.findAll();
     
     for (Doctor doctor : doctors) {
         long citasAtendidasMedico = reservaService.countCitasAtendidasPorDoctor(doctor.getId());
         long pacientesUnicosMedico = reservaService.findPacientesAtendidosPorDoctor(doctor.getId()).size();
         
         EstadisticaMedico stat = new EstadisticaMedico();
         stat.setNombreMedico(doctor.getFirstName() + " " + doctor.getLastName());
         stat.setEspecialidad(doctor.getAreaMedica());
         stat.setCitasAtendidas(citasAtendidasMedico);
         stat.setPacientesUnicos(pacientesUnicosMedico);
         
         estadisticasMedicos.add(stat);
     }
     
     model.addAttribute("totalCitas", totalCitas);
     model.addAttribute("totalMedicos", totalMedicos);
     model.addAttribute("totalPacientes", totalPacientes);
     model.addAttribute("citasAtendidas", citasAtendidas);
     model.addAttribute("estadisticasMedicos", estadisticasMedicos);
     
     return "admin-reporte";
 }


}
