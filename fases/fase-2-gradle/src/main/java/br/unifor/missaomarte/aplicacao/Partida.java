package br.unifor.missaomarte.aplicacao;

import br.unifor.missaomarte.dominio.Dificuldade;
import br.unifor.missaomarte.dominio.Direcao;
import br.unifor.missaomarte.dominio.Evento;
import br.unifor.missaomarte.dominio.Missao;
import br.unifor.missaomarte.dominio.Ranking;
import br.unifor.missaomarte.dominio.RegistroRanking;
import br.unifor.missaomarte.dominio.StatusMissao;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Coordena uma partida (GRASP Controller): repassa as ações do jogador para a
 * missão, mede o tempo e, quando a missão é cumprida, registra o resultado no ranking.
 */
public class Partida {

    private static final DateTimeFormatter FORMATO_DATA_HORA = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final String piloto;
    private final Dificuldade dificuldade;
    private final Missao missao;
    private final ServicoRanking servicoRanking;
    private final Clock relogio;
    private final Instant inicio;

    public Partida(String piloto, Dificuldade dificuldade, Missao missao,
                   ServicoRanking servicoRanking, Clock relogio) {
        this.piloto = piloto;
        this.dificuldade = dificuldade;
        this.missao = missao;
        this.servicoRanking = servicoRanking;
        this.relogio = relogio;
        this.inicio = relogio.instant();
    }

    public List<Evento> mover(Direcao direcao) { return missao.mover(direcao); }
    public List<Evento> embarcar() { return missao.embarcar(); }
    public void abortar() { missao.abortar(); }

    /**
     * Fecha uma missão cumprida: calcula as estatísticas, guarda o recorde
     * anterior e tenta colocar o piloto no ranking.
     */
    public ResumoPartida concluir() {
        if (missao.getStatus() != StatusMissao.CUMPRIDA) {
            throw new IllegalStateException("Só é possível concluir uma missão cumprida.");
        }
        long tempoSegundos = Duration.between(inicio, relogio.instant()).toSeconds();
        int resgatados = missao.getNave().getQuantidadeABordo();

        Ranking rankingAnterior = servicoRanking.consultar();
        RegistroRanking registro = new RegistroRanking(piloto, missao.getPontos(), dificuldade,
                resgatados, agoraFormatado(), tempoSegundos);
        ResultadoRegistro resultado = servicoRanking.registrar(registro);

        return new ResumoPartida(missao.getPontos(), missao.getMovimentos(), tempoSegundos,
                resgatados, rankingAnterior.getRecorde().orElse(null), resultado);
    }

    private String agoraFormatado() {
        return LocalDateTime.now(relogio).format(FORMATO_DATA_HORA);
    }

    public String getPiloto() { return piloto; }
    public Dificuldade getDificuldade() { return dificuldade; }
    public Missao getMissao() { return missao; }
}
