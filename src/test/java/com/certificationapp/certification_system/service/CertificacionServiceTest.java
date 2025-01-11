package com.certificationapp.certification_system.service;

import com.certificationapp.certification_system.exception.ResourceNotFoundException;
import com.certificationapp.certification_system.model.Certificacion;
import com.certificationapp.certification_system.model.Usuario;
import com.certificationapp.certification_system.repository.CertificacionRepository;
import com.certificationapp.certification_system.service.impl.CertificacionServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CertificacionServiceTest {

    @Mock
    private CertificacionRepository certificacionRepository;

    @Mock
    private UsuarioService usuarioService;

    @InjectMocks
    private CertificacionServiceImpl certificacionService;

    private Certificacion certificacionTest;
    private Usuario usuarioTest;

    @BeforeEach
    void setUp() {
        usuarioTest = new Usuario();
        usuarioTest.setId(1L);
        usuarioTest.setUsername("testuser");
        usuarioTest.setEmail("test@example.com");
        usuarioTest.setRole(Usuario.Role.USER);

        certificacionTest = new Certificacion();
        certificacionTest.setId(1L);
        certificacionTest.setUsuario(usuarioTest);
        certificacionTest.setTipo("PROFESIONAL");
        certificacionTest.setStatus(Certificacion.Status.PENDING);
        certificacionTest.setFechaCreacion(LocalDateTime.now());
    }

    @Test
    @DisplayName("Crear certificación exitosamente")
    void crearCertificacion_DatosValidos_RetornaCertificacion() {
        when(usuarioService.obtenerUsuarioPorId(1L)).thenReturn(usuarioTest);
        when(certificacionRepository.save(any(Certificacion.class))).thenReturn(certificacionTest);

        Certificacion resultado = certificacionService.crearCertificacion(certificacionTest);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getTipo()).isEqualTo("PROFESIONAL");
        assertThat(resultado.getStatus()).isEqualTo(Certificacion.Status.PENDING);
        assertThat(resultado.getUsuario().getId()).isEqualTo(1L);
        verify(certificacionRepository).save(any(Certificacion.class));
    }

    @Test
    @DisplayName("Obtener certificación por ID existente")
    void obtenerCertificacionPorId_IdExistente_RetornaCertificacion() {
        when(certificacionRepository.findById(1L)).thenReturn(Optional.of(certificacionTest));

        Certificacion resultado = certificacionService.obtenerCertificacionPorId(1L);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getId()).isEqualTo(1L);
        assertThat(resultado.getTipo()).isEqualTo(certificacionTest.getTipo());
    }

    @Test
    @DisplayName("Obtener certificación por ID inexistente lanza excepción")
    void obtenerCertificacionPorId_IdInexistente_LanzaExcepcion() {
        when(certificacionRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> certificacionService.obtenerCertificacionPorId(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Certificación no encontrada con id: 99");
    }

    @Test
    @DisplayName("Obtener todas las certificaciones")
    void obtenerTodasLasCertificaciones_ExistenCertificaciones_RetornaLista() {
        Certificacion otraCertificacion = new Certificacion();
        otraCertificacion.setId(2L);
        otraCertificacion.setUsuario(usuarioTest);
        otraCertificacion.setTipo("TECNICA");

        when(certificacionRepository.findAll()).thenReturn(Arrays.asList(certificacionTest, otraCertificacion));

        List<Certificacion> resultados = certificacionService.obtenerTodasLasCertificaciones();

        assertThat(resultados).hasSize(2);
        assertThat(resultados.get(0).getTipo()).isEqualTo("PROFESIONAL");
        assertThat(resultados.get(1).getTipo()).isEqualTo("TECNICA");
    }

    @Test
    @DisplayName("Obtener certificaciones por usuario")
    void obtenerCertificacionesPorUsuario_UsuarioExistente_RetornaLista() {
        when(usuarioService.obtenerUsuarioPorId(1L)).thenReturn(usuarioTest);
        when(certificacionRepository.findByUsuario(usuarioTest))
                .thenReturn(Arrays.asList(certificacionTest));

        List<Certificacion> resultados = certificacionService.obtenerCertificacionesPorUsuario(1L);

        assertThat(resultados).hasSize(1);
        assertThat(resultados.get(0).getUsuario().getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Actualizar estado de certificación exitosamente")
    void actualizarEstadoCertificacion_EstadoValido_RetornaCertificacionActualizada() {
        when(certificacionRepository.findById(1L)).thenReturn(Optional.of(certificacionTest));
        when(certificacionRepository.save(any(Certificacion.class))).thenAnswer(invocation -> {
            Certificacion cert = invocation.getArgument(0);
            cert.setStatus(Certificacion.Status.IN_REVIEW);
            return cert;
        });

        Certificacion resultado = certificacionService.actualizarEstadoCertificacion(1L, Certificacion.Status.IN_REVIEW);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getStatus()).isEqualTo(Certificacion.Status.IN_REVIEW);
        verify(certificacionRepository).save(any(Certificacion.class));
    }

    @Test
    @DisplayName("Eliminar certificación exitosamente")
    void eliminarCertificacion_IdExistente_EliminaCertificacion() {
        when(certificacionRepository.existsById(1L)).thenReturn(true);
        doNothing().when(certificacionRepository).deleteById(1L);

        certificacionService.eliminarCertificacion(1L);

        verify(certificacionRepository).deleteById(1L);
    }

    @Test
    @DisplayName("Eliminar certificación inexistente lanza excepción")
    void eliminarCertificacion_IdInexistente_LanzaExcepcion() {
        when(certificacionRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> certificacionService.eliminarCertificacion(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Certificación no encontrada con id: 99");
    }
}