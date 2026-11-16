### CRC-48: RenderizadorDeRoteiro

* **Tipo:** Interface de Representação (Porta de Saída de Roteiros)
* **Responsabilidades:**
  - Definir o contrato para transformação e renderização de instâncias de `RoteiroQuadrinhos` em diferentes mídias e formatos de saída.
  - Declarar o método de renderização textual ou estruturada (`renderizar`).
  - Informar a extensão de arquivo associada ao formato suportado (`getExtensao`).
* **Colaboradores:**
  - `RoteiroQuadrinhos`
