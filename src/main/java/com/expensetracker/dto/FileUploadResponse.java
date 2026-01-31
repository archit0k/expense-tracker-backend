package com.expensetracker.dto;

import java.time.LocalDateTime;

public class FileUploadResponse {

    private Long id;
    private String originalFilename;
    private String contentType;
    private Long fileSize;
    private Long expenseId;
    private LocalDateTime uploadedAt;

    public FileUploadResponse(Long id, String originalFilename, String contentType,
            Long fileSize, Long expenseId, LocalDateTime uploadedAt) {
        this.id = id;
        this.originalFilename = originalFilename;
        this.contentType = contentType;
        this.fileSize = fileSize;
        this.expenseId = expenseId;
        this.uploadedAt = uploadedAt;
    }

    // Getters
    public Long getId() {
        return id;
    }

    public String getOriginalFilename() {
        return originalFilename;
    }

    public String getContentType() {
        return contentType;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public Long getExpenseId() {
        return expenseId;
    }

    public LocalDateTime getUploadedAt() {
        return uploadedAt;
    }
}
