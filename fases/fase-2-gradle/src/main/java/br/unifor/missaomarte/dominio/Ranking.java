package br.unifor.missaomarte.dominio;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * Regras do ranking Top 5: ordem por pontuação (maior primeiro) e quem entra.
 * É imutável: adicionar um registro devolve um novo ranking.
 */
public final class Ranking {

    public static final int LIMITE = 5;

    private static final Comparator<RegistroRanking> MAIOR_PONTUACAO_PRIMEIRO =
            Comparator.comparingInt(RegistroRanking::pontuacao).reversed();

    private final List<RegistroRanking> registros;

    public Ranking(List<RegistroRanking> registros) {
        List<RegistroRanking> ordenados = new ArrayList<>(registros);
        ordenados.sort(MAIOR_PONTUACAO_PRIMEIRO);
        this.registros = Collections.unmodifiableList(ordenados);
    }

    public static Ranking vazio() {
        return new Ranking(List.of());
    }

    public List<RegistroRanking> getRegistros() { return registros; }
    public boolean isVazio() { return registros.isEmpty(); }

    public Optional<RegistroRanking> getRecorde() {
        return registros.isEmpty() ? Optional.empty() : Optional.of(registros.get(0));
    }

    /** Entra quem tem pontuação positiva e há vaga, ou supera o último colocado. */
    public boolean entraNoTop(int pontuacao) {
        if (pontuacao <= 0) return false;
        if (registros.size() < LIMITE) return true;
        return pontuacao > registros.get(registros.size() - 1).pontuacao();
    }

    /** Novo ranking com o registro incluído, mantendo só os 5 melhores. Em empate, quem já estava fica na frente. */
    public Ranking comNovoRegistro(RegistroRanking registro) {
        List<RegistroRanking> todos = new ArrayList<>(registros);
        todos.add(registro);
        todos.sort(MAIOR_PONTUACAO_PRIMEIRO);
        return new Ranking(todos.subList(0, Math.min(LIMITE, todos.size())));
    }
}
