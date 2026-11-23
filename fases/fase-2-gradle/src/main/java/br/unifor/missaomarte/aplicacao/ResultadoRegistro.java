package br.unifor.missaomarte.aplicacao;

/**
 * Resultado da tentativa de colocar uma missão no ranking.
 *
 * @param entrouNoTop  a pontuação garantiu uma vaga no Top 5
 * @param erroAoSalvar mensagem de erro se a vaga foi garantida mas a gravação falhou; senão null
 */
public record ResultadoRegistro(boolean entrouNoTop, String erroAoSalvar) {

    static ResultadoRegistro naoEntrou() { return new ResultadoRegistro(false, null); }
    static ResultadoRegistro entrou() { return new ResultadoRegistro(true, null); }
    static ResultadoRegistro entrouSemSalvar(String erro) { return new ResultadoRegistro(true, erro); }
}
