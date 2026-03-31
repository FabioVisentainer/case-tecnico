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
        usuarioService.criarSeNaoExistir("admin", EnumPapelUsuario.ADMINISTRADOR, "admin123", true);

        usuarioService.criarSeNaoExistir("professor1", EnumPapelUsuario.PROFESSOR, "prof123", true);
        usuarioService.criarSeNaoExistir("professor2", EnumPapelUsuario.PROFESSOR, "prof123", true);

        usuarioService.criarSeNaoExistir("aluno1", EnumPapelUsuario.ALUNO, "aluno123", true);
        usuarioService.criarSeNaoExistir("aluno2", EnumPapelUsuario.ALUNO, "aluno123", true);
        usuarioService.criarSeNaoExistir("aluno3", EnumPapelUsuario.ALUNO, "aluno123", true);
        usuarioService.criarSeNaoExistir("aluno4", EnumPapelUsuario.ALUNO, "aluno123", true);
        usuarioService.criarSeNaoExistir("aluno5", EnumPapelUsuario.ALUNO, "aluno123", true);
        usuarioService.criarSeNaoExistir("aluno6", EnumPapelUsuario.ALUNO, "aluno123", true);
        usuarioService.criarSeNaoExistir("aluno7", EnumPapelUsuario.ALUNO, "aluno123", true);
    }
}


