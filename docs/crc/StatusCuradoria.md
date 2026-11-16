### CRC-37: StatusCuradoria

* **Tipo:** Enumeração de Domínio
* **Responsabilidades:**
  - Controlar o estado do fluxo de revisão e ciclo de vida de uma situação-problema (como rascunho, aguardando revisão, validada ou recusada).
  - Informar se a situação está em fila de espera para verificação (`aguardaRevisao`).
  - Indicar se o problema já recebeu parecer formal de revisão (`foiRevisada`).
* **Colaboradores:**
  - `SituacaoProblema`
