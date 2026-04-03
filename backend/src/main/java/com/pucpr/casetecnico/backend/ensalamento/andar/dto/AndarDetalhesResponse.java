package com.pucpr.casetecnico.backend.ensalamento.andar.dto;

import java.util.List;

public record AndarDetalhesResponse(
    Long id,
    String nome,
    Long blocoId,
    boolean ativo,
    List<SalaResumoResponse> salas
) {}