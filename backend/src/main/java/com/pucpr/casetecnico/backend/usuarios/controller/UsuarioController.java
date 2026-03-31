package com.pucpr.casetecnico.backend.usuarios.controller;

import java.util.List;
import java.util.Map;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import lombok.RequiredArgsConstructor;

import com.pucpr.casetecnico.backend.usuarios.dto.UsuarioCadastroRequest;
import com.pucpr.casetecnico.backend.usuarios.dto.UsuarioResponse;
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
        try {
            UsuarioResponse response = usuarioService.cadastrarPorAdmin(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException exception) {
            return ResponseEntity.badRequest().body(Map.of("message", exception.getMessage()));
        }
    }

    @GetMapping
    public List<UsuarioResponse> listar() {
        return usuarioService.listarTodos();
    }
}


