package com.expensetracker.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.expensetracker.model.Expense;
import com.expensetracker.model.User;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    Page<Expense> findByUser(User user, Pageable pageable);

    List<Expense> findByUser(User user);

    Page<Expense> findByUserAndCategory(User user, String category, Pageable pageable);

    Page<Expense> findByUserAndExpenseDateBetween(
            User user,
            LocalDate startDate,
            LocalDate endDate,
            Pageable pageable
    );

    Page<Expense> findByUserAndCategoryAndExpenseDateBetween(
            User user,
            String category,
            LocalDate startDate,
            LocalDate endDate,
            Pageable pageable
    );

    Expense findByIdAndUser(Long id, User user);
}
