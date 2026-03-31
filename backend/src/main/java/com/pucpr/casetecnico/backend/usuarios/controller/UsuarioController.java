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
public class UsuarioController {

    private final UsuarioService usuarioService;

    @PostMapping
    public ResponseEntity<?> cadastrar(
            @Valid @RequestBody
            UsuarioCadastroRequest request
    ) {
        UsuarioResponse response = usuarioService.cadastrarPorAdmin(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public UsuarioPageResponse listar(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "false") boolean mostrarInativos,
            @RequestParam(required = false) String q,
            @RequestParam(required = false) EnumPapelUsuario papel) {
        return usuarioService.listarPaginado(page, size, mostrarInativos, q, papel);
    }

    @GetMapping("/{id}")
    public UsuarioResponse buscarPorId(@PathVariable Long id) {
        return usuarioService.buscarPorIdResponse(id);
    }

    @PutMapping("/{id}")
    public UsuarioResponse editar(
            @PathVariable Long id,
            @Valid @RequestBody UsuarioAtualizacaoRequest request) {
        return usuarioService.atualizarPorAdmin(id, request);
    }

    @PatchMapping("/{id}/status")
    public UsuarioResponse alterarStatus(
            @PathVariable Long id,
            @Valid @RequestBody UsuarioStatusRequest request) {
        return usuarioService.alterarStatus(id, request.ativo());
    }
}


