package com.pucpr.casetecnico.backend.ensalamento.bloco.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
import com.pucpr.casetecnico.backend.ensalamento.andar.model.Andar;
import java.util.List;

@Entity
@Table(
    name = "blocos",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_blocos_nome", columnNames = {"nome"})
    }
)
@Getter
@Setter
@SuperBuilder(toBuilder = true)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Bloco extends AuditoriaEntidade {

    @Column(nullable = false, length = 120)
    private String nome;

    @Column(nullable = false)
    private boolean ativo;

    @OneToMany(mappedBy = "bloco", orphanRemoval = true)
    private List<Andar> andares;
}