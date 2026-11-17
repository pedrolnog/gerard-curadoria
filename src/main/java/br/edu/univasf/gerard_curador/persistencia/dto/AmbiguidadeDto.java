package br.edu.univasf.gerard_curador.persistencia.dto;

/**
 * DTO anemico para transporte JSON de uma ambiguidade declarada pelo curador.
 *
 * <p>Diferente do dominio rico, apenas carrega trecho e motivo sem validacao;
 * o dominio nao importa Gson nem conhece esta representacao.
 */
public record AmbiguidadeDto(
        String trecho,
        String motivo) {
}
