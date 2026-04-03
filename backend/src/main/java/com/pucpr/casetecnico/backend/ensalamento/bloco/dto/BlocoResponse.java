package com.pucpr.casetecnico.backend.ensalamento.bloco.dto;

public record BlocoResponse(
    Long id,
    String nome,
    boolean ativo,
    long totalAndares,
    long totalSalas
) {}