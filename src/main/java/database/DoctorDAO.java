package database;

import models.Doctor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class DoctorDAO {

    private static final Logger logger = LoggerFactory.getLogger(DoctorDAO.class);

    // INSERT DOCTOR
    public static void insertDoctor(
            String name,
            String specialization,
            String phone,
            String roomNo) {

        String query = "INSERT INTO doctors(name, specialization, phone, room_no) VALUES (?, ?, ?, ?)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(query)) {

            pst.setString(1, name);
            pst.setString(2, specialization);
            pst.setString(3, phone);
            pst.setString(4, roomNo);

            pst.executeUpdate();
            logger.info("Doctor Added Successfully: {}", name);

        } catch (Exception e) {
            logger.error("Error inserting doctor: ", e);
        }
    }

    // GET ALL DOCTORS
    public static List<Doctor> getAllDoctors() {
        List<Doctor> doctors = new ArrayList<>();
        String query = "SELECT * FROM doctors";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(query);
             ResultSet rs = pst.executeQuery()) {

            while (rs.next()) {
                Doctor doctor = new Doctor(
                        rs.getInt("doctor_id"),
                        rs.getString("name"),
                        rs.getString("specialization"),
                        rs.getString("phone"),
                        rs.getString("room_no")
                );
                doctors.add(doctor);
            }

        } catch (Exception e) {
            logger.error("Error fetching all doctors: ", e);
        }
        return doctors;
    }

    // VIEW DOCTORS (Console printing)
    public static void viewDoctors() {
        List<Doctor> doctors = getAllDoctors();
        for (Doctor d : doctors) {
            System.out.println(
                    d.getDoctorId() + " | " +
                    d.getName() + " | " +
                    d.getSpecialization() + " | " +
                    d.getPhone() + " | " +
                    d.getRoomNo());
        }
    }

    // GET DOCTOR BY ID
    public static Doctor getDoctorById(int id) {
        String query = "SELECT * FROM doctors WHERE doctor_id=?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(query)) {

            pst.setInt(1, id);

            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return new Doctor(
                            rs.getInt("doctor_id"),
                            rs.getString("name"),
                            rs.getString("specialization"),
                            rs.getString("phone"),
                            rs.getString("room_no")
                    );
                }
            }

        } catch (Exception e) {
            logger.error("Error searching doctor by ID: ", e);
        }
        return null;
    }

    // SEARCH DOCTOR (Console printing)
    public static void searchDoctor(int id) {
        Doctor d = getDoctorById(id);
        if (d != null) {
            System.out.println(
                    d.getDoctorId() + " | " +
                    d.getName() + " | " +
                    d.getSpecialization() + " | " +
                    d.getPhone());
        } else {
            logger.warn("Doctor with ID {} not found", id);
        }
    }

    // UPDATE DOCTOR ROOM
    public static void updateDoctorRoom(int id, String roomNo) {
        String query = "UPDATE doctors SET room_no=? WHERE doctor_id=?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(query)) {

            pst.setString(1, roomNo);
            pst.setInt(2, id);

            int updated = pst.executeUpdate();
            if (updated > 0) {
                logger.info("Doctor ID {} room updated successfully to: {}", id, roomNo);
            } else {
                logger.warn("No doctor found with ID {} to update.", id);
            }

        } catch (Exception e) {
            logger.error("Error updating doctor room: ", e);
        }
    }

    // DELETE DOCTOR
    public static void deleteDoctor(int id) {
        String query = "DELETE FROM doctors WHERE doctor_id=?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(query)) {

            pst.setInt(1, id);

            int deleted = pst.executeUpdate();
            if (deleted > 0) {
                logger.info("Doctor ID {} deleted successfully.", id);
            } else {
                logger.warn("No doctor found with ID {} to delete.", id);
            }

        } catch (Exception e) {
            logger.error("Error deleting doctor: ", e);
        }
    }
}