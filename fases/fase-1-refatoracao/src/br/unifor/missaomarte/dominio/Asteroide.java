package br.unifor.missaomarte.dominio;

/** Obstáculo fixo: a nave perde uma vida se estiver na mesma posição. */
public class Asteroide {

    private final Posicao posicao;

    public Asteroide(Posicao posicao) {
        this.posicao = posicao;
    }

    public Posicao getPosicao() { return posicao; }

    public boolean colideCom(Posicao outra) {
        return posicao.equals(outra);
    }
}
