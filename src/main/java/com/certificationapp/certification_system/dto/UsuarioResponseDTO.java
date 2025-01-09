package com.certificationapp.certification_system.dto;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * DTO for user response data.
 * Excludes sensitive information like passwords.
 */
@Data
public class UsuarioResponseDTO {
    private Long id;
    private String username;
    private String email;
    private String role;
    private LocalDateTime createdAt;
}