package com.everleaf.servlet;

import com.everleaf.dao.CategoryDAO;
import com.everleaf.dao.CategoryDAOImpl;
import com.everleaf.model.Category;
import com.everleaf.util.CsrfUtil;
import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.util.List;

@WebServlet(urlPatterns = {"/categories", "/categories/*"})
public class CategoryServlet extends HttpServlet {
    private final CategoryDAO categoryDAO = new CategoryDAOImpl();
    private final Gson gson = com.everleaf.util.GsonUtil.getGson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int userId = (int) req.getSession().getAttribute("user_id");
        List<Category> categories = categoryDAO.getCategoriesByUser(userId);
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().write(gson.toJson(categories));
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
            Category cat = new Category();
            cat.setUserId(userId);
            cat.setCategoryName(req.getParameter("categoryName"));
            boolean success = categoryDAO.createCategory(cat);
            resp.getWriter().write("{\"success\":" + success + "}");
        } else if (pathInfo.equals("/update")) {
            int categoryId = Integer.parseInt(req.getParameter("categoryId"));
            String newName = req.getParameter("categoryName");
            boolean success = categoryDAO.updateCategory(categoryId, newName, userId);
            resp.getWriter().write("{\"success\":" + success + "}");
        } else if (pathInfo.equals("/delete")) {
            int categoryId = Integer.parseInt(req.getParameter("categoryId"));
            boolean success = categoryDAO.deleteCategory(categoryId, userId);
            resp.getWriter().write("{\"success\":" + success + "}");
        }
    }
}
