package com.certificationapp.certification_system.service;

import com.certificationapp.certification_system.exception.ResourceNotFoundException;
import com.certificationapp.certification_system.model.Certificacion;
import com.certificationapp.certification_system.model.Documento;
import com.certificationapp.certification_system.model.Usuario;
import com.certificationapp.certification_system.repository.DocumentoRepository;
import com.certificationapp.certification_system.service.impl.DocumentoServiceImpl;
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
class DocumentoServiceTest {

    @Mock
    private DocumentoRepository documentoRepository;

    @Mock
    private CertificacionService certificacionService;

    @InjectMocks
    private DocumentoServiceImpl documentoService;

    private Documento documentoTest;
    private Certificacion certificacionTest;
    private Usuario usuarioTest;

    @BeforeEach
    void setUp() {
        usuarioTest = new Usuario();
        usuarioTest.setId(1L);
        usuarioTest.setUsername("testuser");

        certificacionTest = new Certificacion();
        certificacionTest.setId(1L);
        certificacionTest.setUsuario(usuarioTest);
        certificacionTest.setTipo("PROFESIONAL");
        certificacionTest.setStatus(Certificacion.Status.PENDING);

        documentoTest = new Documento();
        documentoTest.setId(1L);
        documentoTest.setCertificacion(certificacionTest);
        documentoTest.setNombre("test-document.pdf");
        documentoTest.setTipo("application/pdf");
        documentoTest.setUrl("abc-123-xyz.pdf");
        documentoTest.setFechaSubida(LocalDateTime.now());
    }

    @Test
    @DisplayName("Guardar documento exitosamente")
    void guardarDocumento_DatosValidos_RetornaDocumento() {
        when(documentoRepository.save(any(Documento.class))).thenReturn(documentoTest);

        Documento resultado = documentoService.guardarDocumento(documentoTest);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getNombre()).isEqualTo("test-document.pdf");
        assertThat(resultado.getTipo()).isEqualTo("application/pdf");
        assertThat(resultado.getCertificacion().getId()).isEqualTo(1L);
        verify(documentoRepository).save(any(Documento.class));
    }

    @Test
    @DisplayName("Obtener documento por ID existente")
    void obtenerDocumentoPorId_IdExistente_RetornaDocumento() {
        when(documentoRepository.findById(1L)).thenReturn(Optional.of(documentoTest));

        Documento resultado = documentoService.obtenerDocumentoPorId(1L);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getId()).isEqualTo(1L);
        assertThat(resultado.getNombre()).isEqualTo(documentoTest.getNombre());
    }

    @Test
    @DisplayName("Obtener documento por ID inexistente lanza excepción")
    void obtenerDocumentoPorId_IdInexistente_LanzaExcepcion() {
        when(documentoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> documentoService.obtenerDocumentoPorId(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Documento no encontrado con id: 99");
    }

    @Test
    @DisplayName("Obtener documentos por certificación")
    void obtenerDocumentosPorCertificacion_CertificacionExistente_RetornaLista() {
        Documento otroDocumento = new Documento();
        otroDocumento.setId(2L);
        otroDocumento.setCertificacion(certificacionTest);
        otroDocumento.setNombre("otro-documento.pdf");

        when(certificacionService.obtenerCertificacionPorId(1L)).thenReturn(certificacionTest);
        when(documentoRepository.findByCertificacion(certificacionTest))
                .thenReturn(Arrays.asList(documentoTest, otroDocumento));

        List<Documento> resultados = documentoService.obtenerDocumentosPorCertificacion(1L);

        assertThat(resultados).hasSize(2);
        assertThat(resultados.get(0).getNombre()).isEqualTo("test-document.pdf");
        assertThat(resultados.get(1).getNombre()).isEqualTo("otro-documento.pdf");
    }

    @Test
    @DisplayName("Eliminar documento exitosamente")
    void eliminarDocumento_IdExistente_EliminaDocumento() {
        when(documentoRepository.existsById(1L)).thenReturn(true);
        doNothing().when(documentoRepository).deleteById(1L);

        documentoService.eliminarDocumento(1L);

        verify(documentoRepository).deleteById(1L);
    }

    @Test
    @DisplayName("Eliminar documento inexistente lanza excepción")
    void eliminarDocumento_IdInexistente_LanzaExcepcion() {
        when(documentoRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> documentoService.eliminarDocumento(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Documento no encontrado con id: 99");
    }

    @Test
    @DisplayName("Guardar documento con tipo no permitido lanza excepción")
    void guardarDocumento_TipoNoPermitido_LanzaExcepcion() {
        documentoTest.setTipo("application/exe");

        assertThatThrownBy(() -> documentoService.guardarDocumento(documentoTest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Tipo de documento no permitido");
    }

    @Test
    @DisplayName("Guardar documento sin certificación lanza excepción")
    void guardarDocumento_SinCertificacion_LanzaExcepcion() {
        documentoTest.setCertificacion(null);

        assertThatThrownBy(() -> documentoService.guardarDocumento(documentoTest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("La certificación es obligatoria");
    }

    @Test
    @DisplayName("Verificar tipos de archivo permitidos")
    void guardarDocumento_TiposPermitidos_GuardaExitosamente() {
        String[] tiposPermitidos = {
                "application/pdf",
                "image/jpeg",
                "image/png",
                "application/msword",
                "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
        };

        for (String tipo : tiposPermitidos) {
            documentoTest.setTipo(tipo);
            when(documentoRepository.save(any(Documento.class))).thenReturn(documentoTest);

            Documento resultado = documentoService.guardarDocumento(documentoTest);
            assertThat(resultado).isNotNull();
            assertThat(resultado.getTipo()).isEqualTo(tipo);
        }
    }
}