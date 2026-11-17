### CRC-34: CatalogoDaSituacao

* **Tipo:** Entidade de Domínio / Agregador de Recursos
* **Responsabilidades:**
  - Gerenciar e centralizar os participantes narrativos e objetos contados que integram a situação-problema.
  - Realizar buscas de `ParticipanteNarrativo` e `ObjetoContado` através de seus respectivos identificadores.
  - Verificar a presença e pertinência de elementos no catálogo da situação (`contem`).
  - Filtrar e agrupar objetos contados com base em sua `FamiliaObjeto`.
* **Colaboradores:**
  - `ParticipanteNarrativo`
  - `ObjetoContado`
  - `FamiliaObjeto`
