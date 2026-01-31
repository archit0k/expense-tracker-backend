package com.expensetracker.dto;

import java.time.LocalDate;

public class ExpenseResponse {

    private Long id;
    private Double amount;
    private String category;
    private String description;
    private LocalDate expenseDate;

    public ExpenseResponse(
            Long id,
            Double amount,
            String category,
            String description,
            LocalDate expenseDate
    ) {
        this.id = id;
        this.amount = amount;
        this.category = category;
        this.description = description;
        this.expenseDate = expenseDate;
    }

    public Long getId() {
        return id;
    }

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
}
