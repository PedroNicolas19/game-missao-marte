package br.unifor.missaomarte.dominio;

/**
 * Fato ocorrido num turno.
 *
 * @param tipo       o que aconteceu
 * @param passageiro passageiro envolvido (só em PASSAGEIRO_EMBARCADO; senão null)
 * @param valor      pontos ganhos (PASSAGEIRO_EMBARCADO) ou vidas restantes (COLISAO); senão 0
 */
public record Evento(TipoEvento tipo, Passageiro passageiro, int valor) {

    public static Evento de(TipoEvento tipo) {
        return new Evento(tipo, null, 0);
    }

    public static Evento embarque(Passageiro passageiro) {
        return new Evento(TipoEvento.PASSAGEIRO_EMBARCADO, passageiro, passageiro.getPontuacao());
    }

    public static Evento colisao(int vidasRestantes) {
        return new Evento(TipoEvento.COLISAO, null, vidasRestantes);
    }
}
