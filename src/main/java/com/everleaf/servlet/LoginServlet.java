package com.everleaf.servlet;

import com.everleaf.model.User;
import com.everleaf.service.UserService;
import com.everleaf.util.CsrfUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    private final UserService userService = new UserService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        CsrfUtil.generateToken(req.getSession(true));
        req.getRequestDispatcher("/login.html").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!CsrfUtil.validateToken(req)) {
            resp.sendRedirect(req.getContextPath() + "/login?error=Invalid CSRF Token");
            return;
        }

        String email = req.getParameter("email");
        String password = req.getParameter("password");

        User user = userService.authenticate(email, password);
        if (user != null) {
            HttpSession session = req.getSession(true);
            session.setAttribute("user_id", user.getUserId());
            session.setAttribute("user_name", user.getName());
            resp.sendRedirect(req.getContextPath() + "/dashboard");
        } else {
            resp.sendRedirect(req.getContextPath() + "/login?error=Invalid email or password");
        }
    }
}
