package com.certificationapp.certification_system.controller;

import com.certificationapp.certification_system.common.ApiResponse;
import com.certificationapp.certification_system.dto.DocumentoResponseDTO;
import com.certificationapp.certification_system.mapper.DocumentoMapper;
import com.certificationapp.certification_system.model.Documento;
import com.certificationapp.certification_system.service.DocumentoService;
import com.certificationapp.certification_system.service.FileStorageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

/**
 * REST controller for managing documents.
 * Handles document upload, download, and management operations.
 *
 * @version 1.0
 * @since 2024-01-09
 */
@RestController
@RequestMapping("/api/v1/documents")
@Tag(name = "Document Management", description = "APIs for managing certification documents")
@RequiredArgsConstructor
public class DocumentoController {

    private final DocumentoService documentoService;
    private final FileStorageService fileStorageService;
    private final DocumentoMapper documentoMapper;

    /**
     * Uploads a new document for a certification.
     *
     * @param certificacionId the certification ID
     * @param file the file to upload
     * @return the created document details
     */
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload document", description = "Uploads a new document for a certification")
    public ResponseEntity<ApiResponse<DocumentoResponseDTO>> uploadDocument(
            @Parameter(description = "Certification ID") @RequestParam Long certificacionId,
            @Parameter(description = "File to upload") @RequestParam("file") MultipartFile file) {

        String fileName = fileStorageService.storeFile(file);

        Documento documento = new Documento();
        documento.setNombre(file.getOriginalFilename());
        documento.setTipo(file.getContentType());
        documento.setUrl(fileName);

        var savedDocumento = documentoService.guardarDocumento(documento);
        var responseDTO = documentoMapper.toDto(savedDocumento);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(responseDTO, "Document uploaded successfully"));
    }

    /**
     * Retrieves a document by ID.
     *
     * @param id the document ID
     * @return the document details
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get document", description = "Retrieves document details by ID")
    public ResponseEntity<ApiResponse<DocumentoResponseDTO>> getDocument(
            @Parameter(description = "Document ID") @PathVariable Long id) {

        var documento = documentoService.obtenerDocumentoPorId(id);
        var responseDTO = documentoMapper.toDto(documento);

        return ResponseEntity.ok(ApiResponse.success(responseDTO, "Document retrieved successfully"));
    }

    /**
     * Lists all documents for a certification.
     *
     * @param certificacionId the certification ID
     * @return list of documents
     */
    @GetMapping("/certification/{certificacionId}")
    @Operation(summary = "List certification documents",
            description = "Retrieves all documents for a specific certification")
    public ResponseEntity<ApiResponse<List<DocumentoResponseDTO>>> getCertificationDocuments(
            @Parameter(description = "Certification ID") @PathVariable Long certificacionId) {

        var documentos = documentoService.obtenerDocumentosPorCertificacion(certificacionId);
        var responseDTOs = documentos.stream()
                .map(documentoMapper::toDto)
                .collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.success(responseDTOs, "Documents retrieved successfully"));
    }

    /**
     * Deletes a document.
     *
     * @param id the document ID
     * @return confirmation response
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete document", description = "Deletes a document by ID")
    public ResponseEntity<ApiResponse<Void>> deleteDocument(
            @Parameter(description = "Document ID") @PathVariable Long id) {

        var documento = documentoService.obtenerDocumentoPorId(id);
        fileStorageService.deleteFile(documento.getUrl());
        documentoService.eliminarDocumento(id);

        return ResponseEntity.ok(ApiResponse.success(null, "Document deleted successfully"));
    }
}