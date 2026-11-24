package br.unifor.missaomarte.dominio;

/**
 * Coordenada (x, y) no mapa. O eixo y cresce para baixo, como no desenho do console.
 * É imutável: mover devolve uma nova posição.
 */
public record Posicao(int x, int y) {

    public static final Posicao ORIGEM = new Posicao(0, 0);

    public Posicao mover(Direcao direcao) {
        return new Posicao(x + direcao.getDx(), y + direcao.getDy());
    }

    @Override
    public String toString() {
        return "(" + x + "," + y + ")";
    }
}
