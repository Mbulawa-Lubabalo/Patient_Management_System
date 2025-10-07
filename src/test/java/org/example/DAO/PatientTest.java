package org.example.DAO;


import org.example.model.Patient;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class PatientTest {

    @Test
    void testDefaultConstructorAndSetters() {
        Patient patient = new Patient();

        patient.setId(1);
        patient.setName("John Doe");
        patient.setDiagnosis("Flu");
        patient.setTreatmentPlan("Rest and fluids");

        assertEquals(1, patient.getId());
        assertEquals("John Doe", patient.getName());
        assertEquals("Flu", patient.getDiagnosis());
        assertEquals("Rest and fluids", patient.getTreatmentPlan());
    }

    @Test
    void testParameterizedConstructor() {
        Patient patient = new Patient(2, "Jane Smith", "Cold", "Vitamin C and rest");

        assertEquals(2, patient.getId());
        assertEquals("Jane Smith", patient.getName());
        assertEquals("Cold", patient.getDiagnosis());
        assertEquals("Vitamin C and rest", patient.getTreatmentPlan());
    }

    @Test
    void testToString() {
        Patient patient = new Patient(3, "Alice", "Headache", "Painkillers");
        String result = patient.toString();

        assertTrue(result.contains("Alice"));
        assertTrue(result.contains("Headache"));
        assertTrue(result.contains("Painkillers"));
    }
}
