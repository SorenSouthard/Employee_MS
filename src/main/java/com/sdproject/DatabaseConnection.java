    package com.sdproject;

    import java.sql.Connection;
    import java.sql.DriverManager;
    import java.sql.PreparedStatement;
    import java.sql.ResultSet;
    import java.sql.SQLException;
    import java.util.ArrayList;
    import java.util.List;

    public class DatabaseConnection {
        private static final String URL = "jdbc:mysql://localhost:3306/employeedata";
        private static final String USER = "root";
        private static final String PASSWORD = "password";

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
                e.empid, e.Fname, e.Lname, e.email, e.HireDate, e.Salary, e.SSN,
                p.pay_date, p.earnings, p.fed_tax, p.fed_med, p.fed_SS, p.state_tax, p.retire_401k, p.health_care,
                a.street, a.city_ID, a.state_ID, a.zip, a.gender, a.identified_race, a.DOB, a.phone_number,
                d.Name AS division_name, 
                jt.job_title AS job_title
            FROM employees e
            LEFT JOIN payroll p ON e.empid = p.empid
            LEFT JOIN address a ON e.empid = a.empid
            LEFT JOIN employee_division ed ON e.empid = ed.empid
            LEFT JOIN division d ON ed.div_id = d.ID
            LEFT JOIN employee_job_titles ejt ON e.empid = ejt.empid
            LEFT JOIN job_titles jt ON ejt.job_title_id = jt.job_title_id
            WHERE e.empid = ?
        """;

        try (Connection conn = getConnection();
            PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, empid);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    // Create and return an Employee object with the retrieved data
                    return new Employee(
                        rs.getInt("empid"),
                        rs.getString("Fname"),
                        rs.getString("Lname"),
                        rs.getString("email"),
                        rs.getString("HireDate"),
                        rs.getDouble("Salary"),
                        rs.getString("SSN"),
                        rs.getString("pay_date"),
                        rs.getDouble("earnings"),
                        rs.getDouble("fed_tax"),
                        rs.getDouble("fed_med"),
                        rs.getDouble("fed_SS"),
                        rs.getDouble("state_tax"),
                        rs.getDouble("retire_401k"),
                        rs.getDouble("health_care"), 
                        rs.getString("street"),
                        rs.getInt("city_ID"),
                        rs.getInt("state_ID"),
                        rs.getString("zip"),
                        rs.getString("gender"),
                        rs.getString("identified_race"),
                        rs.getString("DOB"),
                        rs.getString("phone_number"),
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

    public static List<Employee> getAllEmployees() throws SQLException {
        String query = """
            SELECT 
                e.empid, e.Fname, e.Lname, e.email, e.HireDate, e.Salary, e.SSN,
                MAX(p.pay_date) AS pay_date, MAX(p.earnings) AS earnings, MAX(p.fed_tax) AS fed_tax, 
                MAX(p.fed_med) AS fed_med, MAX(p.fed_SS) AS fed_SS, MAX(p.state_tax) AS state_tax, 
                MAX(p.retire_401k) AS retire_401k, MAX(p.health_care) AS health_care,
                MAX(a.street) AS street, MAX(a.city_ID) AS city_ID, MAX(a.state_ID) AS state_ID, 
                MAX(a.zip) AS zip, MAX(a.gender) AS gender, MAX(a.identified_race) AS identified_race, 
                MAX(a.DOB) AS DOB, MAX(a.phone_number) AS phone_number,
                MAX(d.Name) AS division_name, 
                MAX(jt.job_title) AS job_title
            FROM employees e
            LEFT JOIN payroll p ON e.empid = p.empid
            LEFT JOIN address a ON e.empid = a.empid
            LEFT JOIN employee_division ed ON e.empid = ed.empid
            LEFT JOIN division d ON ed.div_id = d.ID
            LEFT JOIN employee_job_titles ejt ON e.empid = ejt.empid
            LEFT JOIN job_titles jt ON ejt.job_title_id = jt.job_title_id
            GROUP BY e.empid
        """;

        List<Employee> employees = new ArrayList<>();
        try (Connection conn = getConnection();
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                employees.add(new Employee(
                    rs.getInt("empid"),
                    rs.getString("Fname"),
                    rs.getString("Lname"),
                    rs.getString("email"),
                    rs.getString("HireDate"),
                    rs.getDouble("Salary"),
                    rs.getString("SSN"),
                    rs.getString("pay_date"),
                    rs.getDouble("earnings"),
                    rs.getDouble("fed_tax"),
                    rs.getDouble("fed_med"),
                    rs.getDouble("fed_SS"),
                    rs.getDouble("state_tax"),
                    rs.getDouble("retire_401k"),
                    rs.getDouble("health_care"), 
                    rs.getString("street"),
                    rs.getInt("city_ID"),
                    rs.getInt("state_ID"),
                    rs.getString("zip"),
                    rs.getString("gender"),
                    rs.getString("identified_race"),
                    rs.getString("DOB"),
                    rs.getString("phone_number"),
                    rs.getString("division_name"),
                    rs.getString("job_title")
                ));
            }
        }
        System.out.println(employees);
        return employees;
    }

    public static List<Employee> searchEmployees(String column, String value) throws SQLException {
        List<String> allowed = List.of("Fname", "Lname", "DOB", "SSN", "empid");
        if (!allowed.contains(column)) {
            throw new IllegalArgumentException("Invalid search column: " + column);
        }
    
        String condition;
        if (column.equals("empid")) {
            condition = "e.empid = ?";
        } else {
            condition = "LOWER(e." + column + ") LIKE LOWER(?)";
        }
    
        String query =
            "SELECT " +
                "e.empid, e.Fname, e.Lname, e.email, e.HireDate, e.Salary, e.SSN, " +
                "MAX(p.pay_date) AS pay_date, MAX(p.earnings) AS earnings, MAX(p.fed_tax) AS fed_tax, " +
                "MAX(p.fed_med) AS fed_med, MAX(p.fed_SS) AS fed_SS, MAX(p.state_tax) AS state_tax, " +
                "MAX(p.retire_401k) AS retire_401k, MAX(p.health_care) AS health_care, " +
                "MAX(a.street) AS street, MAX(a.city_ID) AS city_ID, MAX(a.state_ID) AS state_ID, " +
                "MAX(a.zip) AS zip, MAX(a.gender) AS gender, MAX(a.identified_race) AS identified_race, " +
                "MAX(a.DOB) AS DOB, MAX(a.phone_number) AS phone_number, " +
                "MAX(d.Name) AS division_name, MAX(jt.job_title) AS job_title " +
            "FROM employees e " +
            "LEFT JOIN payroll p ON e.empid = p.empid " +
            "LEFT JOIN address a ON e.empid = a.empid " +
            "LEFT JOIN employee_division ed ON e.empid = ed.empid " +
            "LEFT JOIN division d ON ed.div_id = d.ID " +
            "LEFT JOIN employee_job_titles ejt ON e.empid = ejt.empid " +
            "LEFT JOIN job_titles jt ON ejt.job_title_id = jt.job_title_id " +
            "WHERE " + condition + " " +
            "GROUP BY e.empid";
    
        List<Employee> employees = new ArrayList<>();
    
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
    
            if (column.equals("empid")) {
                stmt.setInt(1, Integer.parseInt(value));
            } else {
                stmt.setString(1, "%" + value + "%");  // fuzzy match
            }
    
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    employees.add(new Employee(
                        rs.getInt("empid"),
                        rs.getString("Fname"),
                        rs.getString("Lname"),
                        rs.getString("email"),
                        rs.getString("HireDate"),
                        rs.getDouble("Salary"),
                        rs.getString("SSN"),
                        rs.getString("pay_date"),
                        rs.getDouble("earnings"),
                        rs.getDouble("fed_tax"),
                        rs.getDouble("fed_med"),
                        rs.getDouble("fed_SS"),
                        rs.getDouble("state_tax"),
                        rs.getDouble("retire_401k"),
                        rs.getDouble("health_care"),
                        rs.getString("street"),
                        rs.getInt("city_ID"),
                        rs.getInt("state_ID"),
                        rs.getString("zip"),
                        rs.getString("gender"),
                        rs.getString("identified_race"),
                        rs.getString("DOB"),
                        rs.getString("phone_number"),
                        rs.getString("division_name"),
                        rs.getString("job_title")
                    ));
                }
            }
        }
        return employees;
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