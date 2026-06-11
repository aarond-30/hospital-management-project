package database;

import models.Appointment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class AppointmentDAO {

    private static final Logger logger = LoggerFactory.getLogger(AppointmentDAO.class);

    public static void ensureStatusColumnExists() {
        String checkQuery = "SHOW COLUMNS FROM appointments LIKE 'status'";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement checkPst = con.prepareStatement(checkQuery);
             ResultSet rs = checkPst.executeQuery()) {

            if (!rs.next()) {
                String alterQuery = "ALTER TABLE appointments ADD COLUMN status VARCHAR(20) DEFAULT 'Pending'";
                try (PreparedStatement alterPst = con.prepareStatement(alterQuery)) {
                    alterPst.executeUpdate();
                    logger.info("Altered appointments table to add status column.");
                }
            }
        } catch (Exception e) {
            logger.error("Error ensuring status column exists: ", e);
        }
    }

    // CHECK DOCTOR AVAILABILITY
    public static boolean isDoctorAvailable(int doctorId, String date, String time) {
        String query = "SELECT * FROM appointments WHERE doctor_id=? AND appointment_date=? AND appointment_time=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(query)) {
            pst.setInt(1, doctorId);
            pst.setString(2, date);
            pst.setString(3, time);
            try (ResultSet rs = pst.executeQuery()) {
                return !rs.next(); // Returns true if doctor is free
            }
        } catch (Exception e) {
            logger.error("Error checking doctor availability: ", e);
            return false;
        }
    }

    // BOOK APPOINTMENT
    public static boolean bookAppointment(
            int patientId,
            int doctorId,
            String date,
            String time) {

        ensureStatusColumnExists();
        String checkQuery = "SELECT * FROM appointments WHERE doctor_id=? AND appointment_date=? AND appointment_time=?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement checkPst = con.prepareStatement(checkQuery)) {

            checkPst.setInt(1, doctorId);
            checkPst.setString(2, date);
            checkPst.setString(3, time);

            try (ResultSet rs = checkPst.executeQuery()) {
                if (rs.next()) {
                    logger.warn("Doctor with ID {} is already booked at {} on {}", doctorId, time, date);
                    return false;
                }
            }

            String query = "INSERT INTO appointments(patient_id, doctor_id, appointment_date, appointment_time, status) VALUES (?, ?, ?, ?, 'Pending')";
            try (PreparedStatement pst = con.prepareStatement(query)) {
                pst.setInt(1, patientId);
                pst.setInt(2, doctorId);
                pst.setString(3, date);
                pst.setString(4, time);

                pst.executeUpdate();
                logger.info("Appointment Booked Successfully for Patient ID: {} with Doctor ID: {}", patientId, doctorId);
                return true;
            }

        } catch (Exception e) {
            logger.error("Error booking appointment: ", e);
            return false;
        }
    }

    // GET ALL APPOINTMENTS WITH STATUS MAPPED
    public static List<Appointment> getAllAppointmentsWithStatus() {
        ensureStatusColumnExists();
        List<Appointment> list = new ArrayList<>();
        String query = "SELECT appointment_id, patient_id, doctor_id, appointment_date, appointment_time, " +
                "IFNULL(NULLIF(status, ''), " +
                "CASE " +
                "WHEN appointment_date < CURDATE() THEN 'Completed' " +
                "WHEN appointment_date = CURDATE() THEN 'Today' " +
                "ELSE 'Pending' " +
                "END) AS status " +
                "FROM appointments " +
                "ORDER BY " +
                "CASE " +
                "WHEN IFNULL(NULLIF(status, ''), " +
                "CASE WHEN appointment_date < CURDATE() THEN 'Completed' " +
                "WHEN appointment_date = CURDATE() THEN 'Today' " +
                "ELSE 'Pending' END) = 'Today' THEN 0 " +
                "WHEN IFNULL(NULLIF(status, ''), " +
                "CASE WHEN appointment_date < CURDATE() THEN 'Completed' " +
                "WHEN appointment_date = CURDATE() THEN 'Today' " +
                "ELSE 'Pending' END) = 'Pending' THEN 1 " +
                "ELSE 2 END, " +
                "appointment_date ASC";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(query);
             ResultSet rs = pst.executeQuery()) {

            while (rs.next()) {
                Appointment app = new Appointment(
                        rs.getInt("appointment_id"),
                        rs.getInt("patient_id"),
                        rs.getInt("doctor_id"),
                        rs.getString("appointment_date"),
                        rs.getString("appointment_time"),
                        rs.getString("status")
                );
                list.add(app);
            }

        } catch (Exception e) {
            logger.error("Error fetching appointments with status: ", e);
        }
        return list;
    }

    // GET ALL RAW APPOINTMENTS
    public static List<Appointment> getAllAppointments() {
        ensureStatusColumnExists();
        List<Appointment> list = new ArrayList<>();
        String query = "SELECT * FROM appointments";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(query);
             ResultSet rs = pst.executeQuery()) {

            while (rs.next()) {
                Appointment app = new Appointment(
                        rs.getInt("appointment_id"),
                        rs.getInt("patient_id"),
                        rs.getInt("doctor_id"),
                        rs.getString("appointment_date"),
                        rs.getString("appointment_time"),
                        rs.getString("status")
                );
                list.add(app);
            }

        } catch (Exception e) {
            logger.error("Error fetching all appointments: ", e);
        }
        return list;
    }

    // VIEW APPOINTMENTS (Console printing)
    public static void viewAppointments() {
        List<Appointment> appointments = getAllAppointments();
        for (Appointment app : appointments) {
            System.out.println(
                    app.getAppointmentId() + " | " +
                    app.getPatientId() + " | " +
                    app.getDoctorId() + " | " +
                    app.getAppointmentDate() + " | " +
                    app.getAppointmentTime());
        }
    }

    // GET APPOINTMENT BY ID
    public static Appointment getAppointmentById(int id) {
        ensureStatusColumnExists();
        String query = "SELECT * FROM appointments WHERE appointment_id=?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(query)) {

            pst.setInt(1, id);

            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return new Appointment(
                            rs.getInt("appointment_id"),
                            rs.getInt("patient_id"),
                            rs.getInt("doctor_id"),
                            rs.getString("appointment_date"),
                            rs.getString("appointment_time"),
                            rs.getString("status")
                    );
                }
            }

        } catch (Exception e) {
            logger.error("Error searching appointment by ID: ", e);
        }
        return null;
    }

    // SEARCH APPOINTMENT (Console printing)
    public static void searchAppointment(int id) {
        Appointment app = getAppointmentById(id);
        if (app != null) {
            System.out.println(
                    app.getAppointmentId() + " | " +
                    app.getPatientId() + " | " +
                    app.getDoctorId() + " | " +
                    app.getAppointmentDate() + " | " +
                    app.getAppointmentTime());
        } else {
            logger.warn("Appointment with ID {} not found", id);
        }
    }

    // DELETE APPOINTMENT
    public static void deleteAppointment(int id) {
        String query = "DELETE FROM appointments WHERE appointment_id=?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(query)) {

            pst.setInt(1, id);

            int deleted = pst.executeUpdate();
            if (deleted > 0) {
                logger.info("Appointment Deleted Successfully: ID {}", id);
            } else {
                logger.warn("No appointment found with ID {} to delete.", id);
            }

        } catch (Exception e) {
            logger.error("Error deleting appointment: ", e);
        }
    }

    // MARK COMPLETE
    public static boolean markComplete(int appointmentId) {
        ensureStatusColumnExists();
        String query = "UPDATE appointments SET status='Completed' WHERE appointment_id=?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(query)) {

            pst.setInt(1, appointmentId);

            int updated = pst.executeUpdate();
            if (updated > 0) {
                logger.info("Appointment ID {} marked as Completed.", appointmentId);
                return true;
            }

        } catch (Exception e) {
            logger.error("Error marking appointment complete: ", e);
        }
        return false;
    }
}