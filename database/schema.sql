CREATE DATABASE IF NOT EXISTS everleaf_db;
USE everleaf_db;

CREATE TABLE users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_users_email (email)
);

CREATE TABLE categories (
    category_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    category_name VARCHAR(100) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    UNIQUE KEY uk_user_category (user_id, category_name)
);

CREATE TABLE documents (
    document_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    category_id INT,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    original_filename VARCHAR(255) NOT NULL,
    storage_filename VARCHAR(255) NOT NULL UNIQUE,
    mime_type VARCHAR(100),
    file_size BIGINT,
    upload_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_favorite BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (category_id) REFERENCES categories(category_id) ON DELETE SET NULL,
    INDEX idx_documents_user (user_id),
    INDEX idx_documents_category (category_id),
    INDEX idx_documents_title (title),
    INDEX idx_documents_favorite (user_id, is_favorite)
);

CREATE TABLE IF NOT EXISTS notes (
    note_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    category_id INT,
    title VARCHAR(255) NOT NULL,
    content TEXT,
    color VARCHAR(20) DEFAULT '#FEF9C3',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (category_id) REFERENCES categories(category_id) ON DELETE SET NULL,
    INDEX idx_notes_user (user_id),
    INDEX idx_notes_category (category_id)
);

