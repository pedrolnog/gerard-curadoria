# Diagrama de Classes - Gerard Curadoria

Este documento apresenta a arquitetura estática e a estrutura de classes do sistema **Gerard Curadoria**, subdividida em diagramas por camada lógica para facilitar a leitura e compreensão do modelo de domínio, representação em quadrinhos e persistência.

---

## 1. Camada Quantitativa e Exceções de Domínio (`dominio.quantitativo` e `dominio.excecao`)

Modela as grandezas, unidades, números inteiros com semântica de domínio, famílias de objetos e quantidades imutáveis, além da hierarquia de exceções específicas de regras quantitativas.

```mermaid
classDiagram
    direction TB

    class Numero {
        -int valor
        +de(int valor)$ Numero
        +getValor() int
        +somar(Numero outro) Numero
        +subtrair(Numero outro) Numero
        +diferencaAbsolutaPara(Numero outro) Numero
        +ehNegativo() boolean
        +ehZero() boolean
    }

    class GrandezaQuantitativa {
        <<enumeration>>
        -String nomeExibido
        +aceitaUnidade(UnidadeMedida unidade) boolean
        +ehCompativelCom(GrandezaQuantitativa outra) boolean
        +getUnidadePadrao() UnidadeMedida
    }

    class UnidadeMedida {
        <<enumeration>>
        -GrandezaQuantitativa grandeza
        -String singular
        -String plural
        +flexionar(Numero numero) String
        +ehCompativelCom(UnidadeMedida outra) boolean
    }

    class FamiliaObjeto {
        -String id
        -String nomeConceitual
        +getId() String
        +getNomeConceitual() String
    }

    class CaracteristicaObjeto {
        -String dimensao
        -String valor
        +de(String dimensao, String valor)$ CaracteristicaObjeto
        +getDimensao() String
        +getValor() String
    }

    class RealizacaoTextualObjeto {
        -String singular
        -String plural
        -String idioma
        +emPortugues(String singular, String plural)$ RealizacaoTextualObjeto
        +flexionar(Numero numero) String
        +descrever(Numero numero) String
    }

    class ReferenciaVisual {
        -String chave
        +de(String chave)$ ReferenciaVisual
        +naoDefinida()$ ReferenciaVisual
        +estaDefinida() boolean
    }

    class ObjetoContado {
        -String id
        -FamiliaObjeto familia
        -Set~CaracteristicaObjeto~ caracteristicas
        -RealizacaoTextualObjeto realizacaoTextual
        -ReferenciaVisual referenciaVisual
        +quantificar(Numero n, GrandezaQuantitativa g, UnidadeMedida u) Quantidade
        +contar(int quantosItens) Quantidade
        +pertenceAFamilia(FamiliaObjeto outra) boolean
    }

    class Quantidade {
        -Numero numero
        -ObjetoContado objeto
        -GrandezaQuantitativa grandeza
        -UnidadeMedida unidade
        +somar(Quantidade outra) Quantidade
        +subtrair(Quantidade outra) Quantidade
        +ehCompativelCom(Quantidade outra) boolean
        +comportaRetiradaDe(Quantidade outra) boolean
    }

    class ViolacaoDeDominioException {
        <<abstract>>
    }

    class QuantidadeIncompativelException {
    }

    class RetiradaImpossivelException {
    }

    ViolacaoDeDominioException <|-- QuantidadeIncompativelException
    ViolacaoDeDominioException <|-- RetiradaImpossivelException

    Quantidade "1" o-- "1" Numero
    Quantidade "1" o-- "1" ObjetoContado
    Quantidade "1" o-- "1" GrandezaQuantitativa
    Quantidade "1" o-- "1" UnidadeMedida

    ObjetoContado "1" o-- "1" FamiliaObjeto
    ObjetoContado "1" *-- "*" CaracteristicaObjeto
    ObjetoContado "1" *-- "1" RealizacaoTextualObjeto
    ObjetoContado "1" *-- "1" ReferenciaVisual
    UnidadeMedida --> GrandezaQuantitativa
```

---

## 2. Camada Narrativa (`dominio.narrativo`)

Modela os agentes da história, ações com sentidos de transformação, inventários aditivos e a evolução dos estados de posse ao longo do tempo narrativo.

```mermaid
classDiagram
    direction TB

    class ParticipanteNarrativo {
        -String id
        -String nomeExibido
        -ReferenciaVisualParticipante referenciaVisual
        +getId() String
        +getNomeExibido() String
        +getReferenciaVisual() ReferenciaVisualParticipante
    }

    class ReferenciaVisualParticipante {
        -String chave
        +de(String chave)$ ReferenciaVisualParticipante
        +naoDefinida()$ ReferenciaVisualParticipante
        +estaDefinida() boolean
    }

    class SentidoTransformacao {
        <<enumeration>>
        -String nomeExibido
        -int fator
        +aplicar(EstadoDePosse estado, Quantidade qtd, MarcadorTemporal m) EstadoDePosse
        +podeSerAplicado(EstadoDePosse estado, Quantidade qtd) boolean
        +ehAcrescimo() boolean
        +ehRetirada() boolean
    }

    class AcaoNarrativa {
        -String id
        -String verbo
        -String participio
        -SentidoTransformacao sentido
        -boolean exigeDestinatario
        -String preposicaoDoDestinatario
        +transferencia(String id, String verbo, String part, SentidoTransformacao s)$ AcaoNarrativa
        +semDestinatario(String id, String verbo, String part, SentidoTransformacao s)$ AcaoNarrativa
        +ehTransferencia() boolean
    }

    class MarcadorTemporal {
        -int ordem
        -String rotulo
        +daOrdem(int ordem)$ MarcadorTemporal
        +de(int ordem, String rotulo)$ MarcadorTemporal
        +ehAnteriorA(MarcadorTemporal outro) boolean
    }

    class ContextoNarrativo {
        -String local
        -String situacao
        -String momento
        +naoInformado()$ ContextoNarrativo
        +estaVazio() boolean
        +descrever() String
    }

    class Inventario {
        -Map~String, Quantidade~ itens
        +vazio()$ Inventario
        +com(Quantidade... quantidades)$ Inventario
        +adicionar(Quantidade quantidade) Inventario
        +retirar(Quantidade quantidade) Inventario
        +comporta(Quantidade quantidade) boolean
        +totalGeral() Numero
    }

    class EstadoDePosse {
        -ParticipanteNarrativo possuidor
        -Inventario inventario
        -MarcadorTemporal momento
        +acrescentar(Quantidade qtd, MarcadorTemporal m) EstadoDePosse
        +retirar(Quantidade qtd, MarcadorTemporal m) EstadoDePosse
        +comporta(Quantidade quantidade) boolean
        +totalGeral() Numero
    }

    class TransformacaoNarrativa {
        -ParticipanteNarrativo ator
        -AcaoNarrativa acao
        -ParticipanteNarrativo destinatario
        -Quantidade quantidade
        -MarcadorTemporal marcador
        +aplicarSobre(EstadoDePosse estado) EstadoDePosse
        +aplicarSobreDestinatario(EstadoDePosse estadoDestinatario) EstadoDePosse
        +podeSerAplicadaSobre(EstadoDePosse estado) boolean
    }

    EstadoDePosse "1" *-- "1" Inventario
    Inventario "1" o-- "*" Quantidade
    EstadoDePosse "1" o-- "1" ParticipanteNarrativo
    EstadoDePosse "1" o-- "1" MarcadorTemporal

    ParticipanteNarrativo "1" *-- "1" ReferenciaVisualParticipante
    AcaoNarrativa "1" o-- "1" SentidoTransformacao

    TransformacaoNarrativa "1" o-- "1" ParticipanteNarrativo : ator
    TransformacaoNarrativa "1" o-- "0..1" ParticipanteNarrativo : destinatario
    TransformacaoNarrativa "1" o-- "1" AcaoNarrativa
    TransformacaoNarrativa "1" o-- "1" Quantidade
    TransformacaoNarrativa "1" o-- "1" MarcadorTemporal
```

---

## 3. Camada Estrutural e Validação (`dominio.estrutural` e `dominio.validacao`)

Modela as estruturas do raciocínio aditivo (transformações, parte-todo, comparação quantitativa) fundamentadas na categorização de Gerard Vergnaud, além do motor de validação e diagnóstico semântico.

```mermaid
classDiagram
    direction TB

    class PapelQuantitativo {
        <<enumeration>>
        -String nomeExibido
        +ehEstado() boolean
        +ehTransformacao() boolean
        +ehComposicao() boolean
        +ehComparacao() boolean
    }

    class RelacaoEstrutural {
        <<abstract>>
        -String id
        -Map~PapelQuantitativo, Quantidade~ papeis
        +getId() String
        +getCategoria()* String
        +validar() ResultadoValidacao
        +validarRegrasDaCategoria(ResultadoValidacao parcial)* ResultadoValidacao
        +resolver(PapelQuantitativo papelDesconhecido)* Numero
        +quantidadeDoPapel(PapelQuantitativo papel) Quantidade
    }

    class ComposicaoDeTransformacoes {
        -SentidoTransformacao sentidoPrimeira
        -SentidoTransformacao sentidoSegunda
        -SentidoTransformacao sentidoResultante
        +getCategoria() String
        +validarRegrasDaCategoria(ResultadoValidacao resultado) ResultadoValidacao
        +resolver(PapelQuantitativo papelDesconhecido) Numero
    }

    class ComposicaoParteTodo {
        -List~Quantidade~ partes
        -Quantidade todo
        +getCategoria() String
        +validarRegrasDaCategoria(ResultadoValidacao resultado) ResultadoValidacao
        +resolverParteAusente(int quantidadeDePartesEsperadas) Numero
        +resolverTodo() Numero
    }

    class ComparacaoQuantitativa {
        -ParticipanteNarrativo participanteReferente
        -ParticipanteNarrativo participanteReferido
        -Direcao direcao
        +getCategoria() String
        +validarRegrasDaCategoria(ResultadoValidacao resultado) ResultadoValidacao
        +resolver(PapelQuantitativo papelDesconhecido) Numero
    }

    class IncognitaQuantitativa {
        -PapelQuantitativo papelOriginal
        -String descricaoDaPergunta
        -Quantidade valorResolvido
        +designar(PapelQuantitativo papel, String descricao)$ IncognitaQuantitativa
        +estaResolvida() boolean
        +resolver(Quantidade valor) IncognitaQuantitativa
    }

    class TipoDiagnostico {
        <<enumeration>>
        -String nomeExibido
        -Severidade severidade
        +ehErro() boolean
        +ehAviso() boolean
    }

    class Diagnostico {
        -TipoDiagnostico tipo
        -String elementoEnvolvido
        -String detalhe
        +de(TipoDiagnostico tipo, String elemento, String detalhe)$ Diagnostico
        +ehErro() boolean
        +ehAviso() boolean
    }

    class ResultadoValidacao {
        -List~Diagnostico~ diagnosticos
        +semOcorrencias()$ ResultadoValidacao
        +acrescentar(Diagnostico diagnostico) ResultadoValidacao
        +combinarCom(ResultadoValidacao outro) ResultadoValidacao
        +ehValido() boolean
        +getErros() List~Diagnostico~
        +getAvisos() List~Diagnostico~
    }

    RelacaoEstrutural <|-- ComposicaoDeTransformacoes
    RelacaoEstrutural <|-- ComposicaoParteTodo
    RelacaoEstrutural <|-- ComparacaoQuantitativa

    RelacaoEstrutural "1" o-- "*" PapelQuantitativo
    RelacaoEstrutural ..> ResultadoValidacao : produz
    IncognitaQuantitativa "1" o-- "1" PapelQuantitativo

    ResultadoValidacao "1" *-- "*" Diagnostico
    Diagnostico "1" o-- "1" TipoDiagnostico
```

---

## 4. Camada de Situação e Repositório (`dominio.situacao` e `dominio.repositorio`)

Reúne o agregado principal `SituacaoProblema`, que orquestra catálogo, contexto, estados de posse, transformações narrativas, relação estrutural e a projeção temporal da sequência de episódios.

```mermaid
classDiagram
    direction TB

    class Repositorio~T, ID~ {
        <<interface>>
        +salvar(T entidade) void
        +buscarPorId(ID id) Optional~T~
        +listarTodos() List~T~
        +remover(ID id) boolean
    }

    class StatusCuradoria {
        <<enumeration>>
        -String nomeExibido
        +aguardaRevisao() boolean
        +foiRevisada() boolean
    }

    class EscopoDaSituacao {
        <<enumeration>>
        -String nomeExibido
        +permiteModeloAditivo() boolean
    }

    class TipoEpisodio {
        <<enumeration>>
        -String rotulo
        +getRotulo() String
    }

    class AmbiguidadeDeclarada {
        -String trecho
        -String motivo
        +getTrecho() String
        +getMotivo() String
    }

    class CatalogoDaSituacao {
        -Map~String, ParticipanteNarrativo~ participantes
        -Map~String, ObjetoContado~ objetos
        +participantePorId(String id) ParticipanteNarrativo
        +objetoPorId(String id) ObjetoContado
        +contem(ParticipanteNarrativo p) boolean
        +contem(ObjetoContado o) boolean
    }

    class EpisodioNarrativo {
        -TipoEpisodio tipo
        -MarcadorTemporal momento
        -EstadoDePosse estado
        -TransformacaoNarrativa transformacao
        -IncognitaQuantitativa incognita
        +deEstado(EstadoDePosse estado)$ EpisodioNarrativo
        +deEvento(TransformacaoNarrativa transformacao)$ EpisodioNarrativo
        +dePergunta(IncognitaQuantitativa incognita, MarcadorTemporal m)$ EpisodioNarrativo
    }

    class SequenciaNarrativa {
        -String idSituacao
        -List~EpisodioNarrativo~ episodios
        -ContextoNarrativo contexto
        +quantidadeDeEpisodios() int
        +episodiosDoTipo(TipoEpisodio tipo) List~EpisodioNarrativo~
        +preservaOrdemTemporal() boolean
    }

    class SituacaoProblema {
        -String id
        -String enunciado
        -CatalogoDaSituacao catalogo
        -EstadoDePosse estadoInicial
        -List~TransformacaoNarrativa~ transformacoes
        -EstadoDePosse estadoFinalCurado
        -IncognitaQuantitativa incognita
        -RelacaoEstrutural relacaoEstrutural
        -ContextoNarrativo contexto
        -StatusCuradoria status
        -EscopoDaSituacao escopo
        +validar() ResultadoValidacao
        +calcularEstadoFinal() EstadoDePosse
        +calcularPercursoDeEstados() List~EstadoDePosse~
        +projetarSequenciaNarrativa() SequenciaNarrativa
        +promoverParaValidada(String revisor) SituacaoProblema
        +recusar(String revisor, String motivo) SituacaoProblema
    }

    SituacaoProblema "1" *-- "*" TransformacaoNarrativa
    SituacaoProblema "1" *-- "1" CatalogoDaSituacao
    SituacaoProblema "1" *-- "1" EstadoDePosse : estadoInicial
    SituacaoProblema "1" *-- "0..1" EstadoDePosse : estadoFinalCurado
    SituacaoProblema "1" *-- "1" IncognitaQuantitativa
    SituacaoProblema "1" *-- "1" RelacaoEstrutural
    SituacaoProblema "1" *-- "1" ContextoNarrativo
    SituacaoProblema "1" *-- "*" AmbiguidadeDeclarada
    SituacaoProblema "1" o-- "1" StatusCuradoria
    SituacaoProblema "1" o-- "1" EscopoDaSituacao
    SituacaoProblema ..> SequenciaNarrativa : projeta

    SequenciaNarrativa "1" *-- "*" EpisodioNarrativo
    EpisodioNarrativo "1" o-- "1" TipoEpisodio
```

---

## 5. Camada de Representação em Quadrinhos (`representacao.quadrinhos`)

Transforma a sequência narrativa formalizada em roteiros de histórias em quadrinhos com cenas, quadros, balões e renderizadores em diferentes formatos de saída.

```mermaid
classDiagram
    direction TB

    class RenderizadorDeRoteiro {
        <<interface>>
        +renderizar(RoteiroQuadrinhos roteiro)* String
        +getExtensao()* String
    }

    class RenderizadorHtml {
        +renderizar(RoteiroQuadrinhos roteiro) String
        +getExtensao() String
    }

    class RenderizadorTextual {
        +renderizar(RoteiroQuadrinhos roteiro) String
        +getExtensao() String
    }

    class Balao {
        -Tipo tipo
        -String emissor
        -String texto
        +narracao(String texto)$ Balao
        +fala(String emissor, String texto)$ Balao
        +pergunta(String texto)$ Balao
        +possuiEmissor() boolean
    }

    class DescritorCena {
        -List~String~ personagensVisiveis
        -Map~String, Integer~ elementosVisuais
        -String ambientacao
        +totalDeElementos() int
        +possuiAmbientacao() boolean
    }

    class Quadro {
        -int numero
        -String titulo
        -String legenda
        -DescritorCena cena
        -List~Balao~ baloes
        +possuiLegenda() boolean
        +possuiPergunta() boolean
    }

    class RoteiroQuadrinhos {
        -String idSituacao
        -String titulo
        -List~Quadro~ quadros
        +quantidadeDeQuadros() int
        +respeitaFaixaDeQuadros() boolean
        +getQuadroComPergunta() Quadro
    }

    class GeradorRoteiroQuadrinhos {
        +gerar(SequenciaNarrativa sequencia, String titulo)$ RoteiroQuadrinhos
    }

    RenderizadorDeRoteiro <|.. RenderizadorTextual
    RenderizadorDeRoteiro <|.. RenderizadorHtml

    RoteiroQuadrinhos "1" *-- "*" Quadro
    Quadro "1" *-- "1" DescritorCena
    Quadro "1" *-- "*" Balao
    GeradorRoteiroQuadrinhos ..> RoteiroQuadrinhos : gera
```

---

## 6. Camada de Persistência (`persistencia`)

Implementa a persistência das situações-problema em arquivos JSON através do repositório, desacoplando o modelo de domínio dos formatos de serialização por meio de Data Transfer Objects (DTOs).

```mermaid
classDiagram
    direction TB

    class Repositorio~T, ID~ {
        <<interface>>
    }

    class RepositorioSituacaoJson {
        -Path diretorio
        -Gson gson
        -MapeadorSituacao mapeador
        +salvar(SituacaoProblema situacao) void
        +buscarPorId(String id) Optional~SituacaoProblema~
        +listarTodos() List~SituacaoProblema~
        +remover(String id) boolean
        +serializar(SituacaoProblema situacao) String
        +desserializar(String textoJson) SituacaoProblema
    }

    class MapeadorSituacao {
        +paraDto(SituacaoProblema situacao) SituacaoDto
        +paraDominio(SituacaoDto dto) SituacaoProblema
    }

    class ErroDeLeituraJson {
    }

    Repositorio <|.. RepositorioSituacaoJson
    RepositorioSituacaoJson "1" o-- "1" MapeadorSituacao
    RepositorioSituacaoJson ..> ErroDeLeituraJson : lanca

    note for RepositorioSituacaoJson "Pacote persistencia.dto agrupa os records imutáveis:
- SituacaoDto
- ParticipanteDto
- ObjetoContadoDto
- ContextoDto
- IncognitaDto
- EstadoDto
- TransformacaoDto
- AcaoDto
- QuantidadeDto
- RelacaoDto
- AmbiguidadeDto
- RealizacaoTextualDto"
```

---

## 7. Visão Geral das Dependências entre Pacotes

Apresenta o fluxo arquitetural unidirecional de dependências entre módulos, onde a camada de domínio permanece isolada e independente de infraestrutura ou interface gráfica.

```mermaid
graph LR
    app[app] --> interfacegrafica[interfacegrafica]
    app --> persistencia[persistencia]
    app --> representacao[representacao.quadrinhos]
    app --> dominio[dominio]

    interfacegrafica --> dominio
    interfacegrafica --> persistencia
    interfacegrafica --> representacao

    persistencia --> dominio
    persistencia -.->|implementa Repositorio| dominio

    representacao --> dominio
```
