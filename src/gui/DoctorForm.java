package gui;

import javax.swing.*;

public class DoctorForm extends JFrame {

    JTextField nameField, specializationField,
            phoneField, roomField;

    JButton saveBtn;

    public DoctorForm() {

        setTitle("Doctor Registration");
        setSize(400, 400);
        setLayout(null);

        JLabel name = new JLabel("Doctor Name");
        name.setBounds(50, 40, 120, 30);
        add(name);

        nameField = new JTextField();
        nameField.setBounds(180, 40, 150, 30);
        add(nameField);

        JLabel specialization = new JLabel("Specialization");
        specialization.setBounds(50, 100, 120, 30);
        add(specialization);

        specializationField = new JTextField();
        specializationField.setBounds(180, 100, 150, 30);
        add(specializationField);

        JLabel phone = new JLabel("Phone");
        phone.setBounds(50, 160, 120, 30);
        add(phone);

        phoneField = new JTextField();
        phoneField.setBounds(180, 160, 150, 30);
        add(phoneField);

        JLabel room = new JLabel("Room No");
        room.setBounds(50, 220, 120, 30);
        add(room);

        roomField = new JTextField();
        roomField.setBounds(180, 220, 150, 30);
        add(roomField);

        saveBtn = new JButton("Save");
        saveBtn.setBounds(120, 290, 120, 40);
        add(saveBtn);

        setVisible(true);
    }
}