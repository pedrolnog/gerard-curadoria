### CRC-30: RealizacaoTextualObjeto

* **Tipo:** Value Object (Imutável)
* **Responsabilidades:**
  - Armazenar as flexões no singular e no plural associadas ao nome de um objeto contado, bem como seu idioma.
  - Flexionar a forma gramatical adequada com base na concordância numérica de um `Numero`.
  - Gerar descrições textuais combinando o numeral com o substantivo devidamente flexionado.
* **Colaboradores:**
  - `Numero`
  - `ObjetoContado`
