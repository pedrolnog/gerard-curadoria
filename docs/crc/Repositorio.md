### CRC-32: Repositorio

* **Tipo:** Interface de Domínio (Porta de Repositório Genérico)
* **Responsabilidades:**
  - Definir o contrato genérico (`<T, ID>`) para operações de persistência, consulta e ciclo de vida de agregados.
  - Abstrair o mecanismo de armazenamento de dados (disco, formato JSON, banco de dados ou memória) das regras de negócio.
  - Estabelecer os métodos padronizados para salvar, buscar por identificador, listar todas as entidades e remover.
* **Colaboradores:**
  - Nenhum
