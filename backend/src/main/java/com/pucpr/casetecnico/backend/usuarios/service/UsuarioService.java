package com.pucpr.casetecnico.backend.usuarios.service;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

import com.pucpr.casetecnico.backend.ensalamento.presenca.model.PresencaSala;
import com.pucpr.casetecnico.backend.ensalamento.presenca.service.PresencaSalaService;
import com.pucpr.casetecnico.backend.usuarios.model.EnumPapelUsuario;
import com.pucpr.casetecnico.backend.usuarios.model.Usuario;
import com.pucpr.casetecnico.backend.usuarios.repository.UsuarioRepository;
import com.pucpr.casetecnico.backend.usuarios.dto.UsuarioCadastroRequest;
import com.pucpr.casetecnico.backend.usuarios.dto.UsuarioAtualizacaoRequest;
import com.pucpr.casetecnico.backend.usuarios.dto.UsuarioPageResponse;
import com.pucpr.casetecnico.backend.usuarios.dto.UsuarioResponse;

@Service
@RequiredArgsConstructor
public class UsuarioService implements UserDetailsService {

    private static final Pattern USERNAME_PATTERN = Pattern.compile("^[a-z0-9._-]+$");

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final PresencaSalaService presencaSalaService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado."));

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
            throw new IllegalArgumentException("Administrador não pode ser criado por este endpoint.");
        }

        if (usuarioRepository.existsByUsername(usernameNormalizado)) {
            throw new IllegalArgumentException("Já existe usuário com esse username.");
        }

        if (usuarioRepository.existsByCpf(cpfNormalizado)) {
            throw new IllegalArgumentException("Já existe usuário com esse CPF.");
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

    public UsuarioPageResponse listarPaginado(int page, int size, boolean mostrarInativos, String q, EnumPapelUsuario papel) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Order.asc("nome").ignoreCase()));

        Specification<Usuario> specification = (root, query, builder) -> {
            var predicates = builder.conjunction();

            if (!mostrarInativos) {
                predicates = builder.and(predicates, builder.isTrue(root.get("ativo")));
            }

            if (papel != null) {
                predicates = builder.and(predicates, builder.equal(root.get("papel"), papel));
            }

            if (q != null && !q.isBlank()) {
                String termo = "%" + q.trim().toLowerCase() + "%";
                predicates = builder.and(predicates, builder.or(
                        builder.like(builder.lower(root.get("nome")), termo),
                        builder.like(builder.lower(root.get("username")), termo)));
            }

            return predicates;
        };

        Page<Usuario> usuarios = usuarioRepository.findAll(specification, pageable);

        List<Long> idsPagina = usuarios.getContent().stream().map(Usuario::getId).toList();
        Map<Long, PresencaSala> presencasAtivasPorUsuario = presencaSalaService.buscarAtivasPorUsuarioIds(idsPagina).stream()
                .collect(Collectors.toMap(p -> p.getUsuario().getId(), Function.identity()));

        Page<UsuarioResponse> responsePage = usuarios.map(usuario -> toResponse(usuario, presencasAtivasPorUsuario.get(usuario.getId())));
        return new UsuarioPageResponse(
                responsePage.getContent(),
                responsePage.getTotalElements(),
                responsePage.getTotalPages(),
                responsePage.getNumber(),
                responsePage.getSize(),
                responsePage.isFirst(),
                responsePage.isLast());
    }

    public UsuarioResponse atualizarPorAdmin(Long id, UsuarioAtualizacaoRequest request) {
        Usuario usuario = buscarPorId(id);

        String nomeNormalizado = normalizarNome(request.nome());
        String usernameNormalizado = normalizarUsername(request.username());
        String cpfNormalizado = normalizarCpf(request.cpf());

        if (!usernameNormalizado.equals(usuario.getUsername()) || !cpfNormalizado.equals(usuario.getCpf())) {
            throw new IllegalArgumentException("CPF e username não podem ser alterados.");
        }

        if (request.papel() == EnumPapelUsuario.ADMINISTRADOR) {
            throw new IllegalArgumentException("Administrador não pode ser criado por este endpoint.");
        }

        usuario.setNome(nomeNormalizado);
        usuario.setPapel(request.papel());
        usuario.setAtivo(request.ativo() == null || request.ativo());

        if (request.senha() != null && !request.senha().isBlank()) {
            usuario.setSenha(passwordEncoder.encode(request.senha()));
        }

        Usuario salvo = usuarioRepository.save(usuario);
        return toResponse(salvo);
    }

    public UsuarioResponse alterarStatus(Long id, boolean ativo) {
        Usuario usuario = buscarPorId(id);
        usuario.setAtivo(ativo);
        Usuario salvo = usuarioRepository.save(usuario);
        return toResponse(salvo);
    }

    public UsuarioResponse buscarPorIdResponse(Long id) {
        Usuario usuario = buscarPorId(id);
        PresencaSala presencaAtiva = presencaSalaService.buscarAtivasPorUsuarioIds(List.of(id)).stream()
                .findFirst()
                .orElse(null);
        return toResponse(usuario, presencaAtiva);
    }

    public Usuario buscarPorUsername(String username) {
        return usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado."));
    }

    private Usuario buscarPorId(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado."));
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
            throw new IllegalArgumentException("Username inválido. Use apenas letras minúsculas, números, ponto, underscore ou hífen, sem espaços.");
        }
        return usernameNormalizado;
    }

    private String normalizarCpf(String cpf) {
        String cpfNormalizado = cpf.replaceAll("\\D", "");
        if (cpfNormalizado.length() != 11) {
            throw new IllegalArgumentException("CPF inválido.");
        }
        return cpfNormalizado;
    }


    private UsuarioResponse toResponse(Usuario usuario) {
        return toResponse(usuario, null);
    }

    private UsuarioResponse toResponse(Usuario usuario, PresencaSala presencaAtiva) {
        boolean emSala = presencaAtiva != null;
        String blocoAtual = emSala ? presencaAtiva.getSala().getAndar().getBloco().getNome() : null;
        String andarAtual = emSala ? presencaAtiva.getSala().getAndar().getNome() : null;
        String salaAtual = emSala ? presencaAtiva.getSala().getNome() : null;
        Instant entradaAtual = emSala ? presencaAtiva.getEntrada() : null;

        return new UsuarioResponse(
                usuario.getId(),
                usuario.getNome(),
                usuario.getUsername(),
                usuario.getCpf(),
                usuario.getPapel(),
                usuario.isAtivo(),
                emSala,
                blocoAtual,
                andarAtual,
                salaAtual,
                entradaAtual);
    }
}


