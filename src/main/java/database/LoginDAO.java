package database;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class LoginDAO {

    private static final Logger logger = LoggerFactory.getLogger(LoginDAO.class);

    public static boolean login(
            String username,
            String password) {

        String query = "SELECT * FROM users WHERE username=? AND password=?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(query)) {

            pst.setString(1, username);
            pst.setString(2, password);

            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    System.out.println("Login Successful");
                    logger.info("User logged in successfully: {}", username);
                    return true;
                } else {
                    System.out.println("Invalid Username or Password");
                    logger.warn("Failed login attempt for user: {}", username);
                    return false;
                }
            }

        } catch (Exception e) {
            logger.error("Error during user login: ", e);
            return false;
        }
    }
}