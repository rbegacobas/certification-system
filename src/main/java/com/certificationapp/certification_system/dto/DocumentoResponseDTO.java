package com.certificationapp.certification_system.dto;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * DTO for document response data.
 * Contains all document details for client responses.
 */
@Data
public class DocumentoResponseDTO {
    private Long id;
    private Long certificacionId;
    private String nombre;
    private String tipo;
    private String url;
    private LocalDateTime fechaSubida;
}