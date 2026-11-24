package br.unifor.missaomarte.dominio;

import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertTrue;

class InimigoTest {

    @Test
    void andaSemSairDoMapa() {
        Mapa mapa = new Mapa(1);
        Inimigo inimigo = new Inimigo(new Posicao(1, 1));
        Random random = new Random(7);

        for (int passo = 0; passo < 500; passo++) {
            inimigo.mover(random, mapa);
            assertTrue(mapa.contem(inimigo.getPosicao()), "fora do mapa no passo " + passo);
        }
    }
}
