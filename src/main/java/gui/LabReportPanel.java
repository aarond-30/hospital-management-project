package gui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import database.LabReportDAO;
import database.PatientDAO;
import database.DoctorDAO;
import models.Patient;
import models.Doctor;
import models.LabReport;

public class LabReportPanel extends JPanel {

    JComboBox<String> patientBox;
    JComboBox<String> doctorBox;
    JComboBox<String> testNameBox;
    JTextField dateField;
    JButton bookBtn, updateBtn, printBtn;

    JTable table;
    DefaultTableModel model;
    List<LabReport> reportList;

    public LabReportPanel() {
        setLayout(new BorderLayout(15, 15));
        setBackground(new Color(245, 248, 255));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Title
        JLabel title = new JLabel("HMS Laboratory Diagnostics & Reports");
        title.setFont(new Font("Arial", Font.BOLD, 22));
        title.setForeground(new Color(44, 62, 80));
        add(title, BorderLayout.NORTH);

        // Split Panel (Left Form, Right History Table)
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(420);
        splitPane.setOpaque(false);
        splitPane.setBorder(null);

        // ------------------ LEFT PANEL: BOOKING FORM ------------------
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 230, 245), 1, true),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        // Select Patient
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Select Patient:"), gbc);
        patientBox = new JComboBox<>();
        patientBox.setPreferredSize(new Dimension(180, 28));
        gbc.gridx = 1;
        formPanel.add(patientBox, gbc);

        // Select Doctor
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Referred By:"), gbc);
        doctorBox = new JComboBox<>();
        doctorBox.setPreferredSize(new Dimension(180, 28));
        gbc.gridx = 1;
        formPanel.add(doctorBox, gbc);

        // Select/Input Test Name
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Test Type:"), gbc);
        
        String[] testOptions = {
                "Complete Blood Count (CBC)",
                "Lipid Profile (Cholesterol)",
                "Liver Function Test (LFT)",
                "Kidney Function Test (KFT)",
                "Thyroid Profile (T3, T4, TSH)",
                "Urine Analysis",
                "Blood Sugar (HbA1c)",
                "X-Ray Chest",
                "Electrocardiogram (ECG)"
        };
        testNameBox = new JComboBox<>(testOptions);
        testNameBox.setEditable(true);
        testNameBox.setPreferredSize(new Dimension(180, 28));
        gbc.gridx = 1;
        formPanel.add(testNameBox, gbc);

        // Date
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Booking Date:"), gbc);
        dateField = new JTextField(new SimpleDateFormat("yyyy-MM-dd").format(new Date()), 10);
        gbc.gridx = 1;
        formPanel.add(dateField, gbc);

        // Book Button
        bookBtn = new JButton("Book Diagnostics Test");
        bookBtn.setFont(new Font("Arial", Font.BOLD, 13));
        bookBtn.setBackground(new Color(34, 139, 34));
        bookBtn.setForeground(Color.WHITE);
        bookBtn.setFocusPainted(false);

        gbc.gridx = 0; gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.CENTER;
        gbc.insets = new Insets(15, 0, 5, 0);
        formPanel.add(bookBtn, gbc);

        splitPane.setLeftComponent(formPanel);

        // ------------------ RIGHT PANEL: DIAGNOSTIC LOGS ------------------
        JPanel historyPanel = new JPanel(new BorderLayout(10, 10));
        historyPanel.setBackground(Color.WHITE);
        historyPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 230, 245), 1, true),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        JLabel histTitle = new JLabel("All Diagnostic Reports History");
        histTitle.setFont(new Font("Arial", Font.BOLD, 15));
        historyPanel.add(histTitle, BorderLayout.NORTH);

        String[] cols = {"Report ID", "Patient Name", "Referring Doctor", "Test Name", "Status", "Date"};
        model = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(model);
        table.setFont(new Font("Arial", Font.PLAIN, 13));
        table.setRowHeight(26);
        table.setShowGrid(true);
        table.setGridColor(new Color(240, 240, 240));

        JScrollPane scroll = new JScrollPane(table);
        historyPanel.add(scroll, BorderLayout.CENTER);

        JPanel actionBtnContainer = new JPanel(new GridLayout(1, 2, 10, 0));
        actionBtnContainer.setOpaque(false);

        updateBtn = new JButton("Update Lab Metrics / Complete");
        updateBtn.setFont(new Font("Arial", Font.BOLD, 13));
        updateBtn.setBackground(new Color(70, 130, 180));
        updateBtn.setForeground(Color.WHITE);
        updateBtn.setFocusPainted(false);

        printBtn = new JButton("Print / Open Report PDF");
        printBtn.setFont(new Font("Arial", Font.BOLD, 13));
        printBtn.setBackground(new Color(34, 139, 34));
        printBtn.setForeground(Color.WHITE);
        printBtn.setFocusPainted(false);

        actionBtnContainer.add(updateBtn);
        actionBtnContainer.add(printBtn);
        historyPanel.add(actionBtnContainer, BorderLayout.SOUTH);

        splitPane.setRightComponent(historyPanel);
        add(splitPane, BorderLayout.CENTER);

        // Populate items
        loadSelectors();
        refreshTable();

        // Listeners
        bookBtn.addActionListener(e -> bookTest());
        updateBtn.addActionListener(e -> openUpdateDialog());
        printBtn.addActionListener(e -> generatePDFReport());
    }

    private void loadSelectors() {
        try {
            patientBox.removeAllItems();
            doctorBox.removeAllItems();

            List<Patient> patients = PatientDAO.getAllPatients();
            for (Patient p : patients) {
                patientBox.addItem(p.getPatientId() + " - " + p.getName());
            }

            List<Doctor> doctors = DoctorDAO.getAllDoctors();
            for (Doctor d : doctors) {
                doctorBox.addItem(d.getDoctorId() + " - " + d.getName());
            }

            if (patients.isEmpty()) {
                patientBox.addItem("No Patients");
                bookBtn.setEnabled(false);
            }
            if (doctors.isEmpty()) {
                doctorBox.addItem("No Doctors");
                bookBtn.setEnabled(false);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Failed loading selection criteria: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void bookTest() {
        if (patientBox.getSelectedItem() == null || patientBox.getSelectedItem().toString().startsWith("No Patients")) return;
        if (doctorBox.getSelectedItem() == null || doctorBox.getSelectedItem().toString().startsWith("No Doctors")) return;

        String patSel = patientBox.getSelectedItem().toString();
        String docSel = doctorBox.getSelectedItem().toString();

        int patId = Integer.parseInt(patSel.split(" - ")[0]);
        String patName = patSel.split(" - ")[1];
        int docId = Integer.parseInt(docSel.split(" - ")[0]);

        Object testObj = testNameBox.getSelectedItem();
        String testName = (testObj != null) ? testObj.toString().trim() : "";
        String dateVal = dateField.getText().trim();

        if (testName.isEmpty() || dateVal.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter test type and date.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!dateVal.matches("\\d{4}-\\d{2}-\\d{2}")) {
            JOptionPane.showMessageDialog(this, "Use date format YYYY-MM-DD.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        boolean success = LabReportDAO.insertLabReport(patId, docId, testName, dateVal);
        if (success) {
            database.AuditDAO.log(null, "Booked lab test: " + testName + " for Patient: " + patName);
            JOptionPane.showMessageDialog(this, "Lab test booked successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            refreshTable();
        } else {
            JOptionPane.showMessageDialog(this, "Failed booking lab test.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void openUpdateDialog() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a lab report to update.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        LabReport selectedReport = reportList.get(selectedRow);

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Update Lab Metrics & Results", true);
        dialog.setSize(450, 400);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.fill = GridBagConstraints.BOTH;

        gbc.gridx = 0; gbc.gridy = 0;
        dialog.add(new JLabel("Test Type:"), gbc);
        JTextField tfTest = new JTextField(selectedReport.getTestName());
        tfTest.setEditable(false);
        gbc.gridx = 1;
        dialog.add(tfTest, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        dialog.add(new JLabel("Enter Clinical Metrics:"), gbc);
        JTextArea taMetrics = new JTextArea(selectedReport.getTestMetrics() == null ? "" : selectedReport.getTestMetrics(), 5, 20);
        taMetrics.setFont(new Font("Monospaced", Font.PLAIN, 12));
        taMetrics.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        JScrollPane scrollMetrics = new JScrollPane(taMetrics);
        gbc.gridx = 1;
        dialog.add(scrollMetrics, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        dialog.add(new JLabel("Diagnosis / Summary:"), gbc);
        JTextField tfSummary = new JTextField(selectedReport.getDiagnosisSummary() == null ? "" : selectedReport.getDiagnosisSummary());
        gbc.gridx = 1;
        dialog.add(tfSummary, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        dialog.add(new JLabel("Report Status:"), gbc);
        JComboBox<String> cbStatus = new JComboBox<>(new String[]{"Pending", "Completed"});
        cbStatus.setSelectedItem(selectedReport.getStatus());
        gbc.gridx = 1;
        dialog.add(cbStatus, gbc);

        // Buttons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        JButton saveBtn = new JButton("Save Results");
        saveBtn.setBackground(new Color(34, 139, 34));
        saveBtn.setForeground(Color.WHITE);
        JButton cancelBtn = new JButton("Cancel");

        btnPanel.add(saveBtn);
        btnPanel.add(cancelBtn);

        gbc.gridx = 0; gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        dialog.add(btnPanel, gbc);

        cancelBtn.addActionListener(e -> dialog.dispose());

        saveBtn.addActionListener(e -> {
            String metrics = taMetrics.getText().trim();
            String summary = tfSummary.getText().trim();
            String status = cbStatus.getSelectedItem().toString();

            if ("Completed".equals(status) && (metrics.isEmpty() || summary.isEmpty())) {
                JOptionPane.showMessageDialog(dialog, "Please complete lab metrics and diagnosis summary before setting status to Completed.", "Validation Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }

            boolean ok = LabReportDAO.updateLabReport(selectedReport.getReportId(), metrics, summary, status);
            if (ok) {
                String patientName = selectedReport.getPatientName() != null ? selectedReport.getPatientName() : "ID: " + selectedReport.getPatientId();
                database.AuditDAO.log(null, "Completed lab test: " + selectedReport.getTestName() + " for Patient: " + patientName);
                
                // Dispatch patient SMS simulated notification on completion
                if ("Completed".equals(status)) {
                    Patient p = PatientDAO.getPatientById(selectedReport.getPatientId());
                    String phone = (p != null) ? p.getPhone() : "N/A";
                    database.NotificationSimulator.sendNotification(phone, "Dear " + patientName + ", your lab report for " + selectedReport.getTestName() + " is ready. Diagnosis: " + summary + ".");
                }

                JOptionPane.showMessageDialog(dialog, "Lab report updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
                refreshTable();
            } else {
                JOptionPane.showMessageDialog(dialog, "Failed to update lab report in database.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        dialog.setVisible(true);
    }

    private void generatePDFReport() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a lab report to generate PDF.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        LabReport selectedReport = reportList.get(selectedRow);
        String path = LabReportDAO.generateLabReportPDF(selectedReport);
        if (path != null) {
            int option = JOptionPane.showConfirmDialog(this, "Lab report generated as " + path + ".\nWould you like to open it now?", "Open PDF", JOptionPane.YES_NO_OPTION);
            if (option == JOptionPane.YES_OPTION) {
                try {
                    java.io.File file = new java.io.File(path);
                    if (Desktop.isDesktopSupported()) {
                        Desktop.getDesktop().open(file);
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Failed opening PDF: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    public void refreshTable() {
        model.setRowCount(0);
        loadSelectors();
        reportList = LabReportDAO.getAllLabReports();
        for (LabReport lr : reportList) {
            Object[] row = {
                    lr.getReportId(),
                    lr.getPatientName() != null ? lr.getPatientName() : "ID: " + lr.getPatientId(),
                    lr.getDoctorName() != null ? "Dr. " + lr.getDoctorName() : "ID: " + lr.getDoctorId(),
                    lr.getTestName(),
                    lr.getStatus(),
                    lr.getTestDate()
            };
            model.addRow(row);
        }
    }
}
