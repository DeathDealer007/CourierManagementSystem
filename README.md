# Courier Management System

Welcome to the **Courier Management System** repository. This repository has been cleaned and organized to contain strictly the frontend and backend application source code, separated into clean, modular folders. All presentation decks, documentation binaries, and transient logs have been excluded to keep the codebase lightweight and focused.

---

## 📂 Repository Structure

The project is structured into two main components:

```text
CourierManagementSystem/
├── 📁 frontend/     # Angular CLI based web application
└── 📁 backend/      # Spring Boot based Microservices architecture
```

### 💻 Frontend (`/frontend`)
The frontend is built using **Angular** and provides a responsive dashboard for managing and tracking shipments.
* **Key Features**: Admin dashboard, shipment list, creation interface, user registration/login, live delivery tracking interface.
* **Technology Stack**: Angular, TailwindCSS, TypeScript.
* **To Run**:
  ```bash
  cd frontend
  npm install
  npm start
  ```

### ⚙️ Backend (`/backend`)
The backend is built as a highly scalable **Spring Boot Microservices** suite.
* **Core Services**:
  * **Eureka Server**: Service Discovery registry.
  * **API Gateway**: Router and routing entry-point.
  * **Auth Service**: JWT-based authentication & authorization.
  * **Admin Service**: Administrative controls and user management.
  * **Delivery Service**: Shipment creation, status updates, and management.
  * **Tracking Service**: Live tracking history and event log processing.
* **Messaging**: Asynchronous event handling via RabbitMQ.
* **Database**: PostgreSQL / MySQL configuration.
* **To Run (with Docker Compose)**:
  ```bash
  cd backend
  docker-compose up --build
  ```

---

## 🧼 Cleanup Summary

For clarity and compliance, the following files and directories were removed from the repository:
* 🗙 Powerpoint Presentations (`.pptx`, `.ppt`)
* 🗙 Word Documentation (`.doc`, `.docx`)
* 🗙 Static diagram images (`.png`, `.jpg`, `.jpeg`) unless embedded inside code
* 🗙 System logs (`.log`, `.txt` logs)
* 🗙 Build/target/dist directories (ignored in `.gitignore`)

---

Developed with ❤️ and optimized for deployment.
