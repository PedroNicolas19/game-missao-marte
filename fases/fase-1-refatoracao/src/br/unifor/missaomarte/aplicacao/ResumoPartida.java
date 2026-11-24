package br.unifor.missaomarte.aplicacao;

import br.unifor.missaomarte.dominio.RegistroRanking;

/**
 * Estatísticas de uma missão cumprida.
 *
 * @param recordeAnterior primeiro colocado do ranking antes desta partida (null se o ranking estava vazio)
 */
public record ResumoPartida(
        int pontuacao,
        int movimentos,
        long tempoSegundos,
        int passageirosResgatados,
        RegistroRanking recordeAnterior,
        ResultadoRegistro resultadoRanking) {
}
