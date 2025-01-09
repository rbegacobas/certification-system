package com.certificationapp.certification_system.service;

import com.certificationapp.certification_system.dto.UsuarioCreateDTO;
import com.certificationapp.certification_system.model.Usuario;
import java.util.List;

public interface UsuarioService {
    Usuario crearUsuario(UsuarioCreateDTO createDTO);  // Cambiado aquí
    Usuario obtenerUsuarioPorId(Long id);
    Usuario obtenerUsuarioPorUsername(String username);
    List<Usuario> obtenerTodosLosUsuarios();
    Usuario actualizarUsuario(Long id, Usuario usuarioDetails);
    void eliminarUsuario(Long id);
    boolean existeEmail(String email);
    boolean existeUsername(String username);
}