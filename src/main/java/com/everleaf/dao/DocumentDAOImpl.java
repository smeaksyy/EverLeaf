package com.everleaf.dao;

import com.everleaf.model.Document;
import com.everleaf.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DocumentDAOImpl implements DocumentDAO {
    @Override
    public boolean addDocument(Document doc) {
        String sql = "INSERT INTO documents (user_id, category_id, title, description, original_filename, storage_filename, mime_type, file_size, favorite) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, doc.getUserId());
            pstmt.setInt(2, doc.getCategoryId());
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
        return fetchList("SELECT * FROM documents WHERE user_id = ? ORDER BY upload_date DESC", userId);
    }

    @Override
    public Document getDocumentById(int documentId, int userId) {
        String sql = "SELECT * FROM documents WHERE document_id = ? AND user_id = ?";
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
        String sql = "SELECT * FROM documents WHERE user_id = ? AND (title LIKE ? OR description LIKE ? OR original_filename LIKE ?) ORDER BY upload_date DESC";
        List<Document> list = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            String like = "%" + query + "%";
            pstmt.setString(2, like);
            pstmt.setString(3, like);
            pstmt.setString(4, like);
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
        String sql = "SELECT * FROM documents WHERE user_id = ? AND category_id = ? ORDER BY upload_date DESC";
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
            pstmt.setInt(1, doc.getCategoryId());
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
        String sql = "UPDATE documents SET favorite = ? WHERE document_id = ? AND user_id = ?";
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
        return fetchCount("SELECT COUNT(*) FROM documents WHERE user_id = ? AND favorite = true", userId);
    }

    @Override
    public List<Document> getRecentDocuments(int userId, int limit) {
        String sql = "SELECT * FROM documents WHERE user_id = ? ORDER BY upload_date DESC LIMIT ?";
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
        return fetchList("SELECT * FROM documents WHERE user_id = ? AND favorite = true ORDER BY upload_date DESC", userId);
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
        return new Document(
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
            rs.getBoolean("favorite")
        );
    }
}
