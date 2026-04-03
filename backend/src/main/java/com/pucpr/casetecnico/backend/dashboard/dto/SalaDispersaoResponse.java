package com.pucpr.casetecnico.backend.dashboard.dto;

public record SalaDispersaoResponse(
        Long salaId,
        String salaNome,
        String blocoNome,
        String andarNome,
        Integer capacidade,
        Long presentesAgora,
        double percentualOcupacao) {
}

