package gui;

import javax.swing.*;

public class AppointmentForm extends JFrame {

    JTextField patientIdField, doctorIdField,
            dateField, timeField;

    JButton bookBtn;

    public AppointmentForm() {

        setTitle("Book Appointment");
        setSize(400, 350);
        setLayout(null);

        JLabel patient = new JLabel("Patient ID");
        patient.setBounds(50, 40, 100, 30);
        add(patient);

        patientIdField = new JTextField();
        patientIdField.setBounds(170, 40, 150, 30);
        add(patientIdField);

        JLabel doctor = new JLabel("Doctor ID");
        doctor.setBounds(50, 90, 100, 30);
        add(doctor);

        doctorIdField = new JTextField();
        doctorIdField.setBounds(170, 90, 150, 30);
        add(doctorIdField);

        JLabel date = new JLabel("Date");
        date.setBounds(50, 140, 100, 30);
        add(date);

        dateField = new JTextField();
        dateField.setBounds(170, 140, 150, 30);
        add(dateField);

        JLabel time = new JLabel("Time");
        time.setBounds(50, 190, 100, 30);
        add(time);

        timeField = new JTextField();
        timeField.setBounds(170, 190, 150, 30);
        add(timeField);

        bookBtn = new JButton("Book");
        bookBtn.setBounds(120, 250, 120, 40);
        add(bookBtn);

        setVisible(true);
    }
}
