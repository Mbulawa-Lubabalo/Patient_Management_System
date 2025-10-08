package org.example.controller;

import io.javalin.http.Context;
import io.javalin.http.InternalServerErrorResponse;
import org.example.DAO.PatientDAO;
import org.example.DAO.PatientProgressDAO;
import org.example.model.Patient;
import org.example.model.PatientProgress;

import java.util.List;

/**
 * Controller class to handle all API endpoints for the application.
 * It manages routing requests, processing data, and interacting with the DAO layer.
 */
public class ApiController {

    private static final PatientDAO patientDAO = new PatientDAO();
    private static final PatientProgressDAO progressDAO = new PatientProgressDAO();

    // -------------------------------------------------------------------
    // PATIENT API METHODS
    // -------------------------------------------------------------------

    /**
     * Handles GET request to retrieve all patient records.
     * Responds with a JSON array of Patient objects.
     * @param ctx The Javalin request context.
     */
    public static void getAllPatients(Context ctx) {
        try {
            List<Patient> patients = patientDAO.getAllPatients();
            ctx.json(patients);
        } catch (Exception e) {
            System.err.println("Failed to fetch patients: " + e.getMessage());
            throw new InternalServerErrorResponse("Failed to fetch patient list.");
        }
    }

    /**
     * Handles POST request to add a new patient record.
     * Expects a JSON body mapping to the Patient model.
     * On successful creation, returns the new Patient object with its generated ID.
     * @param ctx The Javalin request context containing the Patient JSON body.
     */
    public static void addPatient(Context ctx) {
        try {
            Patient newPatient = ctx.bodyAsClass(Patient.class);
            int id = patientDAO.addPatient(newPatient);

            if (id > 0) {
                newPatient.setId(id);
                ctx.status(201).json(newPatient); // Return the newly created patient with its ID
            } else {
                ctx.status(400).result("Failed to save patient. Check data validity.");
            }
        } catch (Exception e) {
            System.err.println("Error processing new patient record: " + e.getMessage());
            throw new InternalServerErrorResponse("Failed to add new patient.");
        }
    }

    // -------------------------------------------------------------------
    // PROGRESS API METHODS
    // -------------------------------------------------------------------

    /**
     * Handles POST request to add a new patient progress entry.
     * Expects a JSON body mapping to the PatientProgress model.
     * Performs basic validation on patient ID and cycle number.
     * @param ctx The Javalin request context containing the PatientProgress JSON body.
     */
    public static void addPatientProgress(Context ctx) {
        try {
            // Javalin/Jackson maps the incoming JSON fields to the PatientProgress POJO
            PatientProgress newProgress = ctx.bodyAsClass(PatientProgress.class);

            // Basic validation
            if (newProgress.getPatientId() <= 0 || newProgress.getCycleNumber() < 0) {
                ctx.status(400).result("Invalid patient ID or cycle number.");
                return;
            }

            progressDAO.insertProgress(newProgress);

            ctx.status(201).result("Progress record successfully added.");

        } catch (Exception e) {
            System.err.println("Error processing new progress record: " + e.getMessage());
            throw new InternalServerErrorResponse("Failed to add patient progress due to a server error.");
        }
    }

    /**
     * Handles GET request to retrieve all progress entries for a specific patient.
     * Expects the patient ID as a path parameter.
     * Responds with a JSON array of PatientProgress objects.
     * @param ctx The Javalin request context containing the patientId path parameter.
     * @throws if no progress data is found for the given ID.
     */
    public static void getPatientProgress(Context ctx) {
        try {
            int patientId = ctx.pathParamAsClass("patientId", Integer.class).get();

            if (patientId <= 0) {
                ctx.status(400).result("Invalid patient ID provided.");
                return;
            }

            List<PatientProgress> progressList = progressDAO.getProgressByPatientId(patientId);

            if (progressList.isEmpty()) {
                ctx.status(404).result("No progress data found for patient ID: " + patientId);
                return;
            }

            ctx.json(progressList);
        } catch (Exception e) {
            System.err.println("Error retrieving patient progress: " + e.getMessage());
            throw new InternalServerErrorResponse("Failed to retrieve progress data.");
        }
    }
}
