package com.pucpr.casetecnico.backend.ensalamento.bloco.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record BlocoCadastroRequest(
    @NotBlank(message = "Nome do bloco é obrigatório")
    @Size(min = 1, max = 120, message = "Nome deve ter entre 1 e 120 caracteres")
    String nome
) {}