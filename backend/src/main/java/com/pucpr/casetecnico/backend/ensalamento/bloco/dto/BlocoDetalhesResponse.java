package com.pucpr.casetecnico.backend.ensalamento.bloco.dto;

import java.util.List;

public record BlocoDetalhesResponse(
    Long id,
    String nome,
    boolean ativo,
    List<AndarResumoResponse> andares
) {}