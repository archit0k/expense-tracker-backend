package com.expensetracker.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.expensetracker.dto.CreateExpenseRequest;
import com.expensetracker.dto.ExpenseResponse;
import com.expensetracker.dto.UpdateExpenseRequest;
import com.expensetracker.exception.ResourceNotFoundException;
import com.expensetracker.model.Expense;
import com.expensetracker.model.User;
import com.expensetracker.repository.ExpenseRepository;
import com.expensetracker.repository.UserRepository;

@Service
public class ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;

    public ExpenseService(
            ExpenseRepository expenseRepository,
            UserRepository userRepository,
            EmailService emailService) {
        this.expenseRepository = expenseRepository;
        this.userRepository = userRepository;
        this.emailService = emailService;
    }

    private User getCurrentUser() {
        Long userId = Long.valueOf(
                SecurityContextHolder.getContext()
                        .getAuthentication()
                        .getPrincipal()
                        .toString());

        return userRepository.findById(userId).orElseThrow();
    }

    @CacheEvict(value = "expenses", allEntries = true)
    public ExpenseResponse createExpense(CreateExpenseRequest request) {
        User user = getCurrentUser();

        Expense expense = new Expense();
        expense.setAmount(request.getAmount());
        expense.setCategory(request.getCategory());
        expense.setDescription(request.getDescription());
        expense.setExpenseDate(request.getExpenseDate());
        expense.setUser(user);

        Expense saved = expenseRepository.save(expense);

        emailService.sendExpenseNotification(user.getEmail(), saved.getAmount());

        return toResponse(saved);
    }

    @CacheEvict(value = "expenses", allEntries = true)
    public ExpenseResponse updateExpense(Long id, UpdateExpenseRequest request) {
        User user = getCurrentUser();
        Expense expense = expenseRepository.findByIdAndUser(id, user);

        if (expense == null) {
            throw new ResourceNotFoundException("Expense not found with id: " + id);
        }

        // Update only non-null fields (partial update)
        if (request.getAmount() != null) {
            expense.setAmount(request.getAmount());
        }
        if (request.getCategory() != null) {
            expense.setCategory(request.getCategory());
        }
        if (request.getDescription() != null) {
            expense.setDescription(request.getDescription());
        }
        if (request.getExpenseDate() != null) {
            expense.setExpenseDate(request.getExpenseDate());
        }

        Expense updated = expenseRepository.save(expense);
        return toResponse(updated);
    }

    public Page<ExpenseResponse> getExpenses(
            String category,
            LocalDate startDate,
            LocalDate endDate,
            Pageable pageable) {
        User user = getCurrentUser();

        if (category != null && startDate != null && endDate != null) {
            return expenseRepository
                    .findByUserAndCategoryAndExpenseDateBetween(
                            user, category, startDate, endDate, pageable)
                    .map(this::toResponse);
        }

        if (category != null) {
            return expenseRepository
                    .findByUserAndCategory(user, category, pageable)
                    .map(this::toResponse);
        }

        if (startDate != null && endDate != null) {
            return expenseRepository
                    .findByUserAndExpenseDateBetween(
                            user, startDate, endDate, pageable)
                    .map(this::toResponse);
        }

        return expenseRepository.findByUser(user, pageable).map(this::toResponse);
    }

    @Cacheable(value = "expenses", key = "#id")
    public ExpenseResponse getExpenseById(Long id) {
        User user = getCurrentUser();
        Expense expense = expenseRepository.findByIdAndUser(id, user);

        if (expense == null) {
            throw new ResourceNotFoundException("Expense not found with id: " + id);
        }

        return toResponse(expense);
    }

    @CacheEvict(value = "expenses", allEntries = true)
    public void deleteExpense(Long id) {
        User user = getCurrentUser();
        Expense expense = expenseRepository.findByIdAndUser(id, user);

        if (expense == null) {
            throw new ResourceNotFoundException("Expense not found with id: " + id);
        }

        expenseRepository.delete(expense);
    }

    public Double getTotalExpense() {
        User user = getCurrentUser();
        return expenseRepository.findByUser(user)
                .stream()
                .mapToDouble(Expense::getAmount)
                .sum();
    }

    public Map<String, Double> getCategorySummary() {
        User user = getCurrentUser();

        return expenseRepository.findByUser(user)
                .stream()
                .collect(Collectors.groupingBy(
                        Expense::getCategory,
                        Collectors.summingDouble(Expense::getAmount)));
    }

    private ExpenseResponse toResponse(Expense expense) {
        return new ExpenseResponse(
                expense.getId(),
                expense.getAmount(),
                expense.getCategory(),
                expense.getDescription(),
                expense.getExpenseDate());
    }
}
