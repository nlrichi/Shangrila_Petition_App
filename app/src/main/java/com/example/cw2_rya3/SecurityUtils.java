package com.example.cw2_rya3;
import java.security.MessageDigest;

public class SecurityUtils {
    public static String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedHash = digest.digest(password.getBytes("UTF-8"));
            StringBuilder sb = new StringBuilder();
            for (byte b : encodedHash) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean verifyPassword(String plainText, String storedHash) {
        return hashPassword(plainText).equals(storedHash);
    }
}
