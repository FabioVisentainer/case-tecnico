package com.pucpr.casetecnico.backend.dashboard.dto;

public record OcupacaoFaixaHorariaResponse(
        String faixa,
        Long checkins,
        double percentual) {
}

