### CRC-47: Quadro

* **Tipo:** Entidade de Representação Gráfica
* **Responsabilidades:**
  - Representar um requadro individual dentro da tira ou página de história em quadrinhos.
  - Armazenar o número sequencial do quadro, título descritivo, legenda opcional, descrição da cena (`DescritorCena`) e lista de balões (`Balao`).
  - Indicar a presença de legenda explicativa ou de balão contendo a pergunta matemática da situação (`possuiPergunta`).
  - Fornecer descrição formatada de todos os elementos que compõem o quadro.
* **Colaboradores:**
  - `DescritorCena`
  - `Balao`
  - `RoteiroQuadrinhos`
