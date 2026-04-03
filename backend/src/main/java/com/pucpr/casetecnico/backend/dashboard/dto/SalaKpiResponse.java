package com.pucpr.casetecnico.backend.dashboard.dto;

public record SalaKpiResponse(
        Long salaId,
        String salaNome,
        String blocoNome,
        String andarNome,
        double taxaOcupacao,
        double taxaCapacidade,
        Long horasUtilizadas,
        Long horasDisponiveis) {
}

