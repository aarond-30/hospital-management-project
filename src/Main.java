import database.PatientDAO;
import database.DoctorDAO;
import database.AppointmentDAO;
import database.LoginDAO;

public class Main {

    public static void main(String[] args) {

        // LOGIN
        LoginDAO.login(
                "admin",
                "admin123");

        // INSERT PATIENT
        PatientDAO.insertPatient(
                "Aaron",
                20,
                "Male",
                "9876543210",
                "Fever",
                "Bangalore");

        // VIEW PATIENTS
        PatientDAO.viewPatients();

        // SEARCH PATIENT
        PatientDAO.searchPatient(1);

        // UPDATE PATIENT
        PatientDAO.updatePatientDisease(
                1,
                "Cold");

        // PATIENT COUNT
        PatientDAO.patientCount();

        // INSERT DOCTOR
        DoctorDAO.insertDoctor(
                "Dr Smith",
                "Cardiologist",
                "9876543210",
                "A101");

        // VIEW DOCTORS
        DoctorDAO.viewDoctors();

        // SEARCH DOCTOR
        DoctorDAO.searchDoctor(1);

        // UPDATE DOCTOR ROOM
        DoctorDAO.updateDoctorRoom(
                1,
                "B202");

        // BOOK APPOINTMENT
        AppointmentDAO.bookAppointment(
                1,
                1,
                "2026-05-26",
                "10:30:00");

        // VIEW APPOINTMENTS
        AppointmentDAO.viewAppointments();

        // SEARCH APPOINTMENT
        AppointmentDAO.searchAppointment(1);

        // DELETE APPOINTMENT
        AppointmentDAO.deleteAppointment(1);

        // DELETE PATIENT
        PatientDAO.deletePatient(2);

        // DELETE DOCTOR
        DoctorDAO.deleteDoctor(2);
    }
}