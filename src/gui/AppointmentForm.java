package gui;

import javax.swing.*;
import java.awt.event.*;

public class AppointmentForm extends JFrame {

    JLabel patientLabel;
    JLabel doctorLabel;
    JLabel dateLabel;
    JLabel timeLabel;
    JLabel statusLabel;

    JTextField patientIdField;
    JTextField doctorIdField;
    JTextField dateField;
    JTextField timeField;

    JComboBox<String> statusBox;

    JButton bookBtn;
    JButton clearBtn;
    JButton backBtn;

    public AppointmentForm() {

        setTitle("Appointment Booking");
        setSize(450, 500);
        setLayout(null);

        // ---------------- PATIENT ID ----------------

        patientLabel = new JLabel("Patient ID");
        patientLabel.setBounds(50, 40, 120, 30);
        add(patientLabel);

        patientIdField = new JTextField();
        patientIdField.setBounds(180, 40, 180, 30);
        add(patientIdField);

        // ---------------- DOCTOR ID ----------------

        doctorLabel = new JLabel("Doctor ID");
        doctorLabel.setBounds(50, 90, 120, 30);
        add(doctorLabel);

        doctorIdField = new JTextField();
        doctorIdField.setBounds(180, 90, 180, 30);
        add(doctorIdField);

        // ---------------- DATE ----------------

        dateLabel = new JLabel("Appointment Date");
        dateLabel.setBounds(50, 140, 120, 30);
        add(dateLabel);

        dateField = new JTextField();
        dateField.setBounds(180, 140, 180, 30);
        add(dateField);

        // ---------------- TIME ----------------

        timeLabel = new JLabel("Appointment Time");
        timeLabel.setBounds(50, 190, 120, 30);
        add(timeLabel);

        timeField = new JTextField();
        timeField.setBounds(180, 190, 180, 30);
        add(timeField);

        // ---------------- CONSULTATION STATUS ----------------

        statusLabel = new JLabel("Consultation Status");
        statusLabel.setBounds(50, 240, 150, 30);
        add(statusLabel);

        String[] status = {
                "Consulted",
                "Not Consulted"
        };

        statusBox = new JComboBox<>(status);
        statusBox.setBounds(180, 240, 180, 30);
        add(statusBox);

        // ---------------- BOOK BUTTON ----------------

        bookBtn = new JButton("Book");
        bookBtn.setBounds(40, 330, 100, 40);
        add(bookBtn);

        // ---------------- CLEAR BUTTON ----------------

        clearBtn = new JButton("Clear");
        clearBtn.setBounds(160, 330, 100, 40);
        add(clearBtn);

        // ---------------- BACK BUTTON ----------------

        backBtn = new JButton("Back");
        backBtn.setBounds(280, 330, 100, 40);
        add(backBtn);

        // ====================================================
        // BOOK BUTTON ACTION
        // ====================================================

        bookBtn.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {

                String patientId =
                        patientIdField.getText();

                String doctorId =
                        doctorIdField.getText();

                String date =
                        dateField.getText();

                String time =
                        timeField.getText();

                String consultationStatus =
                        statusBox.getSelectedItem().toString();

                JOptionPane.showMessageDialog(
                        null,

                        "Appointment Booked Successfully\n\n"

                        + "Patient ID : " + patientId + "\n"

                        + "Doctor ID : " + doctorId + "\n"

                        + "Date : " + date + "\n"

                        + "Time : " + time + "\n"

                        + "Status : " + consultationStatus
                );
            }
        });

        // ====================================================
        // CLEAR BUTTON ACTION
        // ====================================================

        clearBtn.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {

                patientIdField.setText("");

                doctorIdField.setText("");

                dateField.setText("");

                timeField.setText("");

                statusBox.setSelectedIndex(0);
            }
        });

        // ====================================================
        // BACK BUTTON ACTION
        // ====================================================

        backBtn.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {

                dispose();
            }
        });

        // ---------------- FRAME SETTINGS ----------------

        setVisible(true);

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    // ====================================================
    // MAIN METHOD
    // ====================================================

    public static void main(String[] args) {

        new AppointmentForm();
    }
}