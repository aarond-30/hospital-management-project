package database;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {

    private static final Logger logger = LoggerFactory.getLogger(UserDAO.class);

    public static void ensureUsersTableExists() {
        String createTableQuery = "CREATE TABLE IF NOT EXISTS users (" +
                "user_id INT AUTO_INCREMENT PRIMARY KEY, " +
                "username VARCHAR(50) UNIQUE NOT NULL, " +
                "password VARCHAR(100) NOT NULL, " +
                "role VARCHAR(20) DEFAULT 'Staff'" +
                ")";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(createTableQuery)) {
            pst.executeUpdate();
            logger.info("Checked/created users table successfully.");

            // Populate default accounts if table is empty
            String checkEmpty = "SELECT COUNT(*) FROM users";
            try (PreparedStatement checkPst = con.prepareStatement(checkEmpty);
                 ResultSet rs = checkPst.executeQuery()) {
                if (rs.next() && rs.getInt(1) == 0) {
                    insertUser("admin", "admin123", "Admin");
                    insertUser("staff", "staff123", "Staff");
                    logger.info("Inserted default administrator and staff accounts.");
                }
            }
        } catch (Exception e) {
            logger.error("Error ensuring users table exists: ", e);
        }
    }

    public static boolean insertUser(String username, String password, String role) {
        ensureUsersTableExists();
        String query = "INSERT INTO users (username, password, role) VALUES (?, ?, ?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(query)) {
            pst.setString(1, username);
            pst.setString(2, password);
            pst.setString(3, role);

            int result = pst.executeUpdate();
            return result > 0;
        } catch (Exception e) {
            logger.error("Error inserting user: ", e);
            return false;
        }
    }

    public static boolean deleteUser(int userId) {
        ensureUsersTableExists();
        String query = "DELETE FROM users WHERE user_id=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(query)) {
            pst.setInt(1, userId);

            int result = pst.executeUpdate();
            return result > 0;
        } catch (Exception e) {
            logger.error("Error deleting user: ", e);
            return false;
        }
    }

    public static List<String[]> getAllUsers() {
        ensureUsersTableExists();
        List<String[]> list = new ArrayList<>();
        String query = "SELECT user_id, username, role FROM users ORDER BY user_id ASC";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(query);
             ResultSet rs = pst.executeQuery()) {

            while (rs.next()) {
                list.add(new String[]{
                        String.valueOf(rs.getInt("user_id")),
                        rs.getString("username"),
                        rs.getString("role")
                });
            }
        } catch (Exception e) {
            logger.error("Error retrieving users: ", e);
        }
        return list;
    }
}
