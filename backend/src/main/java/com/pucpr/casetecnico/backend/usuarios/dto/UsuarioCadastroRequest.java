package com.pucpr.casetecnico.backend.usuarios.dto;

import org.hibernate.validator.constraints.br.CPF;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import com.pucpr.casetecnico.backend.usuarios.model.EnumPapelUsuario;

public record UsuarioCadastroRequest(

        @NotBlank(message = "Nome obrigatório.")
        @Size(max = 120, message = "Nome deve ter no máximo 120 caracteres.")
        String nome,

        @NotBlank(message = "Username obrigatório.")
        @Size(max = 40, message = "Username deve ter no máximo 40 caracteres.")
        @Pattern(regexp = "^[A-Za-z0-9._-]+$", message = "Username inválido. Não use espaços.")
        String username,

        @NotBlank(message = "CPF obrigatório.")
        @CPF(message = "CPF invalido.")
        String cpf,

        @NotNull(message = "Papel obrigatório.")
        EnumPapelUsuario papel,

        @NotBlank(message = "Senha obrigatória.")
        @Size(min = 4, message = "Senha obrigatória com no mínimo 4 caracteres.")
        String senha,

        Boolean ativo

) {}