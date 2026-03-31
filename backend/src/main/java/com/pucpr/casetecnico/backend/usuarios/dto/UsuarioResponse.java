package com.pucpr.casetecnico.backend.usuarios.dto;

import com.pucpr.casetecnico.backend.usuarios.model.EnumPapelUsuario;

public record UsuarioResponse(
        Long id,
        String nome,
        EnumPapelUsuario papel,
        boolean ativo) {
}

