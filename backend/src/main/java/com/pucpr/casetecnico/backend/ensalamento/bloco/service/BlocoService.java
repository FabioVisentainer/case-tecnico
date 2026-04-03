package com.pucpr.casetecnico.backend.ensalamento.bloco.service;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.pucpr.casetecnico.backend.ensalamento.bloco.dto.BlocoCadastroRequest;
import com.pucpr.casetecnico.backend.ensalamento.bloco.dto.BlocoDetalhesResponse;
import com.pucpr.casetecnico.backend.ensalamento.bloco.dto.BlocoResponse;
import com.pucpr.casetecnico.backend.ensalamento.bloco.dto.AndarResumoResponse;
import com.pucpr.casetecnico.backend.ensalamento.andar.repository.AndarRepository;
import com.pucpr.casetecnico.backend.ensalamento.sala.repository.SalaRepository;
import com.pucpr.casetecnico.backend.ensalamento.bloco.model.Bloco;
import com.pucpr.casetecnico.backend.ensalamento.bloco.repository.BlocoRepository;

@Service
@RequiredArgsConstructor
public class BlocoService {

    private static final Pattern NATURAL_TOKEN_PATTERN = Pattern.compile("\\d+|\\D+");

    private final BlocoRepository blocoRepository;
    private final AndarRepository andarRepository;
    private final SalaRepository salaRepository;

    @Transactional
    public BlocoResponse cadastrar(BlocoCadastroRequest request) {
        String nomeNormalizado = normalizarNome(request.nome());

        if (blocoRepository.existsByNomeIgnoreCase(nomeNormalizado)) {
            throw new IllegalArgumentException("Já existe um bloco com esse nome.");
        }

        Bloco bloco = Bloco.builder()
            .nome(nomeNormalizado)
            .ativo(true)
            .build();
        
        Bloco saved = blocoRepository.save(bloco);
        return toBlocoResponse(saved);
    }

    @Transactional(readOnly = true)
    public Page<BlocoResponse> listarPaginado(int page, int size, boolean mostrarInativos, String q) {
        PageRequest pageable = PageRequest.of(page, size);
        Specification<Bloco> specification = (root, query, builder) -> {
            var predicates = builder.conjunction();

            if (!mostrarInativos) {
                predicates = builder.and(predicates, builder.isTrue(root.get("ativo")));
            }

            if (q != null && !q.isBlank()) {
                String termo = "%" + q.trim().toLowerCase() + "%";
                predicates = builder.and(predicates, builder.like(builder.lower(root.get("nome")), termo));
            }

            return predicates;
        };

        // Ordenação natural para nomes com números (ex: Bloco 9, Bloco 09, Bloco 10)
        List<Bloco> filtrados = blocoRepository.findAll(specification);
        filtrados.sort(this::compareNomeNatural);

        int inicio = Math.min((int) pageable.getOffset(), filtrados.size());
        int fim = Math.min(inicio + pageable.getPageSize(), filtrados.size());
        List<BlocoResponse> pagina = filtrados.subList(inicio, fim).stream().map(this::toBlocoResponse).toList();

        return new PageImpl<>(pagina, pageable, filtrados.size());
    }

    @Transactional(readOnly = true)
    public BlocoDetalhesResponse buscarComDetalhes(Long id) {
        Bloco bloco = blocoRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Bloco não encontrado."));
        
        var andares = bloco.getAndares().stream()
            .map(andar -> new AndarResumoResponse(andar.getId(), andar.getNome(), andar.isAtivo()))
            .toList();
        
        return new BlocoDetalhesResponse(
            bloco.getId(),
            bloco.getNome(),
            bloco.isAtivo(),
            andares
        );
    }

    @Transactional
    public BlocoResponse atualizar(Long id, BlocoCadastroRequest request) {
        Bloco bloco = blocoRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Bloco não encontrado."));

        String nomeNormalizado = normalizarNome(request.nome());
        if (blocoRepository.existsByNomeIgnoreCaseAndIdNot(nomeNormalizado, id)) {
            throw new IllegalArgumentException("Ja existe bloco com esse nome.");
        }

        bloco.setNome(nomeNormalizado);
        Bloco updated = blocoRepository.save(bloco);
        return toBlocoResponse(updated);
    }

    @Transactional
    public void deletar(Long id) {
        alterarStatus(id, false);
    }

    @Transactional
    public BlocoResponse alterarStatus(Long id, boolean ativo) {
        Bloco bloco = blocoRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Bloco não encontrado."));

        bloco.setAtivo(ativo);
        andarRepository.findAllByBlocoId(bloco.getId()).forEach(andar -> {
            andar.setAtivo(ativo);
            salaRepository.findAllByAndarId(andar.getId()).forEach(sala -> sala.setAtivo(ativo));
        });
        Bloco salvo = blocoRepository.save(bloco);
        return toBlocoResponse(salvo);
    }

    private String normalizarNome(String nome) {
        String nomeNormalizado = nome == null ? "" : nome.trim().replaceAll("\\s+", " ");
        if (nomeNormalizado.isBlank()) {
            throw new IllegalArgumentException("Nome do bloco é obrigatório.");
        }
        return nomeNormalizado;
    }

    private BlocoResponse toBlocoResponse(Bloco bloco) {
        return new BlocoResponse(
            bloco.getId(),
            bloco.getNome(),
            bloco.isAtivo(),
            andarRepository.countByBlocoId(bloco.getId()),
            salaRepository.countByAndarBlocoId(bloco.getId())
        );
    }

    private int compareNomeNatural(Bloco blocoA, Bloco blocoB) {
        List<String> tokensA = tokenizarNome(blocoA.getNome());
        List<String> tokensB = tokenizarNome(blocoB.getNome());
        int limite = Math.min(tokensA.size(), tokensB.size());

        for (int i = 0; i < limite; i++) {
            String tokenA = tokensA.get(i);
            String tokenB = tokensB.get(i);
            boolean numeroA = Character.isDigit(tokenA.charAt(0));
            boolean numeroB = Character.isDigit(tokenB.charAt(0));

            if (numeroA && numeroB) {
                BigInteger numeroTokenA = new BigInteger(tokenA);
                BigInteger numeroTokenB = new BigInteger(tokenB);
                int comparacaoNumero = numeroTokenA.compareTo(numeroTokenB);
                if (comparacaoNumero != 0) {
                    return comparacaoNumero;
                }

                // Se os valores forem iguais (09 e 9), o mais curto vem primeiro.
                int comparacaoTamanho = Integer.compare(tokenA.length(), tokenB.length());
                if (comparacaoTamanho != 0) {
                    return comparacaoTamanho;
                }
            } else {
                int comparacaoTexto = tokenA.compareToIgnoreCase(tokenB);
                if (comparacaoTexto != 0) {
                    return comparacaoTexto;
                }
            }
        }

        int comparacaoQuantidadeTokens = Integer.compare(tokensA.size(), tokensB.size());
        if (comparacaoQuantidadeTokens != 0) {
            return comparacaoQuantidadeTokens;
        }

        return blocoA.getNome().compareToIgnoreCase(blocoB.getNome());
    }

    private List<String> tokenizarNome(String nome) {
        List<String> tokens = new ArrayList<>();
        Matcher matcher = NATURAL_TOKEN_PATTERN.matcher(nome == null ? "" : nome);
        while (matcher.find()) {
            tokens.add(matcher.group());
        }
        return tokens;
    }
}