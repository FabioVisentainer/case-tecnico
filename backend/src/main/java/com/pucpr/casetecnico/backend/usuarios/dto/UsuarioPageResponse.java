package com.pucpr.casetecnico.backend.usuarios.dto;

import java.util.List;

public record UsuarioPageResponse(
        List<UsuarioResponse> content,
        long totalElements,
        int totalPages,
        int page,
        int size,
        boolean first,
        boolean last) {
}

