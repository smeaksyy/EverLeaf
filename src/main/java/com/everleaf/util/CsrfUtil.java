package com.everleaf.util;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.util.UUID;

public class CsrfUtil {
    public static String generateToken(HttpSession session) {
        String existing = (String) session.getAttribute("csrfToken");
        if (existing != null && !existing.trim().isEmpty()) {
            return existing;
        }
        String token = UUID.randomUUID().toString();
        session.setAttribute("csrfToken", token);
        return token;
    }

    public static boolean validateToken(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) return false;
        
        String sessionToken = (String) session.getAttribute("csrfToken");
        if (sessionToken == null || sessionToken.trim().isEmpty()) return false;
        
        String requestToken = request.getParameter("csrfToken");
        if (requestToken == null || requestToken.trim().isEmpty()) {
            requestToken = request.getParameter("csrf_token");
        }
        if (requestToken == null || requestToken.trim().isEmpty()) {
            requestToken = request.getHeader("X-CSRF-Token");
        }
        
        return sessionToken.equals(requestToken);
    }
}
