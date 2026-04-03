package com.pucpr.casetecnico.backend.security.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import lombok.RequiredArgsConstructor;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import com.pucpr.casetecnico.backend.security.dto.AuthRequest;
import com.pucpr.casetecnico.backend.security.dto.AuthMeResponse;
import com.pucpr.casetecnico.backend.security.dto.AuthResponse;
import com.pucpr.casetecnico.backend.security.JwtService;
import com.pucpr.casetecnico.backend.usuarios.model.Usuario;
import com.pucpr.casetecnico.backend.usuarios.service.UsuarioService;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticação", description = "Login e dados do usuário autenticado")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UsuarioService usuarioService;

    @PostMapping("/login")
    @Operation(summary = "Autenticar usuário", description = "Valida as credenciais e retorna o token JWT")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuário autenticado com sucesso"),
            @ApiResponse(responseCode = "401", description = "Credenciais inválidas ou usuário inativo")
    })
    public ResponseEntity<?> login(@RequestBody AuthRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.username(), request.senha()));

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String token = jwtService.generateToken(userDetails);
            return ResponseEntity.ok(new AuthResponse(token, "Bearer"));
        } catch (DisabledException exception) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Usuário inativo. Contate um administrador."));
        } catch (BadCredentialsException exception) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Credenciais invalidas"));
        }
    }

    @GetMapping("/me")
    @Operation(summary = "Dados do usuário autenticado", description = "Retorna nome, username e papel do usuário logado")
    @ApiResponse(responseCode = "200", description = "Usuário autenticado encontrado")
    public AuthMeResponse me(Authentication authentication) {
        Usuario usuario = usuarioService.buscarPorUsername(authentication.getName());
        return new AuthMeResponse(usuario.getNome(), usuario.getUsername(), usuario.getPapel());
    }
}


