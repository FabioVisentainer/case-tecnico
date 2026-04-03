package com.pucpr.casetecnico.backend.dashboard.dto;

public record BlocoComparativoResponse(
        String blocoNome,
        Integer capacidadeTotal,
        Long presentes,
        double percentualOcupacao) {
}

