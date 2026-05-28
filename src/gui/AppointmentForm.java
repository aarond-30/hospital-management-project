package gui;

import javax.swing.*;
import java.awt.*;

import database.AppointmentDAO;

public class AppointmentForm extends JFrame {

    JTextField patientIdField, doctorIdField,
            dateField, timeField;

    JButton bookBtn;

    public AppointmentForm() {

        setTitle("Book Appointment");
        setSize(450, 400);
        setLayout(null);
        setLocationRelativeTo(null);

        getContentPane().setBackground(
                new Color(230, 240, 255));

        JLabel title = new JLabel(
                "Book Appointment");

        title.setFont(
                new Font("Arial", Font.BOLD, 22));

        title.setBounds(110, 10, 250, 30);

        add(title);

        JLabel patient = new JLabel("Patient ID");
        patient.setFont(
                new Font("Arial", Font.BOLD, 14));

        patient.setBounds(50, 70, 100, 30);
        add(patient);

        patientIdField = new JTextField();
        patientIdField.setBounds(170, 70, 180, 30);
        add(patientIdField);

        JLabel doctor = new JLabel("Doctor ID");
        doctor.setFont(
                new Font("Arial", Font.BOLD, 14));

        doctor.setBounds(50, 120, 100, 30);
        add(doctor);

        doctorIdField = new JTextField();
        doctorIdField.setBounds(170, 120, 180, 30);
        add(doctorIdField);

        JLabel dateLabel = new JLabel("Date");
        dateLabel.setFont(
                new Font("Arial", Font.BOLD, 14));

        dateLabel.setBounds(50, 170, 100, 30);
        add(dateLabel);

        dateField = new JTextField();
        dateField.setBounds(170, 170, 180, 30);
        add(dateField);

        JLabel timeLabel = new JLabel("Time");
        timeLabel.setFont(
                new Font("Arial", Font.BOLD, 14));

        timeLabel.setBounds(50, 220, 100, 30);
        add(timeLabel);

        timeField = new JTextField();
        timeField.setBounds(170, 220, 180, 30);
        add(timeField);

        bookBtn = new JButton("Book");

        bookBtn.setBounds(140, 290, 140, 40);

        bookBtn.setBackground(
                new Color(70, 130, 180));

        bookBtn.setForeground(Color.WHITE);

        bookBtn.setFont(
                new Font("Arial", Font.BOLD, 15));

        bookBtn.setFocusPainted(false);

        add(bookBtn);

        bookBtn.addActionListener(e -> {

            try {

                int patientId =
                        Integer.parseInt(
                                patientIdField.getText());

                int doctorId =
                        Integer.parseInt(
                                doctorIdField.getText());

                String appointmentDate =
                        dateField.getText();

                String appointmentTime =
                        timeField.getText();

                boolean booked =
                        AppointmentDAO.bookAppointment(
                                patientId,
                                doctorId,
                                appointmentDate,
                                appointmentTime);

                if (booked) {

                    JOptionPane.showMessageDialog(
                            null,
                            "Appointment Booked Successfully");

                    patientIdField.setText("");
                    doctorIdField.setText("");
                    dateField.setText("");
                    timeField.setText("");

                } else {

                    JOptionPane.showMessageDialog(
                            null,
                            "Doctor Already Booked");
                }

            } catch (Exception ex) {

                JOptionPane.showMessageDialog(
                        null,
                        ex.toString());
            }
        });

        setDefaultCloseOperation(
                JFrame.DISPOSE_ON_CLOSE);

        setVisible(true);
    }

    public static void main(String[] args) {

        new AppointmentForm();
    }
}