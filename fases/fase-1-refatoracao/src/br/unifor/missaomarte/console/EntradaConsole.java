package br.unifor.missaomarte.console;

import java.util.Scanner;

/** Leitura do teclado. Isola o Scanner do resto da interface. */
class EntradaConsole {

    private final Scanner scanner;

    EntradaConsole(Scanner scanner) {
        this.scanner = scanner;
    }

    /** Mostra o prompt e lê uma linha sem espaços nas pontas; no fim da entrada, devolve o padrão. */
    String lerLinha(String prompt, String padrao) {
        if (prompt != null && !prompt.isEmpty()) {
            System.out.print(prompt);
        }
        if (scanner.hasNextLine()) {
            return scanner.nextLine().trim();
        }
        return padrao;
    }

    boolean temProximaLinha() {
        return scanner.hasNextLine();
    }

    /** Consome uma linha (por exemplo, o Enter de "Pressione Enter"). */
    void descartarLinha() {
        scanner.nextLine();
    }
}
