<div align="center">
  <h1>🌿 Everleaf</h1>
  <h3>A Smart Digital Document Management System</h3>
  <p><strong>Less Paper. More Possibilities.</strong></p>
  
  ![Java 17](https://img.shields.io/badge/Java-17-007396?style=for-the-badge&logo=java&logoColor=white)
  ![Maven](https://img.shields.io/badge/Maven-3.8+-C71A22?style=for-the-badge&logo=apachemaven&logoColor=white)
  ![MySQL](https://img.shields.io/badge/MySQL-8.x-4479A1?style=for-the-badge&logo=mysql&logoColor=white)
  ![Tomcat](https://img.shields.io/badge/Tomcat-10.1-F8DC75?style=for-the-badge&logo=apachetomcat&logoColor=black)
  ![Jakarta Servlet](https://img.shields.io/badge/Jakarta_Servlet-6.0-007396?style=for-the-badge&logo=jakartaee&logoColor=white)
</div>

<hr/>

## 📋 Table of Contents
- [1. Project Overview](#1-project-overview)
- [2. Problem Statement](#2-problem-statement)
- [3. Project Objectives](#3-project-objectives)
- [4. Features](#4-features)
- [5. Technology Stack](#5-technology-stack)
- [6. Architecture](#6-architecture)
- [7. Database Design](#7-database-design)
- [8. Core Java Concepts Demonstrated](#8-core-java-concepts-demonstrated)
- [9. Prerequisites](#9-prerequisites)
- [10. Installation & Setup](#10-installation--setup)
- [11. Project Screenshots](#11-project-screenshots)
- [12. API Endpoints](#12-api-endpoints)
- [13. Security Measures](#13-security-measures)
- [14. Current Project Status](#14-current-project-status)
- [15. Known Limitations](#15-known-limitations)
- [16. Future Enhancements](#16-future-enhancements)
- [17. Project Team](#17-project-team)
- [18. License](#18-license)

---

## 1. Project Overview
**Everleaf** is a digital document management platform designed to help users organize, store, search, and manage documents digitally while encouraging reduced paper consumption. Built as a B.Tech CSE Java Programming project.

## 2. Problem Statement
In today's world, excessive paper usage leads to environmental degradation, document loss, and organizational inefficiency. Everleaf addresses these challenges by providing a digital-first approach to document management.

## 3. Project Objectives
- Build a functional Java web application for document management
- Implement secure user authentication with password hashing
- Create a clean, normalized database design
- Demonstrate core Java concepts (OOP, collections, interfaces, exception handling)
- Develop a responsive, modern user interface
- Promote sustainability through digital document organization

## 4. Features
| Status | Feature |
| :---: | :--- |
| ✅ | User Registration & Login (BCrypt password hashing) |
| ✅ | Session-based Authentication |
| ✅ | CSRF Protection |
| ✅ | Document Upload (PDF, DOCX, TXT, PNG, JPG) |
| ✅ | Document Management (CRUD) |
| ✅ | Category Management (CRUD) |
| ✅ | Document Search |
| ✅ | Favorites System |
| ✅ | Secure File Download |
| ✅ | Responsive Landing Page |
| ✅ | Premium Dashboard |
| ✅ | Input Validation (client + server) |
| 🔄 | User Profile/Settings |
| 🔄 | Document Sharing |
| 🔄 | Advanced Analytics |
| 🔄 | Dark Mode |

*(✅ Implemented \| 🔄 In-Progress/Planned)*

## 5. Technology Stack
| Layer | Technology |
| --- | --- |
| **Frontend** | HTML5, CSS3, JavaScript |
| **Backend** | Java 17, Jakarta Servlets |
| **Database** | MySQL 8.x |
| **Server** | Apache Tomcat 10.1 |
| **Build Tool** | Apache Maven |
| **Security** | BCrypt, CSRF Tokens, PreparedStatement |
| **Version Control** | Git, GitHub |

## 6. Architecture
Everleaf follows an **MVC-like architecture**:
- **Model**: User, Category, Document POJOs
- **DAO Layer**: Interfaces + Implementations for database operations
- **Service Layer**: Business logic and validation
- **Servlet Layer**: HTTP request handling
- **Utility Layer**: Database connection, password hashing, file handling, validation
- **Frontend**: HTML/CSS/JS with fetch API for AJAX calls

*(Reference [`docs/architecture.md`](docs/architecture.md) for detailed architecture)*

## 7. Database Design
Everleaf consists of 3 tables with foreign key relationships:
- `users`
- `categories`
- `documents`

*(Reference [`docs/database-design.md`](docs/database-design.md) for the full ER diagram description)*

```sql
-- Brief snapshot of schema
CREATE TABLE users (id INT PRIMARY KEY, ...);
CREATE TABLE categories (id INT PRIMARY KEY, user_id INT, ...);
CREATE TABLE documents (id INT PRIMARY KEY, user_id INT, category_id INT, ...);
```

## 8. Core Java Concepts Demonstrated
- **Encapsulation**: Private fields with getters/setters in model classes
- **Abstraction**: DAO interfaces (`UserDAO`, `CategoryDAO`, `DocumentDAO`)
- **Inheritance**: Servlets extending `HttpServlet`, Filter implementation
- **Polymorphism**: DAO interface implementations, method overriding (`doGet`/`doPost`)
- **Interfaces**: DAO layer interfaces, Servlet Filter interface
- **Collections & Generics**: `List<Document>`, `List<Category>`, `Map<String, Object>`
- **Exception Handling**: try-with-resources, SQL exception handling
- **String Processing**: Input validation, HTML sanitization, file name generation

## 9. Prerequisites
- Java JDK 17+ installed
- Apache Maven 3.8+ installed
- MySQL 8.0+ installed and running
- Apache Tomcat 10.1+ installed
- Git installed

## 10. Installation & Setup

### Step 1: Clone the Repository
```bash
git clone https://github.com/yourusername/Everleaf.git
cd Everleaf
```

### Step 2: MySQL Database Setup
```bash
mysql -u root -p < database/schema.sql
# Optional seed data
mysql -u root -p everleaf_db < database/seed.sql
```

### Step 3: Configure Database Connection
```bash
cp src/main/resources/db.properties.example src/main/resources/db.properties
# Edit db.properties with your MySQL credentials
```

### Step 4: Build with Maven
```bash
mvn clean package
```

### Step 5: Deploy to Tomcat
```bash
cp target/everleaf.war $CATALINA_HOME/webapps/
# Or deploy through Tomcat Manager
```

### Step 6: Access the Application
```text
http://localhost:8080/everleaf/
```

## 11. Project Screenshots
> 📸 *Screenshots will be added after deployment.*

## 12. API Endpoints
| Method | Endpoint | Description |
| --- | --- | --- |
| **GET** | `/` | Landing page |
| **GET/POST** | `/register` | User registration |
| **GET/POST** | `/login` | User login |
| **GET** | `/logout` | User logout |
| **GET** | `/dashboard` | Dashboard data (JSON) |
| **GET/POST** | `/documents/*` | Document CRUD |
| **GET/POST** | `/categories/*` | Category CRUD |
| **GET** | `/download` | Secure file download |

## 13. Security Measures
- 🔒 **BCrypt password hashing** (never stores plaintext)
- 🔒 **PreparedStatement** for all SQL (SQL injection prevention)
- 🔒 **CSRF token validation** on all state-changing operations
- 🔒 **Session-based authentication** with `AuthFilter`
- 🔒 **Server-side input validation**
- 🔒 **HTML output sanitization**
- 🔒 **Ownership verification** on all data operations
- 🔒 **File type and size validation**
- 🔒 **Safe storage filenames** (UUID-based)
- 🔒 **No exposed internal paths**

## 14. Current Project Status
### Review 1 (Current)
- ✅ Complete project structure
- ✅ Database schema designed and implemented
- ✅ JDBC connectivity with DAO pattern
- ✅ User authentication (register/login/logout)
- ✅ Document management servlets
- ✅ Responsive landing page
- ✅ Premium dashboard UI
- ✅ Security implementations

## 15. Known Limitations
- No email verification yet
- No password reset functionality
- No file preview (download only)
- No document versioning
- Single-user document ownership (no sharing)
- No real-time notifications

## 16. Future Enhancements
- User profile and settings page
- Document sharing between users
- Email verification and password reset
- In-browser document preview
- Advanced search with date filters
- Document versioning
- Dashboard analytics and charts
- Dark mode theme
- Mobile app (Android)
- Cloud storage integration

## 17. Project Team
| Role | Name |
| --- | --- |
| **Developer** | [Your Name] |
| **Guide/Mentor** | [Faculty Name] |

## 18. License
This project is developed for educational purposes as part of B.Tech CSE coursework.

---
<p align="center">
  <strong>Everleaf</strong> — Less Paper. More Possibilities. 🌿<br>
  Made with ☕ Java and 💚 for a greener future.
</p>
