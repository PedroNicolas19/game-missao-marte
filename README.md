# Missão Marte Unifor — Projeto Integrador

Projeto final da disciplina **Projeto de Arquitetura de Sistemas** (Unifor).

A equipe evolui o jogo **Missão Marte Unifor** em fases: começa com uma aplicação Java de console e termina com um jogo gráfico multiplataforma (desktop e Android, com libGDX), um servidor de ranking (Spring Boot + API REST) e uma melhoria definida pela equipe.

## Equipe

| Integrante | GitHub |
| --- | --- |
| Levi Costa Queiroz | [@levicostaq](https://github.com/levicostaq) |
| Pedro Nicolas | [@PedroNicolas19](https://github.com/PedroNicolas19) |
| Leonardo Norões | [@leomilfont](https://github.com/leomilfont) |

## Fases

Cada fase fica numa pasta própria em `fases/`. Uma fase nova começa como cópia da anterior, e **as fases anteriores não são alteradas depois de concluídas**.

| Fase | Pasta | Conteúdo | Situação |
| --- | --- | --- | --- |
| 0 | `fases/fase-0-inicial` | Projeto inicial em modo texto (versão base) | ⏳ Pendente |
| 1 | `fases/fase-1-refatoracao` | Refatoração em camadas (domínio, aplicação, console, infraestrutura) | ⏳ Pendente |
| 2 | `fases/fase-2-gradle` | Build com Gradle Wrapper e testes com JUnit 5 | ⏳ Pendente |
| 3 | `fases/fase-3-libgdx` | Introdução ao libGDX (módulos `core` e desktop) | ⏳ Pendente |
| 4 | `fases/fase-4-interface-grafica` | Interface gráfica com sprites, HUD, animações e áudio | ⏳ Pendente |
| 5 | `fases/fase-5-servidor-ranking` | Servidor Spring Boot com ranking global e integração com o cliente | ⏳ Pendente |
| 6 | `fases/fase-6-android` | Módulo Android e controles por toque | ⏳ Pendente |
| 7 | `fases/fase-7-evolucao-livre` | Melhoria escolhida pela equipe | ⏳ Pendente |

## Requisitos de ambiente

- **JDK 17** no `PATH` e em `JAVA_HOME`. Confira com `java -version`.
- Git.
- A partir da Fase 2, o Gradle **não** precisa estar instalado: usamos o Gradle Wrapper (`./gradlew`).
- A partir da Fase 3: placa de vídeo com OpenGL 2.0 ou superior.
- Fase 6: Android SDK para gerar o APK.

## Como executar

### Fase 0 — projeto inicial (console)

Linux/macOS:

```bash
cd fases/fase-0-inicial
javac -encoding UTF-8 -d out $(find src -name '*.java')
java -cp out Main
```

Windows (PowerShell):

```powershell
cd fases\fase-0-inicial
$files = Get-ChildItem -Recurse src -Filter *.java | ForEach-Object FullName
javac -encoding UTF-8 -d out $files
java -cp out Main
```

> As instruções das próximas fases serão adicionadas aqui conforme cada fase for concluída.

## Organização do trabalho

- **Quadro Kanban (Trello):** _<link do quadro>_
- Colunas: **Backlog** → **Em andamento** → **Em revisão** → **Concluído**.
- Cada tarefa tem um responsável, a sprint e critérios de aceite.
- Fluxo de cada tarefa:
  1. Criar uma branch a partir da `main` (ex.: `fase1/dominio`).
  2. Fazer commits pequenos e descritivos.
  3. Abrir um Pull Request.
  4. **Outro integrante revisa.**
  5. Fazer o merge e mover o cartão para **Concluído**.
- Padrão das mensagens de commit: `tipo(escopo): descrição`, com tipo `feat`, `fix`, `refactor`, `test`, `docs`, `build` ou `chore`.

## Segurança

Senhas, tokens, chaves e outros segredos **nunca** são versionados. Configurações sensíveis (como o segredo do token do servidor) vêm de variáveis de ambiente.

## Créditos

A **Fase 0** usa o projeto inicial disponibilizado pelo professor no repositório [T200-ProjArqSist](https://github.com/marcelobezerra-dotcom/T200-ProjArqSist/tree/main/missaoMarteUnifor/projetoExtensaoFinal) (pasta `exercicio10/fases/fase-0-oopuro`). Essa pasta é mantida como ponto de partida e não é alterada. As fases seguintes são desenvolvidas pela equipe.
