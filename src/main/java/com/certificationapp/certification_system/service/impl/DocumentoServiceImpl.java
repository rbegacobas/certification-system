package com.certificationapp.certification_system.service.impl;

import com.certificationapp.certification_system.exception.ResourceNotFoundException;
import com.certificationapp.certification_system.model.Certificacion;
import com.certificationapp.certification_system.model.Documento;
import com.certificationapp.certification_system.repository.DocumentoRepository;
import com.certificationapp.certification_system.service.CertificacionService;
import com.certificationapp.certification_system.service.DocumentoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional

public class DocumentoServiceImpl implements DocumentoService {

    private final DocumentoRepository documentoRepository;
    private final CertificacionService certificacionService;

    @Override
    public Documento guardarDocumento(Documento documento) {
        return documentoRepository.save(documento);
    }

    @Override
    @Transactional(readOnly = true)
    public Documento obtenerDocumentoPorId(Long id) {
        return documentoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Documento no encontrado con id: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Documento> obtenerDocumentosPorCertificacion(Long certificacionId) {
        Certificacion certificacion = certificacionService.obtenerCertificacionPorId(certificacionId);
        return documentoRepository.findByCertificacion(certificacion);
    }

    @Override
    public void eliminarDocumento(Long id) {
        if (!documentoRepository.existsById(id)) {
            throw new ResourceNotFoundException("Documento no encontrado con id: " + id);
        }
        documentoRepository.deleteById(id);
    }

}
