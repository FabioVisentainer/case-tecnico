package com.pucpr.casetecnico.backend.controller;

import java.util.Map;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api")
@Tag(name = "Utilidades", description = "Endpoints simples de teste e validação")
public class HelloController {

    @GetMapping("/hello")
    @Operation(summary = "Mensagem pública de teste", description = "Retorna uma mensagem simples para validar a API")
    @ApiResponse(responseCode = "200", description = "Mensagem retornada com sucesso")
    public Map<String, String> hello() {
        return Map.of("message", "Hello from Spring Boot");
    }

    @GetMapping("/private/hello")
    @Operation(summary = "Mensagem protegida de teste", description = "Retorna uma mensagem apenas para usuário autenticado")
    @ApiResponse(responseCode = "200", description = "Mensagem retornada com sucesso")
    public Map<String, String> privateHello(Authentication authentication) {
        return Map.of("message", "Hello " + authentication.getName() + ", this is a protected endpoint");
    }
}
