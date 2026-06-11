package gui;

import javax.swing.*;
import java.awt.*;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

public class DBConfigDialog extends JDialog {

    JTextField hostField, portField, dbNameField, userField;
    JPasswordField passField;
    JButton testBtn, saveBtn, cancelBtn;
    boolean isConfigured = false;

    public DBConfigDialog(Frame owner) {
        super(owner, "Database Connection Settings", true);
        setSize(400, 360);
        setLayout(new BorderLayout(10, 10));
        setLocationRelativeTo(owner);
        getContentPane().setBackground(new Color(245, 248, 255));

        JLabel title = new JLabel("Database Setup Required", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 18));
        title.setBorder(BorderFactory.createEmptyBorder(15, 10, 10, 10));
        add(title, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(new Color(245, 248, 255));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 10, 6, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        // Host
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Host:"), gbc);
        hostField = new JTextField("localhost", 12);
        gbc.gridx = 1;
        formPanel.add(hostField, gbc);

        // Port
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Port:"), gbc);
        portField = new JTextField("3306", 12);
        gbc.gridx = 1;
        formPanel.add(portField, gbc);

        // DB Name
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Database Name:"), gbc);
        dbNameField = new JTextField("hospital_db", 12);
        gbc.gridx = 1;
        formPanel.add(dbNameField, gbc);

        // User
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Username:"), gbc);
        userField = new JTextField("root", 12);
        gbc.gridx = 1;
        formPanel.add(userField, gbc);

        // Password
        gbc.gridx = 0; gbc.gridy = 4;
        formPanel.add(new JLabel("Password:"), gbc);
        passField = new JPasswordField("Aaron@2007", 12);
        gbc.gridx = 1;
        formPanel.add(passField, gbc);

        add(formPanel, BorderLayout.CENTER);

        // Buttons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        btnPanel.setBackground(new Color(245, 248, 255));

        testBtn = new JButton("Test Connection");
        saveBtn = new JButton("Save");
        cancelBtn = new JButton("Cancel");

        testBtn.setBackground(new Color(70, 130, 180));
        testBtn.setForeground(Color.WHITE);
        saveBtn.setBackground(new Color(34, 139, 34));
        saveBtn.setForeground(Color.WHITE);

        btnPanel.add(testBtn);
        btnPanel.add(saveBtn);
        btnPanel.add(cancelBtn);
        add(btnPanel, BorderLayout.SOUTH);

        testBtn.addActionListener(e -> testConnection(true));
        saveBtn.addActionListener(e -> saveConfig());
        cancelBtn.addActionListener(e -> dispose());

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

    private boolean testConnection(boolean showSuccessMsg) {
        String url = "jdbc:mysql://" + hostField.getText().trim() + ":" + portField.getText().trim() + "/" + dbNameField.getText().trim();
        String user = userField.getText().trim();
        String pass = String.valueOf(passField.getPassword()).trim();

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection conn = DriverManager.getConnection(url, user, pass)) {
                if (showSuccessMsg) {
                    JOptionPane.showMessageDialog(this, "Connection Successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
                }
                return true;
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Connection Failed:\n" + ex.getMessage(), "Connection Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    private void saveConfig() {
        if (!testConnection(false)) {
            int option = JOptionPane.showConfirmDialog(this, "Connection failed. Save settings anyway?", "Warning", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (option != JOptionPane.YES_OPTION) {
                return;
            }
        }

        Properties props = new Properties();
        props.setProperty("db.host", hostField.getText().trim());
        props.setProperty("db.port", portField.getText().trim());
        props.setProperty("db.name", dbNameField.getText().trim());
        props.setProperty("db.user", userField.getText().trim());
        props.setProperty("db.pass", String.valueOf(passField.getPassword()).trim());

        try (FileOutputStream out = new FileOutputStream("config.properties")) {
            props.store(out, "Hospital Management System DB Configuration");
            JOptionPane.showMessageDialog(this, "Database Configuration Saved Successfully!", "Saved", JOptionPane.INFORMATION_MESSAGE);
            isConfigured = true;
            dispose();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Failed to save configuration properties: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isConfigured() {
        return isConfigured;
    }
}
