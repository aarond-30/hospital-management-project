package database;

import com.lowagie.text.Document;
import com.lowagie.text.Font;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;
import models.LabReport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class LabReportDAO {

    private static final Logger logger = LoggerFactory.getLogger(LabReportDAO.class);

    public static void ensureLabReportsTableExists() {
        String query = "CREATE TABLE IF NOT EXISTS lab_reports (" +
                "report_id INT AUTO_INCREMENT PRIMARY KEY, " +
                "patient_id INT NOT NULL, " +
                "doctor_id INT NOT NULL, " +
                "test_name VARCHAR(100) NOT NULL, " +
                "test_date VARCHAR(20) NOT NULL, " +
                "test_metrics TEXT DEFAULT NULL, " +
                "diagnosis_summary TEXT DEFAULT NULL, " +
                "status VARCHAR(20) DEFAULT 'Pending', " +
                "FOREIGN KEY (patient_id) REFERENCES patients(patient_id) ON DELETE CASCADE, " +
                "FOREIGN KEY (doctor_id) REFERENCES doctors(doctor_id) ON DELETE CASCADE" +
                ")";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(query)) {
            pst.executeUpdate();
            logger.info("Checked/created lab_reports table successfully.");
        } catch (Exception e) {
            logger.error("Error creating lab_reports table: ", e);
        }
    }

    public static boolean insertLabReport(int patientId, int doctorId, String testName, String testDate) {
        ensureLabReportsTableExists();
        String query = "INSERT INTO lab_reports (patient_id, doctor_id, test_name, test_date, status) VALUES (?, ?, ?, ?, 'Pending')";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(query)) {
            pst.setInt(1, patientId);
            pst.setInt(2, doctorId);
            pst.setString(3, testName);
            pst.setString(4, testDate);

            int result = pst.executeUpdate();
            return result > 0;
        } catch (Exception e) {
            logger.error("Error inserting lab report: ", e);
            return false;
        }
    }

    public static boolean updateLabReport(int reportId, String metrics, String summary, String status) {
        ensureLabReportsTableExists();
        String query = "UPDATE lab_reports SET test_metrics=?, diagnosis_summary=?, status=? WHERE report_id=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(query)) {
            pst.setString(1, metrics);
            pst.setString(2, summary);
            pst.setString(3, status);
            pst.setInt(4, reportId);

            int result = pst.executeUpdate();
            return result > 0;
        } catch (Exception e) {
            logger.error("Error updating lab report: ", e);
            return false;
        }
    }

    public static List<LabReport> getAllLabReports() {
        ensureLabReportsTableExists();
        List<LabReport> list = new ArrayList<>();
        String query = "SELECT lr.*, p.name AS patient_name, d.name AS doctor_name " +
                "FROM lab_reports lr " +
                "LEFT JOIN patients p ON lr.patient_id = p.patient_id " +
                "LEFT JOIN doctors d ON lr.doctor_id = d.doctor_id " +
                "ORDER BY lr.report_id DESC";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(query);
             ResultSet rs = pst.executeQuery()) {

            while (rs.next()) {
                LabReport lr = new LabReport(
                        rs.getInt("report_id"),
                        rs.getInt("patient_id"),
                        rs.getString("patient_name"),
                        rs.getInt("doctor_id"),
                        rs.getString("doctor_name"),
                        rs.getString("test_name"),
                        rs.getString("test_date"),
                        rs.getString("test_metrics"),
                        rs.getString("diagnosis_summary"),
                        rs.getString("status")
                );
                list.add(lr);
            }
        } catch (Exception e) {
            logger.error("Error retrieving lab reports: ", e);
        }
        return list;
    }

    public static String generateLabReportPDF(LabReport lr) {
        String fileName = "LabReport_Diagnostics_" + lr.getReportId() + ".pdf";
        try {
            Document document = new Document();
            FileOutputStream fos = new FileOutputStream(fileName);
            PdfWriter.getInstance(document, fos);
            document.open();

            Font titleFont = new Font(Font.HELVETICA, 18, Font.BOLD);
            Font sectionFont = new Font(Font.HELVETICA, 12, Font.BOLD);
            Font normalFont = new Font(Font.HELVETICA, 10, Font.NORMAL);
            Font alertFont = new Font(Font.HELVETICA, 10, Font.ITALIC);

            document.add(new Paragraph("HMS LAB DIAGNOSTICS & MEDICAL REPORT", titleFont));
            document.add(new Paragraph("-------------------------------------------------------------------------"));
            document.add(new Paragraph("Report ID:    " + lr.getReportId(), normalFont));
            document.add(new Paragraph("Test Date:    " + lr.getTestDate(), normalFont));
            document.add(new Paragraph("Status:       " + lr.getStatus(), normalFont));
            document.add(new Paragraph(" "));

            document.add(new Paragraph("PATIENT & REFERRING CLINICIAN", sectionFont));
            document.add(new Paragraph("Patient Name: " + (lr.getPatientName() != null ? lr.getPatientName() : "ID: " + lr.getPatientId()), normalFont));
            document.add(new Paragraph("Referred By:  Dr. " + (lr.getDoctorName() != null ? lr.getDoctorName() : "ID: " + lr.getDoctorId()), normalFont));
            document.add(new Paragraph(" "));

            document.add(new Paragraph("DIAGNOSTIC TEST DETAILS", sectionFont));
            document.add(new Paragraph("Test Type:    " + lr.getTestName(), normalFont));
            document.add(new Paragraph(" "));

            document.add(new Paragraph("LAB METRICS / CLINICAL FINDINGS", sectionFont));
            document.add(new Paragraph("--------------------------------------------------", normalFont));
            String metrics = lr.getTestMetrics();
            if (metrics == null || metrics.trim().isEmpty()) {
                document.add(new Paragraph("Metrics: [Pending Lab Evaluation]", alertFont));
            } else {
                document.add(new Paragraph(metrics, normalFont));
            }
            document.add(new Paragraph("--------------------------------------------------", normalFont));
            document.add(new Paragraph(" "));

            document.add(new Paragraph("DIAGNOSTIC SUMMARY & OPINION", sectionFont));
            String summary = lr.getDiagnosisSummary();
            if (summary == null || summary.trim().isEmpty()) {
                document.add(new Paragraph("Summary: [Pending Diagnosis]", alertFont));
            } else {
                document.add(new Paragraph(summary, normalFont));
            }
            document.add(new Paragraph(" "));
            document.add(new Paragraph("--------------------------------------------------", normalFont));
            document.add(new Paragraph("Authorized Laboratory Signature Verification", normalFont));

            document.close();
            fos.close();
            logger.info("Lab Report PDF generated successfully for Report ID: {}", lr.getReportId());
            return fileName;
        } catch (Exception e) {
            logger.error("Error generating Lab Report PDF: ", e);
            return null;
        }
    }
}
