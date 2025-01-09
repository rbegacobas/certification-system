package com.certificationapp.certification_system.service.impl;

import com.certificationapp.certification_system.dto.UsuarioCreateDTO;
import com.certificationapp.certification_system.exception.ResourceNotFoundException;
import com.certificationapp.certification_system.model.Usuario;
import com.certificationapp.certification_system.repository.UsuarioRepository;
import com.certificationapp.certification_system.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class UsuarioServiceImpl implements UsuarioService{
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;


    public UsuarioServiceImpl(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }



    @Override
    public Usuario crearUsuario(UsuarioCreateDTO createDTO) {
        if (usuarioRepository.existsByEmail(createDTO.getEmail())) {
            throw new IllegalArgumentException("Email already registered");
        }
        if (usuarioRepository.existsByUsername(createDTO.getUsername())) {
            throw new IllegalArgumentException("Username already taken");
        }

        Usuario usuario = new Usuario();
        usuario.setUsername(createDTO.getUsername());
        usuario.setEmail(createDTO.getEmail());
        usuario.setPassword(passwordEncoder.encode(createDTO.getPassword()));
        usuario.setRole(Usuario.Role.USER);  // Por defecto, role USER

        return usuarioRepository.save(usuario);
    }

    @Override
    @Transactional(readOnly = true)
    public Usuario obtenerUsuarioPorId(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public Usuario obtenerUsuarioPorUsername(String username) {
        return usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con username: " + username));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Usuario> obtenerTodosLosUsuarios() {
        return usuarioRepository.findAll();
    }

    @Override
    public Usuario actualizarUsuario(Long id, Usuario usuarioDetails) {
        Usuario usuario = obtenerUsuarioPorId(id);

        if (!usuario.getEmail().equals(usuarioDetails.getEmail()) &&
                usuarioRepository.existsByEmail(usuarioDetails.getEmail())) {
            throw new IllegalArgumentException("El email ya está registrado");
        }

        if (!usuario.getUsername().equals(usuarioDetails.getUsername()) &&
                usuarioRepository.existsByUsername(usuarioDetails.getUsername())) {
            throw new IllegalArgumentException("El username ya está en uso");
        }

        usuario.setUsername(usuarioDetails.getUsername());
        usuario.setEmail(usuarioDetails.getEmail());

        return usuarioRepository.save(usuario);
    }

    @Override
    public void eliminarUsuario(Long id) {
        if (!usuarioRepository.existsById(id)) {
            throw new ResourceNotFoundException("Usuario no encontrado con id: " + id);
        }
        usuarioRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existeEmail(String email) {
        return usuarioRepository.existsByEmail(email);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existeUsername(String username) {
        return usuarioRepository.existsByUsername(username);
    }
}
