package com.certificationapp.certification_system.controller;

import com.certificationapp.certification_system.common.ApiResponse;
import com.certificationapp.certification_system.dto.CertificacionCreateDTO;
import com.certificationapp.certification_system.dto.CertificacionResponseDTO;
import com.certificationapp.certification_system.dto.CertificacionUpdateDTO;
import com.certificationapp.certification_system.mapper.CertificacionMapper;
import com.certificationapp.certification_system.model.Certificacion;
import com.certificationapp.certification_system.service.CertificacionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * REST controller for managing certifications.
 * Implements endpoints for CRUD operations on certifications.
 *
 * @version 1.0
 * @since 2024-01-09
 */
@RestController
@RequestMapping("/api/v1/certifications")
@Tag(name = "Certification Management", description = "APIs for managing certifications")
@RequiredArgsConstructor
public class CertificacionController {

    private final CertificacionService certificacionService;
    private final CertificacionMapper certificacionMapper;

    /**
     * Creates a new certification.
     *
     * @param createDTO the certification data
     * @return the created certification
     */
    @PostMapping
    @Operation(summary = "Create certification", description = "Creates a new certification request")
    public ResponseEntity<ApiResponse<CertificacionResponseDTO>> createCertification(
            @Valid @RequestBody CertificacionCreateDTO createDTO) {

        var certificacion = certificacionMapper.toEntity(createDTO);
        var createdCertificacion = certificacionService.crearCertificacion(certificacion);
        var responseDTO = certificacionMapper.toDto(createdCertificacion);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(responseDTO, "Certification created successfully"));
    }

    /**
     * Retrieves a certification by ID.
     *
     * @param id the certification ID
     * @return the certification details
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get certification by ID", description = "Retrieves certification details by ID")
    public ResponseEntity<ApiResponse<CertificacionResponseDTO>> getCertificationById(
            @Parameter(description = "Certification ID") @PathVariable Long id) {

        var certificacion = certificacionService.obtenerCertificacionPorId(id);
        var responseDTO = certificacionMapper.toDto(certificacion);

        return ResponseEntity.ok(ApiResponse.success(responseDTO, "Certification retrieved successfully"));
    }

    /**
     * Lists certifications by user.
     *
     * @param userId the user ID
     * @return list of certifications for the user
     */
    @GetMapping("/user/{userId}")
    @Operation(summary = "List user certifications", description = "Retrieves all certifications for a specific user")
    public ResponseEntity<ApiResponse<List<CertificacionResponseDTO>>> getUserCertifications(
            @Parameter(description = "User ID") @PathVariable Long userId) {

        var certificaciones = certificacionService.obtenerCertificacionesPorUsuario(userId);
        var responseDTOs = certificaciones.stream()
                .map(certificacionMapper::toDto)
                .collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.success(responseDTOs, "User certifications retrieved successfully"));
    }

    /**
     * Updates certification status.
     *
     * @param id the certification ID
     * @param updateDTO the updated status data
     * @return the updated certification
     */
    @PutMapping("/{id}/status")
    @Operation(summary = "Update certification status", description = "Updates the status of an existing certification")
    public ResponseEntity<ApiResponse<CertificacionResponseDTO>> updateCertificationStatus(
            @Parameter(description = "Certification ID") @PathVariable Long id,
            @Valid @RequestBody CertificacionUpdateDTO updateDTO) {

        var certificacion = certificacionService.actualizarEstadoCertificacion(
                id,
                Certificacion.Status.valueOf(updateDTO.getStatus())
        );
        var responseDTO = certificacionMapper.toDto(certificacion);

        return ResponseEntity.ok(ApiResponse.success(responseDTO, "Certification status updated successfully"));
    }
}