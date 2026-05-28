package gui;

import javax.swing.*;
import java.awt.*;

public class Dashboard extends JFrame {

    JButton patientBtn,
            doctorBtn,
            appointmentBtn,
            viewBtn;

    public Dashboard() {

        setTitle("Hospital Dashboard");

        setSize(550, 450);

        setLayout(null);

        setLocationRelativeTo(null);

        getContentPane().setBackground(
                new Color(240, 248, 255));

        JLabel title = new JLabel(
                "Hospital Management System");

        title.setFont(
                new Font("Arial", Font.BOLD, 24));

        title.setBounds(80, 20, 400, 40);

        add(title);

        patientBtn = new JButton("Add Patient");
        patientBtn.setBounds(160, 90, 220, 40);

        doctorBtn = new JButton("Add Doctor");
        doctorBtn.setBounds(160, 150, 220, 40);

        appointmentBtn =
                new JButton("Book Appointment");

        appointmentBtn.setBounds(160, 210, 220, 40);

        viewBtn =
                new JButton("View Patients");

        viewBtn.setBounds(160, 270, 220, 40);

        JButton[] buttons = {
                patientBtn,
                doctorBtn,
                appointmentBtn,
                viewBtn
        };

        for (JButton btn : buttons) {

            btn.setBackground(
                    new Color(70, 130, 180));

            btn.setForeground(Color.WHITE);

            btn.setFont(
                    new Font("Arial", Font.BOLD, 14));

            btn.setFocusPainted(false);

            add(btn);
        }

        patientBtn.addActionListener(e -> {

            new PatientForm();
        });

        doctorBtn.addActionListener(e -> {

            new DoctorForm();
        });

        appointmentBtn.addActionListener(e -> {

            new AppointmentForm();
        });

        viewBtn.addActionListener(e -> {

            new ViewPatient();
        });

        setDefaultCloseOperation(
                JFrame.EXIT_ON_CLOSE);

        setVisible(true);
    }

    public static void main(String[] args) {

        new Dashboard();
    }
}