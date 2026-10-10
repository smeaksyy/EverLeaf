# Everleaf — System Architecture

## 1. Overview
The Everleaf application is built on a robust, classic MVC-inspired layered architecture. This design cleanly separates concerns, ensuring that presentation, business logic, data access, and data representation are decoupled. The architecture leverages modern web standards on the frontend while relying on rock-solid Java EE (Jakarta EE) standards on the backend. This structure promotes maintainability, testability, and scalability.

## 2. Architecture Diagram

```text
┌─────────────────────────────────────────────────────────┐
│                    PRESENTATION LAYER                   │
│          HTML5 / CSS3 / JavaScript (Fetch API)          │
├─────────────────────────────────────────────────────────┤
│                     SERVLET LAYER                       │
│    RegisterServlet, LoginServlet, DashboardServlet,     │
│    DocumentServlet, CategoryServlet, DownloadServlet    │
│    AuthFilter (Security)                                │
├─────────────────────────────────────────────────────────┤
│                    SERVICE LAYER                        │
│     UserService, CategoryService, DocumentService       │
├─────────────────────────────────────────────────────────┤
│                      DAO LAYER                          │
│     UserDAO/Impl, CategoryDAO/Impl, DocumentDAO/Impl    │
├─────────────────────────────────────────────────────────┤
│                     MODEL LAYER                         │
│           User, Category, Document (POJOs)              │
├─────────────────────────────────────────────────────────┤
│                    UTILITY LAYER                        │
│  DatabaseConnection, PasswordUtil, FileUtil,            │
│  CsrfUtil, ValidationUtil                               │
├─────────────────────────────────────────────────────────┤
│                      DATABASE                           │
│                MySQL (everleaf_db)                      │
└─────────────────────────────────────────────────────────┘
```

## 3. Layer Descriptions

- **Presentation Layer**: Built with HTML, CSS, and vanilla JavaScript. It relies heavily on the modern Fetch API to communicate asynchronously with the backend, mostly expecting JSON payloads. This provides a dynamic, single-page-like experience without full page reloads.
- **Servlet Layer (Controllers)**: Acts as the entry point for all HTTP requests. These Jakarta Servlets handle routing, coordinate authentication (via `AuthFilter`), extract request parameters, and dispatch commands to the Service Layer.
- **Service Layer**: Contains the core business logic. It orchestrates complex operations, enforces business rules, handles validation, and coordinates transactions across multiple DAOs if necessary.
- **DAO Layer (Data Access Object)**: Responsible for all database interactions. It uses an interface-based design pattern (e.g., `UserDAO` interface with `UserDAOImpl` implementation) to decouple business logic from persistence mechanisms.
- **Model Layer**: Consists of Plain Old Java Objects (POJOs) like `User`, `Category`, and `Document` that represent database entities. They are used to transfer data between layers.
- **Utility Layer**: Provides cross-cutting functionalities that are used throughout the application, such as database connection management, password hashing (BCrypt), file system operations, CSRF token management, and input validation.

## 4. Request Flow

A typical request follows a synchronous, top-down path:
1. **User Interaction**: The user interacts with the UI (e.g., clicks a button on an HTML page).
2. **AJAX Request**: JavaScript intercepts the event and sends an HTTP request using the Fetch API.
3. **Security Check**: The `AuthFilter` intercepts the request, verifying the user's session and checking for valid CSRF tokens. Unauthenticated requests are redirected or denied.
4. **Servlet Processing**: The appropriate Servlet receives the request, parses parameters/JSON, and validates input formats.
5. **Business Logic Execution**: The Servlet calls the relevant Service layer method.
6. **Data Access**: The Service layer delegates database operations to the DAO layer.
7. **Database Execution**: The DAO executes parameterized SQL queries via JDBC and maps ResultSets back to Model POJOs.
8. **Response Generation**: The data flows back up. The Servlet formats the result (usually as JSON) and writes it to the HTTP response.

## 5. Authentication Flow

- **Registration**: The user submits details. The Servlet receives the request, and the Service layer checks for email uniqueness via the DAO. The password is hashed using BCrypt (`PasswordUtil`) before being stored in the database.
- **Login**: The user submits credentials. The system retrieves the hashed password for the provided email. BCrypt compares the raw password against the hash. If successful, a new session is created, the user ID is stored in the session, and a CSRF token is generated.

## 6. File Upload Flow

1. **Client Submission**: A `multipart/form-data` request containing the file is submitted.
2. **Servlet Handling**: `DocumentServlet` processes the multi-part request.
3. **File Processing**: `FileUtil` generates a unique UUID-based filename (to prevent collisions) while preserving the original name and MIME type in memory.
4. **Storage**: The physical file is saved securely on the server's filesystem outside the web root (to prevent direct execution or access).
5. **Database Logging**: A record containing metadata (original name, storage UUID, size, MIME type, associated category, and user) is inserted into the `documents` table via the `DocumentDAO`.

## 7. Security Architecture

- **Authentication & Authorization**: Handled via secure, server-side sessions. The `AuthFilter` protects all secured endpoints.
- **Data Protection**: Passwords are never stored in plain text. jBCrypt is used for strong hashing.
- **SQL Injection Prevention**: The DAO layer strictly utilizes JDBC `PreparedStatement`s for all dynamic queries.
- **Cross-Site Request Forgery (CSRF)**: State-changing operations require a valid CSRF token, managed by `CsrfUtil`.
- **File Upload Security**: Uploaded files are renamed using UUIDs and stored outside the web root. File extensions and MIME types can be validated before saving.

## 8. Technology Decisions

- **Java 17 & Jakarta EE 10**: Provides a stable, high-performance, and strongly-typed backend environment. Jakarta EE is the modern standard for enterprise Java web apps.
- **Tomcat 10.1**: A robust and widely-used servlet container fully compliant with the Jakarta Servlet 6.0 specification.
- **MySQL**: A reliable, ACID-compliant relational database, perfect for structured entity relationships.
- **Vanilla JS (Fetch API)**: Keeps the frontend lightweight and removes the need for heavy frameworks, focusing purely on core web concepts.
- **BCrypt**: Industry-standard cryptographic hash function designed specifically for secure password storage.
