package br.unifor.missaomarte.dominio;

import java.util.Random;

/** Obstáculo que anda uma casa por turno, numa direção sorteada, sem sair do mapa. */
public class Inimigo {

    /** Ordem do sorteio igual à da Fase 0, para a mesma semente gerar o mesmo jogo. */
    private static final Direcao[] DIRECOES_SORTEADAS = {
            Direcao.DIREITA, Direcao.ESQUERDA, Direcao.BAIXO, Direcao.CIMA
    };

    private Posicao posicao;

    public Inimigo(Posicao posicao) {
        this.posicao = posicao;
    }

    public Posicao getPosicao() { return posicao; }

    public boolean colideCom(Posicao outra) {
        return posicao.equals(outra);
    }

    public void mover(Random random, Mapa mapa) {
        Direcao direcao = DIRECOES_SORTEADAS[random.nextInt(DIRECOES_SORTEADAS.length)];
        Posicao destino = posicao.mover(direcao);
        if (mapa.contem(destino)) {
            posicao = destino;
        }
    }
}
