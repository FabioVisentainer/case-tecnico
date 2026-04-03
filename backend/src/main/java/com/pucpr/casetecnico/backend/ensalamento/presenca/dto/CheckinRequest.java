package com.pucpr.casetecnico.backend.ensalamento.presenca.dto;

import jakarta.validation.constraints.NotNull;

public record CheckinRequest(
        @NotNull(message = "Sala obrigatória.")
        Long salaId
) {}