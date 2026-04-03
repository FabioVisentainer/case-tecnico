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

@RestController
@RequestMapping("/api/andares")
@RequiredArgsConstructor
public class AndarController {

    private final AndarService andarService;

    @PostMapping
    public ResponseEntity<AndarResponse> cadastrar(@Valid @RequestBody AndarCadastroRequest request) {
        AndarResponse response = andarService.cadastrar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public Page<AndarResponse> listarPorBloco(
            @RequestParam Long blocoId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return andarService.listarPorBlocoPaginado(blocoId, page, size);
    }

    @GetMapping("/{id}")
    public AndarDetalhesResponse buscarComDetalhes(@PathVariable Long id) {
        return andarService.buscarComDetalhes(id);
    }

    @PutMapping("/{id}")
    public AndarResponse atualizar(@PathVariable Long id, @Valid @RequestBody AndarCadastroRequest request) {
        return andarService.atualizar(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        andarService.deletar(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/status")
    public AndarResponse alterarStatus(@PathVariable Long id, @Valid @RequestBody AndarStatusRequest request) {
        return andarService.alterarStatus(id, request.ativo());
    }
}