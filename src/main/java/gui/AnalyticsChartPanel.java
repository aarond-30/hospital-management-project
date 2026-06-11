package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Arc2D;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import database.DoctorDAO;
import database.PatientDAO;
import database.AppointmentDAO;
import models.Doctor;
import models.Patient;
import models.Appointment;

public class AnalyticsChartPanel extends JPanel {

    public AnalyticsChartPanel() {
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(225, 235, 245), 1, true),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        
        // Turn on high-quality text and graphics antialiasing
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();

        // Header Title
        g2.setColor(new Color(44, 62, 80));
        g2.setFont(new Font("Segoe UI", Font.BOLD, 18));
        g2.drawString("Visual Analytics Overview", 15, 25);

        // Chart dimensions (side-by-side)
        int padding = 15;
        int chartWidth = (w - (padding * 3)) / 2;
        int chartHeight = h - 60;

        // Draw Bar Chart (Left)
        drawBarChart(g2, padding, 45, chartWidth, chartHeight);

        // Draw Donut Chart (Right)
        drawDonutChart(g2, (padding * 2) + chartWidth, 45, chartWidth, chartHeight);
    }

    private void drawBarChart(Graphics2D g2, int x, int y, int w, int h) {
        // Soft backdrop
        g2.setColor(new Color(250, 252, 255));
        g2.fillRoundRect(x, y, w, h, 16, 16);
        g2.setColor(new Color(225, 235, 250));
        g2.drawRoundRect(x, y, w, h, 16, 16);

        // Title
        g2.setColor(new Color(52, 73, 94));
        g2.setFont(new Font("Segoe UI", Font.BOLD, 13));
        g2.drawString("Appointments Workload by Doctor", x + 15, y + 25);

        // Calculate Workloads
        List<Doctor> doctors = DoctorDAO.getAllDoctors();
        List<Appointment> appointments = AppointmentDAO.getAllAppointmentsWithStatus();

        Map<Integer, Integer> appCounts = new HashMap<>();
        for (Doctor d : doctors) {
            appCounts.put(d.getDoctorId(), 0);
        }

        int maxVal = 0;
        for (Appointment app : appointments) {
            int docId = app.getDoctorId();
            if (appCounts.containsKey(docId)) {
                int count = appCounts.get(docId) + 1;
                appCounts.put(docId, count);
                if (count > maxVal) {
                    maxVal = count;
                }
            }
        }

        if (doctors.isEmpty()) {
            g2.setColor(new Color(127, 140, 141));
            g2.setFont(new Font("Segoe UI", Font.ITALIC, 13));
            g2.drawString("No Doctor Records Found", x + w/2 - 75, y + h/2);
            return;
        }

        // Round maxVal to a clean ceiling number for grid lines
        int gridMax = ((maxVal + 3) / 4) * 4;
        if (gridMax == 0) gridMax = 4;

        int plotX = x + 40;
        int plotY = y + 55;
        int plotW = w - 60;
        int plotH = h - 95;

        // Draw horizontal grid lines
        g2.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        g2.setStroke(new BasicStroke(1.0f));
        for (int i = 0; i <= 4; i++) {
            int gy = plotY + plotH - (i * plotH / 4);
            g2.setColor(new Color(230, 235, 245));
            g2.drawLine(plotX, gy, plotX + plotW, gy);

            // Draw Y-axis labels
            g2.setColor(new Color(127, 140, 141));
            int val = i * gridMax / 4;
            String label = String.valueOf(val);
            g2.drawString(label, plotX - g2.getFontMetrics().stringWidth(label) - 8, gy + 4);
        }

        // Draw plot bars
        int barSpacing = 12;
        int numDoctors = Math.min(doctors.size(), 5); // Display top 5 doctors
        int barW = (plotW - (barSpacing * (numDoctors + 1))) / numDoctors;

        g2.setFont(new Font("Segoe UI", Font.BOLD, 10));
        for (int i = 0; i < numDoctors; i++) {
            Doctor doc = doctors.get(i);
            int count = appCounts.get(doc.getDoctorId());

            int barH = (int) (((double) count / gridMax) * plotH);
            int bx = plotX + barSpacing + i * (barW + barSpacing);
            int by = plotY + plotH - barH;

            // Draw a beautiful Indigo to Violet gradient bar
            GradientPaint barGradient = new GradientPaint(
                    bx, by, new Color(92, 107, 192),  // Modern Indigo
                    bx, by + barH, new Color(149, 117, 205) // Soft Violet
            );
            g2.setPaint(barGradient);
            
            // Draw bar with rounded top corners (we achieve this by making the round rect overlap below the X-axis)
            g2.fillRoundRect(bx, by, barW, barH + 10, 8, 8);
            
            // Draw grid outline on the bar
            g2.setColor(new Color(92, 107, 192, 150));
            g2.drawRoundRect(bx, by, barW, barH + 10, 8, 8);

            // Print doctor labels (e.g. Dr. John)
            g2.setPaint(null);
            g2.setColor(new Color(44, 62, 80));
            String name = doc.getName();
            if (name.startsWith("Dr. ") || name.startsWith("Dr ")) {
                name = name.substring(3);
            }
            String docLabel = name.length() > 8 ? name.substring(0, 7) + ".." : name;
            g2.drawString(docLabel, bx + (barW - g2.getFontMetrics().stringWidth(docLabel)) / 2, plotY + plotH + 15);

            // Workload value counts
            String countVal = String.valueOf(count);
            g2.setFont(new Font("Segoe UI", Font.BOLD, 10));
            g2.drawString(countVal, bx + (barW - g2.getFontMetrics().stringWidth(countVal)) / 2, by - 5);
        }

        // Draw primary axis lines
        g2.setColor(new Color(189, 195, 199));
        g2.setStroke(new BasicStroke(1.5f));
        g2.drawLine(plotX, plotY + plotH, plotX + plotW, plotY + plotH); // X Axis line
    }

    private void drawDonutChart(Graphics2D g2, int x, int y, int w, int h) {
        // Soft backdrop
        g2.setColor(new Color(250, 252, 255));
        g2.fillRoundRect(x, y, w, h, 16, 16);
        g2.setColor(new Color(225, 235, 250));
        g2.drawRoundRect(x, y, w, h, 16, 16);

        // Title
        g2.setColor(new Color(52, 73, 94));
        g2.setFont(new Font("Segoe UI", Font.BOLD, 13));
        g2.drawString("Patient Age Brackets (Demographics)", x + 15, y + 25);

        // Calculate age group metrics
        List<Patient> patients = PatientDAO.getAllPatients();
        int children = 0; // < 18
        int adults = 0;   // 18 - 60
        int seniors = 0;  // > 60

        for (Patient p : patients) {
            int age = p.getAge();
            if (age < 18) children++;
            else if (age <= 60) adults++;
            else seniors++;
        }

        int total = children + adults + seniors;
        if (total == 0) {
            g2.setColor(new Color(127, 140, 141));
            g2.setFont(new Font("Segoe UI", Font.ITALIC, 13));
            g2.drawString("No Patient Records Found", x + w/2 - 75, y + h/2);
            return;
        }

        // Layout boundaries
        int diameter = Math.min(w, h) - 120;
        int px = x + (w - diameter) / 2 - 40; // Shift left for legend spacing
        int py = y + (h - diameter) / 2 + 10;

        double childAngle = ((double) children / total) * 360.0;
        double adultAngle = ((double) adults / total) * 360.0;
        double seniorAngle = ((double) seniors / total) * 360.0;

        Color colorChild = new Color(52, 152, 219);   // Royal Blue
        Color colorAdult = new Color(46, 204, 113);   // Mint Green
        Color colorSenior = new Color(241, 196, 15);  // Warm Yellow

        double startAngle = 90.0; // Start at top 12 o'clock

        // 3-degree whitespace separator gap
        double gap = total > 1 ? 3.0 : 0.0;

        // Draw Child slice
        if (children > 0) {
            g2.setColor(colorChild);
            g2.fill(new Arc2D.Double(px, py, diameter, diameter, startAngle, childAngle - gap, Arc2D.PIE));
            startAngle += childAngle;
        }

        // Draw Adult slice
        if (adults > 0) {
            g2.setColor(colorAdult);
            g2.fill(new Arc2D.Double(px, py, diameter, diameter, startAngle, adultAngle - gap, Arc2D.PIE));
            startAngle += adultAngle;
        }

        // Draw Senior slice
        if (seniors > 0) {
            g2.setColor(colorSenior);
            g2.fill(new Arc2D.Double(px, py, diameter, diameter, startAngle, seniorAngle - gap, Arc2D.PIE));
        }

        // Clear center to create a donut hole
        int innerDiam = (int) (diameter * 0.55);
        int ix = px + (diameter - innerDiam) / 2;
        int iy = py + (diameter - innerDiam) / 2;
        g2.setColor(new Color(250, 252, 255)); // Matches card backdrop color
        g2.fillOval(ix, iy, innerDiam, innerDiam);

        // Total count text in the center
        g2.setColor(new Color(44, 62, 80));
        g2.setFont(new Font("Segoe UI", Font.BOLD, 18));
        String countStr = String.valueOf(total);
        g2.drawString(countStr, ix + (innerDiam - g2.getFontMetrics().stringWidth(countStr)) / 2, iy + (innerDiam / 2) + 2);

        g2.setFont(new Font("Segoe UI", Font.PLAIN, 9));
        g2.setColor(new Color(127, 140, 141));
        String totalLabel = "TOTAL PATIENTS";
        g2.drawString(totalLabel, ix + (innerDiam - g2.getFontMetrics().stringWidth(totalLabel)) / 2, iy + (innerDiam / 2) + 16);

        // Draw round circular legend indicators on the right
        int lx = x + w - 120;
        int ly = y + (h / 2) - 30;
        g2.setFont(new Font("Segoe UI", Font.BOLD, 11));

        // Legend: Children
        g2.setColor(colorChild);
        g2.fillOval(lx, ly, 10, 10);
        g2.setColor(new Color(44, 62, 80));
        g2.drawString("Under 18: " + children, lx + 16, ly + 9);

        // Legend: Adults
        ly += 22;
        g2.setColor(colorAdult);
        g2.fillOval(lx, ly, 10, 10);
        g2.setColor(new Color(44, 62, 80));
        g2.drawString("18 - 60: " + adults, lx + 16, ly + 9);

        // Legend: Seniors
        ly += 22;
        g2.setColor(colorSenior);
        g2.fillOval(lx, ly, 10, 10);
        g2.setColor(new Color(44, 62, 80));
        g2.drawString("Over 60: " + seniors, lx + 16, ly + 9);
    }
}
