# 🏥 Oncology Patient Management System

## 📖 Project Overview
This is a **single-page web application** designed for oncologists and clinical staff to efficiently **track patient information, treatment plans,** and **key clinical markers** (like tumor marker levels) in real-time.  

The application is built using **HTML, CSS, and JavaScript**, and relies on a **RESTful API backend (Java)** for data persistence.

The main advantage of this system is the **real-time visualization** of patient progression, enabling clinicians to quickly identify treatment response or disease progression.

---

## ✨ Features

- **🧍 Patient Registration:** Add new patients with essential details (Name, Diagnosis, Treatment Plan).
- **📈 Progress Tracking:** Record periodic updates (Cycle Number, Measurement Date, Tumor Marker Level, Status, Dose).
- **🔢 Quick ID Assignment:** One-click Patient ID transfer to the Progress Entry form.
- **📊 Real-time Visualization:** Interactive line chart (via **Chart.js**) showing tumor marker trends across treatment cycles.
- **📱 Responsive Design:** Optimized for both desktop and mobile layouts using pure CSS.

---

## ⚙️ Setup & Dependencies

This project is **frontend-only**, but it **requires a running backend** to store and retrieve patient data.

### 1. Backend API (Required)

The frontend expects a Java (or similar) REST API running at:

http://localhost:7000/api


#### Required Endpoints:
| Method | Endpoint | Description |
|:-------|:----------|:-------------|
| `POST` | `/api/patients` | Register a new patient |
| `GET` | `/api/patients` | Retrieve all patients |
| `POST` | `/api/progress` | Record a new progress entry |
| `GET` | `/api/progress/{patientId}` | Retrieve all progress entries for a specific patient |

🧩 **Action Required:**  
Make sure your backend server is running on **port 7000** and serving the `/api` routes before opening the frontend.

---

### 2. Running the Client

Since this is a simple static web app, no build tools are needed.

```bash
# 1. Clone or download the project
git clone https://github.com/<your-repo>/Oncology-Patient-Management-System.git
cd Oncology-Patient-Management-System

# 2. Start your backend (Java Javalin server)
mvn exec:java

# Option 1: Run via backend serving static files
http://localhost:7000/

# Option 2: Open directly in a browser
open src/main/resources/public/index.html
```

## 👩‍⚕️ Usage Guide

### 🩺 A. Register a New Patient
1. Fill out the **Register New Patient** form.  
2. Click **"Register Patient"**.  
3. A new **Patient ID** (e.g., `42`) will appear in the **Active Patient Records** table.  

---

### 💊 B. Record Patient Progress
1. Find the patient in the table and click **"Update Progress ID"** to auto-fill their ID.  
2. Enter:
   - **Cycle Number**  
   - **Measurement Date**  
   - **Tumor Marker Level (U/mL)**  
3. Click **"Record Progress"** to save the entry.  

---

### 📈 C. Visualize Progress
1. Click **"View Progress"** next to a patient.  
2. A **line chart** will appear, displaying tumor marker trends over time.  

---

## 🛠️ Technology Stack

| **Layer** | **Technology** |
|------------|----------------|
| **Frontend** | HTML5, CSS3, Vanilla JavaScript |
| **Visualization** | Chart.js v4.4.2 |
| **Backend** | Java + Javalin |
| **Database** | SQLite (via JDBC) |

---

## 💡 Future Enhancements
- 🔐 Add authentication for clinicians.  
- 📤 Enable exporting patient data to **CSV/PDF**.  
- ⚠️ Add notifications for significant tumor marker changes.  
- 🤖 Integrate **machine learning insights** for treatment prediction.  

---

## 🧑‍💻 Author
**Lubabalo Mbulawa**  
*Software Engineer*  

📧 [email or LinkedIn link here]  
