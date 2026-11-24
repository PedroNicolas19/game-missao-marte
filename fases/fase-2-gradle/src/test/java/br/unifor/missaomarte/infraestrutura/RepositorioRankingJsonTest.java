package br.unifor.missaomarte.infraestrutura;

import br.unifor.missaomarte.dominio.Dificuldade;
import br.unifor.missaomarte.dominio.RegistroRanking;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static br.unifor.missaomarte.DadosDeTeste.registro;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Todos os arquivos ficam em uma pasta temporária criada e apagada pelo JUnit (@TempDir). */
class RepositorioRankingJsonTest {

    @TempDir
    Path pasta;

    private Path arquivo() {
        return pasta.resolve("ranking.json");
    }

    @Test
    void gravaELeNomesComVirgulaAspasEChaves() throws Exception {
        RepositorioRankingJson repositorio = new RepositorioRankingJson(arquivo());
        List<RegistroRanking> originais = List.of(
                new RegistroRanking("Silva, João \"Jo\" {1} \\ fim", 42, Dificuldade.FACIL, 4, "2026-11-22 10:00:00", 30),
                new RegistroRanking("Bia", 50, Dificuldade.DIFICIL, 5, "2026-11-22 11:00:00", 12));

        repositorio.salvar(originais);

        assertTrue(Files.exists(arquivo()));
        assertEquals(originais, repositorio.carregar());
    }

    @Test
    void arquivoAusenteOuMalformadoViraRankingVazio() throws Exception {
        RepositorioRankingJson repositorio = new RepositorioRankingJson(arquivo());
        assertTrue(repositorio.carregar().isEmpty(), "arquivo ausente");

        Files.writeString(arquivo(), "[{\"name\":\"Ana\",\"score\":", StandardCharsets.UTF_8);
        assertTrue(repositorio.carregar().isEmpty(), "arquivo cortado");
    }

    @Test
    void rankingGravadoPelaFase0ContinuaLegivel() throws Exception {
        String fase0 = "[{\"name\":\"Ana\",\"score\":33,\"dificuldade\":\"MEDIO\",\"passageirosColetados\":5,"
                + "\"dataHora\":\"2026-11-22 09:00:00\",\"tempoJogo\":40}]";
        Files.writeString(arquivo(), fase0, StandardCharsets.UTF_8);

        List<RegistroRanking> lidos = new RepositorioRankingJson(arquivo()).carregar();

        assertEquals(List.of(new RegistroRanking("Ana", 33, Dificuldade.MEDIO, 5, "2026-11-22 09:00:00", 40)), lidos);
    }

    @Test
    void limparApagaOArquivo() throws Exception {
        RepositorioRankingJson repositorio = new RepositorioRankingJson(arquivo());
        repositorio.salvar(List.of(registro("Ana", 10)));

        repositorio.limpar();

        assertFalse(Files.exists(arquivo()));
    }
}
