package com.pucpr.casetecnico.backend.ensalamento.andar.dto;

public record AndarResponse(
    Long id,
    String nome,
    Long blocoId,
    boolean ativo
) {}