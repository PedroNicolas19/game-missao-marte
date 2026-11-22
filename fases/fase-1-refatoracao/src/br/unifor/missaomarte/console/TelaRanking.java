package br.unifor.missaomarte.console;

import br.unifor.missaomarte.aplicacao.FalhaNoRankingException;
import br.unifor.missaomarte.aplicacao.ServicoRanking;
import br.unifor.missaomarte.dominio.Ranking;
import br.unifor.missaomarte.dominio.RegistroRanking;

/** Telas de consulta e limpeza do ranking. */
class TelaRanking {

    private final EntradaConsole entrada;
    private final ServicoRanking servicoRanking;

    TelaRanking(EntradaConsole entrada, ServicoRanking servicoRanking) {
        this.entrada = entrada;
        this.servicoRanking = servicoRanking;
    }

    void exibir() {
        Ranking ranking = servicoRanking.consultar();
        System.out.println("\n====== RANKING TOP 5 PILOTOS ======");
        if (ranking.isVazio()) {
            System.out.println(" - Nenhum registro encontrado. Seja o primeiro a jogar!");
        } else {
            int posicao = 1;
            for (RegistroRanking r : ranking.getRegistros()) {
                System.out.printf("%d. %s - %d pts | Dificuldade: %s | Coletados: %d | Tempo: %ds | %s%n",
                        posicao++, r.nome(), r.pontuacao(), r.dificuldade(),
                        r.passageirosResgatados(), r.tempoSegundos(), r.dataHora());
            }
        }
        System.out.println("===================================");
    }

    void resetar() {
        System.out.print("Você realmente deseja limpar o histórico de ranking? (s/n): ");
        String confirmacao = entrada.lerLinha("", "n").toLowerCase();
        if (!confirmacao.equals("s") && !confirmacao.equals("sim")) {
            System.out.println("Operação cancelada.");
            return;
        }
        try {
            servicoRanking.limpar();
            System.out.println("Ranking resetado com sucesso!");
        } catch (FalhaNoRankingException e) {
            System.out.println("Erro ao resetar ranking: " + e.getMessage());
        }
    }
}
