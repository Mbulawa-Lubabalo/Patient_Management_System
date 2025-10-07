// API Configuration
const API_BASE_URL = 'http://localhost:7000/api';

// Global state for Chart.js instance
let progressChartInstance = null;

document.addEventListener('DOMContentLoaded', () => {
    loadPatients();
    setupAddPatientForm();
    setupAddProgressForm();
});

// -------------------------------------------------------------
// 1. Patient Registration Form Handling
// -------------------------------------------------------------

function setupAddPatientForm() {
    const form = document.getElementById('add-patient-form');
    form.addEventListener('submit', async (e) => {
        e.preventDefault();

        const formData = new FormData(form);
        const data = Object.fromEntries(formData.entries());

        try {
            const response = await fetch(`${API_BASE_URL}/patients`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(data),
            });

            if (response.ok) {
                const newPatient = await response.json();
                alert(`Patient ${newPatient.name} registered successfully! ID: ${newPatient.id}`);
                form.reset();
                loadPatients(); // Refresh the patient list
            } else {
                const errorText = await response.text();
                alert(`Failed to register patient: ${errorText || response.statusText}`);
            }
        } catch (error) {
            console.error('Error submitting patient registration:', error);
            alert('Network error while submitting patient registration.');
        }
    });
}

// -------------------------------------------------------------
// 2. Patient Progress Form Handling
// -------------------------------------------------------------

function setupAddProgressForm() {
    const form = document.getElementById('add-progress-form');
    form.addEventListener('submit', async (e) => {
        e.preventDefault();

        const formData = new FormData(form);
        const data = Object.fromEntries(formData.entries());

        // Convert numerical fields
        data.patientId = parseInt(data.patientId);
        data.cycleNumber = parseInt(data.cycleNumber);
        data.tumorMarkerLevel = parseFloat(data.tumorMarkerLevel);

        try {
            const response = await fetch(`${API_BASE_URL}/progress`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(data),
            });

            if (response.status === 201) {
                alert('Patient progress recorded successfully!');
                form.reset();
            } else {
                const errorText = await response.text();
                alert(`Failed to record progress: ${errorText || response.statusText}`);
            }
        } catch (error) {
            console.error('Error submitting progress:', error);
            alert('Network error while submitting progress.');
        }
    });
}

// -------------------------------------------------------------
// 3. Patient List and Table Rendering
// -------------------------------------------------------------

async function loadPatients() {
    const tbody = document.getElementById('patient-list-body');
    tbody.innerHTML = '<tr><td colspan="5" class="text-center-placeholder text-gray-500">Loading patient data...</td></tr>';

    try {
        const response = await fetch(`${API_BASE_URL}/patients`);

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        const patients = await response.json();
        tbody.innerHTML = ''; // Clear loading message

        if (patients.length === 0) {
             tbody.innerHTML = '<tr><td colspan="5" class="text-center-placeholder text-gray-500 py-4">No active patient records found.</td></tr>';
             return;
        }

        patients.forEach(patient => {
            const row = tbody.insertRow();
            row.className = 'hover:bg-gray-50 transition duration-100';

            row.insertCell().textContent = patient.id;
            row.insertCell().textContent = patient.name;
            row.insertCell().textContent = patient.diagnosis;
            row.insertCell().textContent = patient.treatmentPlan;

            const actionCell = row.insertCell();
            const progressBtn = document.createElement('button');
            progressBtn.textContent = 'View Progress';
            progressBtn.className = 'action-btn py-1 px-3 bg-yellow-400 text-text-dark rounded-lg hover:bg-yellow-500 transition duration-150 shadow-md';
            progressBtn.onclick = () => showProgressView(patient.id, patient.name);

            const updateIdBtn = document.createElement('button');
            updateIdBtn.textContent = 'Update Progress ID';
            updateIdBtn.className = 'action-btn ml-2 py-1 px-3 bg-indigo-500 text-white rounded-lg hover:bg-indigo-600 transition duration-150 shadow-md';
            updateIdBtn.onclick = () => document.getElementById('progress_patient_id').value = patient.id;

            actionCell.appendChild(progressBtn);
            actionCell.appendChild(updateIdBtn);
        });

    } catch (error) {
        console.error('Failed to load patients:', error);
        tbody.innerHTML = '<tr><td colspan="5" class="text-center-placeholder text-danger py-4">Error loading data. Is the Java server running on port 7000?</td></tr>';
    }
}

// -------------------------------------------------------------
// 4. Progress Visualization (Chart.js)
// -------------------------------------------------------------

async function showProgressView(patientId, patientName) {
    const chartCard = document.getElementById('progress-visualization-card');
    const chartTitle = document.getElementById('chart-title');

    chartTitle.textContent = `Patient Progress: ${patientName} (ID: ${patientId})`;
    chartCard.classList.remove('hidden');

    try {
        const response = await fetch(`${API_BASE_URL}/progress/${patientId}`);

        if (!response.ok) {
            if (response.status === 404) {
                alert(`No progress data found for ${patientName}. Please add an initial entry.`);
                chartCard.classList.add('hidden');
                return;
            }
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        const data = await response.json();
        renderChart(data);

    } catch (error) {
        console.error('Failed to load progress data:', error);
        alert('Error loading progress data. Check console.');
        chartCard.classList.add('hidden');
    }
}

function hideProgressView() {
    document.getElementById('progress-visualization-card').classList.add('hidden');
}

function renderChart(progressData) {
    const ctx = document.getElementById('progressChart').getContext('2d');

    // Extract data for Chart.js
    const labels = progressData.map(p => `Cycle ${p.cycleNumber} (${p.measurementDate})`);
    const tumorMarkers = progressData.map(p => p.tumorMarkerLevel);
    const statuses = progressData.map(p => p.status); // Used for tooltips/annotations

    // Destroy existing chart instance if it exists
    if (progressChartInstance) {
        progressChartInstance.destroy();
    }

    progressChartInstance = new Chart(ctx, {
        type: 'line',
        data: {
            labels: labels,
            datasets: [{
                label: 'Tumor Marker Level (U/mL)',
                data: tumorMarkers,
                borderColor: '#dc3545', // Danger/Red color for cancer marker
                backgroundColor: 'rgba(220, 53, 69, 0.1)',
                fill: true,
                tension: 0.2,
                pointRadius: 6,
                pointHoverRadius: 8,
                pointBackgroundColor: tumorMarkers.map(level => level > 200 ? '#dc3545' : '#28a745'), // Visual indicator
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
                legend: {
                    display: true,
                    position: 'top',
                },
                tooltip: {
                    callbacks: {
                        // Add Status and Dose information to the tooltip
                        title: (context) => context[0].label,
                        label: (context) => {
                            const progress = progressData[context.dataIndex];
                            return [
                                `Marker: ${context.formattedValue} U/mL`,
                                `Status: ${progress.status}`,
                                `Dose: ${progress.treatmentDose}`
                            ];
                        }
                    }
                },
                title: {
                    display: true,
                    text: 'Tumor Marker Progression Over Treatment Cycles'
                }
            },
            scales: {
                y: {
                    title: {
                        display: true,
                        text: 'Tumor Marker Level (U/mL)'
                    },
                    beginAtZero: true
                },
                x: {
                    title: {
                        display: true,
                        text: 'Treatment Cycle'
                    }
                }
            }
        }
    });
}
