package database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class DoctorDAO {

    // INSERT DOCTOR
    public static void insertDoctor(
            String name,
            String specialization,
            String phone,
            String roomNo) {

        try {

            Connection con = DBConnection.getConnection();

            String query = "INSERT INTO doctors(name, specialization, phone, room_no) VALUES (?, ?, ?, ?)";

            PreparedStatement pst = con.prepareStatement(query);

            pst.setString(1, name);
            pst.setString(2, specialization);
            pst.setString(3, phone);
            pst.setString(4, roomNo);

            pst.executeUpdate();

            System.out.println("Doctor Added Successfully");

        } catch (Exception e) {
            System.out.println(e);
        }
    }

    // VIEW DOCTORS
    public static void viewDoctors() {

        try {

            Connection con = DBConnection.getConnection();

            String query = "SELECT * FROM doctors";

            PreparedStatement pst = con.prepareStatement(query);

            ResultSet rs = pst.executeQuery();

            while (rs.next()) {

                System.out.println(
                        rs.getInt("doctor_id") + " | " +
                                rs.getString("name") + " | " +
                                rs.getString("specialization") + " | " +
                                rs.getString("phone") + " | " +
                                rs.getString("room_no"));
            }

        } catch (Exception e) {
            System.out.println(e);
        }
    }

    // SEARCH DOCTOR
    public static void searchDoctor(int id) {

        try {

            Connection con = DBConnection.getConnection();

            String query = "SELECT * FROM doctors WHERE doctor_id=?";

            PreparedStatement pst = con.prepareStatement(query);

            pst.setInt(1, id);

            ResultSet rs = pst.executeQuery();

            while (rs.next()) {

                System.out.println(
                        rs.getInt("doctor_id") + " | " +
                                rs.getString("name") + " | " +
                                rs.getString("specialization") + " | " +
                                rs.getString("phone"));
            }

        } catch (Exception e) {
            System.out.println(e);
        }
    }

    // UPDATE DOCTOR ROOM
    public static void updateDoctorRoom(
            int id,
            String roomNo) {

        try {

            Connection con = DBConnection.getConnection();

            String query = "UPDATE doctors SET room_no=? WHERE doctor_id=?";

            PreparedStatement pst = con.prepareStatement(query);

            pst.setString(1, roomNo);
            pst.setInt(2, id);

            pst.executeUpdate();

            System.out.println("Doctor Updated Successfully");

        } catch (Exception e) {
            System.out.println(e);
        }
    }

    // DELETE DOCTOR
    public static void deleteDoctor(int id) {

        try {

            Connection con = DBConnection.getConnection();

            String query = "DELETE FROM doctors WHERE doctor_id=?";

            PreparedStatement pst = con.prepareStatement(query);

            pst.setInt(1, id);

            pst.executeUpdate();

            System.out.println("Doctor Deleted Successfully");

        } catch (Exception e) {
            System.out.println(e);
        }
    }
}