package com.pucpr.casetecnico.backend.dashboard.dto;

public record HeatmapHorarioResponse(
        Integer diaNumero,
        String diaNome,
        Integer hora,
        Long checkins) {
}

