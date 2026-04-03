package com.pucpr.casetecnico.backend.ensalamento.presenca.service;

import java.time.Instant;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import com.pucpr.casetecnico.backend.ensalamento.presenca.dto.AlunoPresenteResponse;
import com.pucpr.casetecnico.backend.ensalamento.presenca.dto.MinhaPresencaResponse;
import com.pucpr.casetecnico.backend.ensalamento.presenca.dto.PresencaAtualResponse;
import com.pucpr.casetecnico.backend.ensalamento.presenca.dto.PresencaHistoricoItemResponse;
import com.pucpr.casetecnico.backend.ensalamento.presenca.dto.SalaBuscaResponse;
import com.pucpr.casetecnico.backend.ensalamento.presenca.model.PresencaSala;
import com.pucpr.casetecnico.backend.ensalamento.presenca.repository.PresencaSalaRepository;
import com.pucpr.casetecnico.backend.ensalamento.sala.model.Sala;
import com.pucpr.casetecnico.backend.ensalamento.sala.repository.SalaRepository;
import com.pucpr.casetecnico.backend.usuarios.model.EnumPapelUsuario;
import com.pucpr.casetecnico.backend.usuarios.model.Usuario;
import com.pucpr.casetecnico.backend.usuarios.repository.UsuarioRepository;

@Service
@RequiredArgsConstructor
public class PresencaSalaService {

    private final PresencaSalaRepository presencaSalaRepository;
    private final SalaRepository salaRepository;
    private final UsuarioRepository usuarioRepository;

    @Transactional(readOnly = true)
    public Page<SalaBuscaResponse> buscarSalasAtivas(String q, int page, int size) {
        PageRequest pageable = PageRequest.of(page, size);
        return salaRepository.buscarSalasAtivasPorTermoPaginado(q == null ? "" : q.trim(), pageable)
                .map(sala -> new SalaBuscaResponse(
                        sala.getId(),
                        sala.getNome(),
                        sala.getAndar().getNome(),
                        sala.getAndar().getBloco().getNome(),
                        sala.getLotacaoAlunos(),
                        sala.getLotacaoProfessores()));
    }

    @Transactional
    public PresencaAtualResponse checkin(Authentication authentication, Long salaId) {
        Usuario usuario = buscarUsuarioAutenticado(authentication);

        presencaSalaRepository.buscarAtivaPorUsuarioId(usuario.getId())
                .ifPresent(p -> {
                    throw new IllegalArgumentException("Usuário já está em uma sala. Faça checkout antes de novo check-in.");
                });

        Sala sala = salaRepository.findById(salaId)
                .orElseThrow(() -> new IllegalArgumentException("Sala não encontrada."));

        if (!sala.isAtivo() || !sala.getAndar().isAtivo() || !sala.getAndar().getBloco().isAtivo()) {
            throw new IllegalArgumentException("Sala inativa para check-in.");
        }

        PresencaSala presenca = PresencaSala.builder()
                .usuario(usuario)
                .sala(sala)
                .entrada(Instant.now())
                .saida(null)
                .build();

        return toAtualResponse(presencaSalaRepository.save(presenca), usuario);
    }

    @Transactional
    public PresencaAtualResponse checkout(Authentication authentication) {
        Usuario usuario = buscarUsuarioAutenticado(authentication);
        PresencaSala ativa = presencaSalaRepository.buscarAtivaPorUsuarioId(usuario.getId())
                .orElseThrow(() -> new IllegalArgumentException("Usuário não possui check-in ativo."));

        ativa.setSaida(Instant.now());
        return toAtualResponse(presencaSalaRepository.save(ativa), usuario);
    }

    @Transactional(readOnly = true)
    public MinhaPresencaResponse minhaPresenca(Authentication authentication) {
        Usuario usuario = buscarUsuarioAutenticado(authentication);

        PresencaAtualResponse atual = presencaSalaRepository.buscarAtivaPorUsuarioId(usuario.getId())
                .map(presenca -> toAtualResponse(presenca, usuario))
                .orElse(null);

        List<PresencaHistoricoItemResponse> historico = presencaSalaRepository.listarHistoricoPorUsuario(usuario.getId())
                .stream()
                .limit(30)
                .map(this::toHistoricoItem)
                .toList();

        return new MinhaPresencaResponse(atual, historico);
    }

    @Transactional(readOnly = true)
    public List<AlunoPresenteResponse> listarAlunosPresentesNaMinhaSala(Authentication authentication) {
        Usuario usuario = buscarUsuarioAutenticado(authentication);
        if (usuario.getPapel() != EnumPapelUsuario.PROFESSOR) {
            throw new IllegalArgumentException("Somente professor pode visualizar alunos presentes.");
        }

        PresencaSala ativa = presencaSalaRepository.buscarAtivaPorUsuarioId(usuario.getId())
                .orElse(null);
        if (ativa == null) {
            return List.of();
        }

        return presencaSalaRepository
                .listarPresentesAtivosPorSalaEPapel(ativa.getSala().getId(), EnumPapelUsuario.ALUNO)
                .stream()
                .map(presenca -> new AlunoPresenteResponse(
                        presenca.getUsuario().getId(),
                        presenca.getUsuario().getNome(),
                        presenca.getUsuario().getUsername()))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<PresencaSala> buscarAtivasPorUsuarioIds(List<Long> usuarioIds) {
        if (usuarioIds == null || usuarioIds.isEmpty()) {
            return List.of();
        }
        return presencaSalaRepository.buscarAtivasPorUsuarioIds(usuarioIds);
    }

    private Usuario buscarUsuarioAutenticado(Authentication authentication) {
        return usuarioRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new IllegalArgumentException("Usuário autenticado não encontrado."));
    }

    private PresencaAtualResponse toAtualResponse(PresencaSala presenca, Usuario usuario) {
        Long quantidadeAlunosPresentes = null;
        if (usuario.getPapel() == EnumPapelUsuario.PROFESSOR) {
            quantidadeAlunosPresentes = presencaSalaRepository.contarPresentesAtivosPorSalaEPapel(
                    presenca.getSala().getId(),
                    EnumPapelUsuario.ALUNO);
        }

        return new PresencaAtualResponse(
                presenca.getId(),
                presenca.getSala().getAndar().getBloco().getNome(),
                presenca.getSala().getAndar().getNome(),
                presenca.getSala().getNome(),
                presenca.getEntrada(),
                presenca.getSaida(),
                presenca.getSaida() == null,
                quantidadeAlunosPresentes);
    }

    private PresencaHistoricoItemResponse toHistoricoItem(PresencaSala presenca) {
        return new PresencaHistoricoItemResponse(
                presenca.getSala().getAndar().getBloco().getNome(),
                presenca.getSala().getAndar().getNome(),
                presenca.getSala().getNome(),
                presenca.getEntrada(),
                presenca.getSaida());
    }

}