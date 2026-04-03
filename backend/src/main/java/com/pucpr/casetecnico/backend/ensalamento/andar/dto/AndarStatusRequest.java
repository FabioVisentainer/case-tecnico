package com.pucpr.casetecnico.backend.ensalamento.andar.dto;

import jakarta.validation.constraints.NotNull;

public record AndarStatusRequest(
        @NotNull(message = "Status obrigatório.")
        Boolean ativo) {
}