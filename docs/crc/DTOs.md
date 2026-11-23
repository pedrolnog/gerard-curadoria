### CRC-44: DTOs

* **Tipo:** Data Transfer Objects anêmicos com getters e setters (Pacote persistencia.dto)
* **Responsabilidades:**
  - Desacoplar o modelo de domínio da representação externa serializável em JSON.
  - Estruturar os dados transitados em classes anêmicas (construtor vazio para o Gson, getters e setters):
    - `SituacaoDto`: agrega dados gerais, enquadramento, listas e metadados de revisão da situação.
    - `ParticipanteDto`: transporta identificador, nome e chave visual de participantes narrativos.
    - `ObjetoContadoDto`: transporta dados do objeto, sua família, características e textos.
    - `ContextoDto`: registra local, ambientação e momento contextual da história.
    - `IncognitaDto`: transporta a pergunta formulada, papel almejado e estado de resolução.
    - `EstadoDto`: transporta possuidor, momento temporal e inventário de quantidades.
    - `TransformacaoDto`: transporta ator, destinatário, ação, quantidade e ordenação temporal.
    - `AcaoDto`: armazena verbos, particípios, preposições e sentido semântico da ação.
    - `QuantidadeDto`: transporta numeral, identificador de objeto, grandeza e unidade de medida.
    - `RelacaoDto`: transporta categoria de relação aditiva, mapa de papéis e parâmetros estruturais.
    - `AmbiguidadeDto`: armazena trecho e justificativa de ambiguidade do enunciado.
    - `RealizacaoTextualDto`: armazena singular, plural e idioma de um objeto.
* **Colaboradores:**
  - `MapeadorSituacao`
  - `RepositorioSituacaoJson`
