package com.everleaf.model;

import java.time.LocalDateTime;

public class Category {
    private int categoryId;
    private int userId;
    private String categoryName;
    private LocalDateTime createdAt;

    public Category() {}

    public Category(int categoryId, int userId, String categoryName, LocalDateTime createdAt) {
        this.categoryId = categoryId;
        this.userId = userId;
        this.categoryName = categoryName;
        this.createdAt = createdAt;
    }

    public int getCategoryId() { return categoryId; }
    public void setCategoryId(int categoryId) { this.categoryId = categoryId; }
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    @Override
    public String toString() {
        return "Category{" +
                "categoryId=" + categoryId +
                ", userId=" + userId +
                ", categoryName='" + categoryName + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}
