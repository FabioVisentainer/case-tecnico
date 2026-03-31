package com.pucpr.casetecnico.backend.usuarios.service;

import java.util.List;
import java.util.regex.Pattern;

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

    private static final Pattern USERNAME_PATTERN = Pattern.compile("^[a-z0-9._-]+$");

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario nao encontrado"));

        return User.withUsername(usuario.getUsername())
                .password(usuario.getSenha())
                .disabled(!usuario.isAtivo())
                .authorities(List.of(new SimpleGrantedAuthority("ROLE_" + usuario.getPapel().name())))
                .build();
    }

    public UsuarioResponse cadastrarPorAdmin(UsuarioCadastroRequest request) {
        String nomeNormalizado = normalizarNome(request.nome());
        String usernameNormalizado = normalizarUsername(request.username());
        String cpfNormalizado = normalizarCpf(request.cpf());

        if (request.papel() == EnumPapelUsuario.ADMINISTRADOR) {
            throw new IllegalArgumentException("Administrador nao pode ser criado por este endpoint.");
        }

        if (usuarioRepository.existsByUsername(usernameNormalizado)) {
            throw new IllegalArgumentException("Ja existe usuario com esse username.");
        }

        if (usuarioRepository.existsByCpf(cpfNormalizado)) {
            throw new IllegalArgumentException("Ja existe usuario com esse CPF.");
        }

        Usuario usuario = Usuario.builder()
                .nome(nomeNormalizado)
                .username(usernameNormalizado)
                .cpf(cpfNormalizado)
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

    public Usuario buscarPorUsername(String username) {
        return usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario nao encontrado"));
    }

    public void criarSeNaoExistir(String nome, String username, String cpf, EnumPapelUsuario papel, String senha, boolean ativo) {
        String usernameNormalizado = normalizarUsername(username);
        String cpfNormalizado = normalizarCpf(cpf);

        if (usuarioRepository.existsByUsername(usernameNormalizado) || usuarioRepository.existsByCpf(cpfNormalizado)) {
            return;
        }

        Usuario usuario = Usuario.builder()
                .nome(normalizarNome(nome))
                .username(usernameNormalizado)
                .cpf(cpfNormalizado)
                .papel(papel)
                .senha(passwordEncoder.encode(senha))
                .ativo(ativo)
                .build();
        usuarioRepository.save(usuario);
    }

    private String normalizarNome(String nome) {
        String nomeNormalizado = nome.trim().replaceAll("\\s+", " ");
        if (nomeNormalizado.isBlank()) {
            throw new IllegalArgumentException("Nome obrigatório.");
        }
        return nomeNormalizado;
    }

    private String normalizarUsername(String username) {
        String usernameNormalizado = username.trim().toLowerCase();
        if (!USERNAME_PATTERN.matcher(usernameNormalizado).matches()) {
            throw new IllegalArgumentException("Username invalido. Use apenas letras minusculas, números, ponto, underline ou hífen, sem espaços.");
        }
        return usernameNormalizado;
    }

    private String normalizarCpf(String cpf) {
        String cpfNormalizado = cpf.replaceAll("\\D", "");
        if (cpfNormalizado.length() != 11) {
            throw new IllegalArgumentException("CPF invalido.");
        }
        return cpfNormalizado;
    }


    private UsuarioResponse toResponse(Usuario usuario) {
        return new UsuarioResponse(
                usuario.getId(),
                usuario.getNome(),
                usuario.getUsername(),
                usuario.getCpf(),
                usuario.getPapel(),
                usuario.isAtivo());
    }
}


