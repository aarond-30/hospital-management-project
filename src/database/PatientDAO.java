package database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class PatientDAO {

    // INSERT PATIENT
    public static void insertPatient(
            String name,
            int age,
            String gender,
            String phone,
            String disease,
            String address) {

        try {

            Connection con = DBConnection.getConnection();

            String query = "INSERT INTO patients(name, age, gender, phone, disease, address) VALUES (?, ?, ?, ?, ?, ?)";

            PreparedStatement pst = con.prepareStatement(query);

            pst.setString(1, name);
            pst.setInt(2, age);
            pst.setString(3, gender);
            pst.setString(4, phone);
            pst.setString(5, disease);
            pst.setString(6, address);

            pst.executeUpdate();

            System.out.println("Patient Added Successfully");

        } catch (Exception e) {
            System.out.println(e);
        }
    }

    // VIEW PATIENTS
    public static void viewPatients() {

        try {

            Connection con = DBConnection.getConnection();

            String query = "SELECT * FROM patients";

            PreparedStatement pst = con.prepareStatement(query);

            ResultSet rs = pst.executeQuery();

            while (rs.next()) {

                System.out.println(
                        rs.getInt("patient_id") + " | " +
                                rs.getString("name") + " | " +
                                rs.getInt("age") + " | " +
                                rs.getString("gender") + " | " +
                                rs.getString("phone") + " | " +
                                rs.getString("disease") + " | " +
                                rs.getString("address"));
            }

        } catch (Exception e) {
            System.out.println(e);
        }
    }

    // SEARCH PATIENT
    public static void searchPatient(int id) {

        try {

            Connection con = DBConnection.getConnection();

            String query = "SELECT * FROM patients WHERE patient_id=?";

            PreparedStatement pst = con.prepareStatement(query);

            pst.setInt(1, id);

            ResultSet rs = pst.executeQuery();

            while (rs.next()) {

                System.out.println(
                        rs.getInt("patient_id") + " | " +
                                rs.getString("name") + " | " +
                                rs.getInt("age") + " | " +
                                rs.getString("gender"));
            }

        } catch (Exception e) {
            System.out.println(e);
        }
    }

    // UPDATE PATIENT
    public static void updatePatientDisease(
            int id,
            String disease) {

        try {

            Connection con = DBConnection.getConnection();

            String query = "UPDATE patients SET disease=? WHERE patient_id=?";

            PreparedStatement pst = con.prepareStatement(query);

            pst.setString(1, disease);
            pst.setInt(2, id);

            pst.executeUpdate();

            System.out.println("Patient Updated Successfully");

        } catch (Exception e) {
            System.out.println(e);
        }
    }

    // DELETE PATIENT
    public static void deletePatient(int id) {

        try {

            Connection con = DBConnection.getConnection();

            String query = "DELETE FROM patients WHERE patient_id=?";

            PreparedStatement pst = con.prepareStatement(query);

            pst.setInt(1, id);

            pst.executeUpdate();

            System.out.println("Patient Deleted Successfully");

        } catch (Exception e) {
            System.out.println(e);
        }
    }

    // TOTAL PATIENT COUNT
    public static void patientCount() {

        try {

            Connection con = DBConnection.getConnection();

            String query = "SELECT COUNT(*) AS total FROM patients";

            PreparedStatement pst = con.prepareStatement(query);

            ResultSet rs = pst.executeQuery();

            while (rs.next()) {

                System.out.println(
                        "Total Patients: " +
                                rs.getInt("total"));
            }

        } catch (Exception e) {
            System.out.println(e);
        }
    }
}