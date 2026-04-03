package com.pucpr.casetecnico.backend.dashboard.dto;

import java.time.Instant;
import java.util.List;

public record DashboardBlocosResponse(
        Instant geradoEm,
        List<BlocoKpiResponse> kpis,
        List<BlocoRankingResponse> ranking,
        List<BlocoHeatmapResponse> heatmap,
        List<BlocoComparativoResponse> comparativo) {
}

