package br.unifor.missaomarte.console;

import br.unifor.missaomarte.aplicacao.FabricaMissao;
import br.unifor.missaomarte.aplicacao.Partida;
import br.unifor.missaomarte.aplicacao.ResultadoRegistro;
import br.unifor.missaomarte.aplicacao.ResumoPartida;
import br.unifor.missaomarte.aplicacao.ServicoRanking;
import br.unifor.missaomarte.dominio.Dificuldade;
import br.unifor.missaomarte.dominio.Direcao;
import br.unifor.missaomarte.dominio.Evento;
import br.unifor.missaomarte.dominio.Mapa;
import br.unifor.missaomarte.dominio.Missao;
import br.unifor.missaomarte.dominio.Nave;
import br.unifor.missaomarte.dominio.RegistroRanking;
import br.unifor.missaomarte.dominio.StatusMissao;

import java.time.Clock;
import java.util.List;

/**
 * Tela de uma partida: lê a configuração, mostra o mapa, traduz teclas em ações
 * e transforma os eventos do domínio em mensagens.
 */
class TelaPartida {

    private static final String PILOTO_PADRAO = "Piloto Anônimo";
    private static final int TAMANHO_PADRAO = 5;

    private final EntradaConsole entrada;
    private final RenderizadorMapa renderizador;
    private final FabricaMissao fabricaMissao;
    private final ServicoRanking servicoRanking;
    private final Clock relogio;

    TelaPartida(EntradaConsole entrada, RenderizadorMapa renderizador, FabricaMissao fabricaMissao,
                ServicoRanking servicoRanking, Clock relogio) {
        this.entrada = entrada;
        this.renderizador = renderizador;
        this.fabricaMissao = fabricaMissao;
        this.servicoRanking = servicoRanking;
        this.relogio = relogio;
    }

    void jogar() {
        String piloto = entrada.lerLinha("\nDigite o nome do piloto: ", PILOTO_PADRAO);
        if (piloto.isEmpty()) {
            piloto = PILOTO_PADRAO;
        }
        Dificuldade dificuldade = lerDificuldade();
        Mapa mapa = new Mapa(lerTamanhoMapa());

        System.out.println("\nIniciando missão na dificuldade " + dificuldade + "...");
        System.out.println("Pressione Enter para decolar!");
        if (!entrada.temProximaLinha()) {
            System.out.println("Entrada encerrada. Missão não iniciada.");
            return;
        }
        entrada.descartarLinha();

        Missao missao = fabricaMissao.criar(dificuldade, mapa);
        Partida partida = new Partida(piloto, dificuldade, missao, servicoRanking, relogio);
        executar(partida);
    }

    private void executar(Partida partida) {
        Missao missao = partida.getMissao();
        while (missao.isEmAndamento()) {
            renderizador.desenhar(missao, partida.getPiloto());
            exibirStatus(missao);

            String comando = entrada.lerLinha("Comando (w/s/a/d/c/q): ", "").toLowerCase();
            if (comando.isEmpty()) {
                if (!entrada.temProximaLinha()) {
                    System.out.println("Entrada encerrada. Missão abortada.");
                    partida.abortar();
                }
                continue;
            }

            char tecla = comando.charAt(0);
            List<Evento> eventos;
            if (tecla == 'q') {
                System.out.println("Missão abortada pelo piloto.");
                partida.abortar();
                continue;
            } else if (tecla == 'c') {
                eventos = partida.embarcar();
            } else {
                Direcao direcao = direcaoDaTecla(tecla);
                if (direcao == null) {
                    System.out.println("Comando inválido.");
                    continue;
                }
                eventos = partida.mover(direcao);
            }
            exibirEventos(eventos, missao);
        }

        if (missao.getStatus() == StatusMissao.CUMPRIDA) {
            exibirVitoria(partida.concluir());
        }
    }

    private static Direcao direcaoDaTecla(char tecla) {
        switch (tecla) {
            case 'w': return Direcao.CIMA;
            case 's': return Direcao.BAIXO;
            case 'a': return Direcao.ESQUERDA;
            case 'd': return Direcao.DIREITA;
            default: return null;
        }
    }

    private void exibirStatus(Missao missao) {
        Nave nave = missao.getNave();
        System.out.printf("Nave em (%d,%d) | Pontos: %d | Vidas: %d | A bordo: %d/%d | Restantes: %d%n",
                nave.getPosicao().x(), nave.getPosicao().y(), missao.getPontos(), nave.getVidas(),
                nave.getQuantidadeABordo(), nave.getCapacidade(), missao.getPassageiros().size());
    }

    private void exibirEventos(List<Evento> eventos, Missao missao) {
        for (Evento evento : eventos) {
            switch (evento.tipo()) {
                case NENHUM_PASSAGEIRO:
                    System.out.println("Nenhum passageiro nesta posição.");
                    break;
                case PASSAGEIRO_EMBARCADO:
                    System.out.printf("Passageiro %s embarcado com sucesso! +%d pontos!%n",
                            evento.passageiro().getNome(), evento.valor());
                    break;
                case NAVE_CHEIA:
                    System.out.println("Nave cheia! Não há espaço para mais passageiros.");
                    break;
                case COLISAO:
                    System.out.printf("Alerta! Colisão detectada! Vidas restantes: %d%n", evento.valor());
                    break;
                case NAVE_DESTRUIDA:
                    System.out.println("GAME OVER! A nave foi destruída.");
                    break;
                case PONTUACAO_ZERADA:
                    System.out.println("Combustível/Pontuação zerada! Missão perdida.");
                    break;
                case RETORNE_A_PLATAFORMA:
                    System.out.println("✨ ALERTA: Todos os passageiros resgatados! Retorne para a Plataforma de Pouso 'L' em "
                            + missao.getMapa().getPlataforma() + " para completar a missão.");
                    break;
                case MISSAO_CUMPRIDA:
                    // a mensagem de vitória é mostrada ao concluir a partida
                    break;
            }
        }
    }

    private void exibirVitoria(ResumoPartida resumo) {
        System.out.println("\n================================================================");
        System.out.println("🚀 DECOLAGEM AUTORIZADA! Nave acoplada à plataforma em (0,0).");
        System.out.println("Retornando à órbita marciana com todos os passageiros. Missão cumprida!");
        System.out.println("================================================================");
        System.out.println("Estatísticas da Partida:");
        System.out.printf(" - Pontuação Final: %d pontos%n", resumo.pontuacao());
        System.out.printf(" - Movimentos Efetuados: %d%n", resumo.movimentos());
        System.out.printf(" - Tempo de Jogo: %d segundos%n", resumo.tempoSegundos());
        System.out.printf(" - Passageiros Resgatados: %d%n", resumo.passageirosResgatados());

        RegistroRanking recorde = resumo.recordeAnterior();
        int pontosRecorde = recorde == null ? 0 : recorde.pontuacao();
        if (resumo.pontuacao() > pontosRecorde && pontosRecorde > 0) {
            System.out.println("🏆 Novo recorde absoluto do sistema!");
        } else if (pontosRecorde > 0) {
            System.out.printf(" - Recorde atual a ser batido: %d pontos (Piloto: %s)%n", pontosRecorde, recorde.nome());
        }
        System.out.println("================================================================");

        ResultadoRegistro ranking = resumo.resultadoRanking();
        if (ranking.erroAoSalvar() != null) {
            System.out.println("Não foi possível salvar o ranking: " + ranking.erroAoSalvar());
        }
        if (ranking.entrouNoTop()) {
            System.out.println("Parabéns! Você entrou para o Top 5 de pilotos!");
        }
    }

    private Dificuldade lerDificuldade() {
        System.out.print("Escolha a Dificuldade (facil/medio/dificil): ");
        return Dificuldade.deString(entrada.lerLinha("", "medio"));
    }

    private int lerTamanhoMapa() {
        try {
            int tamanho = Integer.parseInt(entrada.lerLinha(
                    "Tamanho do mapa (ex: 5 para mapa de -5 a +5): ", String.valueOf(TAMANHO_PADRAO)));
            return tamanho > 0 ? tamanho : TAMANHO_PADRAO;
        } catch (NumberFormatException e) {
            System.out.println("Entrada inválida, usando tamanho padrão (5).");
            return TAMANHO_PADRAO;
        }
    }
}
