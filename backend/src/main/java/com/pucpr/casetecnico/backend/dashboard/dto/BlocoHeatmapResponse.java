package com.pucpr.casetecnico.backend.dashboard.dto;

public record BlocoHeatmapResponse(
        String blocoNome,
        String turno,
        Long checkins) {
}

