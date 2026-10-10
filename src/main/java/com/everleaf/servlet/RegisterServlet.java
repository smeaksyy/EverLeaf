package com.everleaf.servlet;

import com.everleaf.service.CategoryService;
import com.everleaf.service.RegisterResult;
import com.everleaf.service.UserService;
import com.everleaf.util.CsrfUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {
    private final UserService userService = new UserService();
    private final CategoryService categoryService = new CategoryService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String token = CsrfUtil.generateToken(req.getSession(true));
        req.setAttribute("csrfToken", token);
        req.getRequestDispatcher("/register.html").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!CsrfUtil.validateToken(req)) {
            resp.sendRedirect(req.getContextPath() + "/register?error=Invalid CSRF Token");
            return;
        }

        String name = req.getParameter("name");
        String email = req.getParameter("email");
        String password = req.getParameter("password");
        String confirmPassword = req.getParameter("confirmPassword");
        if (confirmPassword == null) {
            confirmPassword = req.getParameter("confirm_password");
        }

        RegisterResult result = userService.register(name, email, password, confirmPassword);

        if (result.isSuccess()) {
            categoryService.createDefaultCategories(result.getUser().getUserId());
            resp.sendRedirect(req.getContextPath() + "/login?success=Registration successful. Please login.");
        } else {
            resp.sendRedirect(req.getContextPath() + "/register?error=" + java.net.URLEncoder.encode(result.getMessage(), "UTF-8"));
        }
    }
}
