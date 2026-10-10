package com.everleaf.dao;

import com.everleaf.model.Category;
import com.everleaf.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CategoryDAOImpl implements CategoryDAO {
    @Override
    public boolean createCategory(Category category) {
        String sql = "INSERT INTO categories (user_id, category_name) VALUES (?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, category.getUserId());
            pstmt.setString(2, category.getCategoryName());
            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        category.setCategoryId(rs.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public List<Category> getCategoriesByUser(int userId) {
        List<Category> categories = new ArrayList<>();
        String sql = "SELECT * FROM categories WHERE user_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    categories.add(mapResultSetToCategory(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return categories;
    }

    @Override
    public Category getCategoryById(int categoryId) {
        String sql = "SELECT * FROM categories WHERE category_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, categoryId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToCategory(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean updateCategory(int categoryId, String newName, int userId) {
        String sql = "UPDATE categories SET category_name = ? WHERE category_id = ? AND user_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, newName);
            pstmt.setInt(2, categoryId);
            pstmt.setInt(3, userId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean deleteCategory(int categoryId, int userId) {
        String sql = "DELETE FROM categories WHERE category_id = ? AND user_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, categoryId);
            pstmt.setInt(2, userId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public int getCategoryCountByUser(int userId) {
        String sql = "SELECT COUNT(*) FROM categories WHERE user_id = ?";
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

    private Category mapResultSetToCategory(ResultSet rs) throws SQLException {
        return new Category(
            rs.getInt("category_id"),
            rs.getInt("user_id"),
            rs.getString("category_name"),
            rs.getTimestamp("created_at") != null ? rs.getTimestamp("created_at").toLocalDateTime() : null
        );
    }
}
