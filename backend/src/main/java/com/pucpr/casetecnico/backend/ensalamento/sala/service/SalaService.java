package com.pucpr.casetecnico.backend.ensalamento.sala.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.pucpr.casetecnico.backend.ensalamento.sala.dto.SalaCadastroRequest;
import com.pucpr.casetecnico.backend.ensalamento.sala.dto.SalaResponse;
import com.pucpr.casetecnico.backend.ensalamento.sala.model.Sala;
import com.pucpr.casetecnico.backend.ensalamento.sala.repository.SalaRepository;
import com.pucpr.casetecnico.backend.ensalamento.andar.model.Andar;
import com.pucpr.casetecnico.backend.ensalamento.andar.repository.AndarRepository;

@Service
@RequiredArgsConstructor
public class SalaService {

    private final SalaRepository salaRepository;
    private final AndarRepository andarRepository;

    @Transactional
    public SalaResponse cadastrar(SalaCadastroRequest request) {
        Andar andar = andarRepository.findById(request.andarId())
            .orElseThrow(() -> new RuntimeException("Andar não encontrado"));

        String nomeNormalizado = normalizarNome(request.nome());
        
        // Validar se já existe sala com o mesmo nome no andar
        if (salaRepository.existsByAndarIdAndNomeIgnoreCase(request.andarId(), nomeNormalizado)) {
            throw new RuntimeException("Já existe uma sala com este nome neste andar.");
        }
        
        Sala sala = Sala.builder()
            .nome(nomeNormalizado)
            .lotacaoAlunos(request.lotacaoAlunos())
            .lotacaoProfessores(request.lotacaoProfessores())
            .andar(andar)
            .ativo(true)
            .build();
        
        Sala saved = salaRepository.save(sala);
        return toSalaResponse(saved);
    }

    @Transactional(readOnly = true)
    public Page<SalaResponse> listarPorAndarPaginado(Long andarId, int page, int size) {
        andarRepository.findById(andarId)
            .orElseThrow(() -> new RuntimeException("Andar não encontrado"));
        
        PageRequest pageable = PageRequest.of(page, size);
        Specification<Sala> spec = (root, query, cb) -> cb.equal(root.get("andar").get("id"), andarId);
        Page<Sala> salas = salaRepository.findAll(spec, pageable);
        
        return salas.map(this::toSalaResponse);
    }

    @Transactional(readOnly = true)
    public SalaResponse buscarPorId(Long id) {
        Sala sala = salaRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Sala não encontrada."));
        return toSalaResponse(sala);
    }

    @Transactional
    public SalaResponse atualizar(Long id, SalaCadastroRequest request) {
        Sala sala = salaRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Sala não encontrada."));

        String nomeNormalizado = normalizarNome(request.nome());
        
        // Validar se já existe sala com o mesmo nome no andar (excluindo a própria sala)
        if (salaRepository.existsByAndarIdAndNomeIgnoreCaseAndIdNot(request.andarId(), nomeNormalizado, id)) {
            throw new RuntimeException("Já existe uma sala com este nome neste andar.");
        }
        
        sala.setNome(nomeNormalizado);
        sala.setLotacaoAlunos(request.lotacaoAlunos());
        sala.setLotacaoProfessores(request.lotacaoProfessores());
        
        Sala updated = salaRepository.save(sala);
        return toSalaResponse(updated);
    }

    @Transactional
    public void deletar(Long id) {
        alterarStatus(id, false);
    }

    @Transactional
    public SalaResponse alterarStatus(Long id, boolean ativo) {
        Sala sala = salaRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Sala não encontrada."));
        sala.setAtivo(ativo);
        return toSalaResponse(salaRepository.save(sala));
    }

    private SalaResponse toSalaResponse(Sala sala) {
        return new SalaResponse(
            sala.getId(),
            sala.getNome(),
            sala.getLotacaoAlunos(),
            sala.getLotacaoProfessores(),
            sala.getAndar().getId(),
            sala.isAtivo()
        );
    }

    private String normalizarNome(String nome) {
        String nomeNormalizado = nome == null ? "" : nome.trim().replaceAll("\\s+", " ");
        if (nomeNormalizado.isBlank()) {
            throw new IllegalArgumentException("Nome da sala é obrigatório.");
        }
        return nomeNormalizado;
    }
}


