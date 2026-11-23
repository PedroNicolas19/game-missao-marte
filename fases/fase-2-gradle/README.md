# Fase 2 — Migração para Gradle

O mesmo jogo da Fase 1, agora compilado, testado e executado pelo Gradle. **Nenhum arquivo do jogo foi alterado**: os 31 fontes da Fase 1 foram copiados para o layout padrão `src/main/java`, e a igualdade foi conferida por hash. O passo a passo, os conceitos de Gradle e as evidências estão em [docs/tutorial.md](docs/tutorial.md).

## Requisitos

- **JDK 17** (`java -version` e `JAVA_HOME`). O Gradle 8.7 não roda em JDKs muito novos (ex.: 25 ou 26).
- Não é preciso instalar o Gradle. O projeto traz o **Gradle Wrapper 8.7**, que baixa a distribuição na primeira execução e a guarda em `~/.gradle`.

## Comandos

Execute dentro de `fases/fase-2-gradle`.

Linux/macOS:

```bash
./gradlew --version            # deve mostrar Gradle 8.7 e JVM 17
./gradlew clean build          # compila, testa e empacota
./gradlew test                 # apenas os testes
./gradlew run --console=plain  # joga no terminal
./gradlew installDist          # gera build/install/missao-marte/bin/missao-marte
```

Windows (PowerShell): troque `./gradlew` por `.\gradlew.bat`.

O `ranking.json` é gravado na pasta de onde o jogo é executado. No `gradlew run`, essa pasta é `fases/fase-2-gradle`. O arquivo é ignorado pelo Git.

## Controles

- `w`, `a`, `s`, `d`: mover a nave (custa 1 ponto).
- `c`: embarcar o passageiro da posição atual.
- `q`: abortar a missão.

## Estrutura

```text
fase-2-gradle/
├── settings.gradle          nome do projeto
├── build.gradle             plugin application, Java 17, UTF-8, JUnit 5
├── gradlew, gradlew.bat     scripts do wrapper
├── gradle/wrapper/          jar e propriedades do wrapper (Gradle 8.7)
├── docs/tutorial.md
└── src/
    ├── main/java/br/unifor/missaomarte/    código do jogo (idêntico à Fase 1)
    │   ├── dominio/  aplicacao/  infraestrutura/  console/
    └── test/java/br/unifor/missaomarte/    36 testes JUnit 5
        ├── dominio/  aplicacao/  infraestrutura/
```

## Testes

`./gradlew test` executa 36 testes em 10 classes, e cada resultado aparece no console. O relatório HTML fica em `build/reports/tests/test/index.html`.
