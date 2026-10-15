package com.everleaf.dao;

import com.everleaf.model.Document;
import com.everleaf.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DocumentDAOImpl implements DocumentDAO {
    @Override
    public boolean addDocument(Document doc) {
        String sql = "INSERT INTO documents (user_id, category_id, title, description, original_filename, storage_filename, mime_type, file_size, is_favorite) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, doc.getUserId());
            if (doc.getCategoryId() > 0) {
                pstmt.setInt(2, doc.getCategoryId());
            } else {
                pstmt.setNull(2, Types.INTEGER);
            }
            pstmt.setString(3, doc.getTitle());
            pstmt.setString(4, doc.getDescription());
            pstmt.setString(5, doc.getOriginalFilename());
            pstmt.setString(6, doc.getStorageFilename());
            pstmt.setString(7, doc.getMimeType());
            pstmt.setLong(8, doc.getFileSize());
            pstmt.setBoolean(9, doc.isFavorite());
            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) doc.setDocumentId(rs.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public List<Document> getDocumentsByUser(int userId) {
        String sql = "SELECT d.*, c.category_name FROM documents d LEFT JOIN categories c ON d.category_id = c.category_id WHERE d.user_id = ? ORDER BY d.upload_date DESC";
        return fetchList(sql, userId);
    }

    @Override
    public Document getDocumentById(int documentId, int userId) {
        String sql = "SELECT d.*, c.category_name FROM documents d LEFT JOIN categories c ON d.category_id = c.category_id WHERE d.document_id = ? AND d.user_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, documentId);
            pstmt.setInt(2, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) return mapResultSetToDocument(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Document> searchDocuments(int userId, String query) {
        String sql = "SELECT d.*, c.category_name FROM documents d LEFT JOIN categories c ON d.category_id = c.category_id WHERE d.user_id = ? AND (d.title LIKE ? OR d.description LIKE ? OR c.category_name LIKE ?) ORDER BY d.upload_date DESC";
        List<Document> list = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            String pattern = "%" + query + "%";
            pstmt.setInt(1, userId);
            pstmt.setString(2, pattern);
            pstmt.setString(3, pattern);
            pstmt.setString(4, pattern);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) list.add(mapResultSetToDocument(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public List<Document> getDocumentsByCategory(int userId, int categoryId) {
        String sql = "SELECT d.*, c.category_name FROM documents d LEFT JOIN categories c ON d.category_id = c.category_id WHERE d.user_id = ? AND d.category_id = ? ORDER BY d.upload_date DESC";
        List<Document> list = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.setInt(2, categoryId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) list.add(mapResultSetToDocument(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public boolean updateDocument(Document doc) {
        String sql = "UPDATE documents SET category_id = ?, title = ?, description = ? WHERE document_id = ? AND user_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            if (doc.getCategoryId() > 0) {
                pstmt.setInt(1, doc.getCategoryId());
            } else {
                pstmt.setNull(1, Types.INTEGER);
            }
            pstmt.setString(2, doc.getTitle());
            pstmt.setString(3, doc.getDescription());
            pstmt.setInt(4, doc.getDocumentId());
            pstmt.setInt(5, doc.getUserId());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean deleteDocument(int documentId, int userId) {
        String sql = "DELETE FROM documents WHERE document_id = ? AND user_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, documentId);
            pstmt.setInt(2, userId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean updateFavoriteStatus(int documentId, int userId, boolean favorite) {
        String sql = "UPDATE documents SET is_favorite = ? WHERE document_id = ? AND user_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setBoolean(1, favorite);
            pstmt.setInt(2, documentId);
            pstmt.setInt(3, userId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public int getDocumentCountByUser(int userId) {
        return fetchCount("SELECT COUNT(*) FROM documents WHERE user_id = ?", userId);
    }

    @Override
    public int getFavoriteCountByUser(int userId) {
        return fetchCount("SELECT COUNT(*) FROM documents WHERE user_id = ? AND is_favorite = 1", userId);
    }

    @Override
    public List<Document> getRecentDocuments(int userId, int limit) {
        String sql = "SELECT d.*, c.category_name FROM documents d LEFT JOIN categories c ON d.category_id = c.category_id WHERE d.user_id = ? ORDER BY d.upload_date DESC LIMIT ?";
        List<Document> list = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.setInt(2, limit);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) list.add(mapResultSetToDocument(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public List<Document> getFavoriteDocuments(int userId) {
        String sql = "SELECT d.*, c.category_name FROM documents d LEFT JOIN categories c ON d.category_id = c.category_id WHERE d.user_id = ? AND d.is_favorite = 1 ORDER BY d.upload_date DESC";
        return fetchList(sql, userId);
    }

    private List<Document> fetchList(String sql, int userId) {
        List<Document> list = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) list.add(mapResultSetToDocument(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    private int fetchCount(String sql, int userId) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private Document mapResultSetToDocument(ResultSet rs) throws SQLException {
        Document doc = new Document(
            rs.getInt("document_id"),
            rs.getInt("user_id"),
            rs.getInt("category_id"),
            rs.getString("title"),
            rs.getString("description"),
            rs.getString("original_filename"),
            rs.getString("storage_filename"),
            rs.getString("mime_type"),
            rs.getLong("file_size"),
            rs.getTimestamp("upload_date") != null ? rs.getTimestamp("upload_date").toLocalDateTime() : null,
            rs.getBoolean("is_favorite")
        );
        try {
            doc.setCategoryName(rs.getString("category_name"));
        } catch (SQLException ignored) {}
        return doc;
    }
}
