# Curador de Histórias Aditivas

Modelo orientado a objetos para curar situações-problema de estrutura aditiva, validá-las sem correção automática e representá-las como roteiros de quadrinhos.

## Baixar e executar (sem compilar)

1. Instale o Java 17 ou mais recente (`java -version` para conferir).
2. Na página de [Releases](https://github.com/pedrolnog/gerard-curadoria/releases), baixe o `.zip` do seu sistema (Linux, Windows ou macOS) e extraia.
3. Dentro da pasta extraída, rode:

```
java -jar curador-historias-aditivas.jar
```

A tela lê e grava as situações na pasta `dados/`, que vem junto no `.zip`. Rode o comando de dentro da pasta extraída para que ela seja encontrada.

## Compilar a partir do código

### Requisitos

- JDK 17+
- Maven 3.9+

### Comandos

```
mvn test            # 335 testes automatizados
mvn exec:java       # demonstração no console
mvn javafx:run      # interface gráfica
mvn package         # gera target/curador-historias-aditivas.jar (executável, com JavaFX embutido)
```

O `.jar` gerado traz o JavaFX do sistema em que foi compilado. Para gerar o de outro sistema, use `-Djavafx.platform=win`, `-Djavafx.platform=mac` ou `-Djavafx.platform=mac-aarch64`.

## Estrutura de pacotes

```
app                       demonstração de console e situações curadas
dominio
├── quantitativo          Numero, Quantidade, ObjetoContado, grandezas e unidades
├── excecao               hierarquia de ViolacaoDeDominioException
├── narrativo             participantes, inventários, estados, ações e transformações
├── estrutural            RelacaoEstrutural e suas subclasses, papéis e incógnita
├── validacao             Diagnostico e ResultadoValidacao
├── situacao              SituacaoProblema, curadoria, escopo e sequência narrativa
└── repositorio           interface genérica Repositorio<T, ID>
representacao.quadrinhos  quadros, balões e renderizadores de roteiro
persistencia              repositório JSON, mapeador e erros de leitura
└── dto                   DTOs (getters/setters) usados na serialização
interfacegrafica          tela JavaFX e lógica de rascunho
```

## Dependências e fronteiras

| Dependência | Onde é permitida |
|---|---|
| JUnit 5 | somente em `src/test` |
| Gson | somente em `persistencia` |
| JavaFX 21 | somente em `interfacegrafica` |

O sentido das dependências é `app` e `interfacegrafica` → `persistencia` e `representacao` → `dominio`. O teste `ArquiteturaTest` garante que o domínio não importa `javafx`, `swing`, `awt`, `gson` nem camadas externas.

## Situações curadas

| Identificador | Estrutura | Incógnita |
|---|---|---|
| `rosas-01` | composição de duas transformações | estado final |
| `figurinhas-lucas-01` | transformação com estado inicial desconhecido | estado inicial |
| `bilas-joao-01` | duas partidas | primeira transformação |

## Dados

- `dados/`: situações em JSON.
- `dados/roteiros/`: roteiros gerados em `txt` e `html`.

## Documentação

- [Relatório justificativo](docs/relatorio.md)
- [Diagrama de classes](docs/diagrama-classes.md)
- [Matriz de rastreabilidade](docs/matriz-rastreabilidade.md)
- [Cartões CRC](docs/crc/)

## Conceitos de POO em destaque

- `Numero`, `Quantidade`, `Inventario` → encapsulamento e imutabilidade
- `ViolacaoDeDominioException` → herança em hierarquia de exceções
- `SentidoTransformacao` → polimorfismo em enum (corpo por constante)
- `RelacaoEstrutural` → classe abstrata, Template Method e ligação dinâmica
- `RenderizadorDeRoteiro` → interface com método `default` e Strategy
- `Repositorio<T, ID>` → interface genérica e inversão de dependência
- `SituacaoProblema` → composição, agregação e Builder
- `ehCompativelCom(...)` → sobrecarga
- `CelulaDiagnostico`, `CelulaSituacao` → herança de `ListCell`
