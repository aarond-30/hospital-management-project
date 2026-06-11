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

public class AuditDAO {

    private static final Logger logger = LoggerFactory.getLogger(AuditDAO.class);

    public static void ensureAuditTableExists() {
        String query = "CREATE TABLE IF NOT EXISTS audit_logs (" +
                "log_id INT AUTO_INCREMENT PRIMARY KEY, " +
                "log_time VARCHAR(25) NOT NULL, " +
                "user_name VARCHAR(50) NOT NULL, " +
                "action_details VARCHAR(255) NOT NULL" +
                ")";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(query)) {
            pst.executeUpdate();
        } catch (Exception e) {
            logger.error("Error creating audit table: ", e);
        }
    }

    public static void log(String username, String action) {
        ensureAuditTableExists();
        String query = "INSERT INTO audit_logs (log_time, user_name, action_details) VALUES (?, ?, ?)";
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String timeStr = df.format(new Date());

        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(query)) {
            pst.setString(1, timeStr);
            pst.setString(2, (username == null || username.isEmpty()) ? "SYSTEM" : username);
            pst.setString(3, action);
            pst.executeUpdate();
            logger.info("Audit Logged: {} | {} by {}", timeStr, action, username);
        } catch (Exception e) {
            logger.error("Error writing audit log: ", e);
        }
    }

    public static List<String[]> getAllLogs() {
        ensureAuditTableExists();
        List<String[]> list = new ArrayList<>();
        String query = "SELECT log_id, log_time, user_name, action_details FROM audit_logs ORDER BY log_id DESC";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(query);
             ResultSet rs = pst.executeQuery()) {

            while (rs.next()) {
                list.add(new String[]{
                        String.valueOf(rs.getInt("log_id")),
                        rs.getString("log_time"),
                        rs.getString("user_name"),
                        rs.getString("action_details")
                });
            }
        } catch (Exception e) {
            logger.error("Error retrieving audit logs: ", e);
        }
        return list;
    }
}
