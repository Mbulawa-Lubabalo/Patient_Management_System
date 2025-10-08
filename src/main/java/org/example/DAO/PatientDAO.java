package org.example.DAO;

import org.example.model.Patient;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for managing Patient records in the database.
 * This class handles all database interactions related to the Patient model.
 */
public class PatientDAO {

    private static final String DEFAULT_URL = "jdbc:sqlite:oncology_pms.db";
    private final String dbUrl;

    /**
     * Default constructor, uses the standard database file path.
     */
    public PatientDAO() {
        this(DEFAULT_URL);
    }

    /**
     * Constructor allowing a custom database URL (useful for testing or specific environments).
     * @param dbUrl The JDBC connection string for the SQLite database.
     */
    public PatientDAO(String dbUrl) {
        this.dbUrl = dbUrl;
    }

    /**
     * Retrieves all patient records from the database.
     * Patients are ordered by ID in descending order (newest first).
     * @return A list of all {@link Patient} objects found in the database.
     */
    public List<Patient> getAllPatients() {
        List<Patient> patients = new ArrayList<>();
        String sql = "SELECT id, name, diagnosis, treatmentPlan FROM patient ORDER BY id DESC";

        try (Connection conn = DriverManager.getConnection(this.dbUrl);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Patient patient = new Patient(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("diagnosis"),
                        rs.getString("treatmentPlan")
                );
                patients.add(patient);
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving all patients: " + e.getMessage());
        }
        return patients;
    }

    /**
     * Inserts a new patient record into the database.
     * It uses {@code Statement.RETURN_GENERATED_KEYS} to retrieve the new primary key.
     * @param patient The {@link Patient} object to insert (ID field is ignored).
     * @return The auto-generated ID of the new patient, or -1 on failure.
     */
    public int addPatient(Patient patient) {
        String sql = "INSERT INTO patient(name, diagnosis, treatmentPlan) VALUES(?, ?, ?)";
        int generatedId = -1;

        try (Connection conn = DriverManager.getConnection(this.dbUrl);
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, patient.getName());
            pstmt.setString(2, patient.getDiagnosis());
            pstmt.setString(3, patient.getTreatmentPlan());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        generatedId = rs.getInt(1);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error adding patient: " + e.getMessage());
        }
        return generatedId;
    }
}
