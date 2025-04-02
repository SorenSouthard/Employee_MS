package com.sdproject;

public class Employee {
    private String fname;
    private String lname;
    private String email;
    private double salary;
    private String divisionName;
    private String jobTitle;

    public Employee(String fname, String lname, String email, double salary, String divisionName, String jobTitle) {
        this.fname = fname;
        this.lname = lname;
        this.email = email;
        this.salary = salary;
        this.divisionName = divisionName;
        this.jobTitle = jobTitle;
    }

    // Getters
    public String getFname() {
        return fname;
    }

    public String getLname() {
        return lname;
    }

    public String getEmail() {
        return email;
    }

    public double getSalary() {
        return salary;
    }

    public String getDivisionName() {
        return divisionName;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    @Override
    public String toString() {
        return "Employee Details:\n" +
               "First Name: " + fname + "\n" +
               "Last Name: " + lname + "\n" +
               "Email: " + email + "\n" +
               "Salary: " + salary + "\n" +
               "Division: " + divisionName + "\n" +
               "Job Title: " + jobTitle;
    }
}