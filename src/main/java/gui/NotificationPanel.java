package gui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import database.NotificationSimulator;

public class NotificationPanel extends JPanel {

    JTable table;
    DefaultTableModel model;
    JButton refreshBtn, clearBtn;

    public NotificationPanel() {
        setLayout(new BorderLayout(15, 15));
        setBackground(new Color(245, 248, 255));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Title
        JLabel title = new JLabel("HMS Outbound Alert Simulator Logs");
        title.setFont(new Font("Arial", Font.BOLD, 22));
        title.setForeground(new Color(44, 62, 80));
        add(title, BorderLayout.NORTH);

        // Table
        String[] cols = {"Timestamp", "Contact (Phone/Email)", "Simulated Message Content"};
        model = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
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

        // Control Panel
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 5));
        bottomPanel.setOpaque(false);

        refreshBtn = new JButton("Refresh Inbox");
        refreshBtn.setFont(new Font("Arial", Font.BOLD, 14));
        refreshBtn.setBackground(new Color(70, 130, 180));
        refreshBtn.setForeground(Color.WHITE);
        refreshBtn.setPreferredSize(new Dimension(180, 38));
        refreshBtn.setFocusPainted(false);

        clearBtn = new JButton("Clear Simulator Logs");
        clearBtn.setFont(new Font("Arial", Font.BOLD, 14));
        clearBtn.setBackground(new Color(178, 34, 34));
        clearBtn.setForeground(Color.WHITE);
        clearBtn.setPreferredSize(new Dimension(180, 38));
        clearBtn.setFocusPainted(false);

        bottomPanel.add(refreshBtn);
        bottomPanel.add(clearBtn);
        add(bottomPanel, BorderLayout.SOUTH);

        // Listeners
        refreshBtn.addActionListener(e -> refreshTable());
        clearBtn.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to clear all simulator logs?", "Confirm Clear", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                NotificationSimulator.clearHistory();
                refreshTable();
            }
        });

        // Load initially
        refreshTable();
    }

    public void refreshTable() {
        model.setRowCount(0);
        List<String[]> logs = NotificationSimulator.getNotificationHistory();
        for (String[] log : logs) {
            model.addRow(log);
        }
    }
}
