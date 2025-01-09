package com.certificationapp.certification_system.repository;


import com.certificationapp.certification_system.model.Documento;
import com.certificationapp.certification_system.model.Certificacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocumentoRepository extends JpaRepository<Documento, Long>{
    List<Documento> findByCertificacion(Certificacion certificacion);
    void deleteByCertificacion(Certificacion certificacion);
}
