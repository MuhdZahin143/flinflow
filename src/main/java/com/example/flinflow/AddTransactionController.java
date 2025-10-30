/*package com.example.flinflow;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class AddTransactionController {

    @FXML private RadioButton incomeRadio;
    @FXML private RadioButton expenseRadio;
    @FXML private ComboBox<String> categoryBox;
    @FXML private TextField descriptionField;
    @FXML private TextField amountField;
    @FXML private DatePicker datePicker;

    @FXML
    private void initialize() {
        // Setup categories based on transaction type
        setupCategories();
        
        // Update categories when radio button changes
        incomeRadio.selectedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) setupCategories();
        });
        
        expenseRadio.selectedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) setupCategories();
        });
    }

    private void setupCategories() {
        categoryBox.getItems().clear();
        
        if (incomeRadio.isSelected()) {
            // Income categories
            categoryBox.getItems().addAll(
                "Salary", "Freelance", "Investment", "Bonus", "Gift", "Other Income"
            );
        } else {
            // Expense categories  
            categoryBox.getItems().addAll(
                "Food & Dining", "Transportation", "Bills & Utilities", 
                "Shopping", "Entertainment", "Healthcare", "Other Expense"
            );
        }
    }

    @FXML
    private void handleAddTransaction() {
        String type = incomeRadio.isSelected() ? "Income" : "Expense";
        String category = categoryBox.getValue();
        String description = descriptionField.getText().trim();
        String amountText = amountField.getText().trim();
        String date = (datePicker.getValue() != null) ? datePicker.getValue().toString() : "";

        // Validation
        if (category == null || category.isEmpty()) {
            showAlert("Validation Error", "Please select a category.");
            return;
        }
        
        if (description.isEmpty()) {
            showAlert("Validation Error", "Please enter a description.");
            return;
        }
        
        if (amountText.isEmpty()) {
            showAlert("Validation Error", "Please enter an amount.");
            return;
        }
        
        if (date.isEmpty()) {
            showAlert("Validation Error", "Please select a date.");
            return;
        }

        // Validate amount is numeric
        try {
            double amount = Double.parseDouble(amountText);
            if (amount <= 0) {
                showAlert("Validation Error", "Amount must be greater than 0.");
                return;
            }
        } catch (NumberFormatException e) {
            showAlert("Validation Error", "Please enter a valid amount.");
            return;
        }

        // Success message
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Transaction Added");
        alert.setHeaderText(null);
        alert.setContentText(
            "✅ Transaction Added Successfully!\n\n" +
            "Type: " + type + "\n" +
            "Category: " + category + "\n" + 
            "Description: " + description + "\n" +
            "Amount: RM " + amountText + "\n" +
            "Date: " + date
        );
        alert.showAndWait();

        // Close the popup
        closeWindow();
    }

    @FXML
    private void handleCancel() {
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) amountField.getScene().getWindow();
        stage.close();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}*/

package com.example.flinflow;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AddTransactionController {

    @FXML private RadioButton incomeRadio;
    @FXML private RadioButton expenseRadio;
    @FXML private ComboBox<String> categoryBox;
    @FXML private TextField descriptionField;
    @FXML private TextField amountField;
    @FXML private DatePicker datePicker;

    @FXML
    private void initialize() {
        setupCategories();
        
        incomeRadio.selectedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) setupCategories();
        });
        
        expenseRadio.selectedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) setupCategories();
        });
    }

    private void setupCategories() {
        categoryBox.getItems().clear();
        
        if (incomeRadio.isSelected()) {
            categoryBox.getItems().addAll(
                "Salary", "Freelance", "Investment", "Bonus", "Gift", "Other Income"
            );
        } else {
            categoryBox.getItems().addAll(
                "Food & Dining", "Transportation", "Bills & Utilities", 
                "Shopping", "Entertainment", "Healthcare", "Other Expense"
            );
        }
    }

    @FXML
    private void handleAddTransaction() {
        String type = incomeRadio.isSelected() ? "Income" : "Expense";
        String category = categoryBox.getValue();
        String description = descriptionField.getText().trim();
        String amountText = amountField.getText().trim();
        java.sql.Date date = (datePicker.getValue() != null) ? 
                            java.sql.Date.valueOf(datePicker.getValue()) : null;

        // Validation
        if (category == null || category.isEmpty()) {
            showAlert("Validation Error", "Please select a category.");
            return;
        }
        
        if (description.isEmpty()) {
            showAlert("Validation Error", "Please enter a description.");
            return;
        }
        
        if (amountText.isEmpty()) {
            showAlert("Validation Error", "Please enter an amount.");
            return;
        }
        
        if (date == null) {
            showAlert("Validation Error", "Please select a date.");
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(amountText);
            if (amount <= 0) {
                showAlert("Validation Error", "Amount must be greater than 0.");
                return;
            }
        } catch (NumberFormatException e) {
            showAlert("Validation Error", "Please enter a valid amount.");
            return;
        }

        if (saveTransactionToDatabase(type, category, description, amount, date)) {
            showSuccessAlert(type, category, description, amountText, date.toString());
            closeWindow();
        } else {
            showAlert("Database Error", "Failed to save transaction.");
        }
    }

   private boolean saveTransactionToDatabase(String type, String category, String description, 
                                        double amount, java.sql.Date date) {
    String sql = "INSERT INTO transactions (type, category, description, amount, transaction_date, user_id) VALUES (?, ?, ?, ?, ?, ?)";
    
    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {
        
        stmt.setString(1, type);
        stmt.setString(2, category);
        stmt.setString(3, description);
        stmt.setDouble(4, amount);
        stmt.setDate(5, date);
        
        int currentUserId = getCurrentUserId();
        stmt.setInt(6, currentUserId);
        
        int rowsAffected = stmt.executeUpdate();
        return rowsAffected > 0;
        
    } catch (SQLException e) {
        e.printStackTrace();
        return false;
    }
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

    private void showSuccessAlert(String type, String category, String description, String amount, String date) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Transaction Added");
        alert.setHeaderText(null);
        alert.setContentText(
            "Transaction Added Successfully!\n\n" +
            "Type: " + type + "\n" +
            "Category: " + category + "\n" + 
            "Description: " + description + "\n" +
            "Amount: RM " + amount + "\n" +
            "Date: " + date + "\n\n" +
            "Dashboard will be updated automatically."
        );
        alert.showAndWait();
    }

    @FXML
    private void handleCancel() {
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) amountField.getScene().getWindow();
        stage.close();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}