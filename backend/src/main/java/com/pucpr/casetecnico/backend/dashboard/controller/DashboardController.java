package com.pucpr.casetecnico.backend.dashboard.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import com.pucpr.casetecnico.backend.dashboard.dto.DashboardBlocosResponse;
import com.pucpr.casetecnico.backend.dashboard.dto.DashboardGeralResponse;
import com.pucpr.casetecnico.backend.dashboard.dto.DashboardHorariosResponse;
import com.pucpr.casetecnico.backend.dashboard.dto.DashboardSalasResponse;
import com.pucpr.casetecnico.backend.dashboard.dto.RelatorioUsoAtualResponse;
import com.pucpr.casetecnico.backend.dashboard.dto.RelatorioUsoBlocoResponse;
import com.pucpr.casetecnico.backend.dashboard.service.DashboardService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/dashboards")
@RequiredArgsConstructor
@Tag(name = "Dashboards", description = "Indicadores e relatórios de ocupação")
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/geral")
    @Operation(summary = "Dashboard geral", description = "Retorna a visão executiva com indicadores consolidados")
    public DashboardGeralResponse dashboardGeral() {
        return dashboardService.dashboardGeral();
    }

    @GetMapping("/blocos")
    @Operation(summary = "Dashboard de blocos", description = "Retorna indicadores e ranking por bloco")
    public DashboardBlocosResponse dashboardBlocos() {
        return dashboardService.dashboardBlocos();
    }

    @GetMapping("/salas")
    @Operation(summary = "Dashboard de salas", description = "Retorna indicadores e ranking por sala")
    public DashboardSalasResponse dashboardSalas() {
        return dashboardService.dashboardSalas();
    }

    @GetMapping("/horarios")
    @Operation(summary = "Dashboard de horários", description = "Retorna indicadores de ocupação por faixa horária")
    public DashboardHorariosResponse dashboardHorarios() {
        return dashboardService.dashboardHorarios();
    }

    @GetMapping("/relatorio")
    @Operation(summary = "Relatório de uso por período", description = "Gera um relatório por intervalo de data e hora")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Relatório gerado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Parâmetros de período inválidos")
    })
    public List<RelatorioUsoBlocoResponse> relatorioUso(
            @Parameter(description = "Data e hora inicial do período") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @Parameter(description = "Data e hora final do período") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fim) {
        return dashboardService.relatorioUso(inicio, fim);
    }

    @GetMapping("/relatorio/atual")
    @Operation(summary = "Relatório de uso atual", description = "Retorna a ocupação atual das salas do campus")
    public RelatorioUsoAtualResponse relatorioUsoAtual() {
        return dashboardService.relatorioUsoAtual();
    }
}

