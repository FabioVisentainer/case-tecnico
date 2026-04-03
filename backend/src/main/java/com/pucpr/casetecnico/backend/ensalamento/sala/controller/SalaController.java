package com.pucpr.casetecnico.backend.ensalamento.sala.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.pucpr.casetecnico.backend.ensalamento.sala.dto.SalaCadastroRequest;
import com.pucpr.casetecnico.backend.ensalamento.sala.dto.SalaResponse;
import com.pucpr.casetecnico.backend.ensalamento.sala.dto.SalaStatusRequest;
import com.pucpr.casetecnico.backend.ensalamento.sala.service.SalaService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/salas")
@RequiredArgsConstructor
@Tag(name = "Espaços - Salas", description = "CRUD de salas por andar")
public class SalaController {

    private final SalaService salaService;

    @PostMapping
    @Operation(summary = "Cadastrar sala", description = "Cria uma nova sala vinculada a um andar")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Sala cadastrada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "403", description = "Acesso negado")
    })
    public ResponseEntity<SalaResponse> cadastrar(@Valid @RequestBody SalaCadastroRequest request) {
        SalaResponse response = salaService.cadastrar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @Operation(summary = "Listar salas por andar", description = "Retorna salas paginadas de um andar específico")
    public Page<SalaResponse> listarPorAndar(
            @Parameter(description = "Identificador do andar") @RequestParam Long andarId,
            @Parameter(description = "Página da consulta") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Quantidade de registros por página") @RequestParam(defaultValue = "10") int size) {
        return salaService.listarPorAndarPaginado(andarId, page, size);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar sala por ID", description = "Retorna os dados de uma sala específica")
    public SalaResponse buscarPorId(@PathVariable Long id) {
        return salaService.buscarPorId(id);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar sala", description = "Atualiza os dados de uma sala existente")
    public SalaResponse atualizar(@PathVariable Long id, @Valid @RequestBody SalaCadastroRequest request) {
        return salaService.atualizar(id, request);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Remover sala", description = "Remove ou inativa uma sala conforme a regra de negócio")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        salaService.deletar(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Alterar status da sala", description = "Ativa ou inativa uma sala")
    public SalaResponse alterarStatus(@PathVariable Long id, @Valid @RequestBody SalaStatusRequest request) {
        return salaService.alterarStatus(id, request.ativo());
    }
}