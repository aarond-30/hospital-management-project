package database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class AppointmentDAO {

    // BOOK APPOINTMENT
    public static boolean bookAppointment(
            int patientId,
            int doctorId,
            String date,
            String time) {

        try {

            Connection con = DBConnection.getConnection();

            // CHECK IF DOCTOR ALREADY BOOKED
            String checkQuery = "SELECT * FROM appointments WHERE doctor_id=? AND appointment_date=? AND appointment_time=?";

            PreparedStatement checkPst = con.prepareStatement(checkQuery);

            checkPst.setInt(1, doctorId);
            checkPst.setString(2, date);
            checkPst.setString(3, time);

            ResultSet rs = checkPst.executeQuery();

            if (rs.next()) {

                return false;

            } else {

                String query = "INSERT INTO appointments(patient_id, doctor_id, appointment_date, appointment_time) VALUES (?, ?, ?, ?)";

                PreparedStatement pst = con.prepareStatement(query);

                pst.setInt(1, patientId);
                pst.setInt(2, doctorId);
                pst.setString(3, date);
                pst.setString(4, time);

                pst.executeUpdate();

                return true;
            }

        } catch (Exception e) {

            System.out.println(e);

            return false;
        }
    }

    // VIEW APPOINTMENTS
    public static void viewAppointments() {

        try {

            Connection con = DBConnection.getConnection();

            String query = "SELECT * FROM appointments";

            PreparedStatement pst = con.prepareStatement(query);

            ResultSet rs = pst.executeQuery();

            while (rs.next()) {

                System.out.println(
                        rs.getInt("appointment_id") + " | " +
                                rs.getInt("patient_id") + " | " +
                                rs.getInt("doctor_id") + " | " +
                                rs.getString("appointment_date") + " | " +
                                rs.getString("appointment_time"));
            }

        } catch (Exception e) {

            System.out.println(e);
        }
    }

    // SEARCH APPOINTMENT
    public static void searchAppointment(int id) {

        try {

            Connection con = DBConnection.getConnection();

            String query = "SELECT * FROM appointments WHERE appointment_id=?";

            PreparedStatement pst = con.prepareStatement(query);

            pst.setInt(1, id);

            ResultSet rs = pst.executeQuery();

            while (rs.next()) {

                System.out.println(
                        rs.getInt("appointment_id") + " | " +
                                rs.getInt("patient_id") + " | " +
                                rs.getInt("doctor_id") + " | " +
                                rs.getString("appointment_date") + " | " +
                                rs.getString("appointment_time"));
            }

        } catch (Exception e) {

            System.out.println(e);
        }
    }

    // DELETE APPOINTMENT
    public static void deleteAppointment(int id) {

        try {

            Connection con = DBConnection.getConnection();

            String query = "DELETE FROM appointments WHERE appointment_id=?";

            PreparedStatement pst = con.prepareStatement(query);

            pst.setInt(1, id);

            pst.executeUpdate();

            System.out.println(
                    "Appointment Deleted Successfully");

        } catch (Exception e) {

            System.out.println(e);
        }
    }
}