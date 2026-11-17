package br.edu.univasf.gerard_curador.persistencia.dto;

/**
 * DTO anemico para transporte JSON de uma quantidade referenciando um objeto por id.
 *
 * <p>Diferente do dominio rico, nao valida compatibilidade de grandeza, unidade ou sinal;
 * o dominio nao importa Gson nem conhece esta representacao.
 */
public record QuantidadeDto(
        String objeto,
        Integer numero,
        String grandeza,
        String unidade) {
}
