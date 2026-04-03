package com.pucpr.casetecnico.backend.dashboard.dto;

public record BlocoOcupacaoResponse(
        String blocoNome,
        double percentualOcupacao,
        Long presentes,
        Integer capacidadeTotal) {
}

