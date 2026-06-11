package database;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class DashboardDAO {

    private static final Logger logger = LoggerFactory.getLogger(DashboardDAO.class);

    public static int getPatientCount() {
        String query = "SELECT COUNT(*) FROM patients";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(query);
             ResultSet rs = pst.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (Exception e) {
            logger.error("Error getting patient count: ", e);
        }
        return 0;
    }

    public static int getDoctorCount() {
        String query = "SELECT COUNT(*) FROM doctors";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(query);
             ResultSet rs = pst.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (Exception e) {
            logger.error("Error getting doctor count: ", e);
        }
        return 0;
    }

    public static int getAppointmentCount() {
        String query = "SELECT COUNT(*) FROM appointments";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(query);
             ResultSet rs = pst.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (Exception e) {
            logger.error("Error getting appointment count: ", e);
        }
        return 0;
    }
}