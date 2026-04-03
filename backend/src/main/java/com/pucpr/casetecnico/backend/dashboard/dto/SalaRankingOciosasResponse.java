package com.pucpr.casetecnico.backend.dashboard.dto;

public record SalaRankingOciosasResponse(
        Long salaId,
        String salaNome,
        String blocoNome,
        String andarNome,
        Long checkins,
        double percentualOciosidade) {
}

