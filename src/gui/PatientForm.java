package gui;

import javax.swing.*;

public class PatientForm extends JFrame {

    JTextField nameField, ageField, genderField,
            phoneField, diseaseField, addressField;

    JButton saveBtn;

    public PatientForm() {

        setTitle("Patient Registration");
        setSize(400, 500);
        setLayout(null);

        JLabel name = new JLabel("Name");
        name.setBounds(50, 30, 100, 30);
        add(name);

        nameField = new JTextField();
        nameField.setBounds(150, 30, 150, 30);
        add(nameField);

        JLabel age = new JLabel("Age");
        age.setBounds(50, 80, 100, 30);
        add(age);

        ageField = new JTextField();
        ageField.setBounds(150, 80, 150, 30);
        add(ageField);

        JLabel gender = new JLabel("Gender");
        gender.setBounds(50, 130, 100, 30);
        add(gender);

        genderField = new JTextField();
        genderField.setBounds(150, 130, 150, 30);
        add(genderField);

        JLabel phone = new JLabel("Phone");
        phone.setBounds(50, 180, 100, 30);
        add(phone);

        phoneField = new JTextField();
        phoneField.setBounds(150, 180, 150, 30);
        add(phoneField);

        JLabel disease = new JLabel("Disease");
        disease.setBounds(50, 230, 100, 30);
        add(disease);

        diseaseField = new JTextField();
        diseaseField.setBounds(150, 230, 150, 30);
        add(diseaseField);

        JLabel address = new JLabel("Address");
        address.setBounds(50, 280, 100, 30);
        add(address);

        addressField = new JTextField();
        addressField.setBounds(150, 280, 150, 30);
        add(addressField);

        saveBtn = new JButton("Save");
        saveBtn.setBounds(130, 350, 120, 40);
        add(saveBtn);

        setVisible(true);
    }
}