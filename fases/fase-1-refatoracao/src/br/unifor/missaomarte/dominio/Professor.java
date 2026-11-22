package br.unifor.missaomarte.dominio;

public class Professor extends Passageiro {

    public Professor(String nome, Posicao posicao) {
        super(nome, posicao);
    }

    @Override public String getTipo() { return "Professor"; }
    @Override public int getPontuacao() { return 10; }
    @Override public char getSimbolo() { return 'P'; }
}
