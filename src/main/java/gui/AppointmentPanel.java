package gui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.util.List;
import java.util.regex.Pattern;
import database.AppointmentDAO;
import models.Appointment;

public class AppointmentPanel extends JPanel {

    JTable table;
    DefaultTableModel model;
    TableRowSorter<DefaultTableModel> sorter;

    JTextField searchField;
    JComboBox<String> statusFilter;

    JButton bookBtn, completeBtn, deleteBtn;

    public AppointmentPanel() {
        setLayout(new BorderLayout(15, 15));
        setBackground(new Color(245, 248, 255));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // --- TOP SEARCH & FILTER BAR ---
        JPanel searchBarPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        searchBarPanel.setOpaque(false);

        JLabel searchLabel = new JLabel("Search (Patient/Doctor ID):");
        searchLabel.setFont(new Font("Arial", Font.BOLD, 14));
        searchBarPanel.add(searchLabel);

        searchField = new JTextField(12);
        searchField.setFont(new Font("Arial", Font.PLAIN, 14));
        searchBarPanel.add(searchField);

        JLabel filterLabel = new JLabel("Status Filter:");
        filterLabel.setFont(new Font("Arial", Font.BOLD, 14));
        searchBarPanel.add(filterLabel);

        String[] statuses = {"All", "Today", "Pending", "Completed"};
        statusFilter = new JComboBox<>(statuses);
        statusFilter.setFont(new Font("Arial", Font.PLAIN, 13));
        searchBarPanel.add(statusFilter);

        add(searchBarPanel, BorderLayout.NORTH);

        // --- CENTER TABLE ---
        String[] columns = {"ID", "Patient ID", "Doctor ID", "Appointment Date", "Appointment Time", "Status"};
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

        bookBtn = new JButton("Book Appointment");
        completeBtn = new JButton("Mark Complete");
        deleteBtn = new JButton("Delete Appointment");

        JButton[] buttons = {bookBtn, completeBtn, deleteBtn};
        Color primaryColor = new Color(70, 130, 180);
        Color accentYes = new Color(34, 139, 34);
        Color accentNo = new Color(178, 34, 34);

        for (JButton btn : buttons) {
            btn.setFont(new Font("Arial", Font.BOLD, 14));
            btn.setForeground(Color.WHITE);
            btn.setFocusPainted(false);
            btn.setPreferredSize(new Dimension(180, 38));
            actionsPanel.add(btn);
        }

        bookBtn.setBackground(primaryColor);
        completeBtn.setBackground(accentYes);
        deleteBtn.setBackground(accentNo);

        // Deletes are only visible to admins for security
        deleteBtn.setVisible(AppSession.isAdmin);

        add(actionsPanel, BorderLayout.SOUTH);

        // --- LISTENERS ---
        searchField.addCaretListener(e -> applyFilters());
        statusFilter.addActionListener(e -> applyFilters());

        bookBtn.addActionListener(e -> {
            new AppointmentForm(SwingUtilities.getWindowAncestor(this), this::refreshTable);
        });

        completeBtn.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Please select an appointment to mark complete.", "Selection Required", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int modelRow = table.convertRowIndexToModel(selectedRow);
            int appointmentId = Integer.parseInt(model.getValueAt(modelRow, 0).toString());

            boolean success = AppointmentDAO.markComplete(appointmentId);
            if (success) {
                JOptionPane.showMessageDialog(this, "Appointment status updated to Completed.", "Success", JOptionPane.INFORMATION_MESSAGE);
                refreshTable();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to update appointment status.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        deleteBtn.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Please select an appointment to delete.", "Selection Required", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int modelRow = table.convertRowIndexToModel(selectedRow);
            int appointmentId = Integer.parseInt(model.getValueAt(modelRow, 0).toString());

            int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete appointment ID: " + appointmentId + "?", "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (confirm == JOptionPane.YES_OPTION) {
                AppointmentDAO.deleteAppointment(appointmentId);
                JOptionPane.showMessageDialog(this, "Appointment Deleted Successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
                refreshTable();
            }
        });

        // Load data
        refreshTable();
    }

    private void applyFilters() {
        String searchText = searchField.getText().trim();
        String selectedStatus = statusFilter.getSelectedItem().toString();

        // Regex search matching columns 1 (Patient ID) or 2 (Doctor ID)
        RowFilter<DefaultTableModel, Object> rfSearch = RowFilter.regexFilter("(?i)" + Pattern.quote(searchText), 1, 2);

        RowFilter<DefaultTableModel, Object> rfStatus = new RowFilter<>() {
            @Override
            public boolean include(Entry<? extends DefaultTableModel, ?> entry) {
                if ("All".equals(selectedStatus)) return true;
                String statusVal = entry.getStringValue(5); // Status is index 5
                return selectedStatus.equalsIgnoreCase(statusVal);
            }
        };

        java.util.List<RowFilter<DefaultTableModel, Object>> filters = new java.util.ArrayList<>();
        filters.add(rfSearch);
        filters.add(rfStatus);

        sorter.setRowFilter(RowFilter.andFilter(filters));
    }

    public void refreshTable() {
        model.setRowCount(0);
        try {
            List<Appointment> list = AppointmentDAO.getAllAppointmentsWithStatus();
            for (Appointment app : list) {
                Object[] row = {
                        app.getAppointmentId(),
                        app.getPatientId(),
                        app.getDoctorId(),
                        app.getAppointmentDate(),
                        app.getAppointmentTime(),
                        app.getStatus()
                };
                model.addRow(row);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Failed to load appointments list: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
