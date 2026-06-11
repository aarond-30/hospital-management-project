package database;

import com.lowagie.text.Document;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class PDFReportDAO {

    private static final Logger logger = LoggerFactory.getLogger(PDFReportDAO.class);

    public static String generatePatientPDF(int patientId) {
        String query = "SELECT * FROM patients WHERE patient_id=?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(query)) {

            pst.setInt(1, patientId);

            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    String fileName = "PatientReport_" + patientId + ".pdf";
                    Document document = new Document();

                    try (FileOutputStream fos = new FileOutputStream(fileName)) {
                        PdfWriter.getInstance(document, fos);
                        document.open();

                        document.add(new Paragraph("HOSPITAL MANAGEMENT SYSTEM"));
                        document.add(new Paragraph(" "));
                        document.add(new Paragraph("PATIENT REPORT"));
                        document.add(new Paragraph("--------------------------------"));
                        document.add(new Paragraph("Patient ID : " + rs.getInt("patient_id")));
                        document.add(new Paragraph("Name : " + rs.getString("name")));
                        document.add(new Paragraph("Age : " + rs.getInt("age")));
                        document.add(new Paragraph("Gender : " + rs.getString("gender")));
                        document.add(new Paragraph("Phone : " + rs.getString("phone")));
                        document.add(new Paragraph("Disease : " + rs.getString("disease")));
                        document.add(new Paragraph("Address : " + rs.getString("address")));

                        document.close();
                    }

                    System.out.println("PDF Generated Successfully");
                    logger.info("PDF report generated successfully for Patient ID: {}", patientId);
                    return fileName;

                } else {
                    System.out.println("Patient Not Found");
                    logger.warn("Could not generate PDF; Patient ID {} not found", patientId);
                    return null;
                }
            }

        } catch (Exception e) {
            logger.error("Error generating PDF patient report: ", e);
            return null;
        }
    }
}