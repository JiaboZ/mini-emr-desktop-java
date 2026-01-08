📘 MiniEMR Desktop (Java Swing)

A desktop-based Electronic Medical Record (EMR) system built with Java, simulating core workflows commonly found in real-world healthcare software such as Accuro EMR.

This project focuses on appointment scheduling, business rule enforcement, and clean layered architecture, rather than simple CRUD operations.

✨ Features
🧑‍⚕️ Patient Management

Create, edit, and search patients

Health Card Number uniqueness validation

JDBC-based persistence with SQL Server

Desktop UI built with Java Swing

📅 Appointment Scheduling

Book appointments for patients and providers

Provider-level time-slot conflict detection

Friendly validation messages for scheduling conflicts

Appointments displayed with patient and provider names (JOIN-based queries)

Supports multiple appointments per day

🧠 Business Logic

Clear separation of concerns:

UI layer (Swing panels & dialogs)

Service layer (business rules & validation)

DAO layer (JDBC + SQL)

Business exceptions translated into user-friendly messages

🧪 Testing

Unit tests written with JUnit 5

Service-layer tests using Mockito

Tests cover:

Appointment conflict detection

Input validation

SQL exception translation logic

🖥️ Screenshots
Patients Management

Patient list with search, add, and edit functionality

Appointments Management

Provider dropdown selection

Patient picker dialog

Appointment table showing patient & provider names

Conflict validation popup when time slot is unavailable

🏗️ Architecture Overview
UI (Swing)
├── Panels / Dialogs
│
Service Layer
├── Business validation
├── Conflict detection
│
DAO Layer
├── JDBC
├── SQL Server
│
Database (Docker)

🛠️ Tech Stack

Java 17

Java Swing

JDBC

SQL Server

Docker & Docker Compose

Maven

JUnit 5

Mockito

🚀 Getting Started
Prerequisites

Java 17+

Docker

Maven

Run Database
docker-compose up -d

Build & Run
mvn clean package


Run the App main class from your IDE.