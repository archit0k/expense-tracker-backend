package com.expensetracker.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.Positive;

public class UpdateExpenseRequest {

    @Positive(message = "Amount must be positive")
    private Double amount;

    private String category;

    private String description;

    private LocalDate expenseDate;

    public Double getAmount() {
        return amount;
    }

    public String getCategory() {
        return category;
    }

    public String getDescription() {
        return description;
    }

    public LocalDate getExpenseDate() {
        return expenseDate;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setExpenseDate(LocalDate expenseDate) {
        this.expenseDate = expenseDate;
    }
}
