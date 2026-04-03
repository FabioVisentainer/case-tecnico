package com.pucpr.casetecnico.backend.ensalamento.andar.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record AndarCadastroRequest(
    @NotBlank(message = "Nome do andar é obrigatório")
    @Size(min = 1, max = 120, message = "Nome deve ter entre 1 e 120 caracteres")
    String nome,
    
    @NotNull(message = "ID do bloco é obrigatório")
    Long blocoId
) {}