package database;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class RoomDAO {

    private static final Logger logger = LoggerFactory.getLogger(RoomDAO.class);

    public static void ensureRoomsTableExists() {
        String query = "CREATE TABLE IF NOT EXISTS rooms (" +
                "room_number VARCHAR(10) PRIMARY KEY, " +
                "room_type VARCHAR(50) NOT NULL, " +
                "daily_rate DOUBLE DEFAULT 0.0, " +
                "status VARCHAR(20) DEFAULT 'Vacant', " +
                "occupied_by_patient_id INT DEFAULT NULL, " +
                "admitted_date VARCHAR(20) DEFAULT NULL" +
                ")";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(query)) {
            pst.executeUpdate();
            logger.info("Checked/created rooms table successfully.");

            // Populate default rooms if table is empty
            String checkEmpty = "SELECT COUNT(*) FROM rooms";
            try (PreparedStatement checkPst = con.prepareStatement(checkEmpty);
                 ResultSet rs = checkPst.executeQuery()) {
                if (rs.next() && rs.getInt(1) == 0) {
                    insertRoom("101", "ICU", 2500.00);
                    insertRoom("102", "Private Suite", 1500.00);
                    insertRoom("103", "Semi-Private", 1000.00);
                    insertRoom("104", "General Ward A", 500.00);
                    insertRoom("105", "General Ward B", 500.00);
                    insertRoom("201", "ICU", 2500.00);
                    insertRoom("202", "Private Suite", 1500.00);
                    insertRoom("203", "Semi-Private", 1000.00);
                    insertRoom("204", "General Ward C", 500.00);
                    logger.info("Default rooms populated.");
                }
            }
        } catch (Exception e) {
            logger.error("Error ensuring rooms table exists: ", e);
        }
    }

    private static void insertRoom(String number, String type, double rate) {
        String query = "INSERT INTO rooms (room_number, room_type, daily_rate, status) VALUES (?, ?, ?, 'Vacant')";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(query)) {
            pst.setString(1, number);
            pst.setString(2, type);
            pst.setDouble(3, rate);
            pst.executeUpdate();
        } catch (Exception e) {
            logger.error("Error inserting room default: ", e);
        }
    }

    public static List<String[]> getAllRooms() {
        ensureRoomsTableExists();
        List<String[]> list = new ArrayList<>();
        String query = "SELECT r.*, p.name AS patient_name " +
                "FROM rooms r " +
                "LEFT JOIN patients p ON r.occupied_by_patient_id = p.patient_id " +
                "ORDER BY r.room_number ASC";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(query);
             ResultSet rs = pst.executeQuery()) {

            while (rs.next()) {
                list.add(new String[]{
                        rs.getString("room_number"),
                        rs.getString("room_type"),
                        String.format("%.2f", rs.getDouble("daily_rate")),
                        rs.getString("status"),
                        rs.getString("occupied_by_patient_id") == null ? "" : String.valueOf(rs.getInt("occupied_by_patient_id")),
                        rs.getString("patient_name") == null ? "" : rs.getString("patient_name"),
                        rs.getString("admitted_date") == null ? "" : rs.getString("admitted_date")
                });
            }
        } catch (Exception e) {
            logger.error("Error loading rooms: ", e);
        }
        return list;
    }

    public static boolean admitPatient(String roomNumber, int patientId, String date) {
        ensureRoomsTableExists();
        String query = "UPDATE rooms SET status='Occupied', occupied_by_patient_id=?, admitted_date=? WHERE room_number=? AND status='Vacant'";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(query)) {
            pst.setInt(1, patientId);
            pst.setString(2, date);
            pst.setString(3, roomNumber);

            int result = pst.executeUpdate();
            return result > 0;
        } catch (Exception e) {
            logger.error("Error admitting patient to room: ", e);
            return false;
        }
    }

    public static String[] dischargePatient(String roomNumber) {
        ensureRoomsTableExists();
        String fetchQuery = "SELECT r.*, p.name AS patient_name FROM rooms r " +
                "LEFT JOIN patients p ON r.occupied_by_patient_id = p.patient_id " +
                "WHERE r.room_number=?";
        
        String updateQuery = "UPDATE rooms SET status='Vacant', occupied_by_patient_id=NULL, admitted_date=NULL WHERE room_number=?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement fetchPst = con.prepareStatement(fetchQuery);
             PreparedStatement updatePst = con.prepareStatement(updateQuery)) {

            fetchPst.setString(1, roomNumber);
            String[] details = null;

            try (ResultSet rs = fetchPst.executeQuery()) {
                if (rs.next() && "Occupied".equalsIgnoreCase(rs.getString("status"))) {
                    int patientId = rs.getInt("occupied_by_patient_id");
                    String patName = rs.getString("patient_name");
                    double rate = rs.getDouble("daily_rate");
                    String admittedDate = rs.getString("admitted_date");

                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                    String currentDate = df.format(new Date());

                    int days = calculateDays(admittedDate, currentDate);
                    double totalCharges = days * rate;

                    details = new String[]{
                            String.valueOf(patientId),
                            patName,
                            roomNumber,
                            String.format("%.2f", rate),
                            String.valueOf(days),
                            String.format("%.2f", totalCharges)
                    };
                }
            }

            if (details != null) {
                updatePst.setString(1, roomNumber);
                updatePst.executeUpdate();
            }

            return details;
        } catch (Exception e) {
            logger.error("Error discharging patient: ", e);
            return null;
        }
    }

    private static int calculateDays(String start, String end) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date startDate = sdf.parse(start);
            Date endDate = sdf.parse(end);
            long diff = endDate.getTime() - startDate.getTime();
            int days = (int) (diff / (1000 * 60 * 60 * 24));
            return Math.max(1, days); // Minimum 1 day charge
        } catch (Exception e) {
            return 1;
        }
    }
}
