package gui;

import javax.swing.*;
import java.awt.*;

import database.DoctorDAO;

public class DoctorForm extends JFrame {

    JTextField nameField,
            specializationField,
            phoneField,
            roomField;

    JButton saveBtn;

    public DoctorForm() {

        setTitle("Doctor Registration");

        setSize(450, 450);

        setLayout(null);

        setLocationRelativeTo(null);

        getContentPane().setBackground(
                new Color(230, 240, 255));

        JLabel title = new JLabel(
                "Doctor Registration");

        title.setFont(
                new Font("Arial", Font.BOLD, 22));

        title.setBounds(100, 10, 300, 30);

        add(title);

        JLabel name =
                new JLabel("Doctor Name");

        name.setFont(
                new Font("Arial", Font.BOLD, 14));

        name.setBounds(50, 70, 120, 30);

        add(name);

        nameField = new JTextField();

        nameField.setBounds(180, 70, 180, 30);

        add(nameField);

        JLabel specialization =
                new JLabel("Specialization");

        specialization.setFont(
                new Font("Arial", Font.BOLD, 14));

        specialization.setBounds(50, 130, 120, 30);

        add(specialization);

        specializationField =
                new JTextField();

        specializationField.setBounds(
                180,
                130,
                180,
                30);

        add(specializationField);

        JLabel phone =
                new JLabel("Phone");

        phone.setFont(
                new Font("Arial", Font.BOLD, 14));

        phone.setBounds(50, 190, 120, 30);

        add(phone);

        phoneField = new JTextField();

        phoneField.setBounds(180, 190, 180, 30);

        add(phoneField);

        JLabel room =
                new JLabel("Room No");

        room.setFont(
                new Font("Arial", Font.BOLD, 14));

        room.setBounds(50, 250, 120, 30);

        add(room);

        roomField = new JTextField();

        roomField.setBounds(180, 250, 180, 30);

        add(roomField);

        saveBtn = new JButton("Save");

        saveBtn.setBounds(140, 320, 140, 40);

        saveBtn.setBackground(
                new Color(70, 130, 180));

        saveBtn.setForeground(Color.WHITE);

        saveBtn.setFont(
                new Font("Arial", Font.BOLD, 15));

        saveBtn.setFocusPainted(false);

        add(saveBtn);

        saveBtn.addActionListener(e -> {

            try {

                String nameValue =
                        nameField.getText();

                String specializationValue =
                        specializationField.getText();

                String phoneValue =
                        phoneField.getText();

                String roomValue =
                        roomField.getText();

                DoctorDAO.insertDoctor(
                        nameValue,
                        specializationValue,
                        phoneValue,
                        roomValue);

                JOptionPane.showMessageDialog(
                        null,
                        "Doctor Added Successfully");

                nameField.setText("");
                specializationField.setText("");
                phoneField.setText("");
                roomField.setText("");

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

        new DoctorForm();
    }
}