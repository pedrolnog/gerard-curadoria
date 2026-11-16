### CRC-45: Balao

* **Tipo:** Value Object de Representação Gráfica
* **Responsabilidades:**
  - Modelar o balão de quadrinhos com seu tipo específico (narração, fala de personagem ou pergunta/desafio), emissor opcional e texto.
  - Prover métodos de criação semânticos (`narracao`, `fala`, `pergunta`).
  - Indicar se o balão possui personagem emissor explícito (`possuiEmissor`).
  - Fornecer representação textual descritiva do balão.
* **Colaboradores:**
  - `Quadro`
