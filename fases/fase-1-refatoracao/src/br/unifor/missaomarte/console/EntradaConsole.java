package br.unifor.missaomarte.console;

import java.util.Optional;
import java.util.Scanner;

/** Leitura do teclado. Isola o Scanner do resto da interface. */
class EntradaConsole {

    private final Scanner scanner;

    EntradaConsole(Scanner scanner) {
        this.scanner = scanner;
    }

    /**
     * Mostra o prompt e lê uma linha sem espaços nas pontas.
     * Devolve vazio quando a entrada termina (Ctrl+D / fim do arquivo).
     */
    Optional<String> ler(String prompt) {
        if (prompt != null && !prompt.isEmpty()) {
            System.out.print(prompt);
        }
        if (scanner.hasNextLine()) {
            return Optional.of(scanner.nextLine().trim());
        }
        return Optional.empty();
    }

    /** Como {@link #ler(String)}, mas usa o valor padrão quando a entrada termina. */
    String lerLinha(String prompt, String padrao) {
        return ler(prompt).orElse(padrao);
    }
}
