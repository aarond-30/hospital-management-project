package gui;

import javax.swing.*;
import java.awt.*;
import database.DoctorDAO;

public class DoctorForm extends JDialog {

    JTextField nameField,
            specializationField,
            phoneField,
            roomField;

    JButton saveBtn, cancelBtn;
    Runnable refreshCallback;

    public DoctorForm(Window parent, Runnable refreshCallback) {
        super(parent, "Doctor Registration", ModalityType.APPLICATION_MODAL);
        this.refreshCallback = refreshCallback;

        setSize(450, 420);
        setLayout(new BorderLayout(10, 10));
        setLocationRelativeTo(parent);
        getContentPane().setBackground(new Color(245, 248, 255));

        JLabel title = new JLabel("Doctor Registration", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 22));
        title.setBorder(BorderFactory.createEmptyBorder(20, 10, 10, 10));
        add(title, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(new Color(245, 248, 255));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        // Name
        JLabel name = new JLabel("Doctor Name");
        name.setFont(new Font("Arial", Font.BOLD, 14));
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(name, gbc);

        nameField = new JTextField(15);
        gbc.gridx = 1;
        formPanel.add(nameField, gbc);

        // Specialization
        JLabel specialization = new JLabel("Specialization");
        specialization.setFont(new Font("Arial", Font.BOLD, 14));
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(specialization, gbc);

        specializationField = new JTextField(15);
        gbc.gridx = 1;
        formPanel.add(specializationField, gbc);

        // Phone
        JLabel phone = new JLabel("Phone");
        phone.setFont(new Font("Arial", Font.BOLD, 14));
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(phone, gbc);

        phoneField = new JTextField(15);
        gbc.gridx = 1;
        formPanel.add(phoneField, gbc);

        // Room
        JLabel room = new JLabel("Room No");
        room.setFont(new Font("Arial", Font.BOLD, 14));
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(room, gbc);

        roomField = new JTextField(15);
        gbc.gridx = 1;
        formPanel.add(roomField, gbc);

        add(formPanel, BorderLayout.CENTER);

        // Buttons
        saveBtn = new JButton("Save");
        saveBtn.setPreferredSize(new Dimension(110, 35));
        saveBtn.setBackground(new Color(70, 130, 180));
        saveBtn.setForeground(Color.WHITE);
        saveBtn.setFont(new Font("Arial", Font.BOLD, 14));
        saveBtn.setFocusPainted(false);

        cancelBtn = new JButton("Cancel");
        cancelBtn.setPreferredSize(new Dimension(110, 35));
        cancelBtn.setBackground(new Color(200, 200, 200));
        cancelBtn.setForeground(Color.BLACK);
        cancelBtn.setFont(new Font("Arial", Font.BOLD, 14));
        cancelBtn.setFocusPainted(false);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 15));
        buttonPanel.setBackground(new Color(245, 248, 255));
        buttonPanel.add(saveBtn);
        buttonPanel.add(cancelBtn);
        add(buttonPanel, BorderLayout.SOUTH);

        saveBtn.addActionListener(e -> saveDoctor());
        cancelBtn.addActionListener(e -> dispose());

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setVisible(true);
    }

    private void saveDoctor() {
        nameField.setBorder(UIManager.getBorder("TextField.border"));
        specializationField.setBorder(UIManager.getBorder("TextField.border"));
        phoneField.setBorder(UIManager.getBorder("TextField.border"));
        roomField.setBorder(UIManager.getBorder("TextField.border"));

        String nameValue = nameField.getText().trim();
        String specializationValue = specializationField.getText().trim();
        String phoneValue = phoneField.getText().trim();
        String roomValue = roomField.getText().trim();

        boolean valid = true;

        if (nameValue.isEmpty()) {
            nameField.setBorder(BorderFactory.createLineBorder(Color.RED));
            valid = false;
        }

        if (specializationValue.isEmpty()) {
            specializationField.setBorder(BorderFactory.createLineBorder(Color.RED));
            valid = false;
        }

        if (phoneValue.isEmpty() || !phoneValue.matches("\\d{10}")) {
            phoneField.setBorder(BorderFactory.createLineBorder(Color.RED));
            valid = false;
        }

        if (roomValue.isEmpty()) {
            roomField.setBorder(BorderFactory.createLineBorder(Color.RED));
            valid = false;
        }

        if (!valid) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields. Phone must be 10 digits.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            DoctorDAO.insertDoctor(nameValue, specializationValue, phoneValue, roomValue);
            database.AuditDAO.log(null, "Registered new doctor: " + nameValue);
            JOptionPane.showMessageDialog(this, "Doctor Added Successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
            if (refreshCallback != null) {
                refreshCallback.run();
            }
            dispose();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error inserting doctor: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}