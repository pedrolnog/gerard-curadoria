### CRC-28: ReferenciaVisualParticipante

* **Tipo:** Value Object (Imutável)
* **Responsabilidades:**
  - Encapsular a chave identificadora da representação visual ou avatar de um participante da história.
  - Prover métodos de criação segura para referências com chave ou instâncias especiais de referência não definida (`naoDefinida`).
  - Indicar se o participante possui uma referência gráfica efetivamente configurada (`estaDefinida`).
* **Colaboradores:**
  - `ParticipanteNarrativo`
