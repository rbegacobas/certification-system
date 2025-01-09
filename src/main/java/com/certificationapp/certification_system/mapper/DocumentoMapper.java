package com.certificationapp.certification_system.mapper;

import com.certificationapp.certification_system.dto.DocumentoCreateDTO;
import com.certificationapp.certification_system.dto.DocumentoResponseDTO;
import com.certificationapp.certification_system.model.Documento;
import org.mapstruct.*;

/**
 * MapStruct mapper for Documento entity and DTOs.
 * Handles the mapping between different document data representations.
 */
@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface DocumentoMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "fechaSubida", ignore = true)
    @Mapping(source = "certificacionId", target = "certificacion.id")
    Documento toEntity(DocumentoCreateDTO dto);

    @Mapping(source = "certificacion.id", target = "certificacionId")
    DocumentoResponseDTO toDto(Documento documento);
}