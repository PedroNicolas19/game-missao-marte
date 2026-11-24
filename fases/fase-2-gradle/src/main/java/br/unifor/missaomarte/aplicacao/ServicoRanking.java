package br.unifor.missaomarte.aplicacao;

import br.unifor.missaomarte.dominio.Ranking;
import br.unifor.missaomarte.dominio.RegistroRanking;

/** Casos de uso do ranking: consultar, registrar uma missão cumprida e limpar o histórico. */
public class ServicoRanking {

    private final RepositorioRanking repositorio;

    public ServicoRanking(RepositorioRanking repositorio) {
        this.repositorio = repositorio;
    }

    public Ranking consultar() {
        return new Ranking(repositorio.carregar());
    }

    /**
     * Inclui o registro se ele entrar no Top 5.
     *
     * @return se entrou e, caso a gravação tenha falhado, a mensagem de erro
     */
    public ResultadoRegistro registrar(RegistroRanking registro) {
        Ranking atual = consultar();
        if (!atual.entraNoTop(registro.pontuacao())) {
            return ResultadoRegistro.naoEntrou();
        }
        try {
            repositorio.salvar(atual.comNovoRegistro(registro).getRegistros());
            return ResultadoRegistro.entrou();
        } catch (FalhaNoRankingException e) {
            return ResultadoRegistro.entrouSemSalvar(e.getMessage());
        }
    }

    public void limpar() throws FalhaNoRankingException {
        repositorio.limpar();
    }
}
