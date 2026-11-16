### CRC-42: MapeadorSituacao

* **Tipo:** Serviço de Infraestrutura / Tradutor de Persistência
* **Responsabilidades:**
  - Converter uma instância rica do agregado `SituacaoProblema` para o modelo anêmico e serializável `SituacaoDto`.
  - Reconstruir e instanciar um `SituacaoProblema` consistente a partir dos dados lidos em um `SituacaoDto`.
  - Mapear bidirecionalmente todos os componentes aninhados (participantes, objetos, inventários, ações, transformações, relações estruturais e incógnitas).
* **Colaboradores:**
  - `SituacaoProblema`
  - `SituacaoDto`
  - DTOs do pacote `persistencia.dto`
