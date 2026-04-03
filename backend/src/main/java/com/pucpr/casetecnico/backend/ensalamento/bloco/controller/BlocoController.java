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

@RestController
@RequestMapping("/api/blocos")
@RequiredArgsConstructor
public class BlocoController {

    private final BlocoService blocoService;

    @PostMapping
    public ResponseEntity<BlocoResponse> cadastrar(@Valid @RequestBody BlocoCadastroRequest request) {
        BlocoResponse response = blocoService.cadastrar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public Page<BlocoResponse> listar(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "false") boolean mostrarInativos,
            @RequestParam(required = false) String q) {
        return blocoService.listarPaginado(page, size, mostrarInativos, q);
    }

    @GetMapping("/{id}")
    public BlocoDetalhesResponse buscarComDetalhes(@PathVariable Long id) {
        return blocoService.buscarComDetalhes(id);
    }

    @PutMapping("/{id}")
    public BlocoResponse atualizar(@PathVariable Long id, @Valid @RequestBody BlocoCadastroRequest request) {
        return blocoService.atualizar(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        blocoService.deletar(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/status")
    public BlocoResponse alterarStatus(@PathVariable Long id, @Valid @RequestBody BlocoStatusRequest request) {
        return blocoService.alterarStatus(id, request.ativo());
    }
}