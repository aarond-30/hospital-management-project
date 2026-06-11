package database;

import com.lowagie.text.Document;
import com.lowagie.text.Font;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class PrescriptionDAO {

    private static final Logger logger = LoggerFactory.getLogger(PrescriptionDAO.class);

    public static void ensurePrescriptionsTableExists() {
        String query = "CREATE TABLE IF NOT EXISTS prescriptions (" +
                "prescription_id INT AUTO_INCREMENT PRIMARY KEY, " +
                "patient_id INT NOT NULL, " +
                "doctor_id INT NOT NULL, " +
                "visit_date VARCHAR(20) NOT NULL, " +
                "symptoms VARCHAR(255) NOT NULL, " +
                "diagnosis VARCHAR(255) NOT NULL, " +
                "medicines TEXT NOT NULL" +
                ")";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(query)) {
            pst.executeUpdate();
            logger.info("Checked/created prescriptions table successfully.");
        } catch (Exception e) {
            logger.error("Error creating prescriptions table: ", e);
        }
    }

    public static boolean insertPrescription(int patientId, int doctorId, String date, String symptoms, String diagnosis, String medicines) {
        ensurePrescriptionsTableExists();
        String query = "INSERT INTO prescriptions (patient_id, doctor_id, visit_date, symptoms, diagnosis, medicines) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(query)) {
            pst.setInt(1, patientId);
            pst.setInt(2, doctorId);
            pst.setString(3, date);
            pst.setString(4, symptoms);
            pst.setString(5, diagnosis);
            pst.setString(6, medicines);

            int result = pst.executeUpdate();
            return result > 0;
        } catch (Exception e) {
            logger.error("Error saving prescription: ", e);
            return false;
        }
    }

    public static List<String[]> getPrescriptionsByPatient(int patientId) {
        ensurePrescriptionsTableExists();
        List<String[]> list = new ArrayList<>();
        String query = "SELECT pr.*, d.name AS doctor_name " +
                "FROM prescriptions pr " +
                "LEFT JOIN doctors d ON pr.doctor_id = d.doctor_id " +
                "WHERE pr.patient_id=? " +
                "ORDER BY pr.prescription_id DESC";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(query)) {
            pst.setInt(1, patientId);
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    list.add(new String[]{
                            String.valueOf(rs.getInt("prescription_id")),
                            rs.getString("doctor_name") == null ? "Doctor ID: " + rs.getInt("doctor_id") : rs.getString("doctor_name"),
                            rs.getString("visit_date"),
                            rs.getString("symptoms"),
                            rs.getString("diagnosis"),
                            rs.getString("medicines")
                    });
                }
            }
        } catch (Exception e) {
            logger.error("Error loading patient prescription history: ", e);
        }
        return list;
    }

    public static String generatePrescriptionPDF(int presId, String patName, String docName, String date, String symptoms, String diagnosis, String medicines) {
        String fileName = "Prescription_Slip_" + presId + ".pdf";
        try {
            Document document = new Document();
            FileOutputStream fos = new FileOutputStream(fileName);
            PdfWriter.getInstance(document, fos);
            document.open();

            Font titleFont = new Font(Font.HELVETICA, 18, Font.BOLD);
            Font sectionFont = new Font(Font.HELVETICA, 12, Font.BOLD);
            Font normalFont = new Font(Font.HELVETICA, 10, Font.NORMAL);

            document.add(new Paragraph("HMS CLINICAL PRESCRIPTION SLIP", titleFont));
            document.add(new Paragraph("-------------------------------------------------------------------------"));
            document.add(new Paragraph("Prescription ID: " + presId, normalFont));
            document.add(new Paragraph("Visit Date:      " + date, normalFont));
            document.add(new Paragraph(" "));

            document.add(new Paragraph("PATIENT & DOCTOR INFORMATION", sectionFont));
            document.add(new Paragraph("Patient Name:    " + patName, normalFont));
            document.add(new Paragraph("Doctor Name:     " + docName, normalFont));
            document.add(new Paragraph(" "));

            document.add(new Paragraph("CLINICAL DIAGNOSIS DETAILS", sectionFont));
            document.add(new Paragraph("Symptoms:        " + symptoms, normalFont));
            document.add(new Paragraph("Diagnosis:       " + diagnosis, normalFont));
            document.add(new Paragraph(" "));

            document.add(new Paragraph("PRESCRIBED MEDICATIONS & DOSAGE", sectionFont));
            document.add(new Paragraph("--------------------------------------------------", normalFont));
            document.add(new Paragraph(medicines, normalFont));
            document.add(new Paragraph("--------------------------------------------------", normalFont));
            document.add(new Paragraph(" "));
            document.add(new Paragraph("Please follow dosage guidelines. Get well soon.", normalFont));

            document.close();
            fos.close();
            logger.info("Prescription PDF generated successfully for Pres ID: {}", presId);
            return fileName;
        } catch (Exception e) {
            logger.error("Error generating prescription PDF: ", e);
            return null;
        }
    }
}
