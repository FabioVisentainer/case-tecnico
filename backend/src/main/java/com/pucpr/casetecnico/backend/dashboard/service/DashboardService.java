package com.pucpr.casetecnico.backend.dashboard.service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import com.pucpr.casetecnico.backend.dashboard.dto.BlocoComparativoResponse;
import com.pucpr.casetecnico.backend.dashboard.dto.BlocoHeatmapResponse;
import com.pucpr.casetecnico.backend.dashboard.dto.BlocoKpiResponse;
import com.pucpr.casetecnico.backend.dashboard.dto.BlocoOcupacaoResponse;
import com.pucpr.casetecnico.backend.dashboard.dto.BlocoRankingResponse;
import com.pucpr.casetecnico.backend.dashboard.dto.DashboardBlocosResponse;
import com.pucpr.casetecnico.backend.dashboard.dto.DashboardGeralResponse;
import com.pucpr.casetecnico.backend.dashboard.dto.DashboardHorariosResponse;
import com.pucpr.casetecnico.backend.dashboard.dto.DashboardSalasResponse;
import com.pucpr.casetecnico.backend.dashboard.dto.HeatmapHorarioResponse;
import com.pucpr.casetecnico.backend.dashboard.dto.HorarioPicoResponse;
import com.pucpr.casetecnico.backend.dashboard.dto.OcupacaoFaixaHorariaResponse;
import com.pucpr.casetecnico.backend.dashboard.dto.SalaDispersaoResponse;
import com.pucpr.casetecnico.backend.dashboard.dto.SalaKpiResponse;
import com.pucpr.casetecnico.backend.dashboard.dto.SalaRankingOciosasResponse;
import com.pucpr.casetecnico.backend.dashboard.dto.SalaRankingUtilizadaResponse;
import com.pucpr.casetecnico.backend.dashboard.dto.RelatorioUsoAndarResponse;
import com.pucpr.casetecnico.backend.dashboard.dto.RelatorioUsoAtualResponse;
import com.pucpr.casetecnico.backend.dashboard.dto.RelatorioUsoBlocoResponse;
import com.pucpr.casetecnico.backend.dashboard.dto.RelatorioUsoSalaResponse;
import com.pucpr.casetecnico.backend.dashboard.repository.DashboardRepository;
import com.pucpr.casetecnico.backend.dashboard.dto.SalaUsoAtualResponse;
import com.pucpr.casetecnico.backend.dashboard.dto.SemestreOcupacaoResponse;
import com.pucpr.casetecnico.backend.dashboard.dto.TurnoOcupacaoResponse;
import com.pucpr.casetecnico.backend.ensalamento.sala.repository.SalaRepository;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final DashboardRepository dashboardRepository;
    private final SalaRepository salaRepository;

    @Transactional(readOnly = true)
    public List<RelatorioUsoBlocoResponse> relatorioUso(LocalDateTime inicio, LocalDateTime fim) {
        Instant inicioInstant = inicio.toInstant(ZoneOffset.UTC);
        Instant fimInstant = fim.toInstant(ZoneOffset.UTC);

        if (fimInstant.isBefore(inicioInstant)) {
            throw new IllegalArgumentException("Período inválido. A data fim deve ser maior que o início.");
        }

        long minutosPeriodo = Math.max(1L, java.time.Duration.between(inicioInstant, fimInstant).toMinutes());
        List<DashboardRepository.RelatorioUsoSalaProjection> linhas = dashboardRepository.relatorioUsoPorPeriodo(inicioInstant,
                fimInstant);

        Map<String, BlocoAcumulador> blocos = new LinkedHashMap<>();
        for (DashboardRepository.RelatorioUsoSalaProjection linha : linhas) {
            String blocoNome = linha.getBlocoNome();
            String andarNome = linha.getAndarNome();
            String salaNome = linha.getSalaNome();

            int capacidadeSala = Math.max(1,
                    (linha.getLotacaoAlunos() == null ? 0 : linha.getLotacaoAlunos())
                            + (linha.getLotacaoProfessores() == null ? 0 : linha.getLotacaoProfessores()));
            long minutosPresenca = linha.getTotalMinutosPresenca() == null ? 0L : linha.getTotalMinutosPresenca();
            double percentualSala = percentual(minutosPresenca, minutosPeriodo * capacidadeSala);

            BlocoAcumulador bloco = blocos.computeIfAbsent(blocoNome, nome -> new BlocoAcumulador());
            AndarAcumulador andar = bloco.andares.computeIfAbsent(andarNome, nome -> new AndarAcumulador());

            andar.salas.add(new RelatorioUsoSalaResponse(salaNome, percentualSala));
            andar.minutosPresencaTotal += minutosPresenca;
            andar.capacidadeMinutosTotal += (minutosPeriodo * capacidadeSala);
            bloco.minutosPresencaTotal += minutosPresenca;
            bloco.capacidadeMinutosTotal += (minutosPeriodo * capacidadeSala);
        }

        List<RelatorioUsoBlocoResponse> resposta = new ArrayList<>();
        for (Map.Entry<String, BlocoAcumulador> blocoEntry : blocos.entrySet()) {
            BlocoAcumulador bloco = blocoEntry.getValue();
            List<RelatorioUsoAndarResponse> andares = new ArrayList<>();

            for (Map.Entry<String, AndarAcumulador> andarEntry : bloco.andares.entrySet()) {
                AndarAcumulador andar = andarEntry.getValue();
                andares.add(new RelatorioUsoAndarResponse(
                        andarEntry.getKey(),
                        percentual(andar.minutosPresencaTotal, andar.capacidadeMinutosTotal),
                        andar.salas));
            }

            resposta.add(new RelatorioUsoBlocoResponse(
                    blocoEntry.getKey(),
                    percentual(bloco.minutosPresencaTotal, bloco.capacidadeMinutosTotal),
                    andares));
        }

        return resposta;
    }

    @Transactional(readOnly = true)
    public RelatorioUsoAtualResponse relatorioUsoAtual() {
        List<SalaUsoAtualResponse> salasEmUso = mapearUsoAtualSalas();

        long totalProfessores = salasEmUso.stream().mapToLong(s -> s.professoresPresentes() == null ? 0L : s.professoresPresentes()).sum();
        long totalAlunos = salasEmUso.stream().mapToLong(s -> s.alunosPresentes() == null ? 0L : s.alunosPresentes()).sum();
        double mediaOcupacao = salasEmUso.isEmpty()
                ? 0.0
                : salasEmUso.stream().mapToDouble(SalaUsoAtualResponse::percentualOcupacaoAtual).average().orElse(0.0);

        return new RelatorioUsoAtualResponse(
                Instant.now(),
                (long) salasEmUso.size(),
                totalProfessores,
                totalAlunos,
                arredondar2(mediaOcupacao),
                salasEmUso);
    }

    @Transactional(readOnly = true)
    public DashboardGeralResponse dashboardGeral() {
        Integer capacidadeTotal = salaRepository.somarCapacidadeTotalAtiva();
        long totalSalasAtivas = salaRepository.countByAtivoTrueAndAndarAtivoTrueAndAndarBlocoAtivoTrue();

        List<SalaUsoAtualResponse> usoAtualSalas = mapearUsoAtualSalas();
        long totalPresentesAgora = usoAtualSalas.stream()
                .mapToLong(s -> (s.professoresPresentes() == null ? 0L : s.professoresPresentes())
                        + (s.alunosPresentes() == null ? 0L : s.alunosPresentes()))
                .sum();
        long totalAlunosPresentes = usoAtualSalas.stream().mapToLong(s -> s.alunosPresentes() == null ? 0L : s.alunosPresentes()).sum();

        int capacidade = capacidadeTotal == null ? 0 : capacidadeTotal;
        double taxaMedia = capacidade <= 0 ? 0.0 : arredondar2((totalPresentesAgora * 100.0) / capacidade);
        double taxaOciosidade = arredondar2(Math.max(0.0, 100.0 - taxaMedia));

        List<BlocoOcupacaoResponse> blocos = dashboardRepository.buscarOcupacaoAtualPorBloco().stream()
                .map(item -> {
                    int cap = item.getCapacidadeTotal() == null ? 0 : item.getCapacidadeTotal();
                    long presentes = item.getPresentes() == null ? 0L : item.getPresentes();
                    double percentual = cap <= 0 ? 0.0 : arredondar2((presentes * 100.0) / cap);
                    return new BlocoOcupacaoResponse(item.getBlocoNome(), percentual, presentes, cap);
                })
                .toList();

        Instant agora = Instant.now();
        Instant inicioSemestreAtual = LocalDateTime.now().getMonthValue() <= 6
                ? LocalDateTime.of(LocalDateTime.now().getYear(), 1, 1, 0, 0).toInstant(ZoneOffset.UTC)
                : LocalDateTime.of(LocalDateTime.now().getYear(), 7, 1, 0, 0).toInstant(ZoneOffset.UTC);

        List<DashboardRepository.TurnoOcupacaoProjection> turnoBruto = dashboardRepository
                .buscarOcupacaoPorTurno(inicioSemestreAtual, agora);
        long totalTurno = turnoBruto.stream().mapToLong(t -> t.getTotalCheckins() == null ? 0L : t.getTotalCheckins()).sum();
        List<TurnoOcupacaoResponse> turnos = turnoBruto.stream()
                .map(item -> {
                    long checkins = item.getTotalCheckins() == null ? 0L : item.getTotalCheckins();
                    double percentual = totalTurno <= 0 ? 0.0 : arredondar2((checkins * 100.0) / totalTurno);
                    return new TurnoOcupacaoResponse(item.getTurno(), percentual, checkins);
                })
                .toList();

        Instant inicioEvolucao = LocalDateTime.now().minusYears(3).withMonth(1).withDayOfMonth(1).withHour(0).withMinute(0)
                .withSecond(0).withNano(0).toInstant(ZoneOffset.UTC);
        List<SemestreOcupacaoResponse> evolucao = dashboardRepository.buscarEvolucaoSemestral(inicioEvolucao, agora).stream()
                .map(item -> {
                    long salasUtilizadas = item.getSalasUtilizadas() == null ? 0L : item.getSalasUtilizadas();
                    double percentual = totalSalasAtivas <= 0 ? 0.0 : arredondar2((salasUtilizadas * 100.0) / totalSalasAtivas);
                    int ano = item.getAno() == null ? 0 : item.getAno();
                    int semestreNumero = item.getSemestreNumero() == null ? 0 : item.getSemestreNumero();
                    String semestre = ano + "/" + semestreNumero;
                    return new SemestreOcupacaoResponse(semestre, percentual, salasUtilizadas, totalSalasAtivas);
                })
                .toList();

        return new DashboardGeralResponse(
                agora,
                taxaMedia,
                taxaOciosidade,
                totalSalasAtivas,
                capacidade,
                totalAlunosPresentes,
                blocos,
                turnos,
                evolucao);
    }

    @Transactional(readOnly = true)
    public DashboardBlocosResponse dashboardBlocos() {
        Instant agora = Instant.now();
        Instant inicio12Meses = LocalDateTime.now().minusMonths(12).withHour(0).withMinute(0).withSecond(0).withNano(0).toInstant(ZoneOffset.UTC);

        List<BlocoKpiResponse> kpis = dashboardRepository.buscarEstatisticasPorBloco().stream()
                .map(item -> {
                    Long numeroSalas = item.getNumeroSalas() == null ? 0L : item.getNumeroSalas();
                    Long capacidade = item.getCapacidadeTotal() == null ? 0L : item.getCapacidadeTotal();
                    
                    List<DashboardRepository.BlocoOcupacaoExecutivaProjection> ocupacaoAtual = 
                        dashboardRepository.buscarOcupacaoAtualPorBloco().stream()
                            .filter(b -> b.getBlocoNome().equals(item.getBlocoNome()))
                            .toList();
                    
                    double ocupacaoMedia = 0.0;
                    if (!ocupacaoAtual.isEmpty()) {
                        DashboardRepository.BlocoOcupacaoExecutivaProjection ocu = ocupacaoAtual.get(0);
                        int cap = ocu.getCapacidadeTotal() == null ? 0 : ocu.getCapacidadeTotal();
                        long presentes = ocu.getPresentes() == null ? 0L : ocu.getPresentes();
                        ocupacaoMedia = cap <= 0 ? 0.0 : arredondar2((presentes * 100.0) / cap);
                    }
                    
                    return new BlocoKpiResponse(
                            item.getBlocoNome(),
                            ocupacaoMedia,
                            Math.toIntExact(capacidade),
                            Math.toIntExact(numeroSalas));
                })
                .toList();

        long totalUtilizacoes = dashboardRepository.buscarRankingBlocos(inicio12Meses, agora).stream()
                .mapToLong(r -> r.getUtilizacoes() == null ? 0L : r.getUtilizacoes())
                .sum();

        List<BlocoRankingResponse> ranking = dashboardRepository.buscarRankingBlocos(inicio12Meses, agora).stream()
                .map(item -> {
                    Long utilizacoes = item.getUtilizacoes() == null ? 0L : item.getUtilizacoes();
                    double percentual = totalUtilizacoes <= 0 ? 0.0 : arredondar2((utilizacoes * 100.0) / totalUtilizacoes);
                    return new BlocoRankingResponse(item.getBlocoNome(), utilizacoes, percentual);
                })
                .toList();

        List<BlocoHeatmapResponse> heatmap = dashboardRepository.buscarHeatmapBloco(inicio12Meses, agora).stream()
                .map(item -> new BlocoHeatmapResponse(
                        item.getBlocoNome(),
                        item.getTurno(),
                        item.getCheckins() == null ? 0L : item.getCheckins()))
                .toList();

        List<BlocoComparativoResponse> comparativo = dashboardRepository.buscarOcupacaoAtualPorBloco().stream()
                .map(item -> {
                    int capacidade = item.getCapacidadeTotal() == null ? 0 : item.getCapacidadeTotal();
                    long presentes = item.getPresentes() == null ? 0L : item.getPresentes();
                    double percentualOcupacao = capacidade <= 0 ? 0.0 : arredondar2((presentes * 100.0) / capacidade);
                    return new BlocoComparativoResponse(
                            item.getBlocoNome(),
                            capacidade,
                            presentes,
                            percentualOcupacao);
                })
                .toList();

        return new DashboardBlocosResponse(agora, kpis, ranking, heatmap, comparativo);
    }

    @Transactional(readOnly = true)
    public DashboardSalasResponse dashboardSalas() {
        Instant agora = Instant.now();
        Instant inicio12Meses = LocalDateTime.now().minusMonths(12).withHour(0).withMinute(0).withSecond(0).withNano(0).toInstant(ZoneOffset.UTC);
        
        long totalHorasDisponiveis = 12 * 30 * 24;

        List<SalaKpiResponse> kpis = salaRepository.findAll().stream()
                .filter(sala -> sala.isAtivo() && sala.getAndar().isAtivo() && sala.getAndar().getBloco().isAtivo())
                .map(sala -> {
                    int capacidade = Math.max(1, 
                            (sala.getLotacaoAlunos() == null ? 0 : sala.getLotacaoAlunos()) + 
                            (sala.getLotacaoProfessores() == null ? 0 : sala.getLotacaoProfessores()));
                    
                    List<DashboardRepository.SalaUtilizacaoProjection> utilizacao = 
                        dashboardRepository.buscarSalasUtilizacaoPorPeriodo(inicio12Meses, agora).stream()
                            .filter(s -> s.getSalaId().equals(sala.getId()))
                            .toList();
                    
                    long minutosPresenca = 0;
                    long checkins = 0;
                    if (!utilizacao.isEmpty()) {
                        DashboardRepository.SalaUtilizacaoProjection util = utilizacao.get(0);
                        minutosPresenca = util.getMinutosPresenca() == null ? 0L : util.getMinutosPresenca();
                        checkins = util.getCheckins() == null ? 0L : util.getCheckins();
                    }
                    
                    long horasUtilizadas = minutosPresenca / 60;
                    double taxaOcupacao = checkins > 0 ? arredondar2((checkins / (double)(totalHorasDisponiveis * 2)) * 100) : 0.0;
                    double taxaCapacidade = arredondar2((minutosPresenca / 60.0) / (totalHorasDisponiveis * capacidade) * 100);
                    
                    return new SalaKpiResponse(
                            sala.getId(),
                            sala.getNome(),
                            sala.getAndar().getBloco().getNome(),
                            sala.getAndar().getNome(),
                            taxaOcupacao,
                            taxaCapacidade,
                            horasUtilizadas,
                            totalHorasDisponiveis);
                })
                .toList();

        long totalUtilizacoes = dashboardRepository.buscarSalasUtilizacaoPorPeriodo(inicio12Meses, agora).stream()
                .mapToLong(s -> s.getCheckins() == null ? 0L : s.getCheckins())
                .sum();

        List<SalaRankingUtilizadaResponse> rankingUtilizadas = dashboardRepository.buscarSalasUtilizacaoPorPeriodo(inicio12Meses, agora)
                .stream()
                .limit(10)
                .map(item -> {
                    Long checkins = item.getCheckins() == null ? 0L : item.getCheckins();
                    Long minutos = item.getMinutosPresenca() == null ? 0L : item.getMinutosPresenca();
                    double percentual = totalUtilizacoes <= 0 ? 0.0 : arredondar2((checkins * 100.0) / totalUtilizacoes);
                    return new SalaRankingUtilizadaResponse(
                            item.getSalaId(),
                            item.getSalaNome(),
                            item.getBlocoNome(),
                            item.getAndarNome(),
                            checkins,
                            minutos,
                            percentual);
                })
                .toList();

        List<SalaRankingOciosasResponse> rankingOciosas = dashboardRepository.buscarSalasOciosasPortiodo(inicio12Meses, agora)
                .stream()
                .limit(10)
                .map(item -> {
                    Long checkins = item.getCheckins() == null ? 0L : item.getCheckins();
                    double percentualOciosidade = 100.0 - (totalUtilizacoes <= 0 ? 0.0 : arredondar2((checkins * 100.0) / totalUtilizacoes));
                    return new SalaRankingOciosasResponse(
                            item.getSalaId(),
                            item.getSalaNome(),
                            item.getBlocoNome(),
                            item.getAndarNome(),
                            checkins,
                            percentualOciosidade);
                })
                .toList();

        List<SalaDispersaoResponse> dispersao = dashboardRepository.buscarUsoAtualSalas().stream()
                .map(item -> {
                    int capacidade = Math.max(1,
                            (item.getLotacaoAlunos() == null ? 0 : item.getLotacaoAlunos()) + 
                            (item.getLotacaoProfessores() == null ? 0 : item.getLotacaoProfessores()));
                    long presentes = (item.getProfessoresPresentes() == null ? 0L : item.getProfessoresPresentes()) +
                            (item.getAlunosPresentes() == null ? 0L : item.getAlunosPresentes());
                    double percentualOcupacao = arredondar2((presentes * 100.0) / capacidade);
                    return new SalaDispersaoResponse(
                            null,
                            item.getSalaNome(),
                            item.getBlocoNome(),
                            item.getAndarNome(),
                            capacidade,
                            presentes,
                            percentualOcupacao);
                })
                .toList();

        return new DashboardSalasResponse(agora, kpis, rankingUtilizadas, rankingOciosas, dispersao);
    }

    @Transactional(readOnly = true)
    public DashboardHorariosResponse dashboardHorarios() {
        Instant agora = Instant.now();
        Instant inicio7Dias = LocalDateTime.now().minusDays(7).withHour(0).withMinute(0).withSecond(0).withNano(0).toInstant(ZoneOffset.UTC);

        List<HorarioPicoResponse> kpis = new ArrayList<>();
        
        List<DashboardRepository.HorarioPicoProjection> picos = dashboardRepository.buscarHorariosPico(inicio7Dias, agora);
        long totalCheckinsPico = picos.stream().mapToLong(h -> h.getCheckins() == null ? 0L : h.getCheckins()).sum();
        
        for (DashboardRepository.HorarioPicoProjection pico : picos.stream().limit(3).toList()) {
            Long checkins = pico.getCheckins() == null ? 0L : pico.getCheckins();
            double percentual = totalCheckinsPico <= 0 ? 0.0 : arredondar2((checkins * 100.0) / totalCheckinsPico);
            String hora = String.format("%02d:00", pico.getHora());
            kpis.add(new HorarioPicoResponse("🔴 Pico", hora, checkins, percentual));
        }
        
        List<DashboardRepository.HorarioPicoProjection> baixos = dashboardRepository.buscarHorariosBaixaOcupacao(inicio7Dias, agora);
        long totalCheckinsBaixo = baixos.stream().mapToLong(h -> h.getCheckins() == null ? 0L : h.getCheckins()).sum();
        
        for (DashboardRepository.HorarioPicoProjection baixo : baixos.stream().limit(3).toList()) {
            Long checkins = baixo.getCheckins() == null ? 0L : baixo.getCheckins();
            double percentual = totalCheckinsBaixo <= 0 ? 0.0 : arredondar2((checkins * 100.0) / totalCheckinsBaixo);
            String hora = String.format("%02d:00", baixo.getHora());
            kpis.add(new HorarioPicoResponse("💤 Baixa", hora, checkins, percentual));
        }

        Map<String, String> diasSemana = new LinkedHashMap<>();
        diasSemana.put("1", "Domingo");
        diasSemana.put("2", "Segunda");
        diasSemana.put("3", "Terça");
        diasSemana.put("4", "Quarta");
        diasSemana.put("5", "Quinta");
        diasSemana.put("6", "Sexta");
        diasSemana.put("7", "Sábado");

        List<HeatmapHorarioResponse> heatmap = dashboardRepository.buscarHeatmapHorario(inicio7Dias, agora).stream()
                .map(item -> new HeatmapHorarioResponse(
                        item.getDiaSemana(),
                        diasSemana.getOrDefault(item.getDiaSemana().toString(), "Desconhecido"),
                        item.getHora(),
                        item.getCheckins() == null ? 0L : item.getCheckins()))
                .toList();

        List<OcupacaoFaixaHorariaResponse> ocupacaoFaixa = dashboardRepository.buscarOcupacaoPorFaixaHoraria(inicio7Dias, agora).stream()
                .map(item -> {
                    Long checkins = item.getCheckins() == null ? 0L : item.getCheckins();
                    return new OcupacaoFaixaHorariaResponse(item.getFaixa(), checkins, 0.0);
                })
                .toList();

        long totalFaixa = ocupacaoFaixa.stream().mapToLong(OcupacaoFaixaHorariaResponse::checkins).sum();
        List<OcupacaoFaixaHorariaResponse> ocupacaoFaixaComPercentual = ocupacaoFaixa.stream()
                .map(item -> {
                    double percentual = totalFaixa <= 0 ? 0.0 : arredondar2((item.checkins() * 100.0) / totalFaixa);
                    return new OcupacaoFaixaHorariaResponse(item.faixa(), item.checkins(), percentual);
                })
                .toList();

        List<TurnoOcupacaoResponse> ocupacaoTurno = dashboardRepository.buscarOcupacaoPorTurno(inicio7Dias, agora).stream()
                .map(item -> {
                    long checkins = item.getTotalCheckins() == null ? 0L : item.getTotalCheckins();
                    long totalTurno = dashboardRepository.buscarOcupacaoPorTurno(inicio7Dias, agora).stream()
                            .mapToLong(t -> t.getTotalCheckins() == null ? 0L : t.getTotalCheckins()).sum();
                    double percentual = totalTurno <= 0 ? 0.0 : arredondar2((checkins * 100.0) / totalTurno);
                    return new TurnoOcupacaoResponse(item.getTurno(), percentual, checkins);
                })
                .toList();

        return new DashboardHorariosResponse(agora, kpis, heatmap, ocupacaoFaixaComPercentual, ocupacaoTurno);
    }

    private List<SalaUsoAtualResponse> mapearUsoAtualSalas() {
        return dashboardRepository.buscarUsoAtualSalas().stream()
                .map(item -> {
                    long professores = item.getProfessoresPresentes() == null ? 0L : item.getProfessoresPresentes();
                    long alunos = item.getAlunosPresentes() == null ? 0L : item.getAlunosPresentes();
                    int capacidade = Math.max(1,
                            (item.getLotacaoAlunos() == null ? 0 : item.getLotacaoAlunos())
                                    + (item.getLotacaoProfessores() == null ? 0 : item.getLotacaoProfessores()));
                    double percentual = arredondar2(((professores + alunos) * 100.0) / capacidade);
                    return new SalaUsoAtualResponse(
                            item.getBlocoNome(),
                            item.getAndarNome(),
                            item.getSalaNome(),
                            professores,
                            alunos,
                            capacidade,
                            percentual);
                })
                .toList();
    }

    private double percentual(long numerador, long denominador) {
        if (denominador <= 0) {
            return 0.0;
        }
        double valor = (numerador * 100.0) / denominador;
        return Math.max(0.0, Math.min(100.0, Math.round(valor * 100.0) / 100.0));
    }

    private static class BlocoAcumulador {
        private final Map<String, AndarAcumulador> andares = new LinkedHashMap<>();
        private long minutosPresencaTotal;
        private long capacidadeMinutosTotal;
    }

    private static class AndarAcumulador {
        private final List<RelatorioUsoSalaResponse> salas = new ArrayList<>();
        private long minutosPresencaTotal;
        private long capacidadeMinutosTotal;
    }

    private double arredondar2(double valor) {
        return Math.round(valor * 100.0) / 100.0;
    }
}

