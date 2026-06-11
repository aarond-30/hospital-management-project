package gui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import database.AuditDAO;

public class AuditTrailPanel extends JPanel {

    JTable table;
    DefaultTableModel model;
    JButton refreshBtn;

    public AuditTrailPanel() {
        setLayout(new BorderLayout(15, 15));
        setBackground(new Color(245, 248, 255));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        if (!AppSession.isAdmin) {
            showAccessDenied();
            return;
        }

        // Title
        JLabel title = new JLabel("System Security Logs & Audit Trail");
        title.setFont(new Font("Arial", Font.BOLD, 22));
        title.setForeground(new Color(44, 62, 80));
        add(title, BorderLayout.NORTH);

        // Table
        String[] cols = {"Log ID", "Timestamp", "Operator", "Activity Description"};
        model = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };
        table = new JTable(model);
        table.setFont(new Font("Arial", Font.PLAIN, 13));
        table.setRowHeight(25);
        table.setShowGrid(true);
        table.setGridColor(new Color(220, 230, 245));
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(200, 215, 240)));
        add(scroll, BorderLayout.CENTER);

        // Refresh Button
        refreshBtn = new JButton("Refresh Audit Logs");
        refreshBtn.setFont(new Font("Arial", Font.BOLD, 14));
        refreshBtn.setBackground(new Color(70, 130, 180));
        refreshBtn.setForeground(Color.WHITE);
        refreshBtn.setFocusPainted(false);
        refreshBtn.setPreferredSize(new Dimension(200, 38));
        
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottom.setOpaque(false);
        bottom.add(refreshBtn);
        add(bottom, BorderLayout.SOUTH);

        refreshBtn.addActionListener(e -> refreshTable());

        refreshTable();
    }

    private void showAccessDenied() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        JLabel alert = new JLabel("ACCESS DENIED");
        alert.setFont(new Font("Arial", Font.BOLD, 28));
        alert.setForeground(new Color(178, 34, 34));
        
        JLabel desc = new JLabel("Security logs are restricted to system administrators.");
        desc.setFont(new Font("Arial", Font.PLAIN, 14));
        desc.setForeground(Color.DARK_GRAY);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(alert, gbc);
        gbc.gridy = 1;
        gbc.insets = new Insets(15, 0, 0, 0);
        panel.add(desc, gbc);

        add(panel, BorderLayout.CENTER);
    }

    public void refreshTable() {
        if (!AppSession.isAdmin) return;
        model.setRowCount(0);
        List<String[]> list = AuditDAO.getAllLogs();
        for (String[] log : list) {
            model.addRow(log);
        }
    }
}
