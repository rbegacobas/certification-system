package com.certificationapp.certification_system.service;

import com.certificationapp.certification_system.config.FileStorageProperties;
import com.certificationapp.certification_system.exception.*;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@Service
public class FileStorageService {

    @Getter
    private final Path fileStorageLocation;
    private final FileStorageProperties fileStorageProperties;

    public FileStorageService(FileStorageProperties fileStorageProperties) {
        this.fileStorageProperties = fileStorageProperties;
        this.fileStorageLocation = Paths.get(fileStorageProperties.getUploadDir())
                .toAbsolutePath()
                .normalize();

        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (IOException ex) {
            throw new FileStorageOperationException("Could not create the directory where the uploaded files will be stored.", ex);
        }
    }

    public String storeFile(MultipartFile file) {
        validateFile(file);

        String originalFileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        String fileExtension = getFileExtension(originalFileName);
        String fileName = generateUniqueFileName(fileExtension);

        try {
            Path targetLocation = this.fileStorageLocation.resolve(fileName);

            // Usar un InputStream para manejar archivos grandes
            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, targetLocation, StandardCopyOption.REPLACE_EXISTING);
            }

            log.info("File {} stored successfully as {}", originalFileName, fileName);
            return fileName;
        } catch (IOException ex) {
            throw new FileStorageOperationException("Could not store file " + fileName, ex);
        }
    }

    public void deleteFile(String fileName) {
        try {
            Path targetLocation = this.fileStorageLocation.resolve(fileName);
            Files.deleteIfExists(targetLocation);
            log.info("File {} deleted successfully", fileName);
        } catch (IOException ex) {
            throw new FileStorageOperationException("Could not delete file " + fileName, ex);
        }
    }

    private void validateFile(MultipartFile file) {
        // Validar si el archivo está vacío
        if (file.isEmpty()) {
            throw new FileStorageException("Failed to store empty file");
        }

        // Validar el tamaño del archivo
        if (file.getSize() > fileStorageProperties.getMaxFileSize()) {
            throw new FileSizeLimitExceededException(
                    String.format("File size exceeds maximum allowed size of %d bytes",
                            fileStorageProperties.getMaxFileSize())
            );
        }

        // Validar el tipo de archivo
        String contentType = file.getContentType();
        if (contentType == null || !fileStorageProperties.isFileTypeAllowed(contentType)) {
            throw new InvalidFileTypeException("File type not allowed: " + contentType);
        }

        // Validar la extensión del archivo
        String fileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        if (!fileStorageProperties.isExtensionAllowed(fileName)) {
            throw new InvalidFileTypeException("File extension not allowed for file: " + fileName);
        }
    }

    private String generateUniqueFileName(String fileExtension) {
        return UUID.randomUUID().toString() + fileExtension;
    }

    private String getFileExtension(String fileName) {
        return fileName.substring(fileName.lastIndexOf("."));
    }
}