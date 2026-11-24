package br.unifor.missaomarte.dominio;

/** Uma linha do ranking: resultado de uma missão cumprida. */
public record RegistroRanking(
        String nome,
        int pontuacao,
        Dificuldade dificuldade,
        int passageirosResgatados,
        String dataHora,
        long tempoSegundos) {
}
