package br.unifor.missaomarte.dominio;

/**
 * Direções de movimento no mapa. A tecla que escolhe cada direção é
 * responsabilidade da interface (console), não do domínio.
 */
public enum Direcao {
    CIMA(0, -1),
    BAIXO(0, 1),
    ESQUERDA(-1, 0),
    DIREITA(1, 0);

    private final int dx;
    private final int dy;

    Direcao(int dx, int dy) {
        this.dx = dx;
        this.dy = dy;
    }

    public int getDx() { return dx; }
    public int getDy() { return dy; }
}
