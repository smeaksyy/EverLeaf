package com.everleaf.dao;

import com.everleaf.model.User;

public interface UserDAO {
    boolean registerUser(User user);
    User findByEmail(String email);
    User findById(int userId);
}
