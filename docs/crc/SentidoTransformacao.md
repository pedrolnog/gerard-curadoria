### CRC-29: SentidoTransformacao

* **Tipo:** Enumeração de Domínio
* **Responsabilidades:**
  - Classificar a orientação matemática da transformação narrativa em acréscimo (fator positivo +1) ou retirada (fator negativo -1).
  - Aplicar a transformação sobre um `EstadoDePosse`, gerando um novo estado de posse decorrente da modificação temporal.
  - Avaliar previamente se a transformação pode ser aplicada sobre o estado sem violar invariantes (`podeSerAplicado`).
  - Prover operação de inversão do sentido aritmético (`inverter`).
* **Colaboradores:**
  - `EstadoDePosse`
  - `Quantidade`
  - `MarcadorTemporal`
