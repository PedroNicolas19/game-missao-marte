package br.unifor.missaomarte.dominio;

/**
 * Passageiro a ser resgatado. Cada tipo define pontuação, nome do tipo e símbolo
 * no mapa (polimorfismo). Assim, um tipo novo não exige mudar quem desenha o mapa (OCP).
 */
public abstract class Passageiro {

    private final String nome;
    private final Posicao posicao;

    protected Passageiro(String nome, Posicao posicao) {
        this.nome = nome;
        this.posicao = posicao;
    }

    public String getNome() { return nome; }
    public Posicao getPosicao() { return posicao; }

    public abstract String getTipo();
    public abstract int getPontuacao();
    public abstract char getSimbolo();
}
