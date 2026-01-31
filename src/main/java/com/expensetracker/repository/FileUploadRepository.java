package com.expensetracker.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.expensetracker.model.Expense;
import com.expensetracker.model.FileUpload;
import com.expensetracker.model.User;

public interface FileUploadRepository extends JpaRepository<FileUpload, Long> {

    List<FileUpload> findByUser(User user);

    List<FileUpload> findByExpense(Expense expense);

    FileUpload findByIdAndUser(Long id, User user);
}
