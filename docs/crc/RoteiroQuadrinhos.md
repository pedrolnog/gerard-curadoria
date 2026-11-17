### CRC-51: RoteiroQuadrinhos

* **Tipo:** Agregado de Representação Gráfica
* **Responsabilidades:**
  - Encapsular a história em quadrinhos gerada a partir da projeção da sequência narrativa de uma situação-problema.
  - Armazenar o identificador da situação de origem, o título da história e a lista ordenada de quadros (`Quadro`).
  - Validar se a quantidade de quadros respeita os limites estipulados para a narrativa visual (`respeitaFaixaDeQuadros`).
  - Recuperar quadros de interesse, como o primeiro quadro, o último ou o requadro que expressa a pergunta matemática.
* **Colaboradores:**
  - `Quadro`
  - `DescritorCena`
  - `Balao`
