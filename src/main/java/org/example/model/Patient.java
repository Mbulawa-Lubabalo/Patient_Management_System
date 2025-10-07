package org.example.model;

/**
 * POJO representing a patient record. Maps to the 'patient' SQLite table.
 */
public class Patient {

    private int id;
    private String name;
    private String diagnosis;
    private String treatmentPlan;

    // Default Constructor
    public Patient() {
    }

    // Parameterized Constructor (for retrieval from DB)
    public Patient(int id, String name, String diagnosis, String treatmentPlan) {
        this.id = id;
        this.name = name;
        this.diagnosis = diagnosis;
        this.treatmentPlan = treatmentPlan;
    }

    // Constructor for insertion (ID will be auto-generated)
    public Patient(String name, String diagnosis, String treatmentPlan) {
        this.name = name;
        this.diagnosis = diagnosis;
        this.treatmentPlan = treatmentPlan;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDiagnosis() {
        return diagnosis;
    }

    public void setDiagnosis(String diagnosis) {
        this.diagnosis = diagnosis;
    }

    public String getTreatmentPlan() {
        return treatmentPlan;
    }

    public void setTreatmentPlan(String treatmentPlan) {
        this.treatmentPlan = treatmentPlan;
    }
}
