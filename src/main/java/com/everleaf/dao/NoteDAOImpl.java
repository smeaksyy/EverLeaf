package com.everleaf.dao;

import com.everleaf.model.Note;
import com.everleaf.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NoteDAOImpl implements NoteDAO {

    @Override
    public boolean addNote(Note note) {
        String sql = "INSERT INTO notes (user_id, category_id, title, content, color) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, note.getUserId());
            if (note.getCategoryId() != null && note.getCategoryId() > 0) {
                ps.setInt(2, note.getCategoryId());
            } else {
                ps.setNull(2, Types.INTEGER);
            }
            ps.setString(3, note.getTitle());
            ps.setString(4, note.getContent());
            ps.setString(5, note.getColor() != null ? note.getColor() : "#FEF9C3");
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public List<Note> getNotesByUser(int userId) {
        List<Note> list = new ArrayList<>();
        String sql = "SELECT n.*, c.category_name FROM notes n " +
                     "LEFT JOIN categories c ON n.category_id = c.category_id " +
                     "WHERE n.user_id = ? ORDER BY n.updated_at DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToNote(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public Note getNoteById(int noteId, int userId) {
        String sql = "SELECT n.*, c.category_name FROM notes n " +
                     "LEFT JOIN categories c ON n.category_id = c.category_id " +
                     "WHERE n.note_id = ? AND n.user_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, noteId);
            ps.setInt(2, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToNote(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean updateNote(Note note) {
        String sql = "UPDATE notes SET title = ?, content = ?, category_id = ?, color = ? WHERE note_id = ? AND user_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, note.getTitle());
            ps.setString(2, note.getContent());
            if (note.getCategoryId() != null && note.getCategoryId() > 0) {
                ps.setInt(3, note.getCategoryId());
            } else {
                ps.setNull(3, Types.INTEGER);
            }
            ps.setString(4, note.getColor());
            ps.setInt(5, note.getNoteId());
            ps.setInt(6, note.getUserId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean deleteNote(int noteId, int userId) {
        String sql = "DELETE FROM notes WHERE note_id = ? AND user_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, noteId);
            ps.setInt(2, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public int getNoteCountByUser(int userId) {
        String sql = "SELECT COUNT(*) FROM notes WHERE user_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private Note mapResultSetToNote(ResultSet rs) throws SQLException {
        Note note = new Note();
        note.setNoteId(rs.getInt("note_id"));
        note.setUserId(rs.getInt("user_id"));
        int catId = rs.getInt("category_id");
        if (!rs.wasNull()) {
            note.setCategoryId(catId);
        }
        note.setCategoryName(rs.getString("category_name"));
        note.setTitle(rs.getString("title"));
        note.setContent(rs.getString("content"));
        note.setColor(rs.getString("color"));
        Timestamp cat = rs.getTimestamp("created_at");
        if (cat != null) note.setCreatedAt(cat.toLocalDateTime());
        Timestamp uat = rs.getTimestamp("updated_at");
        if (uat != null) note.setUpdatedAt(uat.toLocalDateTime());
        return note;
    }
}
