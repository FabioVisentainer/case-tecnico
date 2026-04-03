package com.pucpr.casetecnico.backend.dashboard.dto;

public record SalaRankingUtilizadaResponse(
        Long salaId,
        String salaNome,
        String blocoNome,
        String andarNome,
        Long checkins,
        Long minutosPresenca,
        double percentual) {
}

