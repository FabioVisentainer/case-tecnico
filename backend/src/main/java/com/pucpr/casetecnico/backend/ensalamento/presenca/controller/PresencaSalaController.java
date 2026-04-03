package com.pucpr.casetecnico.backend.ensalamento.presenca.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import com.pucpr.casetecnico.backend.ensalamento.presenca.dto.CheckinRequest;
import com.pucpr.casetecnico.backend.ensalamento.presenca.dto.AlunoPresenteResponse;
import com.pucpr.casetecnico.backend.ensalamento.presenca.dto.MinhaPresencaResponse;
import com.pucpr.casetecnico.backend.ensalamento.presenca.dto.PresencaAtualResponse;
import com.pucpr.casetecnico.backend.ensalamento.presenca.dto.SalaBuscaResponse;
import com.pucpr.casetecnico.backend.ensalamento.presenca.service.PresencaSalaService;

@RestController
@RequestMapping("/api/presencas")
@RequiredArgsConstructor
public class PresencaSalaController {

    private final PresencaSalaService presencaSalaService;

    @GetMapping("/salas")
    public Page<SalaBuscaResponse> buscarSalas(
            @RequestParam(required = false) String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) {
        return presencaSalaService.buscarSalasAtivas(q, page, size);
    }

    @PostMapping("/checkin")
    public PresencaAtualResponse checkin(Authentication authentication, @Valid @RequestBody CheckinRequest request) {
        return presencaSalaService.checkin(authentication, request.salaId());
    }

    @PatchMapping("/checkout")
    public PresencaAtualResponse checkout(Authentication authentication) {
        return presencaSalaService.checkout(authentication);
    }

    @GetMapping("/minha")
    public MinhaPresencaResponse minhaPresenca(Authentication authentication) {
        return presencaSalaService.minhaPresenca(authentication);
    }

    @GetMapping("/minha/alunos-presentes")
    public List<AlunoPresenteResponse> listarAlunosPresentes(Authentication authentication) {
        return presencaSalaService.listarAlunosPresentesNaMinhaSala(authentication);
    }

}