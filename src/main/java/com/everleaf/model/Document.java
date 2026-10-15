package com.everleaf.model;

import java.time.LocalDateTime;

public class Document {
    private int documentId;
    private int userId;
    private int categoryId;
    private String categoryName;
    private String title;
    private String description;
    private String originalFilename;
    private String storageFilename;
    private String mimeType;
    private long fileSize;
    private LocalDateTime uploadDate;
    private boolean favorite;

    public Document() {}

    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }

    public Document(int documentId, int userId, int categoryId, String title, String description, 
                    String originalFilename, String storageFilename, String mimeType, 
                    long fileSize, LocalDateTime uploadDate, boolean favorite) {
        this.documentId = documentId;
        this.userId = userId;
        this.categoryId = categoryId;
        this.title = title;
        this.description = description;
        this.originalFilename = originalFilename;
        this.storageFilename = storageFilename;
        this.mimeType = mimeType;
        this.fileSize = fileSize;
        this.uploadDate = uploadDate;
        this.favorite = favorite;
    }

    public int getDocumentId() { return documentId; }
    public void setDocumentId(int documentId) { this.documentId = documentId; }
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public int getCategoryId() { return categoryId; }
    public void setCategoryId(int categoryId) { this.categoryId = categoryId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getOriginalFilename() { return originalFilename; }
    public void setOriginalFilename(String originalFilename) { this.originalFilename = originalFilename; }
    public String getStorageFilename() { return storageFilename; }
    public void setStorageFilename(String storageFilename) { this.storageFilename = storageFilename; }
    public String getMimeType() { return mimeType; }
    public void setMimeType(String mimeType) { this.mimeType = mimeType; }
    public long getFileSize() { return fileSize; }
    public void setFileSize(long fileSize) { this.fileSize = fileSize; }
    public LocalDateTime getUploadDate() { return uploadDate; }
    public void setUploadDate(LocalDateTime uploadDate) { this.uploadDate = uploadDate; }
    public boolean isFavorite() { return favorite; }
    public void setFavorite(boolean favorite) { this.favorite = favorite; }

    public String getFormattedFileSize() {
        if (fileSize < 1024) return fileSize + " B";
        else if (fileSize < 1024 * 1024) return String.format("%.2f KB", fileSize / 1024.0);
        else return String.format("%.2f MB", fileSize / (1024.0 * 1024.0));
    }

    @Override
    public String toString() {
        return "Document{" +
                "documentId=" + documentId +
                ", userId=" + userId +
                ", title='" + title + '\'' +
                ", storageFilename='" + storageFilename + '\'' +
                '}';
    }
}
