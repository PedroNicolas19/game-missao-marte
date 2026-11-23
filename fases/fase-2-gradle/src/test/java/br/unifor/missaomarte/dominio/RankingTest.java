package br.unifor.missaomarte.dominio;

import org.junit.jupiter.api.Test;

import static br.unifor.missaomarte.DadosDeTeste.registro;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RankingTest {

    @Test
    void ordenaPorPontuacaoEGuardaSoOsCincoMelhores() {
        Ranking ranking = Ranking.vazio();
        for (int pontos : new int[] {10, 50, 30, 20, 40, 60, 5}) {
            ranking = ranking.comNovoRegistro(registro("P" + pontos, pontos));
        }

        assertEquals(5, ranking.getRegistros().size());
        assertEquals(60, ranking.getRecorde().orElseThrow().pontuacao());
        assertEquals(20, ranking.getRegistros().get(4).pontuacao());
    }

    @Test
    void regraDeEntradaNoTop5() {
        Ranking ranking = Ranking.vazio();
        assertFalse(ranking.entraNoTop(0), "pontuação zero nunca entra");
        assertTrue(ranking.entraNoTop(1), "há vaga");

        for (int pontos : new int[] {10, 20, 30, 40, 50}) {
            ranking = ranking.comNovoRegistro(registro("P" + pontos, pontos));
        }

        assertFalse(ranking.entraNoTop(10), "empate com o último não entra");
        assertTrue(ranking.entraNoTop(11), "supera o último");
    }
}
