### CRC-46: DescritorCena

* **Tipo:** Value Object de Representação Gráfica
* **Responsabilidades:**
  - Descrever os elementos visuais e espaciais que compõem o cenário de um requadro de quadrinhos.
  - Armazenar a lista de personagens visíveis, a contagem de elementos gráficos e a ambientação da cena.
  - Informar se há ambientação/cenário especificado (`possuiAmbientacao`).
  - Calcular e informar o total de elementos visuais presentes na cena (`totalDeElementos`).
* **Colaboradores:**
  - `Quadro`
