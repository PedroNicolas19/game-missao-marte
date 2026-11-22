package br.unifor.missaomarte.infraestrutura;

import br.unifor.missaomarte.aplicacao.FalhaNoRankingException;
import br.unifor.missaomarte.aplicacao.RepositorioRanking;
import br.unifor.missaomarte.dominio.Dificuldade;
import br.unifor.missaomarte.dominio.RegistroRanking;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Guarda o ranking num arquivo JSON (por padrão, ranking.json na pasta de execução).
 * O formato do arquivo é o mesmo da Fase 0, então rankings antigos continuam válidos.
 */
public class RepositorioRankingJson implements RepositorioRanking {

    private final Path arquivo;

    public RepositorioRankingJson(Path arquivo) {
        this.arquivo = arquivo;
    }

    @Override
    public List<RegistroRanking> carregar() {
        if (!Files.exists(arquivo)) {
            return new ArrayList<>();
        }
        try {
            String json = new String(Files.readAllBytes(arquivo), StandardCharsets.UTF_8).trim();
            return lerJson(json);
        } catch (IOException e) {
            return new ArrayList<>();
        }
    }

    @Override
    public void salvar(List<RegistroRanking> registros) throws FalhaNoRankingException {
        try {
            Files.write(arquivo, escreverJson(registros).getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new FalhaNoRankingException(e.getMessage(), e);
        }
    }

    @Override
    public void limpar() throws FalhaNoRankingException {
        try {
            Files.deleteIfExists(arquivo);
        } catch (IOException e) {
            throw new FalhaNoRankingException(e.getMessage(), e);
        }
    }

    static String escreverJson(List<RegistroRanking> registros) {
        StringBuilder builder = new StringBuilder();
        builder.append("[");
        for (int i = 0; i < registros.size(); i++) {
            RegistroRanking r = registros.get(i);
            builder.append("{\"name\":\"")
                    .append(r.nome().replace("\"", "\\\""))
                    .append("\",\"score\":")
                    .append(r.pontuacao())
                    .append(",\"dificuldade\":\"")
                    .append(r.dificuldade().name())
                    .append("\",\"passageirosColetados\":")
                    .append(r.passageirosResgatados())
                    .append(",\"dataHora\":\"")
                    .append(r.dataHora())
                    .append("\",\"tempoJogo\":")
                    .append(r.tempoSegundos())
                    .append("}");
            if (i < registros.size() - 1) {
                builder.append(",");
            }
        }
        builder.append("]");
        return builder.toString();
    }

    static List<RegistroRanking> lerJson(String json) {
        List<RegistroRanking> registros = new ArrayList<>();
        if (json.isEmpty() || json.equals("[]")) {
            return registros;
        }
        json = json.trim();
        if (json.startsWith("[")) {
            json = json.substring(1);
        }
        if (json.endsWith("]")) {
            json = json.substring(0, json.length() - 1);
        }

        int index = 0;
        while (index < json.length()) {
            int start = json.indexOf('{', index);
            if (start < 0) break;
            int end = json.indexOf('}', start);
            if (end < 0) break;
            String object = json.substring(start + 1, end);
            String name = null;
            Integer score = null;
            Dificuldade dificuldade = Dificuldade.MEDIO;
            int passageirosColetados = 0;
            String dataHora = "";
            long tempoJogo = 0;

            for (String part : object.split(",")) {
                String[] pair = part.split(":", 2);
                if (pair.length != 2) continue;
                String key = pair[0].trim().replaceAll("\"", "");
                String value = pair[1].trim();

                if (key.equals("name")) {
                    if (value.startsWith("\"") && value.endsWith("\"")) {
                        name = value.substring(1, value.length() - 1).replace("\\\"", "\"");
                    }
                } else if (key.equals("score")) {
                    try {
                        score = Integer.parseInt(value);
                    } catch (NumberFormatException ignored) { }
                } else if (key.equals("dificuldade")) {
                    if (value.startsWith("\"") && value.endsWith("\"")) {
                        dificuldade = Dificuldade.deString(value.substring(1, value.length() - 1));
                    }
                } else if (key.equals("passageirosColetados")) {
                    try {
                        passageirosColetados = Integer.parseInt(value);
                    } catch (NumberFormatException ignored) { }
                } else if (key.equals("dataHora")) {
                    if (value.startsWith("\"") && value.endsWith("\"")) {
                        dataHora = value.substring(1, value.length() - 1);
                    }
                } else if (key.equals("tempoJogo")) {
                    try {
                        tempoJogo = Long.parseLong(value);
                    } catch (NumberFormatException ignored) { }
                }
            }
            if (name != null && score != null) {
                registros.add(new RegistroRanking(name, score, dificuldade, passageirosColetados, dataHora, tempoJogo));
            }
            index = end + 1;
        }
        return registros;
    }
}
