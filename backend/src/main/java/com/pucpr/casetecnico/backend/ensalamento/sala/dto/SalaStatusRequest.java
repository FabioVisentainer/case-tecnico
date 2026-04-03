package com.pucpr.casetecnico.backend.ensalamento.sala.dto;

import jakarta.validation.constraints.NotNull;

public record SalaStatusRequest(
        @NotNull(message = "Status obrigatório.")
        Boolean ativo) {
}