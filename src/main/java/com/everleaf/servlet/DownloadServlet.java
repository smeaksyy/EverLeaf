package com.everleaf.servlet;

import com.everleaf.dao.DocumentDAO;
import com.everleaf.dao.DocumentDAOImpl;
import com.everleaf.model.Document;
import com.everleaf.util.FileUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

@WebServlet("/download")
public class DownloadServlet extends HttpServlet {
    private final DocumentDAO documentDAO = new DocumentDAOImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int userId = (int) req.getSession().getAttribute("user_id");
        String docIdParam = req.getParameter("documentId");

        if (docIdParam == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing document ID");
            return;
        }

        Document doc = documentDAO.getDocumentById(Integer.parseInt(docIdParam), userId);
        if (doc == null) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Document not found or unauthorized");
            return;
        }

        File file = new File(FileUtil.getUploadDirectory() + File.separator + doc.getStorageFilename());
        if (!file.exists()) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "File not found on server");
            return;
        }

        resp.setContentType(doc.getMimeType());
        resp.setHeader("Content-Disposition", "attachment; filename=\"" + doc.getOriginalFilename() + "\"");
        resp.setContentLengthLong(doc.getFileSize());

        try (FileInputStream in = new FileInputStream(file);
             OutputStream out = resp.getOutputStream()) {
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
        }
    }
}
