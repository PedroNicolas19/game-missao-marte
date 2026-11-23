package br.unifor.missaomarte.aplicacao;

import br.unifor.missaomarte.dominio.Asteroide;
import br.unifor.missaomarte.dominio.Dificuldade;
import br.unifor.missaomarte.dominio.Direcao;
import br.unifor.missaomarte.dominio.Missao;
import br.unifor.missaomarte.dominio.Posicao;
import br.unifor.missaomarte.dominio.Professor;
import br.unifor.missaomarte.dominio.StatusMissao;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

import static br.unifor.missaomarte.DadosDeTeste.missaoVazia;
import static br.unifor.missaomarte.DadosDeTeste.registro;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PartidaTest {

    private final Clock relogio = Clock.fixed(Instant.parse("2026-11-22T13:00:00Z"), ZoneOffset.UTC);

    @Test
    void partidaCumpridaGeraResumoEEntraNoRanking() {
        RepositorioRankingEmMemoria repositorio = new RepositorioRankingEmMemoria();
        repositorio.salvos.add(registro("Recordista", 99));
        Missao missao = missaoVazia(3, 20);
        missao.adicionarPassageiro(new Professor("Dr. Silva", new Posicao(1, 0)));
        Partida partida = new Partida("Ana", Dificuldade.MEDIO, missao, new ServicoRanking(repositorio), relogio);

        partida.mover(Direcao.DIREITA);
        partida.embarcar();
        partida.mover(Direcao.ESQUERDA);
        ResumoPartida resumo = partida.concluir();

        assertEquals(28, resumo.pontuacao());
        assertEquals(2, resumo.movimentos());
        assertEquals(1, resumo.passageirosResgatados());
        assertEquals("Recordista", resumo.recordeAnterior().nome());
        assertTrue(resumo.resultadoRanking().entrouNoTop());
        assertEquals("2026-11-22 13:00:00", repositorio.salvos.get(1).dataHora(), "data formatada");
    }

    @Test
    void colisaoDuranteAPartidaTiraVida() {
        Missao missao = missaoVazia(3, 20);
        missao.adicionarPassageiro(new Professor("Dr. Silva", new Posicao(3, 3)));
        missao.adicionarAsteroide(new Asteroide(new Posicao(0, 1)));
        Partida partida = new Partida("Ana", Dificuldade.MEDIO, missao,
                new ServicoRanking(new RepositorioRankingEmMemoria()), relogio);

        partida.mover(Direcao.BAIXO);

        assertEquals(2, missao.getNave().getVidas());
        assertEquals(StatusMissao.EM_ANDAMENTO, missao.getStatus());
    }

    @Test
    void naoConcluiMissaoQueNaoFoiCumprida() {
        RepositorioRankingEmMemoria repositorio = new RepositorioRankingEmMemoria();
        Missao missao = missaoVazia(3, 20);
        missao.adicionarPassageiro(new Professor("Dr. Silva", new Posicao(3, 3)));
        Partida partida = new Partida("Ana", Dificuldade.MEDIO, missao, new ServicoRanking(repositorio), relogio);

        partida.abortar();

        assertThrows(IllegalStateException.class, partida::concluir);
        assertTrue(repositorio.salvos.isEmpty(), "abortar não grava no ranking");
    }
}
