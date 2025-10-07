package org.example.controller;

import io.javalin.http.Context;
import io.javalin.http.InternalServerErrorResponse;
import org.example.DAO.PatientDAO;
import org.example.DAO.PatientProgressDAO;
import org.example.model.Patient;
import org.example.model.PatientProgress;

import java.util.List;

/**
 * Handles all REST API endpoints for the patient management system.
 */
public class ApiController {

    private static final PatientDAO patientDAO = new PatientDAO();
    private static final PatientProgressDAO progressDAO = new PatientProgressDAO();

    // -------------------------------------------------------------------
    // PATIENT API METHODS
    // -------------------------------------------------------------------

    public static void getAllPatients(Context ctx) {
        try {
            List<Patient> patients = patientDAO.getAllPatients();
            ctx.json(patients);
        } catch (Exception e) {
            System.err.println("Failed to fetch patients: " + e.getMessage());
            throw new InternalServerErrorResponse("Failed to fetch patient list.");
        }
    }

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
