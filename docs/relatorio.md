# Relatório justificativo: Curador de Histórias Aditivas

## 1. Visão geral e arquitetura em camadas

O sistema cura situações-problema de estrutura aditiva: cada situação é escrita por um humano como dado estruturado (participantes, objetos, quantidades, estados, transformações e incógnita), validada pelo domínio e só então representada, por exemplo como roteiro de quadrinhos. O sistema não interpreta enunciados em texto livre.

O projeto usa Java 17, Maven e JUnit 5. A arquitetura tem uma única direção de dependência: `app` e `interfacegrafica` dependem de `persistencia` e `representacao`, e estes dependem de `dominio`. O domínio é dividido em `quantitativo`, `excecao`, `narrativo`, `estrutural`, `validacao`, `situacao` e `repositorio`. Gson só aparece em `persistencia` e JavaFX 21 só em `interfacegrafica`. Essa fronteira é verificada pelo teste `ArquiteturaTest`, que impede o domínio de importar `javafx`, `swing`, `awt`, `gson` ou as camadas externas.

## 2. Modelagem do domínio e alocação de responsabilidades

Cada conhecimento fica no objeto que o possui (ver [matriz de rastreabilidade](matriz-rastreabilidade.md)):

- `dominio.quantitativo`: `Numero` e `Quantidade` fazem a aritmética. `GrandezaQuantitativa` e `UnidadeMedida` definem quais unidades cada grandeza aceita. `ObjetoContado` combina `FamiliaObjeto` e `CaracteristicaObjeto`. `RealizacaoTextualObjeto` e `ReferenciaVisual` guardam gramática e chave de desenho fora do objeto matemático.
- `dominio.narrativo`: `ParticipanteNarrativo`, `Inventario`, `EstadoDePosse`, `AcaoNarrativa`, `MarcadorTemporal`, `TransformacaoNarrativa` e `ContextoNarrativo`.
- `dominio.estrutural`: `PapelQuantitativo`, `IncognitaQuantitativa` e `RelacaoEstrutural` com suas subclasses.
- `dominio.situacao`: `SituacaoProblema` (raiz de agregação), `StatusCuradoria`, `EscopoDaSituacao`, `AmbiguidadeDeclarada`, `SequenciaNarrativa`.
- `dominio.validacao`: `Diagnostico`, `ResultadoValidacao`, `TipoDiagnostico`.

A retirada de uma quantidade inexistente é recusada por `Inventario`, e não pela interface. A compatibilidade entre grandeza e unidade é verificada por `GrandezaQuantitativa`, `UnidadeMedida` e `Quantidade`. A coerência entre os papéis de uma relação é verificada por `RelacaoEstrutural`. A tela apenas coleta e exibe dados.

## 3. Conceitos de POO aplicados

**Encapsulamento e invariantes.** Os atributos são todos privados e o acesso é por getters e setters. As entidades (`SituacaoProblema`, `ParticipanteNarrativo`, `ObjetoContado`, `AcaoNarrativa`, `TransformacaoNarrativa`, `ContextoNarrativo`, `CatalogoDaSituacao`, `IncognitaQuantitativa` etc.) podem ser alteradas depois de criadas, mas cada setter aplica a mesma validação do construtor: um valor inválido lança exceção e o objeto continua como estava. Regras que envolvem dois atributos também valem no setter (por exemplo, `setDestinatario` recusa o próprio ator). Coleções são alteradas por `adicionarX`/`removerX` e expostas só para leitura.

Alguns atributos ficam de propósito sem setter público: o `id` (é a identidade usada em `equals`/`hashCode`) e o status da curadoria, que só muda por `promoverParaValidada` e `recusar`, porque essas operações têm regras próprias. Qualquer alteração de conteúdo em uma situação já revisada a devolve para `CANDIDATA_NAO_CURADA`, já que o conteúdo mudou depois da revisão humana.

Os objetos de valor (`Numero`, `Quantidade`, `Inventario`, `EstadoDePosse`, `MarcadorTemporal`, `CaracteristicaObjeto`) continuam imutáveis, como `String` e `Integer`. Um `EstadoDePosse` é a fotografia de um momento da história: aplicar uma transformação gera o estado seguinte sem apagar o anterior, e é isso que permite mostrar o percurso de estados.

**Abstração.** `RelacaoEstrutural` (classe abstrata) define o contrato de uma relação aditiva. `Repositorio<T, ID>` e `RenderizadorDeRoteiro` abstraem persistência e representação.

**Herança.** `ViolacaoDeDominioException` (abstrata, estende `IllegalArgumentException`) é a raiz de `QuantidadeIncompativelException` e `RetiradaImpossivelException`. `ComposicaoDeTransformacoes`, `ComposicaoParteTodo` e `ComparacaoQuantitativa` estendem `RelacaoEstrutural`. Na interface gráfica, `CelulaDiagnostico` e `CelulaSituacao` estendem `ListCell`, e `CuradorApplication` estende `Application`.

**Polimorfismo e ligação dinâmica.** `RelacaoEstrutural.validar()` é `final` e implementa o Template Method: aplica a regra comum de medidas e chama o passo abstrato `validarRegrasDaCategoria`. `resolver(Papel)` é abstrato, e a subclasse executada é escolhida em tempo de execução. O enum `SentidoTransformacao` declara `aplicar(...)` abstrato, com um corpo por constante (`ACRESCIMO`, `RETIRADA`). `TransformacaoNarrativa` delega a ele em vez de usar `if`. `EpisodioNarrativo` é uma classe abstrata `sealed` com três subclasses (`EpisodioDeEstado`, `EpisodioDeEvento`, `EpisodioDePergunta`): cada uma guarda só o seu dado, e `descrever()` é resolvido por ligação dinâmica, sem campos nulos nem `switch` sobre o tipo.

**Interfaces e "herança múltipla".** Java não tem herança múltipla de classes. O projeto obtém composição de comportamentos por interfaces: `RenderizadorDeRoteiro` (com método `default`, implementado por `RenderizadorTextual` e `RenderizadorHtml`), `Repositorio<T, ID>` genérico (implementado por `RepositorioSituacaoJson`), `Comparable` em `Numero` e `MarcadorTemporal`, e enums que implementam comportamento.

**Composição e agregação.** `SituacaoProblema` compõe suas transformações e seus estados. `EstadoDePosse` compõe um `Inventario`. `Inventario` agrega quantidades de `ObjetoContado`, objetos que existem independentemente dele.

**Sobrecarga.** `ehCompativelCom(UnidadeMedida)` e `ehCompativelCom(GrandezaQuantitativa)` respondem à mesma pergunta para tipos de argumento diferentes.

**Construtores e ausência de destrutores.** Os invariantes são garantidos nos construtores, e o padrão Builder (`SituacaoProblema.construtor(...)`, `CatalogoDaSituacao`, construtores das relações estruturais) monta objetos com muitos campos. Java não tem destrutores: a imutabilidade e a coleta de lixo dispensam a liberação manual, e os recursos de arquivo são tratados com `try-with-resources` e `Files` no repositório.

## 4. Separação domínio / JSON / representação

O domínio define a interface `Repositorio<T, ID>` (inversão de dependência). `persistencia` a implementa em `RepositorioSituacaoJson`, que usa Gson, DTOs anêmicos com getters e setters (`persistencia.dto`) e `MapeadorSituacao`. `representacao.quadrinhos` transforma a `SequenciaNarrativa` em quadros, balões e roteiros (`GeradorRoteiroQuadrinhos`, `Quadro`, `Balao`, `DescritorCena`, `RoteiroQuadrinhos`). O domínio não conhece JSON nem quadrinhos, e a `SituacaoProblema` produz uma sequência sem desenhar nada.

## 5. Generalização para as três situações

As mesmas classes atendem às três situações curadas, montadas em `SituacoesCuradas` e gravadas em JSON em `dados/`:

| Situação | Estrutura | Incógnita |
|---|---|---|
| `rosas-01` | composição de duas transformações | estado final |
| `figurinhas-lucas-01` | transformação com estado inicial desconhecido | estado inicial |
| `bilas-joao-01` | duas partidas | primeira transformação |

Nenhuma subclasse foi criada para um enunciado específico. `FamiliaObjeto` e `CaracteristicaObjeto` são dados, e não tipos, o que evita uma subclasse por enunciado.

## 6. Validação sem correção automática e curadoria humana

Uma operação aritmética inválida interrompe a execução por exceção (`QuantidadeIncompativelException`, `RetiradaImpossivelException`). Problemas de curadoria viram `Diagnostico` dentro de `ResultadoValidacao`, e nunca alteram o dado curado. Se o estado final curado divergir do calculado, ambos os valores são preservados no diagnóstico.

`StatusCuradoria` nasce em `CANDIDATA_NAO_CURADA`. `promoverParaValidada(revisor)` recusa situações com erros ou ambiguidade pendente. Revisor e motivo são guardados separadamente, o que corrigiu um defeito de ida e volta na persistência. `EscopoDaSituacao` impede que uma situação rotulada fora do escopo aditivo seja forçada ao modelo. `AmbiguidadeDeclarada` marca `ENUNCIADO_AMBIGUO` sem associação automática (caso 18).

## 7. Testes

Há 314 testes automatizados, todos passando. `CasosDeTesteMinimosTest` contém os 18 casos da seção 11 do enunciado, com os mesmos nomes. `ArquiteturaTest` garante que o domínio não importa `javafx`, `swing`, `awt`, `gson` nem camadas externas. Há ainda testes por classe (por exemplo `QuantidadeTest`). A lógica da tela fica em `RascunhoDeSituacao`, o que a torna testável sem janela.

## 8. Respostas às perguntas da seção 13

1. `ObjetoContado` carrega família e características, e permite somar por família, comparar e gerar texto com plural correto. Uma `String` esconde essa estrutura e obriga a interpretar texto.
2. A cor é característica do domínio quando distingue objetos para a contagem (por exemplo, rosas de cores diferentes que a história trata separadamente). É escolha visual quando só afeta o desenho, e então fica em `ReferenciaVisual`.
3. Subclasses por enunciado multiplicariam tipos sem comportamento novo. Família e característica são dados, e assim o mesmo código serve a qualquer situação.
4. `GrandezaQuantitativa` e `UnidadeMedida` (via `ehCompativelCom`) e `Quantidade`, que recusa operações incompatíveis. `RelacaoEstrutural.validar()` aplica a regra de medidas entre os papéis.
5. Um valor vazio perde a informação assim que a situação é resolvida. `IncognitaQuantitativa` guarda o papel designado e preserva essa designação após a resolução.
6. Destinatário é função numa transformação, e não propriedade da pessoa. A mesma pessoa pode ser autora numa ação e destinatária em outra. A regência fica em `AcaoNarrativa`, e as referências de papel em `TransformacaoNarrativa`.
7. `Inventario`, que conhece o que o participante possui. Ele lança `RetiradaImpossivelException`, e a interface não repete a regra.
8. Recalcular na leitura apagaria a divergência que a validação deve apontar. `RepositorioSituacaoJson` lê o estado final curado como está escrito.
9. Gerar a sequência é conhecimento da história. Dividir em quadros e balões é linguagem de quadrinhos, que pertence a `GeradorRoteiroQuadrinhos`. Assim a mesma sequência serve a outras mídias.
10. Escreve-se uma nova subclasse de `RelacaoEstrutural` implementando `validarRegrasDaCategoria` e `resolver`. As classes existentes e o núcleo quantitativo não mudam.
11. A situação é rotulada fora do escopo por `EscopoDaSituacao` e não é forçada ao modelo aditivo. O sistema não a converte automaticamente.
12. Todo o domínio, `representacao.quadrinhos` e a persistência. Só `interfacegrafica` depende de JavaFX, e a lógica da tela está em `RascunhoDeSituacao`, independente da janela.

## 9. Limitações e decisões de escopo

- `ReferenciaVisual` e `ReferenciaVisualParticipante` ficam no domínio como **chaves simbólicas opacas** (texto como `"rosa_branca"`): não carregam imagem nem dependem de biblioteca gráfica. Quem decide o desenho é a camada de representação. Uma evolução possível seria um catálogo visual dentro de `representacao`.
- `RelacaoDto` reúne os campos das três subclasses de `RelacaoEstrutural`. Uma nova categoria exige uma subclasse no domínio **e** ajuste no mapeador da persistência. O núcleo do domínio não muda, mas a persistência sim. Uma alternativa seria uma hierarquia de DTOs (`sealed interface`) com um mapeamento por categoria.

- Não há extração automática de papéis a partir de texto. O interpretador automático de enunciados do protótipo foi descartado porque a seção 3.2 proíbe essa extração.
- `RelacaoEstrutural` não implementa as seis categorias completas, conforme a seção 3.2. As três subclasses existentes cobrem as três situações curadas.
- Situações multiplicativas e enunciados ambíguos não são convertidos em dados curados (seção 13.1).
- A tela JavaFX coleta e exibe. Participantes e objetos vêm de `ComboBox` do catálogo, e a ordem segue o campo de ordem, e não a posição.
- A promoção a situação validada depende sempre de um revisor humano.
