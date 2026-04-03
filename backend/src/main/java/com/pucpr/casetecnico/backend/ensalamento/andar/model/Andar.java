package com.pucpr.casetecnico.backend.ensalamento.andar.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import com.pucpr.casetecnico.backend.shared.model.AuditoriaEntidade;
import com.pucpr.casetecnico.backend.ensalamento.bloco.model.Bloco;
import com.pucpr.casetecnico.backend.ensalamento.sala.model.Sala;
import java.util.List;

@Entity
@Table(
    name = "andares",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_andares_bloco_nome", columnNames = {"bloco_id", "nome"})
    }
)
@Getter
@Setter
@SuperBuilder(toBuilder = true)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Andar extends AuditoriaEntidade {

    @Column(nullable = false, length = 120)
    private String nome;

    @ManyToOne(optional = false)
    @JoinColumn(name = "bloco_id", nullable = false, foreignKey = @ForeignKey(name = "fk_andar_bloco"))
    private Bloco bloco;

    @Column(nullable = false)
    private boolean ativo;

    @OneToMany(mappedBy = "andar", orphanRemoval = true)
    private List<Sala> salas;
}