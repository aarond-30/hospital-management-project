package gui;

import javax.swing.*;
import java.awt.*;

import database.PatientDAO;

public class PatientForm extends JFrame {

    JTextField nameField,
            ageField,
            genderField,
            phoneField,
            diseaseField,
            addressField;

    JButton saveBtn;

    public PatientForm() {

        setTitle("Patient Registration");

        setSize(450, 550);

        setLayout(null);

        setLocationRelativeTo(null);

        getContentPane().setBackground(
                new Color(230, 240, 255));

        JLabel title = new JLabel(
                "Patient Registration");

        title.setFont(
                new Font("Arial", Font.BOLD, 22));

        title.setBounds(90, 10, 300, 30);

        add(title);

        JLabel name = new JLabel("Name");

        name.setFont(
                new Font("Arial", Font.BOLD, 14));

        name.setBounds(50, 70, 100, 30);

        add(name);

        nameField = new JTextField();

        nameField.setBounds(170, 70, 180, 30);

        add(nameField);

        JLabel age = new JLabel("Age");

        age.setFont(
                new Font("Arial", Font.BOLD, 14));

        age.setBounds(50, 120, 100, 30);

        add(age);

        ageField = new JTextField();

        ageField.setBounds(170, 120, 180, 30);

        add(ageField);

        JLabel gender = new JLabel("Gender");

        gender.setFont(
                new Font("Arial", Font.BOLD, 14));

        gender.setBounds(50, 170, 100, 30);

        add(gender);

        genderField = new JTextField();

        genderField.setBounds(170, 170, 180, 30);

        add(genderField);

        JLabel phone = new JLabel("Phone");

        phone.setFont(
                new Font("Arial", Font.BOLD, 14));

        phone.setBounds(50, 220, 100, 30);

        add(phone);

        phoneField = new JTextField();

        phoneField.setBounds(170, 220, 180, 30);

        add(phoneField);

        JLabel disease = new JLabel("Disease");

        disease.setFont(
                new Font("Arial", Font.BOLD, 14));

        disease.setBounds(50, 270, 100, 30);

        add(disease);

        diseaseField = new JTextField();

        diseaseField.setBounds(170, 270, 180, 30);

        add(diseaseField);

        JLabel address = new JLabel("Address");

        address.setFont(
                new Font("Arial", Font.BOLD, 14));

        address.setBounds(50, 320, 100, 30);

        add(address);

        addressField = new JTextField();

        addressField.setBounds(170, 320, 180, 30);

        add(addressField);

        saveBtn = new JButton("Save");

        saveBtn.setBounds(140, 400, 140, 40);

        saveBtn.setBackground(
                new Color(70, 130, 180));

        saveBtn.setForeground(Color.WHITE);

        saveBtn.setFont(
                new Font("Arial", Font.BOLD, 15));

        saveBtn.setFocusPainted(false);

        add(saveBtn);

        saveBtn.addActionListener(e -> {

            try {

                String nameValue = nameField.getText();

                int ageValue = Integer.parseInt(
                        ageField.getText());

                String genderValue = genderField.getText();

                String phoneValue = phoneField.getText();

                String diseaseValue = diseaseField.getText();

                String addressValue = addressField.getText();

                PatientDAO.insertPatient(
                        nameValue,
                        ageValue,
                        genderValue,
                        phoneValue,
                        diseaseValue,
                        addressValue);

                JOptionPane.showMessageDialog(
                        null,
                        "Patient Added Successfully");

                nameField.setText("");
                ageField.setText("");
                genderField.setText("");
                phoneField.setText("");
                diseaseField.setText("");
                addressField.setText("");

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

        new PatientForm();
    }
}