package br.unifor.missaomarte.dominio;

/**
 * Área quadrada do jogo, de -tamanho a +tamanho nos dois eixos.
 * A plataforma de pouso fica sempre na origem (0,0).
 */
public final class Mapa {

    private final int tamanho;

    public Mapa(int tamanho) {
        if (tamanho <= 0) {
            throw new IllegalArgumentException("O tamanho do mapa deve ser positivo: " + tamanho);
        }
        this.tamanho = tamanho;
    }

    public int getTamanho() { return tamanho; }
    public int getMinX() { return -tamanho; }
    public int getMaxX() { return tamanho; }
    public int getMinY() { return -tamanho; }
    public int getMaxY() { return tamanho; }

    public Posicao getPlataforma() {
        return Posicao.ORIGEM;
    }

    public boolean contem(Posicao posicao) {
        return posicao.x() >= getMinX() && posicao.x() <= getMaxX()
                && posicao.y() >= getMinY() && posicao.y() <= getMaxY();
    }

    /** Quantidade de casas do mapa: (2 * tamanho + 1)². */
    public int getTotalDeCasas() {
        int lado = 2 * tamanho + 1;
        return lado * lado;
    }
}
