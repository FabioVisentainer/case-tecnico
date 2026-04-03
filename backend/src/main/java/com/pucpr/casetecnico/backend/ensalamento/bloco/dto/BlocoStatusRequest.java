package com.pucpr.casetecnico.backend.ensalamento.bloco.dto;

import jakarta.validation.constraints.NotNull;

public record BlocoStatusRequest(
        @NotNull(message = "Status obrigatório.")
        Boolean ativo) {
}