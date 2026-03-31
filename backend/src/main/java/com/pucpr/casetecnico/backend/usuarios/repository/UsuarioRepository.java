package com.pucpr.casetecnico.backend.usuarios.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.pucpr.casetecnico.backend.usuarios.model.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByNome(String nome);

    boolean existsByNome(String nome);
}


