package com.example.control_acceso.repository;

import com.example.control_acceso.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByUid(String uid);

    Optional<Usuario> findByUidAndActivoTrue(String uid);
}