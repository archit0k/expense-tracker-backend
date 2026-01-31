package com.expensetracker.controller;

import java.util.Map;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.expensetracker.service.ExpenseService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/analytics")
@Tag(name = "Analytics", description = "Expense analytics and reporting APIs (Admin only)")
public class AnalyticsController {

    private final ExpenseService expenseService;

    public AnalyticsController(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }

    @Operation(summary = "Get total expenses", description = "Returns the total sum of all expenses for the current user (Admin only)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Total expense retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Admin role required")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/total")
    public Double getTotalExpense() {
        return expenseService.getTotalExpense();
    }

    @Operation(summary = "Get category summary", description = "Returns expense totals grouped by category (Admin only)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Category summary retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Admin role required")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/category-summary")
    public Map<String, Double> getCategorySummary() {
        return expenseService.getCategorySummary();
    }
}
