package gui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import database.DashboardDAO;
import database.AppointmentDAO;
import models.Appointment;

public class Dashboard extends JFrame {

    // Sidebar navigation buttons
    JButton navOverviewBtn;
    JButton navPatientsBtn;
    JButton navDoctorsBtn;
    JButton navAppointmentsBtn;
    JButton navBillingBtn;
    JButton navRoomsBtn;
    JButton navPrescriptionBtn;
    JButton navLabReportsBtn;
    JButton navUsersBtn;
    JButton navLogsBtn;
    JButton navNotificationsBtn;
    JButton toggleThemeBtn;
    JButton logoutBtn;

    // Card Layout panels
    JPanel cardPanel;
    CardLayout cardLayout;

    // Specific sub-panels
    PatientPanel patientPanel;
    DoctorPanel doctorPanel;
    AppointmentPanel appointmentPanel;
    BillingPanel billingPanel;
    RoomManagementPanel roomPanel;
    PrescriptionPanel prescriptionPanel;
    LabReportPanel labReportPanel;
    UserManagementPanel userPanel;
    AuditTrailPanel auditPanel;
    NotificationPanel notificationPanel;
    JPanel overviewPanel;

    // Stats values on overview
    JLabel patientCountLabel;
    JLabel doctorCountLabel;
    JLabel appointmentCountLabel;
    JTable todayTable;
    DefaultTableModel todayModel;
    AnalyticsChartPanel chartPane;

    // Sidebar navigation styling constants
    private static final Color SIDEBAR_BG = new Color(28, 40, 51);
    private static final Color BTN_HOVER_BG = new Color(44, 62, 80);
    private static final Color BTN_ACTIVE_BG = new Color(40, 55, 71);
    private static final Color ACTIVE_BAR_COLOR = new Color(52, 152, 219);
    private static final Color TEXT_INACTIVE = new Color(189, 195, 199);
    private static final Color TEXT_ACTIVE = Color.WHITE;

    private boolean isDarkMode = false;

    public Dashboard() {
        setTitle("Hospital Management System");
        setSize(1350, 850);
        setMinimumSize(new Dimension(1100, 750));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Main container
        JPanel mainContainer = new JPanel(new BorderLayout());
        add(mainContainer);

        // ----------------- SIDEBAR PANEL (LEFT) -----------------
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(new Color(28, 40, 51)); // Sleek dark blue/slate
        sidebar.setPreferredSize(new Dimension(240, getHeight()));
        sidebar.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Header Title
        JLabel brandName = new JLabel("HMS Core");
        brandName.setFont(new Font("Arial", Font.BOLD, 22));
        brandName.setForeground(Color.WHITE);
        brandName.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidebar.add(brandName);

        JLabel brandSub = new JLabel("Hospital Care Manager");
        brandSub.setFont(new Font("Arial", Font.PLAIN, 12));
        brandSub.setForeground(new Color(178, 186, 187));
        brandSub.setAlignmentX(Component.CENTER_ALIGNMENT);
        brandSub.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        sidebar.add(brandSub);

        // Navigation Buttons
        navOverviewBtn = createSidebarButton("📊  Dashboard Overview");
        navPatientsBtn = createSidebarButton("👥  Patients Portal");
        navDoctorsBtn = createSidebarButton("👨‍⚕️  Doctors Registry");
        navAppointmentsBtn = createSidebarButton("📅  Appointments");
        navRoomsBtn = createSidebarButton("🏥  Ward Rooms");
        navPrescriptionBtn = createSidebarButton("📝  Prescriptions");
        navLabReportsBtn = createSidebarButton("🧪  Lab Diagnostics");
        navBillingBtn = createSidebarButton("💳  Billing Portal");
        navNotificationsBtn = createSidebarButton("🔔  Notification Inbox");
        navUsersBtn = createSidebarButton("⚙️  Staff Registry");
        navLogsBtn = createSidebarButton("📋  System Logs");
        toggleThemeBtn = createSidebarButton("🌓  Switch Dark/Light");
        logoutBtn = createSidebarButton("🚪  Sign Out");

        sidebar.add(navOverviewBtn);
        sidebar.add(Box.createVerticalStrut(8));
        sidebar.add(navPatientsBtn);
        sidebar.add(Box.createVerticalStrut(8));
        sidebar.add(navDoctorsBtn);
        sidebar.add(Box.createVerticalStrut(8));
        sidebar.add(navAppointmentsBtn);
        sidebar.add(Box.createVerticalStrut(8));
        sidebar.add(navRoomsBtn);
        sidebar.add(Box.createVerticalStrut(8));
        sidebar.add(navPrescriptionBtn);
        sidebar.add(Box.createVerticalStrut(8));
        sidebar.add(navLabReportsBtn);
        sidebar.add(Box.createVerticalStrut(8));
        sidebar.add(navBillingBtn);
        sidebar.add(Box.createVerticalStrut(8));
        sidebar.add(navNotificationsBtn);

        // Admin restricted views
        if (AppSession.isAdmin) {
            sidebar.add(Box.createVerticalStrut(8));
            sidebar.add(navUsersBtn);
            sidebar.add(Box.createVerticalStrut(8));
            sidebar.add(navLogsBtn);
        }

        sidebar.add(Box.createVerticalGlue()); // Push settings/logout to bottom
        sidebar.add(toggleThemeBtn);
        sidebar.add(Box.createVerticalStrut(8));
        sidebar.add(logoutBtn);
        sidebar.add(Box.createVerticalStrut(12));

        JLabel sidebarCredits = new JLabel("Atria Institute of Technology");
        sidebarCredits.setFont(new Font("Arial", Font.BOLD, 10));
        sidebarCredits.setForeground(new Color(149, 165, 166));
        sidebarCredits.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidebar.add(sidebarCredits);

        mainContainer.add(sidebar, BorderLayout.WEST);

        // ----------------- CONTENT CONTAINER (CENTER/RIGHT) -----------------
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);
        mainContainer.add(cardPanel, BorderLayout.CENTER);

        // Create Panel Instances
        patientPanel = new PatientPanel();
        doctorPanel = new DoctorPanel();
        appointmentPanel = new AppointmentPanel();
        roomPanel = new RoomManagementPanel();
        prescriptionPanel = new PrescriptionPanel();
        labReportPanel = new LabReportPanel();
        billingPanel = new BillingPanel();
        notificationPanel = new NotificationPanel();
        
        if (AppSession.isAdmin) {
            userPanel = new UserManagementPanel();
            auditPanel = new AuditTrailPanel();
        }
        
        createOverviewPanel();

        // Add to Card Layout
        cardPanel.add(overviewPanel, "OVERVIEW");
        cardPanel.add(patientPanel, "PATIENTS");
        cardPanel.add(doctorPanel, "DOCTORS");
        cardPanel.add(appointmentPanel, "APPOINTMENTS");
        cardPanel.add(roomPanel, "ROOMS");
        cardPanel.add(prescriptionPanel, "PRESCRIPTIONS");
        cardPanel.add(labReportPanel, "LAB_REPORTS");
        cardPanel.add(billingPanel, "BILLING");
        cardPanel.add(notificationPanel, "NOTIFICATIONS");
        
        if (AppSession.isAdmin) {
            cardPanel.add(userPanel, "USERS");
            cardPanel.add(auditPanel, "LOGS");
        }

        // Action Handlers for switching tabs
        navOverviewBtn.addActionListener(e -> {
            refreshStats();
            cardLayout.show(cardPanel, "OVERVIEW");
            setActiveButton(navOverviewBtn);
        });

        navPatientsBtn.addActionListener(e -> {
            patientPanel.refreshTable();
            cardLayout.show(cardPanel, "PATIENTS");
            setActiveButton(navPatientsBtn);
        });

        navDoctorsBtn.addActionListener(e -> {
            doctorPanel.refreshTable();
            cardLayout.show(cardPanel, "DOCTORS");
            setActiveButton(navDoctorsBtn);
        });

        navAppointmentsBtn.addActionListener(e -> {
            appointmentPanel.refreshTable();
            cardLayout.show(cardPanel, "APPOINTMENTS");
            setActiveButton(navAppointmentsBtn);
        });

        navRoomsBtn.addActionListener(e -> {
            roomPanel.refreshWards();
            cardLayout.show(cardPanel, "ROOMS");
            setActiveButton(navRoomsBtn);
        });

        navPrescriptionBtn.addActionListener(e -> {
            prescriptionPanel.refreshHistoryTable();
            cardLayout.show(cardPanel, "PRESCRIPTIONS");
            setActiveButton(navPrescriptionBtn);
        });

        navLabReportsBtn.addActionListener(e -> {
            labReportPanel.refreshTable();
            cardLayout.show(cardPanel, "LAB_REPORTS");
            setActiveButton(navLabReportsBtn);
        });

        navBillingBtn.addActionListener(e -> {
            billingPanel.refreshTable();
            cardLayout.show(cardPanel, "BILLING");
            setActiveButton(navBillingBtn);
        });

        navNotificationsBtn.addActionListener(e -> {
            notificationPanel.refreshTable();
            cardLayout.show(cardPanel, "NOTIFICATIONS");
            setActiveButton(navNotificationsBtn);
        });

        if (AppSession.isAdmin) {
            navUsersBtn.addActionListener(e -> {
                userPanel.refreshTable();
                cardLayout.show(cardPanel, "USERS");
                setActiveButton(navUsersBtn);
            });

            navLogsBtn.addActionListener(e -> {
                auditPanel.refreshTable();
                cardLayout.show(cardPanel, "LOGS");
                setActiveButton(navLogsBtn);
            });
        }

        toggleThemeBtn.addActionListener(e -> toggleTheme());

        logoutBtn.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to sign out?", "Confirm Sign Out", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                new LoginForm();
                dispose();
            }
        });

        // Set Dashboard active initially
        setActiveButton(navOverviewBtn);
        
        // Sync component background elements
        applyDynamicBackgrounds();
        setVisible(true);
    }

    private JButton createSidebarButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Arial", Font.BOLD, 13));
        btn.setForeground(TEXT_INACTIVE);
        btn.setBackground(SIDEBAR_BG);
        btn.setFocusPainted(false);
        btn.setBorderPainted(true);
        // Default transparent left margin, but identical size (4px matte border + padding)
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 4, 0, 0, SIDEBAR_BG),
                BorderFactory.createEmptyBorder(8, 15, 8, 15)
        ));
        btn.setMaximumSize(new Dimension(210, 38));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setHorizontalAlignment(SwingConstants.LEFT);

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                if (btn.getBackground() != BTN_ACTIVE_BG) {
                    btn.setBackground(BTN_HOVER_BG);
                    btn.setForeground(TEXT_ACTIVE);
                    // Slide slightly right by increasing left padding
                    btn.setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createMatteBorder(0, 4, 0, 0, SIDEBAR_BG),
                            BorderFactory.createEmptyBorder(8, 20, 8, 10)
                    ));
                }
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                if (btn.getBackground() != BTN_ACTIVE_BG) {
                    btn.setBackground(SIDEBAR_BG);
                    btn.setForeground(TEXT_INACTIVE);
                    // Reset position padding
                    btn.setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createMatteBorder(0, 4, 0, 0, SIDEBAR_BG),
                            BorderFactory.createEmptyBorder(8, 15, 8, 15)
                    ));
                }
            }
        });
        return btn;
    }

    private void setActiveButton(JButton activeBtn) {
        JButton[] navButtons = {navOverviewBtn, navPatientsBtn, navDoctorsBtn, navAppointmentsBtn, navRoomsBtn, navPrescriptionBtn, navLabReportsBtn, navBillingBtn, navNotificationsBtn, navUsersBtn, navLogsBtn};
        for (JButton btn : navButtons) {
            if (btn != null) {
                if (btn == activeBtn) {
                    btn.setBackground(BTN_ACTIVE_BG);
                    btn.setForeground(TEXT_ACTIVE);
                    // Active vertical highlight indicator on the left
                    btn.setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createMatteBorder(0, 4, 0, 0, ACTIVE_BAR_COLOR),
                            BorderFactory.createEmptyBorder(8, 20, 8, 10) // Keep in shifted position
                    ));
                } else {
                    btn.setBackground(SIDEBAR_BG);
                    btn.setForeground(TEXT_INACTIVE);
                    // Default state
                    btn.setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createMatteBorder(0, 4, 0, 0, SIDEBAR_BG),
                            BorderFactory.createEmptyBorder(8, 15, 8, 15)
                    ));
                }
            }
        }
    }

    private void toggleTheme() {
        try {
            isDarkMode = !isDarkMode;
            if (isDarkMode) {
                UIManager.setLookAndFeel(new com.formdev.flatlaf.FlatDarkLaf());
            } else {
                UIManager.setLookAndFeel(new com.formdev.flatlaf.FlatLightLaf());
            }
            SwingUtilities.updateComponentTreeUI(this);
            applyDynamicBackgrounds();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void applyDynamicBackgrounds() {
        Color bg = UIManager.getColor("Panel.background");
        overviewPanel.setBackground(bg);
        patientPanel.setBackground(bg);
        doctorPanel.setBackground(bg);
        appointmentPanel.setBackground(bg);
        roomPanel.setBackground(bg);
        prescriptionPanel.setBackground(bg);
        labReportPanel.setBackground(bg);
        billingPanel.setBackground(bg);
        notificationPanel.setBackground(bg);
        
        if (userPanel != null) userPanel.setBackground(bg);
        if (auditPanel != null) auditPanel.setBackground(bg);
    }

    private void createOverviewPanel() {
        overviewPanel = new JPanel(new BorderLayout(20, 20));
        overviewPanel.setBackground(new Color(245, 248, 255));
        overviewPanel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        // Welcome / Header Label
        JLabel header = new JLabel("Welcome to Dashboard Overview");
        header.setFont(new Font("Arial", Font.BOLD, 24));
        header.setForeground(new Color(44, 62, 80));
        overviewPanel.add(header, BorderLayout.NORTH);

        // Grid for Stats Cards (3 cards)
        JPanel statsContainer = new JPanel(new GridLayout(1, 3, 20, 0));
        statsContainer.setOpaque(false);

        // Patients Card
        JPanel patientCard = createCardPanel("Patients Managed", "0", new Color(225, 245, 230), new Color(46, 125, 50));
        patientCountLabel = (JLabel) patientCard.getClientProperty("valueLabel");

        // Doctors Card
        JPanel doctorCard = createCardPanel("Active Staff Doctors", "0", new Color(225, 240, 255), new Color(21, 101, 192));
        doctorCountLabel = (JLabel) doctorCard.getClientProperty("valueLabel");

        // Appointments Card
        JPanel appointmentCard = createCardPanel("Total Bookings", "0", new Color(255, 243, 230), new Color(230, 124, 0));
        appointmentCountLabel = (JLabel) appointmentCard.getClientProperty("valueLabel");

        statsContainer.add(patientCard);
        statsContainer.add(doctorCard);
        statsContainer.add(appointmentCard);

        // Combined Middle Area
        JPanel mainOverviewBody = new JPanel(new BorderLayout(0, 15));
        mainOverviewBody.setOpaque(false);
        mainOverviewBody.add(statsContainer, BorderLayout.NORTH);

        // Horizontal Grid containing Custom Charts (Left) & Scheduled list (Right)
        JPanel visualContainer = new JPanel(new GridLayout(1, 2, 20, 0));
        visualContainer.setOpaque(false);

        // Left side: Custom Vector Charts Panel
        chartPane = new AnalyticsChartPanel();
        visualContainer.add(chartPane);

        // Right side: Quick Table for Today's Appointments
        JPanel listPanel = new JPanel(new BorderLayout());
        listPanel.setBackground(Color.WHITE);
        listPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 230, 245), 1, true),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        JLabel listTitle = new JLabel("Today's Scheduled Appointments");
        listTitle.setFont(new Font("Arial", Font.BOLD, 16));
        listTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        listPanel.add(listTitle, BorderLayout.NORTH);

        String[] cols = {"App ID", "Patient ID", "Doctor ID", "Date", "Time", "Status"};
        todayModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        todayTable = new JTable(todayModel);
        todayTable.setFont(new Font("Arial", Font.PLAIN, 13));
        todayTable.setRowHeight(24);
        todayTable.setShowGrid(true);
        todayTable.setGridColor(new Color(240, 240, 240));

        JScrollPane tableScroll = new JScrollPane(todayTable);
        listPanel.add(tableScroll, BorderLayout.CENTER);

        visualContainer.add(listPanel);
        mainOverviewBody.add(visualContainer, BorderLayout.CENTER);

        overviewPanel.add(mainOverviewBody, BorderLayout.CENTER);

        // Academic project footer credits panel
        JPanel creditsPanel = new JPanel(new BorderLayout());
        creditsPanel.setOpaque(false);
        creditsPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        
        JLabel collegeLabel = new JLabel("Atria Institute of Technology - Department of ISE (Section D)", SwingConstants.LEFT);
        collegeLabel.setFont(new Font("Arial", Font.BOLD | Font.ITALIC, 12));
        collegeLabel.setForeground(new Color(127, 140, 141));
        
        JLabel teamLabel = new JLabel("Developed by: Aaron (1AT24IS002) | Nithin (1AT24IS141) | Ragavendra (1AT23IS126)", SwingConstants.RIGHT);
        teamLabel.setFont(new Font("Arial", Font.BOLD, 12));
        teamLabel.setForeground(new Color(127, 140, 141));
        
        creditsPanel.add(collegeLabel, BorderLayout.WEST);
        creditsPanel.add(teamLabel, BorderLayout.EAST);
        overviewPanel.add(creditsPanel, BorderLayout.SOUTH);

        // Fetch numbers
        refreshStats();
    }

    private JPanel createCardPanel(String title, String val, Color bgColor, Color textColor) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(bgColor);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(textColor.brighter(), 1, true),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 13));
        titleLabel.setForeground(textColor.darker());
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel valueLabel = new JLabel(val);
        valueLabel.setFont(new Font("Arial", Font.BOLD, 36));
        valueLabel.setForeground(textColor);
        valueLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        valueLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        card.add(titleLabel);
        card.add(valueLabel);
        
        // Save component reference for updating later
        card.putClientProperty("valueLabel", valueLabel);

        return card;
    }

    public void refreshStats() {
        patientCountLabel.setText(String.valueOf(DashboardDAO.getPatientCount()));
        doctorCountLabel.setText(String.valueOf(DashboardDAO.getDoctorCount()));
        appointmentCountLabel.setText(String.valueOf(DashboardDAO.getAppointmentCount()));

        // Trigger repaint on custom drawing charts to load updated DB values
        if (chartPane != null) {
            chartPane.repaint();
        }

        // Populate today's appointments table
        todayModel.setRowCount(0);
        try {
            List<Appointment> appointments = AppointmentDAO.getAllAppointmentsWithStatus();
            int limit = 0;
            for (Appointment app : appointments) {
                if ("Today".equalsIgnoreCase(app.getStatus()) || limit < 8) {
                    Object[] row = {
                            app.getAppointmentId(),
                            app.getPatientId(),
                            app.getDoctorId(),
                            app.getAppointmentDate(),
                            app.getAppointmentTime(),
                            app.getStatus()
                    };
                    todayModel.addRow(row);
                    limit++;
                }
            }
        } catch (Exception e) {
            // Silently fallback if db has issues
        }
    }

    public static void main(String[] args) {
        // FlatLaf setup on run
        try {
            com.formdev.flatlaf.FlatLightLaf.setup();
        } catch (Exception ex) {
            // Ignore
        }
        new Dashboard();
    }
}