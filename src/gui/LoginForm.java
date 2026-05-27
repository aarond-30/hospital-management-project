package gui;

import javax.swing.*;
import java.awt.event.*;

public class LoginForm extends JFrame {

    JLabel userLabel, passLabel;
    JTextField userField;
    JPasswordField passField;
    JButton loginBtn;

    public LoginForm() {

        setTitle("Hospital Login");
        setSize(400, 300);
        setLayout(null);

        userLabel = new JLabel("Username:");
        userLabel.setBounds(50, 50, 100, 30);
        add(userLabel);

        userField = new JTextField();
        userField.setBounds(150, 50, 150, 30);
        add(userField);

        passLabel = new JLabel("Password:");
        passLabel.setBounds(50, 100, 100, 30);
        add(passLabel);

        passField = new JPasswordField();
        passField.setBounds(150, 100, 150, 30);
        add(passField);

        loginBtn = new JButton("Login");
        loginBtn.setBounds(130, 170, 120, 40);
        add(loginBtn);

        loginBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                new Dashboard();
                dispose();
            }
        });

        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public static void main(String[] args) {

        new LoginForm();
    }
}