package br.unifor.missaomarte.aplicacao;

import br.unifor.missaomarte.dominio.RegistroRanking;

import java.util.List;

/**
 * Onde o ranking fica guardado. A aplicação depende desta interface, e não do
 * arquivo JSON (DIP): na Fase 5 o mesmo papel poderá ser feito pelo servidor.
 */
public interface RepositorioRanking {

    /** Registros salvos. Se não houver nada salvo ou não for possível ler, devolve lista vazia. */
    List<RegistroRanking> carregar();

    void salvar(List<RegistroRanking> registros) throws FalhaNoRankingException;

    void limpar() throws FalhaNoRankingException;
}
