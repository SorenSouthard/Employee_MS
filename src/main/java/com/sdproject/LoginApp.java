package com.sdproject;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.geometry.Pos;
import javafx.stage.Stage;
import javafx.beans.property.SimpleStringProperty;

public class LoginApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        // Create UI elements
        Label usernameLabel = new Label("Username:");
        TextField usernameField = new TextField();
        Label passwordLabel = new Label("Password:");
        PasswordField passwordField = new PasswordField();
        Button loginButton = new Button("Login");
        Label messageLabel = new Label();

        // Set up the layout
        GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(10));
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.add(usernameLabel, 0, 0);
        gridPane.add(usernameField, 1, 0);
        gridPane.add(passwordLabel, 0, 1);
        gridPane.add(passwordField, 1, 1);
        gridPane.add(loginButton, 1, 2);
        gridPane.add(messageLabel, 1, 3);

        // Handle login button click
        loginButton.setOnAction(event -> {
            String username = usernameField.getText();
            String password = passwordField.getText();
        
            try {
                String role = DatabaseConnection.validateUser(username, password);
                if (role != null) {
                    if (role.equals("ADMIN")) {
                        showAdminWindow(); // Redirect to admin window
                        messageLabel.setText("Login successful! Welcome, Admin.");
                    } else if (role.equals("EMPLOYEE")) {
                        int empid = DatabaseConnection.getEmpId(username);
                        Employee employee = DatabaseConnection.getEmployeeDetails(empid);
                        if (employee != null) {
                            showEmployeeInfoWindow(employee); // Redirect to employee info window
                            messageLabel.setText("Login successful!");
                        } else {
                            messageLabel.setText("Employee not found.");
                        }
                    } else {
                        messageLabel.setText("Unknown role. Access denied.");
                    }
                } else {
                    messageLabel.setText("Invalid username or password.");
                }
            } catch (Exception e) {
                messageLabel.setText("Error: " + e.getMessage());
            }
        });

        // Set up the stage
        Scene scene = new Scene(gridPane, 300, 200);
        primaryStage.setTitle("Login");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void showEmployeeInfoWindow(Employee employee) {
        // Create a new stage (window)
        Stage employeeInfoStage = new Stage();
        employeeInfoStage.setTitle("Employee Information");
    
        // Employee Info Section
        Label employeeHeader = new Label("Employee Info");
        employeeHeader.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        Separator employeeSeparator = new Separator();
    
        Label empIdLabel = new Label("Employee ID: " + employee.getEmpid());
        Label fnameLabel = new Label("First Name: " + employee.getFname());
        Label lnameLabel = new Label("Last Name: " + employee.getLname());
        Label emailLabel = new Label("Email: " + employee.getEmail());
        Label dobLabel = new Label("Date of Birth: " + employee.getDateOfBirth());
        Label ssnLabel = new Label("SSN: " + employee.getSsn());
        Label hireDateLabel = new Label("Hire Date: " + employee.getHireDate());
        Label salaryLabel = new Label("Salary: $" + employee.getSalary());
    
        VBox employeeInfo = new VBox(5, empIdLabel, fnameLabel, lnameLabel, emailLabel, dobLabel, ssnLabel, hireDateLabel, salaryLabel);
    
        // Payroll Info Section
        Label payrollHeader = new Label("Payroll Info");
        payrollHeader.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        Separator payrollSeparator = new Separator();
    
        Label jobTitleLabel = new Label("Job Title: " + employee.getJobTitle());
        Label divisionLabel = new Label("Division: " + employee.getDivisionName());
        Label payDateLabel = new Label("Pay Date: " + employee.getPayDate());
        Label earningsLabel = new Label("Earnings: $" + employee.getEarnings());
        Label fedTaxLabel = new Label("Federal Tax: $" + employee.getFederalTax());
        Label fedMedLabel = new Label("Medicare: $" + employee.getFederalMedicare());
        Label fedSSLabel = new Label("Social Security: $" + employee.getFederalSocialSecurity());
        Label stateTaxLabel = new Label("State Tax: $" + employee.getStateTax());
        Label retire401kLabel = new Label("401(k): $" + employee.getRetirement401k());
        Label healthCareLabel = new Label("Health Care: $" + employee.getHealthcare());
    
        VBox payrollInfo = new VBox(5, jobTitleLabel, divisionLabel, payDateLabel, earningsLabel, fedTaxLabel, fedMedLabel, fedSSLabel, stateTaxLabel, retire401kLabel, healthCareLabel);
    
        // Address Info Section
        Label addressHeader = new Label("Address Info");
        addressHeader.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        Separator addressSeparator = new Separator();
    
        Label streetLabel = new Label("Street: " + employee.getStreet());
        Label cityIdLabel = new Label("City ID: " + employee.getCityId());
        Label stateIdLabel = new Label("State ID: " + employee.getStateId());
        Label zipLabel = new Label("ZIP Code: " + employee.getZip());
        Label phoneNumberLabel = new Label("Phone Number: " + employee.getPhoneNumber());
        Label genderLabel = new Label("Gender: " + employee.getGender());
        Label raceLabel = new Label("Identified Race: " + employee.getIdentifiedRace());
    
        VBox addressInfo = new VBox(5, streetLabel, cityIdLabel, stateIdLabel, zipLabel, phoneNumberLabel, genderLabel, raceLabel);
    
        // Close Button
        Button closeButton = new Button("Close");
        closeButton.setOnAction(event -> employeeInfoStage.close());
    
        // Combine all sections
        VBox layout = new VBox(10,
            employeeHeader, employeeSeparator, employeeInfo,
            payrollHeader, payrollSeparator, payrollInfo,
            addressHeader, addressSeparator, addressInfo,
            closeButton
        );
        layout.setPadding(new Insets(10));

        // Wrap the layout in a ScrollPane
        ScrollPane scrollPane = new ScrollPane(layout);
        scrollPane.setFitToWidth(true); // Ensure the content fits the width of the ScrollPane
    
        // Set up the stage
        Scene scene = new Scene(scrollPane, 600, 600);
        employeeInfoStage.setScene(scene);
        employeeInfoStage.show();
    }

    /**
     * Shows the main employee management window with three distinct tabs:
     * 1. Employees tab - for viewing and searching employee data
     * 2. Admin tab - for updating employee data and batch salary updates
     * 3. Reports tab - for viewing employee reports by department and job title
     */
    private void showAdminWindow() {
        Stage employeeListStage = new Stage();
        employeeListStage.setTitle("Employee Management System");

        // Create TabPane to organize different sections of the application
        TabPane tabPane = new TabPane();

        // Admin Tab - for updating employee data and batch salary updates
        Tab adminTab = new Tab("Read/Update Employees");
        adminTab.setClosable(false);
        VBox adminContent = createAdminTab();
        adminTab.setContent(adminContent);

        // Create Employee Tab - for adding new employees
        Tab createEmployeeTab = new Tab("Create Employee");
        createEmployeeTab.setClosable(false);
        VBox createEmployeeContent = createCreateEmployeeTab();
        createEmployeeTab.setContent(createEmployeeContent);

        // Reports Tab - for viewing employee reports
        Tab reportsTab = new Tab("Reports");
        reportsTab.setClosable(false);
        VBox reportsContent = createReportsTab();
        reportsTab.setContent(reportsContent);

        tabPane.getTabs().addAll(adminTab, createEmployeeTab, reportsTab);

        Scene scene = new Scene(tabPane, 1000, 600);
        employeeListStage.setScene(scene);
        employeeListStage.show();
    }

    /**
     * Creates the Admin tab content with update functionality
     * Includes individual employee updates and batch salary updates
     */
    private VBox createAdminTab() {
        // Search functionality
        Label searchLabel = new Label("Search By:");
        searchLabel.setPrefWidth(100);
        
        ComboBox<String> searchFieldBox = new ComboBox<>();
        searchFieldBox.getItems().addAll("Name", "DOB", "SSN", "empid");
        searchFieldBox.setValue("Name"); // Default selection
        searchFieldBox.setPrefWidth(150);
    
        TextField searchField = new TextField();
        searchField.setPromptText("Enter search value");
    
        // Toggle between regular search and salary range search
        searchFieldBox.setOnAction(e -> {
            boolean isSalarySearch = searchFieldBox.getValue().equals("Salary");
            searchField.setVisible(!isSalarySearch);
        });
    
        // Search and reset buttons
        Button searchButton = new Button("Search");
        Button resetButton = new Button("Reset");
        searchButton.setPrefWidth(120);
        resetButton.setPrefWidth(120);

        // Search bar layout
        HBox searchElements = new HBox(10, searchLabel, searchFieldBox, searchField, searchButton, resetButton);
        searchElements.setAlignment(Pos.CENTER_LEFT);

        VBox searchContainer = new VBox(10, searchLabel, searchElements);
        searchContainer.setAlignment(Pos.CENTER_LEFT);

        // Update functionality
        Label updateLabel = new Label("Update Field:");
        updateLabel.setPrefWidth(100);
        updateLabel.setAlignment(Pos.CENTER_LEFT);

        ComboBox<String> updateFieldBox = new ComboBox<>();
        updateFieldBox.getItems().addAll("Fname", "Lname", "email");
        updateFieldBox.setValue("Fname");
        updateFieldBox.setPrefWidth(150);

        TextField updateValueField = new TextField();
        updateValueField.setPromptText("Enter new value");

        Button updateButton = new Button("Update");
        Button deleteButton = new Button("Delete");
        deleteButton.setPrefWidth(120);
        updateButton.setPrefWidth(120);

        HBox updateElements = new HBox(10, updateFieldBox, updateValueField, updateButton, deleteButton);
        updateElements.setAlignment(Pos.CENTER_LEFT);

        VBox updateContainer = new VBox(10, updateLabel, updateElements);
        updateContainer.setAlignment(Pos.CENTER_LEFT);

        // Combine searchElements and updateElements into a single HBox
        HBox combinedBar = new HBox(20, searchContainer, updateContainer);
        combinedBar.setAlignment(Pos.CENTER_LEFT);
        combinedBar.setPadding(new Insets(10));

        // Add a separator between the combined bar and the salary update section
        Separator separator = new Separator();
        separator.setPadding(new Insets(10, 0, 0, 0));

        // Batch salary update fields
        TextField salaryMinRangeField = new TextField();
        TextField salaryMaxRangeField = new TextField();
        TextField percentageField = new TextField();
        salaryMinRangeField.setPromptText("Min Salary");
        salaryMaxRangeField.setPromptText("Max Salary");

        percentageField.setPromptText("Increase %");

        HBox salaryRangeUpdateBox = new HBox(10,
            new Label("Salary Range:"),
            salaryMinRangeField,
            salaryMaxRangeField,
            new Label("Increase:"),
            percentageField,
            new Label("%")
        );

        Button salarySearchButton = new Button("Search by Salary");
        salarySearchButton.setPrefWidth(150);
        Button updateSalaryRangeButton = new Button("Update Salary Range");
        updateSalaryRangeButton.setPrefWidth(150);
        Label updateStatusLabel = new Label();

        // Add the salary update section to a VBox
        VBox updateSalarySection = new VBox(10,
            new Label("Batch Salary Update"),
            salaryRangeUpdateBox,
            salarySearchButton,
            updateSalaryRangeButton,
            updateStatusLabel
        );
        updateSalarySection.setPadding(new Insets(10, 10, 0, 10));

        // Table to display employee data
        TableView<Employee> tableView = new TableView<>();
        setupEmployeeTableColumns(tableView);

        // Initial population of all employees
        try {
            tableView.getItems().addAll(DatabaseConnection.getAllEmployees());
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Search button action - handles both regular and salary range searches
        searchButton.setOnAction(event -> {
            try {
                tableView.getItems().clear();
                String column = searchFieldBox.getValue();
                String value = searchField.getText().toLowerCase();
                tableView.getItems().clear();
                tableView.getItems().addAll(DatabaseConnection.searchEmployees(column, value));


            } catch (Exception e) {
                showAlert("Error", e.getMessage());
            }
        });

        salarySearchButton.setOnAction(event -> {
            try {
                double minSalary = Double.parseDouble(salaryMinRangeField.getText());
                double maxSalary = Double.parseDouble(salaryMaxRangeField.getText());
                
                tableView.getItems().clear();
                tableView.getItems().addAll(DatabaseConnection.searchEmployeesBySalaryRange(minSalary, maxSalary));
            } catch (Exception e) {
                showAlert("Error", e.getMessage());
            }
        });

        // Reset button action - clears search fields and shows all employees
        resetButton.setOnAction(event -> {
            searchField.clear();
            salaryMinRangeField.clear();
            salaryMaxRangeField.clear();
            tableView.getItems().clear();
            try {
                tableView.getItems().addAll(DatabaseConnection.getAllEmployees());
            } catch (SQLException e) {
                showAlert("Error", e.getMessage());
            }
        });

        // Individual update button action
        updateButton.setOnAction(event -> {
            Employee selectedEmployee = tableView.getSelectionModel().getSelectedItem();
            if (selectedEmployee == null) {
                updateStatusLabel.setText("Please select an employee first");
                return;
            }

            String field = updateFieldBox.getValue();
            String value = updateValueField.getText();

            if (value.isEmpty()) {
                updateStatusLabel.setText("Please enter a value");
                return;
            }

            try {
                boolean success = DatabaseConnection.updateEmployeeData(selectedEmployee.getEmpid(), field, value);
                if (success) {
                    updateStatusLabel.setText("Update successful!");
                    refreshTable(tableView);
                } else {
                    updateStatusLabel.setText("Update failed");
                }
            } catch (SQLException e) {
                updateStatusLabel.setText("Error: " + e.getMessage());
            }
        });

        // Delete button action
        deleteButton.setOnAction(event -> {
            Employee selectedEmployee = tableView.getSelectionModel().getSelectedItem();
            if (selectedEmployee == null) {
                updateStatusLabel.setText("Please select an employee to delete.");
                return;
            }

            // Optional: Show a confirmation dialog
            Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmationAlert.setTitle("Delete Employee");
            confirmationAlert.setHeaderText("Are you sure you want to delete this employee?");
            confirmationAlert.setContentText("This action cannot be undone.");
            if (confirmationAlert.showAndWait().orElse(null) != ButtonType.OK) {
                return;
            }

            try {
                boolean success = DatabaseConnection.deleteEmployee(selectedEmployee.getEmpid());
                if (success) {
                    updateStatusLabel.setText("Employee deleted successfully!");
                    refreshTable(tableView);
                } else {
                    updateStatusLabel.setText("Failed to delete employee.");
                }
            } catch (SQLException e) {
                updateStatusLabel.setText("Error: " + e.getMessage());
            }
        });

        // Batch salary update button action
        updateSalaryRangeButton.setOnAction(event -> {
            try {
                double minSalary = Double.parseDouble(salaryMinRangeField.getText());
                double maxSalary = Double.parseDouble(salaryMaxRangeField.getText());
                double percentage = Double.parseDouble(percentageField.getText());
                
                boolean success = DatabaseConnection.updateSalaryRange(minSalary, maxSalary, percentage);
                if (success) {
                    updateStatusLabel.setText("Salary range update successful!");
                    refreshTable(tableView);
                } else {
                    updateStatusLabel.setText("Salary range update failed");
                }
            } catch (Exception e) {
                updateStatusLabel.setText("Error: " + e.getMessage());
            }
        });

        VBox layout = new VBox(10, combinedBar, separator, updateSalarySection, tableView);
        layout.setPadding(new Insets(10));
        return layout;
    }

    private VBox createCreateEmployeeTab() {
        // Input fields
        TextField fnameField = new TextField();
        fnameField.setPromptText("First Name");
    
        TextField lnameField = new TextField();
        lnameField.setPromptText("Last Name");

        TextField emailField = new TextField(); // Default email
        emailField.setPromptText("Email");

        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");

        TextField passwordField = new TextField();
        passwordField.setPromptText("Password");

        ComboBox<String> roleBox = new ComboBox<>();
        roleBox.getItems().addAll("EMPLOYEE", "ADMIN");
        roleBox.setValue("EMPLOYEE"); // Default role
    
        TextField salaryField = new TextField("10000"); // Default salary
        salaryField.setPromptText("Salary");

        TextField ssnField = new TextField("123-45-6789"); // Default SSN
        ssnField.setPromptText("SSN");

        TextField dobField = new TextField("1990-01-01"); // Default DOB
        dobField.setPromptText("Date of Birth (YYYY-MM-DD)");

        TextField payDateField = new TextField("2023-01-01"); // Default pay date
        payDateField.setPromptText("Pay Date (YYYY-MM-DD)");

        TextField hireDateField = new TextField("2023-01-01"); // Default hire date
        hireDateField.setPromptText("Hire Date (YYYY-MM-DD)");
    
        ComboBox<String> jobTitleBox = new ComboBox<>();
        ComboBox<String> departmentBox = new ComboBox<>();
        try {
            jobTitleBox.getItems().addAll(DatabaseConnection.getAllJobTitles());
            departmentBox.getItems().addAll(DatabaseConnection.getAllDepartments());
        } catch (SQLException e) {
            showAlert("Error", "Failed to load job titles or departments: " + e.getMessage());
        }
        jobTitleBox.setPromptText("Select Job Title");
        departmentBox.setPromptText("Select Department");
    
        ComboBox<String> genderBox = new ComboBox<>();
        genderBox.getItems().addAll("Male", "Female", "Non-binary", "Opt-out");
        genderBox.setValue("Opt-out");
    
        ComboBox<String> raceBox = new ComboBox<>();
        raceBox.getItems().addAll("White", "Black or African American", "Asian", "Hispanic or Latino", "Opt-out");
        raceBox.setValue("Opt-out");

        // Input fields for address details
        TextField streetField = new TextField("123 Main St"); // Default street address
        streetField.setPromptText("Street");

        TextField cityIDField = new TextField("1"); // Default city ID
        cityIDField.setPromptText("City ID");

        TextField stateIDField = new TextField("1"); // Default state ID
        stateIDField.setPromptText("State ID");

        TextField zipField = new TextField("12345"); // Default ZIP code
        zipField.setPromptText("ZIP Code");

        TextField phoneNumberField = new TextField("123-456-7890"); // Default phone number
        phoneNumberField.setPromptText("Phone Number");
    
        // Submit button
        Button submitButton = new Button("Create Employee");
        Label statusLabel = new Label();
    
        submitButton.setOnAction(event -> {
            String fname = fnameField.getText();
            String lname = lnameField.getText();
            String email = emailField.getText();
            String salary = salaryField.getText();
            String ssn = ssnField.getText();
            String dob = dobField.getText();
            String hireDate = hireDateField.getText();
            String jobTitle = jobTitleBox.getValue();
            String department = departmentBox.getValue();
            String gender = genderBox.getValue();
            String race = raceBox.getValue();
            String username = usernameField.getText();
            String password = passwordField.getText();
            String role = roleBox.getValue();
            String street = streetField.getText();
            int city_ID = Integer.parseInt(cityIDField.getText());
            int state_ID = Integer.parseInt(stateIDField.getText());
            String zip = zipField.getText();
            String phone_number = phoneNumberField.getText();

            if (hireDate.isEmpty()) {
                hireDate = LocalDate.now().toString(); // Default hire date
            }

            String payDate = LocalDate.now().toString(); // Default pay date
    
            if (email.isEmpty()) {
                email = fname + " " + lname + "@example.com"; // Default email
            }

            if (fname.isEmpty() || lname.isEmpty() || username.isEmpty() || password.isEmpty() || email.isEmpty()) {
                statusLabel.setText("First Name, Last Name, Username, and Password are required.");
                return;
            }
    
            try {
                int newEmpId = DatabaseConnection.getMaxEmpId() + 1; // Generate new empid

                //Type of employee object constructor:
                //int empid, String fname, String lname, String email ,double salary, String SSN, String HireDate,
                //String jobTitle, String department, String gender, String race,
                //String street, int city_ID, int state_ID, String zip, String phone_number
                boolean employeeCreated = DatabaseConnection.createEmployee(
                    newEmpId, fname, lname, email, Double.parseDouble(salary), ssn, dob, hireDate, payDate,
                    jobTitle, department, gender, race, street, city_ID, state_ID, zip, phone_number
                );
                boolean userCreated = DatabaseConnection.createUser(newEmpId, username, password, role);
    
                if (employeeCreated && userCreated) {
                    statusLabel.setText("Employee and user account created successfully!");
                    fnameField.clear();
                    lnameField.clear();
                    emailField.clear();
                    salaryField.setText("10000");
                    ssnField.setText("123-45-6789");
                    dobField.setText("1990-01-01");
                    hireDateField.setText("2023-01-01");
                    jobTitleBox.setValue(null);
                    departmentBox.setValue(null);
                    genderBox.setValue("Opt-out");
                    raceBox.setValue("Opt-out");
                    usernameField.clear();
                    passwordField.clear();
                    roleBox.setValue("Employee");
                    streetField.setText("123 Main St");
                    cityIDField.setText("1");
                    stateIDField.setText("1");
                    zipField.setText("12345");
                    phoneNumberField.setText("123-456-7890");
                } else {
                    statusLabel.setText("Failed to create employee or user account.");
                }
            } catch (Exception e) {
                statusLabel.setText("Error: " + e.getMessage());
            }
        });
    
        // Layout
        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(10);

        // First column
        form.add(new Label("First Name:"), 0, 0);
        form.add(fnameField, 1, 0);
        form.add(new Label("Last Name:"), 0, 1);
        form.add(lnameField, 1, 1);
        form.add(new Label("Username:"), 0, 2);
        form.add(usernameField, 1, 2);
        form.add(new Label("Password:"), 0, 3);
        form.add(passwordField, 1, 3);
        form.add(new Label("Role:"), 0, 4);
        form.add(roleBox, 1, 4);
        form.add(new Label("Street:"), 0, 5);
        form.add(streetField, 1, 5);
        form.add(new Label("City ID:"), 0, 6);
        form.add(cityIDField, 1, 6);
        form.add(new Label("State ID:"), 0, 7);
        form.add(stateIDField, 1, 7);
        form.add(new Label("ZIP Code:"), 0, 8);
        form.add(zipField, 1, 8);
        form.add(new Label("Phone Number:"), 0, 9);
        form.add(phoneNumberField, 1, 9);
        

        // Second column
        form.add(new Label("Email:"), 2, 0);
        form.add(emailField, 3, 0);
        form.add(new Label("Social Security Number:"), 2, 1);
        form.add(ssnField, 3, 1);
        form.add(new Label("Date of Birth:"), 2, 2);
        form.add(dobField, 3, 2);
        form.add(new Label("Hire Date:"), 2, 3);
        form.add(hireDateField, 3, 3);
        form.add(new Label("Pay Date:"), 2, 4);
        form.add(payDateField, 3, 4);
        form.add(new Label("Salary:"), 2, 5);
        form.add(salaryField, 3, 5);

        // Third column
        form.add(new Label("Job Title:"), 4, 0);
        form.add(jobTitleBox, 5, 0);
        form.add(new Label("Department:"), 4, 1);
        form.add(departmentBox, 5, 1);
        form.add(new Label("Gender:"), 4, 2);
        form.add(genderBox, 5, 2);
        form.add(new Label("Identified Race:"), 4, 3);
        form.add(raceBox, 5, 3);

    
        form.add(submitButton, 1, 11);
        form.add(statusLabel, 1, 12);

        VBox layout = new VBox(10, form);
        layout.setPadding(new Insets(10));
        return layout;
    }

    /**
     * Creates the Reports tab content with filtering and summary statistics
     */
    private VBox createReportsTab() {
        // Report type selection
        ComboBox<String> reportTypeBox = new ComboBox<>();
        reportTypeBox.getItems().addAll("All Employees", "By Department", "By Job Title");
        reportTypeBox.setValue("All Employees");

        // Department and Job Title selection dropdowns
        ComboBox<String> departmentBox = new ComboBox<>();
        ComboBox<String> jobTitleBox = new ComboBox<>();
        
        try {
            departmentBox.getItems().addAll(DatabaseConnection.getAllDepartments());
            jobTitleBox.getItems().addAll(DatabaseConnection.getAllJobTitles());
        } catch (SQLException e) {
            e.printStackTrace();
        }

        departmentBox.setVisible(false);
        jobTitleBox.setVisible(false);

        // Table to display employee data
        TableView<Employee> tableView = new TableView<>();
        setupEmployeeTableColumns(tableView);

        // Summary statistics labels
        Label totalEmployeesLabel = new Label();
        Label totalSalaryLabel = new Label();
        Label averageSalaryLabel = new Label();

        VBox summaryBox = new VBox(5,
            totalEmployeesLabel,
            totalSalaryLabel,
            averageSalaryLabel
        );

        // Report type change handler
        reportTypeBox.setOnAction(e -> {
            String selectedType = reportTypeBox.getValue();
            departmentBox.setVisible(selectedType.equals("By Department"));
            jobTitleBox.setVisible(selectedType.equals("By Job Title"));
            updateReport(selectedType, departmentBox.getValue(), jobTitleBox.getValue(), tableView, totalEmployeesLabel, totalSalaryLabel, averageSalaryLabel);
        });

        // Department change handler
        departmentBox.setOnAction(e -> {
            updateReport("By Department", departmentBox.getValue(), null, tableView, totalEmployeesLabel, totalSalaryLabel, averageSalaryLabel);
        });

        // Job title change handler
        jobTitleBox.setOnAction(e -> {
            updateReport("By Job Title", null, jobTitleBox.getValue(), tableView, totalEmployeesLabel, totalSalaryLabel, averageSalaryLabel);
        });

        // Initial report display
        updateReport("All Employees", null, null, tableView, totalEmployeesLabel, totalSalaryLabel, averageSalaryLabel);

        // Layout setup
        HBox controls = new HBox(10,
            new Label("Report Type:"),
            reportTypeBox,
            new Label("Department:"),
            departmentBox,
            new Label("Job Title:"),
            jobTitleBox
        );

        VBox layout = new VBox(10, controls, tableView, summaryBox);
        layout.setPadding(new Insets(10));
        return layout;
    }

    /**
     * Sets up the table columns for employee data display
     */
    private void setupEmployeeTableColumns(TableView<Employee> tableView) {
        TableColumn<Employee, Integer> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("empid"));

        // Name column for first and last name
        TableColumn<Employee, String> nameColumn = new TableColumn<>("Name");
        nameColumn.setCellValueFactory(cellData -> {
            Employee employee = cellData.getValue();
            return new SimpleStringProperty(employee.getFname() + " " + employee.getLname());
        });

        // Email column
        TableColumn<Employee, String> emailColumn = new TableColumn<>("Email");
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));

        // DOB column
        TableColumn<Employee, String> dobColumn = new TableColumn<>("DOB");
        dobColumn.setCellValueFactory(new PropertyValueFactory<>("dob"));

        // SSN column
        TableColumn<Employee, String> ssnColumn = new TableColumn<>("SSN");
        ssnColumn.setCellValueFactory(new PropertyValueFactory<>("ssn"));

        TableColumn<Employee, String> jobTitleColumn = new TableColumn<>("Job Title");
        jobTitleColumn.setCellValueFactory(new PropertyValueFactory<>("jobTitle"));

        TableColumn<Employee, String> departmentColumn = new TableColumn<>("Department");
        departmentColumn.setCellValueFactory(new PropertyValueFactory<>("divisionName"));

        TableColumn<Employee, Double> salaryColumn = new TableColumn<>("Salary");
        salaryColumn.setCellValueFactory(new PropertyValueFactory<>("salary"));

        TableColumn<Employee, Double> earningsColumn = new TableColumn<>("Earnings");
        earningsColumn.setCellValueFactory(new PropertyValueFactory<>("earnings"));

        // Add all columns to the table
        tableView.getColumns().add(idColumn);
        tableView.getColumns().add(nameColumn);
        tableView.getColumns().add(emailColumn);
        tableView.getColumns().add(dobColumn);
        tableView.getColumns().add(ssnColumn);
        tableView.getColumns().add(jobTitleColumn);
        tableView.getColumns().add(departmentColumn);
        tableView.getColumns().add(salaryColumn);
        tableView.getColumns().add(earningsColumn);
    }

    /**
     * Updates the report display based on selected filters
     */
    private void updateReport(String reportType, String department, String jobTitle,
                            TableView<Employee> tableView,
                            Label totalEmployeesLabel,
                            Label totalSalaryLabel,
                            Label averageSalaryLabel) {
        try {
            List<Employee> employees;
            switch (reportType) {
                case "By Department":
                    employees = DatabaseConnection.getEmployeesByDepartment(department);
                    break;
                case "By Job Title":
                    employees = DatabaseConnection.getEmployeesByJobTitle(jobTitle);
                    break;
                default:
                    employees = DatabaseConnection.getAllEmployees();
            }

            tableView.getItems().clear();
            tableView.getItems().addAll(employees);

            // Calculate summary statistics
            int totalEmployees = employees.size();
            double totalSalary = employees.stream()
                .mapToDouble(Employee::getSalary)
                .sum();
            double averageSalary = totalEmployees > 0 ? totalSalary / totalEmployees : 0;

            totalEmployeesLabel.setText(String.format("Total Employees: %d", totalEmployees));
            totalSalaryLabel.setText(String.format("Total Salary: $%.2f", totalSalary));
            averageSalaryLabel.setText(String.format("Average Salary: $%.2f", averageSalary));
        } catch (SQLException e) {
            showAlert("Error", "Failed to update report: " + e.getMessage());
        }
    }

    /**
     * Refreshes the employee table with current data
     */
    private void refreshTable(TableView<Employee> tableView) {
        try {
            tableView.getItems().clear();
            tableView.getItems().addAll(DatabaseConnection.getAllEmployees());
        } catch (SQLException e) {
            showAlert("Error", "Failed to refresh table: " + e.getMessage());
        }
    }

    /**
     * Shows an error alert dialog
     */
    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}