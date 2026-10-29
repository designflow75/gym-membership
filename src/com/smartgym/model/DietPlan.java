package com.smartgym.model;

/**
 * Subclass representing a personalized Diet & Nutrition Plan.
 * Demonstrates OOP Concepts:
 * - Inheritance (extends FitnessPlan)
 * - Method Overriding (implements getPlanType() and generateSummary())
 * - Runtime Polymorphism
 */
public class DietPlan extends FitnessPlan {

    private int targetCalories;
    private int proteinGrams;
    private int carbsGrams;
    private int fatsGrams;

    private String breakfast;
    private String lunch;
    private String snacks;
    private String dinner;
    private double waterIntakeLiters;
    private String userDailyLog; // Editable by the member / user!

    // Default Blank Constructor (For newly created member accounts)
    public DietPlan() {
        super();
        this.targetCalories = 0;
        this.proteinGrams = 0;
        this.carbsGrams = 0;
        this.fatsGrams = 0;
        this.breakfast = "";
        this.lunch = "";
        this.snacks = "";
        this.dinner = "";
        this.waterIntakeLiters = 0.0;
        this.generalNotes = "No diet plan assigned yet. Please consult your trainer.";
        this.userDailyLog = "";
    }

    public static DietPlan createBlankPlan(int memberId, String memberName, String assignedTrainer) {
        DietPlan plan = new DietPlan();
        plan.setMemberId(memberId);
        plan.setMemberName(memberName);
        plan.setAssignedTrainer(assignedTrainer);
        return plan;
    }

    public DietPlan(int memberId, String memberName, String assignedTrainer,
                    int targetCalories, int proteinGrams, int carbsGrams, int fatsGrams,
                    String breakfast, String lunch, String snacks, String dinner,
                    double waterIntakeLiters, String generalNotes) {
        super(memberId, memberName, assignedTrainer, generalNotes);
        this.targetCalories = targetCalories;
        this.proteinGrams = proteinGrams;
        this.carbsGrams = carbsGrams;
        this.fatsGrams = fatsGrams;
        this.breakfast = breakfast != null ? breakfast : "";
        this.lunch = lunch != null ? lunch : "";
        this.snacks = snacks != null ? snacks : "";
        this.dinner = dinner != null ? dinner : "";
        this.waterIntakeLiters = waterIntakeLiters;
        this.userDailyLog = "";
    }

    public DietPlan(int planId, int memberId, String memberName, String assignedTrainer, String lastUpdated,
                    int targetCalories, int proteinGrams, int carbsGrams, int fatsGrams,
                    String breakfast, String lunch, String snacks, String dinner,
                    double waterIntakeLiters, String generalNotes, String userDailyLog) {
        super(planId, memberId, memberName, assignedTrainer, lastUpdated, generalNotes);
        this.targetCalories = targetCalories;
        this.proteinGrams = proteinGrams;
        this.carbsGrams = carbsGrams;
        this.fatsGrams = fatsGrams;
        this.breakfast = breakfast != null ? breakfast : "";
        this.lunch = lunch != null ? lunch : "";
        this.snacks = snacks != null ? snacks : "";
        this.dinner = dinner != null ? dinner : "";
        this.waterIntakeLiters = waterIntakeLiters;
        this.userDailyLog = userDailyLog != null ? userDailyLog : "";
    }

    public boolean isAssigned() {
        return targetCalories > 0;
    }

    @Override
    public String getPlanType() {
        return "Targeted Nutrition & Macronutrient Diet Plan";
    }

    @Override
    public String generateSummary() {
        if (!isAssigned()) {
            return "No diet plan prescribed yet.";
        }
        return String.format("Target: %d kcal | Protein: %dg | Carbs: %dg | Fats: %dg | Water: %.1fL",
                targetCalories, proteinGrams, carbsGrams, fatsGrams, waterIntakeLiters);
    }

    public int getTargetCalories() {
        return targetCalories;
    }

    public void setTargetCalories(int targetCalories) {
        this.targetCalories = targetCalories;
    }

    public int getProteinGrams() {
        return proteinGrams;
    }

    public void setProteinGrams(int proteinGrams) {
        this.proteinGrams = proteinGrams;
    }

    public int getCarbsGrams() {
        return carbsGrams;
    }

    public void setCarbsGrams(int carbsGrams) {
        this.carbsGrams = carbsGrams;
    }

    public int getFatsGrams() {
        return fatsGrams;
    }

    public void setFatsGrams(int fatsGrams) {
        this.fatsGrams = fatsGrams;
    }

    public String getBreakfast() {
        return breakfast;
    }

    public void setBreakfast(String breakfast) {
        this.breakfast = breakfast;
    }

    public String getLunch() {
        return lunch;
    }

    public void setLunch(String lunch) {
        this.lunch = lunch;
    }

    public String getSnacks() {
        return snacks;
    }

    public void setSnacks(String snacks) {
        this.snacks = snacks;
    }

    public String getDinner() {
        return dinner;
    }

    public void setDinner(String dinner) {
        this.dinner = dinner;
    }

    public double getWaterIntakeLiters() {
        return waterIntakeLiters;
    }

    public void setWaterIntakeLiters(double waterIntakeLiters) {
        this.waterIntakeLiters = waterIntakeLiters;
    }

    public String getUserDailyLog() {
        return userDailyLog;
    }

    public void setUserDailyLog(String userDailyLog) {
        this.userDailyLog = userDailyLog;
    }
}
