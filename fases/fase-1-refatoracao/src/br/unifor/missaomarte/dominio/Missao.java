package br.unifor.missaomarte.dominio;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Random;

/**
 * Estado e regras de uma missão: pontos, movimentos, colisões, vitória e derrota.
 *
 * Cada ação do jogador (mover ou embarcar) é um turno. O turno devolve a lista de
 * eventos ocorridos, na ordem, e a interface decide como mostrá-los. Nada aqui lê
 * o teclado ou escreve na tela.
 */
public class Missao {

    private final Nave nave;
    private final Mapa mapa;
    private final Random random;
    private final List<Passageiro> passageiros = new ArrayList<>();
    private final List<Asteroide> asteroides = new ArrayList<>();
    private final List<Inimigo> inimigos = new ArrayList<>();
    private int pontos;
    private int movimentos;
    private StatusMissao status = StatusMissao.EM_ANDAMENTO;

    public Missao(Nave nave, Mapa mapa, int pontosIniciais, Random random) {
        this.nave = nave;
        this.mapa = mapa;
        this.pontos = pontosIniciais;
        this.random = random;
    }

    // ---------------------------------------------------------------- montagem

    public void adicionarPassageiro(Passageiro passageiro) { passageiros.add(passageiro); }
    public void adicionarAsteroide(Asteroide asteroide) { asteroides.add(asteroide); }
    public void adicionarInimigo(Inimigo inimigo) { inimigos.add(inimigo); }

    /** Verdadeiro se a nave ou qualquer elemento do mapa estiver na posição. */
    public boolean posicaoOcupada(Posicao posicao) {
        if (nave.getPosicao().equals(posicao)) return true;
        for (Passageiro p : passageiros) {
            if (p.getPosicao().equals(posicao)) return true;
        }
        for (Asteroide a : asteroides) {
            if (a.getPosicao().equals(posicao)) return true;
        }
        for (Inimigo i : inimigos) {
            if (i.getPosicao().equals(posicao)) return true;
        }
        return false;
    }

    // ------------------------------------------------------------------ turnos

    /** Move a nave uma casa. Cada movimento custa 1 ponto. */
    public List<Evento> mover(Direcao direcao) {
        exigirEmAndamento();
        nave.mover(direcao, mapa);
        pontos--;
        movimentos++;
        return concluirTurno(new ArrayList<>());
    }

    /** Tenta embarcar o passageiro que está na posição da nave. Não custa pontos. */
    public List<Evento> embarcar() {
        exigirEmAndamento();
        List<Evento> eventos = new ArrayList<>();
        Optional<Passageiro> naPosicao = passageiroNaPosicaoDaNave();
        if (naPosicao.isEmpty()) {
            eventos.add(Evento.de(TipoEvento.NENHUM_PASSAGEIRO));
        } else if (nave.embarcar(naPosicao.get())) {
            Passageiro passageiro = naPosicao.get();
            passageiros.remove(passageiro);
            pontos += passageiro.getPontuacao();
            eventos.add(Evento.embarque(passageiro));
        } else {
            eventos.add(Evento.de(TipoEvento.NAVE_CHEIA));
        }
        return concluirTurno(eventos);
    }

    public void abortar() {
        exigirEmAndamento();
        status = StatusMissao.ABORTADA;
    }

    /** O que acontece depois de toda ação: inimigos andam, depois colisão, pontuação e vitória. */
    private List<Evento> concluirTurno(List<Evento> eventos) {
        for (Inimigo inimigo : inimigos) {
            inimigo.mover(random, mapa);
        }

        if (haColisao()) {
            nave.perderVida();
            if (nave.estaDestruida()) {
                eventos.add(Evento.de(TipoEvento.NAVE_DESTRUIDA));
                status = StatusMissao.FRACASSADA;
            } else {
                eventos.add(Evento.colisao(nave.getVidas()));
            }
        }

        if (pontos <= 0) {
            eventos.add(Evento.de(TipoEvento.PONTUACAO_ZERADA));
            status = StatusMissao.FRACASSADA;
        }

        if (todosResgatados() && status == StatusMissao.EM_ANDAMENTO) {
            if (nave.getPosicao().equals(mapa.getPlataforma())) {
                status = StatusMissao.CUMPRIDA;
                eventos.add(Evento.de(TipoEvento.MISSAO_CUMPRIDA));
            } else {
                eventos.add(Evento.de(TipoEvento.RETORNE_A_PLATAFORMA));
            }
        }
        return eventos;
    }

    private boolean haColisao() {
        Posicao posicaoNave = nave.getPosicao();
        for (Asteroide a : asteroides) {
            if (a.colideCom(posicaoNave)) return true;
        }
        for (Inimigo i : inimigos) {
            if (i.colideCom(posicaoNave)) return true;
        }
        return false;
    }

    private Optional<Passageiro> passageiroNaPosicaoDaNave() {
        for (Passageiro p : passageiros) {
            if (p.getPosicao().equals(nave.getPosicao())) return Optional.of(p);
        }
        return Optional.empty();
    }

    private void exigirEmAndamento() {
        if (status != StatusMissao.EM_ANDAMENTO) {
            throw new IllegalStateException("A missão já terminou: " + status);
        }
    }

    // ---------------------------------------------------------------- consulta

    public boolean todosResgatados() { return passageiros.isEmpty(); }
    public boolean isEmAndamento() { return status == StatusMissao.EM_ANDAMENTO; }

    public Nave getNave() { return nave; }
    public Mapa getMapa() { return mapa; }
    public int getPontos() { return pontos; }
    public int getMovimentos() { return movimentos; }
    public StatusMissao getStatus() { return status; }

    /** Passageiros ainda na superfície (somente leitura). */
    public List<Passageiro> getPassageiros() { return Collections.unmodifiableList(passageiros); }
    public List<Asteroide> getAsteroides() { return Collections.unmodifiableList(asteroides); }
    public List<Inimigo> getInimigos() { return Collections.unmodifiableList(inimigos); }
}
