package gui;

import javax.swing.*;

public class Dashboard extends JFrame {

    JButton patientBtn, doctorBtn, appointmentBtn, viewBtn;

    public Dashboard() {

        setTitle("Hospital Dashboard");
        setSize(500, 400);
        setLayout(null);

        patientBtn = new JButton("Add Patient");
        patientBtn.setBounds(150, 50, 200, 40);
        add(patientBtn);

        doctorBtn = new JButton("Add Doctor");
        doctorBtn.setBounds(150, 110, 200, 40);
        add(doctorBtn);

        appointmentBtn = new JButton("Book Appointment");
        appointmentBtn.setBounds(150, 170, 200, 40);
        add(appointmentBtn);

        viewBtn = new JButton("View Patients");
        viewBtn.setBounds(150, 230, 200, 40);
        add(viewBtn);

        patientBtn.addActionListener(e -> new PatientForm());

        doctorBtn.addActionListener(e -> new DoctorForm());

        appointmentBtn.addActionListener(e -> new AppointmentForm());

        viewBtn.addActionListener(e -> new ViewPatient());

        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}