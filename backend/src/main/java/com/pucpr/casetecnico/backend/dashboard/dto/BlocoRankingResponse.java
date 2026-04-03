package com.pucpr.casetecnico.backend.dashboard.dto;

public record BlocoRankingResponse(
        String blocoNome,
        Long utilizacoes,
        double percentual) {
}

