### CRC-41: ErroDeLeituraJson

* **Tipo:** Exceção de Infraestrutura / Persistência
* **Responsabilidades:**
  - Sinalizar problemas de leitura, conteúdo corrompido, formato incompatível ou falhas de I/O ocorridas durante o carregamento de arquivos JSON.
  - Encapsular detalhes e causas originais de falhas de desserialização ocorridas no repositório.
* **Colaboradores:**
  - `RuntimeException`
