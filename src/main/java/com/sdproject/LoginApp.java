package com.sdproject;

import java.sql.SQLException;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.util.Collections;

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
                boolean isValid = DatabaseConnection.validateUser(username, password);
                if (isValid) {
                    int empid = DatabaseConnection.getEmpId(username);
                    Employee employee = DatabaseConnection.getEmployeeDetails(empid);
                    if (employee != null) {
                        showEmployeeInfoWindow(employee);
                        messageLabel.setText("Login successful!");
                    } else {
                        messageLabel.setText("Employee not found.");
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

        // Create UI elements to display employee information
        Label fnameLabel = new Label("First Name: " + employee.getFname());
        Label lnameLabel = new Label("Last Name: " + employee.getLname());
        Label emailLabel = new Label("Email: " + employee.getEmail());
        Label salaryLabel = new Label("Salary: " + employee.getSalary());
        Label divisionLabel = new Label("Division: " + employee.getDivisionName());
        Label jobTitleLabel = new Label("Job Title: " + employee.getJobTitle());
        Button closeButton = new Button("Close");

        // Set up the layout
        GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(10));
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.add(fnameLabel, 0, 0);
        gridPane.add(lnameLabel, 0, 1);
        gridPane.add(emailLabel, 0, 2);
        gridPane.add(salaryLabel, 0, 3);
        gridPane.add(divisionLabel, 0, 4);
        gridPane.add(jobTitleLabel, 0, 5);
        gridPane.add(closeButton, 0, 6);

        // Add a button to view employee list
        Button employeeListButton = new Button("View Employee List");
        gridPane.add(employeeListButton, 1, 4);

        employeeListButton.setOnAction(event -> showEmployeeListWindow());

        // Handle close button click
        closeButton.setOnAction(event -> employeeInfoStage.close());

        // Set up the stage
        Scene scene = new Scene(gridPane, 400, 300);
        employeeInfoStage.setScene(scene);
        employeeInfoStage.show();
    }

    private void showEmployeeListWindow() {
    // Create a new stage (window)
    Stage employeeListStage = new Stage();
    employeeListStage.setTitle("Employee List");

    // Create a TableView
    TableView<Employee> tableView = new TableView<>();

    // Define columns
    TableColumn<Employee, Integer> idColumn = new TableColumn<>("ID");
    idColumn.setCellValueFactory(new PropertyValueFactory<>("empid"));

    TableColumn<Employee, String> fnameColumn = new TableColumn<>("First Name");
    fnameColumn.setCellValueFactory(new PropertyValueFactory<>("fname"));

    TableColumn<Employee, String> jobTitleColumn = new TableColumn<>("Job Title");
    jobTitleColumn.setCellValueFactory(new PropertyValueFactory<>("jobTitle"));

    TableColumn<Employee, Double> salaryColumn = new TableColumn<>("Salary");
    salaryColumn.setCellValueFactory(new PropertyValueFactory<>("salary"));

    TableColumn<Employee, Double> earningsColumn = new TableColumn<>("Earnings");
    earningsColumn.setCellValueFactory(new PropertyValueFactory<>("earnings"));

    TableColumn<Employee, Double> healthcareColumn = new TableColumn<>("Healthcare");
    healthcareColumn.setCellValueFactory(new PropertyValueFactory<>("healthcare"));

    // Add columns to the TableView
    Collections.addAll(tableView.getColumns(), idColumn, fnameColumn, jobTitleColumn, salaryColumn, earningsColumn, healthcareColumn);

    // Fetch employee data and populate the TableView
    try {
        tableView.getItems().addAll(DatabaseConnection.getAllEmployees());
    } catch (SQLException e) {
        System.out.println("Error fetching employee list: " + e.getMessage());
        e.printStackTrace();
    }

    // Set up the layout
    VBox vbox = new VBox(tableView);
    Scene scene = new Scene(vbox, 800, 400);

    // Set up the stage
    employeeListStage.setScene(scene);
    employeeListStage.show();
}

    public static void main(String[] args) {
        launch(args);
    }
}