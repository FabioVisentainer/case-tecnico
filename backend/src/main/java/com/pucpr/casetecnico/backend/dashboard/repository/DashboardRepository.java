package com.pucpr.casetecnico.backend.dashboard.repository;

import java.time.Instant;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.pucpr.casetecnico.backend.ensalamento.presenca.model.PresencaSala;

public interface DashboardRepository extends JpaRepository<PresencaSala, Long> {

    @Query(value = """
            SELECT b.nome AS blocoNome,
                   a.nome AS andarNome,
                   s.nome AS salaNome,
                   SUM(CASE WHEN u.papel = 'PROFESSOR' THEN 1 ELSE 0 END) AS professoresPresentes,
                   SUM(CASE WHEN u.papel = 'ALUNO' THEN 1 ELSE 0 END) AS alunosPresentes,
                   s.lotacao_alunos AS lotacaoAlunos,
                   s.lotacao_professores AS lotacaoProfessores
              FROM presencas_sala p
              JOIN usuarios u ON u.id = p.usuario_id
              JOIN salas s ON s.id = p.sala_id
              JOIN andares a ON a.id = s.andar_id
              JOIN blocos b ON b.id = a.bloco_id
             WHERE p.saida IS NULL
               AND s.ativo = true
               AND a.ativo = true
               AND b.ativo = true
             GROUP BY b.nome, a.nome, s.nome, s.lotacao_alunos, s.lotacao_professores
             ORDER BY b.nome, a.nome, s.nome
            """, nativeQuery = true)
    List<UsoSalaAtualProjection> buscarUsoAtualSalas();

    @Query(value = """
            SELECT b.nome AS blocoNome,
                   COUNT(*) AS presentes,
                   SUM(s.lotacao_alunos + s.lotacao_professores) AS capacidadeTotal
              FROM presencas_sala p
              JOIN salas s ON s.id = p.sala_id
              JOIN andares a ON a.id = s.andar_id
              JOIN blocos b ON b.id = a.bloco_id
             WHERE p.saida IS NULL
               AND s.ativo = true
               AND a.ativo = true
               AND b.ativo = true
             GROUP BY b.nome
             ORDER BY b.nome
            """, nativeQuery = true)
    List<BlocoOcupacaoExecutivaProjection> buscarOcupacaoAtualPorBloco();

    @Query(value = """
            SELECT CASE
                     WHEN HOUR(p.entrada) BETWEEN 6 AND 11 THEN 'Manha'
                     WHEN HOUR(p.entrada) BETWEEN 12 AND 17 THEN 'Tarde'
                     ELSE 'Noite'
                   END AS turno,
                   COUNT(*) AS totalCheckins
              FROM presencas_sala p
             WHERE p.entrada >= :inicio
               AND p.entrada <= :fim
             GROUP BY turno
            """, nativeQuery = true)
    List<TurnoOcupacaoProjection> buscarOcupacaoPorTurno(@Param("inicio") Instant inicio, @Param("fim") Instant fim);

    @Query(value = """
            SELECT CAST(base.ano AS SIGNED) AS ano,
                   CAST(base.semestreNumero AS SIGNED) AS semestreNumero,
                   COUNT(DISTINCT base.sala_id) AS salasUtilizadas
              FROM (
                    SELECT YEAR(p.entrada) AS ano,
                           CASE WHEN MONTH(p.entrada) <= 6 THEN 1 ELSE 2 END AS semestreNumero,
                           p.sala_id
                      FROM presencas_sala p
                     WHERE p.entrada >= :inicio
                       AND p.entrada <= :fim
                   ) base
             GROUP BY base.ano, base.semestreNumero
             ORDER BY base.ano, base.semestreNumero
            """, nativeQuery = true)
    List<SemestreUsoProjection> buscarEvolucaoSemestral(@Param("inicio") Instant inicio, @Param("fim") Instant fim);

    @Query(value = """
            SELECT b.nome AS blocoNome,
                   a.nome AS andarNome,
                   s.nome AS salaNome,
                   s.lotacao_alunos AS lotacaoAlunos,
                   s.lotacao_professores AS lotacaoProfessores,
                   COALESCE(SUM(TIMESTAMPDIFF(MINUTE, p.entrada, COALESCE(p.saida, CURRENT_TIMESTAMP))), 0) AS totalMinutosPresenca
              FROM presencas_sala p
              JOIN salas s ON s.id = p.sala_id
              JOIN andares a ON a.id = s.andar_id
              JOIN blocos b ON b.id = a.bloco_id
             WHERE p.entrada >= :inicio
               AND p.entrada <= :fim
             GROUP BY b.nome, a.nome, s.nome
             ORDER BY b.nome, a.nome, s.nome
            """, nativeQuery = true)
    List<RelatorioUsoSalaProjection> relatorioUsoPorPeriodo(@Param("inicio") Instant inicio, @Param("fim") Instant fim);

    @Query(value = """
            SELECT b.nome AS blocoNome,
                   COUNT(DISTINCT s.id) AS numeroSalas,
                   COALESCE(SUM(s.lotacao_alunos + s.lotacao_professores), 0) AS capacidadeTotal
              FROM blocos b
              LEFT JOIN andares a ON a.bloco_id = b.id
              LEFT JOIN salas s ON s.andar_id = a.id
             WHERE b.ativo = true
               AND a.ativo = true
               AND s.ativo = true
             GROUP BY b.nome
             ORDER BY b.nome
            """, nativeQuery = true)
    List<BlocoEstatisticasProjection> buscarEstatisticasPorBloco();

    @Query(value = """
            SELECT b.nome AS blocoNome,
                   CASE WHEN HOUR(p.entrada) BETWEEN 6 AND 11 THEN 'Manha'
                        WHEN HOUR(p.entrada) BETWEEN 12 AND 17 THEN 'Tarde'
                        ELSE 'Noite'
                   END AS turno,
                   COUNT(*) AS checkins
              FROM presencas_sala p
              JOIN salas s ON s.id = p.sala_id
              JOIN andares a ON a.id = s.andar_id
              JOIN blocos b ON b.id = a.bloco_id
             WHERE p.entrada >= :inicio
               AND p.entrada <= :fim
               AND s.ativo = true
               AND a.ativo = true
               AND b.ativo = true
             GROUP BY b.nome, turno
             ORDER BY b.nome, CASE WHEN turno = 'Manha' THEN 1 WHEN turno = 'Tarde' THEN 2 ELSE 3 END
            """, nativeQuery = true)
    List<BlocoHeatmapProjection> buscarHeatmapBloco(@Param("inicio") Instant inicio, @Param("fim") Instant fim);

    @Query(value = """
            SELECT b.nome AS blocoNome,
                   COUNT(p.id) AS utilizacoes
              FROM presencas_sala p
              JOIN salas s ON s.id = p.sala_id
              JOIN andares a ON a.id = s.andar_id
              JOIN blocos b ON b.id = a.bloco_id
             WHERE p.entrada >= :inicio
               AND p.entrada <= :fim
               AND s.ativo = true
               AND a.ativo = true
               AND b.ativo = true
             GROUP BY b.nome
             ORDER BY utilizacoes DESC
            """, nativeQuery = true)
    List<BlocoRankingProjection> buscarRankingBlocos(@Param("inicio") Instant inicio, @Param("fim") Instant fim);

    @Query(value = """
            SELECT s.id AS salaId,
                   s.nome AS salaNome,
                   b.nome AS blocoNome,
                   a.nome AS andarNome,
                   COALESCE(SUM(TIMESTAMPDIFF(MINUTE, p.entrada, COALESCE(p.saida, CURRENT_TIMESTAMP))), 0) AS minutosPresenca,
                   COUNT(p.id) AS checkins
              FROM salas s
              LEFT JOIN andares a ON a.id = s.andar_id
              LEFT JOIN blocos b ON b.id = a.bloco_id
              LEFT JOIN presencas_sala p ON p.sala_id = s.id
                 AND p.entrada >= :inicio
                 AND p.entrada <= :fim
             WHERE s.ativo = true
               AND a.ativo = true
               AND b.ativo = true
             GROUP BY s.id, s.nome, b.nome, a.nome
             ORDER BY minutosPresenca DESC
            """, nativeQuery = true)
    List<SalaUtilizacaoProjection> buscarSalasUtilizacaoPorPeriodo(@Param("inicio") Instant inicio, @Param("fim") Instant fim);

    @Query(value = """
            SELECT s.id AS salaId,
                   s.nome AS salaNome,
                   b.nome AS blocoNome,
                   a.nome AS andarNome,
                   COALESCE(SUM(TIMESTAMPDIFF(MINUTE, p.entrada, COALESCE(p.saida, CURRENT_TIMESTAMP))), 0) AS minutosPresenca,
                   COUNT(p.id) AS checkins
              FROM salas s
              LEFT JOIN andares a ON a.id = s.andar_id
              LEFT JOIN blocos b ON b.id = a.bloco_id
              LEFT JOIN presencas_sala p ON p.sala_id = s.id
                 AND p.entrada >= :inicio
                 AND p.entrada <= :fim
             WHERE s.ativo = true
               AND a.ativo = true
               AND b.ativo = true
             GROUP BY s.id, s.nome, b.nome, a.nome
             ORDER BY minutosPresenca ASC
            """, nativeQuery = true)
    List<SalaUtilizacaoProjection> buscarSalasOciosasPortiodo(@Param("inicio") Instant inicio, @Param("fim") Instant fim);

    @Query(value = """
            SELECT DAYOFWEEK(p.entrada) AS diaSemana,
                   HOUR(p.entrada) AS hora,
                   COUNT(*) AS checkins
              FROM presencas_sala p
             WHERE p.entrada >= :inicio
               AND p.entrada <= :fim
             GROUP BY DAYOFWEEK(p.entrada), HOUR(p.entrada)
             ORDER BY DAYOFWEEK(p.entrada), HOUR(p.entrada)
            """, nativeQuery = true)
    List<HeatmapHorarioProjection> buscarHeatmapHorario(@Param("inicio") Instant inicio, @Param("fim") Instant fim);

    @Query(value = """
            SELECT CASE WHEN HOUR(p.entrada) BETWEEN 6 AND 11 THEN '6-11h (Manhã)'
                        WHEN HOUR(p.entrada) BETWEEN 12 AND 17 THEN '12-17h (Tarde)'
                        WHEN HOUR(p.entrada) BETWEEN 18 AND 23 THEN '18-23h (Noite)'
                        ELSE '0-5h (Madrugada)'
                   END AS faixa,
                   CASE WHEN HOUR(p.entrada) BETWEEN 6 AND 11 THEN 1
                        WHEN HOUR(p.entrada) BETWEEN 12 AND 17 THEN 2
                        WHEN HOUR(p.entrada) BETWEEN 18 AND 23 THEN 3
                        ELSE 4
                   END AS ordem,
                   COUNT(*) AS checkins
              FROM presencas_sala p
             WHERE p.entrada >= :inicio
               AND p.entrada <= :fim
             GROUP BY CASE WHEN HOUR(p.entrada) BETWEEN 6 AND 11 THEN '6-11h (Manhã)'
                           WHEN HOUR(p.entrada) BETWEEN 12 AND 17 THEN '12-17h (Tarde)'
                           WHEN HOUR(p.entrada) BETWEEN 18 AND 23 THEN '18-23h (Noite)'
                           ELSE '0-5h (Madrugada)'
                      END,
                      CASE WHEN HOUR(p.entrada) BETWEEN 6 AND 11 THEN 1
                           WHEN HOUR(p.entrada) BETWEEN 12 AND 17 THEN 2
                           WHEN HOUR(p.entrada) BETWEEN 18 AND 23 THEN 3
                           ELSE 4
                      END
             ORDER BY ordem
            """, nativeQuery = true)
    List<OcupacaoFaixaHorariaProjection> buscarOcupacaoPorFaixaHoraria(@Param("inicio") Instant inicio, @Param("fim") Instant fim);

    @Query(value = """
            SELECT HOUR(p.entrada) AS hora,
                   COUNT(*) AS checkins
              FROM presencas_sala p
             WHERE p.entrada >= :inicio
               AND p.entrada <= :fim
             GROUP BY HOUR(p.entrada)
             ORDER BY checkins DESC
             LIMIT 5
            """, nativeQuery = true)
    List<HorarioPicoProjection> buscarHorariosPico(@Param("inicio") Instant inicio, @Param("fim") Instant fim);

    @Query(value = """
            SELECT HOUR(p.entrada) AS hora,
                   COUNT(*) AS checkins
              FROM presencas_sala p
             WHERE p.entrada >= :inicio
               AND p.entrada <= :fim
             GROUP BY HOUR(p.entrada)
             ORDER BY checkins ASC
             LIMIT 5
            """, nativeQuery = true)
    List<HorarioPicoProjection> buscarHorariosBaixaOcupacao(@Param("inicio") Instant inicio, @Param("fim") Instant fim);

    interface RelatorioUsoSalaProjection {
        String getBlocoNome();

        String getAndarNome();

        String getSalaNome();

        Integer getLotacaoAlunos();

        Integer getLotacaoProfessores();

        Long getTotalMinutosPresenca();
    }

    interface UsoSalaAtualProjection {
        String getBlocoNome();

        String getAndarNome();

        String getSalaNome();

        Long getProfessoresPresentes();

        Long getAlunosPresentes();

        Integer getLotacaoAlunos();

        Integer getLotacaoProfessores();
    }

    interface BlocoOcupacaoExecutivaProjection {
        String getBlocoNome();

        Long getPresentes();

        Integer getCapacidadeTotal();
    }

    interface TurnoOcupacaoProjection {
        String getTurno();

        Long getTotalCheckins();
    }

    interface SemestreUsoProjection {
        Integer getAno();

        Integer getSemestreNumero();

        Long getSalasUtilizadas();
    }

    interface BlocoEstatisticasProjection {
        String getBlocoNome();

        Long getNumeroSalas();

        Long getCapacidadeTotal();
    }

    interface BlocoHeatmapProjection {
        String getBlocoNome();

        String getTurno();

        Long getCheckins();
    }

    interface BlocoRankingProjection {
        String getBlocoNome();

        Long getUtilizacoes();
    }

    interface SalaUtilizacaoProjection {
        Long getSalaId();

        String getSalaNome();

        String getBlocoNome();

        String getAndarNome();

        Long getMinutosPresenca();

        Long getCheckins();
    }

    interface HeatmapHorarioProjection {
        Integer getDiaSemana();

        Integer getHora();

        Long getCheckins();
    }

    interface OcupacaoFaixaHorariaProjection {
        String getFaixa();

        Integer getOrdem();

        Long getCheckins();
    }

    interface HorarioPicoProjection {
        Integer getHora();

        Long getCheckins();
    }
}

