package com.certificationapp.certification_system.mapper;

import com.certificationapp.certification_system.dto.UsuarioCreateDTO;
import com.certificationapp.certification_system.dto.UsuarioResponseDTO;
import com.certificationapp.certification_system.dto.UsuarioUpdateDTO;
import com.certificationapp.certification_system.model.Usuario;
import org.mapstruct.*;

/**
 * MapStruct mapper for Usuario entity and DTOs.
 * Provides type-safe mapping between different representations of user data.
 */
@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UsuarioMapper {

    /**
     * Maps UsuarioCreateDTO to Usuario entity.
     * Sets default role to USER.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "role", constant = "USER")
    Usuario toEntity(UsuarioCreateDTO dto);

    /**
     * Maps Usuario entity to UsuarioResponseDTO.
     */
    UsuarioResponseDTO toDto(Usuario entity);

    /**
     * Updates an existing Usuario entity with data from UsuarioUpdateDTO.
     * Only updates non-null fields.
     */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntity(@MappingTarget Usuario entity, UsuarioUpdateDTO dto);
}