package com.certificationapp.certification_system.service;

import com.certificationapp.certification_system.model.Documento;
import java.util.List;

public interface DocumentoService {
    Documento guardarDocumento(Documento documento);
    Documento obtenerDocumentoPorId(Long id);
    List<Documento> obtenerDocumentosPorCertificacion(Long certificacionId);
    void eliminarDocumento(Long id);
}
