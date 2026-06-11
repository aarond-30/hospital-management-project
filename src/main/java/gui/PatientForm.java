package gui;

import javax.swing.*;
import java.awt.*;
import database.PatientDAO;

public class PatientForm extends JDialog {

    JTextField nameField,
            ageField,
            phoneField,
            diseaseField,
            addressField;
    
    JComboBox<String> genderBox;

    JButton saveBtn, cancelBtn;

    public PatientForm(Window parent) {
        super(parent, "Patient Registration", ModalityType.APPLICATION_MODAL);

        setSize(450, 520);
        setLayout(new BorderLayout(10, 10));
        setLocationRelativeTo(parent);
        getContentPane().setBackground(new Color(245, 248, 255));

        JLabel title = new JLabel("Patient Registration", SwingConstants.CENTER);
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
        JLabel name = new JLabel("Name");
        name.setFont(new Font("Arial", Font.BOLD, 14));
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(name, gbc);

        nameField = new JTextField(15);
        gbc.gridx = 1;
        formPanel.add(nameField, gbc);

        // Age
        JLabel age = new JLabel("Age");
        age.setFont(new Font("Arial", Font.BOLD, 14));
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(age, gbc);

        ageField = new JTextField(15);
        gbc.gridx = 1;
        formPanel.add(ageField, gbc);

        // Gender
        JLabel gender = new JLabel("Gender");
        gender.setFont(new Font("Arial", Font.BOLD, 14));
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(gender, gbc);

        String[] genders = {"Male", "Female", "Other"};
        genderBox = new JComboBox<>(genders);
        gbc.gridx = 1;
        formPanel.add(genderBox, gbc);

        // Phone
        JLabel phone = new JLabel("Phone");
        phone.setFont(new Font("Arial", Font.BOLD, 14));
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(phone, gbc);

        phoneField = new JTextField(15);
        gbc.gridx = 1;
        formPanel.add(phoneField, gbc);

        // Disease
        JLabel disease = new JLabel("Disease");
        disease.setFont(new Font("Arial", Font.BOLD, 14));
        gbc.gridx = 0; gbc.gridy = 4;
        formPanel.add(disease, gbc);

        diseaseField = new JTextField(15);
        gbc.gridx = 1;
        formPanel.add(diseaseField, gbc);

        // Address
        JLabel address = new JLabel("Address");
        address.setFont(new Font("Arial", Font.BOLD, 14));
        gbc.gridx = 0; gbc.gridy = 5;
        formPanel.add(address, gbc);

        addressField = new JTextField(15);
        gbc.gridx = 1;
        formPanel.add(addressField, gbc);

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

        saveBtn.addActionListener(e -> savePatient());
        cancelBtn.addActionListener(e -> dispose());

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setVisible(true);
    }

    private void savePatient() {
        // Validation reset
        nameField.setBorder(UIManager.getBorder("TextField.border"));
        ageField.setBorder(UIManager.getBorder("TextField.border"));
        phoneField.setBorder(UIManager.getBorder("TextField.border"));
        diseaseField.setBorder(UIManager.getBorder("TextField.border"));
        addressField.setBorder(UIManager.getBorder("TextField.border"));

        String nameValue = nameField.getText().trim();
        String ageStr = ageField.getText().trim();
        String genderValue = genderBox.getSelectedItem().toString();
        String phoneValue = phoneField.getText().trim();
        String diseaseValue = diseaseField.getText().trim();
        String addressValue = addressField.getText().trim();

        boolean valid = true;

        if (nameValue.isEmpty()) {
            nameField.setBorder(BorderFactory.createLineBorder(Color.RED));
            valid = false;
        }

        int ageValue = 0;
        try {
            ageValue = Integer.parseInt(ageStr);
            if (ageValue <= 0 || ageValue > 150) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException ex) {
            ageField.setBorder(BorderFactory.createLineBorder(Color.RED));
            valid = false;
        }

        if (phoneValue.isEmpty() || !phoneValue.matches("\\d{10}")) {
            phoneField.setBorder(BorderFactory.createLineBorder(Color.RED));
            valid = false;
        }

        if (diseaseValue.isEmpty()) {
            diseaseField.setBorder(BorderFactory.createLineBorder(Color.RED));
            valid = false;
        }

        if (addressValue.isEmpty()) {
            addressField.setBorder(BorderFactory.createLineBorder(Color.RED));
            valid = false;
        }

        if (!valid) {
            JOptionPane.showMessageDialog(this, "Please fix the highlighted fields. Phone must be 10 digits.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            PatientDAO.insertPatient(nameValue, ageValue, genderValue, phoneValue, diseaseValue, addressValue);
            database.AuditDAO.log(null, "Registered new patient: " + nameValue);
            JOptionPane.showMessageDialog(this, "Patient Added Successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}