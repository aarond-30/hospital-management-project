package database;

import com.lowagie.text.Document;
import com.lowagie.text.Font;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;
import models.Bill;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class BillingDAO {

    private static final Logger logger = LoggerFactory.getLogger(BillingDAO.class);

    public static void ensureBillingTableExists() {
        String createTableQuery = "CREATE TABLE IF NOT EXISTS billing (" +
                "bill_id INT AUTO_INCREMENT PRIMARY KEY, " +
                "patient_id INT NOT NULL, " +
                "doctor_id INT NOT NULL, " +
                "consultation_fee DOUBLE DEFAULT 0.0, " +
                "room_charges DOUBLE DEFAULT 0.0, " +
                "medicine_charges DOUBLE DEFAULT 0.0, " +
                "other_charges DOUBLE DEFAULT 0.0, " +
                "total_amount DOUBLE DEFAULT 0.0, " +
                "billing_date VARCHAR(20) NOT NULL" +
                ")";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(createTableQuery)) {
            pst.executeUpdate();
            logger.info("Checked/created billing table successfully.");
        } catch (Exception e) {
            logger.error("Error ensuring billing table exists: ", e);
        }
    }

    public static boolean insertBill(Bill b) {
        ensureBillingTableExists();
        String query = "INSERT INTO billing (patient_id, doctor_id, consultation_fee, room_charges, " +
                "medicine_charges, other_charges, total_amount, billing_date) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(query)) {
            pst.setInt(1, b.getPatientId());
            pst.setInt(2, b.getDoctorId());
            pst.setDouble(3, b.getConsultationFee());
            pst.setDouble(4, b.getRoomCharges());
            pst.setDouble(5, b.getMedicineCharges());
            pst.setDouble(6, b.getOtherCharges());
            pst.setDouble(7, b.getTotalAmount());
            pst.setString(8, b.getBillingDate());

            int result = pst.executeUpdate();
            return result > 0;
        } catch (Exception e) {
            logger.error("Error inserting bill: ", e);
            return false;
        }
    }

    public static List<Bill> getAllBills() {
        ensureBillingTableExists();
        List<Bill> list = new ArrayList<>();
        String query = "SELECT b.*, p.name AS patient_name, d.name AS doctor_name " +
                "FROM billing b " +
                "LEFT JOIN patients p ON b.patient_id = p.patient_id " +
                "LEFT JOIN doctors d ON b.doctor_id = d.doctor_id " +
                "ORDER BY b.bill_id DESC";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(query);
             ResultSet rs = pst.executeQuery()) {

            while (rs.next()) {
                Bill b = new Bill(
                        rs.getInt("bill_id"),
                        rs.getInt("patient_id"),
                        rs.getString("patient_name"),
                        rs.getInt("doctor_id"),
                        rs.getString("doctor_name"),
                        rs.getDouble("consultation_fee"),
                        rs.getDouble("room_charges"),
                        rs.getDouble("medicine_charges"),
                        rs.getDouble("other_charges"),
                        rs.getDouble("total_amount"),
                        rs.getString("billing_date")
                );
                list.add(b);
            }
        } catch (Exception e) {
            logger.error("Error retrieving bills: ", e);
        }
        return list;
    }

    public static String generateBillPDF(Bill b) {
        String fileName = "Invoice_Bill_" + b.getBillId() + ".pdf";
        try {
            Document document = new Document();
            FileOutputStream fos = new FileOutputStream(fileName);
            PdfWriter.getInstance(document, fos);
            document.open();

            Font titleFont = new Font(Font.HELVETICA, 20, Font.BOLD);
            Font sectionFont = new Font(Font.HELVETICA, 12, Font.BOLD);
            Font normalFont = new Font(Font.HELVETICA, 10, Font.NORMAL);

            document.add(new Paragraph("HMS HOSPITAL BILL INVOICE", titleFont));
            document.add(new Paragraph("-------------------------------------------------------------------------"));
            document.add(new Paragraph("Invoice ID: " + b.getBillId(), normalFont));
            document.add(new Paragraph("Billing Date: " + b.getBillingDate(), normalFont));
            document.add(new Paragraph(" "));

            document.add(new Paragraph("PATIENT DETAILS", sectionFont));
            document.add(new Paragraph("Patient ID: " + b.getPatientId(), normalFont));
            document.add(new Paragraph("Name: " + b.getPatientName(), normalFont));
            document.add(new Paragraph(" "));

            document.add(new Paragraph("DOCTOR DETAILS", sectionFont));
            document.add(new Paragraph("Doctor ID: " + b.getDoctorId(), normalFont));
            document.add(new Paragraph("Name: " + b.getDoctorName(), normalFont));
            document.add(new Paragraph(" "));

            document.add(new Paragraph("CHARGE DETAILS & FEE BREAKDOWN", sectionFont));
            document.add(new Paragraph("--------------------------------------------------", normalFont));
            document.add(new Paragraph("Consultation Fee:    Rs. " + String.format("%.2f", b.getConsultationFee()), normalFont));
            document.add(new Paragraph("Room Charges:          Rs. " + String.format("%.2f", b.getRoomCharges()), normalFont));
            document.add(new Paragraph("Medicine Charges:      Rs. " + String.format("%.2f", b.getMedicineCharges()), normalFont));
            document.add(new Paragraph("Other Charges:         Rs. " + String.format("%.2f", b.getOtherCharges()), normalFont));
            document.add(new Paragraph("--------------------------------------------------", normalFont));
            document.add(new Paragraph("TOTAL AMOUNT:          Rs. " + String.format("%.2f", b.getTotalAmount()), sectionFont));
            document.add(new Paragraph("--------------------------------------------------", normalFont));
            document.add(new Paragraph(" "));
            document.add(new Paragraph("Thank you for choosing HMS Hospital Care.", normalFont));

            document.close();
            fos.close();
            logger.info("Invoice PDF generated successfully for Bill ID: {}", b.getBillId());
            return fileName;
        } catch (Exception e) {
            logger.error("Error generating bill PDF: ", e);
            return null;
        }
    }
}
