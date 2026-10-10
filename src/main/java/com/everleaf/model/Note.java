package com.everleaf.model;

import java.time.LocalDateTime;

public class Note {
    private int noteId;
    private int userId;
    private Integer categoryId;
    private String categoryName;
    private String title;
    private String content;
    private String color;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Note() {}

    public Note(int noteId, int userId, Integer categoryId, String title, String content, String color, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.noteId = noteId;
        this.userId = userId;
        this.categoryId = categoryId;
        this.title = title;
        this.content = content;
        this.color = color;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public int getNoteId() { return noteId; }
    public void setNoteId(int noteId) { this.noteId = noteId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public Integer getCategoryId() { return categoryId; }
    public void setCategoryId(Integer categoryId) { this.categoryId = categoryId; }

    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
