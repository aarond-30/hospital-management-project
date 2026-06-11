package database;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class ReportDAO {

    private static final Logger logger = LoggerFactory.getLogger(ReportDAO.class);

    public static void generatePatientReport(int patientId) {
        String query = "SELECT * FROM patients WHERE patient_id=?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(query)) {

            pst.setInt(1, patientId);

            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    String fileName = "PatientReport_" + patientId + ".txt";
                    File file = new File(fileName);

                    try (FileWriter writer = new FileWriter(file)) {
                        writer.write("=================================\n");
                        writer.write("       PATIENT REPORT\n");
                        writer.write("=================================\n\n");
                        writer.write("Patient ID : " + rs.getInt("patient_id") + "\n");
                        writer.write("Name : " + rs.getString("name") + "\n");
                        writer.write("Age : " + rs.getInt("age") + "\n");
                        writer.write("Gender : " + rs.getString("gender") + "\n");
                        writer.write("Phone : " + rs.getString("phone") + "\n");
                        writer.write("Disease : " + rs.getString("disease") + "\n");
                        writer.write("Address : " + rs.getString("address") + "\n");
                    }

                    System.out.println("Report Generated Successfully");
                    System.out.println("Saved At: " + file.getAbsolutePath());
                    logger.info("Text report generated for Patient ID: {} at {}", patientId, file.getAbsolutePath());

                } else {
                    System.out.println("Patient ID Not Found");
                    logger.warn("Could not generate report; Patient ID {} not found", patientId);
                }
            }

        } catch (Exception e) {
            logger.error("Error generating text patient report: ", e);
        }
    }
}