package com.pucpr.casetecnico.backend.ensalamento.presenca.dto;

import java.time.Instant;

public record PresencaHistoricoItemResponse(
        String blocoNome,
        String andarNome,
        String salaNome,
        Instant entrada,
        Instant saida
) {}