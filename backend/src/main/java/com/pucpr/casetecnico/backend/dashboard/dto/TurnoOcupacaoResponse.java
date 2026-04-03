package com.pucpr.casetecnico.backend.dashboard.dto;

public record TurnoOcupacaoResponse(
        String turno,
        double percentualOcupacao,
        Long totalCheckins) {
}

