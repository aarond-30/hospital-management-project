package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import database.LoginDAO;

public class LoginForm extends JFrame {

    JLabel userLabel, passLabel;

    JTextField userField;

    JPasswordField passField;

    JButton loginBtn;

    public LoginForm() {

        setTitle("Hospital Login");

        setSize(450, 350);

        setLayout(null);

        setLocationRelativeTo(null);

        getContentPane().setBackground(
                new Color(230, 240, 255));

        JLabel title = new JLabel(
                "Hospital Management System");

        title.setFont(
                new Font("Arial", Font.BOLD, 22));

        title.setBounds(40, 10, 350, 40);

        add(title);

        userLabel = new JLabel("Username:");

        userLabel.setFont(
                new Font("Arial", Font.BOLD, 14));

        userLabel.setBounds(50, 80, 100, 30);

        add(userLabel);

        userField = new JTextField();

        userField.setBounds(170, 80, 180, 30);

        add(userField);

        passLabel = new JLabel("Password:");

        passLabel.setFont(
                new Font("Arial", Font.BOLD, 14));

        passLabel.setBounds(50, 140, 100, 30);

        add(passLabel);

        passField = new JPasswordField();

        passField.setBounds(170, 140, 180, 30);

        add(passField);

        loginBtn = new JButton("Login");

        loginBtn.setBounds(140, 220, 140, 40);

        loginBtn.setBackground(
                new Color(70, 130, 180));

        loginBtn.setForeground(Color.WHITE);

        loginBtn.setFont(
                new Font("Arial", Font.BOLD, 15));

        loginBtn.setFocusPainted(false);

        add(loginBtn);

        loginBtn.addActionListener(
                new ActionListener() {

                    public void actionPerformed(
                            ActionEvent e) {

                        String username = userField.getText();

                        String password = String.valueOf(
                                passField.getPassword());

                        if (username.equals("admin")
                                && password.equals("admin123")) {

                            LoginDAO.login(
                                    username,
                                    password);

                            JOptionPane.showMessageDialog(
                                    null,
                                    "Login Successful");

                            new Dashboard();

                            dispose();

                        } else {

                            JOptionPane.showMessageDialog(
                                    null,
                                    "Invalid Username or Password");
                        }
                    }
                });

        setVisible(true);

        setDefaultCloseOperation(
                JFrame.EXIT_ON_CLOSE);
    }

    public static void main(String[] args) {

        new LoginForm();
    }
}