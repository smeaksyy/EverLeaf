package com.everleaf.service;

import com.everleaf.dao.UserDAO;
import com.everleaf.dao.UserDAOImpl;
import com.everleaf.model.User;
import com.everleaf.util.PasswordUtil;
import com.everleaf.util.ValidationUtil;

public class UserService {
    private final UserDAO userDAO = new UserDAOImpl();

    public RegisterResult register(String name, String email, String password, String confirmPassword) {
        if (ValidationUtil.isNullOrEmpty(name) || ValidationUtil.isNullOrEmpty(email) || ValidationUtil.isNullOrEmpty(password)) {
            return new RegisterResult(false, "All fields are required.", null);
        }
        if (!ValidationUtil.isValidEmail(email)) {
            return new RegisterResult(false, "Invalid email format.", null);
        }
        if (password.length() < 8) {
            return new RegisterResult(false, "Password must be at least 8 characters long.", null);
        }
        if (!password.equals(confirmPassword)) {
            return new RegisterResult(false, "Passwords do not match.", null);
        }

        if (userDAO.findByEmail(email) != null) {
            return new RegisterResult(false, "Email is already registered.", null);
        }

        User user = new User();
        user.setName(ValidationUtil.sanitizeHtml(name));
        user.setEmail(email);
        user.setPasswordHash(PasswordUtil.hashPassword(password));

        boolean success = userDAO.registerUser(user);
        if (success) {
            User registeredUser = userDAO.findByEmail(email);
            return new RegisterResult(true, "Registration successful.", registeredUser);
        } else {
            return new RegisterResult(false, "Registration failed due to server error.", null);
        }
    }

    public User authenticate(String email, String password) {
        User user = userDAO.findByEmail(email);
        if (user != null && PasswordUtil.checkPassword(password, user.getPasswordHash())) {
            return user;
        }
        return null;
    }
}
