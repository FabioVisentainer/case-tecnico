package com.pucpr.casetecnico.backend.ensalamento.andar.dto;

public record SalaResumoResponse(
    Long id,
    String nome,
    Integer lotacaoAlunos,
    Integer lotacaoProfessores,
    boolean ativo
) {}