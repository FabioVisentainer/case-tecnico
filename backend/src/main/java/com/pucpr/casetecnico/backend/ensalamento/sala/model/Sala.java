package com.pucpr.casetecnico.backend.ensalamento.sala.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import com.pucpr.casetecnico.backend.shared.model.AuditoriaEntidade;
import com.pucpr.casetecnico.backend.ensalamento.andar.model.Andar;

@Entity
@Table(
    name = "salas",
    uniqueConstraints = {
        @UniqueConstraint(
            columnNames = {"andar_id", "nome"},
            name = "uk_salas_andar_nome"
        )
    }
)
@Getter
@Setter
@SuperBuilder(toBuilder = true)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Sala extends AuditoriaEntidade {

    @Column(nullable = false, length = 120)
    private String nome;

    @Column(nullable = false)
    private Integer lotacaoAlunos;

    @Column(nullable = false)
    private Integer lotacaoProfessores;

    @ManyToOne(optional = false)
    @JoinColumn(name = "andar_id", nullable = false, foreignKey = @ForeignKey(name = "fk_sala_andar"))
    private Andar andar;

    @Column(nullable = false)
    private boolean ativo;
}