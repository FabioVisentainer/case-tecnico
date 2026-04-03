package com.pucpr.casetecnico.backend.ensalamento.sala.dto;

public record SalaResponse(
    Long id,
    String nome,
    Integer lotacaoAlunos,
    Integer lotacaoProfessores,
    Long andarId,
    boolean ativo
) {}