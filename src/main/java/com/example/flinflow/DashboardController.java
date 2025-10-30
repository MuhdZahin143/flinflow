package com.example.flinflow;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.geometry.Pos;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class DashboardController implements Initializable {

    @FXML private Label totalIncomeLabel;
    @FXML private Label totalExpenseLabel;
    @FXML private Label balanceLabel;
    @FXML private TableView<Transaction> transactionTable;
    @FXML private TableColumn<Transaction, Integer> idColumn;
    @FXML private TableColumn<Transaction, String> typeColumn;
    @FXML private TableColumn<Transaction, String> categoryColumn; 
    @FXML private TableColumn<Transaction, String> dateColumn;
    @FXML private TableColumn<Transaction, String> descriptionColumn;
    @FXML private TableColumn<Transaction, Double> amountColumn;

    private ObservableList<Transaction> transactions = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupTableColumns();
        loadTransactionData();
        updateSummaryCards();
        autoAdjustTableColumns();
    }

    private void setupTableColumns() {
        // Configure table columns
        idColumn.setCellValueFactory(cellData -> cellData.getValue().idProperty().asObject());
        typeColumn.setCellValueFactory(cellData -> cellData.getValue().typeProperty());
        categoryColumn.setCellValueFactory(cellData -> cellData.getValue().categoryProperty()); 
        dateColumn.setCellValueFactory(cellData -> cellData.getValue().transactionDateProperty().asString());
        descriptionColumn.setCellValueFactory(cellData -> cellData.getValue().descriptionProperty());
        amountColumn.setCellValueFactory(cellData -> cellData.getValue().amountProperty().asObject());
        
        idColumn.setCellFactory(column -> new TableCell<Transaction, Integer>() {
            @Override
            protected void updateItem(Integer id, boolean empty) {
                super.updateItem(id, empty);
                if (empty || id == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(id.toString());
                    setAlignment(Pos.CENTER);
                    setStyle("-fx-alignment: CENTER; -fx-font-weight: 500;");
                }
            }
        });
        
        typeColumn.setCellFactory(column -> new TableCell<Transaction, String>() {
            @Override
            protected void updateItem(String type, boolean empty) {
                super.updateItem(type, empty);
                if (empty || type == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(type);
                    setAlignment(Pos.CENTER);
                    if ("Income".equals(type)) {
                        setStyle("-fx-alignment: CENTER; -fx-text-fill: #4CAF50; -fx-font-weight: bold;");
                    } else {
                        setStyle("-fx-alignment: CENTER; -fx-text-fill: #f44336; -fx-font-weight: bold;");
                    }
                }
            }
        });
        
        categoryColumn.setCellFactory(column -> new TableCell<Transaction, String>() {
            @Override
            protected void updateItem(String category, boolean empty) {
                super.updateItem(category, empty);
                if (empty || category == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(category);
                    setAlignment(Pos.CENTER);
                    setStyle("-fx-alignment: CENTER; -fx-font-weight: 500; -fx-text-fill: #2D3748;");
                }
            }
        });
        
        dateColumn.setCellFactory(column -> new TableCell<Transaction, String>() {
            @Override
            protected void updateItem(String date, boolean empty) {
                super.updateItem(date, empty);
                if (empty || date == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(date);
                    setAlignment(Pos.CENTER);
                    setStyle("-fx-alignment: CENTER; -fx-font-weight: 500;");
                }
            }
        });
        
        descriptionColumn.setCellFactory(column -> new TableCell<Transaction, String>() {
            @Override
            protected void updateItem(String description, boolean empty) {
                super.updateItem(description, empty);
                if (empty || description == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(description);
                    setAlignment(Pos.CENTER_LEFT);
                    setStyle("-fx-alignment: CENTER-LEFT; -fx-font-weight: 400;");
                }
            }
        });
        
        amountColumn.setCellFactory(column -> new TableCell<Transaction, Double>() {
            @Override
            protected void updateItem(Double amount, boolean empty) {
                super.updateItem(amount, empty);
                if (empty || amount == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(String.format("RM %.2f", amount));
                    setAlignment(Pos.CENTER);
                    
                    Transaction transaction = getTableView().getItems().get(getIndex());
                    if ("Income".equals(transaction.getType())) {
                        setStyle("-fx-alignment: CENTER; -fx-text-fill: #4CAF50; -fx-font-weight: bold;");
                    } else {
                        setStyle("-fx-alignment: CENTER; -fx-text-fill: #f44336; -fx-font-weight: bold;");
                    }
                }
            }
        });
    }

    private void autoAdjustTableColumns() {
        // Set constrained resize policy to eliminate empty space
        transactionTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
   
    }

    private void loadTransactionData() {
        transactions.clear();
        
        // Load Transaction current User
        String sql = "SELECT * FROM transactions WHERE user_id = ? ORDER BY transaction_date DESC, id DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            // Set current user ID
            int currentUserId = getCurrentUserId();
            stmt.setInt(1, currentUserId);
            
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Transaction transaction = new Transaction(
                    rs.getInt("id"),
                    rs.getString("type"),
                    rs.getString("category"),
                    rs.getString("description"),
                    rs.getDouble("amount"),
                    rs.getDate("transaction_date").toLocalDate()
                );
                transactions.add(transaction);
            }
            
            transactionTable.setItems(transactions);
            
            System.out.println(" Loaded " + transactions.size() + " transactions for user ID: " + currentUserId);
            
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Database Error", "Cannot load transactions: " + e.getMessage());
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

    private void updateSummaryCards() {
        double totalIncome = 0;
        double totalExpense = 0;
        
        for (Transaction transaction : transactions) {
            if ("Income".equals(transaction.getType())) {
                totalIncome += transaction.getAmount();
            } else {
                totalExpense += transaction.getAmount();
            }
        }
        
        double balance = totalIncome - totalExpense;
        
        totalIncomeLabel.setText(String.format("RM %.2f", totalIncome));
        totalExpenseLabel.setText(String.format("RM %.2f", totalExpense));
        balanceLabel.setText(String.format("RM %.2f", balance));
        
        // Color coding balance
        if (balance >= 0) {
            balanceLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #4CAF50;");
        } else {
            balanceLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #f44336;");
        }
    }

    @FXML
    private void handleSetting() { 
        try {
            App.setRoot("Setting.fxml");
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Cannot open settings: " + e.getMessage());
        }
    }

    @FXML
    private void handleAddTransaction() { 
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("add_transaction.fxml"));
            Parent root = loader.load();

            Stage popupStage = new Stage();
            popupStage.initModality(Modality.APPLICATION_MODAL);
            popupStage.setTitle("Add New Transaction");
            popupStage.setScene(new Scene(root));
            popupStage.setResizable(false);
            
            // Refresh data after popup closes
            popupStage.setOnHidden(event -> {
                loadTransactionData();
                updateSummaryCards();
            });
            
            popupStage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Cannot open transaction form: " + e.getMessage());
        }
    }

    @FXML
    private void handleRemoveSelected() {
        Transaction selectedTransaction = transactionTable.getSelectionModel().getSelectedItem();
        
        if (selectedTransaction == null) {
            showAlert("Selection Error", "Please select a transaction to remove.");
            return;
        }
        
        // Confirm deletion
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Delete");
        confirmAlert.setHeaderText("Delete Transaction");
        confirmAlert.setContentText("Are you sure you want to delete this transaction?\n\n" +
                                   "ID: " + selectedTransaction.getId() + "\n" +
                                   "Type: " + selectedTransaction.getType() + "\n" +
                                   "Category: " + selectedTransaction.getCategory() + "\n" +
                                   "Amount: RM " + selectedTransaction.getAmount());
        
        if (confirmAlert.showAndWait().get() == ButtonType.OK) {
            if (deleteTransactionFromDatabase(selectedTransaction.getId())) {
                loadTransactionData();
                updateSummaryCards();
                showAlert("Success", "Transaction deleted successfully.");
            } else {
                showAlert("Error", "Failed to delete transaction.");
            }
        }
    }

    private boolean deleteTransactionFromDatabase(int transactionId) {
        String sql = "DELETE FROM transactions WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, transactionId);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}