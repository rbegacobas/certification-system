package com.certificationapp.certification_system.service;

import com.certificationapp.certification_system.dto.UsuarioCreateDTO;
import com.certificationapp.certification_system.exception.ResourceNotFoundException;
import com.certificationapp.certification_system.model.Usuario;
import com.certificationapp.certification_system.repository.UsuarioRepository;
import com.certificationapp.certification_system.service.impl.UsuarioServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UsuarioServiceImpl usuarioService;

    private Usuario usuarioTest;
    private UsuarioCreateDTO createDTO;

    @BeforeEach
    void setUp() {
        // Configuración inicial de datos de prueba
        usuarioTest = new Usuario();
        usuarioTest.setId(1L);
        usuarioTest.setUsername("testuser");
        usuarioTest.setEmail("test@example.com");
        usuarioTest.setPassword("hashedPassword");
        usuarioTest.setRole(Usuario.Role.USER);

        createDTO = new UsuarioCreateDTO();
        createDTO.setUsername("newuser");
        createDTO.setEmail("new@example.com");
        createDTO.setPassword("password123");
    }

    @Test
    @DisplayName("Crear usuario exitosamente cuando los datos son válidos")
    void crearUsuario_ConDatosValidos_RetornaUsuarioCreado() {
        // Arrange
        when(usuarioRepository.existsByEmail(anyString())).thenReturn(false);
        when(usuarioRepository.existsByUsername(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("hashedPassword");
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioTest);

        // Act
        Usuario resultado = usuarioService.crearUsuario(createDTO);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.getUsername()).isEqualTo(usuarioTest.getUsername());
        assertThat(resultado.getEmail()).isEqualTo(usuarioTest.getEmail());
        assertThat(resultado.getRole()).isEqualTo(Usuario.Role.USER);
    }

    @Test
    @DisplayName("Crear usuario falla cuando el email ya existe")
    void crearUsuario_EmailExistente_LanzaExcepcion() {
        // Arrange
        when(usuarioRepository.existsByEmail(anyString())).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> usuarioService.crearUsuario(createDTO))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Email already registered");
    }

    @Test
    @DisplayName("Obtener usuario por ID exitosamente")
    void obtenerUsuarioPorId_IdExistente_RetornaUsuario() {
        // Arrange
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioTest));

        // Act
        Usuario resultado = usuarioService.obtenerUsuarioPorId(1L);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.getId()).isEqualTo(1L);
        assertThat(resultado.getUsername()).isEqualTo(usuarioTest.getUsername());
    }

    @Test
    @DisplayName("Obtener usuario por ID inexistente lanza excepción")
    void obtenerUsuarioPorId_IdInexistente_LanzaExcepcion() {
        // Arrange
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> usuarioService.obtenerUsuarioPorId(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Usuario no encontrado con id: 99");
    }

    @Test
    @DisplayName("Obtener todos los usuarios exitosamente")
    void obtenerTodosLosUsuarios_ExistenUsuarios_RetornaLista() {
        // Arrange
        Usuario usuario2 = new Usuario();
        usuario2.setId(2L);
        usuario2.setUsername("testuser2");

        List<Usuario> usuarios = Arrays.asList(usuarioTest, usuario2);
        when(usuarioRepository.findAll()).thenReturn(usuarios);

        // Act
        List<Usuario> resultado = usuarioService.obtenerTodosLosUsuarios();

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado).hasSize(2);
        assertThat(resultado.get(0).getUsername()).isEqualTo("testuser");
        assertThat(resultado.get(1).getUsername()).isEqualTo("testuser2");
    }

    @Test
    @DisplayName("Verificar existencia de email")
    void existeEmail_EmailExistente_RetornaTrue() {
        // Arrange
        String email = "test@example.com";
        when(usuarioRepository.existsByEmail(email)).thenReturn(true);

        // Act
        boolean resultado = usuarioService.existeEmail(email);

        // Assert
        assertThat(resultado).isTrue();
    }

    @Test
    @DisplayName("Verificar existencia de username")
    void existeUsername_UsernameExistente_RetornaTrue() {
        // Arrange
        String username = "testuser";
        when(usuarioRepository.existsByUsername(username)).thenReturn(true);

        // Act
        boolean resultado = usuarioService.existeUsername(username);

        // Assert
        assertThat(resultado).isTrue();
    }
}