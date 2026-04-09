# MediNeed – Technical Documentation 

## 1. Introduction

MediNeed is an **offline-first Android application** designed to digitize medicine inventory workflows in healthcare environments where connectivity is unreliable.

Unlike typical systems that depend on constant internet access, MediNeed ensures that **all critical operations work locally**, making it highly reliable for rural clinics and government hospitals. 

This documentation explains the **architecture, workflow, and internal design decisions** behind the system.

---

## 2. Design Philosophy

The system is built around three core principles:

* **Offline-first** → App should never stop due to network issues
* **Reliability over complexity** → Stable and predictable behavior
* **Practical usability** → Designed for real healthcare workflows

---

## 3. Tech Stack (Detailed Breakdown)

MediNeed uses a modern Android stack with carefully selected libraries:

### Core Stack

* **Kotlin** → Primary language
* **Jetpack Compose** → Declarative UI
* **MVVM Architecture** → Clean separation of concerns

---

### Data & Storage

* **Room Database** → Local persistent storage
* **MMKV** → Fast key-value storage (for configs, flags, small data)
* **Kotlin Serialization** → Efficient data parsing and transfer

---

### Dependency Injection

* **Koin** → Lightweight dependency injection framework

---

### Background Processing

* **WorkManager** → Handles background tasks like expiry and stock checks

---

### Networking

* **OkHttp** → Efficient HTTP client for backend communication

---

### Image Handling

* **Coil** → Image loading and caching (if medicine images are used)

---

### Document Generation

* **iText-Core** → PDF report generation

---

### Build & Tooling

* **KSP (Kotlin Symbol Processing)** → Used by Room and other libraries
* **Ktfmt Gradle** → Code formatting and consistency
* **Accompanist Permissions** → Runtime permission handling
* **Compose Navigation** → Screen navigation

---

## 4. System Architecture

MediNeed follows a **modular MVVM architecture** with clear separation of layers:

---

### 1. Presentation Layer (UI)

Built using **Jetpack Compose**

Responsibilities:

* Rendering UI
* Handling user interactions
* Navigation using **Compose Navigation**
* Managing permissions using **Accompanist Permissions**

---

### 2. ViewModel Layer

* Acts as a bridge between UI and data
* Holds UI state
* Contains business logic

Uses:

* **Koin** for dependency injection

---

### 3. Domain Layer

* Defines core models and business rules
* Handles decision-making logic like:

  * When stock is considered low
  * When expiry alert should trigger

---

### 4. Data Layer

Handles all data operations:

* **Room Database**

  * Stores medicines locally
  * Provides DAO for queries

* **MMKV**

  * Stores lightweight data (settings, flags)

* **Kotlin Serialization**

  * Converts data for storage or network transfer

---

### 5. Background Layer

* Managed by **WorkManager**
* Runs periodic tasks:

  * Expiry checks
  * Low stock detection

---

### 6. Network Layer

* Built using **OkHttp**
* Handles:

  * Future sync operations
  * Backend communication

---

## 5. End-to-End Workflow

### Step 1: Medicine Entry

* User enters medicine details via Compose UI
* Data passed to ViewModel
* ViewModel validates and processes input

---

### Step 2: Local Storage

* Data stored in **Room Database**
* Ensures:

  * Immediate persistence
  * No dependency on internet

---

### Step 3: State Management

* ViewModel observes database changes
* UI updates automatically (reactive system)

---

### Step 4: Background Processing

* **WorkManager** triggers periodic worker
* Worker scans database:

  * Checks expiry dates
  * Checks stock levels

---

### Step 5: Alert Generation

* If conditions match:

  * Expiry near
  * Stock below threshold

→ Alerts are generated inside the app

---

### Step 6: Report Generation

* User can generate reports
* **iText-Core** used to create structured PDF reports

---

### Step 7: Optional Sync (Future-ready)

* Data can be sent to backend via **OkHttp**
* Enables:

  * Central monitoring
  * Multi-device access

---

## 6. Offline-First Strategy

MediNeed follows a **local-first data flow**:

1. All writes happen locally (Room DB)
2. UI reads from local database
3. Background workers operate locally
4. Network is used only as an enhancement

### Benefits:

* No downtime
* Faster performance
* Reliable in rural conditions

---

## 7. Data Flow (Simplified)

```
User Input → ViewModel → Room DB → UI Updates
                           ↓
                    WorkManager Worker
                           ↓
                      Alert System
                           ↓
                  (Optional) OkHttp Sync
```

---

## 8. Key Architectural Decisions

### 1. Room over Remote DB

* Ensures offline capability
* Provides structured relational storage

---

### 2. WorkManager for Reliability

* Handles background tasks even if app is closed
* Ensures alerts are always triggered

---

### 3. Koin for Simplicity

* Lightweight and easy to integrate
* Reduces boilerplate

---

### 4. MMKV for Performance

* Faster than SharedPreferences
* Suitable for small data

---

### 5. iText for Reports

* Allows structured and professional PDF generation

---

## 9. Challenges & Solutions

### Challenge 1: Offline Data Consistency

* Solution: Room DB as single source of truth

---

### Challenge 2: Background Execution Limits

* Solution: WorkManager for reliable scheduling

---

### Challenge 3: Clean Architecture in Hackathon Time

* Solution: MVVM + Koin for fast but structured setup

---

### Challenge 4: User Simplicity

* Solution: Compose UI with minimal steps and clear flows

---

## 10. Scalability Considerations

MediNeed is designed to scale:

* Backend sync can be extended
* Multi-device support can be added
* ML layer can integrate on top of existing data

---

## 11. Future Enhancements

* Real-time sync using backend
* ML-based demand prediction
* Barcode scanning integration
* SMS/Push notification system
* Role-based access system

---

## 12. Final Note

MediNeed is not just an inventory app.
It is a **reliable, offline-capable healthcare support system** designed with real-world constraints in mind.

The architecture ensures that even in the absence of internet, **critical healthcare operations continue without disruption**, which is the core requirement in rural environments.

---
