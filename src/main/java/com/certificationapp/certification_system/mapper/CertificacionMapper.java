package com.certificationapp.certification_system.mapper;

import com.certificationapp.certification_system.dto.CertificacionCreateDTO;
import com.certificationapp.certification_system.dto.CertificacionResponseDTO;
import com.certificationapp.certification_system.dto.CertificacionUpdateDTO;
import com.certificationapp.certification_system.model.Certificacion;
import org.mapstruct.*;

/**
 * MapStruct mapper for Certificacion entity and DTOs.
 * Handles the mapping between different certification data representations.
 */
@Mapper(componentModel = "spring",
        uses = {DocumentoMapper.class},
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CertificacionMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "fechaCreacion", ignore = true)
    @Mapping(target = "documentos", ignore = true)
    @Mapping(target = "status", constant = "PENDING")
    @Mapping(target = "usuario.id", source = "usuarioId")
    Certificacion toEntity(CertificacionCreateDTO dto);

    @Mapping(source = "usuario.id", target = "usuarioId")
    CertificacionResponseDTO toDto(Certificacion certificacion);

    @Mapping(target = "status", source = "status")
    void updateEntity(@MappingTarget Certificacion entity, CertificacionUpdateDTO dto);
}