package com.expensetracker.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.expensetracker.dto.FileUploadResponse;
import com.expensetracker.exception.ResourceNotFoundException;
import com.expensetracker.model.Expense;
import com.expensetracker.model.FileUpload;
import com.expensetracker.model.User;
import com.expensetracker.repository.ExpenseRepository;
import com.expensetracker.repository.FileUploadRepository;
import com.expensetracker.repository.UserRepository;

import jakarta.annotation.PostConstruct;

@Service
public class FileStorageService {

    private final FileUploadRepository fileUploadRepository;
    private final UserRepository userRepository;
    private final ExpenseRepository expenseRepository;
    private final Path uploadPath;

    public FileStorageService(
            FileUploadRepository fileUploadRepository,
            UserRepository userRepository,
            ExpenseRepository expenseRepository,
            @Value("${app.file.upload-dir:./uploads}") String uploadDir) {
        this.fileUploadRepository = fileUploadRepository;
        this.userRepository = userRepository;
        this.expenseRepository = expenseRepository;
        this.uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
    }

    @PostConstruct
    public void init() {
        try {
            Files.createDirectories(uploadPath);
        } catch (IOException e) {
            throw new RuntimeException("Could not create upload directory", e);
        }
    }

    private User getCurrentUser() {
        Long userId = Long.valueOf(
                SecurityContextHolder.getContext()
                        .getAuthentication()
                        .getPrincipal()
                        .toString());
        return userRepository.findById(userId).orElseThrow();
    }

    public FileUploadResponse uploadFile(MultipartFile file, Long expenseId) throws IOException {
        User user = getCurrentUser();

        if (file.isEmpty()) {
            throw new IllegalArgumentException("Cannot upload empty file");
        }

        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        String storedFilename = UUID.randomUUID().toString() + extension;

        Path targetPath = uploadPath.resolve(storedFilename);
        Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

        FileUpload fileUpload = new FileUpload();
        fileUpload.setOriginalFilename(originalFilename);
        fileUpload.setStoredFilename(storedFilename);
        fileUpload.setContentType(file.getContentType());
        fileUpload.setFileSize(file.getSize());
        fileUpload.setFilePath(targetPath.toString());
        fileUpload.setUser(user);

        if (expenseId != null) {
            Expense expense = expenseRepository.findByIdAndUser(expenseId, user);
            if (expense != null) {
                fileUpload.setExpense(expense);
            }
        }

        FileUpload saved = fileUploadRepository.save(fileUpload);
        return toResponse(saved);
    }

    public Resource downloadFile(Long fileId) throws IOException {
        User user = getCurrentUser();
        FileUpload fileUpload = fileUploadRepository.findByIdAndUser(fileId, user);

        if (fileUpload == null) {
            throw new ResourceNotFoundException("File not found with id: " + fileId);
        }

        Path filePath = Paths.get(fileUpload.getFilePath());
        Resource resource = new UrlResource(filePath.toUri());

        if (!resource.exists() || !resource.isReadable()) {
            throw new ResourceNotFoundException("File not found or not readable: " + fileId);
        }

        return resource;
    }

    public FileUpload getFileUpload(Long fileId) {
        User user = getCurrentUser();
        FileUpload fileUpload = fileUploadRepository.findByIdAndUser(fileId, user);

        if (fileUpload == null) {
            throw new ResourceNotFoundException("File not found with id: " + fileId);
        }

        return fileUpload;
    }

    public List<FileUploadResponse> getUserFiles() {
        User user = getCurrentUser();
        return fileUploadRepository.findByUser(user)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public void deleteFile(Long fileId) throws IOException {
        User user = getCurrentUser();
        FileUpload fileUpload = fileUploadRepository.findByIdAndUser(fileId, user);

        if (fileUpload == null) {
            throw new ResourceNotFoundException("File not found with id: " + fileId);
        }

        Path filePath = Paths.get(fileUpload.getFilePath());
        Files.deleteIfExists(filePath);

        fileUploadRepository.delete(fileUpload);
    }

    private FileUploadResponse toResponse(FileUpload fileUpload) {
        return new FileUploadResponse(
                fileUpload.getId(),
                fileUpload.getOriginalFilename(),
                fileUpload.getContentType(),
                fileUpload.getFileSize(),
                fileUpload.getExpense() != null ? fileUpload.getExpense().getId() : null,
                fileUpload.getUploadedAt());
    }
}
