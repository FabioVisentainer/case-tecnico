package com.pucpr.casetecnico.backend.usuarios.dto;

import org.hibernate.validator.constraints.br.CPF;

import com.pucpr.casetecnico.backend.usuarios.model.EnumPapelUsuario;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UsuarioAtualizacaoRequest(
        @NotBlank(message = "Nome obrigatório.")
        @Size(max = 120, message = "Nome deve ter no máximo 120 caracteres.")
        String nome,

        @NotBlank(message = "Username obrigatório.")
        @Size(max = 40, message = "Username deve ter no máximo 40 caracteres.")
        @Pattern(regexp = "^[A-Za-z0-9._-]+$", message = "Username inválido. Não use espaços.")
        String username,

        @NotBlank(message = "CPF obrigatório.")
        @CPF(message = "CPF inválido.")
        String cpf,

        @NotNull(message = "Papel obrigatório.")
        EnumPapelUsuario papel,

        @Size(min = 4, message = "Senha deve ter no minimo 4 caracteres.")
        String senha,

        Boolean ativo) {
}

