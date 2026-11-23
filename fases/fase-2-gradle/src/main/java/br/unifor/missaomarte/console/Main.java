package br.unifor.missaomarte.console;

import br.unifor.missaomarte.aplicacao.FabricaMissao;
import br.unifor.missaomarte.aplicacao.ServicoRanking;
import br.unifor.missaomarte.infraestrutura.RepositorioRankingJson;

import java.nio.file.Paths;
import java.time.Clock;
import java.util.Random;
import java.util.Scanner;

/**
 * Ponto de entrada. Só monta as peças e liga umas às outras (composition root);
 * nenhuma regra do jogo fica aqui.
 */
public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Random random = new Random();
        Clock relogio = Clock.systemDefaultZone();

        ServicoRanking servicoRanking = new ServicoRanking(new RepositorioRankingJson(Paths.get("ranking.json")));
        EntradaConsole entrada = new EntradaConsole(scanner);
        TelaPartida telaPartida = new TelaPartida(entrada, new RenderizadorMapa(),
                new FabricaMissao(random), servicoRanking, relogio);
        TelaRanking telaRanking = new TelaRanking(entrada, servicoRanking);

        new AplicacaoConsole(entrada, telaPartida, telaRanking).executar();
        scanner.close();
    }
}
