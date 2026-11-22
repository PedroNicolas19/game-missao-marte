package br.unifor.missaomarte;

import br.unifor.missaomarte.aplicacao.FabricaMissao;
import br.unifor.missaomarte.aplicacao.FalhaNoRankingException;
import br.unifor.missaomarte.aplicacao.Partida;
import br.unifor.missaomarte.aplicacao.RepositorioRanking;
import br.unifor.missaomarte.aplicacao.ResultadoRegistro;
import br.unifor.missaomarte.aplicacao.ResumoPartida;
import br.unifor.missaomarte.aplicacao.ServicoRanking;
import br.unifor.missaomarte.dominio.Asteroide;
import br.unifor.missaomarte.dominio.Astronauta;
import br.unifor.missaomarte.dominio.Dificuldade;
import br.unifor.missaomarte.dominio.Direcao;
import br.unifor.missaomarte.dominio.Engenheiro;
import br.unifor.missaomarte.dominio.Evento;
import br.unifor.missaomarte.dominio.Inimigo;
import br.unifor.missaomarte.dominio.Mapa;
import br.unifor.missaomarte.dominio.Missao;
import br.unifor.missaomarte.dominio.Nave;
import br.unifor.missaomarte.dominio.Passageiro;
import br.unifor.missaomarte.dominio.Posicao;
import br.unifor.missaomarte.dominio.Professor;
import br.unifor.missaomarte.dominio.Ranking;
import br.unifor.missaomarte.dominio.RegistroRanking;
import br.unifor.missaomarte.dominio.StatusMissao;
import br.unifor.missaomarte.dominio.TipoEvento;
import br.unifor.missaomarte.infraestrutura.RepositorioRankingJson;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 * Testes automáticos das regras do jogo, sem biblioteca externa.
 * Na Fase 2 estes testes passam para JUnit 5.
 *
 * Executar (a partir de fases/fase-1-refatoracao):
 *   javac -encoding UTF-8 -d out $(find src test -name '*.java')
 *   java -cp out br.unifor.missaomarte.TestesRegras
 */
public class TestesRegras {

    private static int aprovados = 0;
    private static final List<String> falhas = new ArrayList<>();

    public static void main(String[] args) throws Exception {
        // Domínio
        teste("dificuldade define pontuação inicial e quantidades", TestesRegras::dificuldadeTemConfiguracao);
        teste("dificuldade desconhecida vira Médio", TestesRegras::dificuldadeDesconhecidaViraMedio);
        teste("nave não sai do mapa", TestesRegras::naveNaoSaiDoMapa);
        teste("cada movimento custa 1 ponto e conta como movimento", TestesRegras::movimentoCustaUmPonto);
        teste("embarcar soma a pontuação do tipo e tira o passageiro da superfície", TestesRegras::embarcarSomaPontos);
        teste("embarcar sem passageiro não muda pontos", TestesRegras::embarcarSemPassageiro);
        teste("nave cheia não embarca", TestesRegras::naveCheiaNaoEmbarca);
        teste("colisão tira vida e a terceira destrói a nave", TestesRegras::colisoesDestroemANave);
        teste("pontuação zerada encerra a missão", TestesRegras::pontuacaoZeradaEncerra);
        teste("vitória só ao voltar à plataforma com todos a bordo", TestesRegras::vitoriaSoNaPlataforma);
        teste("não aceita ações depois do fim da missão", TestesRegras::naoJogaDepoisDoFim);
        teste("inimigo anda sem sair do mapa", TestesRegras::inimigoNaoSaiDoMapa);
        teste("ranking ordena e guarda só os 5 melhores", TestesRegras::rankingGuardaTop5);
        teste("regra de entrada no Top 5", TestesRegras::regraDeEntradaNoTop5);

        // Aplicação
        teste("fábrica respeita as quantidades da dificuldade", TestesRegras::fabricaRespeitaQuantidades);
        teste("fábrica não sobrepõe elementos nem ocupa a plataforma", TestesRegras::fabricaNaoSobrepoe);
        teste("mesma semente gera o mesmo mapa", TestesRegras::mesmaSementeMesmoMapa);
        teste("serviço registra no ranking só quem entra no Top 5", TestesRegras::servicoRegistraSoTop5);
        teste("falha ao salvar é informada sem perder a vaga", TestesRegras::falhaAoSalvar);
        teste("partida cumprida gera resumo e entra no ranking", TestesRegras::partidaCumpridaGeraResumo);

        // Infraestrutura
        teste("ranking.json grava e lê os registros", TestesRegras::jsonIdaEVolta);
        teste("ranking.json ausente ou malformado vira ranking vazio", TestesRegras::jsonAusenteOuMalformado);
        teste("ranking.json da fase 0 continua legível", TestesRegras::jsonDaFase0);
        teste("limpar apaga o ranking.json", TestesRegras::limparApagaArquivo);

        System.out.println();
        System.out.printf("%d aprovados, %d com falha%n", aprovados, falhas.size());
        for (String falha : falhas) {
            System.out.println("  ✗ " + falha);
        }
        if (!falhas.isEmpty()) {
            System.exit(1);
        }
    }

    // ================================================================ domínio

    static void dificuldadeTemConfiguracao() {
        igual(30, Dificuldade.FACIL.getPontuacaoInicial(), "pontos fácil");
        igual(20, Dificuldade.MEDIO.getPontuacaoInicial(), "pontos médio");
        igual(45, Dificuldade.DIFICIL.getPontuacaoInicial(), "pontos difícil");
        igual(4, Dificuldade.FACIL.getQuantidadePassageiros(), "passageiros fácil");
        igual(1, Dificuldade.FACIL.getQuantidadeInimigos(), "inimigos fácil");
        igual(3, Dificuldade.DIFICIL.getQuantidadeAsteroides(), "asteroides difícil");
    }

    static void dificuldadeDesconhecidaViraMedio() {
        igual(Dificuldade.FACIL, Dificuldade.deString(" Fácil "), "fácil com acento");
        igual(Dificuldade.DIFICIL, Dificuldade.deString("dificil"), "difícil sem acento");
        igual(Dificuldade.MEDIO, Dificuldade.deString("xyz"), "valor desconhecido");
        igual(Dificuldade.MEDIO, Dificuldade.deString(null), "nulo");
    }

    static void naveNaoSaiDoMapa() {
        Missao missao = missaoVazia(1, 50);
        missao.mover(Direcao.CIMA);
        missao.mover(Direcao.CIMA);
        missao.mover(Direcao.ESQUERDA);
        missao.mover(Direcao.ESQUERDA);
        igual(new Posicao(-1, -1), missao.getNave().getPosicao(), "parou no canto");
    }

    static void movimentoCustaUmPonto() {
        Missao missao = missaoVazia(3, 20);
        missao.mover(Direcao.DIREITA);
        missao.mover(Direcao.BAIXO);
        igual(18, missao.getPontos(), "pontos");
        igual(2, missao.getMovimentos(), "movimentos");
    }

    static void embarcarSomaPontos() {
        Missao missao = missaoVazia(3, 20);
        missao.adicionarPassageiro(new Astronauta("Ast. Maria", new Posicao(1, 0)));
        missao.adicionarPassageiro(new Professor("Dr. Silva", new Posicao(3, 3)));
        missao.mover(Direcao.DIREITA);
        List<Evento> eventos = missao.embarcar();

        igual(TipoEvento.PASSAGEIRO_EMBARCADO, eventos.get(0).tipo(), "evento");
        igual(20, eventos.get(0).valor(), "bônus do astronauta");
        igual(19 + 20, missao.getPontos(), "pontos");
        igual(1, missao.getNave().getQuantidadeABordo(), "a bordo");
        igual(1, missao.getPassageiros().size(), "restantes na superfície");
    }

    static void embarcarSemPassageiro() {
        Missao missao = missaoVazia(3, 20);
        missao.adicionarPassageiro(new Professor("Dr. Silva", new Posicao(3, 3)));
        List<Evento> eventos = missao.embarcar();
        igual(TipoEvento.NENHUM_PASSAGEIRO, eventos.get(0).tipo(), "evento");
        igual(20, missao.getPontos(), "pontos");
        igual(0, missao.getMovimentos(), "movimentos");
    }

    static void naveCheiaNaoEmbarca() {
        Missao missao = new Missao(new Nave("T", 1), new Mapa(3), 20, new Random(1));
        missao.adicionarPassageiro(new Professor("A", new Posicao(1, 0)));
        missao.adicionarPassageiro(new Engenheiro("B", new Posicao(2, 0)));
        missao.mover(Direcao.DIREITA);
        missao.embarcar();
        missao.mover(Direcao.DIREITA);
        List<Evento> eventos = missao.embarcar();
        igual(TipoEvento.NAVE_CHEIA, eventos.get(0).tipo(), "evento");
        igual(1, missao.getPassageiros().size(), "B continua na superfície");
    }

    static void colisoesDestroemANave() {
        Missao missao = missaoVazia(3, 50);
        missao.adicionarPassageiro(new Professor("Dr. Silva", new Posicao(3, 3)));
        missao.adicionarAsteroide(new Asteroide(new Posicao(1, 0)));

        List<Evento> primeira = missao.mover(Direcao.DIREITA);
        igual(Evento.colisao(2), primeira.get(0), "primeira colisão");
        missao.embarcar();                                   // continua em cima do asteroide
        List<Evento> terceira = missao.embarcar();
        verdadeiro(contem(terceira, TipoEvento.NAVE_DESTRUIDA), "nave destruída");
        igual(0, missao.getNave().getVidas(), "vidas");
        igual(StatusMissao.FRACASSADA, missao.getStatus(), "status");
    }

    static void pontuacaoZeradaEncerra() {
        Missao missao = missaoVazia(3, 1);
        missao.adicionarPassageiro(new Professor("Dr. Silva", new Posicao(3, 3)));
        List<Evento> eventos = missao.mover(Direcao.DIREITA);
        verdadeiro(contem(eventos, TipoEvento.PONTUACAO_ZERADA), "evento");
        igual(StatusMissao.FRACASSADA, missao.getStatus(), "status");
    }

    static void vitoriaSoNaPlataforma() {
        Missao missao = missaoVazia(3, 20);
        missao.adicionarPassageiro(new Engenheiro("Eng. Rosa", new Posicao(1, 0)));
        missao.mover(Direcao.DIREITA);
        List<Evento> aposEmbarque = missao.embarcar();
        verdadeiro(contem(aposEmbarque, TipoEvento.RETORNE_A_PLATAFORMA), "pede para voltar");
        igual(StatusMissao.EM_ANDAMENTO, missao.getStatus(), "ainda em andamento");

        List<Evento> aoVoltar = missao.mover(Direcao.ESQUERDA);
        verdadeiro(contem(aoVoltar, TipoEvento.MISSAO_CUMPRIDA), "missão cumprida");
        igual(StatusMissao.CUMPRIDA, missao.getStatus(), "status");
        igual(20 - 2 + 15, missao.getPontos(), "pontos finais");
    }

    static void naoJogaDepoisDoFim() {
        Missao missao = missaoVazia(3, 20);
        missao.abortar();
        lanca(IllegalStateException.class, () -> missao.mover(Direcao.CIMA), "mover");
        lanca(IllegalStateException.class, missao::embarcar, "embarcar");
    }

    static void inimigoNaoSaiDoMapa() {
        Mapa mapa = new Mapa(1);
        Inimigo inimigo = new Inimigo(new Posicao(1, 1));
        Random random = new Random(7);
        for (int i = 0; i < 500; i++) {
            inimigo.mover(random, mapa);
            verdadeiro(mapa.contem(inimigo.getPosicao()), "dentro do mapa no passo " + i);
        }
    }

    static void rankingGuardaTop5() {
        Ranking ranking = Ranking.vazio();
        for (int pontos : new int[] {10, 50, 30, 20, 40, 60, 5}) {
            ranking = ranking.comNovoRegistro(registro("P" + pontos, pontos));
        }
        igual(5, ranking.getRegistros().size(), "tamanho");
        igual(60, ranking.getRecorde().orElseThrow().pontuacao(), "primeiro");
        igual(20, ranking.getRegistros().get(4).pontuacao(), "último");
    }

    static void regraDeEntradaNoTop5() {
        Ranking ranking = Ranking.vazio();
        falso(ranking.entraNoTop(0), "pontuação zero nunca entra");
        verdadeiro(ranking.entraNoTop(1), "há vaga");
        for (int pontos : new int[] {10, 20, 30, 40, 50}) {
            ranking = ranking.comNovoRegistro(registro("P" + pontos, pontos));
        }
        falso(ranking.entraNoTop(10), "empate com o último não entra");
        verdadeiro(ranking.entraNoTop(11), "supera o último");
    }

    // ============================================================== aplicação

    static void fabricaRespeitaQuantidades() {
        for (Dificuldade d : Dificuldade.values()) {
            Missao missao = new FabricaMissao(new Random(3)).criar(d, new Mapa(5));
            igual(d.getQuantidadePassageiros(), missao.getPassageiros().size(), d + " passageiros");
            igual(d.getQuantidadeAsteroides(), missao.getAsteroides().size(), d + " asteroides");
            igual(d.getQuantidadeInimigos(), missao.getInimigos().size(), d + " inimigos");
            igual(d.getPontuacaoInicial(), missao.getPontos(), d + " pontos iniciais");
        }
    }

    static void fabricaNaoSobrepoe() {
        for (long semente = 0; semente < 200; semente++) {
            Mapa mapa = new Mapa(2);
            Missao missao = new FabricaMissao(new Random(semente)).criar(Dificuldade.DIFICIL, mapa);
            Set<Posicao> ocupadas = new HashSet<>();
            ocupadas.add(missao.getNave().getPosicao());
            missao.getPassageiros().forEach(p -> ocupadas.add(p.getPosicao()));
            missao.getAsteroides().forEach(a -> ocupadas.add(a.getPosicao()));
            missao.getInimigos().forEach(i -> ocupadas.add(i.getPosicao()));
            igual(1 + 5 + 3 + 3, ocupadas.size(), "posições distintas (semente " + semente + ")");
            ocupadas.forEach(p -> verdadeiro(mapa.contem(p), "dentro do mapa"));
        }
    }

    static void mesmaSementeMesmoMapa() {
        Missao a = new FabricaMissao(new Random(42)).criar(Dificuldade.MEDIO, new Mapa(5));
        Missao b = new FabricaMissao(new Random(42)).criar(Dificuldade.MEDIO, new Mapa(5));
        for (int i = 0; i < a.getPassageiros().size(); i++) {
            igual(a.getPassageiros().get(i).getPosicao(), b.getPassageiros().get(i).getPosicao(), "passageiro " + i);
        }
        for (int i = 0; i < a.getInimigos().size(); i++) {
            igual(a.getInimigos().get(i).getPosicao(), b.getInimigos().get(i).getPosicao(), "inimigo " + i);
        }
    }

    static void servicoRegistraSoTop5() {
        RepositorioEmMemoria repositorio = new RepositorioEmMemoria();
        ServicoRanking servico = new ServicoRanking(repositorio);
        for (int pontos : new int[] {10, 20, 30, 40, 50}) {
            verdadeiro(servico.registrar(registro("P" + pontos, pontos)).entrouNoTop(), "entra " + pontos);
        }
        falso(servico.registrar(registro("Fraco", 5)).entrouNoTop(), "não entra");
        igual(5, repositorio.salvos.size(), "continua com 5");
        igual(10, servico.consultar().getRegistros().get(4).pontuacao(), "Fraco não foi salvo");
    }

    static void falhaAoSalvar() {
        RepositorioEmMemoria repositorio = new RepositorioEmMemoria();
        repositorio.falharAoSalvar = true;
        ResultadoRegistro resultado = new ServicoRanking(repositorio).registrar(registro("Ana", 30));
        verdadeiro(resultado.entrouNoTop(), "entrou");
        igual("disco cheio", resultado.erroAoSalvar(), "mensagem");
    }

    static void partidaCumpridaGeraResumo() {
        RepositorioEmMemoria repositorio = new RepositorioEmMemoria();
        repositorio.salvos.add(registro("Recordista", 99));
        Clock relogio = Clock.fixed(Instant.parse("2026-11-22T13:00:01Z"), ZoneOffset.UTC);

        Missao missao = missaoVazia(3, 20);
        missao.adicionarPassageiro(new Professor("Dr. Silva", new Posicao(1, 0)));
        Partida partida = new Partida("Ana", Dificuldade.MEDIO, missao, new ServicoRanking(repositorio), relogio);
        partida.mover(Direcao.DIREITA);
        partida.embarcar();
        partida.mover(Direcao.ESQUERDA);

        ResumoPartida resumo = partida.concluir();
        igual(28, resumo.pontuacao(), "pontos");
        igual(2, resumo.movimentos(), "movimentos");
        igual(1, resumo.passageirosResgatados(), "resgatados");
        igual("Recordista", resumo.recordeAnterior().nome(), "recorde anterior");
        verdadeiro(resumo.resultadoRanking().entrouNoTop(), "entrou no ranking");
        igual("2026-11-22 13:00:01", repositorio.salvos.get(1).dataHora(), "data formatada");
    }

    // ========================================================= infraestrutura

    static void jsonIdaEVolta() throws Exception {
        Path arquivo = Files.createTempFile("ranking", ".json");
        try {
            RepositorioRankingJson repositorio = new RepositorioRankingJson(arquivo);
            List<RegistroRanking> originais = List.of(
                    new RegistroRanking("Ana", 42, Dificuldade.FACIL, 4, "2026-11-22 10:00:00", 30),
                    new RegistroRanking("Bia", 50, Dificuldade.DIFICIL, 5, "2026-11-22 11:00:00", 12));
            repositorio.salvar(originais);
            igual(originais, repositorio.carregar(), "registros lidos");
        } finally {
            Files.deleteIfExists(arquivo);
        }
    }

    static void jsonAusenteOuMalformado() throws Exception {
        Path pasta = Files.createTempDirectory("ranking");
        Path arquivo = pasta.resolve("ranking.json");
        try {
            RepositorioRankingJson repositorio = new RepositorioRankingJson(arquivo);
            verdadeiro(repositorio.carregar().isEmpty(), "arquivo ausente");
            Files.write(arquivo, "[{\"name\":\"Ana\",\"score\":".getBytes(StandardCharsets.UTF_8));
            verdadeiro(repositorio.carregar().isEmpty(), "arquivo cortado");
        } finally {
            Files.deleteIfExists(arquivo);
            Files.deleteIfExists(pasta);
        }
    }

    static void jsonDaFase0() throws Exception {
        Path arquivo = Files.createTempFile("ranking", ".json");
        try {
            String fase0 = "[{\"name\":\"Ana\",\"score\":33,\"dificuldade\":\"MEDIO\",\"passageirosColetados\":5,"
                    + "\"dataHora\":\"2026-11-22 09:00:00\",\"tempoJogo\":40}]";
            Files.write(arquivo, fase0.getBytes(StandardCharsets.UTF_8));
            List<RegistroRanking> lidos = new RepositorioRankingJson(arquivo).carregar();
            igual(List.of(new RegistroRanking("Ana", 33, Dificuldade.MEDIO, 5, "2026-11-22 09:00:00", 40)), lidos, "registro");
        } finally {
            Files.deleteIfExists(arquivo);
        }
    }

    static void limparApagaArquivo() throws Exception {
        Path arquivo = Files.createTempFile("ranking", ".json");
        RepositorioRankingJson repositorio = new RepositorioRankingJson(arquivo);
        repositorio.salvar(List.of(registro("Ana", 10)));
        repositorio.limpar();
        falso(Files.exists(arquivo), "arquivo apagado");
    }

    // ================================================================ apoio

    /** Repositório falso em memória: mostra que a aplicação não depende do arquivo JSON (DIP). */
    static class RepositorioEmMemoria implements RepositorioRanking {
        final List<RegistroRanking> salvos = new ArrayList<>();
        boolean falharAoSalvar;

        @Override public List<RegistroRanking> carregar() { return new ArrayList<>(salvos); }

        @Override public void salvar(List<RegistroRanking> registros) throws FalhaNoRankingException {
            if (falharAoSalvar) throw new FalhaNoRankingException("disco cheio", null);
            salvos.clear();
            salvos.addAll(registros);
        }

        @Override public void limpar() { salvos.clear(); }
    }

    private static Missao missaoVazia(int tamanhoMapa, int pontos) {
        return new Missao(new Nave("T-1", 5), new Mapa(tamanhoMapa), pontos, new Random(1));
    }

    private static RegistroRanking registro(String nome, int pontos) {
        return new RegistroRanking(nome, pontos, Dificuldade.MEDIO, 5, "2026-11-22 12:00:00", 60);
    }

    private static boolean contem(List<Evento> eventos, TipoEvento tipo) {
        return eventos.stream().anyMatch(e -> e.tipo() == tipo);
    }

    // ======================================================= mini framework

    interface Corpo { void executar() throws Exception; }

    private static void teste(String nome, Corpo corpo) {
        try {
            corpo.executar();
            aprovados++;
            System.out.println("  ✓ " + nome);
        } catch (Throwable t) {
            falhas.add(nome + ": " + t.getMessage());
            System.out.println("  ✗ " + nome + ": " + t.getMessage());
        }
    }

    private static void igual(Object esperado, Object obtido, String oQue) {
        if (esperado == null ? obtido != null : !esperado.equals(obtido)) {
            throw new AssertionError(oQue + " — esperado <" + esperado + "> mas foi <" + obtido + ">");
        }
    }

    private static void verdadeiro(boolean condicao, String oQue) {
        if (!condicao) throw new AssertionError(oQue + " — esperado verdadeiro");
    }

    private static void falso(boolean condicao, String oQue) {
        if (condicao) throw new AssertionError(oQue + " — esperado falso");
    }

    private static void lanca(Class<? extends Throwable> tipo, Corpo corpo, String oQue) {
        try {
            corpo.executar();
        } catch (Throwable t) {
            if (tipo.isInstance(t)) return;
            throw new AssertionError(oQue + " — lançou " + t.getClass().getSimpleName() + " em vez de " + tipo.getSimpleName());
        }
        throw new AssertionError(oQue + " — esperado " + tipo.getSimpleName());
    }
}
