package com.pucpr.casetecnico.backend.ensalamento.andar.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.pucpr.casetecnico.backend.ensalamento.andar.dto.AndarCadastroRequest;
import com.pucpr.casetecnico.backend.ensalamento.andar.dto.AndarDetalhesResponse;
import com.pucpr.casetecnico.backend.ensalamento.andar.dto.AndarResponse;
import com.pucpr.casetecnico.backend.ensalamento.andar.dto.SalaResumoResponse;
import com.pucpr.casetecnico.backend.ensalamento.andar.model.Andar;
import com.pucpr.casetecnico.backend.ensalamento.andar.repository.AndarRepository;
import com.pucpr.casetecnico.backend.ensalamento.bloco.model.Bloco;
import com.pucpr.casetecnico.backend.ensalamento.bloco.repository.BlocoRepository;
import com.pucpr.casetecnico.backend.ensalamento.sala.repository.SalaRepository;

@Service
@RequiredArgsConstructor
public class AndarService {

    private final AndarRepository andarRepository;
    private final BlocoRepository blocoRepository;
    private final SalaRepository salaRepository;

    @Transactional
    public AndarResponse cadastrar(AndarCadastroRequest request) {
        Bloco bloco = blocoRepository.findById(request.blocoId())
            .orElseThrow(() -> new RuntimeException("Bloco não encontrado"));

        String nomeNormalizado = normalizarNome(request.nome());
        if (andarRepository.existsByBlocoIdAndNomeIgnoreCase(bloco.getId(), nomeNormalizado)) {
            throw new IllegalArgumentException("Já existe um andar com este nome neste bloco.");
        }
        
        Andar andar = Andar.builder()
            .nome(nomeNormalizado)
            .bloco(bloco)
            .ativo(true)
            .build();
        
        Andar saved = andarRepository.save(andar);
        return toAndarResponse(saved);
    }

    @Transactional(readOnly = true)
    public Page<AndarResponse> listarPorBlocoPaginado(Long blocoId, int page, int size) {
        blocoRepository.findById(blocoId)
            .orElseThrow(() -> new RuntimeException("Bloco não encontrado"));
        
        PageRequest pageable = PageRequest.of(page, size);
        Specification<Andar> spec = (root, query, cb) -> cb.equal(root.get("bloco").get("id"), blocoId);
        Page<Andar> andares = andarRepository.findAll(spec, pageable);
        
        return andares.map(this::toAndarResponse);
    }

    @Transactional(readOnly = true)
    public AndarDetalhesResponse buscarComDetalhes(Long id) {
        Andar andar = andarRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Andar não encontrado"));
        
        var salas = andar.getSalas().stream()
            .map(sala -> new SalaResumoResponse(
                sala.getId(),
                sala.getNome(),
                sala.getLotacaoAlunos(),
                sala.getLotacaoProfessores(),
                sala.isAtivo()
            ))
            .toList();
        
        return new AndarDetalhesResponse(
            andar.getId(),
            andar.getNome(),
            andar.getBloco().getId(),
            andar.isAtivo(),
            salas
        );
    }

    @Transactional
    public AndarResponse atualizar(Long id, AndarCadastroRequest request) {
        Andar andar = andarRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Andar não encontrado"));

        String nomeNormalizado = normalizarNome(request.nome());
        if (andarRepository.existsByBlocoIdAndNomeIgnoreCaseAndIdNot(request.blocoId(), nomeNormalizado, id)) {
            throw new IllegalArgumentException("Já existe um andar com este nome neste bloco.");
        }
        
        andar.setNome(nomeNormalizado);
        if (!andar.getBloco().getId().equals(request.blocoId())) {
            Bloco bloco = blocoRepository.findById(request.blocoId())
                .orElseThrow(() -> new IllegalArgumentException("Bloco não encontrado."));
            andar.setBloco(bloco);
        }
        Andar updated = andarRepository.save(andar);
        return toAndarResponse(updated);
    }

    @Transactional
    public void deletar(Long id) {
        alterarStatus(id, false);
    }

    @Transactional
    public AndarResponse alterarStatus(Long id, boolean ativo) {
        Andar andar = andarRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Andar não encontrado."));
        andar.setAtivo(ativo);
        salaRepository.findAllByAndarId(andar.getId()).forEach(sala -> sala.setAtivo(ativo));
        return toAndarResponse(andarRepository.save(andar));
    }

    private AndarResponse toAndarResponse(Andar andar) {
        return new AndarResponse(
            andar.getId(),
            andar.getNome(),
            andar.getBloco().getId(),
            andar.isAtivo()
        );
    }

    private String normalizarNome(String nome) {
        String nomeNormalizado = nome == null ? "" : nome.trim().replaceAll("\\s+", " ");
        if (nomeNormalizado.isBlank()) {
            throw new IllegalArgumentException("Nome do andar é obrigatório.");
        }
        return nomeNormalizado;
    }
}



