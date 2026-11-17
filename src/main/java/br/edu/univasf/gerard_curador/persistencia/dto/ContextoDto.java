package br.edu.univasf.gerard_curador.persistencia.dto;

/**
 * DTO anemico para transporte JSON do contexto narrativo da situacao.
 *
 * <p>Diferente do dominio rico, apenas guarda campos opcionais em texto;
 * o dominio nao importa Gson nem conhece esta representacao.
 */
public record ContextoDto(
        String local,
        String situacao,
        String momento) {
}
