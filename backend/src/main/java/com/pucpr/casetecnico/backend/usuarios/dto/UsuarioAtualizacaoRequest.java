package com.pucpr.casetecnico.backend.usuarios.dto;

import org.hibernate.validator.constraints.br.CPF;

import com.pucpr.casetecnico.backend.usuarios.model.EnumPapelUsuario;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UsuarioAtualizacaoRequest(
        @NotBlank(message = "Nome obrigatorio.")
        @Size(max = 120, message = "Nome deve ter no maximo 120 caracteres.")
        String nome,

        @NotBlank(message = "Username obrigatorio.")
        @Size(max = 40, message = "Username deve ter no maximo 40 caracteres.")
        @Pattern(regexp = "^[A-Za-z0-9._-]+$", message = "Username invalido. Nao use espacos.")
        String username,

        @NotBlank(message = "CPF obrigatorio.")
        @CPF(message = "CPF invalido.")
        String cpf,

        @NotNull(message = "Papel obrigatorio.")
        EnumPapelUsuario papel,

        @Size(min = 4, message = "Senha deve ter no minimo 4 caracteres.")
        String senha,

        Boolean ativo) {
}

