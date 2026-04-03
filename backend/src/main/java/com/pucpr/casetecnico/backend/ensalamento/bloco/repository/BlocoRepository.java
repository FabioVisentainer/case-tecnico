package com.pucpr.casetecnico.backend.ensalamento.bloco.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import com.pucpr.casetecnico.backend.ensalamento.bloco.model.Bloco;

public interface BlocoRepository extends JpaRepository<Bloco, Long>, JpaSpecificationExecutor<Bloco> {
	Optional<Bloco> findByNomeIgnoreCase(String nome);

	boolean existsByNomeIgnoreCase(String nome);

	boolean existsByNomeIgnoreCaseAndIdNot(String nome, Long id);
}

