package com.everleaf.util;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordUtil {
    public static String hashPassword(String plaintext) {
        return BCrypt.hashpw(plaintext, BCrypt.gensalt(12));
    }

    public static boolean checkPassword(String plaintext, String hashed) {
        if (hashed == null || !hashed.startsWith("$2a$")) return false;
        try {
            return BCrypt.checkpw(plaintext, hashed);
        } catch (Exception e) {
            return false;
        }
    }
}
