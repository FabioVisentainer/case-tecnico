package com.pucpr.casetecnico.backend.dashboard.dto;

public record SalaUsoAtualResponse(
        String blocoNome,
        String andarNome,
        String salaNome,
        Long professoresPresentes,
        Long alunosPresentes,
        Integer capacidadeTotal,
        double percentualOcupacaoAtual) {
}

