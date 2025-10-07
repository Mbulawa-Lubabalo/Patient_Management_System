package org.example.model;

/**
 * Represents a single progress entry for a patient's treatment cycle.
 * This object maps directly to the 'patient_progress' SQLite table.
 */
public class PatientProgress {

    private int progressId;
    private int patientId;
    private int cycleNumber;
    private String measurementDate; // Stored as TEXT (YYYY-MM-DD)
    private double tumorMarkerLevel; // Stored as REAL
    private String status;
    private String treatmentDose;

    // Default Constructor (Required by Jackson/Javalin for JSON deserialization)
    public PatientProgress() {
    }

    // Parameterized Constructor (for insertion/retrieval from DB)
    public PatientProgress(int progressId, int patientId, int cycleNumber, String measurementDate,
                           double tumorMarkerLevel, String status, String treatmentDose) {
        this.progressId = progressId;
        this.patientId = patientId;
        this.cycleNumber = cycleNumber;
        this.measurementDate = measurementDate;
        this.tumorMarkerLevel = tumorMarkerLevel;
        this.status = status;
        this.treatmentDose = treatmentDose;
    }

    // Constructor for insertion (excluding progressId)
    public PatientProgress(int patientId, int cycleNumber, String measurementDate,
                           double tumorMarkerLevel, String status, String treatmentDose) {
        this.patientId = patientId;
        this.cycleNumber = cycleNumber;
        this.measurementDate = measurementDate;
        this.tumorMarkerLevel = tumorMarkerLevel;
        this.status = status;
        this.treatmentDose = treatmentDose;
    }


    // Getters and Setters

    public int getProgressId() {
        return progressId;
    }

    public void setProgressId(int progressId) {
        this.progressId = progressId;
    }

    public int getPatientId() {
        return patientId;
    }

    public void setPatientId(int patientId) {
        this.patientId = patientId;
    }

    public int getCycleNumber() {
        return cycleNumber;
    }

    public void setCycleNumber(int cycleNumber) {
        this.cycleNumber = cycleNumber;
    }

    public String getMeasurementDate() {
        return measurementDate;
    }

    public void setMeasurementDate(String measurementDate) {
        this.measurementDate = measurementDate;
    }

    public double getTumorMarkerLevel() {
        return tumorMarkerLevel;
    }

    public void setTumorMarkerLevel(double tumorMarkerLevel) {
        this.tumorMarkerLevel = tumorMarkerLevel;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTreatmentDose() {
        return treatmentDose;
    }

    public void setTreatmentDose(String treatmentDose) {
        this.treatmentDose = treatmentDose;
    }
}
