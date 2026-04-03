package com.pucpr.casetecnico.backend.usuarios.controller;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import lombok.RequiredArgsConstructor;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import com.pucpr.casetecnico.backend.usuarios.dto.UsuarioAtualizacaoRequest;
import com.pucpr.casetecnico.backend.usuarios.dto.UsuarioCadastroRequest;
import com.pucpr.casetecnico.backend.usuarios.dto.UsuarioPageResponse;
import com.pucpr.casetecnico.backend.usuarios.dto.UsuarioResponse;
import com.pucpr.casetecnico.backend.usuarios.dto.UsuarioStatusRequest;
import com.pucpr.casetecnico.backend.usuarios.model.EnumPapelUsuario;
import com.pucpr.casetecnico.backend.usuarios.service.UsuarioService;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
@Tag(name = "Usuários", description = "Gestão de usuários do sistema")
public class UsuarioController {

    private final UsuarioService usuarioService;

    @PostMapping
    @Operation(summary = "Cadastrar usuário", description = "Cria um novo usuário com validação dos dados enviados")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Usuário cadastrado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "403", description = "Acesso negado")
    })
    public ResponseEntity<?> cadastrar(
            @Valid @RequestBody
            UsuarioCadastroRequest request
    ) {
        UsuarioResponse response = usuarioService.cadastrarPorAdmin(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @Operation(summary = "Listar usuários", description = "Retorna a lista paginada de usuários com filtros opcionais")
    public UsuarioPageResponse listar(
            @Parameter(description = "Página da consulta") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Quantidade de registros por página") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Inclui usuários inativos") @RequestParam(defaultValue = "false") boolean mostrarInativos,
            @Parameter(description = "Filtro por nome, username ou CPF") @RequestParam(required = false) String q,
            @Parameter(description = "Filtro por papel do usuário") @RequestParam(required = false) EnumPapelUsuario papel) {
        return usuarioService.listarPaginado(page, size, mostrarInativos, q, papel);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar usuário por ID", description = "Retorna os detalhes de um usuário específico")
    public UsuarioResponse buscarPorId(@PathVariable Long id) {
        return usuarioService.buscarPorIdResponse(id);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar usuário", description = "Atualiza os dados cadastrais de um usuário existente")
    public UsuarioResponse editar(
            @PathVariable Long id,
            @Valid @RequestBody UsuarioAtualizacaoRequest request) {
        return usuarioService.atualizarPorAdmin(id, request);
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Alterar status do usuário", description = "Ativa ou inativa um usuário do sistema")
    public UsuarioResponse alterarStatus(
            @PathVariable Long id,
            @Valid @RequestBody UsuarioStatusRequest request) {
        return usuarioService.alterarStatus(id, request.ativo());
    }
}


