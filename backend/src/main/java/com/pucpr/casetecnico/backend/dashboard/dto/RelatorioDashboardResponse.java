package com.pucpr.casetecnico.backend.dashboard.dto;

import java.time.Instant;
import java.util.List;

public record RelatorioDashboardResponse(
        Instant geradoEm,
        Long totalBlocosEmUso,
        Long totalSalasEmUso,
        Long totalProfessoresPresentes,
        Long totalAlunosPresentes,
        double percentualOcupacaoCampus,
        List<BlocoDashboardResponse> ocupacaoPorBloco,
        List<SalaUsoAtualResponse> topSalasMaiorOcupacao) {
}


