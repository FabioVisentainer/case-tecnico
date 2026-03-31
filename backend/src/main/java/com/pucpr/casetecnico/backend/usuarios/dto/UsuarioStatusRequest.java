package com.pucpr.casetecnico.backend.usuarios.dto;

import jakarta.validation.constraints.NotNull;

public record UsuarioStatusRequest(
        @NotNull(message = "Status obrigatorio.")
        Boolean ativo) {
}

