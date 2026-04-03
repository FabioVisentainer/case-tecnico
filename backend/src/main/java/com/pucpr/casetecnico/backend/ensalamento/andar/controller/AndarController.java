package com.pucpr.casetecnico.backend.ensalamento.andar.controller;

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
import com.pucpr.casetecnico.backend.ensalamento.andar.dto.AndarCadastroRequest;
import com.pucpr.casetecnico.backend.ensalamento.andar.dto.AndarDetalhesResponse;
import com.pucpr.casetecnico.backend.ensalamento.andar.dto.AndarResponse;
import com.pucpr.casetecnico.backend.ensalamento.andar.dto.AndarStatusRequest;
import com.pucpr.casetecnico.backend.ensalamento.andar.service.AndarService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/andares")
@RequiredArgsConstructor
@Tag(name = "Espaços - Andares", description = "CRUD de andares por bloco")
public class AndarController {

    private final AndarService andarService;

    @PostMapping
    @Operation(summary = "Cadastrar andar", description = "Cria um andar vinculado a um bloco")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Andar cadastrado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "403", description = "Acesso negado")
    })
    public ResponseEntity<AndarResponse> cadastrar(@Valid @RequestBody AndarCadastroRequest request) {
        AndarResponse response = andarService.cadastrar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @Operation(summary = "Listar andares por bloco", description = "Retorna andares paginados de um bloco específico")
    public Page<AndarResponse> listarPorBloco(
            @Parameter(description = "Identificador do bloco") @RequestParam Long blocoId,
            @Parameter(description = "Página da consulta") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Quantidade de registros por página") @RequestParam(defaultValue = "10") int size) {
        return andarService.listarPorBlocoPaginado(blocoId, page, size);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar andar com detalhes", description = "Retorna um andar com as salas vinculadas")
    public AndarDetalhesResponse buscarComDetalhes(@PathVariable Long id) {
        return andarService.buscarComDetalhes(id);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar andar", description = "Atualiza o nome ou os dados de um andar existente")
    public AndarResponse atualizar(@PathVariable Long id, @Valid @RequestBody AndarCadastroRequest request) {
        return andarService.atualizar(id, request);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Remover andar", description = "Remove ou inativa um andar conforme a regra de negócio")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        andarService.deletar(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Alterar status do andar", description = "Ativa ou inativa um andar")
    public AndarResponse alterarStatus(@PathVariable Long id, @Valid @RequestBody AndarStatusRequest request) {
        return andarService.alterarStatus(id, request.ativo());
    }
}