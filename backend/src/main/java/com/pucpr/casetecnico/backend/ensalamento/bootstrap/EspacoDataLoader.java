package com.pucpr.casetecnico.backend.ensalamento.bootstrap;

import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

import com.pucpr.casetecnico.backend.ensalamento.andar.model.Andar;
import com.pucpr.casetecnico.backend.ensalamento.andar.repository.AndarRepository;
import com.pucpr.casetecnico.backend.ensalamento.bloco.model.Bloco;
import com.pucpr.casetecnico.backend.ensalamento.bloco.repository.BlocoRepository;
import com.pucpr.casetecnico.backend.ensalamento.sala.model.Sala;
import com.pucpr.casetecnico.backend.ensalamento.sala.repository.SalaRepository;

@Component
@RequiredArgsConstructor
public class EspacoDataLoader implements CommandLineRunner {

    private final BlocoRepository blocoRepository;
    private final AndarRepository andarRepository;
    private final SalaRepository salaRepository;

    @Override
    public void run(String... args) {
        Bloco blocoAmarelo = criarBlocoSeNaoExistir("Bloco Amarelo");
        Bloco blocoVermelho = criarBlocoSeNaoExistir("Bloco Vermelho");
        Bloco biblioteca = criarBlocoSeNaoExistir("Biblioteca");

        Andar amareloTerreo = criarAndarSeNaoExistir(blocoAmarelo, "Terreo");
        Andar amareloPrimeiro = criarAndarSeNaoExistir(blocoAmarelo, "Andar 1");
        criarSalas(amareloTerreo, List.of(
                sala("Sala A01", 35, 2),
                sala("Sala A02", 30, 2),
                sala("Reuniao A", 10, 4)));
        criarSalas(amareloPrimeiro, List.of(
                sala("Sala A11", 40, 2),
                sala("Sala A12", 30, 2),
                sala("Reuniao B", 12, 4)));

        Andar vermelhoTerreo = criarAndarSeNaoExistir(blocoVermelho, "Terreo");
        Andar vermelhoPrimeiro = criarAndarSeNaoExistir(blocoVermelho, "Andar 1");
        Andar vermelhoSegundo = criarAndarSeNaoExistir(blocoVermelho, "Andar 2");
        criarSalas(vermelhoTerreo, List.of(
                sala("Sala V01", 45, 2),
                sala("Laboratorio Redes", 24, 2)));
        criarSalas(vermelhoPrimeiro, List.of(
                sala("Sala V11", 40, 2),
                sala("Laboratorio Software", 30, 2)));
        criarSalas(vermelhoSegundo, List.of(
                sala("Sala V21", 35, 2),
                sala("Laboratorio Hardware", 20, 2)));

        Andar bibliotecaTerreo = criarAndarSeNaoExistir(biblioteca, "Terreo");
        Andar bibliotecaPrimeiro = criarAndarSeNaoExistir(biblioteca, "Andar 1");
        criarSalas(bibliotecaTerreo, List.of(
                sala("Estudo 01", 8, 1),
                sala("Estudo 02", 8, 1)));
        criarSalas(bibliotecaPrimeiro, List.of(
                sala("Estudo 11", 10, 1),
                sala("Estudo 12", 6, 1)));
    }

    private Bloco criarBlocoSeNaoExistir(String nome) {
        return blocoRepository.findByNomeIgnoreCase(nome)
                .orElseGet(() -> blocoRepository.save(Bloco.builder().nome(nome).ativo(true).build()));
    }

    private Andar criarAndarSeNaoExistir(Bloco bloco, String nome) {
        return andarRepository.findByBlocoIdAndNomeIgnoreCase(bloco.getId(), nome)
                .orElseGet(() -> andarRepository.save(Andar.builder().nome(nome).bloco(bloco).ativo(true).build()));
    }

    private void criarSalas(Andar andar, List<SalaTemplate> salas) {
        for (SalaTemplate template : salas) {
            salaRepository.findByAndarIdAndNomeIgnoreCase(andar.getId(), template.nome())
                    .orElseGet(() -> salaRepository.save(Sala.builder()
                            .nome(template.nome())
                            .lotacaoAlunos(template.lotacaoAlunos())
                            .lotacaoProfessores(template.lotacaoProfessores())
                            .andar(andar)
                            .ativo(true)
                            .build()));
        }
    }

    private SalaTemplate sala(String nome, int lotacaoAlunos, int lotacaoProfessores) {
        return new SalaTemplate(nome, lotacaoAlunos, lotacaoProfessores);
    }

    private record SalaTemplate(String nome, int lotacaoAlunos, int lotacaoProfessores) {
    }
}


