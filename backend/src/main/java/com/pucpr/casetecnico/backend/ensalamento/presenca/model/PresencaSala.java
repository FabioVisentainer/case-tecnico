package com.pucpr.casetecnico.backend.ensalamento.presenca.model;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import com.pucpr.casetecnico.backend.ensalamento.sala.model.Sala;
import com.pucpr.casetecnico.backend.shared.model.AuditoriaEntidade;
import com.pucpr.casetecnico.backend.usuarios.model.Usuario;

@Entity
@Table(
        name = "presencas_sala",
        indexes = {
                @Index(name = "idx_presenca_usuario_saida", columnList = "usuario_id,saida"),
                @Index(name = "idx_presenca_sala_entrada", columnList = "sala_id,entrada"),
                @Index(name = "idx_presenca_entrada", columnList = "entrada")
        })
@Getter
@Setter
@SuperBuilder(toBuilder = true)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PresencaSala extends AuditoriaEntidade {

    @ManyToOne(optional = false)
    @JoinColumn(name = "usuario_id", nullable = false, foreignKey = @ForeignKey(name = "fk_presenca_usuario"))
    private Usuario usuario;

    @ManyToOne(optional = false)
    @JoinColumn(name = "sala_id", nullable = false, foreignKey = @ForeignKey(name = "fk_presenca_sala"))
    private Sala sala;

    @Column(nullable = false)
    private Instant entrada;

    @Column
    private Instant saida;
}