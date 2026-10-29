package com.smartgym.ui;

import com.smartgym.exception.AuthenticationException;
import com.smartgym.exception.GymException;
import com.smartgym.model.MemberUser;
import com.smartgym.model.TrainerUser;
import com.smartgym.model.User;
import com.smartgym.service.UserService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Authentication and registration panel supporting sign-in, account creation, and quick demo access.
 */
public class LoginPanel extends JPanel {

    private final UserService userService;
    private final LoginListener loginListener;

    public interface LoginListener {
        void onLoginSuccess(User user);
    }

    public LoginPanel(UserService userService, LoginListener listener) {
        this.userService = userService;
        this.loginListener = listener;

        setLayout(new GridBagLayout());
        setBackground(UITheme.BG_APP);

        initUI();
    }

    private void initUI() {
        JPanel centerContainer = new JPanel();
        centerContainer.setLayout(new BoxLayout(centerContainer, BoxLayout.Y_AXIS));
        centerContainer.setBackground(UITheme.BG_APP);
        centerContainer.setPreferredSize(new Dimension(460, 640));

        // App Branding Header
        JPanel brandPanel = new JPanel();
        brandPanel.setLayout(new BoxLayout(brandPanel, BoxLayout.Y_AXIS));
        brandPanel.setBackground(UITheme.BG_APP);
        brandPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel logoLabel = new JLabel("⚡ SMART GYM MANAGER");
        logoLabel.setFont(UITheme.FONT_TITLE);
        logoLabel.setForeground(UITheme.PRIMARY);
        logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subLabel = new JLabel("Staff & Member Portal");
        subLabel.setFont(UITheme.FONT_SUBHEADER);
        subLabel.setForeground(UITheme.TEXT_MUTED);
        subLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        brandPanel.add(logoLabel);
        brandPanel.add(Box.createVerticalStrut(2));
        brandPanel.add(subLabel);
        brandPanel.add(Box.createVerticalStrut(14));

        centerContainer.add(brandPanel);

        // Tabbed Pane for Sign In vs Create Account
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(UITheme.FONT_SUBHEADER);
        tabbedPane.setBackground(UITheme.BG_CARD);
        tabbedPane.setForeground(UITheme.TEXT_DARK);

        tabbedPane.addTab("  Sign In  ", createSignInPanel());
        tabbedPane.addTab("  Create Account  ", createSignUpPanel());

        centerContainer.add(tabbedPane);

        // Quick 1-Click Access for Viva Demo
        centerContainer.add(Box.createVerticalStrut(12));
        centerContainer.add(createQuickAccessPanel());

        add(centerContainer);
    }

    private JPanel createSignInPanel() {
        JPanel panel = UITheme.createCardPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(18, 20, 18, 20));

        JLabel titleLbl = new JLabel("Welcome Back!");
        titleLbl.setFont(UITheme.FONT_HEADER);
        titleLbl.setForeground(UITheme.TEXT_DARK);
        panel.add(titleLbl);

        JLabel subLbl = new JLabel("Sign in to access your personal gym dashboard.");
        subLbl.setFont(UITheme.FONT_SMALL);
        subLbl.setForeground(UITheme.TEXT_MUTED);
        panel.add(subLbl);
        panel.add(Box.createVerticalStrut(16));

        // Username
        JLabel uLbl = new JLabel("Username");
        uLbl.setFont(UITheme.FONT_BODY_BOLD);
        uLbl.setForeground(UITheme.TEXT_DARK);
        JTextField userField = UITheme.createTextField(20);
        userField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));

        panel.add(uLbl);
        panel.add(Box.createVerticalStrut(4));
        panel.add(userField);
        panel.add(Box.createVerticalStrut(12));

        // Password
        JLabel pLbl = new JLabel("Password");
        pLbl.setFont(UITheme.FONT_BODY_BOLD);
        pLbl.setForeground(UITheme.TEXT_DARK);
        JPasswordField passField = UITheme.createPasswordField(20);
        passField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));

        panel.add(pLbl);
        panel.add(Box.createVerticalStrut(4));
        panel.add(passField);
        panel.add(Box.createVerticalStrut(20));

        // Login Button
        JButton loginBtn = UITheme.createButton("Sign In to Portal →", UITheme.PRIMARY, Color.WHITE);
        loginBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        loginBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

        loginBtn.addActionListener(e -> {
            String username = userField.getText().trim();
            String password = new String(passField.getPassword()).trim();
            try {
                User user = userService.login(username, password);
                if (loginListener != null) {
                    loginListener.onLoginSuccess(user);
                }
            } catch (AuthenticationException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Login Failed", JOptionPane.WARNING_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "System error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        panel.add(loginBtn);
        return panel;
    }

    private JPanel createSignUpPanel() {
        JPanel panel = UITheme.createCardPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(16, 20, 16, 20));

        JLabel titleLbl = new JLabel("Register New Account");
        titleLbl.setFont(UITheme.FONT_HEADER);
        titleLbl.setForeground(UITheme.TEXT_DARK);
        panel.add(titleLbl);
        panel.add(Box.createVerticalStrut(10));

        // Role Selector (Only Member or Trainer)
        JPanel roleRow = new JPanel(new GridLayout(1, 2, 8, 0));
        roleRow.setBackground(UITheme.BG_CARD);
        JLabel rLbl = new JLabel("Account Type:");
        rLbl.setFont(UITheme.FONT_BODY_BOLD);
        rLbl.setForeground(UITheme.TEXT_DARK);
        JComboBox<String> roleCombo = UITheme.createComboBox(new String[]{"Member (Client)", "Trainer (Staff)"});
        roleRow.add(rLbl);
        roleRow.add(roleCombo);
        roleRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        panel.add(roleRow);
        panel.add(Box.createVerticalStrut(8));

        // Form fields
        JTextField nameField = UITheme.createTextField(15);
        JTextField userField = UITheme.createTextField(15);
        JPasswordField passField = UITheme.createPasswordField(15);
        JTextField phoneField = UITheme.createTextField(15);

        // Member-specific fields
        JTextField initWeightField = UITheme.createTextField(6);
        initWeightField.setText("75.0");
        JTextField targetWeightField = UITheme.createTextField(6);
        targetWeightField.setText("68.0");
        JTextField heightField = UITheme.createTextField(6);
        heightField.setText("175.0");
        JComboBox<String> goalCombo = UITheme.createComboBox(MemberUser.FITNESS_GOALS);

        panel.add(createFieldRow("Full Name:", nameField));
        panel.add(Box.createVerticalStrut(6));
        panel.add(createFieldRow("Username:", userField));
        panel.add(Box.createVerticalStrut(6));
        panel.add(createFieldRow("Password:", passField));
        panel.add(Box.createVerticalStrut(6));
        panel.add(createFieldRow("Phone Number:", phoneField));
        panel.add(Box.createVerticalStrut(6));

        JPanel memberExtraPanel = new JPanel(new GridLayout(3, 2, 8, 6));
        memberExtraPanel.setBackground(UITheme.BG_CARD);
        memberExtraPanel.add(new JLabel("Starting Weight (kg):"));
        memberExtraPanel.add(initWeightField);
        memberExtraPanel.add(new JLabel("Target Weight (kg):"));
        memberExtraPanel.add(targetWeightField);
        memberExtraPanel.add(new JLabel("Height (cm):"));
        memberExtraPanel.add(heightField);
        memberExtraPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));

        panel.add(memberExtraPanel);
        panel.add(Box.createVerticalStrut(6));
        panel.add(createFieldRow("Fitness Goal:", goalCombo));
        panel.add(Box.createVerticalStrut(14));

        JButton createBtn = UITheme.createButton("Create Account ✓", UITheme.SUCCESS_DARK, Color.WHITE);
        createBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        createBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

        createBtn.addActionListener(e -> {
            String role = (String) roleCombo.getSelectedItem();
            String name = nameField.getText().trim();
            String uname = userField.getText().trim();
            String pass = new String(passField.getPassword()).trim();
            String phone = phoneField.getText().trim();

            try {
                if (role.startsWith("Member")) {
                    double currW = Double.parseDouble(initWeightField.getText().trim());
                    double targW = Double.parseDouble(targetWeightField.getText().trim());
                    double height = Double.parseDouble(heightField.getText().trim());
                    String goal = (String) goalCombo.getSelectedItem();

                    userService.registerMember(uname, pass, name, phone, uname + "@smartgym.com",
                            "Monthly", "Regular", "Sneha Sen", goal, currW, currW, targW, height);

                    JOptionPane.showMessageDialog(this, "Member account created successfully!\nDiet & Workout plans are currently blank and ready for your trainer to configure.", "Account Created", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    userService.registerTrainer(uname, pass, name, phone, uname + "@smartgym.com",
                            TrainerUser.SPECIALIZATIONS[0], TrainerUser.SHIFTS[0], 35000.0);
                    JOptionPane.showMessageDialog(this, "Trainer account created successfully!", "Account Created", JOptionPane.INFORMATION_MESSAGE);
                }

                // Clear input fields
                nameField.setText("");
                userField.setText("");
                passField.setText("");
                phoneField.setText("");
            } catch (GymException ge) {
                JOptionPane.showMessageDialog(this, ge.getMessage(), "Validation Error", JOptionPane.WARNING_MESSAGE);
            } catch (NumberFormatException nfe) {
                JOptionPane.showMessageDialog(this, "Please enter valid numeric values for weight and height.", "Input Error", JOptionPane.WARNING_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        panel.add(createBtn);
        return panel;
    }

    private JPanel createQuickAccessPanel() {
        JPanel panel = UITheme.createCardPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(10, 14, 10, 14));

        JLabel qLbl = new JLabel("⚡ 1-Click Quick Demo Login (For Examination & Viva):");
        qLbl.setFont(UITheme.FONT_SMALL);
        qLbl.setForeground(UITheme.TEXT_MUTED);
        qLbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(qLbl);
        panel.add(Box.createVerticalStrut(8));

        JPanel btnRow = new JPanel(new GridLayout(1, 2, 10, 0));
        btnRow.setBackground(UITheme.BG_CARD);

        JButton trainerQuick = UITheme.createButton("🏋️ Trainer (Sneha)", UITheme.PRIMARY, Color.WHITE);
        JButton memberQuick = UITheme.createButton("🏃 Member (Rahul)", UITheme.SUCCESS_DARK, Color.WHITE);

        trainerQuick.addActionListener(e -> quickLogin("trainer1", "trainer123"));
        memberQuick.addActionListener(e -> quickLogin("rahul", "member123"));

        btnRow.add(trainerQuick);
        btnRow.add(memberQuick);
        panel.add(btnRow);

        return panel;
    }

    private void quickLogin(String username, String password) {
        try {
            User user = userService.login(username, password);
            if (loginListener != null) {
                loginListener.onLoginSuccess(user);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Quick login failed: " + ex.getMessage());
        }
    }

    private JPanel createFieldRow(String label, JComponent comp) {
        JPanel row = new JPanel(new BorderLayout(8, 0));
        row.setBackground(UITheme.BG_CARD);
        JLabel l = new JLabel(label);
        l.setPreferredSize(new Dimension(120, 26));
        l.setFont(UITheme.FONT_BODY);
        l.setForeground(UITheme.TEXT_DARK);
        row.add(l, BorderLayout.WEST);
        row.add(comp, BorderLayout.CENTER);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        return row;
    }
}
