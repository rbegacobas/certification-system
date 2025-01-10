package com.certificationapp.certification_system.security;

import com.certificationapp.certification_system.model.Documento;
import com.certificationapp.certification_system.model.Usuario;
import com.certificationapp.certification_system.service.CertificacionService;
import com.certificationapp.certification_system.service.DocumentoService;
import com.certificationapp.certification_system.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component("documentoSecurity")
@RequiredArgsConstructor
public class DocumentoSecurity {

    private final DocumentoService documentoService;
    private final UsuarioService usuarioService;
    private final CertificacionService certificacionService;

    public boolean canUploadDocument(Long certificacionId, Authentication authentication) {
        Usuario usuario = usuarioService.obtenerUsuarioPorUsername(authentication.getName());
        return usuario.getRole() == Usuario.Role.ADMIN ||
                certificacionService.obtenerCertificacionPorId(certificacionId)
                        .getUsuario().getId().equals(usuario.getId());
    }

    public boolean canAccessDocument(Long documentId, Authentication authentication) {
        Usuario usuario = usuarioService.obtenerUsuarioPorUsername(authentication.getName());
        Documento documento = documentoService.obtenerDocumentoPorId(documentId);

        return usuario.getRole() == Usuario.Role.ADMIN ||
                documento.getCertificacion().getUsuario().getId().equals(usuario.getId());
    }

    public boolean canAccessCertificationDocuments(Long certificacionId, Authentication authentication) {
        Usuario usuario = usuarioService.obtenerUsuarioPorUsername(authentication.getName());
        return usuario.getRole() == Usuario.Role.ADMIN ||
                certificacionService.obtenerCertificacionPorId(certificacionId)
                        .getUsuario().getId().equals(usuario.getId());
    }

    public boolean canDeleteDocument(Long documentId, Authentication authentication) {
        Usuario usuario = usuarioService.obtenerUsuarioPorUsername(authentication.getName());
        Documento documento = documentoService.obtenerDocumentoPorId(documentId);

        return usuario.getRole() == Usuario.Role.ADMIN ||
                documento.getCertificacion().getUsuario().getId().equals(usuario.getId());
    }
}