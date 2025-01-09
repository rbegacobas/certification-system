package com.certificationapp.certification_system.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO for certification response data.
 * Includes all certification details and associated documents.
 */
@Data
public class CertificacionResponseDTO {
    private Long id;
    private Long usuarioId;
    private String tipo;
    private String status;
    private LocalDateTime fechaCreacion;
    private List<DocumentoResponseDTO> documentos;
}