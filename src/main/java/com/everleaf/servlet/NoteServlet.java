package com.everleaf.servlet;

import com.everleaf.dao.NoteDAO;
import com.everleaf.dao.NoteDAOImpl;
import com.everleaf.model.Note;
import com.everleaf.util.CsrfUtil;
import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;

@WebServlet(urlPatterns = {"/notes", "/notes/*"})
public class NoteServlet extends HttpServlet {
    private final NoteDAO noteDAO = new NoteDAOImpl();
    private final Gson gson = com.everleaf.util.GsonUtil.getGson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        int userId = (int) session.getAttribute("user_id");

        String pathInfo = req.getPathInfo();
        if (pathInfo != null && pathInfo.length() > 1) {
            try {
                int noteId = Integer.parseInt(pathInfo.substring(1));
                Note note = noteDAO.getNoteById(noteId, userId);
                if (note != null) {
                    resp.setContentType("application/json");
                    resp.setCharacterEncoding("UTF-8");
                    resp.getWriter().write(gson.toJson(note));
                    return;
                } else {
                    resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Note not found");
                    return;
                }
            } catch (NumberFormatException ignored) {}
        }

        List<Note> notes = noteDAO.getNotesByUser(userId);
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().write(gson.toJson(notes));
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!CsrfUtil.validateToken(req)) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Invalid CSRF token");
            return;
        }

        HttpSession session = req.getSession(false);
        int userId = (int) session.getAttribute("user_id");
        String pathInfo = req.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/")) {
            // Create note
            String title = req.getParameter("title");
            String content = req.getParameter("content");
            String color = req.getParameter("color");
            String catParam = req.getParameter("categoryId");

            if (title == null || title.trim().isEmpty()) {
                title = "Untitled Leaf Note";
            }

            Note note = new Note();
            note.setUserId(userId);
            note.setTitle(title.trim());
            note.setContent(content != null ? content : "");
            note.setColor(color != null && !color.isEmpty() ? color : "#FEF9C3");
            if (catParam != null && !catParam.isEmpty()) {
                try {
                    note.setCategoryId(Integer.parseInt(catParam));
                } catch (NumberFormatException ignored) {}
            }

            boolean success = noteDAO.addNote(note);
            resp.setContentType("application/json");
            resp.getWriter().write("{\"success\":" + success + "}");
        } else if (pathInfo.equals("/update")) {
            int noteId = Integer.parseInt(req.getParameter("noteId"));
            String title = req.getParameter("title");
            String content = req.getParameter("content");
            String color = req.getParameter("color");
            String catParam = req.getParameter("categoryId");

            Note note = new Note();
            note.setNoteId(noteId);
            note.setUserId(userId);
            note.setTitle(title != null ? title.trim() : "Untitled Note");
            note.setContent(content != null ? content : "");
            note.setColor(color != null && !color.isEmpty() ? color : "#FEF9C3");
            if (catParam != null && !catParam.isEmpty()) {
                try {
                    note.setCategoryId(Integer.parseInt(catParam));
                } catch (NumberFormatException ignored) {}
            }

            boolean success = noteDAO.updateNote(note);
            resp.setContentType("application/json");
            resp.getWriter().write("{\"success\":" + success + "}");
        } else if (pathInfo.equals("/delete")) {
            int noteId = Integer.parseInt(req.getParameter("noteId"));
            boolean success = noteDAO.deleteNote(noteId, userId);
            resp.setContentType("application/json");
            resp.getWriter().write("{\"success\":" + success + "}");
        }
    }
}
