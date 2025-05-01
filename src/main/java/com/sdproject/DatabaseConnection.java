package com.sdproject;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

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

    public static String validateUser(String username, String password) throws SQLException {
        String query = "SELECT password_hash, role FROM users WHERE username = ?";
        try (Connection conn = getConnection();
            PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String storedPasswordHash = rs.getString("password_hash");
                    String role = rs.getString("role");
                    if (storedPasswordHash.equals(password)) {
                        return role; // Return the role if the password matches
                    } else {
                        System.out.println("Invalid password for user: " + username);
                        return null; // Password mismatch
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Error validating user: " + e.getMessage());
            throw e;
        }
        return null; // User not found or password mismatch
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

    public static int getMaxEmpId() throws SQLException {
        String query = "SELECT MAX(empid) AS max_id FROM employees";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            if (rs.next()) {
                return rs.getInt("max_id");
            }
        }
        return 0; // Default if no employees exist
    }

    public static boolean createUser(int empid, String username, String password, String role) throws SQLException {
        String query = "INSERT INTO users (empid, username, password_hash, role) VALUES (?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, empid);
            stmt.setString(2, username);
            stmt.setString(3, password); // Ideally, hash the password before storing
            stmt.setString(4, role);
            return stmt.executeUpdate() > 0;
        }
    }

    public static boolean deleteEmployee(int empid) throws SQLException {
        String deleteEmployeeDivision = "DELETE FROM employee_division WHERE empid = ?";
        String deleteEmployeeJobTitles = "DELETE FROM employee_job_titles WHERE empid = ?";
        String deleteAddress = "DELETE FROM address WHERE empid = ?";
        String deletePayroll = "DELETE FROM payroll WHERE empid = ?";
        String deleteUsers = "DELETE FROM users WHERE empid = ?";
        String deleteEmployee = "DELETE FROM employees WHERE empid = ?";
    
        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false); // Start transaction
    
            // Delete from employee_division
            try (PreparedStatement stmt = conn.prepareStatement(deleteEmployeeDivision)) {
                stmt.setInt(1, empid);
                stmt.executeUpdate();
            }
    
            // Delete from employee_job_titles
            try (PreparedStatement stmt = conn.prepareStatement(deleteEmployeeJobTitles)) {
                stmt.setInt(1, empid);
                stmt.executeUpdate();
            }
    
            // Delete from address
            try (PreparedStatement stmt = conn.prepareStatement(deleteAddress)) {
                stmt.setInt(1, empid);
                stmt.executeUpdate();
            }
    
            // Delete from payroll
            try (PreparedStatement stmt = conn.prepareStatement(deletePayroll)) {
                stmt.setInt(1, empid);
                stmt.executeUpdate();
            }
    
            // Delete from users
            try (PreparedStatement stmt = conn.prepareStatement(deleteUsers)) {
                stmt.setInt(1, empid);
                stmt.executeUpdate();
            }
    
            // Finally, delete from employees
            try (PreparedStatement stmt = conn.prepareStatement(deleteEmployee)) {
                stmt.setInt(1, empid);
                stmt.executeUpdate();
            }
    
            conn.commit(); // Commit transaction
            return true;
        } catch (SQLException e) {
            System.out.println("Error deleting employee: " + e.getMessage());
            throw e;
        }
    }

    public static boolean createEmployee(int empid, String fname, String lname, String email ,double salary, String SSN, String DOB, String HireDate, String pay_date,
                                     String jobTitle, String department, String gender, String race,
                                     String street, int city_ID, int state_ID, String zip, String phone_number) throws SQLException {
        Connection conn = null;
        try {
            conn = getConnection();
            conn.setAutoCommit(false); // Start transaction

            // Calculate default earnings (2% of salary)
            double earnings = salary * 0.02;

            // Calculate payroll percentages
            double fed_tax = earnings * 0.31;
            double fed_med = earnings * 0.014;
            double fed_SS = earnings * 0.062;
            double state_tax = earnings * 0.12;
            double retire_401k = earnings * 0.005;
            double health_care = earnings * 0.03;

            // Insert into employees table
            String employeeQuery = "INSERT INTO employees (empid, fname, lname, email, salary, SSN, hireDate) VALUES (?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(employeeQuery)) {
                stmt.setInt(1, empid);
                stmt.setString(2, fname);
                stmt.setString(3, lname);
                stmt.setString(4, email);
                stmt.setDouble(5, salary);
                stmt.setString(6, SSN);
                stmt.setString(7, HireDate);
                stmt.executeUpdate();
            }

            // Insert into employee_job_titles table
            String jobTitleQuery = "INSERT INTO employee_job_titles (empid, job_title_id) " +
                                   "SELECT ?, job_title_id FROM job_titles WHERE job_title = ?";
            try (PreparedStatement stmt = conn.prepareStatement(jobTitleQuery)) {
                stmt.setInt(1, empid);
                stmt.setString(2, jobTitle);
                stmt.executeUpdate();
            }

            // Insert into employee_division table
            String divisionQuery = "INSERT INTO employee_division (empid, div_id) " +
                                   "SELECT ?, ID FROM division WHERE Name = ?";
            try (PreparedStatement stmt = conn.prepareStatement(divisionQuery)) {
                stmt.setInt(1, empid);
                stmt.setString(2, department);
                stmt.executeUpdate();
            }

            // Insert into address table
            String addressQuery = "INSERT INTO address (empid, gender, identified_race, street, city_ID, state_ID, zip, phone_number, DOB) " +
                                  "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(addressQuery)) {
                stmt.setInt(1, empid);
                stmt.setString(2, gender);
                stmt.setString(3, race);
                stmt.setString(4, street);
                stmt.setInt(5, city_ID);
                stmt.setInt(6, state_ID);
                stmt.setString(7, zip);
                stmt.setString(8, phone_number);
                stmt.setString(9, DOB);
                stmt.executeUpdate();
            }

            // Insert into payroll table
            String payrollQuery = "INSERT INTO payroll (empid, earnings, fed_tax, fed_med, fed_SS, state_tax, retire_401k, health_care, pay_date) " +
                                  "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(payrollQuery)) {
                stmt.setInt(1, empid);
                stmt.setDouble(2, earnings);
                stmt.setDouble(3, fed_tax);
                stmt.setDouble(4, fed_med);
                stmt.setDouble(5, fed_SS);
                stmt.setDouble(6, state_tax);
                stmt.setDouble(7, retire_401k);
                stmt.setDouble(8, health_care);
                stmt.setString(9, pay_date);
                stmt.executeUpdate();
            }


            conn.commit(); // Commit transaction
            return true;
        } catch (SQLException e) {
            if (conn != null) {
                conn.rollback(); // Rollback transaction on error
            }
            throw e;
        } finally {
            if (conn != null) {
                conn.setAutoCommit(true); // Restore default behavior
                conn.close();
            }
        }
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
    List<String> allowed = List.of("Name", "DOB", "SSN", "empid");
    if (!allowed.contains(column)) {
        throw new IllegalArgumentException("Invalid search column: " + column);
    }

    String condition;
    if (column.equals("empid")) {
        condition = "e.empid = ?";
    } else if (column.equals("Name")) {
        condition = "(LOWER(e.Fname) LIKE LOWER(?) OR LOWER(e.Lname) LIKE LOWER(?))";
    } else if (column.equals("DOB")) {
        condition = "a.DOB LIKE ?";
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
        } else if (column.equals("Name")) {
            stmt.setString(1, "%" + value + "%"); // Fuzzy match for first name
            stmt.setString(2, "%" + value + "%"); // Fuzzy match for last name
        } else if (column.equals("DOB")) {
            stmt.setString(1, "%" + value + "%");
        } else {
            stmt.setString(1, "%" + value + "%"); // Fuzzy match for other columns
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

public static boolean updateEmployeeData(int empid, String field, String value) throws SQLException {
    try (Connection conn = getConnection()) {
        conn.setAutoCommit(false);

        try {
            PreparedStatement stmt;

            switch (field) {
                case "Fname":
                case "Lname":
                case "email":
                case "DOB":
                case "SSN":
                    String table = field.equals("DOB") ? "address" : "employees";
                    String query = "UPDATE " + table + " SET " + field + " = ? WHERE empid = ?";
                    stmt = conn.prepareStatement(query);
                    stmt.setString(1, value);
                    stmt.setInt(2, empid);
                    stmt.executeUpdate();
                    break;

                case "Job Title":
                    // Get job_title_id from job_titles
                    stmt = conn.prepareStatement("SELECT job_title_id FROM job_titles WHERE job_title = ?");
                    stmt.setString(1, value);
                    ResultSet rsJob = stmt.executeQuery();
                    if (!rsJob.next()) throw new SQLException("Invalid job title: " + value);
                    int jobTitleId = rsJob.getInt("job_title_id");

                    // Update or insert into employee_job_titles
                    stmt = conn.prepareStatement(
                        "INSERT INTO employee_job_titles (empid, job_title_id) VALUES (?, ?) " +
                        "ON DUPLICATE KEY UPDATE job_title_id = VALUES(job_title_id)"
                    );
                    stmt.setInt(1, empid);
                    stmt.setInt(2, jobTitleId);
                    stmt.executeUpdate();
                    break;

                case "Department":
                    // Get div_id from division
                    stmt = conn.prepareStatement("SELECT ID FROM division WHERE Name = ?");
                    stmt.setString(1, value);
                    ResultSet rsDept = stmt.executeQuery();
                    if (!rsDept.next()) throw new SQLException("Invalid department name: " + value);
                    int divId = rsDept.getInt("ID");

                    // Update or insert into employee_division
                    stmt = conn.prepareStatement(
                        "INSERT INTO employee_division (empid, div_id) VALUES (?, ?) " +
                        "ON DUPLICATE KEY UPDATE div_id = VALUES(div_id)"
                    );
                    stmt.setInt(1, empid);
                    stmt.setInt(2, divId);
                    stmt.executeUpdate();
                    break;

                default:
                    throw new IllegalArgumentException("Unsupported field: " + field);
            }

            conn.commit();
            return true;

        } catch (SQLException e) {
            conn.rollback();
            throw e;
        }
    }
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

/**
 * Searches for employees within a specific salary range
 * @param minSalary The minimum salary in the range
 * @param maxSalary The maximum salary in the range
 * @return List of employees whose salary falls within the specified range
 * @throws SQLException If there is an error executing the database query
 */
public static List<Employee> searchEmployeesBySalaryRange(double minSalary, double maxSalary) throws SQLException {
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
        WHERE e.Salary BETWEEN ? AND ?
        GROUP BY e.empid
    """;

    List<Employee> employees = new ArrayList<>();
    try (Connection conn = getConnection();
        PreparedStatement stmt = conn.prepareStatement(query)) {
        stmt.setDouble(1, minSalary);
        stmt.setDouble(2, maxSalary);
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

/**
 * Updates salaries for all employees within a specified range by a given percentage
 * @param minSalary The minimum salary in the range
 * @param maxSalary The maximum salary in the range
 * @param percentage The percentage increase to apply (e.g., 5 for 5% increase)
 * @return true if the update was successful, false otherwise
 * @throws SQLException If there is an error executing the database update
 */
public static boolean updateSalaryRange(double minSalary, double maxSalary, double percentage) throws SQLException {
    String selectQuery = "SELECT empid, Salary FROM employees WHERE Salary BETWEEN ? AND ?";
    String updateSalary = "UPDATE employees SET Salary = ? WHERE empid = ?";
    String updatePayroll = """
        UPDATE payroll SET earnings = ?, fed_tax = ?, fed_med = ?, fed_SS = ?, 
        state_tax = ?, retire_401k = ?, health_care = ? WHERE empid = ?
    """;

    try (Connection conn = getConnection()) {
        conn.setAutoCommit(false);

        try (PreparedStatement selectStmt = conn.prepareStatement(selectQuery)) {
            selectStmt.setDouble(1, minSalary);
            selectStmt.setDouble(2, maxSalary);
            try (ResultSet rs = selectStmt.executeQuery()) {
                while (rs.next()) {
                    int empid = rs.getInt("empid");
                    double currentSalary = rs.getDouble("Salary");
                    double newSalary = round(currentSalary * (1 + percentage / 100.0));

                    double earnings = round(newSalary * 0.02);
                    double fed_tax = round(earnings * 0.31);
                    double fed_med = round(earnings * 0.014);
                    double fed_SS = round(earnings * 0.062);
                    double state_tax = round(earnings * 0.12);
                    double retire_401k = round(earnings * 0.005);
                    double health_care = round(earnings * 0.03);

                    try (PreparedStatement updateStmt = conn.prepareStatement(updateSalary)) {
                        updateStmt.setDouble(1, newSalary);
                        updateStmt.setInt(2, empid);
                        updateStmt.executeUpdate();
                    }

                    try (PreparedStatement updateStmt = conn.prepareStatement(updatePayroll)) {
                        updateStmt.setDouble(1, earnings);
                        updateStmt.setDouble(2, fed_tax);
                        updateStmt.setDouble(3, fed_med);
                        updateStmt.setDouble(4, fed_SS);
                        updateStmt.setDouble(5, state_tax);
                        updateStmt.setDouble(6, retire_401k);
                        updateStmt.setDouble(7, health_care);
                        updateStmt.setInt(8, empid);
                        updateStmt.executeUpdate();
                    }
                }
            }
        }

        conn.commit();
        return true;
    } catch (SQLException e) {
        throw e;
    }
}

private static double round(double value) {
    return Math.round(value * 100.0) / 100.0;
}



/**
 * Retrieves a list of all unique department names from the database
 * @return List of department names
 * @throws SQLException If there is an error executing the database query
 */
public static List<String> getAllDepartments() throws SQLException {
    String query = "SELECT DISTINCT Name FROM division ORDER BY Name";
    List<String> departments = new ArrayList<>();
    try (Connection conn = getConnection();
        PreparedStatement stmt = conn.prepareStatement(query);
        ResultSet rs = stmt.executeQuery()) {
        while (rs.next()) {
            departments.add(rs.getString("Name"));
        }
    }
    return departments;
}

/**
 * Retrieves a list of all unique job titles from the database
 * @return List of job titles
 * @throws SQLException If there is an error executing the database query
 */
public static List<String> getAllJobTitles() throws SQLException {
    String query = "SELECT DISTINCT job_title FROM job_titles ORDER BY job_title";
    List<String> jobTitles = new ArrayList<>();
    try (Connection conn = getConnection();
        PreparedStatement stmt = conn.prepareStatement(query);
        ResultSet rs = stmt.executeQuery()) {
        while (rs.next()) {
            jobTitles.add(rs.getString("job_title"));
        }
    }
    return jobTitles;
}

/**
 * Retrieves all employees in a specific department
 * @param department The department name to filter by
 * @return List of employees in the specified department
 * @throws SQLException If there is an error executing the database query
 */
public static List<Employee> getEmployeesByDepartment(String department) throws SQLException {
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
        WHERE d.Name = ?
        GROUP BY e.empid
    """;

    List<Employee> employees = new ArrayList<>();
    try (Connection conn = getConnection();
        PreparedStatement stmt = conn.prepareStatement(query)) {
        stmt.setString(1, department);
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

/**
 * Retrieves all employees with a specific job title
 * @param jobTitle The job title to filter by
 * @return List of employees with the specified job title
 * @throws SQLException If there is an error executing the database query
 */
public static List<Employee> getEmployeesByJobTitle(String jobTitle) throws SQLException {
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
        WHERE jt.job_title = ?
        GROUP BY e.empid
    """;

    List<Employee> employees = new ArrayList<>();
    try (Connection conn = getConnection();
        PreparedStatement stmt = conn.prepareStatement(query)) {
        stmt.setString(1, jobTitle);
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
}