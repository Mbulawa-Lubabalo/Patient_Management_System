package org.example.database;


import org.junit.jupiter.api.Test;
import java.io.File;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DatabaseSetupTest {

    @Test
    void testDatabaseCreation() {
        DatabaseSetup.createNewDatabase();
        File dbFile = new File("oncology_pms.db");
        assertTrue(dbFile.exists(), "Database file should exist after setup");
    }
}

