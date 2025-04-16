package com.sdproject;

import java.sql.SQLException;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

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
    Stage employeeListStage = new Stage();
    employeeListStage.setTitle("Employee List");

    // ComboBox for selecting search column
    ComboBox<String> searchFieldBox = new ComboBox<>();
    searchFieldBox.getItems().addAll("Fname", "Lname", "DOB", "SSN", "empid");
    searchFieldBox.setValue("Fname"); // Default selection

    // TextField for search value
    TextField searchField = new TextField();
    searchField.setPromptText("Enter search value");

    // Search button
    Button searchButton = new Button("Search");
    Button resetButton = new Button("Reset");

    // TableView for results
    TableView<Employee> tableView = new TableView<>();

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

    tableView.getColumns().addAll(idColumn, fnameColumn, jobTitleColumn, salaryColumn, earningsColumn, healthcareColumn);

    // Initial population of all employees
    try {
        tableView.getItems().addAll(DatabaseConnection.getAllEmployees());
    } catch (SQLException e) {
        e.printStackTrace();
    }

    // Search logic
    searchButton.setOnAction(event -> {
        String column = searchFieldBox.getValue();
        String value = searchField.getText();
        try {
            tableView.getItems().clear();
            tableView.getItems().addAll(DatabaseConnection.searchEmployees(column, value));
        } catch (Exception e) {
            e.printStackTrace();
        }
    });
    resetButton.setOnAction(event -> {
        searchField.clear(); // clear the text box
        tableView.getItems().clear();
        try {
            tableView.getItems().addAll(DatabaseConnection.getAllEmployees());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    });

    // Layout
    HBox searchBar = new HBox(10, new Label("Search by:"), searchFieldBox, searchField, searchButton, resetButton);
    tableView.setPrefHeight(350); 
    VBox layout = new VBox(10, searchBar, tableView);
    layout.setPadding(new Insets(10));

    Scene scene = new Scene(layout, 900, 450);
    employeeListStage.setScene(scene);
    employeeListStage.show();
}

    public static void main(String[] args) {
        launch(args);
    }
}