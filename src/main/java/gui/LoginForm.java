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
        setTitle("Hospital HMS - Login");
        setSize(420, 360);
        setLayout(new BorderLayout(10, 10));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Warm visual background
        getContentPane().setBackground(new Color(245, 248, 255));

        JLabel title = new JLabel("Hospital Management System", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 22));
        title.setForeground(new Color(44, 62, 80));
        title.setBorder(BorderFactory.createEmptyBorder(25, 10, 10, 10));
        add(title, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(new Color(245, 248, 255));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;

        userLabel = new JLabel("Username:");
        userLabel.setFont(new Font("Arial", Font.BOLD, 14));
        userLabel.setForeground(new Color(44, 62, 80));
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(userLabel, gbc);

        userField = new JTextField(15);
        userField.setFont(new Font("Arial", Font.PLAIN, 14));
        gbc.gridx = 1;
        formPanel.add(userField, gbc);

        passLabel = new JLabel("Password:");
        passLabel.setFont(new Font("Arial", Font.BOLD, 14));
        passLabel.setForeground(new Color(44, 62, 80));
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(passLabel, gbc);

        passField = new JPasswordField(15);
        passField.setFont(new Font("Arial", Font.PLAIN, 14));
        gbc.gridx = 1;
        formPanel.add(passField, gbc);

        add(formPanel, BorderLayout.CENTER);

        loginBtn = new JButton("Sign In");
        loginBtn.setBackground(new Color(52, 152, 219));
        loginBtn.setForeground(Color.WHITE);
        loginBtn.setFont(new Font("Arial", Font.BOLD, 15));
        loginBtn.setFocusPainted(false);
        loginBtn.setPreferredSize(new Dimension(160, 42));

        JPanel footerPanel = new JPanel();
        footerPanel.setLayout(new BoxLayout(footerPanel, BoxLayout.Y_AXIS));
        footerPanel.setBackground(new Color(245, 248, 255));
        footerPanel.setBorder(BorderFactory.createEmptyBorder(0, 15, 12, 15));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(new Color(245, 248, 255));
        buttonPanel.add(loginBtn);
        
        JLabel collegeLabel = new JLabel("Atria Institute of Technology | Dept of ISE", SwingConstants.CENTER);
        collegeLabel.setFont(new Font("Arial", Font.BOLD, 11));
        collegeLabel.setForeground(new Color(127, 140, 141));
        collegeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel teamLabel = new JLabel("Aaron (1AT24IS002) | Nithin (1AT24IS141) | Ragavendra (1AT23IS126) | Sec D", SwingConstants.CENTER);
        teamLabel.setFont(new Font("Arial", Font.PLAIN, 10));
        teamLabel.setForeground(new Color(127, 140, 141));
        teamLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        footerPanel.add(buttonPanel);
        footerPanel.add(Box.createVerticalStrut(2));
        footerPanel.add(collegeLabel);
        footerPanel.add(Box.createVerticalStrut(2));
        footerPanel.add(teamLabel);

        add(footerPanel, BorderLayout.SOUTH);

        loginBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String username = userField.getText().trim();
                String password = String.valueOf(passField.getPassword()).trim();

                if (username.isEmpty() || password.isEmpty()) {
                    JOptionPane.showMessageDialog(LoginForm.this, "Please enter both username and password.", "Login Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Check Admin
                if (username.equals("admin") && password.equals("admin123")) {
                    AppSession.isAdmin = true;
                    LoginDAO.login(username, password);
                    database.AuditDAO.log(username, "User logged in (Administrator)");
                    JOptionPane.showMessageDialog(LoginForm.this, "Login Successful (Administrator)");
                    new Dashboard();
                    dispose();
                } else {
                    // Check database-backed credentials
                    boolean authenticated = LoginDAO.login(username, password);
                    if (authenticated) {
                        AppSession.isAdmin = false; // Staff/User login
                        database.AuditDAO.log(username, "User logged in (Staff)");
                        JOptionPane.showMessageDialog(LoginForm.this, "Login Successful");
                        new Dashboard();
                        dispose();
                    } else {
                        database.AuditDAO.log(username, "Failed login attempt");
                        JOptionPane.showMessageDialog(LoginForm.this, "Invalid Username or Password", "Access Denied", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        setVisible(true);
    }

    public static void main(String[] args) {
        try {
            com.formdev.flatlaf.FlatLightLaf.setup();
        } catch (Exception ex) {
            System.err.println("Failed to initialize FlatLaf Look and Feel: " + ex.getMessage());
        }
        new LoginForm();
    }
}