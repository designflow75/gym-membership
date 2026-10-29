package com.smartgym.ui;

import com.smartgym.model.Attendance;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.util.List;

/**
 * Custom Swing component rendering column charts and circular attendance gauges via Java 2D Graphics.
 */
public class AttendanceChartPanel extends JPanel {

    // Current attendance records to render
    private List<Attendance> attendanceList;

    public AttendanceChartPanel(List<Attendance> list) {
        this.attendanceList = list;
        setBackground(UITheme.BG_CARD);
        setPreferredSize(new Dimension(540, 210));
    }

    /**
     * Updates the chart with new attendance data and schedules an immediate repaint.
     */
    public void updateData(List<Attendance> list) {
        this.attendanceList = list;
        repaint(); // Triggers paintComponent()
    }

    /**
     * Java Swing paintComponent():
     * The heart of custom 2D rendering. Invoked automatically by the Swing repaint manager.
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Downcast to Graphics2D for high-quality rendering controls
        Graphics2D g2 = (Graphics2D) g;

        // Turn on Antialiasing for smooth text and curved shape edges (eliminates pixelated jaggedness)
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();

        // --- STEP 1: Render White Background Card with Soft Border ---
        g2.setColor(UITheme.BG_CARD);
        g2.fill(new RoundRectangle2D.Float(8, 8, w - 16, h - 16, 14, 14));
        g2.setColor(UITheme.BORDER_COLOR);
        g2.draw(new RoundRectangle2D.Float(8, 8, w - 16, h - 16, 14, 14));

        // Header Title
        g2.setColor(UITheme.PRIMARY);
        g2.setFont(UITheme.FONT_HEADER);
        g2.drawString("Attendance Performance & Column Breakdown", 22, 32);

        // Empty state check
        if (attendanceList == null || attendanceList.isEmpty()) {
            g2.setColor(UITheme.TEXT_MUTED);
            g2.setFont(UITheme.FONT_BODY);
            g2.drawString("No attendance logs recorded yet. Visit the gym to log sessions!", 22, 80);
            return;
        }

        // --- STEP 2: Aggregate Statistics ---
        int total = attendanceList.size();
        int present = (int) attendanceList.stream().filter(a -> "Present".equalsIgnoreCase(a.getStatus())).count();
        int late = (int) attendanceList.stream().filter(a -> "Late".equalsIgnoreCase(a.getStatus())).count();
        int absent = (int) attendanceList.stream().filter(a -> "Absent".equalsIgnoreCase(a.getStatus())).count();
        int attendanceRate = (int) Math.round(((double) (present + late) / total) * 100.0);

        // --- STEP 3: Render Circular Attendance Gauge (Left Side) ---
        int gaugeX = 30;
        int gaugeY = 50;
        int gaugeSize = 105;

        // Base circle background
        g2.setColor(UITheme.BG_CARD_ALT);
        g2.fillOval(gaugeX, gaugeY, gaugeSize, gaugeSize);
        g2.setColor(UITheme.BORDER_LIGHT);
        g2.drawOval(gaugeX, gaugeY, gaugeSize, gaugeSize);

        // Circular background track (gray ring)
        g2.setStroke(new BasicStroke(9, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.setColor(UITheme.BORDER_LIGHT);
        g2.drawArc(gaugeX + 8, gaugeY + 8, gaugeSize - 16, gaugeSize - 16, 0, 360);

        // Color-coded progress arc (360 degrees * attendanceRate / 100)
        int arcAngle = (int) Math.round((attendanceRate / 100.0) * 360.0);
        g2.setColor(attendanceRate >= 75 ? UITheme.SUCCESS : UITheme.WARNING);
        // Start angle 90 (top 12 o'clock), sweeping clockwise by -arcAngle
        g2.drawArc(gaugeX + 8, gaugeY + 8, gaugeSize - 16, gaugeSize - 16, 90, -arcAngle);

        // Gauge center text: Percentage display (e.g. "85%")
        g2.setFont(UITheme.FONT_TITLE);
        g2.setColor(UITheme.TEXT_DARK);
        String pctStr = attendanceRate + "%";
        int pctW = g2.getFontMetrics().stringWidth(pctStr);
        g2.drawString(pctStr, gaugeX + (gaugeSize - pctW) / 2, gaugeY + 54);

        g2.setFont(UITheme.FONT_SMALL);
        g2.setColor(UITheme.TEXT_MUTED);
        String subStr = "Attendance";
        int subW = g2.getFontMetrics().stringWidth(subStr);
        g2.drawString(subStr, gaugeX + (gaugeSize - subW) / 2, gaugeY + 72);

        // --- STEP 4: Render Column / Bar Chart (Right Side) ---
        int chartX = 175;
        int chartY = 55;
        int chartW = w - chartX - 25;
        int chartH = 105;

        String[] labels = {"Total Logs", "Present", "Late", "Absent"};
        int[] values = {total, present, late, absent};
        Color[] colColors = {UITheme.PRIMARY, UITheme.SUCCESS, UITheme.WARNING, UITheme.DANGER};

        int maxVal = Math.max(1, total);
        int colWidth = Math.min(55, (chartW - 40) / 4);
        int colSpacing = (chartW - (colWidth * 4)) / 3;

        for (int i = 0; i < 4; i++) {
            int cx = chartX + i * (colWidth + colSpacing);
            // Height proportional to maximum value
            int barHeight = (int) Math.round(((double) values[i] / maxVal) * (chartH - 40));
            int cy = chartY + chartH - 20 - barHeight;

            // Bar background slot (soft gray track)
            g2.setColor(UITheme.BG_CARD_ALT);
            g2.fill(new RoundRectangle2D.Float(cx, chartY, colWidth, chartH - 20, 8, 8));

            // Colored vertical column bar
            if (barHeight > 0) {
                g2.setColor(colColors[i]);
                g2.fill(new RoundRectangle2D.Float(cx, cy, colWidth, barHeight, 8, 8));
            }

            // Numeric value on top of bar
            g2.setFont(UITheme.FONT_SUBHEADER);
            g2.setColor(UITheme.TEXT_DARK);
            String valStr = String.valueOf(values[i]);
            int valW = g2.getFontMetrics().stringWidth(valStr);
            g2.drawString(valStr, cx + (colWidth - valW) / 2, cy - 4);

            // Column label below bar
            g2.setFont(UITheme.FONT_SMALL);
            g2.setColor(UITheme.TEXT_MUTED);
            int lblW = g2.getFontMetrics().stringWidth(labels[i]);
            g2.drawString(labels[i], cx + (colWidth - lblW) / 2, chartY + chartH - 4);
        }

        // Helpful coaching footnote
        g2.setFont(UITheme.FONT_SMALL);
        g2.setColor(UITheme.TEXT_SECONDARY);
        g2.drawString("⭐ Tip: Maintain 80%+ consistency to achieve maximum results and reach your target weight!", 22, h - 16);
    }
}
