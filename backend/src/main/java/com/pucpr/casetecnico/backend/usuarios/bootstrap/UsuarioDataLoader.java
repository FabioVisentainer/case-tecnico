package com.pucpr.casetecnico.backend.usuarios.bootstrap;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;
import com.pucpr.casetecnico.backend.usuarios.model.EnumPapelUsuario;
import com.pucpr.casetecnico.backend.usuarios.service.UsuarioService;

@Component
@RequiredArgsConstructor
public class UsuarioDataLoader implements CommandLineRunner {

    private final UsuarioService usuarioService;

    @Override
    public void run(String... args) {
        usuarioService.criarSeNaoExistir("Fabio Costa", "fabio.costa", gerarCpfValido(1),
                EnumPapelUsuario.ADMINISTRADOR, "admin123", true);

        usuarioService.criarSeNaoExistir("Mariana Alves", "mariana.alves", gerarCpfValido(2),
                EnumPapelUsuario.PROFESSOR, "prof123", true);
        usuarioService.criarSeNaoExistir("Ricardo Nogueira", "ricardo.nogueira", gerarCpfValido(3),
                EnumPapelUsuario.PROFESSOR, "prof123", true);

        usuarioService.criarSeNaoExistir("Ana Beatriz Lima", "ana.beatriz", gerarCpfValido(4), EnumPapelUsuario.ALUNO, "aluno123", true);
        usuarioService.criarSeNaoExistir("Bruno Henrique Souza", "bruno.henrique", gerarCpfValido(5), EnumPapelUsuario.ALUNO, "aluno123", true);
        usuarioService.criarSeNaoExistir("Carolina Martins", "carolina.martins", gerarCpfValido(6), EnumPapelUsuario.ALUNO, "aluno123", true);
        usuarioService.criarSeNaoExistir("Daniel Rocha", "daniel.rocha", gerarCpfValido(7), EnumPapelUsuario.ALUNO, "aluno123", true);
        usuarioService.criarSeNaoExistir("Eduarda Fernandes", "eduarda.fernandes", gerarCpfValido(8), EnumPapelUsuario.ALUNO, "aluno123", true);
        usuarioService.criarSeNaoExistir("Felipe Araujo", "felipe.araujo", gerarCpfValido(9), EnumPapelUsuario.ALUNO, "aluno123", true);
        usuarioService.criarSeNaoExistir("Gabriela Teixeira", "gabriela.teixeira", gerarCpfValido(10), EnumPapelUsuario.ALUNO, "aluno123", true);
    }

    private String gerarCpfValido(int baseNumerica) {
        String base = String.format("%09d", baseNumerica);
        int primeiroDigito = calcularDigito(base, 10);
        int segundoDigito = calcularDigito(base + primeiroDigito, 11);
        return base + primeiroDigito + segundoDigito;
    }

    private int calcularDigito(String valor, int pesoInicial) {
        int soma = 0;
        for (int i = 0; i < valor.length(); i++) {
            int numero = Character.getNumericValue(valor.charAt(i));
            soma += numero * (pesoInicial - i);
        }
        int resto = (soma * 10) % 11;
        return resto == 10 ? 0 : resto;
    }
}


