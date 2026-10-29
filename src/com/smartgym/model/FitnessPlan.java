package com.smartgym.model;

import java.time.LocalDate;

/**
 * Abstract base class for customized fitness prescriptions (Diets and Workouts).
 * Demonstrates OOP Concepts:
 * - Abstraction (abstract methods getPlanType() and generateSummary())
 * - Encapsulation (protected fields with public accessors)
 * - Inheritance (base for DietPlan and WorkoutPlan)
 */
public abstract class FitnessPlan {
    protected int planId;
    protected int memberId;
    protected String memberName;
    protected String assignedTrainer;
    protected String lastUpdated;
    protected String generalNotes;

    public FitnessPlan() {
        this.planId = 0;
        this.memberId = 0;
        this.memberName = "";
        this.assignedTrainer = "Staff Coach";
        this.lastUpdated = LocalDate.now().toString();
        this.generalNotes = "";
    }

    public FitnessPlan(int memberId, String memberName, String assignedTrainer, String generalNotes) {
        this.memberId = memberId;
        this.memberName = memberName;
        this.assignedTrainer = assignedTrainer;
        this.lastUpdated = LocalDate.now().toString();
        this.generalNotes = generalNotes;
    }

    public FitnessPlan(int planId, int memberId, String memberName, String assignedTrainer, String lastUpdated, String generalNotes) {
        this.planId = planId;
        this.memberId = memberId;
        this.memberName = memberName;
        this.assignedTrainer = assignedTrainer;
        this.lastUpdated = lastUpdated;
        this.generalNotes = generalNotes;
    }

    // Abstract methods for polymorphic handling
    public abstract String getPlanType();
    public abstract String generateSummary();

    public int getPlanId() {
        return planId;
    }

    public void setPlanId(int planId) {
        this.planId = planId;
    }

    public int getMemberId() {
        return memberId;
    }

    public void setMemberId(int memberId) {
        this.memberId = memberId;
    }

    public String getMemberName() {
        return memberName;
    }

    public void setMemberName(String memberName) {
        this.memberName = memberName;
    }

    public String getAssignedTrainer() {
        return assignedTrainer;
    }

    public void setAssignedTrainer(String assignedTrainer) {
        this.assignedTrainer = assignedTrainer;
    }

    public String getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(String lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public String getGeneralNotes() {
        return generalNotes;
    }

    public void setGeneralNotes(String generalNotes) {
        this.generalNotes = generalNotes;
    }
}

