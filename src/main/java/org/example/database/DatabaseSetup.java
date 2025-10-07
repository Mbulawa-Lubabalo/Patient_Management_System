package org.example.database;

import java.sql.*;

public class DatabaseSetup {
    // The URL for the physical SQLite database file
    private static final String URL = "jdbc:sqlite:oncology_pms.db";

    public static void createNewDatabase() {
        try {
            // Ensure the SQLite driver is loaded
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            System.err.println("Error loading SQLite JDBC driver: " + e.getMessage());
            return;
        }

        try (Connection conn = DriverManager.getConnection(URL)) {
            if (conn != null) {
                DatabaseMetaData meta = conn.getMetaData();
                System.out.println("SQLite Driver: " + meta.getDriverName());
                System.out.println("Database file checked/created at: " + URL.substring(12));

                // 1. Create Patient Table
                createPatientTable(conn);

                // 2. Create Progress Table
                createProgressTable(conn);

                // 3. Insert Sample Data
                insertSampleProgressData(conn);
            }
        } catch (SQLException e) {
            System.err.println("Database setup error: " + e.getMessage());
        }
    }

    private static void createPatientTable(Connection conn) {
        String sql = "CREATE TABLE IF NOT EXISTS patient (\n"
                + " id INTEGER PRIMARY KEY AUTOINCREMENT,\n"
                + " name TEXT NOT NULL,\n"
                + " diagnosis TEXT NOT NULL,\n"
                + " treatmentPlan TEXT\n"
                + ");";

        try (Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("Patient table status: OK.");
        } catch (SQLException e) {
            System.err.println("Error creating Patient table: " + e.getMessage());
        }
    }

    private static void createProgressTable(Connection conn) {
        String sql = "CREATE TABLE IF NOT EXISTS patient_progress (\n"
                + " progress_id INTEGER PRIMARY KEY AUTOINCREMENT,\n"
                + " patient_id INTEGER NOT NULL,\n"
                + " cycle_number INTEGER NOT NULL,\n"
                + " measurement_date TEXT NOT NULL,\n"
                + " tumor_marker_level REAL NOT NULL,\n"
                + " status TEXT,\n"
                + " treatment_dose TEXT,\n"
                + " FOREIGN KEY (patient_id) REFERENCES patient(id)\n"
                + ");";

        try (Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("Patient Progress table status: OK.");
        } catch (SQLException e) {
            System.err.println("Error creating Progress table: " + e.getMessage());
        }
    }

    private static void insertSampleProgressData(Connection conn) {
        // SQL to insert Alice Johnson if she doesn't exist
        String checkPatientSql = "SELECT id FROM patient WHERE name = 'Alice Johnson'";
        int aliceId = -1;

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(checkPatientSql)) {

            if (rs.next()) {
                aliceId = rs.getInt("id");
            } else {
                // If Alice Johnson doesn't exist, insert her first
                String insertPatientSql = "INSERT INTO patient (name, diagnosis, treatmentPlan) VALUES ('Alice Johnson', 'Ovarian Cancer', 'Chemo Protocol X')";
                stmt.executeUpdate(insertPatientSql);

                // Retrieve the newly generated ID
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        aliceId = generatedKeys.getInt(1);
                    }
                }
                System.out.println("Inserted sample patient: Alice Johnson (ID: " + aliceId + ")");
            }

            if (aliceId != -1) {
                // Check if progress data already exists for Alice
                String checkProgressSql = "SELECT COUNT(*) FROM patient_progress WHERE patient_id = " + aliceId;
                try (ResultSet countRs = stmt.executeQuery(checkProgressSql)) {
                    if (countRs.next() && countRs.getInt(1) > 0) {
                        System.out.println("Sample progress data already exists. Skipping insert.");
                        return;
                    }
                }

                // Insert the progress data
                String insertProgressSql = "INSERT INTO patient_progress (patient_id, cycle_number, measurement_date, tumor_marker_level, status, treatment_dose) VALUES (?, ?, ?, ?, ?, ?)";
                try (PreparedStatement pstmt = conn.prepareStatement(insertProgressSql)) {
                    conn.setAutoCommit(false); // Start transaction for bulk insert

                    Object[][] data = {
                            {aliceId, 0, "2024-01-15", 420.0, "Pre-Treatment", "N/A"},
                            {aliceId, 1, "2024-02-05", 385.0, "Response Check", "Full"},
                            {aliceId, 2, "2024-02-26", 310.0, "Response Check", "Full"},
                            {aliceId, 3, "2024-03-18", 255.0, "Partial Response", "Full"},
                            {aliceId, 4, "2024-04-08", 198.0, "Partial Response", "Full"},
                            {aliceId, 5, "2024-04-29", 150.0, "Partial Response", "Reduced (Toxicity)"},
                            {aliceId, 6, "2024-05-20", 145.0, "Stable Disease", "Full"},
                            {aliceId, 7, "2024-06-10", 160.0, "Stable Disease", "Full"},
                            {aliceId, 8, "2024-07-01", 205.0, "Progression", "Full"}
                    };

                    for (Object[] row : data) {
                        pstmt.setInt(1, (int) row[0]);
                        pstmt.setInt(2, (int) row[1]);
                        pstmt.setString(3, (String) row[2]);
                        pstmt.setDouble(4, (Double) row[3]);
                        pstmt.setString(5, (String) row[4]);
                        pstmt.setString(6, (String) row[5]);
                        pstmt.addBatch();
                    }

                    pstmt.executeBatch();
                    conn.commit(); // Commit the transaction
                    System.out.println("Inserted 9 sample progress records for Alice Johnson.");
                } catch (SQLException e) {
                    try {
                        conn.rollback();
                    } catch (SQLException ex) {
                        System.err.println("Rollback failed: " + ex.getMessage());
                    }
                    System.err.println("Error inserting sample progress data: " + e.getMessage());
                } finally {
                    try {
                        conn.setAutoCommit(true);
                    } catch (SQLException ex) {
                        System.err.println("Failed to reset auto-commit: " + ex.getMessage());
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error setting up sample data: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        createNewDatabase();
    }
}
