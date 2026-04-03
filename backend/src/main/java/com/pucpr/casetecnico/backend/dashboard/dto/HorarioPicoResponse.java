package com.pucpr.casetecnico.backend.dashboard.dto;

public record HorarioPicoResponse(
        String descricao,
        String hora,
        Long checkins,
        double percentual) {
}

