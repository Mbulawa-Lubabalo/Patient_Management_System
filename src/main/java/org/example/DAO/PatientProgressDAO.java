package org.example.DAO;

import org.example.model.PatientProgress;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for managing PatientProgress records in the database.
 * This class handles the insertion and retrieval of patient treatment progress data.
 */
public class PatientProgressDAO {

    private static final String DEFAULT_URL = "jdbc:sqlite:oncology_pms.db";
    private final String dbUrl;

    /**
     * Default constructor, uses the standard database file path.
     * Connects to the database specified by {@code DEFAULT_URL}.
     */
    public PatientProgressDAO() {
        this(DEFAULT_URL);
    }

    /**
     * Constructor allowing a custom database URL.
     * This is useful for testing or connecting to a non-default database file.
     * @param dbUrl The JDBC connection string for the SQLite database.
     */
    public PatientProgressDAO(String dbUrl) {
        this.dbUrl = dbUrl;
    }

    // -------------------------------------------------------------------
    // 1. INSERT Method
    // -------------------------------------------------------------------

    /**
     * Inserts a new patient progress entry into the patient_progress table.
     * @param progress The PatientProgress object to insert.
     */
    public void insertProgress(PatientProgress progress) {
        String sql = "INSERT INTO patient_progress(patient_id, cycle_number, measurement_date, tumor_marker_level, status, treatment_dose) VALUES(?, ?, ?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(this.dbUrl);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, progress.getPatientId());
            pstmt.setInt(2, progress.getCycleNumber());
            pstmt.setString(3, progress.getMeasurementDate());
            pstmt.setDouble(4, progress.getTumorMarkerLevel());
            pstmt.setString(5, progress.getStatus());
            pstmt.setString(6, progress.getTreatmentDose());

            pstmt.executeUpdate();
            System.out.println("Progress recorded for Patient ID: " + progress.getPatientId() + ", Cycle: " + progress.getCycleNumber());

        } catch (SQLException e) {
            System.err.println("Error inserting patient progress: " + e.getMessage());
        }
    }

    // -------------------------------------------------------------------
    // 2. RETRIEVE Method
    // -------------------------------------------------------------------

    /**
     * Retrieves all progress entries for a specific patient, ordered by cycle number.
     * This method is used to fetch data necessary for charting.
     * @param patientId The ID of the patient.
     * @return A list of PatientProgress objects.
     */
    public List<PatientProgress> getProgressByPatientId(int patientId) {
        List<PatientProgress> progressList = new ArrayList<>();
        // Query ordered by cycle_number for correct line chart visualization
        String sql = "SELECT * FROM patient_progress WHERE patient_id = ? ORDER BY cycle_number ASC";

        try (Connection conn = DriverManager.getConnection(this.dbUrl);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, patientId);
            try (ResultSet rs = pstmt.executeQuery()) {

                while (rs.next()) {
                    PatientProgress progress = new PatientProgress(
                            rs.getInt("progress_id"),
                            rs.getInt("patient_id"),
                            rs.getInt("cycle_number"),
                            rs.getString("measurement_date"),
                            rs.getDouble("tumor_marker_level"),
                            rs.getString("status"),
                            rs.getString("treatment_dose")
                    );
                    progressList.add(progress);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving patient progress: " + e.getMessage());
        }
        return progressList;
    }
}
