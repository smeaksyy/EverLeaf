package com.everleaf.servlet;

import com.everleaf.dao.*;
import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@WebServlet("/dashboard")
public class DashboardServlet extends HttpServlet {
    private final DocumentDAO documentDAO = new DocumentDAOImpl();
    private final CategoryDAO categoryDAO = new CategoryDAOImpl();
    private final Gson gson = com.everleaf.util.GsonUtil.getGson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user_id") == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        String acceptHeader = req.getHeader("Accept");
        if (acceptHeader != null && acceptHeader.contains("text/html")) {
            req.getRequestDispatcher("/dashboard.html").forward(req, resp);
            return;
        }

        int userId = (int) session.getAttribute("user_id");
        String userName = (String) session.getAttribute("user_name");

        Map<String, Object> data = new HashMap<>();
        Map<String, Object> userMap = new HashMap<>();
        userMap.put("name", userName != null ? userName : "User");
        data.put("user", userMap);

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalDocs", documentDAO.getDocumentCountByUser(userId));
        stats.put("categories", categoryDAO.getCategoryCountByUser(userId));
        stats.put("favorites", documentDAO.getFavoriteCountByUser(userId));
        stats.put("recent", documentDAO.getRecentDocuments(userId, 100).size());
        data.put("stats", stats);

        data.put("userName", userName != null ? userName : "User");
        data.put("documentCount", documentDAO.getDocumentCountByUser(userId));
        data.put("categoryCount", categoryDAO.getCategoryCountByUser(userId));
        data.put("favoriteCount", documentDAO.getFavoriteCountByUser(userId));
        data.put("recentlyAddedCount", documentDAO.getRecentDocuments(userId, 100).size());
        data.put("recentDocuments", documentDAO.getRecentDocuments(userId, 5));
        data.put("categories", categoryDAO.getCategoriesByUser(userId));

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().write(gson.toJson(data));
    }
}
