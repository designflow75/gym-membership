package com.smartgym.model;

/**
 * Subclass representing a personalized Gym Workout Routine.
 * Demonstrates OOP Concepts:
 * - Inheritance (extends FitnessPlan)
 * - Method Overriding (implements getPlanType() and generateSummary())
 * - Runtime Polymorphism
 */
public class WorkoutPlan extends FitnessPlan {

    private String targetGoal;
    private int daysPerWeek;
    private String mondayWorkout;
    private String tuesdayWorkout;
    private String wednesdayWorkout;
    private String thursdayWorkout;
    private String fridayWorkout;
    private String saturdayWorkout;
    private String sundayWorkout;
    private String cardioGuidelines;
    private String userWorkoutLog; // Editable by the member / user!

    // Default Blank Constructor (For newly created member accounts)
    public WorkoutPlan() {
        super();
        this.targetGoal = "No workout assigned yet";
        this.daysPerWeek = 0;
        this.mondayWorkout = "";
        this.tuesdayWorkout = "";
        this.wednesdayWorkout = "";
        this.thursdayWorkout = "";
        this.fridayWorkout = "";
        this.saturdayWorkout = "";
        this.sundayWorkout = "";
        this.cardioGuidelines = "";
        this.generalNotes = "No workout routine assigned yet. Your trainer will configure your exercises.";
        this.userWorkoutLog = "";
    }

    public static WorkoutPlan createBlankPlan(int memberId, String memberName, String assignedTrainer) {
        WorkoutPlan plan = new WorkoutPlan();
        plan.setMemberId(memberId);
        plan.setMemberName(memberName);
        plan.setAssignedTrainer(assignedTrainer);
        return plan;
    }

    public WorkoutPlan(int memberId, String memberName, String assignedTrainer, String targetGoal, int daysPerWeek,
                       String mondayWorkout, String tuesdayWorkout, String wednesdayWorkout,
                       String thursdayWorkout, String fridayWorkout, String saturdayWorkout, String sundayWorkout,
                       String cardioGuidelines, String generalNotes) {
        super(memberId, memberName, assignedTrainer, generalNotes);
        this.targetGoal = targetGoal != null ? targetGoal : "";
        this.daysPerWeek = daysPerWeek;
        this.mondayWorkout = mondayWorkout != null ? mondayWorkout : "";
        this.tuesdayWorkout = tuesdayWorkout != null ? tuesdayWorkout : "";
        this.wednesdayWorkout = wednesdayWorkout != null ? wednesdayWorkout : "";
        this.thursdayWorkout = thursdayWorkout != null ? thursdayWorkout : "";
        this.fridayWorkout = fridayWorkout != null ? fridayWorkout : "";
        this.saturdayWorkout = saturdayWorkout != null ? saturdayWorkout : "";
        this.sundayWorkout = sundayWorkout != null ? sundayWorkout : "";
        this.cardioGuidelines = cardioGuidelines != null ? cardioGuidelines : "";
        this.userWorkoutLog = "";
    }

    public WorkoutPlan(int planId, int memberId, String memberName, String assignedTrainer, String lastUpdated,
                       String targetGoal, int daysPerWeek,
                       String mondayWorkout, String tuesdayWorkout, String wednesdayWorkout,
                       String thursdayWorkout, String fridayWorkout, String saturdayWorkout, String sundayWorkout,
                       String cardioGuidelines, String generalNotes, String userWorkoutLog) {
        super(planId, memberId, memberName, assignedTrainer, lastUpdated, generalNotes);
        this.targetGoal = targetGoal != null ? targetGoal : "";
        this.daysPerWeek = daysPerWeek;
        this.mondayWorkout = mondayWorkout != null ? mondayWorkout : "";
        this.tuesdayWorkout = tuesdayWorkout != null ? tuesdayWorkout : "";
        this.wednesdayWorkout = wednesdayWorkout != null ? wednesdayWorkout : "";
        this.thursdayWorkout = thursdayWorkout != null ? thursdayWorkout : "";
        this.fridayWorkout = fridayWorkout != null ? fridayWorkout : "";
        this.saturdayWorkout = saturdayWorkout != null ? saturdayWorkout : "";
        this.sundayWorkout = sundayWorkout != null ? sundayWorkout : "";
        this.cardioGuidelines = cardioGuidelines != null ? cardioGuidelines : "";
        this.userWorkoutLog = userWorkoutLog != null ? userWorkoutLog : "";
    }

    public boolean isAssigned() {
        return daysPerWeek > 0;
    }

    @Override
    public String getPlanType() {
        return "Custom Split Resistance & Cardio Workout Routine";
    }

    @Override
    public String generateSummary() {
        if (!isAssigned()) {
            return "No workout routine assigned yet.";
        }
        return String.format("Goal: %s | %d Days/Week Split | Cardio: %s",
                targetGoal, daysPerWeek, cardioGuidelines);
    }

    public String getTargetGoal() {
        return targetGoal;
    }

    public void setTargetGoal(String targetGoal) {
        this.targetGoal = targetGoal;
    }

    public int getDaysPerWeek() {
        return daysPerWeek;
    }

    public void setDaysPerWeek(int daysPerWeek) {
        this.daysPerWeek = daysPerWeek;
    }

    public String getMondayWorkout() {
        return mondayWorkout;
    }

    public void setMondayWorkout(String mondayWorkout) {
        this.mondayWorkout = mondayWorkout;
    }

    public String getTuesdayWorkout() {
        return tuesdayWorkout;
    }

    public void setTuesdayWorkout(String tuesdayWorkout) {
        this.tuesdayWorkout = tuesdayWorkout;
    }

    public String getWednesdayWorkout() {
        return wednesdayWorkout;
    }

    public void setWednesdayWorkout(String wednesdayWorkout) {
        this.wednesdayWorkout = wednesdayWorkout;
    }

    public String getThursdayWorkout() {
        return thursdayWorkout;
    }

    public void setThursdayWorkout(String thursdayWorkout) {
        this.thursdayWorkout = thursdayWorkout;
    }

    public String getFridayWorkout() {
        return fridayWorkout;
    }

    public void setFridayWorkout(String fridayWorkout) {
        this.fridayWorkout = fridayWorkout;
    }

    public String getSaturdayWorkout() {
        return saturdayWorkout;
    }

    public void setSaturdayWorkout(String saturdayWorkout) {
        this.saturdayWorkout = saturdayWorkout;
    }

    public String getSundayWorkout() {
        return sundayWorkout;
    }

    public void setSundayWorkout(String sundayWorkout) {
        this.sundayWorkout = sundayWorkout;
    }

    public String getCardioGuidelines() {
        return cardioGuidelines;
    }

    public void setCardioGuidelines(String cardioGuidelines) {
        this.cardioGuidelines = cardioGuidelines;
    }

    public String getUserWorkoutLog() {
        return userWorkoutLog;
    }

    public void setUserWorkoutLog(String userWorkoutLog) {
        this.userWorkoutLog = userWorkoutLog;
    }
}
