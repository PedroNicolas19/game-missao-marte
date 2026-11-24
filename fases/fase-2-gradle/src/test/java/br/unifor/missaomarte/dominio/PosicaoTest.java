package br.unifor.missaomarte.dominio;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;

class PosicaoTest {

    @Test
    void moverDevolveNovaInstanciaSemAlterarAOriginal() {
        Posicao original = new Posicao(2, 3);

        Posicao movida = original.mover(Direcao.DIREITA);

        assertNotSame(original, movida);
        assertEquals(new Posicao(3, 3), movida);
        assertEquals(new Posicao(2, 3), original);
    }

    @Test
    void cadaDirecaoAndaUmaCasaComYCrescendoParaBaixo() {
        Posicao origem = Posicao.ORIGEM;

        assertEquals(new Posicao(0, -1), origem.mover(Direcao.CIMA));
        assertEquals(new Posicao(0, 1), origem.mover(Direcao.BAIXO));
        assertEquals(new Posicao(-1, 0), origem.mover(Direcao.ESQUERDA));
        assertEquals(new Posicao(1, 0), origem.mover(Direcao.DIREITA));
    }

    @Test
    void equalsEHashCodeComparamAsCoordenadas() {
        Posicao a = new Posicao(1, -2);
        Posicao b = new Posicao(1, -2);

        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
        assertNotEquals(a, new Posicao(-2, 1));
    }
}
