### CRC-26: QuantidadeIncompativelException

* **Tipo:** Exceção de Domínio (Específica)
* **Responsabilidades:**
  - Sinalizar operações aritméticas ou de comparação envolvendo quantidades que possuam grandezas, unidades ou objetos contados incompatíveis.
  - Interromper a execução quando da tentativa de combinação inválida entre grandezas distintas ou unidades incomensuráveis.
* **Colaboradores:**
  - `ViolacaoDeDominioException`
  - `Quantidade`
