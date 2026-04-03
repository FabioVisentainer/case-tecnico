package com.pucpr.casetecnico.backend.ensalamento.presenca.dto;

public record SalaBuscaResponse(
        Long salaId,
        String salaNome,
        String andarNome,
        String blocoNome,
        Integer lotacaoAlunos,
        Integer lotacaoProfessores
) {}