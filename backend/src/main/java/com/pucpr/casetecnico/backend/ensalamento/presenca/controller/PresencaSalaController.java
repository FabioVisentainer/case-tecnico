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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/presencas")
@RequiredArgsConstructor
@Tag(name = "Presença", description = "Check-in, checkout e consulta de presença em sala")
public class PresencaSalaController {

    private final PresencaSalaService presencaSalaService;

    @GetMapping("/salas")
    @Operation(summary = "Buscar salas ativas", description = "Retorna salas ativas para navegação e check-in")
    public Page<SalaBuscaResponse> buscarSalas(
            @Parameter(description = "Texto de busca por bloco, andar ou sala") @RequestParam(required = false) String q,
            @Parameter(description = "Página da consulta") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Quantidade de registros por página") @RequestParam(defaultValue = "12") int size) {
        return presencaSalaService.buscarSalasAtivas(q, page, size);
    }

    @PostMapping("/checkin")
    @Operation(summary = "Registrar check-in", description = "Registra a entrada do usuário autenticado em uma sala")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Check-in realizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Requisição inválida"),
            @ApiResponse(responseCode = "409", description = "Usuário já possui presença ativa")
    })
    public PresencaAtualResponse checkin(Authentication authentication, @Valid @RequestBody CheckinRequest request) {
        return presencaSalaService.checkin(authentication, request.salaId());
    }

    @PatchMapping("/checkout")
    @Operation(summary = "Registrar checkout", description = "Finaliza a presença ativa do usuário autenticado")
    public PresencaAtualResponse checkout(Authentication authentication) {
        return presencaSalaService.checkout(authentication);
    }

    @GetMapping("/minha")
    @Operation(summary = "Consultar minha presença", description = "Retorna a presença atual e o contexto da sala do usuário")
    public MinhaPresencaResponse minhaPresenca(Authentication authentication) {
        return presencaSalaService.minhaPresenca(authentication);
    }

    @GetMapping("/minha/alunos-presentes")
    @Operation(summary = "Listar alunos presentes", description = "Retorna os alunos presentes na mesma sala do professor autenticado")
    public List<AlunoPresenteResponse> listarAlunosPresentes(Authentication authentication) {
        return presencaSalaService.listarAlunosPresentesNaMinhaSala(authentication);
    }

}