package com.certificationapp.certification_system.service;

import com.certificationapp.certification_system.model.Certificacion;
import java.util.List;

public interface CertificacionService {
    Certificacion crearCertificacion(Certificacion certificacion);
    Certificacion obtenerCertificacionPorId(Long id);
    List<Certificacion> obtenerTodasLasCertificaciones();
    List<Certificacion> obtenerCertificacionesPorUsuario(Long usuarioId);
    Certificacion actualizarEstadoCertificacion(Long id, Certificacion.Status nuevoEstado);
    void eliminarCertificacion(Long id);
}
