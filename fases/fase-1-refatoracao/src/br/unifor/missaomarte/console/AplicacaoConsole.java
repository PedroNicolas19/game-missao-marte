package br.unifor.missaomarte.console;

/** Menu principal: direciona para partida, ranking ou saída. */
class AplicacaoConsole {

    private final EntradaConsole entrada;
    private final TelaPartida telaPartida;
    private final TelaRanking telaRanking;

    AplicacaoConsole(EntradaConsole entrada, TelaPartida telaPartida, TelaRanking telaRanking) {
        this.entrada = entrada;
        this.telaPartida = telaPartida;
        this.telaRanking = telaRanking;
    }

    void executar() {
        exibirBoasVindas();

        boolean rodando = true;
        while (rodando) {
            if (!entrada.temProximaLinha()) {
                System.out.println("\nEntrada encerrada. Obrigado por jogar!");
                break;
            }
            exibirMenu();
            String opcao = entrada.lerLinha("Escolha uma opção: ", "1");
            switch (opcao) {
                case "1":
                    telaPartida.jogar();
                    break;
                case "2":
                    telaRanking.exibir();
                    break;
                case "3":
                    telaRanking.resetar();
                    break;
                case "4":
                    rodando = false;
                    System.out.println("\nObrigado por jogar a Missão Marte Unifor!");
                    break;
                default:
                    System.out.println("Opção inválida. Tente novamente.");
            }
        }
    }

    private void exibirBoasVindas() {
        System.out.println("================================================================");
        System.out.println("             MISSÃO MARTE UNIFOR - VERSÃO 10.0                  ");
        System.out.println("================================================================");
        System.out.println("  Pilote sua nave, salve os passageiros e desvie dos perigos!   ");
        System.out.println("================================================================");
    }

    private void exibirMenu() {
        System.out.println("\n--- MENU PRINCIPAL ---");
        System.out.println("1. Iniciar Nova Missão");
        System.out.println("2. Visualizar Ranking Top 5");
        System.out.println("3. Resetar Histórico de Ranking");
        System.out.println("4. Sair do Jogo");
        System.out.println("----------------------");
    }
}
