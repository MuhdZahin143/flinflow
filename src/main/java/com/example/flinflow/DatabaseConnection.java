package com.example.flinflow;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/finflow?serverTimezone=UTC";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "admin"; 
    
    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(URL, USERNAME, PASSWORD);
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL Driver not found", e);
        }
    }
    
    // Test connection
    public static void main(String[] args) {
        System.out.println("Testing MySQL connection...");
        System.out.println("URL: " + URL);
        System.out.println("Username: " + USERNAME);
        
        try (Connection conn = getConnection()) {
            System.out.println("MySQL94 connected successfully!");
            
            var stmt = conn.createStatement();
            var rs = stmt.executeQuery("SELECT COUNT(*) as count FROM users");
            if (rs.next()) {
                System.out.println("✅ Users table found with " + rs.getInt("count") + " records");
            }
            
        } catch (SQLException e) {
            System.out.println("Connection failed: " + e.getMessage());
 
            System.out.println("  1. Check MySQL94 service running in services.msc");
            System.out.println("  2. Verify database 'finflow' exists in MySQL Workbench");
            System.out.println("  3. Try different password if needed");
        }
    }
}