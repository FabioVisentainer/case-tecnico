package com.pucpr.casetecnico.backend.ensalamento.presenca.dto;

import java.util.List;

public record MinhaPresencaResponse(
        PresencaAtualResponse atual,
        List<PresencaHistoricoItemResponse> historicoRecente
) {}

