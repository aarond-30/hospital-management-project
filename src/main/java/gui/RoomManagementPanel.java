package gui;

import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import database.RoomDAO;
import database.PatientDAO;
import database.AuditDAO;
import models.Patient;

public class RoomManagementPanel extends JPanel {

    JPanel gridContainer;
    JScrollPane scrollPane;
    JButton refreshBtn;

    // Local Map to save pending room charges mapping to transfer to BillingPanel
    public static final java.util.Map<Integer, Double> pendingRoomCharges = new java.util.HashMap<>();

    public RoomManagementPanel() {
        setLayout(new BorderLayout(15, 15));
        setBackground(new Color(245, 248, 255));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Title
        JLabel title = new JLabel("HMS Ward Wards & Bed Occupancy");
        title.setFont(new Font("Arial", Font.BOLD, 22));
        title.setForeground(new Color(44, 62, 80));
        add(title, BorderLayout.NORTH);

        // Grid Container
        gridContainer = new JPanel(new GridLayout(0, 3, 20, 20));
        gridContainer.setOpaque(false);

        scrollPane = new JScrollPane(gridContainer);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        add(scrollPane, BorderLayout.CENTER);

        // Control Panel
        JPanel controls = new JPanel(new FlowLayout(FlowLayout.CENTER));
        controls.setOpaque(false);

        refreshBtn = new JButton("Refresh Wards Occupancy");
        refreshBtn.setFont(new Font("Arial", Font.BOLD, 14));
        refreshBtn.setBackground(new Color(70, 130, 180));
        refreshBtn.setForeground(Color.WHITE);
        refreshBtn.setPreferredSize(new Dimension(220, 38));
        refreshBtn.setFocusPainted(false);
        controls.add(refreshBtn);
        
        add(controls, BorderLayout.SOUTH);

        refreshBtn.addActionListener(e -> refreshWards());

        refreshWards();
    }

    public void refreshWards() {
        gridContainer.removeAll();
        List<String[]> rooms = RoomDAO.getAllRooms();

        for (String[] r : rooms) {
            String roomNo = r[0];
            String type = r[1];
            String rate = r[2];
            String status = r[3];
            String patId = r[4];
            String patName = r[5];
            String admittedDate = r[6];

            JPanel card = new JPanel();
            card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
            card.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(220, 230, 245), 1, true),
                    BorderFactory.createEmptyBorder(15, 15, 15, 15)
            ));
            card.setCursor(new Cursor(Cursor.HAND_CURSOR));

            JLabel noLbl = new JLabel("Room " + roomNo);
            noLbl.setFont(new Font("Arial", Font.BOLD, 16));
            noLbl.setAlignmentX(Component.CENTER_ALIGNMENT);

            JLabel typeLbl = new JLabel(type);
            typeLbl.setFont(new Font("Arial", Font.PLAIN, 12));
            typeLbl.setAlignmentX(Component.CENTER_ALIGNMENT);
            typeLbl.setBorder(BorderFactory.createEmptyBorder(3, 0, 5, 0));

            JLabel rateLbl = new JLabel("Rate: Rs. " + rate + " / day");
            rateLbl.setFont(new Font("Arial", Font.ITALIC, 11));
            rateLbl.setAlignmentX(Component.CENTER_ALIGNMENT);

            card.add(noLbl);
            card.add(typeLbl);
            card.add(rateLbl);

            if ("Vacant".equalsIgnoreCase(status)) {
                card.setBackground(new Color(230, 248, 235)); // soft green
                noLbl.setForeground(new Color(34, 139, 34));
                
                JLabel state = new JLabel("VACANT");
                state.setFont(new Font("Arial", Font.BOLD, 12));
                state.setForeground(new Color(34, 139, 34));
                state.setAlignmentX(Component.CENTER_ALIGNMENT);
                state.setBorder(BorderFactory.createEmptyBorder(8, 0, 0, 0));
                card.add(state);

                // Admit trigger on click
                card.addMouseListener(new java.awt.event.MouseAdapter() {
                    @Override
                    public void mouseReleased(java.awt.event.MouseEvent evt) {
                        showAdmitDialog(roomNo, type, rate);
                    }
                });

            } else {
                card.setBackground(new Color(255, 235, 235)); // soft red
                noLbl.setForeground(new Color(178, 34, 34));

                JLabel patLbl = new JLabel("Patient: " + patName + " (" + patId + ")");
                patLbl.setFont(new Font("Arial", Font.BOLD, 12));
                patLbl.setForeground(Color.DARK_GRAY);
                patLbl.setAlignmentX(Component.CENTER_ALIGNMENT);
                patLbl.setBorder(BorderFactory.createEmptyBorder(8, 0, 0, 0));
                card.add(patLbl);

                JLabel dateLbl = new JLabel("Admitted: " + admittedDate);
                dateLbl.setFont(new Font("Arial", Font.PLAIN, 11));
                dateLbl.setForeground(Color.DARK_GRAY);
                dateLbl.setAlignmentX(Component.CENTER_ALIGNMENT);
                card.add(dateLbl);

                JLabel state = new JLabel("OCCUPIED");
                state.setFont(new Font("Arial", Font.BOLD, 11));
                state.setForeground(new Color(178, 34, 34));
                state.setAlignmentX(Component.CENTER_ALIGNMENT);
                state.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
                card.add(state);

                // Discharge trigger on click
                card.addMouseListener(new java.awt.event.MouseAdapter() {
                    @Override
                    public void mouseReleased(java.awt.event.MouseEvent evt) {
                        showDischargeDialog(roomNo, patId, patName, admittedDate);
                    }
                });
            }

            gridContainer.add(card);
        }

        gridContainer.revalidate();
        gridContainer.repaint();
    }

    private void showAdmitDialog(String roomNo, String type, String rate) {
        JComboBox<String> patientCombo = new JComboBox<>();
        try {
            List<Patient> patients = PatientDAO.getAllPatients();
            for (Patient p : patients) {
                patientCombo.addItem(p.getPatientId() + " - " + p.getName());
            }
            if (patients.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please register patients first before allocating rooms.", "No Patients", JOptionPane.WARNING_MESSAGE);
                return;
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error loading patient list: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JTextField dateField = new JTextField(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));

        Object[] form = {
                "Select Patient to Admit:", patientCombo,
                "Admission Date (YYYY-MM-DD):", dateField
        };

        int option = JOptionPane.showConfirmDialog(this, form, "Admit Patient to Room " + roomNo, JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (option == JOptionPane.OK_OPTION) {
            String selected = patientCombo.getSelectedItem().toString();
            int patId = Integer.parseInt(selected.split(" - ")[0]);
            String patName = selected.split(" - ")[1];
            String dateVal = dateField.getText().trim();

            if (!dateVal.matches("\\d{4}-\\d{2}-\\d{2}")) {
                JOptionPane.showMessageDialog(this, "Invalid date format. Use YYYY-MM-DD.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            boolean success = RoomDAO.admitPatient(roomNo, patId, dateVal);
            if (success) {
                AuditDAO.log(null, "Admitted patient " + patName + " (ID " + patId + ") to Room " + roomNo);
                JOptionPane.showMessageDialog(this, "Patient admitted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                refreshWards();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to admit patient. Check room availability.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void showDischargeDialog(String roomNo, String patId, String patName, String admittedDate) {
        int confirm = JOptionPane.showConfirmDialog(this, 
                "Room " + roomNo + " occupancy details:\n" +
                "Patient: " + patName + " (ID " + patId + ")\n" +
                "Admitted on: " + admittedDate + "\n\n" +
                "Do you want to check out / discharge this patient?", 
                "Patient Discharge Wizard", 
                JOptionPane.YES_NO_OPTION, 
                JOptionPane.QUESTION_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            String[] details = RoomDAO.dischargePatient(roomNo);
            if (details != null) {
                int patientId = Integer.parseInt(details[0]);
                String name = details[1];
                int days = Integer.parseInt(details[4]);
                double totalCharges = Double.parseDouble(details[5]);

                // Record stay duration total fee to transfer to Billing
                pendingRoomCharges.put(patientId, totalCharges);

                AuditDAO.log(null, "Discharged patient " + name + " (ID " + patientId + ") from Room " + roomNo);

                JOptionPane.showMessageDialog(this, 
                        "Discharge Summary details:\n" +
                        "------------------------------------\n" +
                        "Patient Name:  " + name + "\n" +
                        "Room Allocated:  Room " + roomNo + "\n" +
                        "Stay Duration: " + days + " day(s)\n" +
                        "Total Charges:  Rs. " + String.format("%.2f", totalCharges) + "\n\n" +
                        "Stay charges have been transferred to the Billing portal.", 
                        "Discharge Summary", 
                        JOptionPane.INFORMATION_MESSAGE);

                refreshWards();
            } else {
                JOptionPane.showMessageDialog(this, "Error discharging patient.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
