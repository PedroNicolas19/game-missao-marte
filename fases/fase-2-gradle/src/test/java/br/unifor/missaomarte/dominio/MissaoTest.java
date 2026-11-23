package br.unifor.missaomarte.dominio;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Random;

import static br.unifor.missaomarte.DadosDeTeste.contem;
import static br.unifor.missaomarte.DadosDeTeste.missaoVazia;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MissaoTest {

    @Test
    void cadaMovimentoCustaUmPontoEContaComoMovimento() {
        Missao missao = missaoVazia(3, 20);

        missao.mover(Direcao.DIREITA);
        missao.mover(Direcao.BAIXO);

        assertEquals(18, missao.getPontos());
        assertEquals(2, missao.getMovimentos());
    }

    @Test
    void embarcarSomaAPontuacaoDoTipoETiraOPassageiroDaSuperficie() {
        Missao missao = missaoVazia(3, 20);
        missao.adicionarPassageiro(new Astronauta("Ast. Maria", new Posicao(1, 0)));
        missao.adicionarPassageiro(new Professor("Dr. Silva", new Posicao(3, 3)));

        missao.mover(Direcao.DIREITA);
        List<Evento> eventos = missao.embarcar();

        assertEquals(TipoEvento.PASSAGEIRO_EMBARCADO, eventos.get(0).tipo());
        assertEquals(20, eventos.get(0).valor(), "bônus do astronauta");
        assertEquals(19 + 20, missao.getPontos());
        assertEquals(1, missao.getNave().getQuantidadeABordo());
        assertEquals(1, missao.getPassageiros().size(), "restantes na superfície");
    }

    @Test
    void embarcarSemPassageiroNaoMudaPontosNemMovimentos() {
        Missao missao = missaoVazia(3, 20);
        missao.adicionarPassageiro(new Professor("Dr. Silva", new Posicao(3, 3)));

        List<Evento> eventos = missao.embarcar();

        assertEquals(TipoEvento.NENHUM_PASSAGEIRO, eventos.get(0).tipo());
        assertEquals(20, missao.getPontos());
        assertEquals(0, missao.getMovimentos());
    }

    @Test
    void naveCheiaNaoEmbarca() {
        Missao missao = new Missao(new Nave("T", 1), new Mapa(3), 20, new Random(1));
        missao.adicionarPassageiro(new Professor("A", new Posicao(1, 0)));
        missao.adicionarPassageiro(new Engenheiro("B", new Posicao(2, 0)));

        missao.mover(Direcao.DIREITA);
        missao.embarcar();
        missao.mover(Direcao.DIREITA);
        List<Evento> eventos = missao.embarcar();

        assertEquals(TipoEvento.NAVE_CHEIA, eventos.get(0).tipo());
        assertEquals(1, missao.getPassageiros().size(), "B continua na superfície");
    }

    @Test
    void cadaColisaoTiraUmaVidaEATerceiraDestroiANave() {
        Missao missao = missaoVazia(3, 50);
        missao.adicionarPassageiro(new Professor("Dr. Silva", new Posicao(3, 3)));
        missao.adicionarAsteroide(new Asteroide(new Posicao(1, 0)));

        List<Evento> primeira = missao.mover(Direcao.DIREITA);
        assertEquals(Evento.colisao(2), primeira.get(0));

        missao.embarcar(); // continua em cima do asteroide: segunda colisão
        List<Evento> terceira = missao.embarcar();

        assertTrue(contem(terceira, TipoEvento.NAVE_DESTRUIDA));
        assertEquals(0, missao.getNave().getVidas());
        assertEquals(StatusMissao.FRACASSADA, missao.getStatus());
    }

    @Test
    void pontuacaoZeradaEncerraAMissao() {
        Missao missao = missaoVazia(3, 1);
        missao.adicionarPassageiro(new Professor("Dr. Silva", new Posicao(3, 3)));

        List<Evento> eventos = missao.mover(Direcao.DIREITA);

        assertTrue(contem(eventos, TipoEvento.PONTUACAO_ZERADA));
        assertEquals(StatusMissao.FRACASSADA, missao.getStatus());
    }

    @Test
    void vitoriaSoAoVoltarAPlataformaComTodosABordo() {
        Missao missao = missaoVazia(3, 20);
        missao.adicionarPassageiro(new Engenheiro("Eng. Rosa", new Posicao(1, 0)));

        missao.mover(Direcao.DIREITA);
        List<Evento> aposEmbarque = missao.embarcar();
        assertTrue(contem(aposEmbarque, TipoEvento.RETORNE_A_PLATAFORMA));
        assertEquals(StatusMissao.EM_ANDAMENTO, missao.getStatus());

        List<Evento> aoVoltar = missao.mover(Direcao.ESQUERDA);
        assertTrue(contem(aoVoltar, TipoEvento.MISSAO_CUMPRIDA));
        assertEquals(StatusMissao.CUMPRIDA, missao.getStatus());
        assertEquals(20 - 2 + 15, missao.getPontos());
    }

    @Test
    void naoAceitaAcoesDepoisDoFim() {
        Missao missao = missaoVazia(3, 20);
        missao.abortar();

        assertEquals(StatusMissao.ABORTADA, missao.getStatus());
        assertThrows(IllegalStateException.class, () -> missao.mover(Direcao.CIMA));
        assertThrows(IllegalStateException.class, missao::embarcar);
    }
}
