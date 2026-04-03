package com.pucpr.casetecnico.backend.dashboard.dto;

import java.util.List;

public record RelatorioUsoBlocoResponse(
        String blocoNome,
        double percentualOcupacao,
        List<RelatorioUsoAndarResponse> andares) {
}


