package database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class LoginDAO {

    public static void login(
            String username,
            String password) {

        try {

            Connection con = DBConnection.getConnection();

            String query = "SELECT * FROM users WHERE username=? AND password=?";

            PreparedStatement pst = con.prepareStatement(query);

            pst.setString(1, username);
            pst.setString(2, password);

            ResultSet rs = pst.executeQuery();

            if (rs.next()) {

                System.out.println("Login Successful");

            } else {

                System.out.println("Invalid Username or Password");
            }

        } catch (Exception e) {
            System.out.println(e);
        }
    }
}