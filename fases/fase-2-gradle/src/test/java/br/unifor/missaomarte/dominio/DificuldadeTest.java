package br.unifor.missaomarte.dominio;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DificuldadeTest {

    @Test
    void cadaDificuldadeDefinePontuacaoInicialEQuantidades() {
        assertEquals(30, Dificuldade.FACIL.getPontuacaoInicial());
        assertEquals(20, Dificuldade.MEDIO.getPontuacaoInicial());
        assertEquals(45, Dificuldade.DIFICIL.getPontuacaoInicial());
        assertEquals(4, Dificuldade.FACIL.getQuantidadePassageiros());
        assertEquals(1, Dificuldade.FACIL.getQuantidadeInimigos());
        assertEquals(3, Dificuldade.DIFICIL.getQuantidadeAsteroides());
    }

    @Test
    void converteTextoComOuSemAcentoEUsaMedioQuandoDesconhecido() {
        assertEquals(Dificuldade.FACIL, Dificuldade.deString(" Fácil "));
        assertEquals(Dificuldade.DIFICIL, Dificuldade.deString("dificil"));
        assertEquals(Dificuldade.MEDIO, Dificuldade.deString("xyz"));
        assertEquals(Dificuldade.MEDIO, Dificuldade.deString(null));
    }
}
