package br.unifor.missaomarte.dominio;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Nave do jogador: posição, vidas e passageiros a bordo. Começa na plataforma (0,0). */
public class Nave {

    public static final int VIDAS_INICIAIS = 3;

    private final String id;
    private final int capacidade;
    private final List<Passageiro> passageiros = new ArrayList<>();
    private int vidas = VIDAS_INICIAIS;
    private Posicao posicao = Posicao.ORIGEM;

    public Nave(String id, int capacidade) {
        this.id = id;
        this.capacidade = capacidade;
    }

    public String getId() { return id; }
    public int getCapacidade() { return capacidade; }
    public int getVidas() { return vidas; }
    public Posicao getPosicao() { return posicao; }

    /** Lista somente leitura: só a própria nave altera quem está a bordo. */
    public List<Passageiro> getPassageiros() {
        return Collections.unmodifiableList(passageiros);
    }

    public int getQuantidadeABordo() {
        return passageiros.size();
    }

    /** Anda uma casa; se o destino estiver fora do mapa, a nave fica parada. */
    public void mover(Direcao direcao, Mapa mapa) {
        Posicao destino = posicao.mover(direcao);
        if (mapa.contem(destino)) {
            posicao = destino;
        }
    }

    public boolean embarcar(Passageiro passageiro) {
        if (passageiros.size() >= capacidade) {
            return false;
        }
        passageiros.add(passageiro);
        return true;
    }

    public void perderVida() {
        if (vidas > 0) {
            vidas--;
        }
    }

    public boolean estaDestruida() {
        return vidas == 0;
    }
}
