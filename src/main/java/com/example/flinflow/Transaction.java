/*package com.example.flinflow;

import java.sql.Date;

public class Transaction {
    private int id;
    private String type;
    private String category;
    private String description;
    private double amount;
    private Date transactionDate;

    public Transaction(int id, String type, String category, String description, double amount, Date transactionDate) {
        this.id = id;
        this.type = type;
        this.category = category;
        this.description = description;
        this.amount = amount;
        this.transactionDate = transactionDate;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public Date getTransactionDate() { return transactionDate; }
    public void setTransactionDate(Date transactionDate) { this.transactionDate = transactionDate; }
}*/
package com.example.flinflow;

import java.time.LocalDate;
import javafx.beans.property.*;

public class Transaction {
    private final IntegerProperty id = new SimpleIntegerProperty();
    private final StringProperty type = new SimpleStringProperty();
    private final StringProperty category = new SimpleStringProperty();
    private final StringProperty description = new SimpleStringProperty();
    private final DoubleProperty amount = new SimpleDoubleProperty();
    private final ObjectProperty<LocalDate> transactionDate = new SimpleObjectProperty<>();
    
    public Transaction() {}
    
    public Transaction(int id, String type, String category, String description, 
                      double amount, LocalDate transactionDate) {
        setId(id);
        setType(type);
        setCategory(category);
        setDescription(description);
        setAmount(amount);
        setTransactionDate(transactionDate);
    }
    
    // Property getters
    public IntegerProperty idProperty() { return id; }
    public StringProperty typeProperty() { return type; }
    public StringProperty categoryProperty() { return category; }
    public StringProperty descriptionProperty() { return description; }
    public DoubleProperty amountProperty() { return amount; }
    public ObjectProperty<LocalDate> transactionDateProperty() { return transactionDate; }
    
    // Normal getters and setters
    public int getId() { return id.get(); }
    public void setId(int id) { this.id.set(id); }
    
    public String getType() { return type.get(); }
    public void setType(String type) { this.type.set(type); }
    
    public String getCategory() { return category.get(); }
    public void setCategory(String category) { this.category.set(category); }
    
    public String getDescription() { return description.get(); }
    public void setDescription(String description) { this.description.set(description); }
    
    public double getAmount() { return amount.get(); }
    public void setAmount(double amount) { this.amount.set(amount); }
    
    public LocalDate getTransactionDate() { return transactionDate.get(); }
    public void setTransactionDate(LocalDate transactionDate) { this.transactionDate.set(transactionDate); }
    
    @Override
    public String toString() {
        return String.format("Transaction{id=%d, type='%s', category='%s', amount=%.2f}", 
                           getId(), getType(), getCategory(), getAmount());
    }
}