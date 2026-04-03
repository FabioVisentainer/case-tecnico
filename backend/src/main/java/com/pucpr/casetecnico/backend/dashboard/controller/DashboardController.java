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

@RestController
@RequestMapping("/api/dashboards")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/geral")
    public DashboardGeralResponse dashboardGeral() {
        return dashboardService.dashboardGeral();
    }

    @GetMapping("/blocos")
    public DashboardBlocosResponse dashboardBlocos() {
        return dashboardService.dashboardBlocos();
    }

    @GetMapping("/salas")
    public DashboardSalasResponse dashboardSalas() {
        return dashboardService.dashboardSalas();
    }

    @GetMapping("/horarios")
    public DashboardHorariosResponse dashboardHorarios() {
        return dashboardService.dashboardHorarios();
    }

    @GetMapping("/relatorio")
    public List<RelatorioUsoBlocoResponse> relatorioUso(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fim) {
        return dashboardService.relatorioUso(inicio, fim);
    }

    @GetMapping("/relatorio/atual")
    public RelatorioUsoAtualResponse relatorioUsoAtual() {
        return dashboardService.relatorioUsoAtual();
    }
}

