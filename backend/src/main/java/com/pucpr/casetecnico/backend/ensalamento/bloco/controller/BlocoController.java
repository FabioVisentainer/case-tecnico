package com.pucpr.casetecnico.backend.ensalamento.bloco.controller;

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
import com.pucpr.casetecnico.backend.ensalamento.bloco.dto.BlocoCadastroRequest;
import com.pucpr.casetecnico.backend.ensalamento.bloco.dto.BlocoDetalhesResponse;
import com.pucpr.casetecnico.backend.ensalamento.bloco.dto.BlocoResponse;
import com.pucpr.casetecnico.backend.ensalamento.bloco.dto.BlocoStatusRequest;
import com.pucpr.casetecnico.backend.ensalamento.bloco.service.BlocoService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/blocos")
@RequiredArgsConstructor
@Tag(name = "Espaços - Blocos", description = "CRUD de blocos do campus")
public class BlocoController {

    private final BlocoService blocoService;

    @PostMapping
    @Operation(summary = "Cadastrar bloco", description = "Cria um novo bloco do campus")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Bloco cadastrado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "403", description = "Acesso negado")
    })
    public ResponseEntity<BlocoResponse> cadastrar(@Valid @RequestBody BlocoCadastroRequest request) {
        BlocoResponse response = blocoService.cadastrar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @Operation(summary = "Listar blocos", description = "Retorna blocos paginados com filtro opcional por nome")
    public Page<BlocoResponse> listar(
            @Parameter(description = "Página da consulta") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Quantidade de registros por página") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Inclui blocos inativos") @RequestParam(defaultValue = "false") boolean mostrarInativos,
            @Parameter(description = "Filtro por nome do bloco") @RequestParam(required = false) String q) {
        return blocoService.listarPaginado(page, size, mostrarInativos, q);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar bloco com detalhes", description = "Retorna um bloco com andares e salas relacionados")
    public BlocoDetalhesResponse buscarComDetalhes(@PathVariable Long id) {
        return blocoService.buscarComDetalhes(id);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar bloco", description = "Atualiza o nome e os dados do bloco")
    public BlocoResponse atualizar(@PathVariable Long id, @Valid @RequestBody BlocoCadastroRequest request) {
        return blocoService.atualizar(id, request);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Remover bloco", description = "Inativa ou remove um bloco conforme a regra de negócio do sistema")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        blocoService.deletar(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Alterar status do bloco", description = "Ativa ou inativa um bloco")
    public BlocoResponse alterarStatus(@PathVariable Long id, @Valid @RequestBody BlocoStatusRequest request) {
        return blocoService.alterarStatus(id, request.ativo());
    }
}