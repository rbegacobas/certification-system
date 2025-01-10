package com.certificationapp.certification_system.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Component
@ConfigurationProperties(prefix = "app.file")
@Data
public class FileStorageProperties {

    private String uploadDir;
    private long maxFileSize = 5242880; // 5MB por defecto
    private Set<String> allowedFileTypes = new HashSet<>(Arrays.asList(
            "application/pdf",
            "image/jpeg",
            "image/png",
            "application/msword",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
    ));

    // Extensiones permitidas para verificación adicional
    private Set<String> allowedExtensions = new HashSet<>(Arrays.asList(
            ".pdf",
            ".jpg",
            ".jpeg",
            ".png",
            ".doc",
            ".docx"
    ));

    public boolean isFileTypeAllowed(String contentType) {
        return allowedFileTypes.contains(contentType.toLowerCase());
    }

    public boolean isExtensionAllowed(String fileName) {
        return allowedExtensions.stream()
                .anyMatch(ext -> fileName.toLowerCase().endsWith(ext));
    }
}