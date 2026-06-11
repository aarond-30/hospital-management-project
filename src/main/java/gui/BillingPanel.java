package gui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import database.BillingDAO;
import database.PatientDAO;
import database.DoctorDAO;
import models.Patient;
import models.Doctor;
import models.Bill;

public class BillingPanel extends JPanel {

    JComboBox<String> patientBox;
    JComboBox<String> doctorBox;
    JTextField consultField, roomField, medField, otherField, totalField;
    JButton calcBtn, saveBtn, printSelectedBtn;

    JTable table;
    DefaultTableModel model;

    public BillingPanel() {
        setLayout(new BorderLayout(15, 15));
        setBackground(new Color(245, 248, 255));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Title
        JLabel title = new JLabel("HMS Billing & Invoicing Portal");
        title.setFont(new Font("Arial", Font.BOLD, 22));
        title.setForeground(new Color(44, 62, 80));
        add(title, BorderLayout.NORTH);

        // Split Panel (Left Form, Right Table)
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
        formPanel.add(new JLabel("Select Doctor:"), gbc);
        doctorBox = new JComboBox<>();
        doctorBox.setPreferredSize(new Dimension(180, 28));
        gbc.gridx = 1;
        formPanel.add(doctorBox, gbc);

        // Consultation Fee
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Consultation Fee (Rs.):"), gbc);
        consultField = new JTextField("50.00", 10);
        gbc.gridx = 1;
        formPanel.add(consultField, gbc);

        // Room Charges
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Room Charges (Rs.):"), gbc);
        roomField = new JTextField("0.00", 10);
        gbc.gridx = 1;
        formPanel.add(roomField, gbc);

        // Medicine Charges
        gbc.gridx = 0; gbc.gridy = 4;
        formPanel.add(new JLabel("Medicine Charges (Rs.):"), gbc);
        medField = new JTextField("0.00", 10);
        gbc.gridx = 1;
        formPanel.add(medField, gbc);

        // Other Charges
        gbc.gridx = 0; gbc.gridy = 5;
        formPanel.add(new JLabel("Other Charges (Rs.):"), gbc);
        otherField = new JTextField("0.00", 10);
        gbc.gridx = 1;
        formPanel.add(otherField, gbc);

        // Total
        gbc.gridx = 0; gbc.gridy = 6;
        formPanel.add(new JLabel("TOTAL AMOUNT (Rs.):"), gbc);
        totalField = new JTextField("0.00", 10);
        totalField.setEditable(false);
        totalField.setFont(new Font("Arial", Font.BOLD, 14));
        totalField.setForeground(new Color(178, 34, 34));
        gbc.gridx = 1;
        formPanel.add(totalField, gbc);

        // Buttons Grid inside form
        JPanel formBtns = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        formBtns.setOpaque(false);

        calcBtn = new JButton("Calculate Total");
        calcBtn.setBackground(new Color(70, 130, 180));
        calcBtn.setForeground(Color.WHITE);
        calcBtn.setFocusPainted(false);

        saveBtn = new JButton("Generate Bill");
        saveBtn.setBackground(new Color(34, 139, 34));
        saveBtn.setForeground(Color.WHITE);
        saveBtn.setFocusPainted(false);

        formBtns.add(calcBtn);
        formBtns.add(saveBtn);

        gbc.gridx = 0; gbc.gridy = 7;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.CENTER;
        formPanel.add(formBtns, gbc);

        splitPane.setLeftComponent(formPanel);

        // ------------------ RIGHT PANEL: HISTORY TABLE ------------------
        JPanel historyPanel = new JPanel(new BorderLayout(10, 10));
        historyPanel.setBackground(Color.WHITE);
        historyPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 230, 245), 1, true),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        JLabel histTitle = new JLabel("Billing & Invoicing History");
        histTitle.setFont(new Font("Arial", Font.BOLD, 15));
        historyPanel.add(histTitle, BorderLayout.NORTH);

        String[] cols = {"Bill ID", "Patient", "Doctor", "Total", "Date"};
        model = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };

        table = new JTable(model);
        table.setFont(new Font("Arial", Font.PLAIN, 13));
        table.setRowHeight(26);
        table.setShowGrid(true);
        table.setGridColor(new Color(240, 240, 240));

        JScrollPane scroll = new JScrollPane(table);
        historyPanel.add(scroll, BorderLayout.CENTER);

        printSelectedBtn = new JButton("Print / Open Selected Invoice PDF");
        printSelectedBtn.setFont(new Font("Arial", Font.BOLD, 13));
        printSelectedBtn.setBackground(new Color(70, 130, 180));
        printSelectedBtn.setForeground(Color.WHITE);
        printSelectedBtn.setFocusPainted(false);
        historyPanel.add(printSelectedBtn, BorderLayout.SOUTH);

        splitPane.setRightComponent(historyPanel);
        add(splitPane, BorderLayout.CENTER);

        // Setup drop-down select options
        loadSelectors();

        // Listeners
        calcBtn.addActionListener(e -> calculateTotal());
        saveBtn.addActionListener(e -> saveAndPrintBill());
        printSelectedBtn.addActionListener(e -> printSelectedBill());

        patientBox.addActionListener(e -> {
            Object selected = patientBox.getSelectedItem();
            if (selected != null && !selected.toString().startsWith("No Patients")) {
                int patId = Integer.parseInt(selected.toString().split(" - ")[0]);
                if (RoomManagementPanel.pendingRoomCharges.containsKey(patId)) {
                    roomField.setText(String.format("%.2f", RoomManagementPanel.pendingRoomCharges.get(patId)));
                } else {
                    roomField.setText("0.00");
                }
            }
        });

        // Load bill logs
        refreshTable();
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

    private double calculateTotal() {
        try {
            double consult = Double.parseDouble(consultField.getText().trim());
            double room = Double.parseDouble(roomField.getText().trim());
            double med = Double.parseDouble(medField.getText().trim());
            double other = Double.parseDouble(otherField.getText().trim());

            double total = consult + room + med + other;
            totalField.setText(String.format("%.2f", total));
            return total;
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter valid numeric amounts.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return -1.0;
        }
    }

    private void saveAndPrintBill() {
        if (patientBox.getSelectedItem() == null || patientBox.getSelectedItem().toString().startsWith("No Patients")) return;
        if (doctorBox.getSelectedItem() == null || doctorBox.getSelectedItem().toString().startsWith("No Doctors")) return;

        double total = calculateTotal();
        if (total < 0) return;

        String patSel = patientBox.getSelectedItem().toString();
        String docSel = doctorBox.getSelectedItem().toString();

        int patId = Integer.parseInt(patSel.split(" - ")[0]);
        String patName = patSel.split(" - ")[1];
        int docId = Integer.parseInt(docSel.split(" - ")[0]);
        String docName = docSel.split(" - ")[1];

        double consult = Double.parseDouble(consultField.getText().trim());
        double room = Double.parseDouble(roomField.getText().trim());
        double med = Double.parseDouble(medField.getText().trim());
        double other = Double.parseDouble(otherField.getText().trim());

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateStr = df.format(new Date());

        Bill bill = new Bill(0, patId, patName, docId, docName, consult, room, med, other, total, dateStr);

        boolean success = BillingDAO.insertBill(bill);
        if (success) {
            // Find newly generated ID
            List<Bill> list = BillingDAO.getAllBills();
            if (!list.isEmpty()) {
                bill.setBillId(list.get(0).getBillId()); // Get first item from desc order list
            }

            // Write logs and simulate messages
            Patient p = PatientDAO.getPatientById(patId);
            String patPhone = (p != null) ? p.getPhone() : "N/A";
            
            database.AuditDAO.log(null, "Generated bill ID " + bill.getBillId() + " for Patient: " + patName + " (Total: Rs. " + total + ")");
            database.NotificationSimulator.sendNotification(patPhone, "Dear " + patName + ", an invoice of Rs. " + total + " (Bill ID " + bill.getBillId() + ") was generated for your hospital visit on " + dateStr + ".");
            
            // Clear checkout stay parameters from cache
            RoomManagementPanel.pendingRoomCharges.remove(patId);

            JOptionPane.showMessageDialog(this, "Bill record saved successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            refreshTable();

            // Export to PDF
            String path = BillingDAO.generateBillPDF(bill);
            if (path != null) {
                int option = JOptionPane.showConfirmDialog(this, "Invoice generated as " + path + ".\nWould you like to open it now?", "Open PDF", JOptionPane.YES_NO_OPTION);
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
        } else {
            JOptionPane.showMessageDialog(this, "Failed to persist bill to DB.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void printSelectedBill() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a bill from history to reprint.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int billId = Integer.parseInt(model.getValueAt(selectedRow, 0).toString());
        List<Bill> list = BillingDAO.getAllBills();
        Bill target = null;
        for (Bill b : list) {
            if (b.getBillId() == billId) {
                target = b;
                break;
            }
        }

        if (target != null) {
            String path = BillingDAO.generateBillPDF(target);
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
    }

    public void refreshTable() {
        model.setRowCount(0);
        loadSelectors(); // Reload selections dynamically as patient/doctor additions might have changed
        List<Bill> list = BillingDAO.getAllBills();
        for (Bill b : list) {
            Object[] row = {
                    b.getBillId(),
                    b.getPatientName() != null ? b.getPatientName() : ("Patient ID: " + b.getPatientId()),
                    b.getDoctorName() != null ? b.getDoctorName() : ("Doctor ID: " + b.getDoctorId()),
                    "Rs. " + String.format("%.2f", b.getTotalAmount()),
                    b.getBillingDate()
            };
            model.addRow(row);
        }
    }
}
