package com.smartgym.model;

/**
 * Represents an enrolled gym member with tracking metrics (BMI, goal progress) and membership details.
 */
public class MemberUser extends User {

    // Preset options for membership registration
    public static final String[] PLANS = {"Monthly", "Quarterly", "Yearly"};
    public static final String[] TYPES = {"Regular", "Premium"};
    public static final String[] FITNESS_GOALS = {
        "Weight Loss & Fat Burning",
        "Muscle Hypertrophy & Bulk",
        "Lean Body Recomposition",
        "Cardio Endurance & Stamina",
        "General Health & Mobility"
    };

    // Private member-specific attributes (Encapsulation)
    private String membershipPlan; // Monthly, Quarterly, Yearly
    private String membershipType; // Regular, Premium
    private String assignedTrainer;
    private String fitnessGoal;

    // Body metric tracking in kilograms and centimeters
    private double initialWeight; // Starting scale weight (kg)
    private double currentWeight; // Latest recorded scale weight (kg)
    private double targetWeight;  // Target goal weight (kg)
    private double heightCm;      // Height in cm (used for BMI calculation)

    // --- Constructor 1: Default Constructor ---
    public MemberUser() {
        super();
        this.role = "MEMBER";
        this.membershipPlan = PLANS[0];
        this.membershipType = TYPES[0];
        this.assignedTrainer = "Sneha Sen";
        this.fitnessGoal = FITNESS_GOALS[0];
        this.initialWeight = 75.0;
        this.currentWeight = 75.0;
        this.targetWeight = 68.0;
        this.heightCm = 175.0;
    }

    // --- Constructor 2: For New Member Registration (Without ID) ---
    public MemberUser(String username, String password, String fullName, String phoneNumber, String email,
                      String membershipPlan, String membershipType, String assignedTrainer, String fitnessGoal,
                      double initialWeight, double currentWeight, double targetWeight, double heightCm) {
        // Calls superclass constructor to initialize base User fields
        super(username, password, fullName, phoneNumber, email, "MEMBER");
        this.membershipPlan = membershipPlan != null ? membershipPlan : PLANS[0];
        this.membershipType = membershipType != null ? membershipType : TYPES[0];
        this.assignedTrainer = assignedTrainer != null ? assignedTrainer : "Unassigned";
        this.fitnessGoal = fitnessGoal != null ? fitnessGoal : FITNESS_GOALS[0];
        this.initialWeight = initialWeight > 0 ? initialWeight : 70.0;
        this.currentWeight = currentWeight > 0 ? currentWeight : this.initialWeight;
        this.targetWeight = targetWeight > 0 ? targetWeight : 65.0;
        this.heightCm = heightCm > 0 ? heightCm : 170.0;
    }

    // --- Constructor 3: For Database Retrieval (With ID and createdDate) ---
    public MemberUser(int userId, String username, String password, String fullName, String phoneNumber, String email, String createdDate,
                      String membershipPlan, String membershipType, String assignedTrainer, String fitnessGoal,
                      double initialWeight, double currentWeight, double targetWeight, double heightCm) {
        super(userId, username, password, fullName, phoneNumber, email, "MEMBER", createdDate);
        this.membershipPlan = membershipPlan != null ? membershipPlan : PLANS[0];
        this.membershipType = membershipType != null ? membershipType : TYPES[0];
        this.assignedTrainer = assignedTrainer != null ? assignedTrainer : "Unassigned";
        this.fitnessGoal = fitnessGoal != null ? fitnessGoal : FITNESS_GOALS[0];
        this.initialWeight = initialWeight > 0 ? initialWeight : 70.0;
        this.currentWeight = currentWeight > 0 ? currentWeight : this.initialWeight;
        this.targetWeight = targetWeight > 0 ? targetWeight : 65.0;
        this.heightCm = heightCm > 0 ? heightCm : 170.0;
    }

    // Overridden methods from User base class

    @Override
    public String getRoleDescription() {
        return "Active Gym Member tracking personal weight loss progress, diet adherence, and workout performance.";
    }

    @Override
    public String getDashboardTitle() {
        return "Member Personal Fitness & Progress Hub";
    }

    @Override
    public boolean hasAccessTo(String feature) {
        return "MY_PROGRESS".equalsIgnoreCase(feature)
                || "MY_ATTENDANCE".equalsIgnoreCase(feature)
                || "MY_DIET".equalsIgnoreCase(feature)
                || "MY_WORKOUT".equalsIgnoreCase(feature);
    }

    // Body metric & progress calculations

    /**
     * Calculates Body Mass Index (BMI).
     * Formula: weight (kg) / [height (m)]^2
     */
    public double getBMI() {
        if (heightCm <= 0) return 0.0;
        double heightInMeters = heightCm / 100.0;
        double bmi = currentWeight / (heightInMeters * heightInMeters);
        return Math.round(bmi * 10.0) / 10.0;
    }

    /**
     * Categorizes BMI into standard WHO buckets (Underweight, Normal, Overweight, Obese).
     */
    public String getBMICategory() {
        double bmi = getBMI();
        if (bmi < 18.5) return "Underweight (Increase calorie intake)";
        if (bmi < 24.9) return "Normal / Healthy Weight";
        if (bmi < 29.9) return "Overweight (Cardio & calorie deficit recommended)";
        return "Obese (Consult trainer for structured plan)";
    }

    /**
     * Calculates net weight changed since joining.
     */
    public double getNetWeightChange() {
        double change = currentWeight - initialWeight;
        return Math.round(change * 10.0) / 10.0;
    }

    /**
     * Calculates remaining weight in kg to reach the target weight.
     */
    public double getRemainingWeight() {
        double rem = Math.abs(currentWeight - targetWeight);
        return Math.round(rem * 10.0) / 10.0;
    }

    /**
     * Calculates the percentage of the weight goal achieved (0% to 100%).
     */
    public int getProgressPercentage() {
        double totalGoal = Math.abs(initialWeight - targetWeight);
        if (totalGoal <= 0.001) {
            return 100;
        }
        double progress = Math.abs(initialWeight - currentWeight);
        int percentage = (int) Math.round((progress / totalGoal) * 100.0);
        if (percentage < 0) percentage = 0;
        if (percentage > 100) percentage = 100;
        return percentage;
    }

    // Getters and Setters

    public String getMembershipPlan() {
        return membershipPlan;
    }

    public void setMembershipPlan(String membershipPlan) {
        this.membershipPlan = membershipPlan;
    }

    public String getMembershipType() {
        return membershipType;
    }

    public void setMembershipType(String membershipType) {
        this.membershipType = membershipType;
    }

    public String getAssignedTrainer() {
        return assignedTrainer;
    }

    public void setAssignedTrainer(String assignedTrainer) {
        this.assignedTrainer = assignedTrainer;
    }

    public String getFitnessGoal() {
        return fitnessGoal;
    }

    public void setFitnessGoal(String fitnessGoal) {
        this.fitnessGoal = fitnessGoal;
    }

    public double getInitialWeight() {
        return initialWeight;
    }

    public void setInitialWeight(double initialWeight) {
        if (initialWeight > 0) this.initialWeight = initialWeight;
    }

    public double getCurrentWeight() {
        return currentWeight;
    }

    public void setCurrentWeight(double currentWeight) {
        if (currentWeight > 0) this.currentWeight = currentWeight;
    }

    public double getTargetWeight() {
        return targetWeight;
    }

    public void setTargetWeight(double targetWeight) {
        if (targetWeight > 0) this.targetWeight = targetWeight;
    }

    public double getHeightCm() {
        return heightCm;
    }

    public void setHeightCm(double heightCm) {
        if (heightCm > 0) this.heightCm = heightCm;
    }

    /**
     * Converts member details into a table row for the Staff Panel Member Roster.
     */
    public Object[] toTableRow() {
        return new Object[]{
                userId,
                fullName,
                username,
                membershipPlan + " (" + membershipType + ")",
                assignedTrainer,
                String.format("%.1f kg", currentWeight),
                String.format("%.1f kg", targetWeight),
                getProgressPercentage() + "%",
                String.format("%.1f", getBMI())
        };
    }
}
