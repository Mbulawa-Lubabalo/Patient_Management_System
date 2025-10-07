//package org.example.DAO;
//
//import org.junit.jupiter.api.*;
//import java.sql.*;
//import java.util.List;
//import static org.junit.jupiter.api.Assertions.*;
//
//class PatientDAOTest {
//
//    // Using the in-memory database URL
//    private static final String TEST_DB_URL = "jdbc:sqlite::memory:";
//    private PatientDAO dao;
//
//    /**
//     * FIX 1: Load the SQLite driver once before all tests.
//     * This ensures the DriverManager is ready to handle "jdbc:sqlite"
//     * when the database is created in @BeforeEach.
//     */
//    @BeforeAll
//    static void loadDriver() throws ClassNotFoundException {
//        // The fully qualified name of the SQLite JDBC driver class
//        Class.forName("org.sqlite.JDBC");
//    }
//
//    @BeforeEach
//    void setUp() throws SQLException {
//        // 1. Establish connection and create/recreate the table
//        //    (The table creation must happen first)
//        try (Connection conn = DriverManager.getConnection(TEST_DB_URL);
//             Statement stmt = conn.createStatement()) {
//
//            // Drop and re-create the table for a clean test environment
//            stmt.executeUpdate("DROP TABLE IF EXISTS patient");
//            stmt.executeUpdate("CREATE TABLE patient (" +
//                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
//                    "name TEXT NOT NULL," +
//                    "diagnosis TEXT," +
//                    "treatmentPlan TEXT)");
//        }
//
//        // 2. Instantiate DAO AFTER the table is created
//        dao = new PatientDAO(TEST_DB_URL);
//    }
//
//    // --- Test Methods ---
//
//    @Test
//    void testAddAndGetAllPatients() {
//        Patient p1 = new Patient(0, "John Doe", "Flu", "Rest");
//        Patient p2 = new Patient(0, "Jane Smith", "Cold", "Vitamin C");
//
//        dao.addPatient(p1);
//        dao.addPatient(p2);
//
//        List<Patient> patients = dao.getAllPatients();
//
//        // ASSERTION 1: Check if the size is 2
//        assertEquals(2, patients.size(), "Should have retrieved exactly 2 patients.");
//
//        // ASSERTION 2: Check patient data
//        // Note: The order from SQLite queries is generally insertion order, but we should rely on content.
//        // For simplicity, we assume order matches insertion.
//        assertEquals("John Doe", patients.get(0).getName());
//        assertEquals("Jane Smith", patients.get(1).getName());
//    }
//
//    @Test
//    void testUpdatePatient() {
//        Patient p = new Patient(0, "John", "Flu", "Rest");
//        dao.addPatient(p);
//
//        // Retrieve the patient to get the ID assigned by the database (FIXES IndexOutOfBoundsException)
//        List<Patient> initialList = dao.getAllPatients();
//        assertFalse(initialList.isEmpty(), "Patient must be added before update test.");
//        Patient patient = initialList.get(0);
//
//        patient.setDiagnosis("Cold");
//        patient.setTreatmentPlan("New Treatment");
//        dao.updatePatient(patient);
//
//        // Re-read from DB and assert the changes
//        Patient updatedPatient = dao.getAllPatients().get(0);
//        assertEquals("Cold", updatedPatient.getDiagnosis());
//    }
//
//    @Test
//    void testDeletePatient() {
//        Patient p = new Patient(0, "Alice", "Cough", "Medication");
//        dao.addPatient(p);
//
//        // Get the ID of the inserted patient (FIXES IndexOutOfBoundsException)
//        List<Patient> initialList = dao.getAllPatients();
//        assertFalse(initialList.isEmpty(), "Patient must be added before delete test.");
//        int id = initialList.get(0).getId();
//
//        dao.deletePatient(id);
//
//        assertTrue(dao.getAllPatients().isEmpty(), "Patient list should be empty after deletion.");
//    }
//}