### CRC-39: Diagnostico

* **Tipo:** Value Object (Imutável)
* **Responsabilidades:**
  - Registrar formalmente uma ocorrência semântica ou estrutural detectada durante a validação da situação-problema.
  - Conectar o tipo de diagnóstico (`TipoDiagnostico`) ao elemento do domínio afetado e ao texto explicativo de detalhe.
  - Discriminar se a ocorrência consiste em um erro impeditivo de consistência ou em um aviso informativo.
* **Colaboradores:**
  - `TipoDiagnostico`
  - `ResultadoValidacao`
