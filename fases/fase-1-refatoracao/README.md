# Fase 1 — Refatoração em camadas

O mesmo jogo da Fase 0, reorganizado em camadas: domínio, aplicação, infraestrutura e console. As regras foram preservadas, e quatro bugs da Fase 0 foram corrigidos. As decisões, os diagramas antes/depois e as regras de referência estão em [docs/arquitetura.md](docs/arquitetura.md).

## Estrutura

```text
fase-1-refatoracao/
├── docs/arquitetura.md
├── src/br/unifor/missaomarte/
│   ├── dominio/          regras do jogo e do ranking (sem console, sem arquivos)
│   ├── aplicacao/        partida, montagem da missão e serviço de ranking
│   ├── infraestrutura/   ranking.json
│   └── console/          menu, telas, desenho do mapa e Main
└── test/br/unifor/missaomarte/TestesRegras.java
```

## Executar o jogo

Requer **JDK 17**. Execute a partir de `fases/fase-1-refatoracao`.

Linux/macOS:

```bash
javac -encoding UTF-8 -d out $(find src -name '*.java')
java -cp out br.unifor.missaomarte.console.Main
```

Windows (PowerShell):

```powershell
$files = Get-ChildItem -Recurse src -Filter *.java | ForEach-Object FullName
javac -encoding UTF-8 -d out $files
java -cp out br.unifor.missaomarte.console.Main
```

No IntelliJ: abra a pasta `fases/fase-1-refatoracao`, marque `src` como *Sources Root* e `test` como *Test Sources Root*, use o JDK 17 e rode `br.unifor.missaomarte.console.Main`.

O arquivo `ranking.json` é criado na pasta de onde o jogo é executado.

## Controles

- `w`, `a`, `s`, `d`: mover a nave (custa 1 ponto).
- `c`: embarcar o passageiro da posição atual.
- `q`: abortar a missão.

## Executar os testes

Linux/macOS:

```bash
javac -encoding UTF-8 -d out $(find src test -name '*.java')
java -cp out br.unifor.missaomarte.TestesRegras
```

Windows (PowerShell):

```powershell
$files = Get-ChildItem -Recurse src, test -Filter *.java | ForEach-Object FullName
javac -encoding UTF-8 -d out $files
java -cp out br.unifor.missaomarte.TestesRegras
```

Resultado esperado: `25 aprovados, 0 com falha`. Se algum teste falhar, o comando termina com código 1.
