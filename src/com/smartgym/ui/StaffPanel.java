package com.smartgym.ui;

import com.smartgym.exception.GymException;
import com.smartgym.model.*;
import com.smartgym.service.AttendanceService;
import com.smartgym.service.FitnessService;
import com.smartgym.service.UserService;
import com.smartgym.util.FileHandler;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Trainer workspace panel for marking attendance, configuring diets and workouts, and managing staff coaches.
 */
public class StaffPanel extends JPanel {

    private final UserService userService;
    private final AttendanceService attendanceService;
    private final FitnessService fitnessService;
    private final TrainerUser currentTrainer;

    // Component references
    private JComboBox<MemberComboItem> attMemberCombo;
    private DefaultTableModel attendanceTableModel;
    private JTable attendanceTable;

    // Diet Plan Components
    private JComboBox<MemberComboItem> dietMemberCombo;
    private JTextField dietCaloriesField;
    private JTextField dietProteinField;
    private JTextField dietCarbsField;
    private JTextField dietFatsField;
    private JTextField dietWaterField;
    private JTextArea dietBreakfastArea;
    private JTextArea dietLunchArea;
    private JTextArea dietSnacksArea;
    private JTextArea dietDinnerArea;
    private JTextArea dietNotesArea;

    // Workout Plan Components
    private JComboBox<MemberComboItem> workoutMemberCombo;
    private JTextField workoutGoalField;
    private JSpinner workoutDaysSpinner;
    private JTextArea workoutMonArea;
    private JTextArea workoutTueArea;
    private JTextArea workoutWedArea;
    private JTextArea workoutThuArea;
    private JTextArea workoutFriArea;
    private JTextArea workoutSatArea;
    private JTextArea workoutSunArea;
    private JTextArea workoutCardioArea;
    private JTextArea workoutNotesArea;

    // Member Roster Table
    private DefaultTableModel memberRosterModel;
    private JTable memberRosterTable;

    // Staff Directory Table
    private DefaultTableModel staffDirectoryModel;
    private JTable staffDirectoryTable;

    public StaffPanel(UserService userService, AttendanceService attendanceService, FitnessService fitnessService, TrainerUser trainer) {
        this.userService = userService;
        this.attendanceService = attendanceService;
        this.fitnessService = fitnessService;
        this.currentTrainer = trainer;

        setLayout(new BorderLayout());
        setBackground(UITheme.BG_APP);

        initUI();
        refreshAllData();
    }

    private void initUI() {
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(UITheme.FONT_SUBHEADER);
        tabbedPane.setBackground(UITheme.BG_CARD);
        tabbedPane.setForeground(UITheme.TEXT_DARK);

        tabbedPane.addTab("  📋 Mark & View Attendance  ", createAttendanceTab());
        tabbedPane.addTab("  🥗 Create / Edit Diet Plan  ", createDietTab());
        tabbedPane.addTab("  🏋️ Create / Edit Workout Routine  ", createWorkoutTab());
        tabbedPane.addTab("  👥 Members Roster & Weight Updates  ", createMembersTab());
        tabbedPane.addTab("  👔 Staff & Coaches Directory  ", createStaffDirectoryTab());

        add(tabbedPane, BorderLayout.CENTER);
    }

    // ──────────────────────────── TAB 1: ATTENDANCE ────────────────────────────
    private JPanel createAttendanceTab() {
        JPanel panel = new JPanel(new BorderLayout(14, 0));
        panel.setBackground(UITheme.BG_APP);
        panel.setBorder(new EmptyBorder(14, 14, 14, 14));

        // Left Form: Mark Attendance
        JPanel formCard = UITheme.createCardPanel();
        formCard.setLayout(new BoxLayout(formCard, BoxLayout.Y_AXIS));
        formCard.setPreferredSize(new Dimension(340, 500));

        JLabel title = new JLabel("Mark Member Attendance");
        title.setFont(UITheme.FONT_HEADER);
        title.setForeground(UITheme.PRIMARY);
        formCard.add(title);
        formCard.add(Box.createVerticalStrut(12));

        attMemberCombo = new JComboBox<>();
        attMemberCombo.setFont(UITheme.FONT_BODY);
        attMemberCombo.setBackground(UITheme.BG_INPUT);
        attMemberCombo.setForeground(UITheme.TEXT_DARK);

        JTextField dateField = UITheme.createTextField(10);
        dateField.setText(LocalDate.now().toString());

        JTextField timeField = UITheme.createTextField(10);
        timeField.setText(LocalTime.now().format(DateTimeFormatter.ofPattern("hh:mm a")));

        JComboBox<String> statusCombo = UITheme.createComboBox(Attendance.STATUSES);
        JComboBox<String> workoutCombo = UITheme.createComboBox(Attendance.WORKOUT_TYPES);
        JTextField notesField = UITheme.createTextField(15);
        notesField.setText("Completed scheduled session.");

        formCard.add(createFormField("Select Member:", attMemberCombo));
        formCard.add(Box.createVerticalStrut(8));
        formCard.add(createFormField("Date (YYYY-MM-DD):", dateField));
        formCard.add(Box.createVerticalStrut(8));
        formCard.add(createFormField("Time In:", timeField));
        formCard.add(Box.createVerticalStrut(8));
        formCard.add(createFormField("Status:", statusCombo));
        formCard.add(Box.createVerticalStrut(8));
        formCard.add(createFormField("Workout Focus:", workoutCombo));
        formCard.add(Box.createVerticalStrut(8));
        formCard.add(createFormField("Trainer Notes:", notesField));
        formCard.add(Box.createVerticalStrut(18));

        JButton markBtn = UITheme.createButton("Log Attendance Entry ✓", UITheme.SUCCESS_DARK, Color.WHITE);
        markBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        markBtn.addActionListener(e -> {
            MemberComboItem item = (MemberComboItem) attMemberCombo.getSelectedItem();
            if (item == null) {
                JOptionPane.showMessageDialog(this, "Please select a member.");
                return;
            }
            try {
                Attendance att = new Attendance(
                        item.memberId,
                        item.fullName,
                        dateField.getText().trim(),
                        timeField.getText().trim(),
                        (String) statusCombo.getSelectedItem(),
                        (String) workoutCombo.getSelectedItem(),
                        notesField.getText().trim()
                );
                attendanceService.markAttendance(att);
                refreshAttendanceTable();
                JOptionPane.showMessageDialog(this, "Attendance marked successfully for " + item.fullName + " (ID: " + item.memberId + ")!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error logging attendance: " + ex.getMessage());
            }
        });
        formCard.add(markBtn);

        panel.add(formCard, BorderLayout.WEST);

        // Right Table: History
        JPanel tablePanel = new JPanel(new BorderLayout(0, 8));
        tablePanel.setBackground(UITheme.BG_APP);

        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        topBar.setBackground(UITheme.BG_APP);
        JButton refreshAttBtn = UITheme.createButton("Refresh Logs 🔄", UITheme.PRIMARY_LIGHT, UITheme.PRIMARY);
        JButton deleteAttBtn = UITheme.createButton("Delete Selected 🗑", UITheme.DANGER, Color.WHITE);
        JButton exportAttBtn = UITheme.createButton("Export Attendance 📄", UITheme.PRIMARY, Color.WHITE);

        refreshAttBtn.addActionListener(e -> refreshAttendanceTable());
        deleteAttBtn.addActionListener(e -> {
            int row = attendanceTable.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Please select an attendance entry to delete.");
                return;
            }
            int id = (int) attendanceTableModel.getValueAt(row, 0);
            try {
                attendanceService.deleteAttendance(id);
                refreshAttendanceTable();
                JOptionPane.showMessageDialog(this, "Attendance log removed.");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error deleting: " + ex.getMessage());
            }
        });
        exportAttBtn.addActionListener(e -> {
            try {
                List<Attendance> list = attendanceService.getAllAttendance();
                String path = FileHandler.exportAttendanceReport(list, new File("gym_attendance_export.txt"));
                JOptionPane.showMessageDialog(this, "Attendance exported to:\n" + path);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Export failed: " + ex.getMessage());
            }
        });

        topBar.add(refreshAttBtn);
        topBar.add(deleteAttBtn);
        topBar.add(exportAttBtn);
        tablePanel.add(topBar, BorderLayout.NORTH);

        String[] cols = {"Log ID", "Member Name", "Date", "Time", "Status", "Workout Focus", "Notes"};
        attendanceTableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        attendanceTable = new JTable(attendanceTableModel);
        UITheme.styleTable(attendanceTable);

        JScrollPane scroll = new JScrollPane(attendanceTable);
        scroll.getViewport().setBackground(UITheme.BG_CARD);
        tablePanel.add(scroll, BorderLayout.CENTER);

        panel.add(tablePanel, BorderLayout.CENTER);
        return panel;
    }

    // ──────────────────────────── TAB 2: DIET PLAN ────────────────────────────
    private JPanel createDietTab() {
        JPanel main = new JPanel(new BorderLayout(0, 10));
        main.setBackground(UITheme.BG_APP);
        main.setBorder(new EmptyBorder(14, 16, 14, 16));

        // Selector Bar
        JPanel topBar = UITheme.createCardPanel();
        topBar.setLayout(new FlowLayout(FlowLayout.LEFT, 12, 6));

        JLabel selLbl = new JLabel("Select Member to Prescribe/Edit Diet:");
        selLbl.setFont(UITheme.FONT_SUBHEADER);
        selLbl.setForeground(UITheme.PRIMARY);

        dietMemberCombo = new JComboBox<>();
        dietMemberCombo.setFont(UITheme.FONT_BODY);
        dietMemberCombo.setPreferredSize(new Dimension(280, 32));
        dietMemberCombo.addActionListener(e -> loadDietForSelectedMember());

        JButton loadBtn = UITheme.createButton("Load Diet 📥", UITheme.PRIMARY_LIGHT, UITheme.PRIMARY);
        loadBtn.addActionListener(e -> loadDietForSelectedMember());

        JButton saveDietBtn = UITheme.createButton("Save & Assign Diet Plan 💾", UITheme.SUCCESS_DARK, Color.WHITE);
        saveDietBtn.addActionListener(e -> saveCurrentDietPlan());

        topBar.add(selLbl);
        topBar.add(dietMemberCombo);
        topBar.add(loadBtn);
        topBar.add(saveDietBtn);

        main.add(topBar, BorderLayout.NORTH);

        // Diet Editor Panel
        JPanel form = UITheme.createCardPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));

        // Macros Row
        JPanel macroRow = new JPanel(new GridLayout(1, 5, 10, 0));
        macroRow.setBackground(UITheme.BG_CARD);

        dietCaloriesField = UITheme.createTextField(6);
        dietProteinField = UITheme.createTextField(6);
        dietCarbsField = UITheme.createTextField(6);
        dietFatsField = UITheme.createTextField(6);
        dietWaterField = UITheme.createTextField(6);

        macroRow.add(createMiniCard("Target Calories (kcal)", dietCaloriesField));
        macroRow.add(createMiniCard("Protein (grams)", dietProteinField));
        macroRow.add(createMiniCard("Carbs (grams)", dietCarbsField));
        macroRow.add(createMiniCard("Fats (grams)", dietFatsField));
        macroRow.add(createMiniCard("Daily Water (Liters)", dietWaterField));
        macroRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 65));

        form.add(macroRow);
        form.add(Box.createVerticalStrut(12));

        // Meals Grid
        JPanel mealsGrid = new JPanel(new GridLayout(2, 2, 10, 10));
        mealsGrid.setBackground(UITheme.BG_CARD);

        dietBreakfastArea = UITheme.createTextArea(3, 20);
        dietLunchArea = UITheme.createTextArea(3, 20);
        dietSnacksArea = UITheme.createTextArea(3, 20);
        dietDinnerArea = UITheme.createTextArea(3, 20);

        mealsGrid.add(createMealCard("🍳 BREAKFAST", dietBreakfastArea));
        mealsGrid.add(createMealCard("🥗 LUNCH", dietLunchArea));
        mealsGrid.add(createMealCard("🍎 EVENING SNACKS", dietSnacksArea));
        mealsGrid.add(createMealCard("🍲 DINNER", dietDinnerArea));

        form.add(mealsGrid);
        form.add(Box.createVerticalStrut(10));

        // Notes Area
        dietNotesArea = UITheme.createTextArea(2, 40);
        form.add(createMealCard("📝 TRAINER'S NUTRITION GUIDELINES & REMARKS", dietNotesArea));

        JScrollPane scrollForm = new JScrollPane(form);
        scrollForm.setBorder(null);
        main.add(scrollForm, BorderLayout.CENTER);

        return main;
    }

    // ──────────────────────────── TAB 3: WORKOUT PLAN ────────────────────────────
    private JPanel createWorkoutTab() {
        JPanel main = new JPanel(new BorderLayout(0, 10));
        main.setBackground(UITheme.BG_APP);
        main.setBorder(new EmptyBorder(14, 16, 14, 16));

        // Selector Bar
        JPanel topBar = UITheme.createCardPanel();
        topBar.setLayout(new FlowLayout(FlowLayout.LEFT, 12, 6));

        JLabel selLbl = new JLabel("Select Member for Workout Routine:");
        selLbl.setFont(UITheme.FONT_SUBHEADER);
        selLbl.setForeground(UITheme.PRIMARY);

        workoutMemberCombo = new JComboBox<>();
        workoutMemberCombo.setFont(UITheme.FONT_BODY);
        workoutMemberCombo.setPreferredSize(new Dimension(280, 32));
        workoutMemberCombo.addActionListener(e -> loadWorkoutForSelectedMember());

        JButton loadBtn = UITheme.createButton("Load Routine 📥", UITheme.PRIMARY_LIGHT, UITheme.PRIMARY);
        loadBtn.addActionListener(e -> loadWorkoutForSelectedMember());

        JButton saveWorkoutBtn = UITheme.createButton("Save & Assign Workout Routine 💾", UITheme.SUCCESS_DARK, Color.WHITE);
        saveWorkoutBtn.addActionListener(e -> saveCurrentWorkoutPlan());

        topBar.add(selLbl);
        topBar.add(workoutMemberCombo);
        topBar.add(loadBtn);
        topBar.add(saveWorkoutBtn);

        main.add(topBar, BorderLayout.NORTH);

        // Workout Editor Panel
        JPanel form = UITheme.createCardPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));

        // Top specs row
        JPanel specRow = new JPanel(new GridLayout(1, 2, 12, 0));
        specRow.setBackground(UITheme.BG_CARD);

        workoutGoalField = UITheme.createTextField(15);
        workoutDaysSpinner = new JSpinner(new SpinnerNumberModel(5, 0, 7, 1));
        workoutDaysSpinner.setFont(UITheme.FONT_BODY);

        specRow.add(createMiniCard("Training Goal", workoutGoalField));
        specRow.add(createMiniCard("Weekly Training Days", workoutDaysSpinner));
        specRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 65));
        form.add(specRow);
        form.add(Box.createVerticalStrut(10));

        // Days Grid
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

        daysGrid.add(createMealCard("MONDAY", workoutMonArea));
        daysGrid.add(createMealCard("TUESDAY", workoutTueArea));
        daysGrid.add(createMealCard("WEDNESDAY", workoutWedArea));
        daysGrid.add(createMealCard("THURSDAY", workoutThuArea));
        daysGrid.add(createMealCard("FRIDAY", workoutFriArea));
        daysGrid.add(createMealCard("SATURDAY", workoutSatArea));
        daysGrid.add(createMealCard("SUNDAY", workoutSunArea));
        daysGrid.add(createMealCard("CARDIO & MOBILITY", workoutCardioArea));

        form.add(daysGrid);
        form.add(Box.createVerticalStrut(10));

        workoutNotesArea = UITheme.createTextArea(2, 40);
        form.add(createMealCard("COACH'S INSTRUCTIONS & PROGRESSIVE OVERLOAD RULES", workoutNotesArea));

        JScrollPane scrollForm = new JScrollPane(form);
        scrollForm.setBorder(null);
        main.add(scrollForm, BorderLayout.CENTER);

        return main;
    }

    // ──────────────────────────── TAB 4: MEMBER ROSTER ────────────────────────────
    private JPanel createMembersTab() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBackground(UITheme.BG_APP);
        panel.setBorder(new EmptyBorder(14, 16, 14, 16));

        JPanel topBar = UITheme.createCardPanel();
        topBar.setLayout(new FlowLayout(FlowLayout.LEFT, 12, 6));

        JLabel title = new JLabel("Member Directory & Weight Management");
        title.setFont(UITheme.FONT_HEADER);
        title.setForeground(UITheme.PRIMARY);

        JButton logWeightBtn = UITheme.createButton("Log Member Scale Weight ⚖️", UITheme.WARNING, Color.WHITE);
        JButton addStaffBtn = UITheme.createButton("Add New Staff 👔➕", UITheme.SUCCESS_DARK, Color.WHITE);
        JButton refreshBtn = UITheme.createButton("Refresh Roster 🔄", UITheme.PRIMARY_LIGHT, UITheme.PRIMARY);

        logWeightBtn.addActionListener(e -> openWeightUpdateDialog());
        addStaffBtn.addActionListener(e -> openAddStaffDialog());
        refreshBtn.addActionListener(e -> refreshMemberRoster());

        topBar.add(title);
        topBar.add(Box.createHorizontalStrut(20));
        topBar.add(logWeightBtn);
        topBar.add(addStaffBtn);
        topBar.add(refreshBtn);

        panel.add(topBar, BorderLayout.NORTH);

        String[] cols = {"ID", "Full Name", "Username", "Plan & Tier", "Assigned Coach", "Current Weight", "Target Weight", "Goal Progress", "BMI"};
        memberRosterModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        memberRosterTable = new JTable(memberRosterModel);
        UITheme.styleTable(memberRosterTable);

        JScrollPane scroll = new JScrollPane(memberRosterTable);
        scroll.getViewport().setBackground(UITheme.BG_CARD);
        panel.add(scroll, BorderLayout.CENTER);

        return panel;
    }

    // ──────────────────────────── TAB 5: STAFF & TRAINERS DIRECTORY ────────────────────────────
    private JPanel createStaffDirectoryTab() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBackground(UITheme.BG_APP);
        panel.setBorder(new EmptyBorder(14, 16, 14, 16));

        JPanel topBar = UITheme.createCardPanel();
        topBar.setLayout(new FlowLayout(FlowLayout.LEFT, 12, 6));

        JLabel title = new JLabel("Gym Coaches & Staff Directory");
        title.setFont(UITheme.FONT_HEADER);
        title.setForeground(UITheme.PRIMARY);

        JButton addStaffBtn = UITheme.createButton("Add New Staff / Coach 👔➕", UITheme.SUCCESS_DARK, Color.WHITE);
        JButton refreshBtn = UITheme.createButton("Refresh Staff List 🔄", UITheme.PRIMARY_LIGHT, UITheme.PRIMARY);

        addStaffBtn.addActionListener(e -> openAddStaffDialog());
        refreshBtn.addActionListener(e -> refreshStaffDirectory());

        topBar.add(title);
        topBar.add(Box.createHorizontalStrut(20));
        topBar.add(addStaffBtn);
        topBar.add(refreshBtn);

        panel.add(topBar, BorderLayout.NORTH);

        String[] cols = {"ID", "Full Name", "Username", "Specialization", "Shift Timing", "Salary (INR)", "Phone", "Email"};
        staffDirectoryModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        staffDirectoryTable = new JTable(staffDirectoryModel);
        UITheme.styleTable(staffDirectoryTable);

        JScrollPane scroll = new JScrollPane(staffDirectoryTable);
        scroll.getViewport().setBackground(UITheme.BG_CARD);
        panel.add(scroll, BorderLayout.CENTER);

        return panel;
    }

    // ──────────────────────────── HELPERS & DATA LOADERS ────────────────────────────
    private void openWeightUpdateDialog() {
        int row = memberRosterTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a member from the table to update their weight.", "Notice", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int memberId = (int) memberRosterModel.getValueAt(row, 0);
        String name = (String) memberRosterModel.getValueAt(row, 1);

        String input = JOptionPane.showInputDialog(this, "Enter latest measured weight (in kg) for " + name + ":", "Log Member Weight", JOptionPane.QUESTION_MESSAGE);
        if (input != null && !input.trim().isEmpty()) {
            try {
                double newWeight = Double.parseDouble(input.trim());
                User user = userService.searchUsers(memberId);
                if (user instanceof MemberUser m) {
                    fitnessService.updateMemberBodyMetrics(memberId, newWeight, m.getTargetWeight(), m.getHeightCm());
                    refreshMemberRoster();
                    JOptionPane.showMessageDialog(this, "Updated weight for " + name + " to " + newWeight + " kg!");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Failed to update weight: " + ex.getMessage());
            }
        }
    }

    private void loadDietForSelectedMember() {
        MemberComboItem item = (MemberComboItem) dietMemberCombo.getSelectedItem();
        if (item == null) return;
        try {
            DietPlan plan = fitnessService.getDietPlan(item.memberId);
            if (!plan.isAssigned()) {
                // Member has no diet assigned yet -> display blank fields ready for trainer
                dietCaloriesField.setText("");
                dietProteinField.setText("");
                dietCarbsField.setText("");
                dietFatsField.setText("");
                dietWaterField.setText("");
                dietBreakfastArea.setText("");
                dietLunchArea.setText("");
                dietSnacksArea.setText("");
                dietDinnerArea.setText("");
                dietNotesArea.setText("Assign target nutrition, calories, and meals for " + item.fullName + ".");
            } else {
                dietCaloriesField.setText(String.valueOf(plan.getTargetCalories()));
                dietProteinField.setText(String.valueOf(plan.getProteinGrams()));
                dietCarbsField.setText(String.valueOf(plan.getCarbsGrams()));
                dietFatsField.setText(String.valueOf(plan.getFatsGrams()));
                dietWaterField.setText(String.valueOf(plan.getWaterIntakeLiters()));

                dietBreakfastArea.setText(plan.getBreakfast());
                dietLunchArea.setText(plan.getLunch());
                dietSnacksArea.setText(plan.getSnacks());
                dietDinnerArea.setText(plan.getDinner());
                dietNotesArea.setText(plan.getGeneralNotes());
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error loading diet: " + ex.getMessage());
        }
    }

    private void saveCurrentDietPlan() {
        MemberComboItem item = (MemberComboItem) dietMemberCombo.getSelectedItem();
        if (item == null) {
            JOptionPane.showMessageDialog(this, "Please select a member.");
            return;
        }
        try {
            int cal = 0;
            if (!dietCaloriesField.getText().trim().isEmpty()) {
                cal = Integer.parseInt(dietCaloriesField.getText().trim());
            }
            int pro = dietProteinField.getText().trim().isEmpty() ? 0 : Integer.parseInt(dietProteinField.getText().trim());
            int carb = dietCarbsField.getText().trim().isEmpty() ? 0 : Integer.parseInt(dietCarbsField.getText().trim());
            int fat = dietFatsField.getText().trim().isEmpty() ? 0 : Integer.parseInt(dietFatsField.getText().trim());
            double water = dietWaterField.getText().trim().isEmpty() ? 0.0 : Double.parseDouble(dietWaterField.getText().trim());

            String coachName = currentTrainer != null ? currentTrainer.getFullName() : "Head Trainer";
            DietPlan plan = new DietPlan(
                    item.memberId,
                    item.fullName,
                    coachName,
                    cal, pro, carb, fat,
                    dietBreakfastArea.getText().trim(),
                    dietLunchArea.getText().trim(),
                    dietSnacksArea.getText().trim(),
                    dietDinnerArea.getText().trim(),
                    water,
                    dietNotesArea.getText().trim()
            );

            fitnessService.saveDietPlan(plan);
            JOptionPane.showMessageDialog(this, "Diet Plan saved & assigned to " + item.fullName + "!", "Saved", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error saving diet: " + ex.getMessage());
        }
    }

    private void loadWorkoutForSelectedMember() {
        MemberComboItem item = (MemberComboItem) workoutMemberCombo.getSelectedItem();
        if (item == null) return;
        try {
            WorkoutPlan plan = fitnessService.getWorkoutPlan(item.memberId);
            if (!plan.isAssigned()) {
                // Member has no workout assigned yet -> display blank fields ready for trainer
                workoutGoalField.setText("General Fitness");
                workoutDaysSpinner.setValue(0);
                workoutMonArea.setText("");
                workoutTueArea.setText("");
                workoutWedArea.setText("");
                workoutThuArea.setText("");
                workoutFriArea.setText("");
                workoutSatArea.setText("");
                workoutSunArea.setText("");
                workoutCardioArea.setText("");
                workoutNotesArea.setText("Assign split routine and exercises for " + item.fullName + ".");
            } else {
                workoutGoalField.setText(plan.getTargetGoal());
                workoutDaysSpinner.setValue(plan.getDaysPerWeek());

                workoutMonArea.setText(plan.getMondayWorkout());
                workoutTueArea.setText(plan.getTuesdayWorkout());
                workoutWedArea.setText(plan.getWednesdayWorkout());
                workoutThuArea.setText(plan.getThursdayWorkout());
                workoutFriArea.setText(plan.getFridayWorkout());
                workoutSatArea.setText(plan.getSaturdayWorkout());
                workoutSunArea.setText(plan.getSundayWorkout());
                workoutCardioArea.setText(plan.getCardioGuidelines());
                workoutNotesArea.setText(plan.getGeneralNotes());
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error loading workout: " + ex.getMessage());
        }
    }

    private void saveCurrentWorkoutPlan() {
        MemberComboItem item = (MemberComboItem) workoutMemberCombo.getSelectedItem();
        if (item == null) {
            JOptionPane.showMessageDialog(this, "Please select a member.");
            return;
        }
        try {
            String goal = workoutGoalField.getText().trim();
            int days = (Integer) workoutDaysSpinner.getValue();
            String coachName = currentTrainer != null ? currentTrainer.getFullName() : "Head Trainer";

            WorkoutPlan plan = new WorkoutPlan(
                    item.memberId,
                    item.fullName,
                    coachName,
                    goal,
                    days,
                    workoutMonArea.getText().trim(),
                    workoutTueArea.getText().trim(),
                    workoutWedArea.getText().trim(),
                    workoutThuArea.getText().trim(),
                    workoutFriArea.getText().trim(),
                    workoutSatArea.getText().trim(),
                    workoutSunArea.getText().trim(),
                    workoutCardioArea.getText().trim(),
                    workoutNotesArea.getText().trim()
            );

            fitnessService.saveWorkoutPlan(plan);
            JOptionPane.showMessageDialog(this, "Workout Routine saved & assigned to " + item.fullName + "!", "Saved", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error saving workout: " + ex.getMessage());
        }
    }

    public void refreshAllData() {
        populateMemberCombos();
        refreshAttendanceTable();
        refreshMemberRoster();
        refreshStaffDirectory();
    }

    private void populateMemberCombos() {
        try {
            List<User> members = userService.getUsersByRole("MEMBER");
            attMemberCombo.removeAllItems();
            dietMemberCombo.removeAllItems();
            workoutMemberCombo.removeAllItems();

            for (User u : members) {
                MemberComboItem item = new MemberComboItem(u.getUserId(), u.getFullName());
                attMemberCombo.addItem(item);
                dietMemberCombo.addItem(item);
                workoutMemberCombo.addItem(item);
            }

            if (dietMemberCombo.getItemCount() > 0) {
                dietMemberCombo.setSelectedIndex(0);
                workoutMemberCombo.setSelectedIndex(0);
                loadDietForSelectedMember();
                loadWorkoutForSelectedMember();
            }
        } catch (Exception ignored) {
        }
    }

    private void refreshAttendanceTable() {
        try {
            List<Attendance> list = attendanceService.getAllAttendance();
            attendanceTableModel.setRowCount(0);
            for (Attendance a : list) {
                attendanceTableModel.addRow(new Object[]{
                        a.getAttendanceId(),
                        a.getMemberName(),
                        a.getDate(),
                        a.getTimeIn(),
                        a.getStatus(),
                        a.getWorkoutType(),
                        a.getNotes()
                });
            }
        } catch (Exception ignored) {
        }
    }

    private void refreshMemberRoster() {
        try {
            List<User> list = userService.getUsersByRole("MEMBER");
            memberRosterModel.setRowCount(0);
            for (User u : list) {
                if (u instanceof MemberUser m) {
                    memberRosterModel.addRow(m.toTableRow());
                }
            }
        } catch (Exception ignored) {
        }
    }

    private void refreshStaffDirectory() {
        try {
            List<User> list = userService.getUsersByRole("TRAINER");
            if (staffDirectoryModel != null) {
                staffDirectoryModel.setRowCount(0);
                for (User u : list) {
                    if (u instanceof TrainerUser t) {
                        staffDirectoryModel.addRow(t.toTableRow());
                    }
                }
            }
        } catch (Exception ignored) {
        }
    }

    /**
     * Modal dialog allowing staff/trainers to register a new coach or staff member.
     * Demonstrates Form input collection, Exception validation, and dynamic table reload.
     */
    private void openAddStaffDialog() {
        Frame parent = (Frame) SwingUtilities.getWindowAncestor(this);
        JDialog dialog = new JDialog(parent, "Register New Staff / Coach", true);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.getContentPane().setBackground(UITheme.BG_APP);

        JPanel form = UITheme.createCardPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBorder(new EmptyBorder(16, 18, 16, 18));

        JLabel header = new JLabel("Register New Staff / Coach");
        header.setFont(UITheme.FONT_HEADER);
        header.setForeground(UITheme.PRIMARY);
        form.add(header);
        form.add(Box.createVerticalStrut(12));

        JTextField nameField = UITheme.createTextField(15);
        JTextField userField = UITheme.createTextField(15);
        JPasswordField passField = UITheme.createPasswordField(15);
        JTextField phoneField = UITheme.createTextField(15);
        JTextField emailField = UITheme.createTextField(15);
        JComboBox<String> specCombo = UITheme.createComboBox(TrainerUser.SPECIALIZATIONS);
        JComboBox<String> shiftCombo = UITheme.createComboBox(TrainerUser.SHIFTS);
        JTextField salaryField = UITheme.createTextField(10);
        salaryField.setText("40000.0");

        form.add(createFormField("Full Name *:", nameField));
        form.add(Box.createVerticalStrut(6));
        form.add(createFormField("Username * (min 3 chars):", userField));
        form.add(Box.createVerticalStrut(6));
        form.add(createFormField("Password * (min 4 chars):", passField));
        form.add(Box.createVerticalStrut(6));
        form.add(createFormField("Phone Number (10 digits):", phoneField));
        form.add(Box.createVerticalStrut(6));
        form.add(createFormField("Email Address:", emailField));
        form.add(Box.createVerticalStrut(6));
        form.add(createFormField("Coach Specialization:", specCombo));
        form.add(Box.createVerticalStrut(6));
        form.add(createFormField("Shift Timing:", shiftCombo));
        form.add(Box.createVerticalStrut(6));
        form.add(createFormField("Monthly Salary (INR):", salaryField));

        JPanel buttonBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonBar.setBackground(UITheme.BG_APP);

        JButton cancelBtn = UITheme.createButton("Cancel", UITheme.TEXT_MUTED, Color.WHITE);
        JButton saveBtn = UITheme.createButton("Register Staff Member 👔", UITheme.SUCCESS_DARK, Color.WHITE);

        cancelBtn.addActionListener(e -> dialog.dispose());
        saveBtn.addActionListener(e -> {
            try {
                String fullName = nameField.getText().trim();
                String username = userField.getText().trim();
                String password = new String(passField.getPassword()).trim();
                String phone = phoneField.getText().trim();
                String email = emailField.getText().trim();
                String spec = (String) specCombo.getSelectedItem();
                String shift = (String) shiftCombo.getSelectedItem();
                double salary = 40000.0;
                try {
                    salary = Double.parseDouble(salaryField.getText().trim());
                } catch (NumberFormatException nfe) {
                    throw new GymException("Please enter a valid numeric salary amount.");
                }

                if (fullName.isEmpty() || username.isEmpty() || password.isEmpty()) {
                    throw new GymException("Full Name, Username, and Password are required fields.");
                }

                userService.registerTrainer(username, password, fullName, phone, email, spec, shift, salary);
                JOptionPane.showMessageDialog(dialog, "Staff Coach " + fullName + " registered successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
                refreshStaffDirectory();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, ex.getMessage(), "Registration Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        buttonBar.add(cancelBtn);
        buttonBar.add(saveBtn);

        dialog.add(new JScrollPane(form), BorderLayout.CENTER);
        dialog.add(buttonBar, BorderLayout.SOUTH);
        dialog.setSize(440, 640);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private JPanel createFormField(String label, JComponent comp) {
        JPanel p = new JPanel(new BorderLayout(6, 4));
        p.setBackground(UITheme.BG_CARD);
        JLabel l = new JLabel(label);
        l.setFont(UITheme.FONT_SMALL);
        l.setForeground(UITheme.TEXT_DARK);
        p.add(l, BorderLayout.NORTH);
        p.add(comp, BorderLayout.CENTER);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        return p;
    }

    private JPanel createMiniCard(String title, JComponent comp) {
        JPanel p = new JPanel(new BorderLayout(4, 4));
        p.setBackground(UITheme.BG_CARD);
        JLabel l = new JLabel(title);
        l.setFont(UITheme.FONT_SMALL);
        l.setForeground(UITheme.TEXT_MUTED);
        p.add(l, BorderLayout.NORTH);
        p.add(comp, BorderLayout.CENTER);
        return p;
    }

    private JPanel createMealCard(String title, JTextArea area) {
        JPanel p = new JPanel(new BorderLayout(4, 4));
        p.setBackground(UITheme.BG_CARD);
        JLabel l = new JLabel(title);
        l.setFont(UITheme.FONT_SUBHEADER);
        l.setForeground(UITheme.PRIMARY);
        p.add(l, BorderLayout.NORTH);
        p.add(new JScrollPane(area), BorderLayout.CENTER);
        return p;
    }

    // Helper item class for JComboBox
    private static class MemberComboItem {
        final int memberId;
        final String fullName;

        MemberComboItem(int id, String name) {
            this.memberId = id;
            this.fullName = name;
        }

        @Override
        public String toString() {
            return String.format("%s (ID: %d)", fullName, memberId);
        }
    }
}
