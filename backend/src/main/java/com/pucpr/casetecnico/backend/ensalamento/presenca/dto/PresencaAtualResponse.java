package com.pucpr.casetecnico.backend.ensalamento.presenca.dto;

import java.time.Instant;

public record PresencaAtualResponse(
        Long presencaId,
        String blocoNome,
        String andarNome,
        String salaNome,
        Instant entrada,
        Instant saida,
        boolean ativa,
        Long quantidadeAlunosPresentes
) {}