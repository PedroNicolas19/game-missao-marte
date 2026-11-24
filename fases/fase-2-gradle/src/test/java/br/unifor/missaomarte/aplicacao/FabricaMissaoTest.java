package br.unifor.missaomarte.aplicacao;

import br.unifor.missaomarte.dominio.Dificuldade;
import br.unifor.missaomarte.dominio.Mapa;
import br.unifor.missaomarte.dominio.Missao;
import br.unifor.missaomarte.dominio.Posicao;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FabricaMissaoTest {

    @ParameterizedTest
    @EnumSource(Dificuldade.class)
    void respeitaAsQuantidadesDaDificuldade(Dificuldade dificuldade) {
        Missao missao = new FabricaMissao(new Random(3)).criar(dificuldade, new Mapa(5));

        assertEquals(dificuldade.getQuantidadePassageiros(), missao.getPassageiros().size());
        assertEquals(dificuldade.getQuantidadeAsteroides(), missao.getAsteroides().size());
        assertEquals(dificuldade.getQuantidadeInimigos(), missao.getInimigos().size());
        assertEquals(dificuldade.getPontuacaoInicial(), missao.getPontos());
    }

    @Test
    void naoColocaDoisObjetosNaMesmaCelulaNemNaPlataforma() {
        for (long semente = 0; semente < 200; semente++) {
            Mapa mapa = new Mapa(2);
            Missao missao = new FabricaMissao(new Random(semente)).criar(Dificuldade.DIFICIL, mapa);

            Set<Posicao> ocupadas = new HashSet<>();
            ocupadas.add(missao.getNave().getPosicao()); // a nave está na plataforma
            missao.getPassageiros().forEach(p -> ocupadas.add(p.getPosicao()));
            missao.getAsteroides().forEach(a -> ocupadas.add(a.getPosicao()));
            missao.getInimigos().forEach(i -> ocupadas.add(i.getPosicao()));

            assertEquals(1 + 5 + 3 + 3, ocupadas.size(), "posições repetidas com a semente " + semente);
            ocupadas.forEach(p -> assertTrue(mapa.contem(p), "fora do mapa: " + p));
        }
    }

    @Test
    void mesmaSementeGeraOMesmoMapa() {
        Missao a = new FabricaMissao(new Random(42)).criar(Dificuldade.MEDIO, new Mapa(5));
        Missao b = new FabricaMissao(new Random(42)).criar(Dificuldade.MEDIO, new Mapa(5));

        for (int i = 0; i < a.getPassageiros().size(); i++) {
            assertEquals(a.getPassageiros().get(i).getPosicao(), b.getPassageiros().get(i).getPosicao());
        }
        for (int i = 0; i < a.getInimigos().size(); i++) {
            assertEquals(a.getInimigos().get(i).getPosicao(), b.getInimigos().get(i).getPosicao());
        }
    }

    @Test
    void recusaMapaPequenoDemaisParaADificuldade() {
        assertFalse(FabricaMissao.cabeNoMapa(Dificuldade.DIFICIL, new Mapa(1)), "difícil em 3x3");
        assertTrue(FabricaMissao.cabeNoMapa(Dificuldade.FACIL, new Mapa(1)), "fácil em 3x3");
        assertThrows(IllegalArgumentException.class,
                () -> new FabricaMissao(new Random(1)).criar(Dificuldade.MEDIO, new Mapa(1)));
    }
}
