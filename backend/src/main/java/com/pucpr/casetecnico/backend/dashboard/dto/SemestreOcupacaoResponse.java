package com.pucpr.casetecnico.backend.dashboard.dto;

public record SemestreOcupacaoResponse(
        String semestre,
        double percentualOcupacao,
        Long salasUtilizadas,
        Long totalSalasAtivas) {
}

