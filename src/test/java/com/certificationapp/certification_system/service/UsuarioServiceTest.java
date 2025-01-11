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
import static org.mockito.Mockito.*;

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
        when(usuarioRepository.existsByEmail(anyString())).thenReturn(false);
        when(usuarioRepository.existsByUsername(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("hashedPassword");
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioTest);

        Usuario resultado = usuarioService.crearUsuario(createDTO);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getUsername()).isEqualTo(usuarioTest.getUsername());
        assertThat(resultado.getEmail()).isEqualTo(usuarioTest.getEmail());
        assertThat(resultado.getRole()).isEqualTo(Usuario.Role.USER);
        verify(passwordEncoder).encode(anyString());
    }

    @Test
    @DisplayName("Crear usuario falla cuando el email ya existe")
    void crearUsuario_EmailExistente_LanzaExcepcion() {
        when(usuarioRepository.existsByEmail(anyString())).thenReturn(true);

        assertThatThrownBy(() -> usuarioService.crearUsuario(createDTO))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Email already registered");
    }

    @Test
    @DisplayName("Crear usuario falla cuando username ya existe")
    void crearUsuario_UsernameExistente_LanzaExcepcion() {
        when(usuarioRepository.existsByEmail(anyString())).thenReturn(false);
        when(usuarioRepository.existsByUsername(anyString())).thenReturn(true);

        assertThatThrownBy(() -> usuarioService.crearUsuario(createDTO))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Username already taken");
    }

    @Test
    @DisplayName("Actualizar usuario exitosamente")
    void actualizarUsuario_DatosValidos_RetornaUsuarioActualizado() {
        Usuario usuarioActualizado = new Usuario();
        usuarioActualizado.setId(1L);
        usuarioActualizado.setUsername("updateduser");
        usuarioActualizado.setEmail("updated@example.com");

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioTest));
        when(usuarioRepository.existsByEmail(anyString())).thenReturn(false);
        when(usuarioRepository.existsByUsername(anyString())).thenReturn(false);
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioActualizado);

        Usuario resultado = usuarioService.actualizarUsuario(1L, usuarioActualizado);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getUsername()).isEqualTo("updateduser");
        assertThat(resultado.getEmail()).isEqualTo("updated@example.com");
    }

    @Test
    @DisplayName("Eliminar usuario exitosamente")
    void eliminarUsuario_IdExistente_EliminaUsuario() {
        when(usuarioRepository.existsById(1L)).thenReturn(true);
        doNothing().when(usuarioRepository).deleteById(1L);

        usuarioService.eliminarUsuario(1L);

        verify(usuarioRepository).deleteById(1L);
    }

    @Test
    @DisplayName("Eliminar usuario con ID inexistente lanza excepción")
    void eliminarUsuario_IdInexistente_LanzaExcepcion() {
        when(usuarioRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> usuarioService.eliminarUsuario(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Usuario no encontrado con id: 99");
    }

    @Test
    @DisplayName("Validar contraseña cumple requisitos mínimos")
    void crearUsuario_ContraseñaInvalida_LanzaExcepcion() {
        createDTO.setPassword("123"); // contraseña muy corta

        assertThatThrownBy(() -> usuarioService.crearUsuario(createDTO))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Password must be at least 6 characters");
    }

    @Test
    @DisplayName("Crear usuario con rol específico")
    void crearUsuario_ConRolEspecifico_AsignaRolCorrecto() {
        when(usuarioRepository.existsByEmail(anyString())).thenReturn(false);
        when(usuarioRepository.existsByUsername(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("hashedPassword");

        Usuario usuarioAdmin = new Usuario();
        usuarioAdmin.setRole(Usuario.Role.ADMIN);
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioAdmin);

        Usuario resultado = usuarioService.crearUsuario(createDTO);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getRole()).isEqualTo(Usuario.Role.ADMIN);
    }

    @Test
    @DisplayName("Obtener usuario por ID exitosamente")
    void obtenerUsuarioPorId_IdExistente_RetornaUsuario() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioTest));

        Usuario resultado = usuarioService.obtenerUsuarioPorId(1L);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getId()).isEqualTo(1L);
        assertThat(resultado.getUsername()).isEqualTo(usuarioTest.getUsername());
    }

    @Test
    @DisplayName("Obtener todos los usuarios exitosamente")
    void obtenerTodosLosUsuarios_ExistenUsuarios_RetornaLista() {
        List<Usuario> usuarios = Arrays.asList(usuarioTest, new Usuario());
        when(usuarioRepository.findAll()).thenReturn(usuarios);

        List<Usuario> resultado = usuarioService.obtenerTodosLosUsuarios();

        assertThat(resultado).hasSize(2);
        verify(usuarioRepository).findAll();
    }

    @Test
    @DisplayName("Verificar existencia de username")
    void existeUsername_UsernameExistente_RetornaTrue() {
        when(usuarioRepository.existsByUsername("testuser")).thenReturn(true);

        boolean resultado = usuarioService.existeUsername("testuser");

        assertThat(resultado).isTrue();
    }

    @Test
    @DisplayName("Verificar existencia de email")
    void existeEmail_EmailExistente_RetornaTrue() {
        when(usuarioRepository.existsByEmail("test@example.com")).thenReturn(true);

        boolean resultado = usuarioService.existeEmail("test@example.com");

        assertThat(resultado).isTrue();
    }
}