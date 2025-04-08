package com.sdproject;

public class Employee {
    private int empid;
    private String fname;
    private String lname;
    private String email;
    private String hireDate;
    private double salary;
    private String ssn;
    private String payDate;
    private double earnings;
    private double federalTax;
    private double federalMedicare;
    private double federalSocialSecurity;
    private double stateTax;
    private double retirement401k;
    private double healthcare;
    private String street;
    private int cityId;
    private int stateId;
    private String zip;
    private String gender;
    private String identifiedRace;
    private String dateOfBirth;
    private String phoneNumber;
    private String divisionName;
    private String jobTitle;

    public Employee(
        // from employees
            int empid, String fname, String lname, String email, String hireDate, double salary, String ssn,  
        // from payroll
            String payDate, double earnings, double federalTax, double federalMedicare, double federalSocialSecurity, double stateTax, double retirement401k, double healthcare,
        // from address
            String street, int cityId, int stateId, String zip, String gender, String identifiedRace, String dateOfBirth, String phoneNumber,
            String divisionName, String jobTitle) {
        
                // from employees
                this.empid = empid;
                this.fname = fname;
                this.lname = lname;
                this.email = email;
                this.hireDate = hireDate;
                this.salary = salary;
                this.ssn = ssn;
                // from payroll
                this.payDate = payDate;
                this.earnings = earnings;
                this.federalTax = federalTax;
                this.federalMedicare = federalMedicare;
                this.federalSocialSecurity = federalSocialSecurity;
                this.stateTax = stateTax;
                this.retirement401k = retirement401k;
                this.healthcare = healthcare;
                // from address
                this.street = street;
                this.cityId = cityId;
                this.stateId = stateId;
                this.zip = zip;
                this.gender = gender;
                this.identifiedRace = identifiedRace;
                this.dateOfBirth = dateOfBirth;
                this.phoneNumber = phoneNumber;
                // from division and job title
                this.divisionName = divisionName;
                this.jobTitle = jobTitle;
    }

    // Getters
    public int getEmpid() {
        return empid;
    }

    public String getFname() {
        return fname;
    }

    public String getLname() {
        return lname;
    }

    public String getEmail() {
        return email;
    }

    public String getHireDate() {
        return hireDate;
    }

    public double getSalary() {
        return salary;
    }

    public String getSsn() {
        return ssn;
    }

    // Getters for payroll fields

    public String getPayDate() {
        return payDate;
    }

    public double getEarnings() {
        return earnings;
    }

    public double getFederalTax() {
        return federalTax;
    }

    public double getFederalMedicare() {
        return federalMedicare;
    }

    public double getFederalSocialSecurity() {
        return federalSocialSecurity;
    }

    public double getStateTax() {
        return stateTax;
    }

    public double getRetirement401k() {
        return retirement401k;
    }

    public double getHealthcare() {
        return healthcare;
    }

    // Getters for address fields

    public String getStreet() {
        return street;
    }

    public int getCityId() {
        return cityId;
    }

    public int getStateId() {
        return stateId;
    }

    public String getZip() {
        return zip;
    }

    public String getGender() {
        return gender;
    }

    public String getIdentifiedRace() {
        return identifiedRace;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    // Getters for division and job title fields

    public String getDivisionName() {
        return divisionName;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    // Setters
}