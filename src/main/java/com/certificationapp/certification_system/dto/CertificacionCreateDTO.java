package com.certificationapp.certification_system.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * DTO for certification creation requests.
 * Contains the essential data needed to create a new certification.
 */
@Data
public class CertificacionCreateDTO {

    @NotNull(message = "User ID is required")
    private Long usuarioId;

    @NotBlank(message = "Certification type is required")
    private String tipo;
}