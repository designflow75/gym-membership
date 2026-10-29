package com.smartgym.ui;

import com.smartgym.model.Attendance;
import com.smartgym.model.DietPlan;
import com.smartgym.model.MemberUser;
import com.smartgym.model.WorkoutPlan;
import com.smartgym.service.AttendanceService;
import com.smartgym.service.FitnessService;
import com.smartgym.service.UserService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Member personal fitness hub displaying weight loss progress, attendance columns, and diet/workout plans.
 */
public class UserPanel extends JPanel {

    private final UserService userService;
    private final AttendanceService attendanceService;
    private final FitnessService fitnessService;
    private MemberUser currentMember;

    // Charts
    private WeightProgressChartPanel weightChartPanel;
    private AttendanceChartPanel attendanceChartPanel;

    // Input fields for weight
    private JTextField currWeightField;
    private JTextField targWeightField;
    private JTextField heightField;

    // Diet components
    private JLabel dietCalLbl;
    private JLabel dietProLbl;
    private JLabel dietCarbLbl;
    private JLabel dietFatLbl;
    private JLabel dietWaterLbl;
    private JTextArea dietBreakfastArea;
    private JTextArea dietLunchArea;
    private JTextArea dietSnacksArea;
    private JTextArea dietDinnerArea;
    private JTextArea userDailyDietLogArea;

    // Workout components
    private JLabel workoutGoalLbl;
    private JLabel workoutDaysLbl;
    private JTextArea workoutMonArea;
    private JTextArea workoutTueArea;
    private JTextArea workoutWedArea;
    private JTextArea workoutThuArea;
    private JTextArea workoutFriArea;
    private JTextArea workoutSatArea;
    private JTextArea workoutSunArea;
    private JTextArea workoutCardioArea;
    private JTextArea userWorkoutLogArea;

    // Attendance Table
    private DefaultTableModel attTableModel;
    private JTable attTable;

    public UserPanel(UserService userService, AttendanceService attendanceService, FitnessService fitnessService, MemberUser member) {
        this.userService = userService;
        this.attendanceService = attendanceService;
        this.fitnessService = fitnessService;
        this.currentMember = member;

        setLayout(new BorderLayout());
        setBackground(UITheme.BG_APP);

        initUI();
        loadAllMemberData();
    }

    private void initUI() {
        // Top Welcome Header Banner
        add(createHeaderBanner(), BorderLayout.NORTH);

        // Tabbed Pane for Member Features
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(UITheme.FONT_SUBHEADER);
        tabbedPane.setBackground(UITheme.BG_CARD);
        tabbedPane.setForeground(UITheme.TEXT_DARK);

        tabbedPane.addTab("  📈 Weight & Goal Progress  ", createWeightProgressTab());
        tabbedPane.addTab("  📊 Attendance (Column Chart)  ", createAttendanceTab());
        tabbedPane.addTab("  🥗 My Diet & Nutrition Plan  ", createDietTab());
        tabbedPane.addTab("  🏋️ My Workout Routine  ", createWorkoutTab());

        add(tabbedPane, BorderLayout.CENTER);
    }

    private JPanel createHeaderBanner() {
        JPanel banner = UITheme.createCardPanel();
        banner.setLayout(new BorderLayout(14, 0));
        banner.setBorder(new EmptyBorder(12, 16, 12, 16));

        JPanel left = new JPanel();
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        left.setBackground(UITheme.BG_CARD);

        JLabel welcomeLbl = new JLabel("Welcome, " + currentMember.getFullName() + "!");
        welcomeLbl.setFont(UITheme.FONT_TITLE);
        welcomeLbl.setForeground(UITheme.PRIMARY);

        JLabel subLbl = new JLabel(String.format("Membership: %s (%s) • Personal Coach: %s • Goal: %s",
                currentMember.getMembershipPlan(), currentMember.getMembershipType(),
                currentMember.getAssignedTrainer(), currentMember.getFitnessGoal()));
        subLbl.setFont(UITheme.FONT_SMALL);
        subLbl.setForeground(UITheme.TEXT_MUTED);

        left.add(welcomeLbl);
        left.add(Box.createVerticalStrut(2));
        left.add(subLbl);
        banner.add(left, BorderLayout.WEST);

        // Right side badge
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        right.setBackground(UITheme.BG_CARD);

        JLabel bmiBadge = new JLabel(String.format("BMI: %.1f", currentMember.getBMI()));
        bmiBadge.setFont(UITheme.FONT_HEADER);
        bmiBadge.setForeground(UITheme.SUCCESS_DARK);
        right.add(bmiBadge);

        banner.add(right, BorderLayout.EAST);
        return banner;
    }

    // ──────────────────────────── TAB 1: WEIGHT LOSS & GOAL PROGRESS ────────────────────────────
    private JPanel createWeightProgressTab() {
        JPanel panel = new JPanel(new BorderLayout(14, 0));
        panel.setBackground(UITheme.BG_APP);
        panel.setBorder(new EmptyBorder(14, 14, 14, 14));

        // Left Card: Interactive Metric Inputs
        JPanel inputCard = UITheme.createCardPanel();
        inputCard.setLayout(new BoxLayout(inputCard, BoxLayout.Y_AXIS));
        inputCard.setPreferredSize(new Dimension(320, 480));

        JLabel title = new JLabel("Update Your Metrics");
        title.setFont(UITheme.FONT_HEADER);
        title.setForeground(UITheme.PRIMARY);
        inputCard.add(title);

        JLabel sub = new JLabel("Input your latest scale measurements.");
        sub.setFont(UITheme.FONT_SMALL);
        sub.setForeground(UITheme.TEXT_MUTED);
        inputCard.add(sub);
        inputCard.add(Box.createVerticalStrut(14));

        currWeightField = UITheme.createTextField(10);
        currWeightField.setText(String.valueOf(currentMember.getCurrentWeight()));

        targWeightField = UITheme.createTextField(10);
        targWeightField.setText(String.valueOf(currentMember.getTargetWeight()));

        heightField = UITheme.createTextField(10);
        heightField.setText(String.valueOf(currentMember.getHeightCm()));

        inputCard.add(createInputRow("Current Weight (kg):", currWeightField));
        inputCard.add(Box.createVerticalStrut(10));
        inputCard.add(createInputRow("Target Weight (kg):", targWeightField));
        inputCard.add(Box.createVerticalStrut(10));
        inputCard.add(createInputRow("Height (cm):", heightField));
        inputCard.add(Box.createVerticalStrut(18));

        JButton updateBtn = UITheme.createButton("Update My Progress 🚀", UITheme.SUCCESS_DARK, Color.WHITE);
        updateBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        updateBtn.addActionListener(e -> saveUpdatedBodyMetrics());
        inputCard.add(updateBtn);
        inputCard.add(Box.createVerticalStrut(14));

        JTextArea tip = new JTextArea("Tip: Log your weight once a week first thing in the morning for the most reliable measurement.");
        tip.setFont(UITheme.FONT_SMALL);
        tip.setForeground(UITheme.TEXT_MUTED);
        tip.setBackground(UITheme.BG_CARD);
        tip.setWrapStyleWord(true);
        tip.setLineWrap(true);
        tip.setEditable(false);
        inputCard.add(tip);

        panel.add(inputCard, BorderLayout.WEST);

        // Center / Right: The Graphical Chart Panel
        weightChartPanel = new WeightProgressChartPanel(currentMember);
        panel.add(weightChartPanel, BorderLayout.CENTER);

        return panel;
    }

    // ──────────────────────────── TAB 2: ATTENDANCE (COLUMN CHART) ────────────────────────────
    private JPanel createAttendanceTab() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBackground(UITheme.BG_APP);
        panel.setBorder(new EmptyBorder(14, 14, 14, 14));

        // Top: Graphical Column Chart
        attendanceChartPanel = new AttendanceChartPanel(null);
        panel.add(attendanceChartPanel, BorderLayout.NORTH);

        // Bottom: Log Table Card
        JPanel tableCard = UITheme.createCardPanel();
        tableCard.setLayout(new BorderLayout(0, 8));

        JPanel tblHeader = new JPanel(new BorderLayout());
        tblHeader.setBackground(UITheme.BG_CARD);

        JLabel tblTitle = new JLabel("Personal Check-In History");
        tblTitle.setFont(UITheme.FONT_HEADER);
        tblTitle.setForeground(UITheme.PRIMARY);

        JButton refreshBtn = UITheme.createButton("Refresh My Attendance 🔄", UITheme.PRIMARY_LIGHT, UITheme.PRIMARY);
        refreshBtn.addActionListener(e -> refreshAttendance());

        tblHeader.add(tblTitle, BorderLayout.WEST);
        tblHeader.add(refreshBtn, BorderLayout.EAST);
        tableCard.add(tblHeader, BorderLayout.NORTH);

        String[] cols = {"Date", "Check-In Time", "Status", "Workout Focus", "Trainer Notes"};
        attTableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        attTable = new JTable(attTableModel);
        UITheme.styleTable(attTable);

        JScrollPane scroll = new JScrollPane(attTable);
        scroll.getViewport().setBackground(UITheme.BG_CARD);
        tableCard.add(scroll, BorderLayout.CENTER);

        panel.add(tableCard, BorderLayout.CENTER);
        return panel;
    }

    // ──────────────────────────── TAB 3: MY DIET PLAN ────────────────────────────
    private JPanel createDietTab() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBackground(UITheme.BG_APP);
        panel.setBorder(new EmptyBorder(14, 14, 14, 14));

        JPanel form = UITheme.createCardPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));

        // Macro Badges Row
        JPanel macroRow = new JPanel(new GridLayout(1, 5, 10, 0));
        macroRow.setBackground(UITheme.BG_CARD);

        dietCalLbl = new JLabel("0 kcal", JLabel.CENTER);
        dietProLbl = new JLabel("0 g", JLabel.CENTER);
        dietCarbLbl = new JLabel("0 g", JLabel.CENTER);
        dietFatLbl = new JLabel("0 g", JLabel.CENTER);
        dietWaterLbl = new JLabel("0 L", JLabel.CENTER);

        macroRow.add(createReadoutBadge("CALORIES", dietCalLbl, UITheme.PRIMARY));
        macroRow.add(createReadoutBadge("PROTEIN", dietProLbl, UITheme.SUCCESS_DARK));
        macroRow.add(createReadoutBadge("CARBS", dietCarbLbl, UITheme.WARNING));
        macroRow.add(createReadoutBadge("FATS", dietFatLbl, UITheme.DANGER));
        macroRow.add(createReadoutBadge("WATER INTAKE", dietWaterLbl, UITheme.PRIMARY));
        macroRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 65));
        form.add(macroRow);
        form.add(Box.createVerticalStrut(10));

        // Meals Grid
        JPanel mealsGrid = new JPanel(new GridLayout(2, 2, 10, 10));
        mealsGrid.setBackground(UITheme.BG_CARD);

        dietBreakfastArea = UITheme.createTextArea(3, 20);
        dietLunchArea = UITheme.createTextArea(3, 20);
        dietSnacksArea = UITheme.createTextArea(3, 20);
        dietDinnerArea = UITheme.createTextArea(3, 20);

        dietBreakfastArea.setEditable(false);
        dietLunchArea.setEditable(false);
        dietSnacksArea.setEditable(false);
        dietDinnerArea.setEditable(false);

        mealsGrid.add(createMealBox("🍳 BREAKFAST (Assigned by Trainer)", dietBreakfastArea));
        mealsGrid.add(createMealBox("🥗 LUNCH (Assigned by Trainer)", dietLunchArea));
        mealsGrid.add(createMealBox("🍎 EVENING SNACKS (Assigned by Trainer)", dietSnacksArea));
        mealsGrid.add(createMealBox("🍲 DINNER (Assigned by Trainer)", dietDinnerArea));
        form.add(mealsGrid);
        form.add(Box.createVerticalStrut(10));

        // Member's Daily Journal / Food Log (Editable by Member!)
        JPanel logCard = new JPanel(new BorderLayout(0, 6));
        logCard.setBackground(UITheme.BG_CARD);

        JLabel logLbl = new JLabel("✏️ My Daily Food & Water Journal (Member Editable):");
        logLbl.setFont(UITheme.FONT_SUBHEADER);
        logLbl.setForeground(UITheme.SUCCESS_DARK);

        userDailyDietLogArea = UITheme.createTextArea(3, 30);
        JButton saveLogBtn = UITheme.createButton("Save My Daily Food Log 💾", UITheme.SUCCESS_DARK, Color.WHITE);
        saveLogBtn.addActionListener(e -> saveUserDietLog());

        logCard.add(logLbl, BorderLayout.NORTH);
        logCard.add(new JScrollPane(userDailyDietLogArea), BorderLayout.CENTER);
        logCard.add(saveLogBtn, BorderLayout.SOUTH);

        form.add(logCard);

        JScrollPane scroll = new JScrollPane(form);
        scroll.setBorder(null);
        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    // ──────────────────────────── TAB 4: MY WORKOUT ROUTINE ────────────────────────────
    private JPanel createWorkoutTab() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBackground(UITheme.BG_APP);
        panel.setBorder(new EmptyBorder(14, 14, 14, 14));

        JPanel form = UITheme.createCardPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));

        // Header specs
        JPanel specRow = new JPanel(new GridLayout(1, 2, 10, 0));
        specRow.setBackground(UITheme.BG_CARD);
        workoutGoalLbl = new JLabel("Goal: Loading...", JLabel.CENTER);
        workoutDaysLbl = new JLabel("Split: 5 Days", JLabel.CENTER);

        specRow.add(createReadoutBadge("TARGET GOAL", workoutGoalLbl, UITheme.PRIMARY));
        specRow.add(createReadoutBadge("WEEKLY SPLIT", workoutDaysLbl, UITheme.SUCCESS_DARK));
        specRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 65));
        form.add(specRow);
        form.add(Box.createVerticalStrut(10));

        // 7 Days Grid
        JPanel daysGrid = new JPanel(new GridLayout(4, 2, 10, 10));
        daysGrid.setBackground(UITheme.BG_CARD);

        workoutMonArea = UITheme.createTextArea(2, 20);
        workoutTueArea = UITheme.createTextArea(2, 20);
        workoutWedArea = UITheme.createTextArea(2, 20);
        workoutThuArea = UITheme.createTextArea(2, 20);
        workoutFriArea = UITheme.createTextArea(2, 20);
        workoutSatArea = UITheme.createTextArea(2, 20);
        workoutSunArea = UITheme.createTextArea(2, 20);
        workoutCardioArea = UITheme.createTextArea(2, 20);

        workoutMonArea.setEditable(false);
        workoutTueArea.setEditable(false);
        workoutWedArea.setEditable(false);
        workoutThuArea.setEditable(false);
        workoutFriArea.setEditable(false);
        workoutSatArea.setEditable(false);
        workoutSunArea.setEditable(false);
        workoutCardioArea.setEditable(false);

        daysGrid.add(createMealBox("MONDAY", workoutMonArea));
        daysGrid.add(createMealBox("TUESDAY", workoutTueArea));
        daysGrid.add(createMealBox("WEDNESDAY", workoutWedArea));
        daysGrid.add(createMealBox("THURSDAY", workoutThuArea));
        daysGrid.add(createMealBox("FRIDAY", workoutFriArea));
        daysGrid.add(createMealBox("SATURDAY", workoutSatArea));
        daysGrid.add(createMealBox("SUNDAY", workoutSunArea));
        daysGrid.add(createMealBox("CARDIO & MOBILITY", workoutCardioArea));

        form.add(daysGrid);
        form.add(Box.createVerticalStrut(10));

        // Member's Workout Journal (Member Editable!)
        JPanel logCard = new JPanel(new BorderLayout(0, 6));
        logCard.setBackground(UITheme.BG_CARD);

        JLabel logLbl = new JLabel("✏️ My Workout Log & PRs (Member Editable):");
        logLbl.setFont(UITheme.FONT_SUBHEADER);
        logLbl.setForeground(UITheme.SUCCESS_DARK);

        userWorkoutLogArea = UITheme.createTextArea(3, 30);
        JButton saveWorkoutLogBtn = UITheme.createButton("Save My Workout Log 💾", UITheme.SUCCESS_DARK, Color.WHITE);
        saveWorkoutLogBtn.addActionListener(e -> saveUserWorkoutLog());

        logCard.add(logLbl, BorderLayout.NORTH);
        logCard.add(new JScrollPane(userWorkoutLogArea), BorderLayout.CENTER);
        logCard.add(saveWorkoutLogBtn, BorderLayout.SOUTH);

        form.add(logCard);

        JScrollPane scroll = new JScrollPane(form);
        scroll.setBorder(null);
        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    // ──────────────────────────── ACTIONS & DATA SYNC ────────────────────────────
    private void saveUpdatedBodyMetrics() {
        try {
            double curr = Double.parseDouble(currWeightField.getText().trim());
            double targ = Double.parseDouble(targWeightField.getText().trim());
            double height = Double.parseDouble(heightField.getText().trim());

            fitnessService.updateMemberBodyMetrics(currentMember.getUserId(), curr, targ, height);

            currentMember.setCurrentWeight(curr);
            currentMember.setTargetWeight(targ);
            currentMember.setHeightCm(height);

            weightChartPanel.updateMember(currentMember);
            JOptionPane.showMessageDialog(this, "Body metrics updated successfully! Goal progress recalculated.", "Progress Updated", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error updating metrics: " + ex.getMessage(), "Input Error", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void saveUserDietLog() {
        try {
            DietPlan plan = fitnessService.getDietPlan(currentMember.getUserId());
            plan.setUserDailyLog(userDailyDietLogArea.getText().trim());
            fitnessService.saveDietPlan(plan);
            JOptionPane.showMessageDialog(this, "Daily food journal saved successfully!");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error saving log: " + ex.getMessage());
        }
    }

    private void saveUserWorkoutLog() {
        try {
            WorkoutPlan plan = fitnessService.getWorkoutPlan(currentMember.getUserId());
            plan.setUserWorkoutLog(userWorkoutLogArea.getText().trim());
            fitnessService.saveWorkoutPlan(plan);
            JOptionPane.showMessageDialog(this, "Workout journal saved successfully!");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error saving log: " + ex.getMessage());
        }
    }

    public void refreshAttendance() {
        try {
            List<Attendance> list = attendanceService.getAttendanceByMember(currentMember.getUserId());
            attendanceChartPanel.updateData(list);
            attTableModel.setRowCount(0);
            for (Attendance a : list) {
                attTableModel.addRow(a.toTableRow());
            }
        } catch (Exception ignored) {
        }
    }

    public void loadAllMemberData() {
        // 1. Attendance
        refreshAttendance();

        // 2. Diet Plan
        try {
            DietPlan diet = fitnessService.getDietPlan(currentMember.getUserId());
            if (!diet.isAssigned()) {
                dietCalLbl.setText("Pending");
                dietProLbl.setText("0 g");
                dietCarbLbl.setText("0 g");
                dietFatLbl.setText("0 g");
                dietWaterLbl.setText("0 L");

                dietBreakfastArea.setText("No meal prescribed yet. Please consult your trainer.");
                dietLunchArea.setText("No meal prescribed yet. Please consult your trainer.");
                dietSnacksArea.setText("No meal prescribed yet. Please consult your trainer.");
                dietDinnerArea.setText("No meal prescribed yet. Please consult your trainer.");
                userDailyDietLogArea.setText(diet.getUserDailyLog());
            } else {
                dietCalLbl.setText(diet.getTargetCalories() + " kcal");
                dietProLbl.setText(diet.getProteinGrams() + " g");
                dietCarbLbl.setText(diet.getCarbsGrams() + " g");
                dietFatLbl.setText(diet.getFatsGrams() + " g");
                dietWaterLbl.setText(diet.getWaterIntakeLiters() + " L");

                dietBreakfastArea.setText(diet.getBreakfast());
                dietLunchArea.setText(diet.getLunch());
                dietSnacksArea.setText(diet.getSnacks());
                dietDinnerArea.setText(diet.getDinner());
                userDailyDietLogArea.setText(diet.getUserDailyLog());
            }
        } catch (Exception ignored) {
        }

        // 3. Workout Routine
        try {
            WorkoutPlan workout = fitnessService.getWorkoutPlan(currentMember.getUserId());
            if (!workout.isAssigned()) {
                workoutGoalLbl.setText("No routine assigned yet");
                workoutDaysLbl.setText("Pending Trainer");

                workoutMonArea.setText("Pending trainer setup.");
                workoutTueArea.setText("Pending trainer setup.");
                workoutWedArea.setText("Pending trainer setup.");
                workoutThuArea.setText("Pending trainer setup.");
                workoutFriArea.setText("Pending trainer setup.");
                workoutSatArea.setText("Pending trainer setup.");
                workoutSunArea.setText("Pending trainer setup.");
                workoutCardioArea.setText("Pending trainer setup.");
                userWorkoutLogArea.setText(workout.getUserWorkoutLog());
            } else {
                workoutGoalLbl.setText(workout.getTargetGoal());
                workoutDaysLbl.setText(workout.getDaysPerWeek() + " Days Split");

                workoutMonArea.setText(workout.getMondayWorkout());
                workoutTueArea.setText(workout.getTuesdayWorkout());
                workoutWedArea.setText(workout.getWednesdayWorkout());
                workoutThuArea.setText(workout.getThursdayWorkout());
                workoutFriArea.setText(workout.getFridayWorkout());
                workoutSatArea.setText(workout.getSaturdayWorkout());
                workoutSunArea.setText(workout.getSundayWorkout());
                workoutCardioArea.setText(workout.getCardioGuidelines());
                userWorkoutLogArea.setText(workout.getUserWorkoutLog());
            }
        } catch (Exception ignored) {
        }
    }

    private JPanel createInputRow(String label, JTextField field) {
        JPanel p = new JPanel(new BorderLayout(6, 4));
        p.setBackground(UITheme.BG_CARD);
        JLabel l = new JLabel(label);
        l.setFont(UITheme.FONT_BODY);
        l.setForeground(UITheme.TEXT_DARK);
        p.add(l, BorderLayout.NORTH);
        p.add(field, BorderLayout.CENTER);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        return p;
    }

    private JPanel createReadoutBadge(String title, JLabel valLbl, Color color) {
        JPanel p = new JPanel(new BorderLayout(2, 2));
        p.setBackground(UITheme.BG_CARD_ALT);
        p.setBorder(BorderFactory.createLineBorder(UITheme.BORDER_LIGHT, 1, true));

        JLabel t = new JLabel(title, JLabel.CENTER);
        t.setFont(UITheme.FONT_SMALL);
        t.setForeground(UITheme.TEXT_MUTED);

        valLbl.setFont(UITheme.FONT_HEADER);
        valLbl.setForeground(color);

        p.add(t, BorderLayout.NORTH);
        p.add(valLbl, BorderLayout.CENTER);
        return p;
    }

    private JPanel createMealBox(String title, JTextArea area) {
        JPanel p = new JPanel(new BorderLayout(4, 4));
        p.setBackground(UITheme.BG_CARD);
        JLabel l = new JLabel(title);
        l.setFont(UITheme.FONT_SUBHEADER);
        l.setForeground(UITheme.PRIMARY);
        p.add(l, BorderLayout.NORTH);
        p.add(new JScrollPane(area), BorderLayout.CENTER);
        return p;
    }
}
