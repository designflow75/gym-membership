package com.smartgym.ui;

import com.smartgym.model.MemberUser;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

/**
 * Custom Swing component rendering weight loss progress bars and BMI gauges via Java 2D Graphics.
 */
public class WeightProgressChartPanel extends JPanel {

    private MemberUser member;

    public WeightProgressChartPanel(MemberUser member) {
        this.member = member;
        setBackground(UITheme.BG_CARD);
        setPreferredSize(new Dimension(540, 260));
    }

    /**
     * Updates the member data and repaints the panel immediately.
     */
    public void updateMember(MemberUser member) {
        this.member = member;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;
        // Enable Antialiasing for smooth vector shapes and text
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();

        // --- STEP 1: Draw White Card Container ---
        g2.setColor(UITheme.BG_CARD);
        g2.fill(new RoundRectangle2D.Float(8, 8, w - 16, h - 16, 14, 14));
        g2.setColor(UITheme.BORDER_COLOR);
        g2.draw(new RoundRectangle2D.Float(8, 8, w - 16, h - 16, 14, 14));

        if (member == null) {
            g2.setColor(UITheme.TEXT_MUTED);
            g2.setFont(UITheme.FONT_HEADER);
            g2.drawString("No member data available", w / 2 - 80, h / 2);
            return;
        }

        // Read encapsulated metrics
        double init = member.getInitialWeight();
        double curr = member.getCurrentWeight();
        double targ = member.getTargetWeight();
        int pct = member.getProgressPercentage();
        double bmi = member.getBMI();
        String bmiCategory = member.getBMICategory();
        double netChange = member.getNetWeightChange();
        double remaining = member.getRemainingWeight();

        // Panel Title & Goal
        g2.setColor(UITheme.PRIMARY);
        g2.setFont(UITheme.FONT_HEADER);
        g2.drawString("Target Progress & Weight Loss Journey", 22, 34);

        g2.setFont(UITheme.FONT_SMALL);
        g2.setColor(UITheme.TEXT_MUTED);
        g2.drawString("Goal: " + member.getFitnessGoal(), 22, 50);

        // --- STEP 2: Draw 4 Key Stat Badges (Initial, Current, Target, BMI) ---
        int badgeW = (w - 65) / 4;
        drawStatBadge(g2, 22, 64, badgeW, 55, "INITIAL", String.format("%.1f kg", init), UITheme.TEXT_SECONDARY);
        drawStatBadge(g2, 22 + badgeW + 7, 64, badgeW, 55, "CURRENT", String.format("%.1f kg", curr), UITheme.PRIMARY);
        drawStatBadge(g2, 22 + (badgeW + 7) * 2, 64, badgeW, 55, "TARGET", String.format("%.1f kg", targ), UITheme.SUCCESS_DARK);
        drawStatBadge(g2, 22 + (badgeW + 7) * 3, 64, badgeW, 55, "BMI", String.format("%.1f", bmi), UITheme.WARNING);

        // --- STEP 3: Draw Goal Completion Progress Bar ---
        int barX = 22;
        int barY = 142;
        int barW = w - 44;
        int barH = 20;

        g2.setColor(UITheme.TEXT_DARK);
        g2.setFont(UITheme.FONT_SUBHEADER);
        g2.drawString("Goal Completed: " + pct + "%", barX, barY - 7);

        String remStr = String.format("Net Change: %+.1f kg | Remaining: %.1f kg", netChange, remaining);
        g2.setFont(UITheme.FONT_SMALL);
        g2.setColor(UITheme.TEXT_MUTED);
        int remStrWidth = g2.getFontMetrics().stringWidth(remStr);
        g2.drawString(remStr, barX + barW - remStrWidth, barY - 7);

        // Background Track
        g2.setColor(UITheme.BORDER_LIGHT);
        g2.fill(new RoundRectangle2D.Float(barX, barY, barW, barH, 10, 10));
        g2.setColor(UITheme.BORDER_COLOR);
        g2.draw(new RoundRectangle2D.Float(barX, barY, barW, barH, 10, 10));

        // Fill Progress Bar with smooth Gradient (Blue -> Emerald Green)
        int fillW = (int) Math.round((barW * pct) / 100.0);
        if (fillW > 0) {
            GradientPaint gp = new GradientPaint(barX, barY, UITheme.PRIMARY, barX + fillW, barY, UITheme.SUCCESS);
            g2.setPaint(gp);
            g2.fill(new RoundRectangle2D.Float(barX, barY, fillW, barH, 10, 10));
        }

        // --- STEP 4: Draw 4-Tier BMI Color Gauge ---
        int bmiY = 190;
        g2.setColor(UITheme.TEXT_DARK);
        g2.setFont(UITheme.FONT_SUBHEADER);
        g2.drawString("BMI Health Indicator: " + bmiCategory, barX, bmiY);

        int bmiBarY = bmiY + 8;
        int segW = barW / 4;

        // 4 color segments: Underweight (Blue), Normal (Green), Overweight (Amber), Obese (Red)
        g2.setColor(new Color(96, 165, 250)); // Light Blue
        g2.fill(new RoundRectangle2D.Float(barX, bmiBarY, segW, 10, 4, 4));

        g2.setColor(new Color(52, 211, 153)); // Emerald Green
        g2.fill(new RoundRectangle2D.Float(barX + segW, bmiBarY, segW, 10, 4, 4));

        g2.setColor(new Color(251, 191, 36)); // Amber
        g2.fill(new RoundRectangle2D.Float(barX + segW * 2, bmiBarY, segW, 10, 4, 4));

        g2.setColor(new Color(248, 113, 113)); // Red
        g2.fill(new RoundRectangle2D.Float(barX + segW * 3, bmiBarY, segW, 10, 4, 4));

        // Triangle pointer marker positioned dynamically based on calculated BMI
        double markerPos = barX;
        if (bmi <= 18.5) {
            markerPos = barX + (bmi / 18.5) * segW * 0.8;
        } else if (bmi <= 24.9) {
            markerPos = barX + segW + ((bmi - 18.5) / (24.9 - 18.5)) * segW;
        } else if (bmi <= 29.9) {
            markerPos = barX + segW * 2 + ((bmi - 24.9) / (29.9 - 24.9)) * segW;
        } else {
            markerPos = barX + segW * 3 + Math.min(1.0, (bmi - 30.0) / 10.0) * segW;
        }

        // Draw Triangle Marker
        g2.setColor(UITheme.TEXT_DARK);
        Polygon pointer = new Polygon();
        pointer.addPoint((int) markerPos, bmiBarY + 12);
        pointer.addPoint((int) markerPos - 5, bmiBarY + 20);
        pointer.addPoint((int) markerPos + 5, bmiBarY + 20);
        g2.fill(pointer);

        // Category Labels
        g2.setFont(UITheme.FONT_SMALL);
        g2.setColor(UITheme.TEXT_MUTED);
        g2.drawString("< 18.5 (Under)", barX, bmiBarY + 34);
        g2.drawString("18.5 - 24.9 (Normal)", barX + segW, bmiBarY + 34);
        g2.drawString("25 - 29.9 (Overweight)", barX + segW * 2, bmiBarY + 34);
        g2.drawString("30+ (Obese)", barX + segW * 3, bmiBarY + 34);
    }

    /**
     * Helper to draw a rounded metric stat card.
     */
    private void drawStatBadge(Graphics2D g2, int x, int y, int w, int h, String title, String value, Color valColor) {
        g2.setColor(UITheme.BG_CARD_ALT);
        g2.fill(new RoundRectangle2D.Float(x, y, w, h, 8, 8));
        g2.setColor(UITheme.BORDER_LIGHT);
        g2.draw(new RoundRectangle2D.Float(x, y, w, h, 8, 8));

        g2.setFont(UITheme.FONT_SMALL);
        g2.setColor(UITheme.TEXT_MUTED);
        g2.drawString(title, x + 8, y + 18);

        g2.setFont(UITheme.FONT_HEADER);
        g2.setColor(valColor);
        g2.drawString(value, x + 8, y + 42);
    }
}
