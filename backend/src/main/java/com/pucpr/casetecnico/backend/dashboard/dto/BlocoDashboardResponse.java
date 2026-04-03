package com.pucpr.casetecnico.backend.dashboard.dto;

public record BlocoDashboardResponse(
        String blocoNome,
        Long salasEmUso,
        Long professoresPresentes,
        Long alunosPresentes,
        double percentualOcupacaoAtual) {
}


