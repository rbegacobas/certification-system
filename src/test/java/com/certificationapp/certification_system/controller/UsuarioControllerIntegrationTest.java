package com.certificationapp.certification_system.controller;

import com.certificationapp.certification_system.config.SecurityConfigTest;
import com.certificationapp.certification_system.dto.UsuarioCreateDTO;
import com.certificationapp.certification_system.dto.UsuarioUpdateDTO;
import com.certificationapp.certification_system.model.Usuario;
import com.certificationapp.certification_system.repository.UsuarioRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import static org.hamcrest.Matchers.containsString;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(SecurityConfigTest.class)
class UsuarioControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @BeforeEach
    void setup() {
        usuarioRepository.deleteAll();
    }

    @Test
    @DisplayName("Crear usuario exitosamente")
    void crearUsuario_DatosValidos_RetornaCreado() throws Exception {
        // Arrange
        UsuarioCreateDTO createDTO = new UsuarioCreateDTO();
        createDTO.setUsername("testuser");
        createDTO.setEmail("test@example.com");
        createDTO.setPassword("password123");

        // Act
        ResultActions response = mockMvc.perform(post("/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createDTO)));

        // Assert
        response.andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.username", is(createDTO.getUsername())))
                .andExpect(jsonPath("$.data.email", is(createDTO.getEmail())))
                .andExpect(jsonPath("$.data.role", is("USER")));
    }

    @Test
    @DisplayName("Crear usuario con email duplicado retorna error")
    void crearUsuario_EmailDuplicado_RetornaError() throws Exception {
        // Arrange
        UsuarioCreateDTO createDTO = new UsuarioCreateDTO();
        createDTO.setUsername("testuser");
        createDTO.setEmail("test@example.com");
        createDTO.setPassword("password123");

        mockMvc.perform(post("/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDTO)))
                .andExpect(status().isCreated());

        // Intentar crear segundo usuario con mismo email
        createDTO.setUsername("otheruser");
        ResultActions response = mockMvc.perform(post("/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createDTO)));

        // Assert
        response.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Email already registered"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Obtener usuario por ID existente")
    void obtenerUsuario_IdExistente_RetornaUsuario() throws Exception {
        // Arrange
        Usuario usuario = new Usuario();
        usuario.setUsername("testuser");
        usuario.setEmail("test@example.com");
        usuario.setPassword("hashedpassword");
        usuario.setRole(Usuario.Role.USER);
        usuarioRepository.save(usuario);

        // Act
        ResultActions response = mockMvc.perform(get("/v1/users/{id}", usuario.getId()));

        // Assert
        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.username", is(usuario.getUsername())))
                .andExpect(jsonPath("$.data.email", is(usuario.getEmail())));
    }

    @Test
    @DisplayName("Obtener usuario por ID inexistente retorna error")
    void obtenerUsuario_IdInexistente_RetornaError() throws Exception {
        // Act
        ResultActions response = mockMvc.perform(get("/v1/users/{id}", 99L));

        // Assert
        response.andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorDetails").value("Usuario no encontrado con id: 99"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Actualizar usuario exitosamente")
    void actualizarUsuario_DatosValidos_RetornaActualizado() throws Exception {
        // Arrange
        Usuario usuario = new Usuario();
        usuario.setUsername("oldusername");
        usuario.setEmail("old@example.com");
        usuario.setPassword("hashedpassword");
        usuario.setRole(Usuario.Role.USER);
        usuarioRepository.save(usuario);

        UsuarioUpdateDTO updateDTO = new UsuarioUpdateDTO();
        updateDTO.setUsername("newusername");
        updateDTO.setEmail("new@example.com");

        // Act
        ResultActions response = mockMvc.perform(put("/v1/users/{id}", usuario.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDTO)));

        // Assert
        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.username", is(updateDTO.getUsername())))
                .andExpect(jsonPath("$.data.email", is(updateDTO.getEmail())));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Eliminar usuario exitosamente")
    void eliminarUsuario_IdExistente_RetornaExito() throws Exception {
        // Arrange
        Usuario usuario = new Usuario();
        usuario.setUsername("testuser");
        usuario.setEmail("test@example.com");
        usuario.setPassword("hashedpassword");
        usuario.setRole(Usuario.Role.USER);
        usuarioRepository.save(usuario);

        // Act
        ResultActions response = mockMvc.perform(delete("/v1/users/{id}", usuario.getId()));

        // Assert
        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(containsString("User deleted successfully")));
    }
}