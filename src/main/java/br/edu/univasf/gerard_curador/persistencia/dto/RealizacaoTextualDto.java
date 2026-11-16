package br.edu.univasf.gerard_curador.persistencia.dto;

/**
 * DTO anemico para transporte JSON das formas linguisticas de um objeto contado.
 *
 * <p>Diferente do dominio rico, apenas agrupa texto bruto sem regras de flexao;
 * o dominio nao importa Gson nem conhece esta representacao.
 */
public record RealizacaoTextualDto(
        String singular,
        String plural,
        String idioma) {
}
