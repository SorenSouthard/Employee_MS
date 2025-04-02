package com.sdproject;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/employeedata";
    private static final String USER = "root";
    private static final String PASSWORD = "pass";

    public static Connection getConnection() throws SQLException {
        try {
            Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Database connection established successfully");
            return conn;
        } catch (SQLException e) {
            System.out.println("Failed to establish database connection");
            throw new SQLException("Database connection failed: " + e.getMessage(), e);
        }
    }

    public static boolean validateUser(String username, String password) throws SQLException {
        String query = "SELECT password_hash FROM users WHERE username = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String storedPasswordHash = rs.getString("password_hash");
                    return storedPasswordHash.equals(password); // Replace with proper hash comparison
                }
            }
        } catch (SQLException e) {
            System.out.println("Error validating user: " + e.getMessage());
            throw e;
        }
        return false; // User not found or password mismatch
    }
    
    public static int getEmpId(String username) throws SQLException {
        String query = "SELECT empid FROM users WHERE username = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("empid");
                }
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving employee ID: " + e.getMessage());
            throw e;
        }
        return -1; // Employee ID not found
    }

    public static Employee getEmployeeDetails(int empid) throws SQLException {
        String query = """
            SELECT 
                e.Fname, e.Lname, e.email, e.salary, 
                d.Name AS division_name, 
                jt.job_title AS job_title
            FROM employees e
            JOIN employee_division ed ON e.empid = ed.empid
            JOIN division d ON ed.div_id = d.ID
            JOIN employee_job_titles ejt ON e.empid = ejt.empid
            JOIN job_titles jt ON ejt.job_title_id = jt.job_title_id
            WHERE e.empid = ?
        """;
    
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, empid);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    // Create and return an Employee object with the retrieved data
                    return new Employee(
                        rs.getString("Fname"),
                        rs.getString("Lname"),
                        rs.getString("email"),
                        rs.getDouble("salary"),
                        rs.getString("division_name"),
                        rs.getString("job_title")
                    );
                }
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving employee details: " + e.getMessage());
            throw e;
        }
        return null; // Return null if no employee is found
    }

    public static void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
                System.out.println("Database connection closed successfully");
            } catch (SQLException e) {
                System.out.println("Error closing database connection: " + e.getMessage());
            }
        }
    }
}