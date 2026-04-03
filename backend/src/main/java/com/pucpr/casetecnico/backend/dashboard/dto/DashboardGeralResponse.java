package com.pucpr.casetecnico.backend.dashboard.dto;

import java.time.Instant;
import java.util.List;

public record DashboardGeralResponse(
        Instant geradoEm,
        double taxaMediaOcupacao,
        double taxaOciosidade,
        Long totalSalasAtivas,
        Integer capacidadeTotal,
        Long totalAlunosPresentes,
        List<BlocoOcupacaoResponse> ocupacaoPorBloco,
        List<TurnoOcupacaoResponse> ocupacaoPorTurno,
        List<SemestreOcupacaoResponse> evolucaoSemestral) {
}

