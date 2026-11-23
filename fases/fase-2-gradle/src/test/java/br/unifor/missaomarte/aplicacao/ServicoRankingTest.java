package br.unifor.missaomarte.aplicacao;

import org.junit.jupiter.api.Test;

import static br.unifor.missaomarte.DadosDeTeste.registro;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ServicoRankingTest {

    @Test
    void registraSoQuemEntraNoTop5() {
        RepositorioRankingEmMemoria repositorio = new RepositorioRankingEmMemoria();
        ServicoRanking servico = new ServicoRanking(repositorio);

        for (int pontos : new int[] {10, 20, 30, 40, 50}) {
            assertTrue(servico.registrar(registro("P" + pontos, pontos)).entrouNoTop(), "entra " + pontos);
        }
        assertFalse(servico.registrar(registro("Fraco", 5)).entrouNoTop());

        assertEquals(5, repositorio.salvos.size());
        assertEquals(10, servico.consultar().getRegistros().get(4).pontuacao(), "Fraco não foi salvo");
    }

    @Test
    void falhaAoSalvarEInformadaSemPerderAVaga() {
        RepositorioRankingEmMemoria repositorio = new RepositorioRankingEmMemoria();
        repositorio.falharAoSalvar = true;

        ResultadoRegistro resultado = new ServicoRanking(repositorio).registrar(registro("Ana", 30));

        assertTrue(resultado.entrouNoTop());
        assertEquals("disco cheio", resultado.erroAoSalvar());
    }
}
