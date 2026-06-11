package gui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import database.UserDAO;

public class UserManagementPanel extends JPanel {

    JTable table;
    DefaultTableModel model;
    JButton addBtn, deleteBtn;

    public UserManagementPanel() {
        setLayout(new BorderLayout(15, 15));
        setBackground(new Color(245, 248, 255));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Security check
        if (!AppSession.isAdmin) {
            showAccessDenied();
            return;
        }

        // Title
        JLabel title = new JLabel("System Operators & Staff Registry");
        title.setFont(new Font("Arial", Font.BOLD, 22));
        title.setForeground(new Color(44, 62, 80));
        add(title, BorderLayout.NORTH);

        // Grid table
        String[] cols = {"User ID", "Username", "Role"};
        model = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };
        table = new JTable(model);
        table.setFont(new Font("Arial", Font.PLAIN, 14));
        table.setRowHeight(28);
        table.setShowGrid(true);
        table.setGridColor(new Color(220, 230, 245));
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(200, 215, 240)));
        add(scroll, BorderLayout.CENTER);

        // Actions panel
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.CENTER, 25, 5));
        actions.setOpaque(false);

        addBtn = new JButton("Register New User");
        addBtn.setFont(new Font("Arial", Font.BOLD, 14));
        addBtn.setBackground(new Color(70, 130, 180));
        addBtn.setForeground(Color.WHITE);
        addBtn.setPreferredSize(new Dimension(200, 38));
        addBtn.setFocusPainted(false);

        deleteBtn = new JButton("Delete Selected User");
        deleteBtn.setFont(new Font("Arial", Font.BOLD, 14));
        deleteBtn.setBackground(new Color(178, 34, 34));
        deleteBtn.setForeground(Color.WHITE);
        deleteBtn.setPreferredSize(new Dimension(200, 38));
        deleteBtn.setFocusPainted(false);

        actions.add(addBtn);
        actions.add(deleteBtn);
        add(actions, BorderLayout.SOUTH);

        // Listeners
        addBtn.addActionListener(e -> showAddUserDialog());
        deleteBtn.addActionListener(e -> deleteSelectedUser());

        // Initial Load
        refreshTable();
    }

    private void showAccessDenied() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        JLabel alert = new JLabel("ACCESS DENIED");
        alert.setFont(new Font("Arial", Font.BOLD, 28));
        alert.setForeground(new Color(178, 34, 34));
        
        JLabel desc = new JLabel("Only administrators are permitted to manage user accounts.");
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

    private void showAddUserDialog() {
        JTextField userField = new JTextField();
        JPasswordField passField = new JPasswordField();
        JComboBox<String> roleBox = new JComboBox<>(new String[]{"Staff", "Admin"});

        Object[] form = {
                "Username:", userField,
                "Password:", passField,
                "Account Role:", roleBox
        };

        int option = JOptionPane.showConfirmDialog(this, form, "Register System User", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (option == JOptionPane.OK_OPTION) {
            String u = userField.getText().trim();
            String p = String.valueOf(passField.getPassword()).trim();
            String r = roleBox.getSelectedItem().toString();

            if (u.isEmpty() || p.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Username and password cannot be empty.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            boolean success = UserDAO.insertUser(u, p, r);
            if (success) {
                JOptionPane.showMessageDialog(this, "User registered successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                refreshTable();
            } else {
                JOptionPane.showMessageDialog(this, "Username already exists or database failed.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void deleteSelectedUser() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a user to delete.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int userId = Integer.parseInt(model.getValueAt(selectedRow, 0).toString());
        String username = model.getValueAt(selectedRow, 1).toString();

        if (username.equals("admin")) {
            JOptionPane.showMessageDialog(this, "The master 'admin' account cannot be deleted.", "Restriction", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete user: " + username + "?", "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = UserDAO.deleteUser(userId);
            if (success) {
                JOptionPane.showMessageDialog(this, "User deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                refreshTable();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete user from database.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public void refreshTable() {
        if (!AppSession.isAdmin) return;
        model.setRowCount(0);
        List<String[]> list = UserDAO.getAllUsers();
        for (String[] user : list) {
            model.addRow(user);
        }
    }
}
