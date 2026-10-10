package com.everleaf.dao;

import com.everleaf.model.Document;
import java.util.List;

public interface DocumentDAO {
    boolean addDocument(Document document);
    List<Document> getDocumentsByUser(int userId);
    Document getDocumentById(int documentId, int userId);
    List<Document> searchDocuments(int userId, String query);
    List<Document> getDocumentsByCategory(int userId, int categoryId);
    boolean updateDocument(Document document);
    boolean deleteDocument(int documentId, int userId);
    boolean updateFavoriteStatus(int documentId, int userId, boolean favorite);
    int getDocumentCountByUser(int userId);
    int getFavoriteCountByUser(int userId);
    List<Document> getRecentDocuments(int userId, int limit);
    List<Document> getFavoriteDocuments(int userId);
}
