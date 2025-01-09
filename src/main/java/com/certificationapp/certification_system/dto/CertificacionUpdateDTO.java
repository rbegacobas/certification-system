package com.certificationapp.certification_system.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * DTO for certification status updates.
 * Used to update the status of an existing certification.
 */
@Data
public class CertificacionUpdateDTO {

    @NotNull(message = "Status is required")
    private String status;
}