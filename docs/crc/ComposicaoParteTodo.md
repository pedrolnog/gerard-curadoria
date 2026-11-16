### CRC-24: ComposicaoParteTodo

* **Tipo:** Entidade de Domínio / Especialização de Relação Estrutural
* **Responsabilidades:**
  - Modelar a relação estática entre um conjunto de partes complementares e um todo correspondente.
  - Armazenar a coleção de quantidades parciais (`partes`) e a quantidade global (`todo`).
  - Validar a compatibilidade de grandezas, unidades e famílias de objetos entre as partes e o todo.
  - Resolver o valor do todo pela soma das partes ou calcular uma parte ausente subtraindo do todo as partes conhecidas.
* **Colaboradores:**
  - `RelacaoEstrutural`
  - `Quantidade`
  - `PapelQuantitativo`
  - `Numero`
  - `ResultadoValidacao`
