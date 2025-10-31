package com.example.flinflow;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.PieChart;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.Node;
import javafx.scene.layout.VBox;
import javafx.animation.ScaleTransition;
import javafx.animation.FadeTransition;
import javafx.util.Duration;
import javafx.geometry.Side;
import java.sql.SQLException;  
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ResourceBundle;

public class SettingController implements Initializable {
    
    @FXML private PieChart incomeExpenseChart;
    @FXML private ImageView profileImage;
    @FXML private TextField usernameField;
    @FXML private TextField fullnameField;
    @FXML private VBox legendContainer;
    
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Check if user is logged in
        if (!SessionManager.isLoggedIn()) {
            showModernAlert("Error", "Please login first", Alert.AlertType.ERROR);
            try {
                App.setRoot("login.fxml");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }
        
        // Load profile image
        loadProfileImage();
        
        // Load user data from database
        loadUserDataFromDatabase();
        
        // Setup chart REAL DATA from database
        setupRealPieChart();
      
    }
    
    private void loadProfileImage() {
        try {
            Image image = new Image(getClass().getResourceAsStream("profile.png"));
            profileImage.setImage(image);
        } catch (Exception e) {
            System.out.println("Profile image not found");
        }
    }
    
    private void loadUserDataFromDatabase() {
        String username = SessionManager.getCurrentUsername();
        
        if (username == null || username.isEmpty()) {
            showModernAlert("Error", "No user logged in", Alert.AlertType.ERROR);
            return;
        }
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT full_name, username FROM users WHERE username = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                String fullName = rs.getString("full_name");
                String dbUsername = rs.getString("username");
                
                usernameField.setText(dbUsername);
                fullnameField.setText(fullName);
                
                System.out.println(" User data loaded: " + fullName + " (@" + dbUsername + ")");
            } else {
                showModernAlert("Error", "User not found in database", Alert.AlertType.ERROR);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            showModernAlert("Database Error", "Failed to load user data: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
    
    private void setupRealPieChart() {
        try {
            // Get real financial data from database
            FinancialData financialData = getFinancialDataFromDatabase();
            
            double totalIncome = financialData.getTotalIncome();
            double totalExpense = financialData.getTotalExpense();
            double balance = financialData.getBalance();
            double total = totalIncome + totalExpense + Math.abs(balance);
            
            // Create pie chart data without label text (cleaner look)
            PieChart.Data expenseData = new PieChart.Data("Expense", totalExpense);
            PieChart.Data incomeData = new PieChart.Data("Income", totalIncome);
            PieChart.Data balanceData = new PieChart.Data("Balance", Math.abs(balance));
            
            // Set data to chart
            incomeExpenseChart.setData(FXCollections.observableArrayList(
                incomeData, expenseData, balanceData
            ));
            
            // Chart settings for more clean
            incomeExpenseChart.setLabelsVisible(false); 
            incomeExpenseChart.setLegendVisible(true);
            incomeExpenseChart.setLegendSide(Side.RIGHT);
            incomeExpenseChart.setStartAngle(90);
            incomeExpenseChart.setClockwise(true);
            
            // Apply colors
            incomeExpenseChart.applyCss();
            incomeExpenseChart.layout();
            
            // Modern color scheme
            incomeData.getNode().setStyle("-fx-pie-color: #F59E0B;");    
            expenseData.getNode().setStyle("-fx-pie-color: #EC4899;");   
            
            if (balance >= 0) {
                balanceData.getNode().setStyle("-fx-pie-color: #10B981;"); 
            } else {
                balanceData.getNode().setStyle("-fx-pie-color: #EF4444;");
            }
            
            // Enhanced chart styling
            incomeExpenseChart.setStyle(
                "-fx-background-color: transparent; " +
                "-fx-border-color: transparent; " +
                "-fx-font-family: 'Segoe UI', 'Helvetica', 'Arial';"
            );
            
            // Create custom legend if container exists
            if (legendContainer != null) {
                createCustomLegend(financialData, total);
            }
            
            System.out.println("Pie Chart updated with real data:");
            System.out.println("   Income: RM " + totalIncome);
            System.out.println("   Expense: RM " + totalExpense);
            System.out.println("   Balance: RM " + balance);
            
        } catch (Exception e) {
            e.printStackTrace();
            showModernAlert("Chart Error", "Failed to load financial data: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
    
    private void createCustomLegend(FinancialData data, double total) {
        legendContainer.getChildren().clear();
        legendContainer.setSpacing(12);
        
        addLegendItem("Income", data.getTotalIncome(), total, "#F59E0B");
        addLegendItem("Expense", data.getTotalExpense(), total, "#EC4899");
        addLegendItem("Balance", Math.abs(data.getBalance()), total, 
            data.getBalance() >= 0 ? "#10B981" : "#EF4444");
    }
    
    private void addLegendItem(String name, double value, double total, String color) {
        double percentage = (value / total) * 100;
        
        VBox item = new VBox(4);
        Label nameLabel = new Label(name);
        Label valueLabel = new Label(String.format("RM %.2f (%.1f%%)", value, percentage));
        
        nameLabel.setStyle(
            "-fx-font-size: 12px; " +
            "-fx-font-weight: 600; " +
            "-fx-text-fill: #4B5563; " +
            "-fx-font-family: 'Segoe UI', 'Helvetica', 'Arial';"
        );
        
        valueLabel.setStyle(
            "-fx-font-size: 14px; " +
            "-fx-font-weight: bold; " +
            "-fx-text-fill: " + color + "; " +
            "-fx-font-family: 'Segoe UI', 'Helvetica', 'Arial';"
        );
        
        item.getChildren().addAll(nameLabel, valueLabel);
        legendContainer.getChildren().add(item);
    }
    
    private FinancialData getFinancialDataFromDatabase() {
    double totalIncome = 0;
    double totalExpense = 0;
    
    try (Connection conn = DatabaseConnection.getConnection()) {
        String sql = "SELECT type, amount FROM transactions WHERE user_id = ?";
        PreparedStatement stmt = conn.prepareStatement(sql);
        
        int currentUserId = getCurrentUserId();
        stmt.setInt(1, currentUserId);
        
        ResultSet rs = stmt.executeQuery();
        
        while (rs.next()) {
            String type = rs.getString("type");
            double amount = rs.getDouble("amount");
            
            if ("Income".equals(type)) {
                totalIncome += amount;
            } else if ("Expense".equals(type)) {
                totalExpense += amount;
            }
        }
        
    } catch (SQLException e) {  
        e.printStackTrace();
        throw new RuntimeException("Failed to fetch financial data from database: " + e.getMessage());
    }
    
    double balance = totalIncome - totalExpense;
    return new FinancialData(totalIncome, totalExpense, balance);
}
    
  private int getCurrentUserId() {
    String username = SessionManager.getCurrentUsername();
    if (username == null) return 0;
    
    String sql = "SELECT id FROM users WHERE username = ?";
    
    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {
        
        stmt.setString(1, username);
        ResultSet rs = stmt.executeQuery();
        
        if (rs.next()) {
            return rs.getInt("id");
        }
    } catch (SQLException e) {  
        e.printStackTrace();
    }
    return 0;
}
    
    @FXML
    private void handleBackToDashboard() {
        try {
            App.setRoot("dashboard.fxml");
        } catch (IOException e) {
            e.printStackTrace();
            showModernAlert("Error", "Cannot return to dashboard", Alert.AlertType.ERROR);
        }
    }
    
    @FXML
    private void handleLogout() {
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Logout Confirmation");
        confirmAlert.setHeaderText(null);
        confirmAlert.setContentText("Are you sure you want to logout?");
        
        confirmAlert.getDialogPane().setStyle(
            "-fx-background-color: white; " +
            "-fx-font-family: 'Segoe UI', 'Helvetica', 'Arial';"
        );
        
        confirmAlert.showAndWait().ifPresent(response -> {
            if (response == javafx.scene.control.ButtonType.OK) {
                SessionManager.logout();
                try {
                    App.setRoot("login.fxml");
                } catch (IOException e) {
                    e.printStackTrace();
                    showModernAlert("Error", "Cannot navigate to login page", Alert.AlertType.ERROR);
                }
            }
        });
    }
    
    private void showModernAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        
        alert.getDialogPane().setStyle(
            "-fx-background-color: white; " +
            "-fx-font-family: 'Segoe UI', 'Helvetica', 'Arial';"
        );
        
        alert.showAndWait();
    }
    
    // Inner class untuk hold financial data
    private static class FinancialData {
        private final double totalIncome;
        private final double totalExpense;
        private final double balance;
        
        public FinancialData(double totalIncome, double totalExpense, double balance) {
            this.totalIncome = totalIncome;
            this.totalExpense = totalExpense;
            this.balance = balance;
        }
        
        public double getTotalIncome() { return totalIncome; }
        public double getTotalExpense() { return totalExpense; }
        public double getBalance() { return balance; }
    }
}