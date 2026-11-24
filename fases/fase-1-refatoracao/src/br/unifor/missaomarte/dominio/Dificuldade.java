package br.unifor.missaomarte.dominio;

/**
 * Níveis de dificuldade. Cada nível conhece a própria configuração
 * (GRASP Information Expert), que antes ficava espalhada em switch/if no Main.
 */
public enum Dificuldade {
    FACIL("Fácil", 30, 4, 1, 1),
    MEDIO("Médio", 20, 5, 2, 2),
    DIFICIL("Difícil", 45, 5, 3, 3);

    private final String rotulo;
    private final int pontuacaoInicial;
    private final int quantidadePassageiros;
    private final int quantidadeAsteroides;
    private final int quantidadeInimigos;

    Dificuldade(String rotulo, int pontuacaoInicial, int quantidadePassageiros,
                int quantidadeAsteroides, int quantidadeInimigos) {
        this.rotulo = rotulo;
        this.pontuacaoInicial = pontuacaoInicial;
        this.quantidadePassageiros = quantidadePassageiros;
        this.quantidadeAsteroides = quantidadeAsteroides;
        this.quantidadeInimigos = quantidadeInimigos;
    }

    public int getPontuacaoInicial() { return pontuacaoInicial; }
    public int getQuantidadePassageiros() { return quantidadePassageiros; }
    public int getQuantidadeAsteroides() { return quantidadeAsteroides; }
    public int getQuantidadeInimigos() { return quantidadeInimigos; }

    /** Converte o texto digitado; qualquer valor desconhecido vira MEDIO. */
    public static Dificuldade deString(String texto) {
        if (texto == null) return MEDIO;
        switch (texto.trim().toLowerCase()) {
            case "facil":
            case "fácil":
                return FACIL;
            case "dificil":
            case "difícil":
                return DIFICIL;
            default:
                return MEDIO;
        }
    }

    @Override
    public String toString() {
        return rotulo;
    }
}
