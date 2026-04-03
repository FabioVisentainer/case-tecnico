package com.pucpr.casetecnico.backend.dashboard.dto;

import java.time.Instant;
import java.util.List;

public record DashboardHorariosResponse(
        Instant geradoEm,
        List<HorarioPicoResponse> kpis,
        List<HeatmapHorarioResponse> heatmap,
        List<OcupacaoFaixaHorariaResponse> ocupacaoFaixa,
        List<TurnoOcupacaoResponse> ocupacaoTurno) {
}

