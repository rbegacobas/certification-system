package com.certificationapp.certification_system.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * DTO for JWT authentication response.
 */
@Data
@AllArgsConstructor
public class JwtResponseDTO {
    private String token;
    private String type = "Bearer";
    private Long id;
    private String username;
    private String email;
    private String role;
}