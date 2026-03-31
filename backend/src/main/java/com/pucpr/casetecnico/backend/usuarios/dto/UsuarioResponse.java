package com.pucpr.casetecnico.backend.usuarios.dto;

import com.pucpr.casetecnico.backend.usuarios.model.EnumPapelUsuario;

public record UsuarioResponse(
        Long id,
        String nome,
        String username,
        String cpf,
        EnumPapelUsuario papel,
        boolean ativo
) {}

