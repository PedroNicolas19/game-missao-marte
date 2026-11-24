package br.unifor.missaomarte.aplicacao;

/** Não foi possível gravar ou apagar o ranking. */
public class FalhaNoRankingException extends Exception {

    private static final long serialVersionUID = 1L;

    public FalhaNoRankingException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}
