package database;

import models.Patient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class PatientDAO {

    private static final Logger logger = LoggerFactory.getLogger(PatientDAO.class);

    public static void ensureVisitedColumnExists() {
        String checkQuery = "SHOW COLUMNS FROM patients LIKE 'visited'";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement checkPst = con.prepareStatement(checkQuery);
             ResultSet rs = checkPst.executeQuery()) {

            if (!rs.next()) {
                String alterQuery = "ALTER TABLE patients ADD COLUMN visited VARCHAR(3) DEFAULT 'NO'";
                try (PreparedStatement alterPst = con.prepareStatement(alterQuery)) {
                    alterPst.executeUpdate();
                    logger.info("Altered patients table to add visited column.");
                }
            }
        } catch (Exception e) {
            logger.error("Error ensuring visited column exists: ", e);
        }
    }

    // INSERT PATIENT
    public static void insertPatient(
            String name,
            int age,
            String gender,
            String phone,
            String disease,
            String address) {

        ensureVisitedColumnExists();
        String query = "INSERT INTO patients(name, age, gender, phone, disease, address, visited) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(query)) {

            pst.setString(1, name);
            pst.setInt(2, age);
            pst.setString(3, gender);
            pst.setString(4, phone);
            pst.setString(5, disease);
            pst.setString(6, address);
            pst.setString(7, "NO");

            pst.executeUpdate();
            logger.info("Patient Added Successfully: {}", name);

        } catch (Exception e) {
            logger.error("Error inserting patient: ", e);
        }
    }

    // GET ALL PATIENTS
    public static List<Patient> getAllPatients() {
        ensureVisitedColumnExists();
        List<Patient> patients = new ArrayList<>();
        String query = "SELECT * FROM patients";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(query);
             ResultSet rs = pst.executeQuery()) {

            while (rs.next()) {
                Patient patient = new Patient(
                        rs.getInt("patient_id"),
                        rs.getString("name"),
                        rs.getInt("age"),
                        rs.getString("gender"),
                        rs.getString("phone"),
                        rs.getString("disease"),
                        rs.getString("address"),
                        rs.getString("visited")
                );
                patients.add(patient);
            }

        } catch (Exception e) {
            logger.error("Error fetching all patients: ", e);
        }
        return patients;
    }

    // VIEW PATIENTS (Console printing)
    public static void viewPatients() {
        List<Patient> patients = getAllPatients();
        for (Patient p : patients) {
            System.out.println(
                    p.getPatientId() + " | " +
                    p.getName() + " | " +
                    p.getAge() + " | " +
                    p.getGender() + " | " +
                    p.getPhone() + " | " +
                    p.getDisease() + " | " +
                    p.getAddress());
        }
    }

    // GET PATIENT BY ID
    public static Patient getPatientById(int id) {
        ensureVisitedColumnExists();
        String query = "SELECT * FROM patients WHERE patient_id=?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(query)) {

            pst.setInt(1, id);

            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return new Patient(
                            rs.getInt("patient_id"),
                            rs.getString("name"),
                            rs.getInt("age"),
                            rs.getString("gender"),
                            rs.getString("phone"),
                            rs.getString("disease"),
                            rs.getString("address"),
                            rs.getString("visited")
                    );
                }
            }

        } catch (Exception e) {
            logger.error("Error searching patient by ID: ", e);
        }
        return null;
    }

    // SEARCH PATIENT (Console printing)
    public static void searchPatient(int id) {
        Patient p = getPatientById(id);
        if (p != null) {
            System.out.println(
                    p.getPatientId() + " | " +
                    p.getName() + " | " +
                    p.getAge() + " | " +
                    p.getGender());
        } else {
            logger.warn("Patient with ID {} not found", id);
        }
    }

    // UPDATE PATIENT
    public static void updatePatientDisease(int id, String disease) {
        String query = "UPDATE patients SET disease=? WHERE patient_id=?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(query)) {

            pst.setString(1, disease);
            pst.setInt(2, id);

            int updated = pst.executeUpdate();
            if (updated > 0) {
                logger.info("Patient ID {} updated successfully with disease: {}", id, disease);
            } else {
                logger.warn("No patient found with ID {} to update.", id);
            }

        } catch (Exception e) {
            logger.error("Error updating patient disease: ", e);
        }
    }

    // UPDATE ALL PATIENT DETAILS
    public static boolean updatePatient(Patient p) {
        String query = "UPDATE patients SET name=?, age=?, gender=?, phone=?, disease=?, address=? WHERE patient_id=?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(query)) {

            pst.setString(1, p.getName());
            pst.setInt(2, p.getAge());
            pst.setString(3, p.getGender());
            pst.setString(4, p.getPhone());
            pst.setString(5, p.getDisease());
            pst.setString(6, p.getAddress());
            pst.setInt(7, p.getPatientId());

            int updated = pst.executeUpdate();
            if (updated > 0) {
                logger.info("Patient ID {} details updated successfully.", p.getPatientId());
                return true;
            }

        } catch (Exception e) {
            logger.error("Error updating patient details: ", e);
        }
        return false;
    }

    // DELETE PATIENT
    public static void deletePatient(int id) {
        String query = "DELETE FROM patients WHERE patient_id=?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(query)) {

            pst.setInt(1, id);

            int deleted = pst.executeUpdate();
            if (deleted > 0) {
                logger.info("Patient ID {} deleted successfully.", id);
            } else {
                logger.warn("No patient found with ID {} to delete.", id);
            }

        } catch (Exception e) {
            logger.error("Error deleting patient: ", e);
        }
    }

    // TOTAL PATIENT COUNT
    public static void patientCount() {
        String query = "SELECT COUNT(*) AS total FROM patients";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(query);
             ResultSet rs = pst.executeQuery()) {

            if (rs.next()) {
                System.out.println("Total Patients: " + rs.getInt("total"));
            }

        } catch (Exception e) {
            logger.error("Error counting patients: ", e);
        }
    }

    // UPDATE VISIT STATUS
    public static void updateVisitStatus(int patientId, String status) {
        ensureVisitedColumnExists();
        String query = "UPDATE patients SET visited=? WHERE patient_id=?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(query)) {

            pst.setString(1, status);
            pst.setInt(2, patientId);

            int updated = pst.executeUpdate();
            if (updated > 0) {
                logger.info("Patient ID {} visit status updated to {}.", patientId, status);
            }

        } catch (Exception e) {
            logger.error("Error updating patient visit status: ", e);
        }
    }
}