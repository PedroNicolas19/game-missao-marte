package br.unifor.missaomarte.dominio;

/** O que pode acontecer durante um turno. A interface decide como mostrar cada evento. */
public enum TipoEvento {
    NENHUM_PASSAGEIRO,
    PASSAGEIRO_EMBARCADO,
    NAVE_CHEIA,
    COLISAO,
    NAVE_DESTRUIDA,
    PONTUACAO_ZERADA,
    RETORNE_A_PLATAFORMA,
    MISSAO_CUMPRIDA
}
