package com.everleaf.servlet;

import com.everleaf.dao.DocumentDAO;
import com.everleaf.dao.DocumentDAOImpl;
import com.everleaf.model.Document;
import com.everleaf.util.CsrfUtil;
import com.everleaf.util.FileUtil;
import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.File;
import java.io.IOException;
import java.util.List;

@WebServlet(urlPatterns = {"/documents", "/documents/*"})
@MultipartConfig(maxFileSize = 10485760, maxRequestSize = 20971520)
public class DocumentServlet extends HttpServlet {
    private final DocumentDAO documentDAO = new DocumentDAOImpl();
    private final Gson gson = com.everleaf.util.GsonUtil.getGson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int userId = (int) req.getSession().getAttribute("user_id");
        String pathInfo = req.getPathInfo();
        String search = req.getParameter("search");
        String category = req.getParameter("category");

        List<Document> docs;
        if (search != null && !search.trim().isEmpty()) {
            docs = documentDAO.searchDocuments(userId, search);
        } else if (category != null) {
            docs = documentDAO.getDocumentsByCategory(userId, Integer.parseInt(category));
        } else {
            docs = documentDAO.getDocumentsByUser(userId);
        }

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().write(gson.toJson(docs));
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!CsrfUtil.validateToken(req)) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Invalid CSRF token");
            return;
        }
        int userId = (int) req.getSession().getAttribute("user_id");
        String pathInfo = req.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/")) {
            Part filePart = req.getPart("file");
            if (filePart == null || !FileUtil.isAllowedFileSize(filePart.getSize()) || !FileUtil.isAllowedFileType(filePart.getContentType())) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid file");
                return;
            }

            FileUtil.ensureUploadDirectoryExists();
            String originalFilename = filePart.getSubmittedFileName();
            String storageFilename = FileUtil.generateStorageFilename(originalFilename);
            filePart.write(FileUtil.getUploadDirectory() + File.separator + storageFilename);

            Document doc = new Document();
            doc.setUserId(userId);
            doc.setCategoryId(Integer.parseInt(req.getParameter("categoryId")));
            doc.setTitle(req.getParameter("title"));
            doc.setDescription(req.getParameter("description"));
            doc.setOriginalFilename(originalFilename);
            doc.setStorageFilename(storageFilename);
            doc.setMimeType(filePart.getContentType());
            doc.setFileSize(filePart.getSize());
            doc.setFavorite(false);

            boolean success = documentDAO.addDocument(doc);
            resp.setContentType("application/json");
            resp.getWriter().write("{\"success\":" + success + "}");
        } else if (pathInfo.equals("/update")) {
            Document doc = new Document();
            doc.setDocumentId(Integer.parseInt(req.getParameter("documentId")));
            doc.setUserId(userId);
            doc.setCategoryId(Integer.parseInt(req.getParameter("categoryId")));
            doc.setTitle(req.getParameter("title"));
            doc.setDescription(req.getParameter("description"));
            boolean success = documentDAO.updateDocument(doc);
            resp.getWriter().write("{\"success\":" + success + "}");
        } else if (pathInfo.equals("/delete")) {
            int documentId = Integer.parseInt(req.getParameter("documentId"));
            boolean success = documentDAO.deleteDocument(documentId, userId);
            resp.getWriter().write("{\"success\":" + success + "}");
        } else if (pathInfo.equals("/favorite")) {
            int documentId = Integer.parseInt(req.getParameter("documentId"));
            boolean favorite = Boolean.parseBoolean(req.getParameter("favorite"));
            boolean success = documentDAO.updateFavoriteStatus(documentId, userId, favorite);
            resp.getWriter().write("{\"success\":" + success + "}");
        }
    }
}
