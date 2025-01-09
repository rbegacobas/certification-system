package com.certificationapp.certification_system.service.impl;

import com.certificationapp.certification_system.exception.ResourceNotFoundException;
import com.certificationapp.certification_system.model.Certificacion;
import com.certificationapp.certification_system.model.Usuario;
import com.certificationapp.certification_system.repository.CertificacionRepository;
import com.certificationapp.certification_system.service.CertificacionService;
import com.certificationapp.certification_system.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional

public class CertificacionServiceImpl implements CertificacionService{
    private final CertificacionRepository certificacionRepository;
    private final UsuarioService usuarioService;

    @Override
    public Certificacion crearCertificacion(Certificacion certificacion) {
        return certificacionRepository.save(certificacion);
    }

    @Override
    @Transactional(readOnly = true)
    public Certificacion obtenerCertificacionPorId(Long id) {
        return certificacionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Certificación no encontrada con id: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Certificacion> obtenerTodasLasCertificaciones() {
        return certificacionRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Certificacion> obtenerCertificacionesPorUsuario(Long usuarioId) {
        Usuario usuario = usuarioService.obtenerUsuarioPorId(usuarioId);
        return certificacionRepository.findByUsuario(usuario);
    }

    @Override
    public Certificacion actualizarEstadoCertificacion(Long id, Certificacion.Status nuevoEstado) {
        Certificacion certificacion = obtenerCertificacionPorId(id);
        certificacion.setStatus(nuevoEstado);
        return certificacionRepository.save(certificacion);
    }

    @Override
    public void eliminarCertificacion(Long id) {
        if (!certificacionRepository.existsById(id)) {
            throw new ResourceNotFoundException("Certificación no encontrada con id: " + id);
        }
        certificacionRepository.deleteById(id);
    }
}
