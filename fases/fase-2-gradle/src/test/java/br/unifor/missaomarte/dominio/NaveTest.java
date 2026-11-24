package br.unifor.missaomarte.dominio;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class NaveTest {

    @Test
    void comecaNaPlataformaComTresVidasESemPassageiros() {
        Nave nave = new Nave("T-1", 5);

        assertEquals(Posicao.ORIGEM, nave.getPosicao());
        assertEquals(Nave.VIDAS_INICIAIS, nave.getVidas());
        assertEquals(0, nave.getQuantidadeABordo());
    }

    @Test
    void embarcaAteACapacidadeERecusaQuandoCheia() {
        Nave nave = new Nave("T-1", 2);

        assertTrue(nave.embarcar(new Astronauta("A", Posicao.ORIGEM)));
        assertTrue(nave.embarcar(new Professor("B", Posicao.ORIGEM)));
        assertFalse(nave.embarcar(new Engenheiro("C", Posicao.ORIGEM)), "nave cheia");

        assertEquals(2, nave.getQuantidadeABordo());
        assertEquals(nave.getCapacidade(), nave.getQuantidadeABordo());
    }

    @Test
    void listaDePassageirosESomenteLeitura() {
        Nave nave = new Nave("T-1", 2);

        assertThrows(UnsupportedOperationException.class,
                () -> nave.getPassageiros().add(new Astronauta("A", Posicao.ORIGEM)));
    }

    @Test
    void naoSaiDoMapa() {
        Nave nave = new Nave("T-1", 5);
        Mapa mapa = new Mapa(1);

        nave.mover(Direcao.CIMA, mapa);
        nave.mover(Direcao.CIMA, mapa);
        nave.mover(Direcao.ESQUERDA, mapa);
        nave.mover(Direcao.ESQUERDA, mapa);

        assertEquals(new Posicao(-1, -1), nave.getPosicao());
    }

    @Test
    void ficaDestruidaComZeroVidasSemFicarNegativa() {
        Nave nave = new Nave("T-1", 5);

        for (int i = 0; i < Nave.VIDAS_INICIAIS + 2; i++) {
            nave.perderVida();
        }

        assertEquals(0, nave.getVidas());
        assertTrue(nave.estaDestruida());
    }
}
