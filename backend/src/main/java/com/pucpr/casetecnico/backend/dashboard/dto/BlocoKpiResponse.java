package com.pucpr.casetecnico.backend.dashboard.dto;

public record BlocoKpiResponse(
        String blocoNome,
        double ocupacaoMedia,
        Integer capacidadeTotal,
        Integer numeroSalas) {
}

