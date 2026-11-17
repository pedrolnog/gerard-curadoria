# Matriz de rastreabilidade

Esta matriz relaciona cada campo curado à classe que o detém, ao pacote e ao conhecimento que ela protege. A última coluna indica o caso da seção 11 do enunciado ou a classe de teste que comprova a responsabilidade.

| Campo curado | Classe responsável | Pacote | Conhecimento que protege | Teste que comprova |
|---|---|---|---|---|
| valor numérico | `Numero` | `dominio.quantitativo` | operações aritméticas, sinal | `NumeroTest` |
| identidade do que é contado | `ObjetoContado` | `dominio.quantitativo` | família e características | `ObjetoContadoTest` |
| agrupamento conceitual | `FamiliaObjeto` | `dominio.quantitativo` | totalização de variações | `FamiliaObjetoTest` |
| cor, material, coleção | `CaracteristicaObjeto` | `dominio.quantitativo` | propriedade sem criar subclasse | `CaracteristicaObjetoTest` |
| natureza da quantidade | `GrandezaQuantitativa` | `dominio.quantitativo` | quais unidades aceita | `UnidadeMedidaEGrandezaTest` |
| unidade de medida | `UnidadeMedida` | `dominio.quantitativo` | vínculo com a grandeza, singular/plural | `UnidadeMedidaEGrandezaTest` |
| quantidade completa | `Quantidade` | `dominio.quantitativo` | recusa de operações incompatíveis | `QuantidadeTest` |
| singular, plural, idioma | `RealizacaoTextualObjeto` | `dominio.quantitativo` | gramática fora do objeto matemático | `RealizacaoTextualObjetoTest` |
| chave de desenho | `ReferenciaVisual` | `dominio.quantitativo` | indireção para o renderizador | `ObjetoContadoTest` |
| operação aritmética inválida | `QuantidadeIncompativelException`, `RetiradaImpossivelException` | `dominio.excecao` | interrupção de operação que viola o domínio | `QuantidadeTest` |
| posse de um participante | `Inventario` | `dominio.narrativo` | retirada impossível, total por família | `InventarioTest` |
| nome de pessoa ou entidade | `ParticipanteNarrativo` | `dominio.narrativo` | identidade estável, sem papel fixo | `ParticipanteNarrativoTest` |
| posse situada no tempo | `EstadoDePosse` | `dominio.narrativo` | fotografia de um momento | `EstadoDePosseTest` |
| significado do verbo | `AcaoNarrativa` | `dominio.narrativo` | sentido, destinatário, regência | `AcaoNarrativaTest` |
| sentido da transformação | `SentidoTransformacao` | `dominio.narrativo` | acréscimo ou retirada sem sinal negativo | `SentidoTransformacaoTest` |
| ordem e rótulo temporal | `MarcadorTemporal` | `dominio.narrativo` | sequência curada, não posicional | `MarcadorTemporalTest` |
| quem fez o quê e quando | `TransformacaoNarrativa` | `dominio.narrativo` | referências explícitas de papel | `TransformacaoNarrativaTest` |
| local, situação, momento | `ContextoNarrativo` | `dominio.narrativo` | ambientação sem inferência | `ContextoNarrativoTest` |
| função de uma quantidade | `PapelQuantitativo` | `dominio.estrutural` | vocabulário fechado de posições | `IncognitaQuantitativaTest` |
| papel originalmente desconhecido | `IncognitaQuantitativa` | `dominio.estrutural` | designação que sobrevive à resolução | `IncognitaQuantitativaTest` |
| coerência entre vários papéis | `RelacaoEstrutural` e subclasses | `dominio.estrutural` | equação do arranjo, regra comum de medidas | `RelacaoEstruturalPolimorfismoTest`, caso 14 |
| ocorrências detectadas | `ResultadoValidacao`, `Diagnostico` | `dominio.validacao` | diagnósticos estruturados, sem correção | `ResultadoValidacaoTest` |
| coerência da história | `SituacaoProblema` | `dominio.situacao` | ordem, catálogo, percurso | `SituacaoProblemaTest` |
| estágio de revisão | `StatusCuradoria` | `dominio.situacao` | promoção só por ato humano | `StatusCuradoriaTest` |
| escopo da situação | `EscopoDaSituacao` | `dominio.situacao` | situação fora do escopo aditivo não é forçada ao modelo | `AmbiguidadeEEscopoTest` |
| ambiguidade do enunciado | `AmbiguidadeDeclarada` | `dominio.situacao` | sem associação automática | caso 18 |
| sequência independente de mídia | `SequenciaNarrativa` | `dominio.situacao` | estados, eventos e pergunta | `SequenciaNarrativaTest` |
| leitura do JSON curado | `RepositorioSituacaoJson`, `MapeadorSituacao` | `persistencia` | leitura sem recálculo | `RepositorioSituacaoJsonTest` |
| divisão em quadros e balões | `GeradorRoteiroQuadrinhos` | `representacao.quadrinhos` | linguagem dos quadrinhos | `GeradorRoteiroQuadrinhosTest` |
| formato do roteiro | `RenderizadorTextual`, `RenderizadorHtml` | `representacao.quadrinhos` | formato de saída intercambiável | caso 12 |
| rascunho de situação na tela | `RascunhoDeSituacao` | `interfacegrafica` | lógica da tela testável sem janela | `RascunhoDeSituacaoTest` |
| dependências entre camadas | todo o domínio | `dominio` | domínio sem `javafx`, `swing`, `awt`, `gson` | `ArquiteturaTest` |

> Nota: os nomes de classes de teste não listados na seção 11 seguem a convenção `<Classe>Test`. Os 18 casos da seção 11 estão em `CasosDeTesteMinimosTest`.
