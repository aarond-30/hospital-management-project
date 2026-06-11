package gui;

import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import database.AppointmentDAO;
import database.PatientDAO;
import database.DoctorDAO;
import database.AuditDAO;
import database.NotificationSimulator;
import models.Patient;
import models.Doctor;

public class AppointmentForm extends JDialog {

    JComboBox<String> patientBox;
    JComboBox<String> doctorBox;
    JTextField dateField;
    JTextField timeField;
    JLabel availLabel;

    JButton checkBtn;
    JButton bookBtn;
    JButton clearBtn;
    JButton cancelBtn;
    Runnable refreshCallback;

    public AppointmentForm(Window parent, Runnable refreshCallback) {
        super(parent, "Book Appointment", ModalityType.APPLICATION_MODAL);
        this.refreshCallback = refreshCallback;

        setSize(480, 520);
        setLayout(new BorderLayout(10, 10));
        setLocationRelativeTo(parent);
        getContentPane().setBackground(new Color(245, 248, 255));

        JLabel title = new JLabel("Appointment Booking", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 22));
        title.setBorder(BorderFactory.createEmptyBorder(20, 10, 10, 10));
        add(title, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(new Color(245, 248, 255));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        // Patient Dropdown
        JLabel patientLabel = new JLabel("Select Patient:");
        patientLabel.setFont(new Font("Arial", Font.BOLD, 14));
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(patientLabel, gbc);

        patientBox = new JComboBox<>();
        patientBox.setPreferredSize(new Dimension(220, 30));
        gbc.gridx = 1;
        formPanel.add(patientBox, gbc);

        // Doctor Dropdown
        JLabel doctorLabel = new JLabel("Select Doctor:");
        doctorLabel.setFont(new Font("Arial", Font.BOLD, 14));
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(doctorLabel, gbc);

        doctorBox = new JComboBox<>();
        doctorBox.setPreferredSize(new Dimension(220, 30));
        gbc.gridx = 1;
        formPanel.add(doctorBox, gbc);

        // Date
        JLabel dateLabel = new JLabel("Date (YYYY-MM-DD):");
        dateLabel.setFont(new Font("Arial", Font.BOLD, 14));
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(dateLabel, gbc);

        dateField = new JTextField(12);
        gbc.gridx = 1;
        formPanel.add(dateField, gbc);

        // Time
        JLabel timeLabel = new JLabel("Time (HH:MM:SS):");
        timeLabel.setFont(new Font("Arial", Font.BOLD, 14));
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(timeLabel, gbc);

        timeField = new JTextField(12);
        gbc.gridx = 1;
        formPanel.add(timeField, gbc);

        // Conflict check label & button
        availLabel = new JLabel("Status: Check availability...");
        availLabel.setFont(new Font("Arial", Font.BOLD, 13));
        availLabel.setForeground(Color.DARK_GRAY);
        gbc.gridx = 0; gbc.gridy = 4;
        gbc.gridwidth = 2;
        formPanel.add(availLabel, gbc);

        checkBtn = new JButton("Verify Slot Availability");
        checkBtn.setBackground(new Color(70, 130, 180));
        checkBtn.setForeground(Color.WHITE);
        checkBtn.setFont(new Font("Arial", Font.BOLD, 12));
        checkBtn.setFocusPainted(false);
        gbc.gridy = 5;
        formPanel.add(checkBtn, gbc);

        add(formPanel, BorderLayout.CENTER);

        // Reset width limit for main buttons
        gbc.gridwidth = 1;

        // Load data into comboboxes
        loadComboboxData();

        // Prepopulate current date and time
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat tf = new SimpleDateFormat("HH:mm:ss");
        dateField.setText(df.format(new Date()));
        timeField.setText(tf.format(new Date()));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        buttonPanel.setBackground(new Color(245, 248, 255));

        bookBtn = new JButton("Book");
        bookBtn.setPreferredSize(new Dimension(100, 35));
        bookBtn.setBackground(new Color(34, 139, 34));
        bookBtn.setForeground(Color.WHITE);
        bookBtn.setFont(new Font("Arial", Font.BOLD, 14));
        bookBtn.setFocusPainted(false);

        clearBtn = new JButton("Clear");
        clearBtn.setPreferredSize(new Dimension(100, 35));
        clearBtn.setBackground(new Color(220, 220, 220));
        clearBtn.setForeground(Color.BLACK);
        clearBtn.setFont(new Font("Arial", Font.BOLD, 14));
        clearBtn.setFocusPainted(false);

        cancelBtn = new JButton("Cancel");
        cancelBtn.setPreferredSize(new Dimension(100, 35));
        cancelBtn.setBackground(new Color(200, 200, 200));
        cancelBtn.setForeground(Color.BLACK);
        cancelBtn.setFont(new Font("Arial", Font.BOLD, 14));
        cancelBtn.setFocusPainted(false);

        buttonPanel.add(bookBtn);
        buttonPanel.add(clearBtn);
        buttonPanel.add(cancelBtn);

        add(buttonPanel, BorderLayout.SOUTH);

        // Listeners
        checkBtn.addActionListener(e -> checkAvailability());
        bookBtn.addActionListener(e -> bookAppointment());

        // Reset check label on edit
        dateField.addCaretListener(e -> resetAvailabilityLabel());
        timeField.addCaretListener(e -> resetAvailabilityLabel());
        doctorBox.addActionListener(e -> resetAvailabilityLabel());

        clearBtn.addActionListener(e -> {
            if (patientBox.getItemCount() > 0) patientBox.setSelectedIndex(0);
            if (doctorBox.getItemCount() > 0) doctorBox.setSelectedIndex(0);
            dateField.setText(df.format(new Date()));
            timeField.setText(tf.format(new Date()));
            resetAvailabilityLabel();
        });
        cancelBtn.addActionListener(e -> dispose());

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setVisible(true);
    }

    private void loadComboboxData() {
        try {
            patientBox.removeAllItems();
            doctorBox.removeAllItems();

            List<Patient> patients = PatientDAO.getAllPatients();
            for (Patient p : patients) {
                patientBox.addItem(p.getPatientId() + " - " + p.getName());
            }

            List<Doctor> doctors = DoctorDAO.getAllDoctors();
            for (Doctor d : doctors) {
                doctorBox.addItem(d.getDoctorId() + " - " + d.getName() + " (" + d.getSpecialization() + ")");
            }

            if (patients.isEmpty()) {
                patientBox.addItem("No Patients Found");
                bookBtn.setEnabled(false);
                checkBtn.setEnabled(false);
            }
            if (doctors.isEmpty()) {
                doctorBox.addItem("No Doctors Found");
                bookBtn.setEnabled(false);
                checkBtn.setEnabled(false);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading dropdown lists: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void resetAvailabilityLabel() {
        availLabel.setText("Status: Check availability...");
        availLabel.setForeground(Color.DARK_GRAY);
    }

    private boolean checkAvailability() {
        if (doctorBox.getSelectedItem() == null || doctorBox.getSelectedItem().toString().startsWith("No Doctors")) {
            return false;
        }

        String docSel = doctorBox.getSelectedItem().toString();
        String dateValue = dateField.getText().trim();
        String timeValue = timeField.getText().trim();

        int doctorId = Integer.parseInt(docSel.split(" - ")[0]);

        if (dateValue.isEmpty() || !dateValue.matches("\\d{4}-\\d{2}-\\d{2}") ||
            timeValue.isEmpty() || !timeValue.matches("\\d{2}:\\d{2}:\\d{2}")) {
            availLabel.setText("Status: Invalid date/time formatting.");
            availLabel.setForeground(Color.RED);
            return false;
        }

        boolean available = AppointmentDAO.isDoctorAvailable(doctorId, dateValue, timeValue);
        if (available) {
            availLabel.setText("Status: Doctor is AVAILABLE at this time.");
            availLabel.setForeground(new Color(34, 139, 34)); // Green
            return true;
        } else {
            availLabel.setText("Status: CONFLICT! Doctor is already booked.");
            availLabel.setForeground(new Color(178, 34, 34)); // Red
            return false;
        }
    }

    private void bookAppointment() {
        dateField.setBorder(UIManager.getBorder("TextField.border"));
        timeField.setBorder(UIManager.getBorder("TextField.border"));

        if (patientBox.getSelectedItem() == null || patientBox.getSelectedItem().toString().startsWith("No Patients")) {
            JOptionPane.showMessageDialog(this, "Please select a valid patient.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (doctorBox.getSelectedItem() == null || doctorBox.getSelectedItem().toString().startsWith("No Doctors")) {
            JOptionPane.showMessageDialog(this, "Please select a valid doctor.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String patientSel = patientBox.getSelectedItem().toString();
        String doctorSel = doctorBox.getSelectedItem().toString();
        String dateValue = dateField.getText().trim();
        String timeValue = timeField.getText().trim();

        int patientId = Integer.parseInt(patientSel.split(" - ")[0]);
        int doctorId = Integer.parseInt(doctorSel.split(" - ")[0]);

        boolean valid = true;

        if (dateValue.isEmpty() || !dateValue.matches("\\d{4}-\\d{2}-\\d{2}")) {
            dateField.setBorder(BorderFactory.createLineBorder(Color.RED));
            valid = false;
        }

        if (timeValue.isEmpty() || !timeValue.matches("\\d{2}:\\d{2}:\\d{2}")) {
            timeField.setBorder(BorderFactory.createLineBorder(Color.RED));
            valid = false;
        }

        if (!valid) {
            JOptionPane.showMessageDialog(this, "Please check date (YYYY-MM-DD) and time (HH:MM:SS) formats.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Perform conflict double-check
        boolean available = AppointmentDAO.isDoctorAvailable(doctorId, dateValue, timeValue);
        if (!available) {
            JOptionPane.showMessageDialog(this, "Doctor scheduling conflict! This slot is already booked.", "Scheduling Conflict", JOptionPane.WARNING_MESSAGE);
            availLabel.setText("Status: CONFLICT! Doctor is already booked.");
            availLabel.setForeground(new Color(178, 34, 34));
            return;
        }

        try {
            boolean success = AppointmentDAO.bookAppointment(patientId, doctorId, dateValue, timeValue);
            if (success) {
                // Fetch info for auditing and messaging
                Patient patObj = PatientDAO.getPatientById(patientId);
                Doctor docObj = DoctorDAO.getDoctorById(doctorId);
                String patPhone = (patObj != null) ? patObj.getPhone() : "N/A";
                String docName = (docObj != null) ? docObj.getName() : "Doctor ID " + doctorId;
                String patName = (patObj != null) ? patObj.getName() : "Patient ID " + patientId;

                AuditDAO.log(null, "Booked appointment for Patient: " + patName + " with Doctor: " + docName + " on " + dateValue + " at " + timeValue);
                NotificationSimulator.sendNotification(patPhone, "Hi " + patName + ", your appointment with " + docName + " is booked for " + dateValue + " at " + timeValue + ". Status: Confirmed.");

                JOptionPane.showMessageDialog(this, "Appointment Booked Successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                if (refreshCallback != null) {
                    refreshCallback.run();
                }
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to book appointment. DB conflict check failed.", "Conflict", JOptionPane.WARNING_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Database error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}