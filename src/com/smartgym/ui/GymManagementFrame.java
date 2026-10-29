package com.smartgym.ui;

import com.smartgym.model.MemberUser;
import com.smartgym.model.TrainerUser;
import com.smartgym.model.User;
import com.smartgym.service.AttendanceService;
import com.smartgym.service.FitnessService;
import com.smartgym.service.UserService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Main application window managing view transitions (Login, Staff, User) using CardLayout.
 */
public class GymManagementFrame extends JFrame {

    // CardLayout container for swapping between panels
    private final CardLayout cardLayout;
    private final JPanel cardsPanel;

    // Service layer dependencies
    private final UserService userService;
    private final AttendanceService attendanceService;
    private final FitnessService fitnessService;

    // Persistent header navigation bar elements
    private JPanel headerBar;
    private JLabel currentUserLabel;
    private JLabel roleBadgeLabel;
    private JButton logoutButton;

    // Currently authenticated user session
    private User currentUser;

    public GymManagementFrame() {
        super("Smart Gym Manager");

        // Initialize business services
        this.userService = new UserService();
        this.attendanceService = new AttendanceService();
        this.fitnessService = new FitnessService();

        // Initialize CardLayout container
        this.cardLayout = new CardLayout();
        this.cardsPanel = new JPanel(cardLayout);

        initFrame();
    }

    private void initFrame() {
        // Window configuration
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1180, 780);
        setMinimumSize(new Dimension(1020, 680));
        setLocationRelativeTo(null); // Centers window on screen
        getContentPane().setBackground(UITheme.BG_APP);
        setLayout(new BorderLayout());

        // Header Navigation Bar (Hidden on login screen; visible once logged in)
        headerBar = createHeaderBar();
        headerBar.setVisible(false);
        add(headerBar, BorderLayout.NORTH);

        // Add Login Card to the CardLayout deck
        LoginPanel loginPanel = new LoginPanel(userService, this::handleUserLogin);
        cardsPanel.add(loginPanel, "LOGIN");

        add(cardsPanel, BorderLayout.CENTER);

        // Display Login panel on launch
        cardLayout.show(cardsPanel, "LOGIN");
    }

    /**
     * Creates the persistent top app bar with logo, logged-in user name, and logout button.
     */
    private JPanel createHeaderBar() {
        JPanel bar = UITheme.createCardPanel();
        bar.setLayout(new BorderLayout());
        bar.setBorder(new EmptyBorder(8, 16, 8, 16));

        // Left Branding
        JLabel logo = new JLabel("⚡ SMART GYM MANAGER");
        logo.setFont(UITheme.FONT_TITLE);
        logo.setForeground(UITheme.PRIMARY);
        bar.add(logo, BorderLayout.WEST);

        // Right side: User information badge and Logout button
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        right.setBackground(UITheme.BG_CARD);

        currentUserLabel = new JLabel("Welcome");
        currentUserLabel.setFont(UITheme.FONT_BODY_BOLD);
        currentUserLabel.setForeground(UITheme.TEXT_DARK);

        roleBadgeLabel = new JLabel("[ ROLE ]");
        roleBadgeLabel.setFont(UITheme.FONT_SMALL);
        roleBadgeLabel.setForeground(UITheme.SUCCESS_DARK);

        logoutButton = UITheme.createButton("Logout 🔒", UITheme.DANGER, Color.WHITE);
        logoutButton.addActionListener(e -> logout());

        right.add(currentUserLabel);
        right.add(roleBadgeLabel);
        right.add(logoutButton);

        bar.add(right, BorderLayout.EAST);
        return bar;
    }

    /**
     * Callback triggered when a user logs in successfully from LoginPanel.
     * Demonstrates Polymorphic view routing:
     * - If Trainer -> opens StaffPanel (attendance marking, diets, routines)
     * - If Member  -> opens UserPanel (progress charts, column bars, diet viewer)
     */
    private void handleUserLogin(User user) {
        this.currentUser = user;

        // Update header user information
        currentUserLabel.setText(user.getFullName() + " (" + user.getUsername() + ")");
        roleBadgeLabel.setText("[" + user.getRole() + "]");
        if ("TRAINER".equalsIgnoreCase(user.getRole())) {
            roleBadgeLabel.setForeground(UITheme.PRIMARY);
        } else {
            roleBadgeLabel.setForeground(UITheme.SUCCESS_DARK);
        }
        headerBar.setVisible(true);

        // Polymorphic Dashboard Navigation using pattern-matching instanceof
        if (user instanceof TrainerUser trainer) {
            StaffPanel staffPanel = new StaffPanel(userService, attendanceService, fitnessService, trainer);
            cardsPanel.add(staffPanel, "STAFF");
            cardLayout.show(cardsPanel, "STAFF");
        } else if (user instanceof MemberUser member) {
            UserPanel userPanel = new UserPanel(userService, attendanceService, fitnessService, member);
            cardsPanel.add(userPanel, "USER");
            cardLayout.show(cardsPanel, "USER");
        }

        revalidate();
        repaint();
    }

    /**
     * Logs out the user and safely resets view to the Login card.
     */
    private void logout() {
        int opt = JOptionPane.showConfirmDialog(
                this, 
                "Are you sure you want to sign out?", 
                "Confirm Logout", 
                JOptionPane.YES_NO_OPTION
        );
        if (opt == JOptionPane.YES_OPTION) {
            this.currentUser = null;
            headerBar.setVisible(false);
            cardLayout.show(cardsPanel, "LOGIN");
        }
    }
}
