package com.everleaf.service;

import com.everleaf.dao.DocumentDAO;
import com.everleaf.dao.DocumentDAOImpl;

public class DocumentService {
    private final DocumentDAO documentDAO = new DocumentDAOImpl();
    
    // Wrapper methods can be added here as needed by servlets
}
