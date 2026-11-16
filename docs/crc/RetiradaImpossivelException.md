### CRC-27: RetiradaImpossivelException

* **Tipo:** Exceção de Domínio (Específica)
* **Responsabilidades:**
  - Sinalizar tentativas de subtração de quantidades em que o valor a subtrair é maior do que o saldo disponível (impedindo resultado negativo).
  - Sinalizar tentativas de retirada de itens inexistentes ou insuficientes em um `Inventario` ou `EstadoDePosse`.
* **Colaboradores:**
  - `ViolacaoDeDominioException`
  - `Quantidade`
  - `Inventario`
