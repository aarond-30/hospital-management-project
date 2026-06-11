package gui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.util.List;
import java.util.regex.Pattern;
import database.DoctorDAO;
import models.Doctor;

public class DoctorPanel extends JPanel {

    JTable table;
    DefaultTableModel model;
    TableRowSorter<DefaultTableModel> sorter;

    JTextField searchField;
    JButton addBtn, updateRoomBtn, deleteBtn;

    public DoctorPanel() {
        setLayout(new BorderLayout(15, 15));
        setBackground(new Color(245, 248, 255));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // --- TOP SEARCH BAR ---
        JPanel searchBarPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        searchBarPanel.setOpaque(false);

        JLabel searchLabel = new JLabel("Search Doctors:");
        searchLabel.setFont(new Font("Arial", Font.BOLD, 14));
        searchBarPanel.add(searchLabel);

        searchField = new JTextField(15);
        searchField.setFont(new Font("Arial", Font.PLAIN, 14));
        searchBarPanel.add(searchField);

        add(searchBarPanel, BorderLayout.NORTH);

        // --- CENTER TABLE ---
        String[] columns = {"ID", "Name", "Specialization", "Phone", "Room No"};
        model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(model);
        table.setFont(new Font("Arial", Font.PLAIN, 14));
        table.setRowHeight(28);
        table.setShowGrid(true);
        table.setGridColor(new Color(220, 230, 245));
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));

        sorter = new TableRowSorter<>(model);
        table.setRowSorter(sorter);

        JScrollPane pane = new JScrollPane(table);
        pane.setBorder(BorderFactory.createLineBorder(new Color(200, 215, 240)));
        add(pane, BorderLayout.CENTER);

        // --- BOTTOM ACTIONS BAR ---
        JPanel actionsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 25, 5));
        actionsPanel.setOpaque(false);

        addBtn = new JButton("Add Doctor");
        updateRoomBtn = new JButton("Update Room No");
        deleteBtn = new JButton("Delete Doctor");

        JButton[] buttons = {addBtn, updateRoomBtn, deleteBtn};
        Color primaryColor = new Color(70, 130, 180);

        for (JButton btn : buttons) {
            btn.setFont(new Font("Arial", Font.BOLD, 14));
            btn.setForeground(Color.WHITE);
            btn.setFocusPainted(false);
            btn.setPreferredSize(new Dimension(170, 38));
            actionsPanel.add(btn);
        }

        addBtn.setBackground(primaryColor);
        updateRoomBtn.setBackground(new Color(255, 165, 0));
        deleteBtn.setBackground(Color.DARK_GRAY);

        // Visibility rules
        deleteBtn.setVisible(AppSession.isAdmin);

        add(actionsPanel, BorderLayout.SOUTH);

        // --- LISTENERS ---
        searchField.addCaretListener(e -> {
            String text = searchField.getText().trim();
            if (text.isEmpty()) {
                sorter.setRowFilter(null);
            } else {
                sorter.setRowFilter(RowFilter.regexFilter("(?i)" + Pattern.quote(text)));
            }
        });

        addBtn.addActionListener(e -> {
            new DoctorForm(SwingUtilities.getWindowAncestor(this), this::refreshTable);
        });

        updateRoomBtn.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Please select a doctor to update their room.", "Selection Required", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int modelRow = table.convertRowIndexToModel(selectedRow);
            int doctorId = Integer.parseInt(model.getValueAt(modelRow, 0).toString());
            String docName = model.getValueAt(modelRow, 1).toString();
            String currentRoom = model.getValueAt(modelRow, 4).toString();

            String newRoom = JOptionPane.showInputDialog(this, "Enter new room number for " + docName + ":", currentRoom);
            if (newRoom != null && !newRoom.trim().isEmpty()) {
                DoctorDAO.updateDoctorRoom(doctorId, newRoom.trim());
                JOptionPane.showMessageDialog(this, "Room updated successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                refreshTable();
            }
        });

        deleteBtn.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Please select a doctor to delete.", "Selection Required", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int modelRow = table.convertRowIndexToModel(selectedRow);
            int doctorId = Integer.parseInt(model.getValueAt(modelRow, 0).toString());
            String docName = model.getValueAt(modelRow, 1).toString();

            int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete doctor: " + docName + "?", "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (confirm == JOptionPane.YES_OPTION) {
                DoctorDAO.deleteDoctor(doctorId);
                JOptionPane.showMessageDialog(this, "Doctor Deleted Successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                refreshTable();
            }
        });

        // Load data
        refreshTable();
    }

    public void refreshTable() {
        model.setRowCount(0);
        try {
            List<Doctor> list = DoctorDAO.getAllDoctors();
            for (Doctor d : list) {
                Object[] row = {
                        d.getDoctorId(),
                        d.getName(),
                        d.getSpecialization(),
                        d.getPhone(),
                        d.getRoomNo()
                };
                model.addRow(row);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Failed to load doctors: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
