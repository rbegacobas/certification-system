package com.certificationapp.certification_system.controller;

import com.certificationapp.certification_system.common.ApiResponse;
import com.certificationapp.certification_system.dto.UsuarioCreateDTO;
import com.certificationapp.certification_system.dto.UsuarioResponseDTO;
import com.certificationapp.certification_system.dto.UsuarioUpdateDTO;
import com.certificationapp.certification_system.mapper.UsuarioMapper;
import com.certificationapp.certification_system.service.UsuarioService;
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
 * REST controller for managing users.
 * Implements standard CRUD operations and follows REST best practices.
 */
@RestController
@RequestMapping("/api/v1/users")
@Tag(name = "User Management", description = "APIs for managing users")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final UsuarioMapper usuarioMapper;

    @PostMapping
    @Operation(summary = "Create a new user", description = "Creates a new user with the provided data")
    public ResponseEntity<ApiResponse<UsuarioResponseDTO>> createUser(
            @Valid @RequestBody UsuarioCreateDTO createDTO) {

        var createdUsuario = usuarioService.crearUsuario(createDTO);
        var responseDTO = usuarioMapper.toDto(createdUsuario);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(responseDTO, "User created successfully"));
    }

    /**
     * Retrieves a user by ID.
     *
     * @param id the user ID
     * @return the user details
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get user by ID", description = "Retrieves user details by their ID")
    public ResponseEntity<ApiResponse<UsuarioResponseDTO>> getUserById(
            @Parameter(description = "User ID") @PathVariable Long id) {

        var usuario = usuarioService.obtenerUsuarioPorId(id);
        var responseDTO = usuarioMapper.toDto(usuario);

        return ResponseEntity.ok(ApiResponse.success(responseDTO, "User retrieved successfully"));
    }

    /**
     * Lists all users.
     *
     * @return list of all users
     */
    @GetMapping
    @Operation(summary = "List all users", description = "Retrieves a list of all users")
    public ResponseEntity<ApiResponse<List<UsuarioResponseDTO>>> getAllUsers() {
        var usuarios = usuarioService.obtenerTodosLosUsuarios();
        var responseDTOs = usuarios.stream()
                .map(usuarioMapper::toDto)
                .collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.success(responseDTOs, "Users retrieved successfully"));
    }

    /**
     * Updates a user.
     *
     * @param id the user ID
     * @param updateDTO the updated user data
     * @return the updated user
     */
    @PutMapping("/{id}")
    @Operation(summary = "Update user", description = "Updates user details for the specified ID")
    public ResponseEntity<ApiResponse<UsuarioResponseDTO>> updateUser(
            @Parameter(description = "User ID") @PathVariable Long id,
            @Valid @RequestBody UsuarioUpdateDTO updateDTO) {

        var usuario = usuarioService.obtenerUsuarioPorId(id);
        usuarioMapper.updateEntity(usuario, updateDTO);
        var updatedUsuario = usuarioService.actualizarUsuario(id, usuario);
        var responseDTO = usuarioMapper.toDto(updatedUsuario);

        return ResponseEntity.ok(ApiResponse.success(responseDTO, "User updated successfully"));
    }

    /**
     * Deletes a user.
     *
     * @param id the user ID
     * @return confirmation response
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete user", description = "Deletes the user with the specified ID")
    public ResponseEntity<ApiResponse<Void>> deleteUser(
            @Parameter(description = "User ID") @PathVariable Long id) {

        usuarioService.eliminarUsuario(id);
        return ResponseEntity.ok(ApiResponse.success(null, "User deleted successfully"));
    }
}