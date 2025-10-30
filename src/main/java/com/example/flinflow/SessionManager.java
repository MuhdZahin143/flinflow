package com.example.flinflow;

/**
 * SessionManager - Manages user authentication session
 * Handles login state and user data across the application
 */
public class SessionManager {
    
    // Store current logged-in user
    private static String currentUsername = null;
    private static String currentFullName = null;
    private static Integer currentUserId = null;
    
    /**
     * Set user session after successful login
     */
    public static void login(String username, String fullName, Integer userId) {
        currentUsername = username;
        currentFullName = fullName;
        currentUserId = userId;
        System.out.println("✅ Session started for: " + fullName + " (@" + username + ")");
    }
    
    /**
     * Clear user session (logout)
     */
    public static void logout() {
        System.out.println("🔓 User logged out: " + currentUsername);
        currentUsername = null;
        currentFullName = null;
        currentUserId = null;
    }
    
    /**
     * Check if user is logged in
     */
    public static boolean isLoggedIn() {
        return currentUsername != null;
    }
    
    /**
     * Get current username
     */
    public static String getCurrentUsername() {
        return currentUsername;
    }
    
    /**
     * Get current full name
     */
    public static String getCurrentFullName() {
        return currentFullName;
    }
    
    /**
     * Get current user ID
     */
    public static Integer getCurrentUserId() {
        return currentUserId;
    }
    
    /**
     * Validate session - return true if valid, false if expired
     */
    public static boolean validateSession() {
        if (!isLoggedIn()) {
            System.out.println("⚠️ No active session found");
            return false;
        }
        return true;
    }
}