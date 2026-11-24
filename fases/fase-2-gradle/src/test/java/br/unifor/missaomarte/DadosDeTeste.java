package br.unifor.missaomarte;

import br.unifor.missaomarte.dominio.Dificuldade;
import br.unifor.missaomarte.dominio.Evento;
import br.unifor.missaomarte.dominio.Mapa;
import br.unifor.missaomarte.dominio.Missao;
import br.unifor.missaomarte.dominio.Nave;
import br.unifor.missaomarte.dominio.RegistroRanking;
import br.unifor.missaomarte.dominio.TipoEvento;

import java.util.List;
import java.util.Random;

/** Objetos prontos usados por vários testes. */
public final class DadosDeTeste {

    private DadosDeTeste() {
    }

    /** Missão sem passageiros, asteroides nem inimigos, com nave de capacidade 5. */
    public static Missao missaoVazia(int tamanhoMapa, int pontos) {
        return new Missao(new Nave("T-1", 5), new Mapa(tamanhoMapa), pontos, new Random(1));
    }

    public static RegistroRanking registro(String nome, int pontos) {
        return new RegistroRanking(nome, pontos, Dificuldade.MEDIO, 5, "2026-11-22 12:00:00", 60);
    }

    public static boolean contem(List<Evento> eventos, TipoEvento tipo) {
        return eventos.stream().anyMatch(e -> e.tipo() == tipo);
    }
}
