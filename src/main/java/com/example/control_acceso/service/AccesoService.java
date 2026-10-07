package com.example.control_acceso.service;

import com.example.control_acceso.model.LogAcceso;
import com.example.control_acceso.model.Usuario;
import com.example.control_acceso.repository.LogAccesoRepository;
import com.example.control_acceso.repository.UsuarioRepository;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AccesoService {

    private final UsuarioRepository usuarioRepository;
    private final LogAccesoRepository logRepository;

    public AccesoService(UsuarioRepository usuarioRepository,
                         LogAccesoRepository logRepository) {
        this.usuarioRepository = usuarioRepository;
        this.logRepository = logRepository;
    }

    public Optional<Usuario> verificarAcceso(String uid) {

        Optional<Usuario> usuario =
                usuarioRepository.findByUidAndActivoTrue(uid);

        if (usuario.isPresent()) {
            logRepository.save(new LogAcceso(uid, "CONCEDIDO"));
        } else {
            logRepository.save(new LogAcceso(uid, "DENEGADO"));
        }

        return usuario;
    }

    public Usuario agregarUsuario(String uid, String nombre) {

        Optional<Usuario> existente =
                usuarioRepository.findByUid(uid);

        if (existente.isPresent()) {

            Usuario usuario = existente.get();

            usuario.setNombre(nombre);
            usuario.setActivo(true);

            Usuario actualizado = usuarioRepository.save(usuario);

            logRepository.save(new LogAcceso(uid, "ALTA"));

            return actualizado;
        }

        Usuario nuevo = new Usuario(uid, nombre);

        Usuario guardado = usuarioRepository.save(nuevo);

        logRepository.save(new LogAcceso(uid, "ALTA"));

        return guardado;
    }

    public boolean eliminarUsuario(String uid) {

        Optional<Usuario> usuario =
                usuarioRepository.findByUidAndActivoTrue(uid);

        if (usuario.isEmpty()) {
            return false;
        }

        usuario.get().setActivo(false);

        usuarioRepository.save(usuario.get());

        logRepository.save(new LogAcceso(uid, "BAJA"));

        return true;
    }

    public List<Usuario> obtenerUsuarios() {

        return usuarioRepository.findAll()
                .stream()
                .filter(Usuario::isActivo)
                .toList();
    }

    public List<LogAcceso> obtenerLogs() {

        return logRepository.findAll();
    }
}