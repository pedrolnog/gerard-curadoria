### CRC-43: RepositorioSituacaoJson

* **Tipo:** Adaptador de Infraestrutura (Implementação de Repositorio)
* **Responsabilidades:**
  - Implementar a porta `Repositorio<SituacaoProblema, String>` utilizando o sistema de arquivos e arquivos no formato JSON.
  - Gravar, atualizar, consultar e excluir situações-problema persistidas em disco.
  - Serializar e desserializar situações-problema via biblioteca Gson em conjunto com o `MapeadorSituacao`.
  - Capturar falhas de I/O e formato de dados, convertendo-as na exceção `ErroDeLeituraJson`.
* **Colaboradores:**
  - `Repositorio`
  - `SituacaoProblema`
  - `MapeadorSituacao`
  - `ErroDeLeituraJson`
