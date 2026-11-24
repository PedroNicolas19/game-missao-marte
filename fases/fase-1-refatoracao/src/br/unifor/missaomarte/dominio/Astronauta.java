package br.unifor.missaomarte.dominio;

public class Astronauta extends Passageiro {

    public Astronauta(String nome, Posicao posicao) {
        super(nome, posicao);
    }

    @Override public String getTipo() { return "Astronauta"; }
    @Override public int getPontuacao() { return 20; }
    @Override public char getSimbolo() { return 'T'; }
}
