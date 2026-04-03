package com.pucpr.casetecnico.backend.dashboard.dto;

import java.time.Instant;
import java.util.List;

public record DashboardSalasResponse(
        Instant geradoEm,
        List<SalaKpiResponse> kpis,
        List<SalaRankingUtilizadaResponse> rankingUtilizadas,
        List<SalaRankingOciosasResponse> rankingOciosas,
        List<SalaDispersaoResponse> dispersao) {
}

