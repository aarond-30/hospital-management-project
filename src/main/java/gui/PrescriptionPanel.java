package gui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import database.PrescriptionDAO;
import database.PatientDAO;
import database.DoctorDAO;
import models.Patient;
import models.Doctor;

public class PrescriptionPanel extends JPanel {

    JComboBox<String> patientBox;
    JComboBox<String> doctorBox;
    JTextField symptomsField, diagnosisField, dateField;
    JTextArea medicineArea;
    JButton saveBtn, reprintBtn;

    JTable table;
    DefaultTableModel model;

    public PrescriptionPanel() {
        setLayout(new BorderLayout(15, 15));
        setBackground(new Color(245, 248, 255));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Title
        JLabel title = new JLabel("HMS Clinical Visits & Prescriptions");
        title.setFont(new Font("Arial", Font.BOLD, 22));
        title.setForeground(new Color(44, 62, 80));
        add(title, BorderLayout.NORTH);

        // Split Panel (Left Form, Right History Table)
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(420);
        splitPane.setOpaque(false);
        splitPane.setBorder(null);

        // ------------------ LEFT PANEL: FORM ------------------
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
        formPanel.add(new JLabel("Prescribed By:"), gbc);
        doctorBox = new JComboBox<>();
        doctorBox.setPreferredSize(new Dimension(180, 28));
        gbc.gridx = 1;
        formPanel.add(doctorBox, gbc);

        // Symptoms
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Recorded Symptoms:"), gbc);
        symptomsField = new JTextField(12);
        gbc.gridx = 1;
        formPanel.add(symptomsField, gbc);

        // Diagnosis
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Clinical Diagnosis:"), gbc);
        diagnosisField = new JTextField(12);
        gbc.gridx = 1;
        formPanel.add(diagnosisField, gbc);

        // Prescribed Medicines
        gbc.gridx = 0; gbc.gridy = 4;
        formPanel.add(new JLabel("Medicines & Dosage:"), gbc);
        medicineArea = new JTextArea(4, 15);
        medicineArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        medicineArea.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        JScrollPane medScroll = new JScrollPane(medicineArea);
        gbc.gridx = 1;
        formPanel.add(medScroll, gbc);

        // Date
        gbc.gridx = 0; gbc.gridy = 5;
        formPanel.add(new JLabel("Visit Date:"), gbc);
        dateField = new JTextField(new SimpleDateFormat("yyyy-MM-dd").format(new Date()), 10);
        gbc.gridx = 1;
        formPanel.add(dateField, gbc);

        // Save Button
        saveBtn = new JButton("Save & Print Script");
        saveBtn.setFont(new Font("Arial", Font.BOLD, 13));
        saveBtn.setBackground(new Color(34, 139, 34));
        saveBtn.setForeground(Color.WHITE);
        saveBtn.setFocusPainted(false);

        gbc.gridx = 0; gbc.gridy = 6;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.CENTER;
        gbc.insets = new Insets(15, 0, 5, 0);
        formPanel.add(saveBtn, gbc);

        splitPane.setLeftComponent(formPanel);

        // ------------------ RIGHT PANEL: MEDICAL HISTORY ------------------
        JPanel historyPanel = new JPanel(new BorderLayout(10, 10));
        historyPanel.setBackground(Color.WHITE);
        historyPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 230, 245), 1, true),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        JLabel histTitle = new JLabel("Patient Visit History Logs");
        histTitle.setFont(new Font("Arial", Font.BOLD, 15));
        historyPanel.add(histTitle, BorderLayout.NORTH);

        String[] cols = {"Visit ID", "Prescribed By", "Date", "Symptoms", "Diagnosis"};
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

        reprintBtn = new JButton("Reprint Selected Prescription PDF");
        reprintBtn.setFont(new Font("Arial", Font.BOLD, 13));
        reprintBtn.setBackground(new Color(70, 130, 180));
        reprintBtn.setForeground(Color.WHITE);
        reprintBtn.setFocusPainted(false);
        historyPanel.add(reprintBtn, BorderLayout.SOUTH);

        splitPane.setRightComponent(historyPanel);
        add(splitPane, BorderLayout.CENTER);

        // Setup drop-down select options
        loadSelectors();

        // Listeners
        saveBtn.addActionListener(e -> savePrescription());
        reprintBtn.addActionListener(e -> reprintPrescription());

        // Reload medical history table dynamically when patient selection is changed!
        patientBox.addActionListener(e -> refreshHistoryTable());

        // Load initially
        refreshHistoryTable();
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
                saveBtn.setEnabled(false);
            }
            if (doctors.isEmpty()) {
                doctorBox.addItem("No Doctors");
                saveBtn.setEnabled(false);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Failed loading selection criteria: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void savePrescription() {
        if (patientBox.getSelectedItem() == null || patientBox.getSelectedItem().toString().startsWith("No Patients")) return;
        if (doctorBox.getSelectedItem() == null || doctorBox.getSelectedItem().toString().startsWith("No Doctors")) return;

        String patSel = patientBox.getSelectedItem().toString();
        String docSel = doctorBox.getSelectedItem().toString();

        int patId = Integer.parseInt(patSel.split(" - ")[0]);
        String patName = patSel.split(" - ")[1];
        int docId = Integer.parseInt(docSel.split(" - ")[0]);
        String docName = docSel.split(" - ")[1];

        String symptoms = symptomsField.getText().trim();
        String diagnosis = diagnosisField.getText().trim();
        String medicines = medicineArea.getText().trim();
        String dateVal = dateField.getText().trim();

        if (symptoms.isEmpty() || diagnosis.isEmpty() || medicines.isEmpty() || dateVal.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all clinical visit fields.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!dateVal.matches("\\d{4}-\\d{2}-\\d{2}")) {
            JOptionPane.showMessageDialog(this, "Use date format YYYY-MM-DD.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        boolean success = PrescriptionDAO.insertPrescription(patId, docId, dateVal, symptoms, diagnosis, medicines);
        if (success) {
            // Find newly generated prescription ID
            List<String[]> list = PrescriptionDAO.getPrescriptionsByPatient(patId);
            int generatedId = 0;
            if (!list.isEmpty()) {
                generatedId = Integer.parseInt(list.get(0)[0]); // First item contains newest ID
            }

            JOptionPane.showMessageDialog(this, "Prescription saved successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            refreshHistoryTable();

            // Export to PDF
            String path = PrescriptionDAO.generatePrescriptionPDF(generatedId, patName, docName, dateVal, symptoms, diagnosis, medicines);
            if (path != null) {
                int option = JOptionPane.showConfirmDialog(this, "Prescription script generated as " + path + ".\nWould you like to open it now?", "Open PDF", JOptionPane.YES_NO_OPTION);
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

            // Clear inputs
            symptomsField.setText("");
            diagnosisField.setText("");
            medicineArea.setText("");

        } else {
            JOptionPane.showMessageDialog(this, "Failed to save prescription to database.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void reprintPrescription() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a visit from history list to print.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (patientBox.getSelectedItem() == null || patientBox.getSelectedItem().toString().startsWith("No Patients")) return;
        String patName = patientBox.getSelectedItem().toString().split(" - ")[1];

        int presId = Integer.parseInt(model.getValueAt(selectedRow, 0).toString());
        String docName = model.getValueAt(selectedRow, 1).toString();
        String date = model.getValueAt(selectedRow, 2).toString();
        String symptoms = model.getValueAt(selectedRow, 3).toString();
        String diagnosis = model.getValueAt(selectedRow, 4).toString();

        // Retrieve medicines (since they are in TEXT, we need to load them from DB again or we get it from list)
        String patSel = patientBox.getSelectedItem().toString();
        int patId = Integer.parseInt(patSel.split(" - ")[0]);
        List<String[]> list = PrescriptionDAO.getPrescriptionsByPatient(patId);
        String medicines = "";
        for (String[] row : list) {
            if (Integer.parseInt(row[0]) == presId) {
                medicines = row[5];
                break;
            }
        }

        String path = PrescriptionDAO.generatePrescriptionPDF(presId, patName, docName, date, symptoms, diagnosis, medicines);
        if (path != null) {
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

    public void refreshHistoryTable() {
        model.setRowCount(0);
        if (patientBox.getSelectedItem() == null || patientBox.getSelectedItem().toString().startsWith("No Patients")) {
            return;
        }

        String patSel = patientBox.getSelectedItem().toString();
        int patId = Integer.parseInt(patSel.split(" - ")[0]);

        List<String[]> list = PrescriptionDAO.getPrescriptionsByPatient(patId);
        for (String[] row : list) {
            // Display: ID, Doctor, Date, Symptoms, Diagnosis (skip Medicines for grid display, available on print)
            model.addRow(new Object[]{row[0], row[1], row[2], row[3], row[4]});
        }
    }
}
