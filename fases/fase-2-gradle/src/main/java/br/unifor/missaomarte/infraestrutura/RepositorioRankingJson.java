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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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

    // ------------------------------------------------------------- escrita

    static String escreverJson(List<RegistroRanking> registros) {
        StringBuilder json = new StringBuilder("[");
        for (int i = 0; i < registros.size(); i++) {
            RegistroRanking r = registros.get(i);
            if (i > 0) json.append(",");
            json.append("{\"name\":").append(texto(r.nome()))
                    .append(",\"score\":").append(r.pontuacao())
                    .append(",\"dificuldade\":").append(texto(r.dificuldade().name()))
                    .append(",\"passageirosColetados\":").append(r.passageirosResgatados())
                    .append(",\"dataHora\":").append(texto(r.dataHora()))
                    .append(",\"tempoJogo\":").append(r.tempoSegundos())
                    .append("}");
        }
        return json.append("]").toString();
    }

    /** Texto JSON entre aspas, escapando aspas, barras e caracteres de controle. */
    private static String texto(String valor) {
        StringBuilder sb = new StringBuilder("\"");
        for (char c : valor.toCharArray()) {
            switch (c) {
                case '"': sb.append("\\\""); break;
                case '\\': sb.append("\\\\"); break;
                case '\n': sb.append("\\n"); break;
                case '\r': sb.append("\\r"); break;
                case '\t': sb.append("\\t"); break;
                default:
                    if (c < 0x20) sb.append(String.format("\\u%04x", (int) c));
                    else sb.append(c);
            }
        }
        return sb.append('"').toString();
    }

    // -------------------------------------------------------------- leitura

    /**
     * Lê uma lista de objetos simples (valores texto ou número). Registros sem
     * nome ou pontuação são ignorados; um arquivo malformado vira ranking vazio.
     */
    static List<RegistroRanking> lerJson(String json) {
        List<RegistroRanking> registros = new ArrayList<>();
        if (json.isBlank()) {
            return registros;
        }
        try {
            for (Map<String, String> campos : new LeitorJson(json).lerListaDeObjetos()) {
                paraRegistro(campos).ifPresent(registros::add);
            }
        } catch (IllegalArgumentException e) {
            return new ArrayList<>();
        }
        return registros;
    }

    private static Optional<RegistroRanking> paraRegistro(Map<String, String> campos) {
        String nome = campos.get("name");
        Long pontuacao = numero(campos.get("score"));
        if (nome == null || pontuacao == null) {
            return Optional.empty();
        }
        Long passageiros = numero(campos.get("passageirosColetados"));
        Long tempo = numero(campos.get("tempoJogo"));
        return Optional.of(new RegistroRanking(
                nome,
                pontuacao.intValue(),
                Dificuldade.deString(campos.get("dificuldade")),
                passageiros == null ? 0 : passageiros.intValue(),
                campos.getOrDefault("dataHora", ""),
                tempo == null ? 0 : tempo));
    }

    private static Long numero(String valor) {
        if (valor == null) return null;
        try {
            return Long.parseLong(valor);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /** Leitor mínimo de JSON para o formato do ranking: [ {"chave": "texto" | número, ...}, ... ]. */
    private static final class LeitorJson {

        private final String json;
        private int pos;

        LeitorJson(String json) {
            this.json = json;
        }

        List<Map<String, String>> lerListaDeObjetos() {
            List<Map<String, String>> objetos = new ArrayList<>();
            esperar('[');
            if (!consumirSe(']')) {
                do {
                    objetos.add(lerObjeto());
                } while (consumirSe(','));
                esperar(']');
            }
            pularEspacos();
            if (pos != json.length()) throw erro("conteúdo depois do fim da lista");
            return objetos;
        }

        private Map<String, String> lerObjeto() {
            Map<String, String> campos = new HashMap<>();
            esperar('{');
            if (!consumirSe('}')) {
                do {
                    String chave = lerTexto();
                    esperar(':');
                    pularEspacos();
                    String valor = atual() == '"' ? lerTexto() : lerLiteral();
                    campos.put(chave, valor);
                } while (consumirSe(','));
                esperar('}');
            }
            return campos;
        }

        private String lerTexto() {
            esperar('"');
            StringBuilder sb = new StringBuilder();
            while (true) {
                char c = proximo();
                if (c == '"') return sb.toString();
                if (c != '\\') {
                    sb.append(c);
                    continue;
                }
                char esc = proximo();
                switch (esc) {
                    case '"': case '\\': case '/': sb.append(esc); break;
                    case 'n': sb.append('\n'); break;
                    case 'r': sb.append('\r'); break;
                    case 't': sb.append('\t'); break;
                    case 'b': sb.append('\b'); break;
                    case 'f': sb.append('\f'); break;
                    case 'u':
                        if (pos + 4 > json.length()) throw erro("escape \\u incompleto");
                        try {
                            sb.append((char) Integer.parseInt(json.substring(pos, pos + 4), 16));
                        } catch (NumberFormatException e) {
                            throw erro("escape \\u inválido");
                        }
                        pos += 4;
                        break;
                    default: throw erro("escape inválido \\" + esc);
                }
            }
        }

        /** Número, true, false ou null: tudo até a próxima vírgula ou fim de objeto. */
        private String lerLiteral() {
            int inicio = pos;
            while (pos < json.length() && ",}] \t\r\n".indexOf(json.charAt(pos)) < 0) pos++;
            if (pos == inicio) throw erro("valor ausente");
            return json.substring(inicio, pos);
        }

        private void esperar(char c) {
            pularEspacos();
            if (proximo() != c) throw erro("esperado '" + c + "'");
        }

        private boolean consumirSe(char c) {
            pularEspacos();
            if (pos < json.length() && json.charAt(pos) == c) {
                pos++;
                return true;
            }
            return false;
        }

        private char atual() {
            if (pos >= json.length()) throw erro("fim inesperado");
            return json.charAt(pos);
        }

        private char proximo() {
            char c = atual();
            pos++;
            return c;
        }

        private void pularEspacos() {
            while (pos < json.length() && Character.isWhitespace(json.charAt(pos))) pos++;
        }

        private IllegalArgumentException erro(String mensagem) {
            return new IllegalArgumentException("JSON inválido na posição " + pos + ": " + mensagem);
        }
    }
}
