# Everleaf — Database Design

## 1. Overview
The Everleaf application utilizes a MySQL relational database named `everleaf_db`. The schema is normalized and consists of three primary tables: `users`, `categories`, and `documents`. This structure efficiently supports digital document management while ensuring data integrity.

## 2. Entity-Relationship Description

The database relies on strong relational links between entities:
- A **User** has many **Categories** (1:N relationship).
- A **User** has many **Documents** (1:N relationship).
- A **Category** has many **Documents** (1:N relationship).
- A **Document** belongs to exactly one **User**, and optionally belongs to one **Category**.

### ER Diagram

```text
  USERS
  +-------------+
  | user_id (PK)|<---+
  | name        |    |
  | email       |    |
  | password    |    |
  +-------------+    |
         ^           |
         |           |
     (1:N)           |
         |           | (1:N)
  CATEGORIES         |
  +-------------+    |
  | category_id |    |
  | user_id (FK)+----+
  | name        |<---+
  +-------------+    |
         ^           |
         |           |
     (1:N)           |
         |           |
  DOCUMENTS          |
  +-------------+    |
  | document_id |    |
  | user_id (FK)+----+
  | category_id |(FK)
  | title       +----+
  | filename    |
  | file_size   |
  +-------------+
```

## 3. Table Definitions

### `users` table
Stores core account information and authentication credentials.

| Column | Type | Constraints | Description |
|---|---|---|---|
| `user_id` | INT | PK, AUTO_INCREMENT | Unique user identifier |
| `name` | VARCHAR(100) | NOT NULL | User's full name |
| `email` | VARCHAR(255) | NOT NULL, UNIQUE | User's email address |
| `password_hash` | VARCHAR(255) | NOT NULL | BCrypt hashed password |
| `created_at` | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP | Account creation date |

### `categories` table
Allows users to organize their documents into logical folders.

| Column | Type | Constraints | Description |
|---|---|---|---|
| `category_id` | INT | PK, AUTO_INCREMENT | Unique category identifier |
| `user_id` | INT | FK → users.user_id, NOT NULL | Owner of the category |
| `category_name` | VARCHAR(100) | NOT NULL | Category display name |
| `created_at` | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP | Category creation date |

**UNIQUE KEY**: `(user_id, category_name)` - Ensures a user cannot have duplicate category names.

### `documents` table
Stores metadata for uploaded files. The actual files reside on the filesystem.

| Column | Type | Constraints | Description |
|---|---|---|---|
| `document_id` | INT | PK, AUTO_INCREMENT | Unique document identifier |
| `user_id` | INT | FK → users.user_id, NOT NULL | Document owner |
| `category_id` | INT | FK → categories.category_id, NULLABLE | Associated category |
| `title` | VARCHAR(255) | NOT NULL | Document title |
| `description` | TEXT | NULLABLE | Document description |
| `original_filename` | VARCHAR(255) | NOT NULL | Original uploaded filename |
| `storage_filename` | VARCHAR(255) | NOT NULL, UNIQUE | UUID-based storage filename |
| `mime_type` | VARCHAR(100) | NULLABLE | File MIME type |
| `file_size` | BIGINT | NULLABLE | File size in bytes |
| `upload_date` | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP | Upload timestamp |
| `is_favorite` | BOOLEAN | DEFAULT FALSE | Favorite status |

## 4. Referential Integrity

Foreign key constraints are strictly enforced to maintain data consistency:
- **`users` → `categories`**: `ON DELETE CASCADE`. If a user is deleted, all their custom categories are automatically removed.
- **`users` → `documents`**: `ON DELETE CASCADE`. If a user is deleted, all their document metadata is removed (file cleanup on disk is handled by the application layer).
- **`categories` → `documents`**: `ON DELETE SET NULL`. If a category is deleted, the associated documents are not deleted; instead, they become "uncategorized" by setting their `category_id` to NULL.

## 5. Indexes

Indexes are used to optimize query performance, particularly for lookups and joins:
- **`PRIMARY KEY`** indexes automatically exist on `user_id`, `category_id`, and `document_id`.
- **`UNIQUE (email)`** on `users`: Optimizes login lookups and enforces unique accounts.
- **`UNIQUE (user_id, category_name)`** on `categories`: Fast validation during category creation.
- **Foreign Key Indexes**: MySQL implicitly indexes foreign keys (`user_id` and `category_id` in the `documents` table), which heavily speeds up the retrieval of a user's dashboard data.

## 6. Data Security

- **Password Storage**: Passwords are emphatically not stored in plain text. They are hashed using BCrypt, protecting against database dumps.
- **Tenant Isolation**: Every application query regarding documents or categories strictly filters by `user_id` enforced at the DAO layer.
- **SQL Injection Prevention**: `PreparedStatement`s are utilized across all database operations, preventing malicious injection of SQL commands.
- **Data Minimization**: No sensitive PII (beyond email) is stored unnecessarily, and seed scripts use dummy data.

## 7. Sample Queries

**1. Retrieve a user's documents with category names:**
```sql
SELECT d.document_id, d.title, d.file_size, c.category_name 
FROM documents d 
LEFT JOIN categories c ON d.category_id = c.category_id 
WHERE d.user_id = ? 
ORDER BY d.upload_date DESC;
```
*Explanation: Uses a LEFT JOIN to fetch category names alongside documents. It gracefully handles documents that have no assigned category.*

**2. Delete a category safely:**
```sql
DELETE FROM categories WHERE category_id = ? AND user_id = ?;
```
*Explanation: The strict inclusion of `user_id` ensures that a malicious actor cannot delete another user's category by guessing the `category_id`.*
