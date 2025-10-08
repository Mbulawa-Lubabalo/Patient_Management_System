package org.example.mainApplication;


import io.javalin.Javalin;
import io.javalin.http.staticfiles.Location;
import org.example.controller.ApiController;
import org.example.database.DatabaseSetup;

/**
 * Main application class to initialize the database and start the Javalin server.
 * This class serves as the entry point for the Oncology Patient Management System backend.
 *
 * <p>Required Dependencies in pom.xml (or build.gradle):</p>
 * <ul>
 * <li>Javalin</li>
 * <li>SQLite JDBC Driver</li>
 * <li>Jackson (for JSON mapping)</li>
 * </ul>
 */
public class Main {

    private static final int PORT = 7000;


    /**
     * The main entry point for the application.
     * * <p>The steps executed are:</p>
     * <ol>
     * <li>Initialize the SQLite database, creating tables and sample data if needed.</li>
     * <li>Start the Javalin web server on {@value #PORT}.</li>
     * <li>Configure static file serving and CORS.</li>
     * <li>Define all API routes by calling {@link #setupRoutes(Javalin)}.</li>
     * </ol>
     * @param args Command line arguments (not used).
     */
    public static void main(String[] args) {
        // 1. Initialize the Database (creates tables and inserts sample data if needed)
        DatabaseSetup.createNewDatabase();

        // 2. Start the Javalin Web Server
        Javalin app = Javalin.create(config -> {
            // Serve static files (index.html, css, js) from the root directory
            config.staticFiles.add(staticFiles -> {
                staticFiles.hostedPath = "/"; // URL path prefix
                staticFiles.directory = "/public"; // Folder inside src/main/resources
                staticFiles.location = Location.CLASSPATH; // Load from resources
            });

            // Configure CORS to allow the frontend to communicate with the API
            config.plugins.enableCors(cors -> {
                cors.add(it -> it.anyHost());
            });
        }).start(PORT);

        System.out.println("Server started successfully on port " + PORT + " (http://localhost:7000)");

        // 3. Setup API Routes
        setupRoutes(app);
    }

    /**
     * Configures all necessary API endpoints for the application.
     * Routes are mapped to the corresponding methods in the {@link ApiController}.
     * * @param app The Javalin application instance to attach the routes to.
     */
    private static void setupRoutes(Javalin app) {
        // Patient CRUD/List routes
        app.get("/api/patients", ApiController::getAllPatients);
        app.post("/api/patients", ApiController::addPatient);

        // Progress routes (New feature)
        app.post("/api/progress", ApiController::addPatientProgress);
        app.get("/api/progress/{patientId}", ApiController::getPatientProgress);
    }
}
