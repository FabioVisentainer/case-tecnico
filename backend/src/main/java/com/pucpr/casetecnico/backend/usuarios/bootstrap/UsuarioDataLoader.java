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
        usuarioService.criarSeNaoExistir("Fabio Costa", "admin", gerarCpfValido(1),
                EnumPapelUsuario.ADMINISTRADOR, "admin123", true);

        usuarioService.criarSeNaoExistir("Mariana Alves", "professor1", gerarCpfValido(2),
                EnumPapelUsuario.PROFESSOR, "prof123", true);
        usuarioService.criarSeNaoExistir("Ricardo Nogueira", "professor2", gerarCpfValido(3),
                EnumPapelUsuario.PROFESSOR, "prof123", true);

        usuarioService.criarSeNaoExistir("Ana Beatriz Lima", "aluno1", gerarCpfValido(4), EnumPapelUsuario.ALUNO, "aluno123", true);
        usuarioService.criarSeNaoExistir("Bruno Henrique Souza", "aluno2", gerarCpfValido(5), EnumPapelUsuario.ALUNO, "aluno123", true);
        usuarioService.criarSeNaoExistir("Carolina Martins", "aluno3", gerarCpfValido(6), EnumPapelUsuario.ALUNO, "aluno123", true);
        usuarioService.criarSeNaoExistir("Daniel Rocha", "aluno4", gerarCpfValido(7), EnumPapelUsuario.ALUNO, "aluno123", true);
        usuarioService.criarSeNaoExistir("Eduarda Fernandes", "aluno5", gerarCpfValido(8), EnumPapelUsuario.ALUNO, "aluno123", true);
        usuarioService.criarSeNaoExistir("Felipe Araujo", "aluno6", gerarCpfValido(9), EnumPapelUsuario.ALUNO, "aluno123", true);
        usuarioService.criarSeNaoExistir("Gabriela Teixeira", "aluno7", gerarCpfValido(10), EnumPapelUsuario.ALUNO, "aluno123", true);
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


