package com.pucpr.casetecnico.backend.usuarios.dto;

import com.pucpr.casetecnico.backend.usuarios.model.EnumPapelUsuario;

public record UsuarioCadastroRequest(
        String nome,
        EnumPapelUsuario papel,
        String senha,
        Boolean ativo) {
}