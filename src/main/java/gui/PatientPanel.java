package gui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.util.List;
import java.util.regex.Pattern;
import database.PDFReportDAO;
import database.PatientDAO;
import models.Patient;

public class PatientPanel extends JPanel {

    JTable table;
    DefaultTableModel model;
    TableRowSorter<DefaultTableModel> sorter;

    JTextField searchField;
    JComboBox<String> visitFilter;

    JButton addBtn, editBtn, deleteBtn, markYesBtn, markNoBtn, reportBtn;

    public PatientPanel() {
        setLayout(new BorderLayout(15, 15));
        setBackground(new Color(245, 248, 255));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // --- TOP SEARCH & FILTER BAR ---
        JPanel searchBarPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        searchBarPanel.setOpaque(false);

        JLabel searchLabel = new JLabel("Search:");
        searchLabel.setFont(new Font("Arial", Font.BOLD, 14));
        searchBarPanel.add(searchLabel);

        searchField = new JTextField(15);
        searchField.setFont(new Font("Arial", Font.PLAIN, 14));
        searchBarPanel.add(searchField);

        JLabel filterLabel = new JLabel("Visit Status:");
        filterLabel.setFont(new Font("Arial", Font.BOLD, 14));
        searchBarPanel.add(filterLabel);

        String[] visitStatuses = {"All", "YES", "NO"};
        visitFilter = new JComboBox<>(visitStatuses);
        visitFilter.setFont(new Font("Arial", Font.PLAIN, 13));
        searchBarPanel.add(visitFilter);

        add(searchBarPanel, BorderLayout.NORTH);

        // --- CENTER TABLE ---
        String[] columns = {
                "ID", "Name", "Age", "Gender", "Phone", "Disease", "Address", "Doctor Visited", "Consultation Status"
        };

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
        JPanel actionsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));
        actionsPanel.setOpaque(false);

        addBtn = new JButton("Add Patient");
        editBtn = new JButton("Edit Patient");
        deleteBtn = new JButton("Delete Patient");
        markYesBtn = new JButton("Mark Visited YES");
        markNoBtn = new JButton("Mark Visited NO");
        reportBtn = new JButton("Download PDF Report");

        JButton[] buttons = {addBtn, editBtn, deleteBtn, markYesBtn, markNoBtn, reportBtn};
        Color primaryColor = new Color(70, 130, 180);
        Color accentYes = new Color(34, 139, 34);
        Color accentNo = new Color(178, 34, 34);
        Color editColor = new Color(255, 165, 0);

        for (JButton btn : buttons) {
            btn.setFont(new Font("Arial", Font.BOLD, 13));
            btn.setForeground(Color.WHITE);
            btn.setFocusPainted(false);
            btn.setPreferredSize(new Dimension(170, 38));
            actionsPanel.add(btn);
        }

        addBtn.setBackground(primaryColor);
        editBtn.setBackground(editColor);
        deleteBtn.setBackground(Color.DARK_GRAY);
        markYesBtn.setBackground(accentYes);
        markNoBtn.setBackground(accentNo);
        reportBtn.setBackground(primaryColor);

        // Visibility rules based on admin role
        markYesBtn.setVisible(AppSession.isAdmin);
        markNoBtn.setVisible(AppSession.isAdmin);
        deleteBtn.setVisible(AppSession.isAdmin);

        add(actionsPanel, BorderLayout.SOUTH);

        // --- LISTENERS ---
        // Search text listener
        searchField.addCaretListener(e -> applyFilters());
        // ComboBox action listener
        visitFilter.addActionListener(e -> applyFilters());

        addBtn.addActionListener(e -> {
            new PatientForm(SwingUtilities.getWindowAncestor(this));
            refreshTable();
        });

        editBtn.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Please select a patient to edit.", "Selection Required", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int modelRow = table.convertRowIndexToModel(selectedRow);
            int patientId = Integer.parseInt(model.getValueAt(modelRow, 0).toString());
            new EditPatientForm(SwingUtilities.getWindowAncestor(this), patientId, this::refreshTable);
        });

        deleteBtn.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Please select a patient to delete.", "Selection Required", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int modelRow = table.convertRowIndexToModel(selectedRow);
            int patientId = Integer.parseInt(model.getValueAt(modelRow, 0).toString());
            String patientName = model.getValueAt(modelRow, 1).toString();

            int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete patient: " + patientName + "?", "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (confirm == JOptionPane.YES_OPTION) {
                PatientDAO.deletePatient(patientId);
                JOptionPane.showMessageDialog(this, "Patient Deleted Successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
                refreshTable();
            }
        });

        markYesBtn.addActionListener(e -> updateVisitStatus("YES"));
        markNoBtn.addActionListener(e -> updateVisitStatus("NO"));

        reportBtn.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Please select a patient to generate a report.", "Selection Required", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int modelRow = table.convertRowIndexToModel(selectedRow);
            int patientId = Integer.parseInt(model.getValueAt(modelRow, 0).toString());

            String filePath = PDFReportDAO.generatePatientPDF(patientId);
            if (filePath != null) {
                int confirm = JOptionPane.showConfirmDialog(this, "PDF Report generated successfully as:\n" + filePath + "\n\nWould you like to open it now?", "Open Report", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    try {
                        java.io.File file = new java.io.File(filePath);
                        if (Desktop.isDesktopSupported()) {
                            Desktop.getDesktop().open(file);
                        } else {
                            JOptionPane.showMessageDialog(this, "A system PDF reader could not be launched automatically.", "Unsupported Desktop", JOptionPane.WARNING_MESSAGE);
                        }
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(this, "Failed to open report file: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this, "Failed to generate report PDF.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Load initial records
        refreshTable();
    }

    private void applyFilters() {
        String searchText = searchField.getText().trim();
        String selectedVisit = visitFilter.getSelectedItem().toString();

        RowFilter<DefaultTableModel, Object> rfSearch = RowFilter.regexFilter("(?i)" + Pattern.quote(searchText), 1, 4, 5); // search by Name (1), Phone (4), Disease (5)
        
        RowFilter<DefaultTableModel, Object> rfVisit = new RowFilter<>() {
            @Override
            public boolean include(Entry<? extends DefaultTableModel, ?> entry) {
                if ("All".equals(selectedVisit)) return true;
                String visitStatusVal = entry.getStringValue(7); // "Doctor Visited" column is index 7
                return selectedVisit.equalsIgnoreCase(visitStatusVal);
            }
        };

        java.util.List<RowFilter<DefaultTableModel, Object>> filters = new java.util.ArrayList<>();
        filters.add(rfSearch);
        filters.add(rfVisit);

        sorter.setRowFilter(RowFilter.andFilter(filters));
    }

    private void updateVisitStatus(String status) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a patient to update.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int modelRow = table.convertRowIndexToModel(selectedRow);
        int patientId = Integer.parseInt(model.getValueAt(modelRow, 0).toString());
        PatientDAO.updateVisitStatus(patientId, status);
        JOptionPane.showMessageDialog(this, "Visit status updated successfully to " + status + ".", "Updated", JOptionPane.INFORMATION_MESSAGE);
        refreshTable();
    }

    public void refreshTable() {
        model.setRowCount(0);
        try {
            List<Patient> list = PatientDAO.getAllPatients();
            for (Patient p : list) {
                Object[] row = {
                        p.getPatientId(),
                        p.getName(),
                        p.getAge(),
                        p.getGender(),
                        p.getPhone(),
                        p.getDisease(),
                        p.getAddress(),
                        p.getDoctorVisited(),
                        p.getConsultationStatus()
                };
                model.addRow(row);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Failed to load patients list: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
