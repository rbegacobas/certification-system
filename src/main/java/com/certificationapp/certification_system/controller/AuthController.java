package com.certificationapp.certification_system.controller;

import com.certificationapp.certification_system.common.ApiResponse;
import com.certificationapp.certification_system.dto.auth.JwtResponseDTO;
import com.certificationapp.certification_system.dto.auth.LoginRequestDTO;
import com.certificationapp.certification_system.dto.UsuarioCreateDTO;
import com.certificationapp.certification_system.model.Usuario;
import com.certificationapp.certification_system.security.JwtTokenProvider;
import com.certificationapp.certification_system.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for handling authentication requests.
 * Provides endpoints for user registration and login.
 */

@RestController
@RequestMapping("/v1/auth")
@Tag(name = "Authentication", description = "Authentication APIs")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UsuarioService usuarioService;
    private final JwtTokenProvider tokenProvider;

    @PostMapping("/login")
    @Operation(summary = "Login user", description = "Authenticate a user and return JWT token")
    public ResponseEntity<ApiResponse<JwtResponseDTO>> login(@Valid @RequestBody LoginRequestDTO loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = tokenProvider.generateToken(authentication);
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Usuario usuario = usuarioService.obtenerUsuarioPorUsername(userDetails.getUsername());

        JwtResponseDTO response = new JwtResponseDTO(
                jwt,
                "Bearer",
                usuario.getId(),
                usuario.getUsername(),
                usuario.getEmail(),
                usuario.getRole().name()
        );

        return ResponseEntity.ok(ApiResponse.success(response, "Login successful"));
    }

    @PostMapping("/register")
    @Operation(summary = "Register user", description = "Register a new user")
    public ResponseEntity<ApiResponse<JwtResponseDTO>> register(@Valid @RequestBody UsuarioCreateDTO createDTO) {
        Usuario usuario = usuarioService.crearUsuario(createDTO);

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(createDTO.getUsername(), createDTO.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = tokenProvider.generateToken(authentication);

        JwtResponseDTO response = new JwtResponseDTO(
                jwt,
                "Bearer",
                usuario.getId(),
                usuario.getUsername(),
                usuario.getEmail(),
                usuario.getRole().name()
        );

        return ResponseEntity.ok(ApiResponse.success(response, "User registered successfully"));
    }
}