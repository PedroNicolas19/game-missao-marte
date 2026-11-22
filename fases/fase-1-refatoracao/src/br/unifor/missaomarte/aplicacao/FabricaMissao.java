package br.unifor.missaomarte.aplicacao;

import br.unifor.missaomarte.dominio.Asteroide;
import br.unifor.missaomarte.dominio.Astronauta;
import br.unifor.missaomarte.dominio.Dificuldade;
import br.unifor.missaomarte.dominio.Engenheiro;
import br.unifor.missaomarte.dominio.Inimigo;
import br.unifor.missaomarte.dominio.Mapa;
import br.unifor.missaomarte.dominio.Missao;
import br.unifor.missaomarte.dominio.Nave;
import br.unifor.missaomarte.dominio.Passageiro;
import br.unifor.missaomarte.dominio.Posicao;
import br.unifor.missaomarte.dominio.Professor;

import java.util.Random;

/**
 * Monta uma missão nova: nave, passageiros, asteroides e inimigos em posições
 * sorteadas e livres (padrão Factory / GRASP Creator).
 *
 * A ordem dos sorteios é a mesma da Fase 0. Com o mesmo Random (mesma semente),
 * o mapa gerado é idêntico.
 */
public class FabricaMissao {

    static final String ID_NAVE = "A-1";
    static final int CAPACIDADE_NAVE = 5;

    private final Random random;

    public FabricaMissao(Random random) {
        this.random = random;
    }

    public Missao criar(Dificuldade dificuldade, Mapa mapa) {
        Missao missao = new Missao(new Nave(ID_NAVE, CAPACIDADE_NAVE), mapa,
                dificuldade.getPontuacaoInicial(), random);

        while (missao.getPassageiros().size() < dificuldade.getQuantidadePassageiros()) {
            Posicao posicao = sortearPosicaoLivre(missao, mapa);
            missao.adicionarPassageiro(criarPassageiro(missao.getPassageiros().size(), posicao));
        }
        while (missao.getAsteroides().size() < dificuldade.getQuantidadeAsteroides()) {
            missao.adicionarAsteroide(new Asteroide(sortearPosicaoLivre(missao, mapa)));
        }
        while (missao.getInimigos().size() < dificuldade.getQuantidadeInimigos()) {
            missao.adicionarInimigo(new Inimigo(sortearPosicaoLivre(missao, mapa)));
        }
        return missao;
    }

    private Posicao sortearPosicaoLivre(Missao missao, Mapa mapa) {
        while (true) {
            int x = random.nextInt(mapa.getMaxX() - mapa.getMinX() + 1) + mapa.getMinX();
            int y = random.nextInt(mapa.getMaxY() - mapa.getMinY() + 1) + mapa.getMinY();
            Posicao posicao = new Posicao(x, y);
            if (!missao.posicaoOcupada(posicao)) {
                return posicao;
            }
        }
    }

    /** Os passageiros seguem sempre a mesma sequência de nomes e tipos. */
    private static Passageiro criarPassageiro(int indice, Posicao posicao) {
        switch (indice % 5) {
            case 0: return new Professor("Dr. Silva", posicao);
            case 1: return new Engenheiro("Eng. Rosa", posicao);
            case 2: return new Professor("Dr. Lima", posicao);
            case 3: return new Engenheiro("Eng. Carlos", posicao);
            default: return new Astronauta("Ast. Maria", posicao);
        }
    }
}
