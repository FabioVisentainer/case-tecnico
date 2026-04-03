package com.pucpr.casetecnico.backend.dashboard.dto;

import java.util.List;

public record RelatorioUsoAndarResponse(
        String andarNome,
        double percentualOcupacao,
        List<RelatorioUsoSalaResponse> salas) {
}


