package com.pucpr.casetecnico.backend.ensalamento.presenca.dto;

public record BlocoComparativoResponse(
        String blocoNome,
        Integer capacidadeTotal,
        Long presentes,
        double percentualOcupacao) {
}

