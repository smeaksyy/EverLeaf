package com.everleaf.dao;

import com.everleaf.model.User;
import com.everleaf.util.DatabaseConnection;

import java.sql.*;

public class UserDAOImpl implements UserDAO {
    @Override
    public boolean registerUser(User user) {
        String sql = "INSERT INTO users (name, email, password_hash) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, user.getName());
            pstmt.setString(2, user.getEmail());
            pstmt.setString(3, user.getPasswordHash());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public User findByEmail(String email) {
        String sql = "SELECT * FROM users WHERE email = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, email);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUser(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public User findById(int userId) {
        String sql = "SELECT * FROM users WHERE user_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUser(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        return new User(
            rs.getInt("user_id"),
            rs.getString("name"),
            rs.getString("email"),
            rs.getString("password_hash"),
            rs.getTimestamp("created_at") != null ? rs.getTimestamp("created_at").toLocalDateTime() : null
        );
    }
}
