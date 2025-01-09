package com.certificationapp.certification_system.repository;


import com.certificationapp.certification_system.model.Certificacion;
import com.certificationapp.certification_system.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CertificacionRepository extends JpaRepository<Certificacion, Long> {
    List<Certificacion> findByUsuario(Usuario usuario);
    List<Certificacion> findByStatus(Certificacion.Status status);
    List<Certificacion> findByUsuarioAndStatus(Usuario usuario, Certificacion.Status status);

}
