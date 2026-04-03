package com.pucpr.casetecnico.backend.ensalamento.presenca.repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.pucpr.casetecnico.backend.ensalamento.presenca.model.PresencaSala;
import com.pucpr.casetecnico.backend.usuarios.model.EnumPapelUsuario;

public interface PresencaSalaRepository extends JpaRepository<PresencaSala, Long> {

    @Query("""
            SELECT p
            FROM PresencaSala p
            JOIN FETCH p.sala s
            JOIN FETCH s.andar a
            JOIN FETCH a.bloco b
            WHERE p.usuario.id = :usuarioId
              AND p.saida IS NULL
            """)
    Optional<PresencaSala> buscarAtivaPorUsuarioId(@Param("usuarioId") Long usuarioId);

    @Query("""
            SELECT p
            FROM PresencaSala p
            JOIN FETCH p.usuario u
            JOIN FETCH p.sala s
            JOIN FETCH s.andar a
            JOIN FETCH a.bloco b
            WHERE p.usuario.id IN :usuarioIds
              AND p.saida IS NULL
            """)
    List<PresencaSala> buscarAtivasPorUsuarioIds(@Param("usuarioIds") Collection<Long> usuarioIds);

    @Query("""
            SELECT p
            FROM PresencaSala p
            JOIN FETCH p.sala s
            JOIN FETCH s.andar a
            JOIN FETCH a.bloco b
            WHERE p.usuario.id = :usuarioId
            ORDER BY p.entrada DESC
            """)
    List<PresencaSala> listarHistoricoPorUsuario(@Param("usuarioId") Long usuarioId);

    @Query("""
            SELECT COUNT(p)
            FROM PresencaSala p
            WHERE p.sala.id = :salaId
              AND p.saida IS NULL
              AND p.usuario.papel = :papel
            """)
    long contarPresentesAtivosPorSalaEPapel(@Param("salaId") Long salaId, @Param("papel") EnumPapelUsuario papel);

    @Query("""
            SELECT p
            FROM PresencaSala p
            JOIN FETCH p.usuario u
            WHERE p.sala.id = :salaId
              AND p.saida IS NULL
              AND p.usuario.papel = :papel
            ORDER BY u.nome
            """)
    List<PresencaSala> listarPresentesAtivosPorSalaEPapel(@Param("salaId") Long salaId,
            @Param("papel") EnumPapelUsuario papel);
}