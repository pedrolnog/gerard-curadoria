### CRC-35: EpisodioNarrativo

* **Tipo:** Entidade de Domínio / Unidade Temporal da Narrativa
* **Responsabilidades:**
  - Representar uma unidade temporal delimitada dentro da sequência narrativa (um marco de estado, uma ação/evento ou uma pergunta quantitativa).
  - Associar o episódio ao seu tipo (`TipoEpisodio`) e ao seu respectivo marcador de tempo (`MarcadorTemporal`).
  - Prover fábricas semânticas especializadas para episódios de estado (`deEstado`), eventos de transformação (`deEvento`) e indagações (`dePergunta`).
  - Identificar seu papel estrutural na história e gerar descrição textual acompanhada de rótulo.
* **Colaboradores:**
  - `TipoEpisodio`
  - `MarcadorTemporal`
  - `EstadoDePosse`
  - `TransformacaoNarrativa`
  - `IncognitaQuantitativa`
