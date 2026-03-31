package com.pucpr.casetecnico.backend.security.dto;

import com.pucpr.casetecnico.backend.usuarios.model.EnumPapelUsuario;

public record AuthMeResponse(
        String nome,
        String username,
        EnumPapelUsuario papel) {
}

