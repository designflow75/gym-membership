package com.smartgym.ui;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.*;

/**
 * Centralized Modern Light UI Theme for Smart Gym Manager.
 * Provides high-contrast, clean typography, soft card borders,
 * and component factories.
 */
public class UITheme {

    // Modern Light Theme Color Palette
    public static final Color BG_APP         = new Color(241, 245, 249);  // #F1F5F9 Soft Slate
    public static final Color BG_CARD        = new Color(255, 255, 255);  // #FFFFFF Crisp White
    public static final Color BG_CARD_ALT    = new Color(248, 250, 252);  // #F8FAFC Off-White
    public static final Color BG_INPUT       = new Color(255, 255, 255);  // #FFFFFF White Input
    public static final Color BORDER_COLOR   = new Color(203, 213, 225);  // #CBD5E1 Slate Border
    public static final Color BORDER_LIGHT   = new Color(226, 232, 240);  // #E2E8F0 Soft Border

    public static final Color PRIMARY        = new Color(37, 99, 235);    // #2563EB Vibrant Royal Blue
    public static final Color PRIMARY_DARK   = new Color(29, 78, 216);    // #1D4ED8 Darker Blue
    public static final Color PRIMARY_LIGHT  = new Color(239, 246, 255);  // #EFF6FF Light Blue Tint
    public static final Color SUCCESS        = new Color(16, 185, 129);   // #10B981 Emerald Green
    public static final Color SUCCESS_DARK   = new Color(5, 150, 105);    // #059669
    public static final Color WARNING        = new Color(217, 119, 6);    // #D97706 Warm Amber
    public static final Color DANGER         = new Color(220, 38, 38);    // #DC2626 Crimson Red
    public static final Color PURPLE         = new Color(147, 51, 234);   // #9333EA Purple Accent

    public static final Color TEXT_DARK      = new Color(15, 23, 42);     // #0F172A Deep Charcoal
    public static final Color TEXT_MUTED     = new Color(100, 116, 139);  // #64748B Slate Muted
    public static final Color TEXT_SECONDARY = new Color(51, 65, 85);     // #334155 Slate Secondary

    // Typography
    public static final Font FONT_TITLE      = new Font("Segoe UI", Font.BOLD, 22);
    public static final Font FONT_HEADER     = new Font("Segoe UI", Font.BOLD, 15);
    public static final Font FONT_SUBHEADER  = new Font("Segoe UI", Font.BOLD, 13);
    public static final Font FONT_BODY       = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_BODY_BOLD  = new Font("Segoe UI", Font.BOLD, 13);
    public static final Font FONT_SMALL      = new Font("Segoe UI", Font.PLAIN, 11);
    public static final Font FONT_STAT_NUM   = new Font("Segoe UI", Font.BOLD, 26);

    public static JPanel createCardPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(BG_CARD);
        panel.setBorder(new CompoundBorder(
                new LineBorder(BORDER_LIGHT, 1, true),
                new EmptyBorder(12, 14, 12, 14)
        ));
        return panel;
    }

    public static JButton createButton(String text, Color bg, Color fg) {
        JButton btn = new JButton(text);
        btn.setFont(FONT_BODY_BOLD);
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFocusPainted(false);
        btn.setBorder(new CompoundBorder(
                new LineBorder(bg.darker(), 1, true),
                new EmptyBorder(8, 16, 8, 16)
        ));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Hover Effect
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(bg.darker());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(bg);
            }
        });
        return btn;
    }

    public static JTextField createTextField(int columns) {
        JTextField tf = new JTextField(columns);
        tf.setBackground(BG_INPUT);
        tf.setForeground(TEXT_DARK);
        tf.setCaretColor(PRIMARY);
        tf.setFont(FONT_BODY);
        tf.setBorder(new CompoundBorder(
                new LineBorder(BORDER_COLOR, 1, true),
                new EmptyBorder(6, 10, 6, 10)
        ));
        return tf;
    }

    public static JPasswordField createPasswordField(int columns) {
        JPasswordField pf = new JPasswordField(columns);
        pf.setBackground(BG_INPUT);
        pf.setForeground(TEXT_DARK);
        pf.setCaretColor(PRIMARY);
        pf.setFont(FONT_BODY);
        pf.setBorder(new CompoundBorder(
                new LineBorder(BORDER_COLOR, 1, true),
                new EmptyBorder(6, 10, 6, 10)
        ));
        return pf;
    }

    public static JTextArea createTextArea(int rows, int cols) {
        JTextArea ta = new JTextArea(rows, cols);
        ta.setBackground(BG_CARD_ALT);
        ta.setForeground(TEXT_DARK);
        ta.setCaretColor(PRIMARY);
        ta.setFont(FONT_BODY);
        ta.setLineWrap(true);
        ta.setWrapStyleWord(true);
        ta.setBorder(new CompoundBorder(
                new LineBorder(BORDER_COLOR, 1, true),
                new EmptyBorder(6, 8, 6, 8)
        ));
        return ta;
    }

    public static <T> JComboBox<T> createComboBox(T[] items) {
        JComboBox<T> cb = new JComboBox<>(items);
        cb.setBackground(BG_INPUT);
        cb.setForeground(TEXT_DARK);
        cb.setFont(FONT_BODY);
        cb.setBorder(new LineBorder(BORDER_COLOR, 1, true));
        return cb;
    }

    public static void styleTable(JTable table) {
        table.setBackground(BG_CARD);
        table.setForeground(TEXT_DARK);
        table.setGridColor(BORDER_LIGHT);
        table.setRowHeight(32);
        table.setFont(FONT_BODY);
        table.setSelectionBackground(new Color(219, 234, 254)); // Light Blue selection
        table.setSelectionForeground(PRIMARY_DARK);

        JTableHeader header = table.getTableHeader();
        header.setBackground(new Color(241, 245, 249));
        header.setForeground(TEXT_DARK);
        header.setFont(FONT_SUBHEADER);
        header.setBorder(new LineBorder(BORDER_COLOR));

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        centerRenderer.setBackground(BG_CARD);
        centerRenderer.setForeground(TEXT_DARK);

        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
    }
}
