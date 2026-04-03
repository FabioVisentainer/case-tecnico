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

@RestController
@RequestMapping("/api/salas")
@RequiredArgsConstructor
public class SalaController {

    private final SalaService salaService;

    @PostMapping
    public ResponseEntity<SalaResponse> cadastrar(@Valid @RequestBody SalaCadastroRequest request) {
        SalaResponse response = salaService.cadastrar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public Page<SalaResponse> listarPorAndar(
            @RequestParam Long andarId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return salaService.listarPorAndarPaginado(andarId, page, size);
    }

    @GetMapping("/{id}")
    public SalaResponse buscarPorId(@PathVariable Long id) {
        return salaService.buscarPorId(id);
    }

    @PutMapping("/{id}")
    public SalaResponse atualizar(@PathVariable Long id, @Valid @RequestBody SalaCadastroRequest request) {
        return salaService.atualizar(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        salaService.deletar(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/status")
    public SalaResponse alterarStatus(@PathVariable Long id, @Valid @RequestBody SalaStatusRequest request) {
        return salaService.alterarStatus(id, request.ativo());
    }
}