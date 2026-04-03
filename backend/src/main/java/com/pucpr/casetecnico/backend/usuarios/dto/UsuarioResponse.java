package com.pucpr.casetecnico.backend.usuarios.dto;

import java.time.Instant;

import com.pucpr.casetecnico.backend.usuarios.model.EnumPapelUsuario;

public record UsuarioResponse(
        Long id,
        String nome,
        String username,
        String cpf,
        EnumPapelUsuario papel,
        boolean ativo,
        boolean emSala,
        String blocoAtual,
        String andarAtual,
        String salaAtual,
        Instant entradaAtual
) {}

