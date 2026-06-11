package gui;

import javax.swing.*;
import java.awt.*;
import database.PatientDAO;
import models.Patient;

public class EditPatientForm extends JDialog {

    JTextField nameField, ageField, phoneField, diseaseField, addressField;
    JComboBox<String> genderBox;
    JButton updateBtn, cancelBtn;
    int patientId;
    Runnable refreshCallback;

    public EditPatientForm(Window parent, int patientId, Runnable refreshCallback) {
        super(parent, "Edit Patient Information", ModalityType.APPLICATION_MODAL);

        this.patientId = patientId;
        this.refreshCallback = refreshCallback;

        setSize(450, 520);
        setLayout(new BorderLayout(10, 10));
        setLocationRelativeTo(parent);
        getContentPane().setBackground(new Color(245, 248, 255));

        JLabel title = new JLabel("Edit Patient Information", SwingConstants.CENTER);
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
        JLabel nameLabel = new JLabel("Name");
        nameLabel.setFont(new Font("Arial", Font.BOLD, 14));
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(nameLabel, gbc);

        nameField = new JTextField(15);
        gbc.gridx = 1;
        formPanel.add(nameField, gbc);

        // Age
        JLabel ageLabel = new JLabel("Age");
        ageLabel.setFont(new Font("Arial", Font.BOLD, 14));
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(ageLabel, gbc);

        ageField = new JTextField(15);
        gbc.gridx = 1;
        formPanel.add(ageField, gbc);

        // Gender
        JLabel genderLabel = new JLabel("Gender");
        genderLabel.setFont(new Font("Arial", Font.BOLD, 14));
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(genderLabel, gbc);

        String[] genders = {"Male", "Female", "Other"};
        genderBox = new JComboBox<>(genders);
        gbc.gridx = 1;
        formPanel.add(genderBox, gbc);

        // Phone
        JLabel phoneLabel = new JLabel("Phone");
        phoneLabel.setFont(new Font("Arial", Font.BOLD, 14));
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(phoneLabel, gbc);

        phoneField = new JTextField(15);
        gbc.gridx = 1;
        formPanel.add(phoneField, gbc);

        // Disease
        JLabel diseaseLabel = new JLabel("Disease");
        diseaseLabel.setFont(new Font("Arial", Font.BOLD, 14));
        gbc.gridx = 0; gbc.gridy = 4;
        formPanel.add(diseaseLabel, gbc);

        diseaseField = new JTextField(15);
        gbc.gridx = 1;
        formPanel.add(diseaseField, gbc);

        // Address
        JLabel addressLabel = new JLabel("Address");
        addressLabel.setFont(new Font("Arial", Font.BOLD, 14));
        gbc.gridx = 0; gbc.gridy = 5;
        formPanel.add(addressLabel, gbc);

        addressField = new JTextField(15);
        gbc.gridx = 1;
        formPanel.add(addressField, gbc);

        add(formPanel, BorderLayout.CENTER);

        updateBtn = new JButton("Update");
        updateBtn.setPreferredSize(new Dimension(110, 35));
        updateBtn.setBackground(new Color(70, 130, 180));
        updateBtn.setForeground(Color.WHITE);
        updateBtn.setFont(new Font("Arial", Font.BOLD, 14));
        updateBtn.setFocusPainted(false);

        cancelBtn = new JButton("Cancel");
        cancelBtn.setPreferredSize(new Dimension(110, 35));
        cancelBtn.setBackground(new Color(200, 200, 200));
        cancelBtn.setForeground(Color.BLACK);
        cancelBtn.setFont(new Font("Arial", Font.BOLD, 14));
        cancelBtn.setFocusPainted(false);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 15));
        buttonPanel.setBackground(new Color(245, 248, 255));
        buttonPanel.add(updateBtn);
        buttonPanel.add(cancelBtn);
        add(buttonPanel, BorderLayout.SOUTH);

        loadPatientData();

        updateBtn.addActionListener(e -> updatePatient());
        cancelBtn.addActionListener(e -> dispose());

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setVisible(true);
    }

    private void loadPatientData() {
        Patient p = PatientDAO.getPatientById(patientId);
        if (p != null) {
            nameField.setText(p.getName());
            ageField.setText(String.valueOf(p.getAge()));
            
            // Match gender combobox
            String gender = p.getGender();
            if ("Male".equalsIgnoreCase(gender)) genderBox.setSelectedIndex(0);
            else if ("Female".equalsIgnoreCase(gender)) genderBox.setSelectedIndex(1);
            else genderBox.setSelectedIndex(2);

            phoneField.setText(p.getPhone());
            diseaseField.setText(p.getDisease());
            addressField.setText(p.getAddress());
        } else {
            JOptionPane.showMessageDialog(this, "Patient not found.", "Error", JOptionPane.ERROR_MESSAGE);
            dispose();
        }
    }

    private void updatePatient() {
        nameField.setBorder(UIManager.getBorder("TextField.border"));
        ageField.setBorder(UIManager.getBorder("TextField.border"));
        phoneField.setBorder(UIManager.getBorder("TextField.border"));
        diseaseField.setBorder(UIManager.getBorder("TextField.border"));
        addressField.setBorder(UIManager.getBorder("TextField.border"));

        String name = nameField.getText().trim();
        String ageStr = ageField.getText().trim();
        String gender = genderBox.getSelectedItem().toString();
        String phone = phoneField.getText().trim();
        String disease = diseaseField.getText().trim();
        String address = addressField.getText().trim();

        boolean valid = true;

        if (name.isEmpty()) {
            nameField.setBorder(BorderFactory.createLineBorder(Color.RED));
            valid = false;
        }

        int age = 0;
        try {
            age = Integer.parseInt(ageStr);
            if (age <= 0 || age > 150) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            ageField.setBorder(BorderFactory.createLineBorder(Color.RED));
            valid = false;
        }

        if (phone.isEmpty() || !phone.matches("\\d{10}")) {
            phoneField.setBorder(BorderFactory.createLineBorder(Color.RED));
            valid = false;
        }

        if (disease.isEmpty()) {
            diseaseField.setBorder(BorderFactory.createLineBorder(Color.RED));
            valid = false;
        }

        if (address.isEmpty()) {
            addressField.setBorder(BorderFactory.createLineBorder(Color.RED));
            valid = false;
        }

        if (!valid) {
            JOptionPane.showMessageDialog(this, "Please fix the highlighted fields. Phone must be 10 digits.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            Patient existing = PatientDAO.getPatientById(patientId);
            String visited = (existing != null) ? existing.getVisited() : "NO";

            Patient patient = new Patient(patientId, name, age, gender, phone, disease, address, visited);
            boolean success = PatientDAO.updatePatient(patient);

            if (success) {
                database.AuditDAO.log(null, "Updated details of patient: " + name + " (ID " + patientId + ")");
                JOptionPane.showMessageDialog(this, "Patient Updated Successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
                if (refreshCallback != null) {
                    refreshCallback.run();
                }
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to update patient details.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Database error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
