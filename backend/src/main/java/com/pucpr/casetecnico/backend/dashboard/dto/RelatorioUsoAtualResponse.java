package com.pucpr.casetecnico.backend.dashboard.dto;

import java.time.Instant;
import java.util.List;

public record RelatorioUsoAtualResponse(
        Instant geradoEm,
        Long totalSalasEmUso,
        Long totalProfessoresPresentes,
        Long totalAlunosPresentes,
        double percentualOcupacaoMedia,
        List<SalaUsoAtualResponse> salasEmUso) {
}


