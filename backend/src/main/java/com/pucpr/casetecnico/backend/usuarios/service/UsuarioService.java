package com.pucpr.casetecnico.backend.usuarios.service;

import java.util.List;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

import com.pucpr.casetecnico.backend.usuarios.model.EnumPapelUsuario;
import com.pucpr.casetecnico.backend.usuarios.model.Usuario;
import com.pucpr.casetecnico.backend.usuarios.repository.UsuarioRepository;
import com.pucpr.casetecnico.backend.usuarios.dto.UsuarioCadastroRequest;
import com.pucpr.casetecnico.backend.usuarios.dto.UsuarioResponse;

@Service
@RequiredArgsConstructor
public class UsuarioService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String nome) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByNome(nome)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario nao encontrado"));

        return User.withUsername(usuario.getNome())
                .password(usuario.getSenha())
                .disabled(!usuario.isAtivo())
                .authorities(List.of(new SimpleGrantedAuthority("ROLE_" + usuario.getPapel().name())))
                .build();
    }

    public UsuarioResponse cadastrarPorAdmin(UsuarioCadastroRequest request) {
        validarRequest(request);

        if (request.papel() == EnumPapelUsuario.ADMINISTRADOR) {
            throw new IllegalArgumentException("Administrador nao pode ser criado por este endpoint.");
        }

        if (usuarioRepository.existsByNome(request.nome())) {
            throw new IllegalArgumentException("Ja existe usuario com esse nome.");
        }

        Usuario usuario = Usuario.builder()
                .nome(request.nome().trim())
                .papel(request.papel())
                .senha(passwordEncoder.encode(request.senha()))
                .ativo(request.ativo() == null || request.ativo())
                .build();

        Usuario salvo = usuarioRepository.save(usuario);
        return toResponse(salvo);
    }

    public List<UsuarioResponse> listarTodos() {
        return usuarioRepository.findAll().stream().map(this::toResponse).toList();
    }

    public void criarSeNaoExistir(String nome, EnumPapelUsuario papel, String senha, boolean ativo) {
        if (usuarioRepository.existsByNome(nome)) {
            return;
        }

        Usuario usuario = Usuario.builder()
                .nome(nome)
                .papel(papel)
                .senha(passwordEncoder.encode(senha))
                .ativo(ativo)
                .build();
        usuarioRepository.save(usuario);
    }

    private void validarRequest(UsuarioCadastroRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Payload obrigatorio.");
        }
        if (request.nome() == null || request.nome().isBlank()) {
            throw new IllegalArgumentException("Nome obrigatorio.");
        }
        if (request.senha() == null || request.senha().isBlank() || request.senha().length() < 4) {
            throw new IllegalArgumentException("Senha obrigatoria com no minimo 4 caracteres.");
        }
        if (request.papel() == null) {
            throw new IllegalArgumentException("Papel obrigatorio.");
        }
    }

    private UsuarioResponse toResponse(Usuario usuario) {
        return new UsuarioResponse(
                usuario.getId(),
                usuario.getNome(),
                usuario.getPapel(),
                usuario.isAtivo());
    }
}


