package com.pucpr.casetecnico.backend.ensalamento.sala.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.pucpr.casetecnico.backend.ensalamento.sala.model.Sala;

public interface SalaRepository extends JpaRepository<Sala, Long>, JpaSpecificationExecutor<Sala> {
    
    @Query("SELECT CASE WHEN COUNT(s) > 0 THEN true ELSE false END FROM Sala s WHERE s.andar.id = :andarId AND s.nome = :nome AND s.id != :salaId")
    boolean existeNomeEmOutraSalaDoAndar(@Param("andarId") Long andarId, @Param("nome") String nome, @Param("salaId") Long salaId);
    
    @Query("SELECT CASE WHEN COUNT(s) > 0 THEN true ELSE false END FROM Sala s WHERE s.andar.id = :andarId AND s.nome = :nome")
    boolean existeNomeNoAndar(@Param("andarId") Long andarId, @Param("nome") String nome);

    boolean existsByAndarIdAndNomeIgnoreCase(Long andarId, String nome);

    boolean existsByAndarIdAndNomeIgnoreCaseAndIdNot(Long andarId, String nome, Long id);

    long countByAndarBlocoId(Long blocoId);

    List<Sala> findAllByAndarBlocoId(Long blocoId);

    List<Sala> findAllByAndarId(Long andarId);

    long countByAtivoTrueAndAndarAtivoTrueAndAndarBlocoAtivoTrue();

    @Query("""
            SELECT COALESCE(SUM(s.lotacaoAlunos + s.lotacaoProfessores), 0)
            FROM Sala s
            WHERE s.ativo = true
              AND s.andar.ativo = true
              AND s.andar.bloco.ativo = true
            """)
    Integer somarCapacidadeTotalAtiva();

    Optional<Sala> findByAndarIdAndNomeIgnoreCase(Long andarId, String nome);

    @Query("""
            SELECT s
            FROM Sala s
            JOIN FETCH s.andar a
            JOIN FETCH a.bloco b
            WHERE s.ativo = true
              AND a.ativo = true
              AND b.ativo = true
              AND (
                :q IS NULL
                OR :q = ''
                OR lower(s.nome) LIKE lower(concat('%', :q, '%'))
                OR lower(a.nome) LIKE lower(concat('%', :q, '%'))
                OR lower(b.nome) LIKE lower(concat('%', :q, '%'))
              )
            """)
    List<Sala> buscarSalasAtivasPorTermo(@Param("q") String q);

    @Query("""
            SELECT s
            FROM Sala s
            JOIN s.andar a
            JOIN a.bloco b
            WHERE s.ativo = true
              AND a.ativo = true
              AND b.ativo = true
              AND (
                :q IS NULL
                OR :q = ''
                OR lower(s.nome) LIKE lower(concat('%', :q, '%'))
                OR lower(a.nome) LIKE lower(concat('%', :q, '%'))
                OR lower(b.nome) LIKE lower(concat('%', :q, '%'))
              )
            """)
    Page<Sala> buscarSalasAtivasPorTermoPaginado(@Param("q") String q, Pageable pageable);
}