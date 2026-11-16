### CRC-23: ComposicaoDeTransformacoes

* **Tipo:** Entidade de Domínio / Especialização de Relação Estrutural
* **Responsabilidades:**
  - Modelar a composição temporal de transformações sucessivas aplicadas sobre o mesmo estado ou participante.
  - Registrar os sentidos (acréscimo ou retirada) da primeira transformação, da segunda e da transformação resultante.
  - Validar a compatibilidade entre os sentidos e grandezas de todas as transformações combinadas.
  - Resolver o valor ou papel ausente na composição de transformações através do cálculo aditivo correspondente.
* **Colaboradores:**
  - `RelacaoEstrutural`
  - `SentidoTransformacao`
  - `PapelQuantitativo`
  - `Quantidade`
  - `Numero`
  - `ResultadoValidacao`
