package com.pucpr.casetecnico.backend.ensalamento.sala.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record SalaCadastroRequest(
    @NotBlank(message = "Nome da sala é obrigatório")
    @Size(min = 1, max = 120, message = "Nome deve ter entre 1 e 120 caracteres")
    String nome,
    
    @NotNull(message = "Lotação de alunos é obrigatória")
    @Positive(message = "Lotação de alunos deve ser positiva")
    Integer lotacaoAlunos,
    
    @NotNull(message = "Lotação de professores é obrigatória")
    @Positive(message = "Lotação de professores deve ser positiva")
    Integer lotacaoProfessores,
    
    @NotNull(message = "ID do andar é obrigatório")
    Long andarId
) {}