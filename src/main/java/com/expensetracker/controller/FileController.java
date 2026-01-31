package com.expensetracker.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.expensetracker.dto.FileUploadResponse;
import com.expensetracker.model.FileUpload;
import com.expensetracker.service.FileStorageService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/files")
@Tag(name = "Files", description = "File upload and download APIs")
public class FileController {

    private final FileStorageService fileStorageService;

    public FileController(FileStorageService fileStorageService) {
        this.fileStorageService = fileStorageService;
    }

    @Operation(summary = "Upload a file", description = "Uploads a file (e.g., receipt) optionally linked to an expense")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "File uploaded successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid file"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public FileUploadResponse uploadFile(
            @Parameter(description = "File to upload") @RequestParam("file") MultipartFile file,
            @Parameter(description = "Optional expense ID to link the file to") @RequestParam(value = "expenseId", required = false) Long expenseId)
            throws IOException {
        return fileStorageService.uploadFile(file, expenseId);
    }

    @Operation(summary = "Download a file", description = "Downloads a file by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "File downloaded successfully"),
            @ApiResponse(responseCode = "404", description = "File not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @GetMapping("/{id}/download")
    public ResponseEntity<Resource> downloadFile(
            @Parameter(description = "File ID") @PathVariable Long id) throws IOException {
        Resource resource = fileStorageService.downloadFile(id);
        FileUpload fileUpload = fileStorageService.getFileUpload(id);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(fileUpload.getContentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + fileUpload.getOriginalFilename() + "\"")
                .body(resource);
    }

    @Operation(summary = "Get all files", description = "Retrieves all files uploaded by the authenticated user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Files retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @GetMapping
    public List<FileUploadResponse> getUserFiles() {
        return fileStorageService.getUserFiles();
    }

    @Operation(summary = "Delete a file", description = "Deletes a file by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "File deleted successfully"),
            @ApiResponse(responseCode = "404", description = "File not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteFile(
            @Parameter(description = "File ID") @PathVariable Long id) throws IOException {
        fileStorageService.deleteFile(id);
    }
}
