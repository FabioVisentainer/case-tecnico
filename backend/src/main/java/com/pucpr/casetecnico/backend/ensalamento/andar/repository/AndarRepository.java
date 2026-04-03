package com.pucpr.casetecnico.backend.ensalamento.andar.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import com.pucpr.casetecnico.backend.ensalamento.andar.model.Andar;

public interface AndarRepository extends JpaRepository<Andar, Long>, JpaSpecificationExecutor<Andar> {
	Optional<Andar> findByBlocoIdAndNomeIgnoreCase(Long blocoId, String nome);

	boolean existsByBlocoIdAndNomeIgnoreCase(Long blocoId, String nome);

	boolean existsByBlocoIdAndNomeIgnoreCaseAndIdNot(Long blocoId, String nome, Long id);

	long countByBlocoId(Long blocoId);

	List<Andar> findAllByBlocoId(Long blocoId);
}

