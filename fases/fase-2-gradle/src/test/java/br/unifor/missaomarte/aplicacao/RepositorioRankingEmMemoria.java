package br.unifor.missaomarte.aplicacao;

import br.unifor.missaomarte.dominio.RegistroRanking;

import java.util.ArrayList;
import java.util.List;

/**
 * Repositório falso, em memória. Mostra que a aplicação depende só da
 * interface RepositorioRanking, e não do arquivo JSON (DIP).
 */
class RepositorioRankingEmMemoria implements RepositorioRanking {

    final List<RegistroRanking> salvos = new ArrayList<>();
    boolean falharAoSalvar;

    @Override
    public List<RegistroRanking> carregar() {
        return new ArrayList<>(salvos);
    }

    @Override
    public void salvar(List<RegistroRanking> registros) throws FalhaNoRankingException {
        if (falharAoSalvar) {
            throw new FalhaNoRankingException("disco cheio", null);
        }
        salvos.clear();
        salvos.addAll(registros);
    }

    @Override
    public void limpar() {
        salvos.clear();
    }
}
