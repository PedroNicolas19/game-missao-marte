package br.unifor.missaomarte.console;

import br.unifor.missaomarte.dominio.Asteroide;
import br.unifor.missaomarte.dominio.Inimigo;
import br.unifor.missaomarte.dominio.Mapa;
import br.unifor.missaomarte.dominio.Missao;
import br.unifor.missaomarte.dominio.Passageiro;
import br.unifor.missaomarte.dominio.Posicao;

/** Desenha o mapa da missão em texto. Só lê o estado do domínio, nunca o altera. */
class RenderizadorMapa {

    void desenhar(Missao missao, String piloto) {
        Mapa mapa = missao.getMapa();
        System.out.println();
        System.out.printf("Mapa da Missão (Pontos: %d) - Piloto: %s%n", missao.getPontos(), piloto);
        System.out.print("    ");
        for (int x = mapa.getMinX(); x <= mapa.getMaxX(); x++) {
            System.out.printf(" %2d", x);
        }
        System.out.println();
        System.out.print("    ");
        for (int x = mapa.getMinX(); x <= mapa.getMaxX(); x++) {
            System.out.print(" __");
        }
        System.out.println();

        for (int y = mapa.getMinY(); y <= mapa.getMaxY(); y++) {
            System.out.printf("%3d|", y);
            for (int x = mapa.getMinX(); x <= mapa.getMaxX(); x++) {
                System.out.printf(" %2c", simboloEm(missao, new Posicao(x, y)));
            }
            System.out.println();
        }

        System.out.println("Legenda: @=Nave, P=Professor, E=Engenheiro, T=Astronauta, #=Asteroide, X=Inimigo, L=Plataforma de Pouso, .=Vazio");
        System.out.println("Comandos: w/s/a/d (mover), c (embarcar), q (sair)");
        System.out.println("Passageiros na superfície marciana:");
        for (Passageiro p : missao.getPassageiros()) {
            System.out.printf(" - %s (%s) em (%d,%d)%n",
                    p.getNome(), p.getTipo(), p.getPosicao().x(), p.getPosicao().y());
        }
    }

    /** Prioridade no desenho: nave, passageiro, asteroide, inimigo, plataforma, vazio. */
    private char simboloEm(Missao missao, Posicao posicao) {
        if (missao.getNave().getPosicao().equals(posicao)) return '@';
        for (Passageiro p : missao.getPassageiros()) {
            if (p.getPosicao().equals(posicao)) return p.getSimbolo();
        }
        for (Asteroide a : missao.getAsteroides()) {
            if (a.getPosicao().equals(posicao)) return '#';
        }
        for (Inimigo i : missao.getInimigos()) {
            if (i.getPosicao().equals(posicao)) return 'X';
        }
        if (missao.getMapa().getPlataforma().equals(posicao)) return 'L';
        return '.';
    }
}
