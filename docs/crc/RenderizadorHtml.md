### CRC-49: RenderizadorHtml

* **Tipo:** Adaptador de Saída (Implementação de RenderizadorDeRoteiro)
* **Responsabilidades:**
  - Implementar o contrato `RenderizadorDeRoteiro` convertendo o roteiro em marcação HTML estilizada.
  - Formatar e dispor visualmente os requadros, cenas, ambientações e balões como uma história em quadrinhos legível no navegador.
  - Informar a extensão de arquivo correspondente (`html`).
* **Colaboradores:**
  - `RenderizadorDeRoteiro`
  - `RoteiroQuadrinhos`
  - `Quadro`
  - `DescritorCena`
  - `Balao`
