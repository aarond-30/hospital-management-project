package gui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import java.awt.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import database.DBConnection;

public class ViewPatient extends JFrame {

    JTable table;

    DefaultTableModel model;

    public ViewPatient() {

        setTitle("Patient Records");

        setSize(1000, 450);

        setLocationRelativeTo(null);

        getContentPane().setBackground(
                new Color(230, 240, 255));

        JLabel title = new JLabel(
                "Patient Records");

        title.setFont(
                new Font("Arial", Font.BOLD, 22));

        title.setBounds(350, 10, 300, 30);

        add(title);

        // UPDATED COLUMNS

        String[] columns = {

                "ID",
                "Name",
                "Age",
                "Gender",
                "Phone",
                "Disease",
                "Address",
                "Doctor Visited",
                "Consultation Status"
        };

        model = new DefaultTableModel(
                columns,
                0);

        table = new JTable(model);

        table.setFont(
                new Font("Arial", Font.PLAIN, 13));

        table.setRowHeight(25);

        JScrollPane pane = new JScrollPane(table);

        pane.setBounds(20, 60, 940, 320);

        add(pane);

        setLayout(null);

        loadPatientData();

        setVisible(true);

        setDefaultCloseOperation(
                JFrame.DISPOSE_ON_CLOSE);
    }

    // LOAD DATA

    public void loadPatientData() {

        try {

            Connection con =
                    DBConnection.getConnection();

            String query =
                    "SELECT * FROM patients";

            PreparedStatement pst =
                    con.prepareStatement(query);

            ResultSet rs =
                    pst.executeQuery();

            while (rs.next()) {

                // CONSULTATION STATUS LOGIC

                String consultationStatus;

                String doctorVisited;

                String disease =
                        rs.getString("disease");

                // SAMPLE LOGIC

                if (disease != null &&
                        !disease.isEmpty()) {

                    consultationStatus =
                            "Consulted";

                    doctorVisited =
                            "YES";
                }

                else {

                    consultationStatus =
                            "Not Consulted";

                    doctorVisited =
                            "NO";
                }

                Object[] row = {

                        rs.getInt("patient_id"),

                        rs.getString("name"),

                        rs.getInt("age"),

                        rs.getString("gender"),

                        rs.getString("phone"),

                        rs.getString("disease"),

                        rs.getString("address"),

                        doctorVisited,

                        consultationStatus
                };

                model.addRow(row);
            }

        }

        catch (Exception e) {

            JOptionPane.showMessageDialog(
                    null,
                    e.toString());
        }
    }

    public static void main(String[] args) {

        new ViewPatient();
    }
}

