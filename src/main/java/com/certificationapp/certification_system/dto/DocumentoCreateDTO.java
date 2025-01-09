package com.certificationapp.certification_system.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * DTO for document creation requests.
 * Contains the necessary data to create a new document.
 */
@Data
public class DocumentoCreateDTO {

    @NotNull(message = "Certification ID is required")
    private Long certificacionId;

    @NotBlank(message = "Document name is required")
    private String nombre;

    @NotBlank(message = "Document type is required")
    private String tipo;

    @NotBlank(message = "Document URL is required")
    private String url;
}