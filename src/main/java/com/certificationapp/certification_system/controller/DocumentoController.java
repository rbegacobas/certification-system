package com.certificationapp.certification_system.controller;

import com.certificationapp.certification_system.dto.DocumentoResponseDTO;
import com.certificationapp.certification_system.exception.FileStorageException;
import com.certificationapp.certification_system.mapper.DocumentoMapper;
import com.certificationapp.certification_system.model.Certificacion;
import com.certificationapp.certification_system.model.Documento;
import com.certificationapp.certification_system.service.CertificacionService;
import com.certificationapp.certification_system.service.DocumentoService;
import com.certificationapp.certification_system.service.FileStorageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/v1/documents")
@Tag(name = "Document Management", description = "APIs for managing certification documents")
@RequiredArgsConstructor
@Validated
public class DocumentoController {

    private final DocumentoService documentoService;
    private final CertificacionService certificacionService;
    private final FileStorageService fileStorageService;
    private final DocumentoMapper documentoMapper;

    @Operation(summary = "Upload document", description = "Uploads a new document for a certification")
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Document uploaded successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid input"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Certification not found")
    })
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("@documentoSecurity.canUploadDocument(#certificacionId, authentication)")
    public ResponseEntity<com.certificationapp.certification_system.common.ApiResponse<DocumentoResponseDTO>> uploadDocument(
            @Parameter(description = "Certification ID")
            @RequestParam @NotNull Long certificacionId,
            @Parameter(description = "File to upload")
            @RequestParam("file") MultipartFile file) {

        log.debug("Uploading document for certification ID: {}", certificacionId);

        // Obtener la certificación
        Certificacion certificacion = certificacionService.obtenerCertificacionPorId(certificacionId);

        // Almacenar el archivo
        String fileName = fileStorageService.storeFile(file);

        // Crear el documento
        Documento documento = new Documento();
        documento.setCertificacion(certificacion);
        documento.setNombre(Objects.requireNonNull(file.getOriginalFilename()));
        documento.setTipo(Objects.requireNonNull(file.getContentType()));
        documento.setUrl(fileName);

        // Guardar el documento
        var savedDocumento = documentoService.guardarDocumento(documento);
        var responseDTO = documentoMapper.toDto(savedDocumento);

        log.info("Document uploaded successfully for certification ID: {}", certificacionId);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(com.certificationapp.certification_system.common.ApiResponse.success(
                        responseDTO,
                        "Document uploaded successfully"
                ));
    }

    @Operation(summary = "Download document", description = "Downloads a document by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Document downloaded successfully"),
            @ApiResponse(responseCode = "404", description = "Document not found")
    })
    @GetMapping("/download/{id}")
    @PreAuthorize("@documentoSecurity.canAccessDocument(#id, authentication)")
    public ResponseEntity<Resource> downloadDocument(@PathVariable Long id) {
        Documento documento = documentoService.obtenerDocumentoPorId(id);

        try {
            Path filePath = fileStorageService.getFileStorageLocation().resolve(documento.getUrl());
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists()) {
                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(documento.getTipo()))
                        .header(HttpHeaders.CONTENT_DISPOSITION,
                                "attachment; filename=\"" + documento.getNombre() + "\"")
                        .body(resource);
            } else {
                throw new FileStorageException("File not found: " + documento.getUrl());
            }
        } catch (Exception e) {
            throw new FileStorageException("Could not download file", e);
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get document", description = "Retrieves document details by ID")
    @PreAuthorize("@documentoSecurity.canAccessDocument(#id, authentication)")
    public ResponseEntity<com.certificationapp.certification_system.common.ApiResponse<DocumentoResponseDTO>> getDocument(@PathVariable Long id) {
        var documento = documentoService.obtenerDocumentoPorId(id);
        var responseDTO = documentoMapper.toDto(documento);

        return ResponseEntity.ok(com.certificationapp.certification_system.common.ApiResponse.success(
                responseDTO,
                "Document retrieved successfully"
        ));
    }

    @GetMapping("/certification/{certificacionId}")
    @Operation(summary = "List certification documents",
            description = "Retrieves all documents for a specific certification")
    @PreAuthorize("@documentoSecurity.canAccessCertificationDocuments(#certificacionId, authentication)")
    public ResponseEntity<com.certificationapp.certification_system.common.ApiResponse<List<DocumentoResponseDTO>>> getCertificationDocuments(
            @PathVariable Long certificacionId) {

        var documentos = documentoService.obtenerDocumentosPorCertificacion(certificacionId);
        var responseDTOs = documentos.stream()
                .map(documentoMapper::toDto)
                .collect(Collectors.toList());

        return ResponseEntity.ok(com.certificationapp.certification_system.common.ApiResponse.success(
                responseDTOs,
                "Documents retrieved successfully"
        ));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete document", description = "Deletes a document by ID")
    @PreAuthorize("@documentoSecurity.canDeleteDocument(#id, authentication)")
    public ResponseEntity<com.certificationapp.certification_system.common.ApiResponse<Void>> deleteDocument(@PathVariable Long id) {
        var documento = documentoService.obtenerDocumentoPorId(id);
        fileStorageService.deleteFile(documento.getUrl());
        documentoService.eliminarDocumento(id);

        return ResponseEntity.ok(com.certificationapp.certification_system.common.ApiResponse.success(
                null,
                "Document deleted successfully"
        ));
    }
}